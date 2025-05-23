package com.google.android.exoplayer2.extractor.ts;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class H262Reader implements ElementaryStreamReader {
    private static final double[] FRAME_RATE_VALUES = {23.976023976023978d, 24.0d, 25.0d, 29.97002997002997d, 30.0d, 50.0d, 59.94005994005994d, 60.0d};
    private static final int START_EXTENSION = 181;
    private static final int START_GROUP = 184;
    private static final int START_PICTURE = 0;
    private static final int START_SEQUENCE_HEADER = 179;
    private static final int START_USER_DATA = 178;
    private final CsdBuffer csdBuffer;
    private String formatId;
    private long frameDurationUs;
    private boolean hasOutputFormat;
    private TrackOutput output;
    private long pesTimeUs;
    private final boolean[] prefixFlags;
    private boolean sampleHasPicture;
    private boolean sampleIsKeyframe;
    private long samplePosition;
    private long sampleTimeUs;
    private boolean startedFirstSample;
    private long totalBytesWritten;
    private final NalUnitTargetBuffer userData;
    private final ParsableByteArray userDataParsable;
    private final UserDataReader userDataReader;

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetFinished() {
    }

    public H262Reader() {
        this(null);
    }

    H262Reader(UserDataReader userDataReader) {
        this.userDataReader = userDataReader;
        this.prefixFlags = new boolean[4];
        this.csdBuffer = new CsdBuffer(128);
        if (userDataReader != null) {
            this.userData = new NalUnitTargetBuffer(START_USER_DATA, 128);
            this.userDataParsable = new ParsableByteArray();
        } else {
            this.userData = null;
            this.userDataParsable = null;
        }
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.csdBuffer.reset();
        if (this.userDataReader != null) {
            this.userData.reset();
        }
        this.totalBytesWritten = 0L;
        this.startedFirstSample = false;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void createTracks(ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator trackIdGenerator) {
        trackIdGenerator.generateNewId();
        this.formatId = trackIdGenerator.getFormatId();
        this.output = extractorOutput.track(trackIdGenerator.getTrackId(), 2);
        UserDataReader userDataReader = this.userDataReader;
        if (userDataReader != null) {
            userDataReader.createTracks(extractorOutput, trackIdGenerator);
        }
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void packetStarted(long j, int i) {
        this.pesTimeUs = j;
    }

    @Override // com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader
    public void consume(ParsableByteArray parsableByteArray) {
        int i;
        int position = parsableByteArray.getPosition();
        int limit = parsableByteArray.limit();
        byte[] bArr = parsableByteArray.data;
        this.totalBytesWritten += parsableByteArray.bytesLeft();
        this.output.sampleData(parsableByteArray, parsableByteArray.bytesLeft());
        while (true) {
            int findNalUnit = NalUnitUtil.findNalUnit(bArr, position, limit, this.prefixFlags);
            if (findNalUnit == limit) {
                break;
            }
            int i2 = findNalUnit + 3;
            int i3 = parsableByteArray.data[i2] & 255;
            int i4 = findNalUnit - position;
            if (!this.hasOutputFormat) {
                if (i4 > 0) {
                    this.csdBuffer.onData(bArr, position, findNalUnit);
                }
                if (this.csdBuffer.onStartCode(i3, i4 < 0 ? -i4 : 0)) {
                    Pair<Format, Long> parseCsdBuffer = parseCsdBuffer(this.csdBuffer, this.formatId);
                    this.output.format((Format) parseCsdBuffer.first);
                    this.frameDurationUs = ((Long) parseCsdBuffer.second).longValue();
                    this.hasOutputFormat = true;
                }
            }
            if (this.userDataReader != null) {
                if (i4 > 0) {
                    this.userData.appendToNalUnit(bArr, position, findNalUnit);
                    i = 0;
                } else {
                    i = -i4;
                }
                if (this.userData.endNalUnit(i)) {
                    this.userDataParsable.reset(this.userData.nalData, NalUnitUtil.unescapeStream(this.userData.nalData, this.userData.nalLength));
                    this.userDataReader.consume(this.sampleTimeUs, this.userDataParsable);
                }
                if (i3 == START_USER_DATA && parsableByteArray.data[findNalUnit + 2] == 1) {
                    this.userData.startNalUnit(i3);
                }
            }
            if (i3 == 0 || i3 == START_SEQUENCE_HEADER) {
                int i5 = limit - findNalUnit;
                if (this.startedFirstSample && this.sampleHasPicture && this.hasOutputFormat) {
                    this.output.sampleMetadata(this.sampleTimeUs, this.sampleIsKeyframe ? 1 : 0, ((int) (this.totalBytesWritten - this.samplePosition)) - i5, i5, null);
                }
                boolean z = this.startedFirstSample;
                if (!z || this.sampleHasPicture) {
                    this.samplePosition = this.totalBytesWritten - i5;
                    long j = this.pesTimeUs;
                    if (j == C.TIME_UNSET) {
                        j = z ? this.sampleTimeUs + this.frameDurationUs : 0L;
                    }
                    this.sampleTimeUs = j;
                    this.sampleIsKeyframe = false;
                    this.pesTimeUs = C.TIME_UNSET;
                    this.startedFirstSample = true;
                }
                this.sampleHasPicture = i3 == 0;
            } else if (i3 == START_GROUP) {
                this.sampleIsKeyframe = true;
            }
            position = i2;
        }
        if (!this.hasOutputFormat) {
            this.csdBuffer.onData(bArr, position, limit);
        }
        if (this.userDataReader != null) {
            this.userData.appendToNalUnit(bArr, position, limit);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0068  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static android.util.Pair<com.google.android.exoplayer2.Format, java.lang.Long> parseCsdBuffer(com.google.android.exoplayer2.extractor.ts.H262Reader.CsdBuffer r20, java.lang.String r21) {
        /*
            r0 = r20
            byte[] r1 = r0.data
            int r2 = r0.length
            byte[] r1 = java.util.Arrays.copyOf(r1, r2)
            r2 = 4
            r3 = r1[r2]
            r3 = r3 & 255(0xff, float:3.57E-43)
            r4 = 5
            r5 = r1[r4]
            r5 = r5 & 255(0xff, float:3.57E-43)
            r6 = 6
            r6 = r1[r6]
            r6 = r6 & 255(0xff, float:3.57E-43)
            int r3 = r3 << r2
            int r7 = r5 >> 4
            r13 = r3 | r7
            r3 = r5 & 15
            int r3 = r3 << 8
            r14 = r3 | r6
            r3 = 7
            r5 = r1[r3]
            r5 = r5 & 240(0xf0, float:3.36E-43)
            int r5 = r5 >> r2
            r6 = 2
            if (r5 == r6) goto L43
            r6 = 3
            if (r5 == r6) goto L3d
            if (r5 == r2) goto L37
            r2 = 1065353216(0x3f800000, float:1.0)
        L34:
            r18 = r2
            goto L4b
        L37:
            int r2 = r14 * 121
            float r2 = (float) r2
            int r5 = r13 * 100
            goto L48
        L3d:
            int r2 = r14 * 16
            float r2 = (float) r2
            int r5 = r13 * 9
            goto L48
        L43:
            int r2 = r14 * 4
            float r2 = (float) r2
            int r5 = r13 * 3
        L48:
            float r5 = (float) r5
            float r2 = r2 / r5
            goto L34
        L4b:
            java.lang.String r9 = "video/mpeg2"
            r10 = 0
            r11 = -1
            r12 = -1
            r15 = -1082130432(0xffffffffbf800000, float:-1.0)
            java.util.List r16 = java.util.Collections.singletonList(r1)
            r17 = -1
            r19 = 0
            r8 = r21
            com.google.android.exoplayer2.Format r2 = com.google.android.exoplayer2.Format.createVideoSampleFormat(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r3 = r1[r3]
            r3 = r3 & 15
            int r3 = r3 + (-1)
            if (r3 < 0) goto L8d
            double[] r5 = com.google.android.exoplayer2.extractor.ts.H262Reader.FRAME_RATE_VALUES
            int r6 = r5.length
            if (r3 >= r6) goto L8d
            r6 = r5[r3]
            int r0 = r0.sequenceExtensionPosition
            int r0 = r0 + 9
            r0 = r1[r0]
            r1 = r0 & 96
            int r1 = r1 >> r4
            r0 = r0 & 31
            if (r1 == r0) goto L85
            double r3 = (double) r1
            r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r3 = r3 + r8
            int r0 = r0 + 1
            double r0 = (double) r0
            double r3 = r3 / r0
            double r6 = r6 * r3
        L85:
            r0 = 4696837146684686336(0x412e848000000000, double:1000000.0)
            double r0 = r0 / r6
            long r0 = (long) r0
            goto L8f
        L8d:
            r0 = 0
        L8f:
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            android.util.Pair r0 = android.util.Pair.create(r2, r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.H262Reader.parseCsdBuffer(com.google.android.exoplayer2.extractor.ts.H262Reader$CsdBuffer, java.lang.String):android.util.Pair");
    }

    private static final class CsdBuffer {
        private static final byte[] START_CODE = {0, 0, 1};
        public byte[] data;
        private boolean isFilling;
        public int length;
        public int sequenceExtensionPosition;

        public CsdBuffer(int i) {
            this.data = new byte[i];
        }

        public void reset() {
            this.isFilling = false;
            this.length = 0;
            this.sequenceExtensionPosition = 0;
        }

        public boolean onStartCode(int i, int i2) {
            if (this.isFilling) {
                int i3 = this.length - i2;
                this.length = i3;
                if (this.sequenceExtensionPosition == 0 && i == H262Reader.START_EXTENSION) {
                    this.sequenceExtensionPosition = i3;
                } else {
                    this.isFilling = false;
                    return true;
                }
            } else if (i == H262Reader.START_SEQUENCE_HEADER) {
                this.isFilling = true;
            }
            byte[] bArr = START_CODE;
            onData(bArr, 0, bArr.length);
            return false;
        }

        public void onData(byte[] bArr, int i, int i2) {
            if (this.isFilling) {
                int i3 = i2 - i;
                byte[] bArr2 = this.data;
                int length = bArr2.length;
                int i4 = this.length;
                if (length < i4 + i3) {
                    this.data = Arrays.copyOf(bArr2, (i4 + i3) * 2);
                }
                System.arraycopy(bArr, i, this.data, this.length, i3);
                this.length += i3;
            }
        }
    }
}