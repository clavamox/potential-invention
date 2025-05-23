package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.extractor.DummyTrackOutput;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.metadata.emsg.EventMessageDecoder;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.hls.HlsChunkSource;
import com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

/* loaded from: classes.dex */
final class HlsSampleStreamWrapper implements Loader.Callback<Chunk>, Loader.ReleaseCallback, SequenceableLoader, ExtractorOutput, SampleQueue.UpstreamFormatChangedListener {
    private static final Set<Integer> MAPPABLE_TYPES = Collections.unmodifiableSet(new HashSet(Arrays.asList(1, 2, 4)));
    public static final int SAMPLE_QUEUE_INDEX_NO_MAPPING_FATAL = -2;
    public static final int SAMPLE_QUEUE_INDEX_NO_MAPPING_NON_FATAL = -3;
    public static final int SAMPLE_QUEUE_INDEX_PENDING = -1;
    private static final String TAG = "HlsSampleStreamWrapper";
    private final Allocator allocator;
    private final Callback callback;
    private final HlsChunkSource chunkSource;
    private int chunkUid;
    private Format downstreamTrackFormat;
    private DrmInitData drmInitData;
    private final DrmSessionManager<?> drmSessionManager;
    private TrackOutput emsgUnwrappingTrackOutput;
    private int enabledTrackGroupCount;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private final Handler handler;
    private boolean haveAudioVideoSampleQueues;
    private final ArrayList<HlsSampleStream> hlsSampleStreams;
    private long lastSeekPositionUs;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private boolean loadingFinished;
    private final Runnable maybeFinishPrepareRunnable;
    private final ArrayList<HlsMediaChunk> mediaChunks;
    private final int metadataType;
    private final Format muxedAudioFormat;
    private final Runnable onTracksEndedRunnable;
    private Set<TrackGroup> optionalTrackGroups;
    private final Map<String, DrmInitData> overridingDrmInitData;
    private long pendingResetPositionUs;
    private boolean pendingResetUpstreamFormats;
    private boolean prepared;
    private int primarySampleQueueIndex;
    private int primarySampleQueueType;
    private int primaryTrackGroupIndex;
    private final List<HlsMediaChunk> readOnlyMediaChunks;
    private boolean released;
    private long sampleOffsetUs;
    private SparseIntArray sampleQueueIndicesByType;
    private boolean[] sampleQueueIsAudioVideoFlags;
    private Set<Integer> sampleQueueMappingDoneByType;
    private FormatAdjustingSampleQueue[] sampleQueues;
    private boolean sampleQueuesBuilt;
    private boolean[] sampleQueuesEnabledStates;
    private boolean seenFirstTrackSelection;
    private int[] trackGroupToSampleQueueIndex;
    private TrackGroupArray trackGroups;
    private final int trackType;
    private boolean tracksEnded;
    private Format upstreamTrackFormat;
    private final Loader loader = new Loader("Loader:HlsSampleStreamWrapper");
    private final HlsChunkSource.HlsChunkHolder nextChunkHolder = new HlsChunkSource.HlsChunkHolder();
    private int[] sampleQueueTrackIds = new int[0];

    public interface Callback extends SequenceableLoader.Callback<HlsSampleStreamWrapper> {
        void onPlaylistRefreshRequired(Uri uri);

        void onPrepared();
    }

    private static int getTrackTypeScore(int i) {
        if (i == 1) {
            return 2;
        }
        if (i != 2) {
            return i != 3 ? 0 : 1;
        }
        return 3;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public void reevaluateBuffer(long j) {
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public void seekMap(SeekMap seekMap) {
    }

    public HlsSampleStreamWrapper(int i, Callback callback, HlsChunkSource hlsChunkSource, Map<String, DrmInitData> map, Allocator allocator, long j, Format format, DrmSessionManager<?> drmSessionManager, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher eventDispatcher, int i2) {
        this.trackType = i;
        this.callback = callback;
        this.chunkSource = hlsChunkSource;
        this.overridingDrmInitData = map;
        this.allocator = allocator;
        this.muxedAudioFormat = format;
        this.drmSessionManager = drmSessionManager;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.metadataType = i2;
        Set<Integer> set = MAPPABLE_TYPES;
        this.sampleQueueMappingDoneByType = new HashSet(set.size());
        this.sampleQueueIndicesByType = new SparseIntArray(set.size());
        this.sampleQueues = new FormatAdjustingSampleQueue[0];
        this.sampleQueueIsAudioVideoFlags = new boolean[0];
        this.sampleQueuesEnabledStates = new boolean[0];
        ArrayList<HlsMediaChunk> arrayList = new ArrayList<>();
        this.mediaChunks = arrayList;
        this.readOnlyMediaChunks = Collections.unmodifiableList(arrayList);
        this.hlsSampleStreams = new ArrayList<>();
        this.maybeFinishPrepareRunnable = new Runnable() { // from class: com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                HlsSampleStreamWrapper.this.maybeFinishPrepare();
            }
        };
        this.onTracksEndedRunnable = new Runnable() { // from class: com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                HlsSampleStreamWrapper.this.onTracksEnded();
            }
        };
        this.handler = new Handler();
        this.lastSeekPositionUs = j;
        this.pendingResetPositionUs = j;
    }

    public void continuePreparing() {
        if (this.prepared) {
            return;
        }
        continueLoading(this.lastSeekPositionUs);
    }

    public void prepareWithMasterPlaylistInfo(TrackGroup[] trackGroupArr, int i, int... iArr) {
        this.trackGroups = createTrackGroupArrayWithDrmInfo(trackGroupArr);
        this.optionalTrackGroups = new HashSet();
        for (int i2 : iArr) {
            this.optionalTrackGroups.add(this.trackGroups.get(i2));
        }
        this.primaryTrackGroupIndex = i;
        Handler handler = this.handler;
        final Callback callback = this.callback;
        callback.getClass();
        handler.post(new Runnable() { // from class: com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                HlsSampleStreamWrapper.Callback.this.onPrepared();
            }
        });
        setIsPrepared();
    }

    public void maybeThrowPrepareError() throws IOException {
        maybeThrowError();
        if (this.loadingFinished && !this.prepared) {
            throw new ParserException("Loading finished before preparation is complete.");
        }
    }

    public TrackGroupArray getTrackGroups() {
        assertIsPrepared();
        return this.trackGroups;
    }

    public int getPrimaryTrackGroupIndex() {
        return this.primaryTrackGroupIndex;
    }

    public int bindSampleQueueToSampleStream(int i) {
        assertIsPrepared();
        Assertions.checkNotNull(this.trackGroupToSampleQueueIndex);
        int i2 = this.trackGroupToSampleQueueIndex[i];
        if (i2 == -1) {
            return this.optionalTrackGroups.contains(this.trackGroups.get(i)) ? -3 : -2;
        }
        boolean[] zArr = this.sampleQueuesEnabledStates;
        if (zArr[i2]) {
            return -2;
        }
        zArr[i2] = true;
        return i2;
    }

    public void unbindSampleQueue(int i) {
        assertIsPrepared();
        Assertions.checkNotNull(this.trackGroupToSampleQueueIndex);
        int i2 = this.trackGroupToSampleQueueIndex[i];
        Assertions.checkState(this.sampleQueuesEnabledStates[i2]);
        this.sampleQueuesEnabledStates[i2] = false;
    }

    /* JADX WARN: Removed duplicated region for block: B:77:0x0126  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0130  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[] r20, boolean[] r21, com.google.android.exoplayer2.source.SampleStream[] r22, boolean[] r23, long r24, boolean r26) {
        /*
            Method dump skipped, instructions count: 325
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper.selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[], boolean[], com.google.android.exoplayer2.source.SampleStream[], boolean[], long, boolean):boolean");
    }

    public void discardBuffer(long j, boolean z) {
        if (!this.sampleQueuesBuilt || isPendingReset()) {
            return;
        }
        int length = this.sampleQueues.length;
        for (int i = 0; i < length; i++) {
            this.sampleQueues[i].discardTo(j, z, this.sampleQueuesEnabledStates[i]);
        }
    }

    public boolean seekToUs(long j, boolean z) {
        this.lastSeekPositionUs = j;
        if (isPendingReset()) {
            this.pendingResetPositionUs = j;
            return true;
        }
        if (this.sampleQueuesBuilt && !z && seekInsideBufferUs(j)) {
            return false;
        }
        this.pendingResetPositionUs = j;
        this.loadingFinished = false;
        this.mediaChunks.clear();
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
        } else {
            this.loader.clearFatalError();
            resetSampleQueues();
        }
        return true;
    }

    public void release() {
        if (this.prepared) {
            for (FormatAdjustingSampleQueue formatAdjustingSampleQueue : this.sampleQueues) {
                formatAdjustingSampleQueue.preRelease();
            }
        }
        this.loader.release(this);
        this.handler.removeCallbacksAndMessages(null);
        this.released = true;
        this.hlsSampleStreams.clear();
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.ReleaseCallback
    public void onLoaderReleased() {
        for (FormatAdjustingSampleQueue formatAdjustingSampleQueue : this.sampleQueues) {
            formatAdjustingSampleQueue.release();
        }
    }

    public void setIsTimestampMaster(boolean z) {
        this.chunkSource.setIsTimestampMaster(z);
    }

    public boolean onPlaylistError(Uri uri, long j) {
        return this.chunkSource.onPlaylistError(uri, j);
    }

    public boolean isReady(int i) {
        return !isPendingReset() && this.sampleQueues[i].isReady(this.loadingFinished);
    }

    public void maybeThrowError(int i) throws IOException {
        maybeThrowError();
        this.sampleQueues[i].maybeThrowError();
    }

    public void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
        this.chunkSource.maybeThrowError();
    }

    public int readData(int i, FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
        Format format;
        if (isPendingReset()) {
            return -3;
        }
        int i2 = 0;
        if (!this.mediaChunks.isEmpty()) {
            int i3 = 0;
            while (i3 < this.mediaChunks.size() - 1 && finishedReadingChunk(this.mediaChunks.get(i3))) {
                i3++;
            }
            Util.removeRange(this.mediaChunks, 0, i3);
            HlsMediaChunk hlsMediaChunk = this.mediaChunks.get(0);
            Format format2 = hlsMediaChunk.trackFormat;
            if (!format2.equals(this.downstreamTrackFormat)) {
                this.eventDispatcher.downstreamFormatChanged(this.trackType, format2, hlsMediaChunk.trackSelectionReason, hlsMediaChunk.trackSelectionData, hlsMediaChunk.startTimeUs);
            }
            this.downstreamTrackFormat = format2;
        }
        int read = this.sampleQueues[i].read(formatHolder, decoderInputBuffer, z, this.loadingFinished, this.lastSeekPositionUs);
        if (read == -5) {
            Format format3 = (Format) Assertions.checkNotNull(formatHolder.format);
            if (i == this.primarySampleQueueIndex) {
                int peekSourceId = this.sampleQueues[i].peekSourceId();
                while (i2 < this.mediaChunks.size() && this.mediaChunks.get(i2).uid != peekSourceId) {
                    i2++;
                }
                if (i2 < this.mediaChunks.size()) {
                    format = this.mediaChunks.get(i2).trackFormat;
                } else {
                    format = (Format) Assertions.checkNotNull(this.upstreamTrackFormat);
                }
                format3 = format3.copyWithManifestFormatInfo(format);
            }
            formatHolder.format = format3;
        }
        return read;
    }

    public int skipData(int i, long j) {
        if (isPendingReset()) {
            return 0;
        }
        FormatAdjustingSampleQueue formatAdjustingSampleQueue = this.sampleQueues[i];
        if (this.loadingFinished && j > formatAdjustingSampleQueue.getLargestQueuedTimestampUs()) {
            return formatAdjustingSampleQueue.advanceToEnd();
        }
        return formatAdjustingSampleQueue.advanceTo(j);
    }

    /*  JADX ERROR: NullPointerException in pass: LoopRegionVisitor
        java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.SSAVar.use(jadx.core.dex.instructions.args.RegisterArg)" because "ssaVar" is null
        	at jadx.core.dex.nodes.InsnNode.rebindArgs(InsnNode.java:493)
        	at jadx.core.dex.nodes.InsnNode.rebindArgs(InsnNode.java:496)
        */
    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public long getBufferedPositionUs() {
        /*
            r7 = this;
            boolean r0 = r7.loadingFinished
            if (r0 == 0) goto L7
            r0 = -9223372036854775808
            return r0
        L7:
            boolean r0 = r7.isPendingReset()
            if (r0 == 0) goto L10
            long r0 = r7.pendingResetPositionUs
            return r0
        L10:
            long r0 = r7.lastSeekPositionUs
            com.google.android.exoplayer2.source.hls.HlsMediaChunk r2 = r7.getLastMediaChunk()
            boolean r3 = r2.isLoadCompleted()
            if (r3 == 0) goto L1d
            goto L36
        L1d:
            java.util.ArrayList<com.google.android.exoplayer2.source.hls.HlsMediaChunk> r2 = r7.mediaChunks
            int r2 = r2.size()
            r3 = 1
            if (r2 <= r3) goto L35
            java.util.ArrayList<com.google.android.exoplayer2.source.hls.HlsMediaChunk> r2 = r7.mediaChunks
            int r3 = r2.size()
            int r3 = r3 + (-2)
            java.lang.Object r2 = r2.get(r3)
            com.google.android.exoplayer2.source.hls.HlsMediaChunk r2 = (com.google.android.exoplayer2.source.hls.HlsMediaChunk) r2
            goto L36
        L35:
            r2 = 0
        L36:
            if (r2 == 0) goto L3e
            long r2 = r2.endTimeUs
            long r0 = java.lang.Math.max(r0, r2)
        L3e:
            boolean r2 = r7.sampleQueuesBuilt
            if (r2 == 0) goto L55
            com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper$FormatAdjustingSampleQueue[] r2 = r7.sampleQueues
            int r3 = r2.length
            r4 = 0
        L46:
            if (r4 >= r3) goto L55
            r5 = r2[r4]
            long r5 = r5.getLargestQueuedTimestampUs()
            long r0 = java.lang.Math.max(r0, r5)
            int r4 = r4 + 1
            goto L46
        L55:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper.getBufferedPositionUs():long");
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public long getNextLoadPositionUs() {
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        return getLastMediaChunk().endTimeUs;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long j) {
        List<HlsMediaChunk> list;
        long max;
        if (this.loadingFinished || this.loader.isLoading() || this.loader.hasFatalError()) {
            return false;
        }
        if (isPendingReset()) {
            list = Collections.emptyList();
            max = this.pendingResetPositionUs;
        } else {
            list = this.readOnlyMediaChunks;
            HlsMediaChunk lastMediaChunk = getLastMediaChunk();
            max = lastMediaChunk.isLoadCompleted() ? lastMediaChunk.endTimeUs : Math.max(this.lastSeekPositionUs, lastMediaChunk.startTimeUs);
        }
        List<HlsMediaChunk> list2 = list;
        this.chunkSource.getNextChunk(j, max, list2, this.prepared || !list2.isEmpty(), this.nextChunkHolder);
        boolean z = this.nextChunkHolder.endOfStream;
        Chunk chunk = this.nextChunkHolder.chunk;
        Uri uri = this.nextChunkHolder.playlistUrl;
        this.nextChunkHolder.clear();
        if (z) {
            this.pendingResetPositionUs = C.TIME_UNSET;
            this.loadingFinished = true;
            return true;
        }
        if (chunk == null) {
            if (uri != null) {
                this.callback.onPlaylistRefreshRequired(uri);
            }
            return false;
        }
        if (isMediaChunk(chunk)) {
            this.pendingResetPositionUs = C.TIME_UNSET;
            HlsMediaChunk hlsMediaChunk = (HlsMediaChunk) chunk;
            hlsMediaChunk.init(this);
            this.mediaChunks.add(hlsMediaChunk);
            this.upstreamTrackFormat = hlsMediaChunk.trackFormat;
        }
        this.eventDispatcher.loadStarted(chunk.dataSpec, chunk.type, this.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, this.loader.startLoading(chunk, this, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(chunk.type)));
        return true;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public boolean isLoading() {
        return this.loader.isLoading();
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Callback
    public void onLoadCompleted(Chunk chunk, long j, long j2) {
        this.chunkSource.onChunkLoadCompleted(chunk);
        this.eventDispatcher.loadCompleted(chunk.dataSpec, chunk.getUri(), chunk.getResponseHeaders(), chunk.type, this.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, j, j2, chunk.bytesLoaded());
        if (!this.prepared) {
            continueLoading(this.lastSeekPositionUs);
        } else {
            this.callback.onContinueLoadingRequested(this);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Callback
    public void onLoadCanceled(Chunk chunk, long j, long j2, boolean z) {
        this.eventDispatcher.loadCanceled(chunk.dataSpec, chunk.getUri(), chunk.getResponseHeaders(), chunk.type, this.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, j, j2, chunk.bytesLoaded());
        if (z) {
            return;
        }
        resetSampleQueues();
        if (this.enabledTrackGroupCount > 0) {
            this.callback.onContinueLoadingRequested(this);
        }
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Callback
    public Loader.LoadErrorAction onLoadError(Chunk chunk, long j, long j2, IOException iOException, int i) {
        Loader.LoadErrorAction createRetryAction;
        long bytesLoaded = chunk.bytesLoaded();
        boolean isMediaChunk = isMediaChunk(chunk);
        long blacklistDurationMsFor = this.loadErrorHandlingPolicy.getBlacklistDurationMsFor(chunk.type, j2, iOException, i);
        boolean maybeBlacklistTrack = blacklistDurationMsFor != C.TIME_UNSET ? this.chunkSource.maybeBlacklistTrack(chunk, blacklistDurationMsFor) : false;
        if (maybeBlacklistTrack) {
            if (isMediaChunk && bytesLoaded == 0) {
                ArrayList<HlsMediaChunk> arrayList = this.mediaChunks;
                Assertions.checkState(arrayList.remove(arrayList.size() - 1) == chunk);
                if (this.mediaChunks.isEmpty()) {
                    this.pendingResetPositionUs = this.lastSeekPositionUs;
                }
            }
            createRetryAction = Loader.DONT_RETRY;
        } else {
            long retryDelayMsFor = this.loadErrorHandlingPolicy.getRetryDelayMsFor(chunk.type, j2, iOException, i);
            createRetryAction = retryDelayMsFor != C.TIME_UNSET ? Loader.createRetryAction(false, retryDelayMsFor) : Loader.DONT_RETRY_FATAL;
        }
        Loader.LoadErrorAction loadErrorAction = createRetryAction;
        this.eventDispatcher.loadError(chunk.dataSpec, chunk.getUri(), chunk.getResponseHeaders(), chunk.type, this.trackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, j, j2, bytesLoaded, iOException, !loadErrorAction.isRetry());
        if (maybeBlacklistTrack) {
            if (!this.prepared) {
                continueLoading(this.lastSeekPositionUs);
            } else {
                this.callback.onContinueLoadingRequested(this);
            }
        }
        return loadErrorAction;
    }

    public void init(int i, boolean z) {
        this.chunkUid = i;
        for (FormatAdjustingSampleQueue formatAdjustingSampleQueue : this.sampleQueues) {
            formatAdjustingSampleQueue.sourceId(i);
        }
        if (z) {
            for (FormatAdjustingSampleQueue formatAdjustingSampleQueue2 : this.sampleQueues) {
                formatAdjustingSampleQueue2.splice();
            }
        }
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public TrackOutput track(int i, int i2) {
        TrackOutput trackOutput;
        if (!MAPPABLE_TYPES.contains(Integer.valueOf(i2))) {
            int i3 = 0;
            while (true) {
                TrackOutput[] trackOutputArr = this.sampleQueues;
                if (i3 >= trackOutputArr.length) {
                    trackOutput = null;
                    break;
                }
                if (this.sampleQueueTrackIds[i3] == i) {
                    trackOutput = trackOutputArr[i3];
                    break;
                }
                i3++;
            }
        } else {
            trackOutput = getMappedTrackOutput(i, i2);
        }
        if (trackOutput == null) {
            if (this.tracksEnded) {
                return createDummyTrackOutput(i, i2);
            }
            trackOutput = createSampleQueue(i, i2);
        }
        if (i2 != 4) {
            return trackOutput;
        }
        if (this.emsgUnwrappingTrackOutput == null) {
            this.emsgUnwrappingTrackOutput = new EmsgUnwrappingTrackOutput(trackOutput, this.metadataType);
        }
        return this.emsgUnwrappingTrackOutput;
    }

    private TrackOutput getMappedTrackOutput(int i, int i2) {
        Assertions.checkArgument(MAPPABLE_TYPES.contains(Integer.valueOf(i2)));
        int i3 = this.sampleQueueIndicesByType.get(i2, -1);
        if (i3 == -1) {
            return null;
        }
        if (this.sampleQueueMappingDoneByType.add(Integer.valueOf(i2))) {
            this.sampleQueueTrackIds[i3] = i;
        }
        return this.sampleQueueTrackIds[i3] == i ? this.sampleQueues[i3] : createDummyTrackOutput(i, i2);
    }

    private SampleQueue createSampleQueue(int i, int i2) {
        int length = this.sampleQueues.length;
        boolean z = true;
        if (i2 != 1 && i2 != 2) {
            z = false;
        }
        FormatAdjustingSampleQueue formatAdjustingSampleQueue = new FormatAdjustingSampleQueue(this.allocator, this.drmSessionManager, this.overridingDrmInitData);
        if (z) {
            formatAdjustingSampleQueue.setDrmInitData(this.drmInitData);
        }
        formatAdjustingSampleQueue.setSampleOffsetUs(this.sampleOffsetUs);
        formatAdjustingSampleQueue.sourceId(this.chunkUid);
        formatAdjustingSampleQueue.setUpstreamFormatChangeListener(this);
        int i3 = length + 1;
        int[] copyOf = Arrays.copyOf(this.sampleQueueTrackIds, i3);
        this.sampleQueueTrackIds = copyOf;
        copyOf[length] = i;
        this.sampleQueues = (FormatAdjustingSampleQueue[]) Util.nullSafeArrayAppend(this.sampleQueues, formatAdjustingSampleQueue);
        boolean[] copyOf2 = Arrays.copyOf(this.sampleQueueIsAudioVideoFlags, i3);
        this.sampleQueueIsAudioVideoFlags = copyOf2;
        copyOf2[length] = z;
        this.haveAudioVideoSampleQueues |= z;
        this.sampleQueueMappingDoneByType.add(Integer.valueOf(i2));
        this.sampleQueueIndicesByType.append(i2, length);
        if (getTrackTypeScore(i2) > getTrackTypeScore(this.primarySampleQueueType)) {
            this.primarySampleQueueIndex = length;
            this.primarySampleQueueType = i2;
        }
        this.sampleQueuesEnabledStates = Arrays.copyOf(this.sampleQueuesEnabledStates, i3);
        return formatAdjustingSampleQueue;
    }

    @Override // com.google.android.exoplayer2.extractor.ExtractorOutput
    public void endTracks() {
        this.tracksEnded = true;
        this.handler.post(this.onTracksEndedRunnable);
    }

    @Override // com.google.android.exoplayer2.source.SampleQueue.UpstreamFormatChangedListener
    public void onUpstreamFormatChanged(Format format) {
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void onNewExtractor() {
        this.sampleQueueMappingDoneByType.clear();
    }

    public void setSampleOffsetUs(long j) {
        if (this.sampleOffsetUs != j) {
            this.sampleOffsetUs = j;
            for (FormatAdjustingSampleQueue formatAdjustingSampleQueue : this.sampleQueues) {
                formatAdjustingSampleQueue.setSampleOffsetUs(j);
            }
        }
    }

    public void setDrmInitData(DrmInitData drmInitData) {
        if (Util.areEqual(this.drmInitData, drmInitData)) {
            return;
        }
        this.drmInitData = drmInitData;
        int i = 0;
        while (true) {
            FormatAdjustingSampleQueue[] formatAdjustingSampleQueueArr = this.sampleQueues;
            if (i >= formatAdjustingSampleQueueArr.length) {
                return;
            }
            if (this.sampleQueueIsAudioVideoFlags[i]) {
                formatAdjustingSampleQueueArr[i].setDrmInitData(drmInitData);
            }
            i++;
        }
    }

    private void updateSampleStreams(SampleStream[] sampleStreamArr) {
        this.hlsSampleStreams.clear();
        for (SampleStream sampleStream : sampleStreamArr) {
            if (sampleStream != null) {
                this.hlsSampleStreams.add((HlsSampleStream) sampleStream);
            }
        }
    }

    private boolean finishedReadingChunk(HlsMediaChunk hlsMediaChunk) {
        int i = hlsMediaChunk.uid;
        int length = this.sampleQueues.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (this.sampleQueuesEnabledStates[i2] && this.sampleQueues[i2].peekSourceId() == i) {
                return false;
            }
        }
        return true;
    }

    private void resetSampleQueues() {
        for (FormatAdjustingSampleQueue formatAdjustingSampleQueue : this.sampleQueues) {
            formatAdjustingSampleQueue.reset(this.pendingResetUpstreamFormats);
        }
        this.pendingResetUpstreamFormats = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTracksEnded() {
        this.sampleQueuesBuilt = true;
        maybeFinishPrepare();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeFinishPrepare() {
        if (!this.released && this.trackGroupToSampleQueueIndex == null && this.sampleQueuesBuilt) {
            for (FormatAdjustingSampleQueue formatAdjustingSampleQueue : this.sampleQueues) {
                if (formatAdjustingSampleQueue.getUpstreamFormat() == null) {
                    return;
                }
            }
            if (this.trackGroups != null) {
                mapSampleQueuesToMatchTrackGroups();
                return;
            }
            buildTracksFromSampleStreams();
            setIsPrepared();
            this.callback.onPrepared();
        }
    }

    @EnsuresNonNull({"trackGroupToSampleQueueIndex"})
    @RequiresNonNull({"trackGroups"})
    private void mapSampleQueuesToMatchTrackGroups() {
        int i = this.trackGroups.length;
        int[] iArr = new int[i];
        this.trackGroupToSampleQueueIndex = iArr;
        Arrays.fill(iArr, -1);
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = 0;
            while (true) {
                FormatAdjustingSampleQueue[] formatAdjustingSampleQueueArr = this.sampleQueues;
                if (i3 >= formatAdjustingSampleQueueArr.length) {
                    break;
                }
                if (formatsMatch(formatAdjustingSampleQueueArr[i3].getUpstreamFormat(), this.trackGroups.get(i2).getFormat(0))) {
                    this.trackGroupToSampleQueueIndex[i2] = i3;
                    break;
                }
                i3++;
            }
        }
        Iterator<HlsSampleStream> it = this.hlsSampleStreams.iterator();
        while (it.hasNext()) {
            it.next().bindSampleQueue();
        }
    }

    @EnsuresNonNull({"trackGroups", "optionalTrackGroups", "trackGroupToSampleQueueIndex"})
    private void buildTracksFromSampleStreams() {
        int length = this.sampleQueues.length;
        int i = 6;
        int i2 = -1;
        int i3 = 0;
        while (true) {
            int i4 = 2;
            if (i3 >= length) {
                break;
            }
            String str = this.sampleQueues[i3].getUpstreamFormat().sampleMimeType;
            if (!MimeTypes.isVideo(str)) {
                if (MimeTypes.isAudio(str)) {
                    i4 = 1;
                } else {
                    i4 = MimeTypes.isText(str) ? 3 : 6;
                }
            }
            if (getTrackTypeScore(i4) > getTrackTypeScore(i)) {
                i2 = i3;
                i = i4;
            } else if (i4 == i && i2 != -1) {
                i2 = -1;
            }
            i3++;
        }
        TrackGroup trackGroup = this.chunkSource.getTrackGroup();
        int i5 = trackGroup.length;
        this.primaryTrackGroupIndex = -1;
        this.trackGroupToSampleQueueIndex = new int[length];
        for (int i6 = 0; i6 < length; i6++) {
            this.trackGroupToSampleQueueIndex[i6] = i6;
        }
        TrackGroup[] trackGroupArr = new TrackGroup[length];
        for (int i7 = 0; i7 < length; i7++) {
            Format upstreamFormat = this.sampleQueues[i7].getUpstreamFormat();
            if (i7 == i2) {
                Format[] formatArr = new Format[i5];
                if (i5 == 1) {
                    formatArr[0] = upstreamFormat.copyWithManifestFormatInfo(trackGroup.getFormat(0));
                } else {
                    for (int i8 = 0; i8 < i5; i8++) {
                        formatArr[i8] = deriveFormat(trackGroup.getFormat(i8), upstreamFormat, true);
                    }
                }
                trackGroupArr[i7] = new TrackGroup(formatArr);
                this.primaryTrackGroupIndex = i7;
            } else {
                trackGroupArr[i7] = new TrackGroup(deriveFormat((i == 2 && MimeTypes.isAudio(upstreamFormat.sampleMimeType)) ? this.muxedAudioFormat : null, upstreamFormat, false));
            }
        }
        this.trackGroups = createTrackGroupArrayWithDrmInfo(trackGroupArr);
        Assertions.checkState(this.optionalTrackGroups == null);
        this.optionalTrackGroups = Collections.emptySet();
    }

    private TrackGroupArray createTrackGroupArrayWithDrmInfo(TrackGroup[] trackGroupArr) {
        for (int i = 0; i < trackGroupArr.length; i++) {
            TrackGroup trackGroup = trackGroupArr[i];
            Format[] formatArr = new Format[trackGroup.length];
            for (int i2 = 0; i2 < trackGroup.length; i2++) {
                Format format = trackGroup.getFormat(i2);
                if (format.drmInitData != null) {
                    format = format.copyWithExoMediaCryptoType(this.drmSessionManager.getExoMediaCryptoType(format.drmInitData));
                }
                formatArr[i2] = format;
            }
            trackGroupArr[i] = new TrackGroup(formatArr);
        }
        return new TrackGroupArray(trackGroupArr);
    }

    private HlsMediaChunk getLastMediaChunk() {
        return this.mediaChunks.get(r0.size() - 1);
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C.TIME_UNSET;
    }

    private boolean seekInsideBufferUs(long j) {
        int length = this.sampleQueues.length;
        for (int i = 0; i < length; i++) {
            if (!this.sampleQueues[i].seekTo(j, false) && (this.sampleQueueIsAudioVideoFlags[i] || !this.haveAudioVideoSampleQueues)) {
                return false;
            }
        }
        return true;
    }

    @RequiresNonNull({"trackGroups", "optionalTrackGroups"})
    private void setIsPrepared() {
        this.prepared = true;
    }

    @EnsuresNonNull({"trackGroups", "optionalTrackGroups"})
    private void assertIsPrepared() {
        Assertions.checkState(this.prepared);
        Assertions.checkNotNull(this.trackGroups);
        Assertions.checkNotNull(this.optionalTrackGroups);
    }

    private static Format deriveFormat(Format format, Format format2, boolean z) {
        if (format == null) {
            return format2;
        }
        int i = z ? format.bitrate : -1;
        int i2 = format.channelCount != -1 ? format.channelCount : format2.channelCount;
        String codecsOfType = Util.getCodecsOfType(format.codecs, MimeTypes.getTrackType(format2.sampleMimeType));
        String mediaMimeType = MimeTypes.getMediaMimeType(codecsOfType);
        if (mediaMimeType == null) {
            mediaMimeType = format2.sampleMimeType;
        }
        return format2.copyWithContainerInfo(format.id, format.label, mediaMimeType, codecsOfType, format.metadata, i, format.width, format.height, i2, format.selectionFlags, format.language);
    }

    private static boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof HlsMediaChunk;
    }

    private static boolean formatsMatch(Format format, Format format2) {
        String str = format.sampleMimeType;
        String str2 = format2.sampleMimeType;
        int trackType = MimeTypes.getTrackType(str);
        if (trackType != 3) {
            return trackType == MimeTypes.getTrackType(str2);
        }
        if (Util.areEqual(str, str2)) {
            return !(MimeTypes.APPLICATION_CEA608.equals(str) || MimeTypes.APPLICATION_CEA708.equals(str)) || format.accessibilityChannel == format2.accessibilityChannel;
        }
        return false;
    }

    private static DummyTrackOutput createDummyTrackOutput(int i, int i2) {
        Log.w(TAG, "Unmapped track with id " + i + " of type " + i2);
        return new DummyTrackOutput();
    }

    private static final class FormatAdjustingSampleQueue extends SampleQueue {
        private DrmInitData drmInitData;
        private final Map<String, DrmInitData> overridingDrmInitData;

        public FormatAdjustingSampleQueue(Allocator allocator, DrmSessionManager<?> drmSessionManager, Map<String, DrmInitData> map) {
            super(allocator, drmSessionManager);
            this.overridingDrmInitData = map;
        }

        public void setDrmInitData(DrmInitData drmInitData) {
            this.drmInitData = drmInitData;
            invalidateUpstreamFormatAdjustment();
        }

        @Override // com.google.android.exoplayer2.source.SampleQueue
        public Format getAdjustedUpstreamFormat(Format format) {
            DrmInitData drmInitData;
            DrmInitData drmInitData2 = this.drmInitData;
            if (drmInitData2 == null) {
                drmInitData2 = format.drmInitData;
            }
            if (drmInitData2 != null && (drmInitData = this.overridingDrmInitData.get(drmInitData2.schemeType)) != null) {
                drmInitData2 = drmInitData;
            }
            return super.getAdjustedUpstreamFormat(format.copyWithAdjustments(drmInitData2, getAdjustedMetadata(format.metadata)));
        }

        private Metadata getAdjustedMetadata(Metadata metadata) {
            if (metadata == null) {
                return null;
            }
            int length = metadata.length();
            int i = 0;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    i2 = -1;
                    break;
                }
                Metadata.Entry entry = metadata.get(i2);
                if ((entry instanceof PrivFrame) && HlsMediaChunk.PRIV_TIMESTAMP_FRAME_OWNER.equals(((PrivFrame) entry).owner)) {
                    break;
                }
                i2++;
            }
            if (i2 == -1) {
                return metadata;
            }
            if (length == 1) {
                return null;
            }
            Metadata.Entry[] entryArr = new Metadata.Entry[length - 1];
            while (i < length) {
                if (i != i2) {
                    entryArr[i < i2 ? i : i - 1] = metadata.get(i);
                }
                i++;
            }
            return new Metadata(entryArr);
        }
    }

    private static class EmsgUnwrappingTrackOutput implements TrackOutput {
        private static final String TAG = "EmsgUnwrappingTrackOutput";
        private byte[] buffer;
        private int bufferPosition;
        private final TrackOutput delegate;
        private final Format delegateFormat;
        private final EventMessageDecoder emsgDecoder = new EventMessageDecoder();
        private Format format;
        private static final Format ID3_FORMAT = Format.createSampleFormat(null, MimeTypes.APPLICATION_ID3, Long.MAX_VALUE);
        private static final Format EMSG_FORMAT = Format.createSampleFormat(null, MimeTypes.APPLICATION_EMSG, Long.MAX_VALUE);

        public EmsgUnwrappingTrackOutput(TrackOutput trackOutput, int i) {
            this.delegate = trackOutput;
            if (i == 1) {
                this.delegateFormat = ID3_FORMAT;
            } else if (i == 3) {
                this.delegateFormat = EMSG_FORMAT;
            } else {
                throw new IllegalArgumentException("Unknown metadataType: " + i);
            }
            this.buffer = new byte[0];
            this.bufferPosition = 0;
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void format(Format format) {
            this.format = format;
            this.delegate.format(this.delegateFormat);
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public int sampleData(ExtractorInput extractorInput, int i, boolean z) throws IOException, InterruptedException {
            ensureBufferCapacity(this.bufferPosition + i);
            int read = extractorInput.read(this.buffer, this.bufferPosition, i);
            if (read != -1) {
                this.bufferPosition += read;
                return read;
            }
            if (z) {
                return -1;
            }
            throw new EOFException();
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void sampleData(ParsableByteArray parsableByteArray, int i) {
            ensureBufferCapacity(this.bufferPosition + i);
            parsableByteArray.readBytes(this.buffer, this.bufferPosition, i);
            this.bufferPosition += i;
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void sampleMetadata(long j, int i, int i2, int i3, TrackOutput.CryptoData cryptoData) {
            Assertions.checkNotNull(this.format);
            ParsableByteArray sampleAndTrimBuffer = getSampleAndTrimBuffer(i2, i3);
            if (!Util.areEqual(this.format.sampleMimeType, this.delegateFormat.sampleMimeType)) {
                if (MimeTypes.APPLICATION_EMSG.equals(this.format.sampleMimeType)) {
                    EventMessage decode = this.emsgDecoder.decode(sampleAndTrimBuffer);
                    if (!emsgContainsExpectedWrappedFormat(decode)) {
                        Log.w(TAG, String.format("Ignoring EMSG. Expected it to contain wrapped %s but actual wrapped format: %s", this.delegateFormat.sampleMimeType, decode.getWrappedMetadataFormat()));
                        return;
                    }
                    sampleAndTrimBuffer = new ParsableByteArray((byte[]) Assertions.checkNotNull(decode.getWrappedMetadataBytes()));
                } else {
                    Log.w(TAG, "Ignoring sample for unsupported format: " + this.format.sampleMimeType);
                    return;
                }
            }
            int bytesLeft = sampleAndTrimBuffer.bytesLeft();
            this.delegate.sampleData(sampleAndTrimBuffer, bytesLeft);
            this.delegate.sampleMetadata(j, i, bytesLeft, i3, cryptoData);
        }

        private boolean emsgContainsExpectedWrappedFormat(EventMessage eventMessage) {
            Format wrappedMetadataFormat = eventMessage.getWrappedMetadataFormat();
            return wrappedMetadataFormat != null && Util.areEqual(this.delegateFormat.sampleMimeType, wrappedMetadataFormat.sampleMimeType);
        }

        private void ensureBufferCapacity(int i) {
            byte[] bArr = this.buffer;
            if (bArr.length < i) {
                this.buffer = Arrays.copyOf(bArr, i + (i / 2));
            }
        }

        private ParsableByteArray getSampleAndTrimBuffer(int i, int i2) {
            int i3 = this.bufferPosition - i2;
            ParsableByteArray parsableByteArray = new ParsableByteArray(Arrays.copyOfRange(this.buffer, i3 - i, i3));
            byte[] bArr = this.buffer;
            System.arraycopy(bArr, i3, bArr, 0, i2);
            this.bufferPosition = i2;
            return parsableByteArray;
        }
    }
}