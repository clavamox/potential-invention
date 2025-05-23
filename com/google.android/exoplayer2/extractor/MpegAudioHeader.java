package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.util.MimeTypes;
import tv.danmaku.ijk.media.player.IjkMediaMeta;

/* loaded from: classes.dex */
public final class MpegAudioHeader {
    public static final int MAX_FRAME_SIZE_BYTES = 4096;
    private static final int SAMPLES_PER_FRAME_L1 = 384;
    private static final int SAMPLES_PER_FRAME_L2 = 1152;
    private static final int SAMPLES_PER_FRAME_L3_V1 = 1152;
    private static final int SAMPLES_PER_FRAME_L3_V2 = 576;
    public int bitrate;
    public int channels;
    public int frameSize;
    public String mimeType;
    public int sampleRate;
    public int samplesPerFrame;
    public int version;
    private static final String[] MIME_TYPE_BY_LAYER = {MimeTypes.AUDIO_MPEG_L1, MimeTypes.AUDIO_MPEG_L2, MimeTypes.AUDIO_MPEG};
    private static final int[] SAMPLING_RATE_V1 = {44100, 48000, 32000};
    private static final int[] BITRATE_V1_L1 = {32000, 64000, 96000, 128000, 160000, 192000, 224000, 256000, 288000, 320000, 352000, 384000, 416000, 448000};
    private static final int[] BITRATE_V2_L1 = {32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000, 176000, 192000, 224000, 256000};
    private static final int[] BITRATE_V1_L2 = {32000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, 384000};
    private static final int[] BITRATE_V1_L3 = {32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000};
    private static final int[] BITRATE_V2 = {8000, 16000, 24000, 32000, 40000, 48000, 56000, 64000, 80000, 96000, 112000, 128000, 144000, 160000};

    private static boolean isMagicPresent(int i) {
        return (i & (-2097152)) == -2097152;
    }

    public static int getFrameSize(int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        if (!isMagicPresent(i) || (i2 = (i >>> 19) & 3) == 1 || (i3 = (i >>> 17) & 3) == 0 || (i4 = (i >>> 12) & 15) == 0 || i4 == 15 || (i5 = (i >>> 10) & 3) == 3) {
            return -1;
        }
        int i7 = SAMPLING_RATE_V1[i5];
        if (i2 == 2) {
            i7 /= 2;
        } else if (i2 == 0) {
            i7 /= 4;
        }
        int i8 = (i >>> 9) & 1;
        if (i3 == 3) {
            return ((((i2 == 3 ? BITRATE_V1_L1[i4 - 1] : BITRATE_V2_L1[i4 - 1]) * 12) / i7) + i8) * 4;
        }
        if (i2 == 3) {
            i6 = i3 == 2 ? BITRATE_V1_L2[i4 - 1] : BITRATE_V1_L3[i4 - 1];
        } else {
            i6 = BITRATE_V2[i4 - 1];
        }
        int i9 = IjkMediaMeta.FF_PROFILE_H264_HIGH_444;
        if (i2 == 3) {
            return ((i6 * IjkMediaMeta.FF_PROFILE_H264_HIGH_444) / i7) + i8;
        }
        if (i3 == 1) {
            i9 = 72;
        }
        return ((i9 * i6) / i7) + i8;
    }

    public static int getFrameSampleCount(int i) {
        int i2;
        int i3;
        if (!isMagicPresent(i) || (i2 = (i >>> 19) & 3) == 1 || (i3 = (i >>> 17) & 3) == 0) {
            return -1;
        }
        int i4 = (i >>> 12) & 15;
        int i5 = (i >>> 10) & 3;
        if (i4 == 0 || i4 == 15 || i5 == 3) {
            return -1;
        }
        return getFrameSizeInSamples(i2, i3);
    }

    public static boolean populateHeader(int i, MpegAudioHeader mpegAudioHeader) {
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        if (!isMagicPresent(i) || (i2 = (i >>> 19) & 3) == 1 || (i3 = (i >>> 17) & 3) == 0 || (i4 = (i >>> 12) & 15) == 0 || i4 == 15 || (i5 = (i >>> 10) & 3) == 3) {
            return false;
        }
        int i9 = SAMPLING_RATE_V1[i5];
        if (i2 == 2) {
            i9 /= 2;
        } else if (i2 == 0) {
            i9 /= 4;
        }
        int i10 = i9;
        int i11 = (i >>> 9) & 1;
        int frameSizeInSamples = getFrameSizeInSamples(i2, i3);
        if (i3 == 3) {
            i6 = i2 == 3 ? BITRATE_V1_L1[i4 - 1] : BITRATE_V2_L1[i4 - 1];
            i8 = (((i6 * 12) / i10) + i11) * 4;
        } else {
            if (i2 == 3) {
                i6 = i3 == 2 ? BITRATE_V1_L2[i4 - 1] : BITRATE_V1_L3[i4 - 1];
                i7 = (i6 * IjkMediaMeta.FF_PROFILE_H264_HIGH_444) / i10;
            } else {
                i6 = BITRATE_V2[i4 - 1];
                i7 = ((i3 == 1 ? 72 : IjkMediaMeta.FF_PROFILE_H264_HIGH_444) * i6) / i10;
            }
            i8 = i7 + i11;
        }
        mpegAudioHeader.setValues(i2, MIME_TYPE_BY_LAYER[3 - i3], i8, i10, ((i >> 6) & 3) == 3 ? 1 : 2, i6, frameSizeInSamples);
        return true;
    }

    private static int getFrameSizeInSamples(int i, int i2) {
        if (i2 == 1) {
            if (i == 3) {
                return 1152;
            }
            return SAMPLES_PER_FRAME_L3_V2;
        }
        if (i2 == 2) {
            return 1152;
        }
        if (i2 == 3) {
            return SAMPLES_PER_FRAME_L1;
        }
        throw new IllegalArgumentException();
    }

    private void setValues(int i, String str, int i2, int i3, int i4, int i5, int i6) {
        this.version = i;
        this.mimeType = str;
        this.frameSize = i2;
        this.sampleRate = i3;
        this.channels = i4;
        this.bitrate = i5;
        this.samplesPerFrame = i6;
    }
}