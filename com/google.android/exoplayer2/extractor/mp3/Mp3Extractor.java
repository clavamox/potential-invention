package com.google.android.exoplayer2.extractor.mp3;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.extractor.Id3Peeker;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.mp3.Seeker;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder;
import com.google.android.exoplayer2.metadata.id3.MlltFrame;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.EOFException;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes.dex */
public final class Mp3Extractor implements Extractor {
    public static final int FLAG_DISABLE_ID3_METADATA = 2;
    public static final int FLAG_ENABLE_CONSTANT_BITRATE_SEEKING = 1;
    private static final int MAX_SNIFF_BYTES = 16384;
    private static final int MAX_SYNC_BYTES = 131072;
    private static final int MPEG_AUDIO_HEADER_MASK = -128000;
    private static final int SCRATCH_LENGTH = 10;
    private static final int SEEK_HEADER_INFO = 1231971951;
    private static final int SEEK_HEADER_UNSET = 0;
    private static final int SEEK_HEADER_VBRI = 1447187017;
    private static final int SEEK_HEADER_XING = 1483304551;
    private long basisTimeUs;
    private boolean disableSeeking;
    private ExtractorOutput extractorOutput;
    private long firstSamplePosition;
    private final int flags;
    private final long forcedFirstSampleTimestampUs;
    private final GaplessInfoHolder gaplessInfoHolder;
    private final Id3Peeker id3Peeker;
    private Metadata metadata;
    private int sampleBytesRemaining;
    private long samplesRead;
    private final ParsableByteArray scratch;
    private Seeker seeker;
    private final MpegAudioHeader synchronizedHeader;
    private int synchronizedHeaderData;
    private TrackOutput trackOutput;
    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() { // from class: com.google.android.exoplayer2.extractor.mp3.Mp3Extractor$$ExternalSyntheticLambda0
        @Override // com.google.android.exoplayer2.extractor.ExtractorsFactory
        public final Extractor[] createExtractors() {
            return Mp3Extractor.lambda$static$0();
        }
    };
    private static final Id3Decoder.FramePredicate REQUIRED_ID3_FRAME_PREDICATE = new Id3Decoder.FramePredicate() { // from class: com.google.android.exoplayer2.extractor.mp3.Mp3Extractor$$ExternalSyntheticLambda1
        @Override // com.google.android.exoplayer2.metadata.id3.Id3Decoder.FramePredicate
        public final boolean evaluate(int i, int i2, int i3, int i4, int i5) {
            return Mp3Extractor.lambda$static$1(i, i2, i3, i4, i5);
        }
    };

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    private static boolean headersMatch(int i, long j) {
        return ((long) (i & MPEG_AUDIO_HEADER_MASK)) == (j & (-128000));
    }

    static /* synthetic */ boolean lambda$static$1(int i, int i2, int i3, int i4, int i5) {
        return (i2 == 67 && i3 == 79 && i4 == 77 && (i5 == 77 || i == 2)) || (i2 == 77 && i3 == 76 && i4 == 76 && (i5 == 84 || i == 2));
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void release() {
    }

    static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new Mp3Extractor()};
    }

    public Mp3Extractor() {
        this(0);
    }

    public Mp3Extractor(int i) {
        this(i, C.TIME_UNSET);
    }

    public Mp3Extractor(int i, long j) {
        this.flags = i;
        this.forcedFirstSampleTimestampUs = j;
        this.scratch = new ParsableByteArray(10);
        this.synchronizedHeader = new MpegAudioHeader();
        this.gaplessInfoHolder = new GaplessInfoHolder();
        this.basisTimeUs = C.TIME_UNSET;
        this.id3Peeker = new Id3Peeker();
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        return synchronize(extractorInput, true);
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = extractorOutput.track(0, 1);
        this.extractorOutput.endTracks();
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void seek(long j, long j2) {
        this.synchronizedHeaderData = 0;
        this.basisTimeUs = C.TIME_UNSET;
        this.samplesRead = 0L;
        this.sampleBytesRemaining = 0;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        if (this.synchronizedHeaderData == 0) {
            try {
                synchronize(extractorInput, false);
            } catch (EOFException unused) {
                return -1;
            }
        }
        if (this.seeker == null) {
            Seeker maybeReadSeekFrame = maybeReadSeekFrame(extractorInput);
            MlltSeeker maybeHandleSeekMetadata = maybeHandleSeekMetadata(this.metadata, extractorInput.getPosition());
            if (this.disableSeeking) {
                this.seeker = new Seeker.UnseekableSeeker();
            } else {
                if (maybeHandleSeekMetadata != null) {
                    this.seeker = maybeHandleSeekMetadata;
                } else if (maybeReadSeekFrame != null) {
                    this.seeker = maybeReadSeekFrame;
                }
                Seeker seeker = this.seeker;
                if (seeker == null || (!seeker.isSeekable() && (this.flags & 1) != 0)) {
                    this.seeker = getConstantBitrateSeeker(extractorInput);
                }
            }
            this.extractorOutput.seekMap(this.seeker);
            this.trackOutput.format(Format.createAudioSampleFormat(null, this.synchronizedHeader.mimeType, null, -1, 4096, this.synchronizedHeader.channels, this.synchronizedHeader.sampleRate, -1, this.gaplessInfoHolder.encoderDelay, this.gaplessInfoHolder.encoderPadding, null, null, 0, null, (this.flags & 2) != 0 ? null : this.metadata));
            this.firstSamplePosition = extractorInput.getPosition();
        } else if (this.firstSamplePosition != 0) {
            long position = extractorInput.getPosition();
            long j = this.firstSamplePosition;
            if (position < j) {
                extractorInput.skipFully((int) (j - position));
            }
        }
        return readSample(extractorInput);
    }

    public void disableSeeking() {
        this.disableSeeking = true;
    }

    private int readSample(ExtractorInput extractorInput) throws IOException, InterruptedException {
        if (this.sampleBytesRemaining == 0) {
            extractorInput.resetPeekPosition();
            if (peekEndOfStreamOrHeader(extractorInput)) {
                return -1;
            }
            this.scratch.setPosition(0);
            int readInt = this.scratch.readInt();
            if (!headersMatch(readInt, this.synchronizedHeaderData) || MpegAudioHeader.getFrameSize(readInt) == -1) {
                extractorInput.skipFully(1);
                this.synchronizedHeaderData = 0;
                return 0;
            }
            MpegAudioHeader.populateHeader(readInt, this.synchronizedHeader);
            if (this.basisTimeUs == C.TIME_UNSET) {
                this.basisTimeUs = this.seeker.getTimeUs(extractorInput.getPosition());
                if (this.forcedFirstSampleTimestampUs != C.TIME_UNSET) {
                    this.basisTimeUs += this.forcedFirstSampleTimestampUs - this.seeker.getTimeUs(0L);
                }
            }
            this.sampleBytesRemaining = this.synchronizedHeader.frameSize;
        }
        int sampleData = this.trackOutput.sampleData(extractorInput, this.sampleBytesRemaining, true);
        if (sampleData == -1) {
            return -1;
        }
        int i = this.sampleBytesRemaining - sampleData;
        this.sampleBytesRemaining = i;
        if (i > 0) {
            return 0;
        }
        this.trackOutput.sampleMetadata(this.basisTimeUs + ((this.samplesRead * 1000000) / this.synchronizedHeader.sampleRate), 1, this.synchronizedHeader.frameSize, 0, null);
        this.samplesRead += this.synchronizedHeader.samplesPerFrame;
        this.sampleBytesRemaining = 0;
        return 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:50:0x009d, code lost:
    
        if (r12 == false) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x009f, code lost:
    
        r11.skipFully(r1 + r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00a7, code lost:
    
        r10.synchronizedHeaderData = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00a9, code lost:
    
        return true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00a4, code lost:
    
        r11.resetPeekPosition();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean synchronize(com.google.android.exoplayer2.extractor.ExtractorInput r11, boolean r12) throws java.io.IOException, java.lang.InterruptedException {
        /*
            r10 = this;
            if (r12 == 0) goto L5
            r0 = 16384(0x4000, float:2.2959E-41)
            goto L7
        L5:
            r0 = 131072(0x20000, float:1.83671E-40)
        L7:
            r11.resetPeekPosition()
            long r1 = r11.getPosition()
            r3 = 0
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            r2 = 1
            r3 = 0
            if (r1 != 0) goto L40
            int r1 = r10.flags
            r1 = r1 & 2
            if (r1 != 0) goto L1e
            r1 = r2
            goto L1f
        L1e:
            r1 = r3
        L1f:
            if (r1 == 0) goto L23
            r1 = 0
            goto L25
        L23:
            com.google.android.exoplayer2.metadata.id3.Id3Decoder$FramePredicate r1 = com.google.android.exoplayer2.extractor.mp3.Mp3Extractor.REQUIRED_ID3_FRAME_PREDICATE
        L25:
            com.google.android.exoplayer2.extractor.Id3Peeker r4 = r10.id3Peeker
            com.google.android.exoplayer2.metadata.Metadata r1 = r4.peekId3Data(r11, r1)
            r10.metadata = r1
            if (r1 == 0) goto L34
            com.google.android.exoplayer2.extractor.GaplessInfoHolder r4 = r10.gaplessInfoHolder
            r4.setFromMetadata(r1)
        L34:
            long r4 = r11.getPeekPosition()
            int r1 = (int) r4
            if (r12 != 0) goto L3e
            r11.skipFully(r1)
        L3e:
            r4 = r3
            goto L42
        L40:
            r1 = r3
            r4 = r1
        L42:
            r5 = r4
            r6 = r5
        L44:
            boolean r7 = r10.peekEndOfStreamOrHeader(r11)
            if (r7 == 0) goto L53
            if (r5 <= 0) goto L4d
            goto L9d
        L4d:
            java.io.EOFException r11 = new java.io.EOFException
            r11.<init>()
            throw r11
        L53:
            com.google.android.exoplayer2.util.ParsableByteArray r7 = r10.scratch
            r7.setPosition(r3)
            com.google.android.exoplayer2.util.ParsableByteArray r7 = r10.scratch
            int r7 = r7.readInt()
            if (r4 == 0) goto L67
            long r8 = (long) r4
            boolean r8 = headersMatch(r7, r8)
            if (r8 == 0) goto L6e
        L67:
            int r8 = com.google.android.exoplayer2.extractor.MpegAudioHeader.getFrameSize(r7)
            r9 = -1
            if (r8 != r9) goto L8f
        L6e:
            int r4 = r6 + 1
            if (r6 != r0) goto L7d
            if (r12 == 0) goto L75
            return r3
        L75:
            com.google.android.exoplayer2.ParserException r11 = new com.google.android.exoplayer2.ParserException
            java.lang.String r12 = "Searched too many bytes."
            r11.<init>(r12)
            throw r11
        L7d:
            if (r12 == 0) goto L88
            r11.resetPeekPosition()
            int r5 = r1 + r4
            r11.advancePeekPosition(r5)
            goto L8b
        L88:
            r11.skipFully(r2)
        L8b:
            r5 = r3
            r6 = r4
            r4 = r5
            goto L44
        L8f:
            int r5 = r5 + 1
            if (r5 != r2) goto L9a
            com.google.android.exoplayer2.extractor.MpegAudioHeader r4 = r10.synchronizedHeader
            com.google.android.exoplayer2.extractor.MpegAudioHeader.populateHeader(r7, r4)
            r4 = r7
            goto Laa
        L9a:
            r7 = 4
            if (r5 != r7) goto Laa
        L9d:
            if (r12 == 0) goto La4
            int r1 = r1 + r6
            r11.skipFully(r1)
            goto La7
        La4:
            r11.resetPeekPosition()
        La7:
            r10.synchronizedHeaderData = r4
            return r2
        Laa:
            int r8 = r8 + (-4)
            r11.advancePeekPosition(r8)
            goto L44
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp3.Mp3Extractor.synchronize(com.google.android.exoplayer2.extractor.ExtractorInput, boolean):boolean");
    }

    private boolean peekEndOfStreamOrHeader(ExtractorInput extractorInput) throws IOException, InterruptedException {
        Seeker seeker = this.seeker;
        if (seeker != null) {
            long dataEndPosition = seeker.getDataEndPosition();
            if (dataEndPosition != -1 && extractorInput.getPeekPosition() > dataEndPosition - 4) {
                return true;
            }
        }
        try {
            return !extractorInput.peekFully(this.scratch.data, 0, 4, true);
        } catch (EOFException unused) {
            return true;
        }
    }

    private Seeker maybeReadSeekFrame(ExtractorInput extractorInput) throws IOException, InterruptedException {
        ParsableByteArray parsableByteArray = new ParsableByteArray(this.synchronizedHeader.frameSize);
        extractorInput.peekFully(parsableByteArray.data, 0, this.synchronizedHeader.frameSize);
        int i = 21;
        if ((this.synchronizedHeader.version & 1) != 0) {
            if (this.synchronizedHeader.channels != 1) {
                i = 36;
            }
        } else if (this.synchronizedHeader.channels == 1) {
            i = 13;
        }
        int i2 = i;
        int seekFrameHeader = getSeekFrameHeader(parsableByteArray, i2);
        if (seekFrameHeader != SEEK_HEADER_XING && seekFrameHeader != SEEK_HEADER_INFO) {
            if (seekFrameHeader == SEEK_HEADER_VBRI) {
                VbriSeeker create = VbriSeeker.create(extractorInput.getLength(), extractorInput.getPosition(), this.synchronizedHeader, parsableByteArray);
                extractorInput.skipFully(this.synchronizedHeader.frameSize);
                return create;
            }
            extractorInput.resetPeekPosition();
            return null;
        }
        XingSeeker create2 = XingSeeker.create(extractorInput.getLength(), extractorInput.getPosition(), this.synchronizedHeader, parsableByteArray);
        if (create2 != null && !this.gaplessInfoHolder.hasGaplessInfo()) {
            extractorInput.resetPeekPosition();
            extractorInput.advancePeekPosition(i2 + 141);
            extractorInput.peekFully(this.scratch.data, 0, 3);
            this.scratch.setPosition(0);
            this.gaplessInfoHolder.setFromXingHeaderValue(this.scratch.readUnsignedInt24());
        }
        extractorInput.skipFully(this.synchronizedHeader.frameSize);
        return (create2 == null || create2.isSeekable() || seekFrameHeader != SEEK_HEADER_INFO) ? create2 : getConstantBitrateSeeker(extractorInput);
    }

    private Seeker getConstantBitrateSeeker(ExtractorInput extractorInput) throws IOException, InterruptedException {
        extractorInput.peekFully(this.scratch.data, 0, 4);
        this.scratch.setPosition(0);
        MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
        return new ConstantBitrateSeeker(extractorInput.getLength(), extractorInput.getPosition(), this.synchronizedHeader);
    }

    private static int getSeekFrameHeader(ParsableByteArray parsableByteArray, int i) {
        if (parsableByteArray.limit() >= i + 4) {
            parsableByteArray.setPosition(i);
            int readInt = parsableByteArray.readInt();
            if (readInt == SEEK_HEADER_XING || readInt == SEEK_HEADER_INFO) {
                return readInt;
            }
        }
        if (parsableByteArray.limit() < 40) {
            return 0;
        }
        parsableByteArray.setPosition(36);
        if (parsableByteArray.readInt() == SEEK_HEADER_VBRI) {
            return SEEK_HEADER_VBRI;
        }
        return 0;
    }

    private static MlltSeeker maybeHandleSeekMetadata(Metadata metadata, long j) {
        if (metadata == null) {
            return null;
        }
        int length = metadata.length();
        for (int i = 0; i < length; i++) {
            Metadata.Entry entry = metadata.get(i);
            if (entry instanceof MlltFrame) {
                return MlltSeeker.create(j, (MlltFrame) entry);
            }
        }
        return null;
    }
}