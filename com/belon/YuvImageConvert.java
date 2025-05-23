package com.belon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import androidx.core.view.ViewCompat;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.io.ByteArrayOutputStream;
import java.lang.ref.SoftReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
public class YuvImageConvert {
    ExecutorService executors;

    public interface YuvImageCallback {
        void complete(Bitmap bitmap);
    }

    public YuvImageConvert() {
        this.executors = null;
        this.executors = Executors.newSingleThreadExecutor();
    }

    public void yuv_y_to_bmp(byte[] bArr, byte[] bArr2, int i, int i2) {
        int i3 = (((i * 3) + 3) & (-4)) * i2;
        bArr2[0] = 66;
        bArr2[1] = 77;
        bArr2[2] = (byte) (i3 & 255);
        bArr2[3] = (byte) ((i3 >> 8) & 255);
        bArr2[4] = (byte) ((i3 >> 16) & 255);
        bArr2[5] = (byte) ((i3 >> 24) & 255);
        bArr2[10] = (byte) 54;
        bArr2[11] = (byte) 0;
        bArr2[12] = (byte) 0;
        bArr2[13] = (byte) 0;
        int i4 = i2 * (-1);
        bArr2[14] = (byte) 40;
        bArr2[15] = (byte) 0;
        bArr2[16] = (byte) 0;
        bArr2[17] = (byte) 0;
        bArr2[18] = (byte) (i & 255);
        bArr2[19] = (byte) ((i >> 8) & 255);
        bArr2[20] = (byte) ((i >> 16) & 255);
        bArr2[21] = (byte) ((i >> 24) & 255);
        bArr2[22] = (byte) (i4 & 255);
        bArr2[23] = (byte) ((i4 >> 8) & 255);
        bArr2[24] = (byte) ((i4 >> 16) & 255);
        bArr2[25] = (byte) ((i4 >> 24) & 255);
        bArr2[26] = 1;
        bArr2[27] = 0;
        bArr2[28] = 24;
        bArr2[29] = 0;
        bArr2[30] = 0;
        bArr2[34] = 0;
        for (int i5 = 0; i5 < bArr.length; i5++) {
            int i6 = i5 * 3;
            bArr2[i6 + 54] = bArr[i5];
            bArr2[i6 + 1 + 54] = bArr[i5];
            bArr2[i6 + 2 + 54] = bArr[i5];
        }
    }

    public void bitmapToYuvImage(final Bitmap bitmap, final YuvImageCallback yuvImageCallback) {
        this.executors.submit(new Runnable() { // from class: com.belon.YuvImageConvert.1
            @Override // java.lang.Runnable
            public void run() {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int i = width * height;
                int[] iArr = new int[i];
                bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
                byte[] bArr = new byte[(i * 3) / 2];
                YuvImageConvert.this.conver_argb_to_yuv(bArr, iArr, width, height);
                YuvImage yuvImage = new YuvImage(bArr, 17, width, height, null);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap2 = (Bitmap) new SoftReference(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options)).get();
                YuvImageCallback yuvImageCallback2 = yuvImageCallback;
                if (yuvImageCallback2 != null) {
                    yuvImageCallback2.complete(bitmap2);
                }
            }
        });
    }

    public void bitmapToYuvImage2(final Bitmap bitmap, final YuvImageCallback yuvImageCallback) {
        this.executors.submit(new Runnable() { // from class: com.belon.YuvImageConvert.2
            @Override // java.lang.Runnable
            public void run() {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int i = width * height;
                int[] iArr = new int[i];
                bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
                byte[] bArr = new byte[i];
                YuvImageConvert.this.conver_argb_to_ydata(bArr, iArr, width, height);
                int i2 = (i * 3) + 54;
                byte[] bArr2 = new byte[i2];
                YuvImageConvert.this.yuv_y_to_bmp(bArr, bArr2, width, height);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap2 = (Bitmap) new SoftReference(BitmapFactory.decodeByteArray(bArr2, 0, i2, options)).get();
                YuvImageCallback yuvImageCallback2 = yuvImageCallback;
                if (yuvImageCallback2 != null) {
                    yuvImageCallback2.complete(bitmap2);
                }
            }
        });
    }

    public void conver_argb_to_ydata(byte[] bArr, int[] iArr, int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            int i5 = 0;
            while (i5 < i) {
                int i6 = iArr[(i4 * i) + i5] & ViewCompat.MEASURED_SIZE_MASK;
                int i7 = i6 & 255;
                int i8 = 255;
                int i9 = (i6 >> 8) & 255;
                int i10 = (i6 >> 16) & 255;
                int i11 = (((((i7 * 66) + (i9 * TsExtractor.TS_STREAM_TYPE_AC3)) + (i10 * 25)) + 128) >> 8) + 16;
                int i12 = (((((i7 * (-38)) - (i9 * 74)) + (i10 * 112)) + 128) >> 8) + 128;
                int i13 = (((((i7 * 112) - (i9 * 94)) - (i10 * 18)) + 128) >> 8) + 128;
                if (i11 < 16) {
                    i8 = 16;
                } else if (i11 <= 255) {
                    i8 = i11;
                }
                bArr[i3] = (byte) i8;
                i5++;
                i3++;
            }
        }
    }

    public void conver_argb_to_yuv(byte[] bArr, int[] iArr, int i, int i2) {
        int i3 = i * i2;
        for (int i4 = 0; i4 < i2; i4++) {
            for (int i5 = 0; i5 < i; i5++) {
                int i6 = (i4 * i) + i5;
                int i7 = iArr[i6] & ViewCompat.MEASURED_SIZE_MASK;
                int i8 = i7 & 255;
                int i9 = 255;
                int i10 = (i7 >> 8) & 255;
                int i11 = (i7 >> 16) & 255;
                int i12 = (((((i8 * 66) + (i10 * TsExtractor.TS_STREAM_TYPE_AC3)) + (i11 * 25)) + 128) >> 8) + 16;
                int i13 = (((((i8 * (-38)) - (i10 * 74)) + (i11 * 112)) + 128) >> 8) + 128;
                int i14 = (((((i8 * 112) - (i10 * 94)) - (i11 * 18)) + 128) >> 8) + 128;
                if (i12 < 16) {
                    i9 = 16;
                } else if (i12 <= 255) {
                    i9 = i12;
                }
                bArr[i6] = (byte) i9;
                int i15 = ((i4 >> 1) * i) + i3 + (i5 & (-2));
                bArr[i15 + 0] = Byte.MIN_VALUE;
                bArr[i15 + 1] = Byte.MIN_VALUE;
            }
        }
    }
}