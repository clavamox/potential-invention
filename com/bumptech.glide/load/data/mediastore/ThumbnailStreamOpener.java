package com.bumptech.glide.load.data.mediastore;

import android.content.ContentResolver;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/* loaded from: classes.dex */
class ThumbnailStreamOpener {
    private static final FileService DEFAULT_SERVICE = new FileService();
    private static final String TAG = "ThumbStreamOpener";
    private final ArrayPool byteArrayPool;
    private final ContentResolver contentResolver;
    private final List<ImageHeaderParser> parsers;
    private final ThumbnailQuery query;
    private final FileService service;

    ThumbnailStreamOpener(List<ImageHeaderParser> list, ThumbnailQuery thumbnailQuery, ArrayPool arrayPool, ContentResolver contentResolver) {
        this(list, DEFAULT_SERVICE, thumbnailQuery, arrayPool, contentResolver);
    }

    ThumbnailStreamOpener(List<ImageHeaderParser> list, FileService fileService, ThumbnailQuery thumbnailQuery, ArrayPool arrayPool, ContentResolver contentResolver) {
        this.service = fileService;
        this.query = thumbnailQuery;
        this.byteArrayPool = arrayPool;
        this.contentResolver = contentResolver;
        this.parsers = list;
    }

    int getOrientation(Uri uri) {
        InputStream inputStream = null;
        try {
            try {
                inputStream = this.contentResolver.openInputStream(uri);
                int orientation = ImageHeaderParserUtils.getOrientation(this.parsers, inputStream, this.byteArrayPool);
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException unused) {
                    }
                }
                return orientation;
            } catch (IOException | NullPointerException e) {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "Failed to open uri: " + uri, e);
                }
                if (inputStream == null) {
                    return -1;
                }
                try {
                    inputStream.close();
                    return -1;
                } catch (IOException unused2) {
                    return -1;
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    inputStream.close();
                } catch (IOException unused3) {
                }
            }
            throw th;
        }
    }

    public InputStream open(Uri uri) throws FileNotFoundException {
        String path = getPath(uri);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File file = this.service.get(path);
        if (!isValid(file)) {
            return null;
        }
        Uri fromFile = Uri.fromFile(file);
        try {
            return this.contentResolver.openInputStream(fromFile);
        } catch (NullPointerException e) {
            throw ((FileNotFoundException) new FileNotFoundException("NPE opening uri: " + uri + " -> " + fromFile).initCause(e));
        }
    }

    /* JADX WARN: Not initialized variable reg: 3, insn: 0x0048: MOVE (r2 I:??[OBJECT, ARRAY]) = (r3 I:??[OBJECT, ARRAY]), block:B:27:0x0048 */
    /* JADX WARN: Removed duplicated region for block: B:29:0x004b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.String getPath(android.net.Uri r7) {
        /*
            r6 = this;
            java.lang.String r0 = "ThumbStreamOpener"
            java.lang.String r1 = "Failed to query for thumbnail for Uri: "
            r2 = 0
            com.bumptech.glide.load.data.mediastore.ThumbnailQuery r3 = r6.query     // Catch: java.lang.Throwable -> L26 java.lang.SecurityException -> L28
            android.database.Cursor r3 = r3.query(r7)     // Catch: java.lang.Throwable -> L26 java.lang.SecurityException -> L28
            if (r3 == 0) goto L20
            boolean r4 = r3.moveToFirst()     // Catch: java.lang.SecurityException -> L1e java.lang.Throwable -> L47
            if (r4 == 0) goto L20
            r4 = 0
            java.lang.String r7 = r3.getString(r4)     // Catch: java.lang.SecurityException -> L1e java.lang.Throwable -> L47
            if (r3 == 0) goto L1d
            r3.close()
        L1d:
            return r7
        L1e:
            r4 = move-exception
            goto L2a
        L20:
            if (r3 == 0) goto L25
            r3.close()
        L25:
            return r2
        L26:
            r7 = move-exception
            goto L49
        L28:
            r4 = move-exception
            r3 = r2
        L2a:
            r5 = 3
            boolean r5 = android.util.Log.isLoggable(r0, r5)     // Catch: java.lang.Throwable -> L47
            if (r5 == 0) goto L41
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L47
            r5.<init>(r1)     // Catch: java.lang.Throwable -> L47
            java.lang.StringBuilder r7 = r5.append(r7)     // Catch: java.lang.Throwable -> L47
            java.lang.String r7 = r7.toString()     // Catch: java.lang.Throwable -> L47
            android.util.Log.d(r0, r7, r4)     // Catch: java.lang.Throwable -> L47
        L41:
            if (r3 == 0) goto L46
            r3.close()
        L46:
            return r2
        L47:
            r7 = move-exception
            r2 = r3
        L49:
            if (r2 == 0) goto L4e
            r2.close()
        L4e:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.data.mediastore.ThumbnailStreamOpener.getPath(android.net.Uri):java.lang.String");
    }

    private boolean isValid(File file) {
        return this.service.exists(file) && 0 < this.service.length(file);
    }
}