package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.chunk.ChunkSource;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class ChunkSampleStream<T extends ChunkSource> implements SampleStream, SequenceableLoader, Loader.Callback<Chunk>, Loader.ReleaseCallback {
    private static final String TAG = "ChunkSampleStream";
    private final SequenceableLoader.Callback<ChunkSampleStream<T>> callback;
    private final BaseMediaChunkOutput chunkOutput;
    private final T chunkSource;
    long decodeOnlyUntilPositionUs;
    private final SampleQueue[] embeddedSampleQueues;
    private final Format[] embeddedTrackFormats;
    private final int[] embeddedTrackTypes;
    private final boolean[] embeddedTracksSelected;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private long lastSeekPositionUs;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    boolean loadingFinished;
    private final ArrayList<BaseMediaChunk> mediaChunks;
    private int nextNotifyPrimaryFormatMediaChunkIndex;
    private long pendingResetPositionUs;
    private Format primaryDownstreamTrackFormat;
    private final SampleQueue primarySampleQueue;
    public final int primaryTrackType;
    private final List<BaseMediaChunk> readOnlyMediaChunks;
    private ReleaseCallback<T> releaseCallback;
    private final Loader loader = new Loader("Loader:ChunkSampleStream");
    private final ChunkHolder nextChunkHolder = new ChunkHolder();

    public interface ReleaseCallback<T extends ChunkSource> {
        void onSampleStreamReleased(ChunkSampleStream<T> chunkSampleStream);
    }

    public ChunkSampleStream(int i, int[] iArr, Format[] formatArr, T t, SequenceableLoader.Callback<ChunkSampleStream<T>> callback, Allocator allocator, long j, DrmSessionManager<?> drmSessionManager, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher eventDispatcher) {
        this.primaryTrackType = i;
        this.embeddedTrackTypes = iArr;
        this.embeddedTrackFormats = formatArr;
        this.chunkSource = t;
        this.callback = callback;
        this.eventDispatcher = eventDispatcher;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        ArrayList<BaseMediaChunk> arrayList = new ArrayList<>();
        this.mediaChunks = arrayList;
        this.readOnlyMediaChunks = Collections.unmodifiableList(arrayList);
        int i2 = 0;
        int length = iArr == null ? 0 : iArr.length;
        this.embeddedSampleQueues = new SampleQueue[length];
        this.embeddedTracksSelected = new boolean[length];
        int i3 = length + 1;
        int[] iArr2 = new int[i3];
        SampleQueue[] sampleQueueArr = new SampleQueue[i3];
        SampleQueue sampleQueue = new SampleQueue(allocator, drmSessionManager);
        this.primarySampleQueue = sampleQueue;
        iArr2[0] = i;
        sampleQueueArr[0] = sampleQueue;
        while (i2 < length) {
            SampleQueue sampleQueue2 = new SampleQueue(allocator, DrmSessionManager.getDummyDrmSessionManager());
            this.embeddedSampleQueues[i2] = sampleQueue2;
            int i4 = i2 + 1;
            sampleQueueArr[i4] = sampleQueue2;
            iArr2[i4] = iArr[i2];
            i2 = i4;
        }
        this.chunkOutput = new BaseMediaChunkOutput(iArr2, sampleQueueArr);
        this.pendingResetPositionUs = j;
        this.lastSeekPositionUs = j;
    }

    public void discardBuffer(long j, boolean z) {
        if (isPendingReset()) {
            return;
        }
        int firstIndex = this.primarySampleQueue.getFirstIndex();
        this.primarySampleQueue.discardTo(j, z, true);
        int firstIndex2 = this.primarySampleQueue.getFirstIndex();
        if (firstIndex2 > firstIndex) {
            long firstTimestampUs = this.primarySampleQueue.getFirstTimestampUs();
            int i = 0;
            while (true) {
                SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
                if (i >= sampleQueueArr.length) {
                    break;
                }
                sampleQueueArr[i].discardTo(firstTimestampUs, z, this.embeddedTracksSelected[i]);
                i++;
            }
        }
        discardDownstreamMediaChunks(firstIndex2);
    }

    public ChunkSampleStream<T>.EmbeddedSampleStream selectEmbeddedTrack(long j, int i) {
        for (int i2 = 0; i2 < this.embeddedSampleQueues.length; i2++) {
            if (this.embeddedTrackTypes[i2] == i) {
                Assertions.checkState(!this.embeddedTracksSelected[i2]);
                this.embeddedTracksSelected[i2] = true;
                this.embeddedSampleQueues[i2].seekTo(j, true);
                return new EmbeddedSampleStream(this, this.embeddedSampleQueues[i2], i2);
            }
        }
        throw new IllegalStateException();
    }

    public T getChunkSource() {
        return this.chunkSource;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public long getBufferedPositionUs() {
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long j = this.lastSeekPositionUs;
        BaseMediaChunk lastMediaChunk = getLastMediaChunk();
        if (!lastMediaChunk.isLoadCompleted()) {
            if (this.mediaChunks.size() > 1) {
                lastMediaChunk = this.mediaChunks.get(r2.size() - 2);
            } else {
                lastMediaChunk = null;
            }
        }
        if (lastMediaChunk != null) {
            j = Math.max(j, lastMediaChunk.endTimeUs);
        }
        return Math.max(j, this.primarySampleQueue.getLargestQueuedTimestampUs());
    }

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        return this.chunkSource.getAdjustedSeekPositionUs(j, seekParameters);
    }

    public void seekToUs(long j) {
        BaseMediaChunk baseMediaChunk;
        boolean seekTo;
        this.lastSeekPositionUs = j;
        if (isPendingReset()) {
            this.pendingResetPositionUs = j;
            return;
        }
        int i = 0;
        for (int i2 = 0; i2 < this.mediaChunks.size(); i2++) {
            baseMediaChunk = this.mediaChunks.get(i2);
            long j2 = baseMediaChunk.startTimeUs;
            if (j2 == j && baseMediaChunk.clippedStartTimeUs == C.TIME_UNSET) {
                break;
            } else {
                if (j2 > j) {
                    break;
                }
            }
        }
        baseMediaChunk = null;
        if (baseMediaChunk != null) {
            seekTo = this.primarySampleQueue.seekTo(baseMediaChunk.getFirstSampleIndex(0));
            this.decodeOnlyUntilPositionUs = 0L;
        } else {
            seekTo = this.primarySampleQueue.seekTo(j, j < getNextLoadPositionUs());
            this.decodeOnlyUntilPositionUs = this.lastSeekPositionUs;
        }
        if (seekTo) {
            this.nextNotifyPrimaryFormatMediaChunkIndex = primarySampleIndexToMediaChunkIndex(this.primarySampleQueue.getReadIndex(), 0);
            SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
            int length = sampleQueueArr.length;
            while (i < length) {
                sampleQueueArr[i].seekTo(j, true);
                i++;
            }
            return;
        }
        this.pendingResetPositionUs = j;
        this.loadingFinished = false;
        this.mediaChunks.clear();
        this.nextNotifyPrimaryFormatMediaChunkIndex = 0;
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
            return;
        }
        this.loader.clearFatalError();
        this.primarySampleQueue.reset();
        SampleQueue[] sampleQueueArr2 = this.embeddedSampleQueues;
        int length2 = sampleQueueArr2.length;
        while (i < length2) {
            sampleQueueArr2[i].reset();
            i++;
        }
    }

    public void release() {
        release(null);
    }

    public void release(ReleaseCallback<T> releaseCallback) {
        this.releaseCallback = releaseCallback;
        this.primarySampleQueue.preRelease();
        for (SampleQueue sampleQueue : this.embeddedSampleQueues) {
            sampleQueue.preRelease();
        }
        this.loader.release(this);
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.ReleaseCallback
    public void onLoaderReleased() {
        this.primarySampleQueue.release();
        for (SampleQueue sampleQueue : this.embeddedSampleQueues) {
            sampleQueue.release();
        }
        ReleaseCallback<T> releaseCallback = this.releaseCallback;
        if (releaseCallback != null) {
            releaseCallback.onSampleStreamReleased(this);
        }
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public boolean isReady() {
        return !isPendingReset() && this.primarySampleQueue.isReady(this.loadingFinished);
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
        this.primarySampleQueue.maybeThrowError();
        if (this.loader.isLoading()) {
            return;
        }
        this.chunkSource.maybeThrowError();
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
        if (isPendingReset()) {
            return -3;
        }
        maybeNotifyPrimaryTrackFormatChanged();
        return this.primarySampleQueue.read(formatHolder, decoderInputBuffer, z, this.loadingFinished, this.decodeOnlyUntilPositionUs);
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public int skipData(long j) {
        int advanceTo;
        if (isPendingReset()) {
            return 0;
        }
        if (this.loadingFinished && j > this.primarySampleQueue.getLargestQueuedTimestampUs()) {
            advanceTo = this.primarySampleQueue.advanceToEnd();
        } else {
            advanceTo = this.primarySampleQueue.advanceTo(j);
        }
        maybeNotifyPrimaryTrackFormatChanged();
        return advanceTo;
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Callback
    public void onLoadCompleted(Chunk chunk, long j, long j2) {
        this.chunkSource.onChunkLoadCompleted(chunk);
        this.eventDispatcher.loadCompleted(chunk.dataSpec, chunk.getUri(), chunk.getResponseHeaders(), chunk.type, this.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, j, j2, chunk.bytesLoaded());
        this.callback.onContinueLoadingRequested(this);
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Callback
    public void onLoadCanceled(Chunk chunk, long j, long j2, boolean z) {
        this.eventDispatcher.loadCanceled(chunk.dataSpec, chunk.getUri(), chunk.getResponseHeaders(), chunk.type, this.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, j, j2, chunk.bytesLoaded());
        if (z) {
            return;
        }
        this.primarySampleQueue.reset();
        for (SampleQueue sampleQueue : this.embeddedSampleQueues) {
            sampleQueue.reset();
        }
        this.callback.onContinueLoadingRequested(this);
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0076  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00c3  */
    @Override // com.google.android.exoplayer2.upstream.Loader.Callback
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.google.android.exoplayer2.upstream.Loader.LoadErrorAction onLoadError(com.google.android.exoplayer2.source.chunk.Chunk r30, long r31, long r33, java.io.IOException r35, int r36) {
        /*
            r29 = this;
            r0 = r29
            r7 = r30
            long r25 = r30.bytesLoaded()
            boolean r8 = r29.isMediaChunk(r30)
            java.util.ArrayList<com.google.android.exoplayer2.source.chunk.BaseMediaChunk> r1 = r0.mediaChunks
            int r1 = r1.size()
            r9 = 1
            int r10 = r1 + (-1)
            r1 = 0
            int r1 = (r25 > r1 ? 1 : (r25 == r1 ? 0 : -1))
            r11 = 0
            if (r1 == 0) goto L27
            if (r8 == 0) goto L27
            boolean r1 = r0.haveReadFromMediaChunk(r10)
            if (r1 != 0) goto L25
            goto L27
        L25:
            r12 = r11
            goto L28
        L27:
            r12 = r9
        L28:
            r13 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            if (r12 == 0) goto L3f
            com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy r1 = r0.loadErrorHandlingPolicy
            int r2 = r7.type
            r3 = r33
            r5 = r35
            r6 = r36
            long r1 = r1.getBlacklistDurationMsFor(r2, r3, r5, r6)
            r5 = r1
            goto L40
        L3f:
            r5 = r13
        L40:
            T extends com.google.android.exoplayer2.source.chunk.ChunkSource r1 = r0.chunkSource
            r2 = r30
            r3 = r12
            r4 = r35
            boolean r1 = r1.onChunkLoadError(r2, r3, r4, r5)
            if (r1 == 0) goto L73
            if (r12 == 0) goto L6c
            com.google.android.exoplayer2.upstream.Loader$LoadErrorAction r1 = com.google.android.exoplayer2.upstream.Loader.DONT_RETRY
            if (r8 == 0) goto L74
            com.google.android.exoplayer2.source.chunk.BaseMediaChunk r2 = r0.discardUpstreamMediaChunksFromIndex(r10)
            if (r2 != r7) goto L5b
            r2 = r9
            goto L5c
        L5b:
            r2 = r11
        L5c:
            com.google.android.exoplayer2.util.Assertions.checkState(r2)
            java.util.ArrayList<com.google.android.exoplayer2.source.chunk.BaseMediaChunk> r2 = r0.mediaChunks
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L74
            long r2 = r0.lastSeekPositionUs
            r0.pendingResetPositionUs = r2
            goto L74
        L6c:
            java.lang.String r1 = "ChunkSampleStream"
            java.lang.String r2 = "Ignoring attempt to cancel non-cancelable load."
            com.google.android.exoplayer2.util.Log.w(r1, r2)
        L73:
            r1 = 0
        L74:
            if (r1 != 0) goto L91
            com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy r15 = r0.loadErrorHandlingPolicy
            int r1 = r7.type
            r16 = r1
            r17 = r33
            r19 = r35
            r20 = r36
            long r1 = r15.getRetryDelayMsFor(r16, r17, r19, r20)
            int r3 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r3 == 0) goto L8f
            com.google.android.exoplayer2.upstream.Loader$LoadErrorAction r1 = com.google.android.exoplayer2.upstream.Loader.createRetryAction(r11, r1)
            goto L91
        L8f:
            com.google.android.exoplayer2.upstream.Loader$LoadErrorAction r1 = com.google.android.exoplayer2.upstream.Loader.DONT_RETRY_FATAL
        L91:
            boolean r2 = r1.isRetry()
            r2 = r2 ^ r9
            r28 = r2
            com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher r8 = r0.eventDispatcher
            com.google.android.exoplayer2.upstream.DataSpec r9 = r7.dataSpec
            android.net.Uri r10 = r30.getUri()
            java.util.Map r11 = r30.getResponseHeaders()
            int r12 = r7.type
            int r13 = r0.primaryTrackType
            com.google.android.exoplayer2.Format r14 = r7.trackFormat
            int r15 = r7.trackSelectionReason
            java.lang.Object r3 = r7.trackSelectionData
            r16 = r3
            long r3 = r7.startTimeUs
            r17 = r3
            long r3 = r7.endTimeUs
            r19 = r3
            r21 = r31
            r23 = r33
            r27 = r35
            r8.loadError(r9, r10, r11, r12, r13, r14, r15, r16, r17, r19, r21, r23, r25, r27, r28)
            if (r2 == 0) goto Lc8
            com.google.android.exoplayer2.source.SequenceableLoader$Callback<com.google.android.exoplayer2.source.chunk.ChunkSampleStream<T extends com.google.android.exoplayer2.source.chunk.ChunkSource>> r2 = r0.callback
            r2.onContinueLoadingRequested(r0)
        Lc8:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.chunk.ChunkSampleStream.onLoadError(com.google.android.exoplayer2.source.chunk.Chunk, long, long, java.io.IOException, int):com.google.android.exoplayer2.upstream.Loader$LoadErrorAction");
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long j) {
        List<BaseMediaChunk> list;
        long j2;
        if (this.loadingFinished || this.loader.isLoading() || this.loader.hasFatalError()) {
            return false;
        }
        boolean isPendingReset = isPendingReset();
        if (isPendingReset) {
            list = Collections.emptyList();
            j2 = this.pendingResetPositionUs;
        } else {
            list = this.readOnlyMediaChunks;
            j2 = getLastMediaChunk().endTimeUs;
        }
        this.chunkSource.getNextChunk(j, j2, list, this.nextChunkHolder);
        boolean z = this.nextChunkHolder.endOfStream;
        Chunk chunk = this.nextChunkHolder.chunk;
        this.nextChunkHolder.clear();
        if (z) {
            this.pendingResetPositionUs = C.TIME_UNSET;
            this.loadingFinished = true;
            return true;
        }
        if (chunk == null) {
            return false;
        }
        if (isMediaChunk(chunk)) {
            BaseMediaChunk baseMediaChunk = (BaseMediaChunk) chunk;
            if (isPendingReset) {
                long j3 = baseMediaChunk.startTimeUs;
                long j4 = this.pendingResetPositionUs;
                if (j3 == j4) {
                    j4 = 0;
                }
                this.decodeOnlyUntilPositionUs = j4;
                this.pendingResetPositionUs = C.TIME_UNSET;
            }
            baseMediaChunk.init(this.chunkOutput);
            this.mediaChunks.add(baseMediaChunk);
        } else if (chunk instanceof InitializationChunk) {
            ((InitializationChunk) chunk).init(this.chunkOutput);
        }
        this.eventDispatcher.loadStarted(chunk.dataSpec, chunk.type, this.primaryTrackType, chunk.trackFormat, chunk.trackSelectionReason, chunk.trackSelectionData, chunk.startTimeUs, chunk.endTimeUs, this.loader.startLoading(chunk, this, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(chunk.type)));
        return true;
    }

    @Override // com.google.android.exoplayer2.source.SequenceableLoader
    public boolean isLoading() {
        return this.loader.isLoading();
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
    public void reevaluateBuffer(long j) {
        int size;
        int preferredQueueSize;
        if (this.loader.isLoading() || this.loader.hasFatalError() || isPendingReset() || (size = this.mediaChunks.size()) <= (preferredQueueSize = this.chunkSource.getPreferredQueueSize(j, this.readOnlyMediaChunks))) {
            return;
        }
        while (true) {
            if (preferredQueueSize >= size) {
                preferredQueueSize = size;
                break;
            } else if (!haveReadFromMediaChunk(preferredQueueSize)) {
                break;
            } else {
                preferredQueueSize++;
            }
        }
        if (preferredQueueSize == size) {
            return;
        }
        long j2 = getLastMediaChunk().endTimeUs;
        BaseMediaChunk discardUpstreamMediaChunksFromIndex = discardUpstreamMediaChunksFromIndex(preferredQueueSize);
        if (this.mediaChunks.isEmpty()) {
            this.pendingResetPositionUs = this.lastSeekPositionUs;
        }
        this.loadingFinished = false;
        this.eventDispatcher.upstreamDiscarded(this.primaryTrackType, discardUpstreamMediaChunksFromIndex.startTimeUs, j2);
    }

    private boolean isMediaChunk(Chunk chunk) {
        return chunk instanceof BaseMediaChunk;
    }

    private boolean haveReadFromMediaChunk(int i) {
        int readIndex;
        BaseMediaChunk baseMediaChunk = this.mediaChunks.get(i);
        if (this.primarySampleQueue.getReadIndex() > baseMediaChunk.getFirstSampleIndex(0)) {
            return true;
        }
        int i2 = 0;
        do {
            SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
            if (i2 >= sampleQueueArr.length) {
                return false;
            }
            readIndex = sampleQueueArr[i2].getReadIndex();
            i2++;
        } while (readIndex <= baseMediaChunk.getFirstSampleIndex(i2));
        return true;
    }

    boolean isPendingReset() {
        return this.pendingResetPositionUs != C.TIME_UNSET;
    }

    private void discardDownstreamMediaChunks(int i) {
        int min = Math.min(primarySampleIndexToMediaChunkIndex(i, 0), this.nextNotifyPrimaryFormatMediaChunkIndex);
        if (min > 0) {
            Util.removeRange(this.mediaChunks, 0, min);
            this.nextNotifyPrimaryFormatMediaChunkIndex -= min;
        }
    }

    private void maybeNotifyPrimaryTrackFormatChanged() {
        int primarySampleIndexToMediaChunkIndex = primarySampleIndexToMediaChunkIndex(this.primarySampleQueue.getReadIndex(), this.nextNotifyPrimaryFormatMediaChunkIndex - 1);
        while (true) {
            int i = this.nextNotifyPrimaryFormatMediaChunkIndex;
            if (i > primarySampleIndexToMediaChunkIndex) {
                return;
            }
            this.nextNotifyPrimaryFormatMediaChunkIndex = i + 1;
            maybeNotifyPrimaryTrackFormatChanged(i);
        }
    }

    private void maybeNotifyPrimaryTrackFormatChanged(int i) {
        BaseMediaChunk baseMediaChunk = this.mediaChunks.get(i);
        Format format = baseMediaChunk.trackFormat;
        if (!format.equals(this.primaryDownstreamTrackFormat)) {
            this.eventDispatcher.downstreamFormatChanged(this.primaryTrackType, format, baseMediaChunk.trackSelectionReason, baseMediaChunk.trackSelectionData, baseMediaChunk.startTimeUs);
        }
        this.primaryDownstreamTrackFormat = format;
    }

    private int primarySampleIndexToMediaChunkIndex(int i, int i2) {
        do {
            i2++;
            if (i2 >= this.mediaChunks.size()) {
                return this.mediaChunks.size() - 1;
            }
        } while (this.mediaChunks.get(i2).getFirstSampleIndex(0) <= i);
        return i2 - 1;
    }

    private BaseMediaChunk getLastMediaChunk() {
        return this.mediaChunks.get(r0.size() - 1);
    }

    private BaseMediaChunk discardUpstreamMediaChunksFromIndex(int i) {
        BaseMediaChunk baseMediaChunk = this.mediaChunks.get(i);
        ArrayList<BaseMediaChunk> arrayList = this.mediaChunks;
        Util.removeRange(arrayList, i, arrayList.size());
        this.nextNotifyPrimaryFormatMediaChunkIndex = Math.max(this.nextNotifyPrimaryFormatMediaChunkIndex, this.mediaChunks.size());
        int i2 = 0;
        this.primarySampleQueue.discardUpstreamSamples(baseMediaChunk.getFirstSampleIndex(0));
        while (true) {
            SampleQueue[] sampleQueueArr = this.embeddedSampleQueues;
            if (i2 >= sampleQueueArr.length) {
                return baseMediaChunk;
            }
            SampleQueue sampleQueue = sampleQueueArr[i2];
            i2++;
            sampleQueue.discardUpstreamSamples(baseMediaChunk.getFirstSampleIndex(i2));
        }
    }

    public final class EmbeddedSampleStream implements SampleStream {
        private final int index;
        private boolean notifiedDownstreamFormat;
        public final ChunkSampleStream<T> parent;
        private final SampleQueue sampleQueue;

        @Override // com.google.android.exoplayer2.source.SampleStream
        public void maybeThrowError() throws IOException {
        }

        public EmbeddedSampleStream(ChunkSampleStream<T> chunkSampleStream, SampleQueue sampleQueue, int i) {
            this.parent = chunkSampleStream;
            this.sampleQueue = sampleQueue;
            this.index = i;
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public boolean isReady() {
            return !ChunkSampleStream.this.isPendingReset() && this.sampleQueue.isReady(ChunkSampleStream.this.loadingFinished);
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int skipData(long j) {
            if (ChunkSampleStream.this.isPendingReset()) {
                return 0;
            }
            maybeNotifyDownstreamFormat();
            if (ChunkSampleStream.this.loadingFinished && j > this.sampleQueue.getLargestQueuedTimestampUs()) {
                return this.sampleQueue.advanceToEnd();
            }
            return this.sampleQueue.advanceTo(j);
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
            if (ChunkSampleStream.this.isPendingReset()) {
                return -3;
            }
            maybeNotifyDownstreamFormat();
            return this.sampleQueue.read(formatHolder, decoderInputBuffer, z, ChunkSampleStream.this.loadingFinished, ChunkSampleStream.this.decodeOnlyUntilPositionUs);
        }

        public void release() {
            Assertions.checkState(ChunkSampleStream.this.embeddedTracksSelected[this.index]);
            ChunkSampleStream.this.embeddedTracksSelected[this.index] = false;
        }

        private void maybeNotifyDownstreamFormat() {
            if (this.notifiedDownstreamFormat) {
                return;
            }
            ChunkSampleStream.this.eventDispatcher.downstreamFormatChanged(ChunkSampleStream.this.embeddedTrackTypes[this.index], ChunkSampleStream.this.embeddedTrackFormats[this.index], 0, null, ChunkSampleStream.this.lastSeekPositionUs);
            this.notifiedDownstreamFormat = true;
        }
    }
}