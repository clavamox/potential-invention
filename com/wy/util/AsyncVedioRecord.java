package com.wy.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import tv.danmaku.ijk.media.player.IjkMediaMeta;

/* loaded from: classes.dex */
public class AsyncVedioRecord {
    private static final String TAG = "VedioRecord";
    private boolean isRunning;
    private boolean mMuxerStarted;
    private int mTrackIndex;
    private MediaCodec mediaCodec;
    private MediaMuxer mediaMuxer;
    RecordListener recordListener;
    Thread thread;
    public boolean isEnd = false;
    private int width = 640;
    private int height = 480;
    private int fps = 15;
    Queue<Bitmap> queue = new ConcurrentLinkedQueue();

    public interface RecordListener {
        void onError(Exception exc);

        void onFinished();
    }

    public void start(final String str, final String str2, final int i, final int i2, RecordListener recordListener) {
        this.recordListener = recordListener;
        new Thread(new Runnable() { // from class: com.wy.util.AsyncVedioRecord.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Log.d(AsyncVedioRecord.TAG, "out:" + str);
                    AsyncVedioRecord.this.mediaCodec = MediaCodec.createEncoderByType(MimeTypes.VIDEO_H264);
                    AsyncVedioRecord.this.mediaMuxer = new MediaMuxer(str, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AsyncVedioRecord.this.fps = (int) (i / (i2 * 1.0f));
                Log.d(AsyncVedioRecord.TAG, "fps:" + AsyncVedioRecord.this.fps);
                MediaFormat createVideoFormat = MediaFormat.createVideoFormat(MimeTypes.VIDEO_H264, AsyncVedioRecord.this.width, AsyncVedioRecord.this.height);
                createVideoFormat.setInteger("color-format", AsyncVedioRecord.this.getColorFormat());
                createVideoFormat.setInteger(IjkMediaMeta.IJKM_KEY_BITRATE, 3000000);
                createVideoFormat.setInteger("frame-rate", AsyncVedioRecord.this.fps);
                createVideoFormat.setInteger("i-frame-interval", 1);
                AsyncVedioRecord.this.mediaCodec.configure(createVideoFormat, (Surface) null, (MediaCrypto) null, 1);
                AsyncVedioRecord.this.mediaCodec.start();
                AsyncVedioRecord.this.queue.clear();
                AsyncVedioRecord.this.startEncode();
                new Thread(new Runnable() { // from class: com.wy.util.AsyncVedioRecord.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        for (int i3 = 0; i3 < i; i3++) {
                            try {
                                Bitmap decodeFile = BitmapFactory.decodeFile(str2 + File.separatorChar + "/Image-" + String.format("%05d", Integer.valueOf(i3)) + ".jpg");
                                if (decodeFile != null) {
                                    AsyncVedioRecord.this.putBitmap(decodeFile);
                                    Thread.sleep(5L);
                                } else {
                                    Log.d(AsyncVedioRecord.TAG, "bitmap is null");
                                }
                            } catch (Exception unused) {
                                return;
                            }
                        }
                        AsyncVedioRecord.this.isRunning = false;
                    }
                }).start();
            }
        }).start();
    }

    public void putBitmap(Bitmap bitmap) {
        this.queue.add(bitmap);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startEncode() {
        Thread thread = new Thread(new Runnable() { // from class: com.wy.util.AsyncVedioRecord.2
            @Override // java.lang.Runnable
            public void run() {
                AsyncVedioRecord.this.isRunning = true;
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                long j = 0;
                while (true) {
                    if (AsyncVedioRecord.this.isRunning || AsyncVedioRecord.this.queue.size() > 0) {
                        Bitmap poll = AsyncVedioRecord.this.queue.poll();
                        if (poll != null) {
                            int dequeueInputBuffer = AsyncVedioRecord.this.mediaCodec.dequeueInputBuffer(10000L);
                            if (dequeueInputBuffer >= 0) {
                                long computePresentationTime = AsyncVedioRecord.this.computePresentationTime(j);
                                if (AsyncVedioRecord.this.isEnd) {
                                    AsyncVedioRecord.this.mediaCodec.queueInputBuffer(dequeueInputBuffer, 0, 0, computePresentationTime, 4);
                                    AsyncVedioRecord.this.isRunning = false;
                                    AsyncVedioRecord.this.drainEncoder(true, bufferInfo);
                                } else {
                                    byte[] nv12 = AsyncVedioRecord.this.getNV12(View.MeasureSpec.getSize(poll.getWidth()), View.MeasureSpec.getSize(poll.getHeight()), poll);
                                    ByteBuffer inputBuffer = AsyncVedioRecord.this.mediaCodec.getInputBuffer(dequeueInputBuffer);
                                    inputBuffer.clear();
                                    inputBuffer.put(nv12);
                                    AsyncVedioRecord.this.mediaCodec.queueInputBuffer(dequeueInputBuffer, 0, nv12.length, computePresentationTime, 0);
                                    AsyncVedioRecord.this.drainEncoder(false, bufferInfo);
                                }
                                j++;
                            } else {
                                try {
                                    Thread.sleep(10L);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        AsyncVedioRecord asyncVedioRecord = AsyncVedioRecord.this;
                        asyncVedioRecord.stop(asyncVedioRecord.recordListener);
                        return;
                    }
                }
            }
        });
        this.thread = thread;
        thread.start();
    }

    public void stop(final RecordListener recordListener) {
        new Thread(new Runnable() { // from class: com.wy.util.AsyncVedioRecord.3
            @Override // java.lang.Runnable
            public void run() {
                AsyncVedioRecord.this.isRunning = false;
                try {
                    try {
                        Thread.sleep(100L);
                        if (AsyncVedioRecord.this.mediaCodec != null) {
                            AsyncVedioRecord.this.mediaCodec.stop();
                            AsyncVedioRecord.this.mediaCodec.release();
                            AsyncVedioRecord.this.mediaCodec = null;
                        }
                        if (AsyncVedioRecord.this.mediaMuxer != null) {
                            try {
                                if (AsyncVedioRecord.this.mMuxerStarted) {
                                    AsyncVedioRecord.this.mediaMuxer.stop();
                                    AsyncVedioRecord.this.mediaMuxer.release();
                                }
                                AsyncVedioRecord.this.mediaMuxer = null;
                                AsyncVedioRecord.this.mMuxerStarted = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        RecordListener recordListener2 = recordListener;
                        if (recordListener2 != null) {
                            recordListener2.onFinished();
                        }
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                        RecordListener recordListener3 = recordListener;
                        if (recordListener3 != null) {
                            recordListener3.onError(e2);
                        }
                    }
                } finally {
                    AsyncVedioRecord.this.queue.clear();
                }
            }
        }).start();
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void drainEncoder(boolean z, MediaCodec.BufferInfo bufferInfo) {
        if (z) {
            try {
                this.mediaCodec.signalEndOfInputStream();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        while (true) {
            int dequeueOutputBuffer = this.mediaCodec.dequeueOutputBuffer(bufferInfo, 10000L);
            if (dequeueOutputBuffer == -1) {
                if (!z) {
                    return;
                }
            } else if (dequeueOutputBuffer == -2) {
                if (this.mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                this.mTrackIndex = this.mediaMuxer.addTrack(this.mediaCodec.getOutputFormat());
                this.mediaMuxer.start();
                this.mMuxerStarted = true;
            } else if (dequeueOutputBuffer >= 0) {
                ByteBuffer outputBuffer = this.mediaCodec.getOutputBuffer(dequeueOutputBuffer);
                if (outputBuffer == null) {
                    throw new RuntimeException("encoderOutputBuffer " + dequeueOutputBuffer + " was null");
                }
                if ((bufferInfo.flags & 2) != 0) {
                    bufferInfo.size = 0;
                }
                if (bufferInfo.size != 0) {
                    if (!this.mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }
                    outputBuffer.position(bufferInfo.offset);
                    outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                    try {
                        this.mediaMuxer.writeSampleData(this.mTrackIndex, outputBuffer, bufferInfo);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                this.mediaCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                if ((bufferInfo.flags & 4) != 0) {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long computePresentationTime(long j) {
        return ((j * 1000000) / this.fps) + 132;
    }

    public int[] getMediaCodecList() {
        int codecCount = MediaCodecList.getCodecCount();
        MediaCodecInfo mediaCodecInfo = null;
        for (int i = 0; i < codecCount && mediaCodecInfo == null; i++) {
            MediaCodecInfo codecInfoAt = MediaCodecList.getCodecInfoAt(i);
            if (codecInfoAt.isEncoder()) {
                String[] supportedTypes = codecInfoAt.getSupportedTypes();
                boolean z = false;
                for (int i2 = 0; i2 < supportedTypes.length && !z; i2++) {
                    if (supportedTypes[i2].equals(MimeTypes.VIDEO_H264)) {
                        z = true;
                    }
                }
                if (z) {
                    mediaCodecInfo = codecInfoAt;
                }
            }
        }
        return mediaCodecInfo.getCapabilitiesForType(MimeTypes.VIDEO_H264).colorFormats;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0018 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:12:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int getColorFormat() {
        /*
            r6 = this;
            int[] r0 = r6.getMediaCodecList()
            int r1 = r0.length
            r2 = 0
            r3 = r2
        L7:
            if (r3 >= r1) goto L16
            r4 = r0[r3]
            r5 = 39
            if (r4 == r5) goto L15
            switch(r4) {
                case 19: goto L15;
                case 20: goto L15;
                case 21: goto L15;
                default: goto L12;
            }
        L12:
            int r3 = r3 + 1
            goto L7
        L15:
            r2 = r4
        L16:
            if (r2 > 0) goto L1a
            r2 = 21
        L1a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.wy.util.AsyncVedioRecord.getColorFormat():int");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] getNV12(int i, int i2, Bitmap bitmap) {
        int i3 = i * i2;
        int[] iArr = new int[i3];
        bitmap.getPixels(iArr, 0, i, 0, 0, i, i2);
        byte[] bArr = new byte[(i3 * 3) / 2];
        int colorFormat = getColorFormat();
        if (colorFormat == 19) {
            encodeYUV420P(bArr, iArr, i, i2);
        } else if (colorFormat == 21) {
            encodeYUV420SP(bArr, iArr, i, i2);
        }
        return bArr;
    }

    private void encodeYUV420P(byte[] bArr, int[] iArr, int i, int i2) {
        int i3 = i * i2;
        int i4 = (i3 / 4) + i3;
        int i5 = 0;
        int i6 = 0;
        for (int i7 = 0; i7 < i2; i7++) {
            int i8 = 0;
            while (i8 < i) {
                int i9 = iArr[i5];
                int i10 = (16711680 & i9) >> 16;
                int i11 = (65280 & i9) >> 8;
                int i12 = 255;
                int i13 = (i9 & 255) >> 0;
                int i14 = (((((i10 * 66) + (i11 * TsExtractor.TS_STREAM_TYPE_AC3)) + (i13 * 25)) + 128) >> 8) + 16;
                int i15 = (((((i10 * (-38)) - (i11 * 74)) + (i13 * 112)) + 128) >> 8) + 128;
                int i16 = (((((i10 * 112) - (i11 * 94)) - (i13 * 18)) + 128) >> 8) + 128;
                int i17 = i6 + 1;
                if (i14 < 0) {
                    i14 = 0;
                } else if (i14 > 255) {
                    i14 = 255;
                }
                bArr[i6] = (byte) i14;
                if (i7 % 2 == 0 && i5 % 2 == 0) {
                    int i18 = i4 + 1;
                    if (i16 < 0) {
                        i16 = 0;
                    } else if (i16 > 255) {
                        i16 = 255;
                    }
                    bArr[i4] = (byte) i16;
                    int i19 = i3 + 1;
                    if (i15 < 0) {
                        i12 = 0;
                    } else if (i15 <= 255) {
                        i12 = i15;
                    }
                    bArr[i3] = (byte) i12;
                    i3 = i19;
                    i4 = i18;
                }
                i5++;
                i8++;
                i6 = i17;
            }
        }
    }

    private void encodeYUV420SP(byte[] bArr, int[] iArr, int i, int i2) {
        int i3 = i * i2;
        int i4 = 0;
        int i5 = 0;
        for (int i6 = 0; i6 < i2; i6++) {
            int i7 = 0;
            while (i7 < i) {
                int i8 = iArr[i5];
                int i9 = (16711680 & i8) >> 16;
                int i10 = (65280 & i8) >> 8;
                int i11 = 255;
                int i12 = (i8 & 255) >> 0;
                int i13 = (((((i9 * 66) + (i10 * TsExtractor.TS_STREAM_TYPE_AC3)) + (i12 * 25)) + 128) >> 8) + 16;
                int i14 = (((((i9 * (-38)) - (i10 * 74)) + (i12 * 112)) + 128) >> 8) + 128;
                int i15 = (((((i9 * 112) - (i10 * 94)) - (i12 * 18)) + 128) >> 8) + 128;
                int i16 = i4 + 1;
                if (i13 < 0) {
                    i13 = 0;
                } else if (i13 > 255) {
                    i13 = 255;
                }
                bArr[i4] = (byte) i13;
                if (i6 % 2 == 0 && i5 % 2 == 0) {
                    int i17 = i3 + 1;
                    if (i14 < 0) {
                        i14 = 0;
                    } else if (i14 > 255) {
                        i14 = 255;
                    }
                    bArr[i3] = (byte) i14;
                    i3 = i17 + 1;
                    if (i15 < 0) {
                        i11 = 0;
                    } else if (i15 <= 255) {
                        i11 = i15;
                    }
                    bArr[i17] = (byte) i11;
                }
                i5++;
                i7++;
                i4 = i16;
            }
        }
    }
}