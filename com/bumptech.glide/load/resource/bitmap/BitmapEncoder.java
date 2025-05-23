package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;

/* loaded from: classes.dex */
public class BitmapEncoder implements ResourceEncoder<Bitmap> {
    private static final String TAG = "BitmapEncoder";
    private final ArrayPool arrayPool;
    public static final Option<Integer> COMPRESSION_QUALITY = Option.memory("com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionQuality", 90);
    public static final Option<Bitmap.CompressFormat> COMPRESSION_FORMAT = Option.memory("com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionFormat");

    public BitmapEncoder(ArrayPool arrayPool) {
        this.arrayPool = arrayPool;
    }

    @Deprecated
    public BitmapEncoder() {
        this.arrayPool = null;
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0074 A[Catch: all -> 0x00c8, TRY_LEAVE, TryCatch #5 {all -> 0x00c8, blocks: (B:3:0x0023, B:15:0x004d, B:18:0x006e, B:20:0x0074, B:46:0x00c4, B:44:0x00c7, B:38:0x0069), top: B:2:0x0023 }] */
    @Override // com.bumptech.glide.load.Encoder
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean encode(com.bumptech.glide.load.engine.Resource<android.graphics.Bitmap> r9, java.io.File r10, com.bumptech.glide.load.Options r11) {
        /*
            r8 = this;
            java.lang.String r0 = "BitmapEncoder"
            java.lang.String r1 = "Compressed with type: "
            java.lang.Object r9 = r9.get()
            android.graphics.Bitmap r9 = (android.graphics.Bitmap) r9
            android.graphics.Bitmap$CompressFormat r2 = r8.getFormat(r9, r11)
            int r3 = r9.getWidth()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            int r4 = r9.getHeight()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.String r5 = "encode: [%dx%d] %s"
            com.bumptech.glide.util.pool.GlideTrace.beginSectionFormat(r5, r3, r4, r2)
            long r3 = com.bumptech.glide.util.LogTime.getLogTime()     // Catch: java.lang.Throwable -> Lc8
            com.bumptech.glide.load.Option<java.lang.Integer> r5 = com.bumptech.glide.load.resource.bitmap.BitmapEncoder.COMPRESSION_QUALITY     // Catch: java.lang.Throwable -> Lc8
            java.lang.Object r5 = r11.get(r5)     // Catch: java.lang.Throwable -> Lc8
            java.lang.Integer r5 = (java.lang.Integer) r5     // Catch: java.lang.Throwable -> Lc8
            int r5 = r5.intValue()     // Catch: java.lang.Throwable -> Lc8
            r6 = 0
            java.io.FileOutputStream r7 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L58 java.io.IOException -> L5a
            r7.<init>(r10)     // Catch: java.lang.Throwable -> L58 java.io.IOException -> L5a
            com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool r10 = r8.arrayPool     // Catch: java.lang.Throwable -> L52 java.io.IOException -> L55
            if (r10 == 0) goto L46
            com.bumptech.glide.load.data.BufferedOutputStream r10 = new com.bumptech.glide.load.data.BufferedOutputStream     // Catch: java.lang.Throwable -> L52 java.io.IOException -> L55
            com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool r6 = r8.arrayPool     // Catch: java.lang.Throwable -> L52 java.io.IOException -> L55
            r10.<init>(r7, r6)     // Catch: java.lang.Throwable -> L52 java.io.IOException -> L55
            r6 = r10
            goto L47
        L46:
            r6 = r7
        L47:
            r9.compress(r2, r5, r6)     // Catch: java.lang.Throwable -> L58 java.io.IOException -> L5a
            r6.close()     // Catch: java.lang.Throwable -> L58 java.io.IOException -> L5a
            r6.close()     // Catch: java.io.IOException -> L50 java.lang.Throwable -> Lc8
        L50:
            r10 = 1
            goto L6d
        L52:
            r9 = move-exception
            r6 = r7
            goto Lc2
        L55:
            r10 = move-exception
            r6 = r7
            goto L5b
        L58:
            r9 = move-exception
            goto Lc2
        L5a:
            r10 = move-exception
        L5b:
            r5 = 3
            boolean r5 = android.util.Log.isLoggable(r0, r5)     // Catch: java.lang.Throwable -> L58
            if (r5 == 0) goto L67
            java.lang.String r5 = "Failed to encode Bitmap"
            android.util.Log.d(r0, r5, r10)     // Catch: java.lang.Throwable -> L58
        L67:
            if (r6 == 0) goto L6c
            r6.close()     // Catch: java.io.IOException -> L6c java.lang.Throwable -> Lc8
        L6c:
            r10 = 0
        L6d:
            r5 = 2
            boolean r5 = android.util.Log.isLoggable(r0, r5)     // Catch: java.lang.Throwable -> Lc8
            if (r5 == 0) goto Lbe
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lc8
            r5.<init>(r1)     // Catch: java.lang.Throwable -> Lc8
            java.lang.StringBuilder r1 = r5.append(r2)     // Catch: java.lang.Throwable -> Lc8
            java.lang.String r2 = " of size "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> Lc8
            int r2 = com.bumptech.glide.util.Util.getBitmapByteSize(r9)     // Catch: java.lang.Throwable -> Lc8
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> Lc8
            java.lang.String r2 = " in "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> Lc8
            double r2 = com.bumptech.glide.util.LogTime.getElapsedMillis(r3)     // Catch: java.lang.Throwable -> Lc8
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> Lc8
            java.lang.String r2 = ", options format: "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> Lc8
            com.bumptech.glide.load.Option<android.graphics.Bitmap$CompressFormat> r2 = com.bumptech.glide.load.resource.bitmap.BitmapEncoder.COMPRESSION_FORMAT     // Catch: java.lang.Throwable -> Lc8
            java.lang.Object r11 = r11.get(r2)     // Catch: java.lang.Throwable -> Lc8
            java.lang.StringBuilder r11 = r1.append(r11)     // Catch: java.lang.Throwable -> Lc8
            java.lang.String r1 = ", hasAlpha: "
            java.lang.StringBuilder r11 = r11.append(r1)     // Catch: java.lang.Throwable -> Lc8
            boolean r9 = r9.hasAlpha()     // Catch: java.lang.Throwable -> Lc8
            java.lang.StringBuilder r9 = r11.append(r9)     // Catch: java.lang.Throwable -> Lc8
            java.lang.String r9 = r9.toString()     // Catch: java.lang.Throwable -> Lc8
            android.util.Log.v(r0, r9)     // Catch: java.lang.Throwable -> Lc8
        Lbe:
            com.bumptech.glide.util.pool.GlideTrace.endSection()
            return r10
        Lc2:
            if (r6 == 0) goto Lc7
            r6.close()     // Catch: java.io.IOException -> Lc7 java.lang.Throwable -> Lc8
        Lc7:
            throw r9     // Catch: java.lang.Throwable -> Lc8
        Lc8:
            r9 = move-exception
            com.bumptech.glide.util.pool.GlideTrace.endSection()
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.BitmapEncoder.encode(com.bumptech.glide.load.engine.Resource, java.io.File, com.bumptech.glide.load.Options):boolean");
    }

    private Bitmap.CompressFormat getFormat(Bitmap bitmap, Options options) {
        Bitmap.CompressFormat compressFormat = (Bitmap.CompressFormat) options.get(COMPRESSION_FORMAT);
        if (compressFormat != null) {
            return compressFormat;
        }
        if (bitmap.hasAlpha()) {
            return Bitmap.CompressFormat.PNG;
        }
        return Bitmap.CompressFormat.JPEG;
    }

    @Override // com.bumptech.glide.load.ResourceEncoder
    public EncodeStrategy getEncodeStrategy(Options options) {
        return EncodeStrategy.TRANSFORMED;
    }
}