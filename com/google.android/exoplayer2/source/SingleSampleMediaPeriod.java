package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.StatsDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
final class SingleSampleMediaPeriod implements MediaPeriod, Loader.Callback<SourceLoadable> {
    private static final int INITIAL_SAMPLE_SIZE = 1024;
    private final DataSource.Factory dataSourceFactory;
    private final DataSpec dataSpec;
    private final long durationUs;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    final Format format;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    boolean loadingFinished;
    boolean notifiedReadingStarted;
    byte[] sampleData;
    int sampleSize;
    private final TrackGroupArray tracks;
    private final TransferListener transferListener;
    final boolean treatLoadErrorsAsEndOfStream;
    private final ArrayList<SampleStreamImpl> sampleStreams = new ArrayList<>();
    final Loader loader = new Loader("Loader:SingleSampleMediaPeriod");

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void discardBuffer(long j, boolean z) {
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        return j;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void maybeThrowPrepareError() throws IOException {
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public void reevaluateBuffer(long j) {
    }

    public SingleSampleMediaPeriod(DataSpec dataSpec, DataSource.Factory factory, TransferListener transferListener, Format format, long j, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher eventDispatcher, boolean z) {
        this.dataSpec = dataSpec;
        this.dataSourceFactory = factory;
        this.transferListener = transferListener;
        this.format = format;
        this.durationUs = j;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher;
        this.treatLoadErrorsAsEndOfStream = z;
        this.tracks = new TrackGroupArray(new TrackGroup(format));
        eventDispatcher.mediaPeriodCreated();
    }

    public void release() {
        this.loader.release();
        this.eventDispatcher.mediaPeriodReleased();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void prepare(MediaPeriod.Callback callback, long j) {
        callback.onPrepared(this);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public TrackGroupArray getTrackGroups() {
        return this.tracks;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long selectTracks(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j) {
        for (int i = 0; i < trackSelectionArr.length; i++) {
            SampleStream sampleStream = sampleStreamArr[i];
            if (sampleStream != null && (trackSelectionArr[i] == null || !zArr[i])) {
                this.sampleStreams.remove(sampleStream);
                sampleStreamArr[i] = null;
            }
            if (sampleStreamArr[i] == null && trackSelectionArr[i] != null) {
                SampleStreamImpl sampleStreamImpl = new SampleStreamImpl();
                this.sampleStreams.add(sampleStreamImpl);
                sampleStreamArr[i] = sampleStreamImpl;
                zArr2[i] = true;
            }
        }
        return j;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long j) {
        if (this.loadingFinished || this.loader.isLoading() || this.loader.hasFatalError()) {
            return false;
        }
        DataSource createDataSource = this.dataSourceFactory.createDataSource();
        TransferListener transferListener = this.transferListener;
        if (transferListener != null) {
            createDataSource.addTransferListener(transferListener);
        }
        this.eventDispatcher.loadStarted(this.dataSpec, 1, -1, this.format, 0, null, 0L, this.durationUs, this.loader.startLoading(new SourceLoadable(this.dataSpec, createDataSource), this, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(1)));
        return true;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean isLoading() {
        return this.loader.isLoading();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long readDiscontinuity() {
        if (this.notifiedReadingStarted) {
            return C.TIME_UNSET;
        }
        this.eventDispatcher.readingStarted();
        this.notifiedReadingStarted = true;
        return C.TIME_UNSET;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getNextLoadPositionUs() {
        return (this.loadingFinished || this.loader.isLoading()) ? Long.MIN_VALUE : 0L;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getBufferedPositionUs() {
        return this.loadingFinished ? Long.MIN_VALUE : 0L;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long seekToUs(long j) {
        for (int i = 0; i < this.sampleStreams.size(); i++) {
            this.sampleStreams.get(i).reset();
        }
        return j;
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Callback
    public void onLoadCompleted(SourceLoadable sourceLoadable, long j, long j2) {
        this.sampleSize = (int) sourceLoadable.dataSource.getBytesRead();
        this.sampleData = (byte[]) Assertions.checkNotNull(sourceLoadable.sampleData);
        this.loadingFinished = true;
        this.eventDispatcher.loadCompleted(sourceLoadable.dataSpec, sourceLoadable.dataSource.getLastOpenedUri(), sourceLoadable.dataSource.getLastResponseHeaders(), 1, -1, this.format, 0, null, 0L, this.durationUs, j, j2, this.sampleSize);
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Callback
    public void onLoadCanceled(SourceLoadable sourceLoadable, long j, long j2, boolean z) {
        this.eventDispatcher.loadCanceled(sourceLoadable.dataSpec, sourceLoadable.dataSource.getLastOpenedUri(), sourceLoadable.dataSource.getLastResponseHeaders(), 1, -1, null, 0, null, 0L, this.durationUs, j, j2, sourceLoadable.dataSource.getBytesRead());
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Callback
    public Loader.LoadErrorAction onLoadError(SourceLoadable sourceLoadable, long j, long j2, IOException iOException, int i) {
        Loader.LoadErrorAction createRetryAction;
        long retryDelayMsFor = this.loadErrorHandlingPolicy.getRetryDelayMsFor(1, j2, iOException, i);
        boolean z = retryDelayMsFor == C.TIME_UNSET || i >= this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(1);
        if (this.treatLoadErrorsAsEndOfStream && z) {
            this.loadingFinished = true;
            createRetryAction = Loader.DONT_RETRY;
        } else {
            createRetryAction = retryDelayMsFor != C.TIME_UNSET ? Loader.createRetryAction(false, retryDelayMsFor) : Loader.DONT_RETRY_FATAL;
        }
        this.eventDispatcher.loadError(sourceLoadable.dataSpec, sourceLoadable.dataSource.getLastOpenedUri(), sourceLoadable.dataSource.getLastResponseHeaders(), 1, -1, this.format, 0, null, 0L, this.durationUs, j, j2, sourceLoadable.dataSource.getBytesRead(), iOException, !createRetryAction.isRetry());
        return createRetryAction;
    }

    private final class SampleStreamImpl implements SampleStream {
        private static final int STREAM_STATE_END_OF_STREAM = 2;
        private static final int STREAM_STATE_SEND_FORMAT = 0;
        private static final int STREAM_STATE_SEND_SAMPLE = 1;
        private boolean notifiedDownstreamFormat;
        private int streamState;

        private SampleStreamImpl() {
        }

        public void reset() {
            if (this.streamState == 2) {
                this.streamState = 1;
            }
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public boolean isReady() {
            return SingleSampleMediaPeriod.this.loadingFinished;
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public void maybeThrowError() throws IOException {
            if (SingleSampleMediaPeriod.this.treatLoadErrorsAsEndOfStream) {
                return;
            }
            SingleSampleMediaPeriod.this.loader.maybeThrowError();
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
            maybeNotifyDownstreamFormat();
            int i = this.streamState;
            if (i == 2) {
                decoderInputBuffer.addFlag(4);
                return -4;
            }
            if (z || i == 0) {
                formatHolder.format = SingleSampleMediaPeriod.this.format;
                this.streamState = 1;
                return -5;
            }
            if (!SingleSampleMediaPeriod.this.loadingFinished) {
                return -3;
            }
            if (SingleSampleMediaPeriod.this.sampleData != null) {
                decoderInputBuffer.addFlag(1);
                decoderInputBuffer.timeUs = 0L;
                if (decoderInputBuffer.isFlagsOnly()) {
                    return -4;
                }
                decoderInputBuffer.ensureSpaceForWrite(SingleSampleMediaPeriod.this.sampleSize);
                decoderInputBuffer.data.put(SingleSampleMediaPeriod.this.sampleData, 0, SingleSampleMediaPeriod.this.sampleSize);
            } else {
                decoderInputBuffer.addFlag(4);
            }
            this.streamState = 2;
            return -4;
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int skipData(long j) {
            maybeNotifyDownstreamFormat();
            if (j <= 0 || this.streamState == 2) {
                return 0;
            }
            this.streamState = 2;
            return 1;
        }

        private void maybeNotifyDownstreamFormat() {
            if (this.notifiedDownstreamFormat) {
                return;
            }
            SingleSampleMediaPeriod.this.eventDispatcher.downstreamFormatChanged(MimeTypes.getTrackType(SingleSampleMediaPeriod.this.format.sampleMimeType), SingleSampleMediaPeriod.this.format, 0, null, 0L);
            this.notifiedDownstreamFormat = true;
        }
    }

    static final class SourceLoadable implements Loader.Loadable {
        private final StatsDataSource dataSource;
        public final DataSpec dataSpec;
        private byte[] sampleData;

        @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
        public void cancelLoad() {
        }

        public SourceLoadable(DataSpec dataSpec, DataSource dataSource) {
            this.dataSpec = dataSpec;
            this.dataSource = new StatsDataSource(dataSource);
        }

        @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
        public void load() throws IOException, InterruptedException {
            this.dataSource.resetBytesRead();
            try {
                this.dataSource.open(this.dataSpec);
                int i = 0;
                while (i != -1) {
                    int bytesRead = (int) this.dataSource.getBytesRead();
                    byte[] bArr = this.sampleData;
                    if (bArr == null) {
                        this.sampleData = new byte[1024];
                    } else if (bytesRead == bArr.length) {
                        this.sampleData = Arrays.copyOf(bArr, bArr.length * 2);
                    }
                    StatsDataSource statsDataSource = this.dataSource;
                    byte[] bArr2 = this.sampleData;
                    i = statsDataSource.read(bArr2, bytesRead, bArr2.length - bytesRead);
                }
            } finally {
                Util.closeQuietly(this.dataSource);
            }
        }
    }
}