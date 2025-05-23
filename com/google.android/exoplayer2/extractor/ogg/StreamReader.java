package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

/* loaded from: classes.dex */
abstract class StreamReader {
    private static final int STATE_END_OF_INPUT = 3;
    private static final int STATE_READ_HEADERS = 0;
    private static final int STATE_READ_PAYLOAD = 2;
    private static final int STATE_SKIP_HEADERS = 1;
    private long currentGranule;
    private ExtractorOutput extractorOutput;
    private boolean formatSet;
    private long lengthOfReadPacket;
    private final OggPacket oggPacket = new OggPacket();
    private OggSeeker oggSeeker;
    private long payloadStartPosition;
    private int sampleRate;
    private boolean seekMapSet;
    private SetupData setupData;
    private int state;
    private long targetGranule;
    private TrackOutput trackOutput;

    protected abstract long preparePayload(ParsableByteArray parsableByteArray);

    protected abstract boolean readHeaders(ParsableByteArray parsableByteArray, long j, SetupData setupData) throws IOException, InterruptedException;

    static class SetupData {
        Format format;
        OggSeeker oggSeeker;

        SetupData() {
        }
    }

    void init(ExtractorOutput extractorOutput, TrackOutput trackOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = trackOutput;
        reset(true);
    }

    protected void reset(boolean z) {
        if (z) {
            this.setupData = new SetupData();
            this.payloadStartPosition = 0L;
            this.state = 0;
        } else {
            this.state = 1;
        }
        this.targetGranule = -1L;
        this.currentGranule = 0L;
    }

    final void seek(long j, long j2) {
        this.oggPacket.reset();
        if (j == 0) {
            reset(!this.seekMapSet);
        } else if (this.state != 0) {
            long convertTimeToGranule = convertTimeToGranule(j2);
            this.targetGranule = convertTimeToGranule;
            this.oggSeeker.startSeek(convertTimeToGranule);
            this.state = 2;
        }
    }

    final int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        int i = this.state;
        if (i == 0) {
            return readHeaders(extractorInput);
        }
        if (i != 1) {
            if (i == 2) {
                return readPayload(extractorInput, positionHolder);
            }
            throw new IllegalStateException();
        }
        extractorInput.skipFully((int) this.payloadStartPosition);
        this.state = 2;
        return 0;
    }

    private int readHeaders(ExtractorInput extractorInput) throws IOException, InterruptedException {
        boolean z = true;
        while (z) {
            if (!this.oggPacket.populate(extractorInput)) {
                this.state = 3;
                return -1;
            }
            this.lengthOfReadPacket = extractorInput.getPosition() - this.payloadStartPosition;
            z = readHeaders(this.oggPacket.getPayload(), this.payloadStartPosition, this.setupData);
            if (z) {
                this.payloadStartPosition = extractorInput.getPosition();
            }
        }
        this.sampleRate = this.setupData.format.sampleRate;
        if (!this.formatSet) {
            this.trackOutput.format(this.setupData.format);
            this.formatSet = true;
        }
        if (this.setupData.oggSeeker != null) {
            this.oggSeeker = this.setupData.oggSeeker;
        } else if (extractorInput.getLength() == -1) {
            this.oggSeeker = new UnseekableOggSeeker();
        } else {
            OggPageHeader pageHeader = this.oggPacket.getPageHeader();
            this.oggSeeker = new DefaultOggSeeker(this, this.payloadStartPosition, extractorInput.getLength(), pageHeader.headerSize + pageHeader.bodySize, pageHeader.granulePosition, (pageHeader.type & 4) != 0);
        }
        this.setupData = null;
        this.state = 2;
        this.oggPacket.trimPayload();
        return 0;
    }

    private int readPayload(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        long read = this.oggSeeker.read(extractorInput);
        if (read >= 0) {
            positionHolder.position = read;
            return 1;
        }
        if (read < -1) {
            onSeekEnd(-(read + 2));
        }
        if (!this.seekMapSet) {
            this.extractorOutput.seekMap(this.oggSeeker.createSeekMap());
            this.seekMapSet = true;
        }
        if (this.lengthOfReadPacket > 0 || this.oggPacket.populate(extractorInput)) {
            this.lengthOfReadPacket = 0L;
            ParsableByteArray payload = this.oggPacket.getPayload();
            long preparePayload = preparePayload(payload);
            if (preparePayload >= 0) {
                long j = this.currentGranule;
                if (j + preparePayload >= this.targetGranule) {
                    long convertGranuleToTime = convertGranuleToTime(j);
                    this.trackOutput.sampleData(payload, payload.limit());
                    this.trackOutput.sampleMetadata(convertGranuleToTime, 1, payload.limit(), 0, null);
                    this.targetGranule = -1L;
                }
            }
            this.currentGranule += preparePayload;
            return 0;
        }
        this.state = 3;
        return -1;
    }

    protected long convertGranuleToTime(long j) {
        return (j * 1000000) / this.sampleRate;
    }

    protected long convertTimeToGranule(long j) {
        return (this.sampleRate * j) / 1000000;
    }

    protected void onSeekEnd(long j) {
        this.currentGranule = j;
    }

    private static final class UnseekableOggSeeker implements OggSeeker {
        @Override // com.google.android.exoplayer2.extractor.ogg.OggSeeker
        public long read(ExtractorInput extractorInput) {
            return -1L;
        }

        @Override // com.google.android.exoplayer2.extractor.ogg.OggSeeker
        public void startSeek(long j) {
        }

        private UnseekableOggSeeker() {
        }

        @Override // com.google.android.exoplayer2.extractor.ogg.OggSeeker
        public SeekMap createSeekMap() {
            return new SeekMap.Unseekable(C.TIME_UNSET);
        }
    }
}