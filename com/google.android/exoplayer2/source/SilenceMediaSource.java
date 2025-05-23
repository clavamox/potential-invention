package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;

/* loaded from: classes.dex */
public final class SilenceMediaSource extends BaseMediaSource {
    private static final int CHANNEL_COUNT = 2;
    private static final int ENCODING = 2;
    private final long durationUs;
    private static final int SAMPLE_RATE_HZ = 44100;
    private static final Format FORMAT = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, -1, -1, 2, SAMPLE_RATE_HZ, 2, null, null, 0, null);
    private static final byte[] SILENCE_SAMPLE = new byte[Util.getPcmFrameSize(2, 2) * 1024];

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void maybeThrowSourceInfoRefreshError() {
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void releasePeriod(MediaPeriod mediaPeriod) {
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource
    protected void releaseSourceInternal() {
    }

    public SilenceMediaSource(long j) {
        Assertions.checkArgument(j >= 0);
        this.durationUs = j;
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource
    protected void prepareSourceInternal(TransferListener transferListener) {
        refreshSourceInfo(new SinglePeriodTimeline(this.durationUs, true, false, false));
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public MediaPeriod createPeriod(MediaSource.MediaPeriodId mediaPeriodId, Allocator allocator, long j) {
        return new SilenceMediaPeriod(this.durationUs);
    }

    private static final class SilenceMediaPeriod implements MediaPeriod {
        private static final TrackGroupArray TRACKS = new TrackGroupArray(new TrackGroup(SilenceMediaSource.FORMAT));
        private final long durationUs;
        private final ArrayList<SampleStream> sampleStreams = new ArrayList<>();

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public boolean continueLoading(long j) {
            return false;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public void discardBuffer(long j, boolean z) {
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public long getBufferedPositionUs() {
            return Long.MIN_VALUE;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public long getNextLoadPositionUs() {
            return Long.MIN_VALUE;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public boolean isLoading() {
            return false;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public void maybeThrowPrepareError() {
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long readDiscontinuity() {
            return C.TIME_UNSET;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public void reevaluateBuffer(long j) {
        }

        public SilenceMediaPeriod(long j) {
            this.durationUs = j;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public void prepare(MediaPeriod.Callback callback, long j) {
            callback.onPrepared(this);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public TrackGroupArray getTrackGroups() {
            return TRACKS;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long selectTracks(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j) {
            long constrainSeekPosition = constrainSeekPosition(j);
            for (int i = 0; i < trackSelectionArr.length; i++) {
                SampleStream sampleStream = sampleStreamArr[i];
                if (sampleStream != null && (trackSelectionArr[i] == null || !zArr[i])) {
                    this.sampleStreams.remove(sampleStream);
                    sampleStreamArr[i] = null;
                }
                if (sampleStreamArr[i] == null && trackSelectionArr[i] != null) {
                    SilenceSampleStream silenceSampleStream = new SilenceSampleStream(this.durationUs);
                    silenceSampleStream.seekTo(constrainSeekPosition);
                    this.sampleStreams.add(silenceSampleStream);
                    sampleStreamArr[i] = silenceSampleStream;
                    zArr2[i] = true;
                }
            }
            return constrainSeekPosition;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long seekToUs(long j) {
            long constrainSeekPosition = constrainSeekPosition(j);
            for (int i = 0; i < this.sampleStreams.size(); i++) {
                ((SilenceSampleStream) this.sampleStreams.get(i)).seekTo(constrainSeekPosition);
            }
            return constrainSeekPosition;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
            return constrainSeekPosition(j);
        }

        private long constrainSeekPosition(long j) {
            return Util.constrainValue(j, 0L, this.durationUs);
        }
    }

    private static final class SilenceSampleStream implements SampleStream {
        private final long durationBytes;
        private long positionBytes;
        private boolean sentFormat;

        @Override // com.google.android.exoplayer2.source.SampleStream
        public boolean isReady() {
            return true;
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public void maybeThrowError() {
        }

        public SilenceSampleStream(long j) {
            this.durationBytes = SilenceMediaSource.getAudioByteCount(j);
            seekTo(0L);
        }

        public void seekTo(long j) {
            this.positionBytes = Util.constrainValue(SilenceMediaSource.getAudioByteCount(j), 0L, this.durationBytes);
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
            if (!this.sentFormat || z) {
                formatHolder.format = SilenceMediaSource.FORMAT;
                this.sentFormat = true;
                return -5;
            }
            long j = this.durationBytes - this.positionBytes;
            if (j != 0) {
                int min = (int) Math.min(SilenceMediaSource.SILENCE_SAMPLE.length, j);
                decoderInputBuffer.ensureSpaceForWrite(min);
                decoderInputBuffer.data.put(SilenceMediaSource.SILENCE_SAMPLE, 0, min);
                decoderInputBuffer.timeUs = SilenceMediaSource.getAudioPositionUs(this.positionBytes);
                decoderInputBuffer.addFlag(1);
                this.positionBytes += min;
                return -4;
            }
            decoderInputBuffer.addFlag(4);
            return -4;
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int skipData(long j) {
            long j2 = this.positionBytes;
            seekTo(j);
            return (int) ((this.positionBytes - j2) / SilenceMediaSource.SILENCE_SAMPLE.length);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long getAudioByteCount(long j) {
        return Util.getPcmFrameSize(2, 2) * ((j * 44100) / 1000000);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long getAudioPositionUs(long j) {
        return ((j / Util.getPcmFrameSize(2, 2)) * 1000000) / 44100;
    }
}