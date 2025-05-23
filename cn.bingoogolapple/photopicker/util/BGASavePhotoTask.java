package cn.bingoogolapple.photopicker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import cn.bingoogolapple.photopicker.util.BGAAsyncTask;
import java.io.File;
import java.lang.ref.SoftReference;

/* loaded from: classes.dex */
public class BGASavePhotoTask extends BGAAsyncTask<Void, Void> {
    private SoftReference<Bitmap> mBitmap;
    private Context mContext;
    private File mNewFile;

    public BGASavePhotoTask(BGAAsyncTask.Callback<Void> callback, Context context, File file) {
        super(callback);
        this.mContext = context.getApplicationContext();
        this.mNewFile = file;
    }

    public void setBitmapAndPerform(Bitmap bitmap) {
        this.mBitmap = new SoftReference<>(bitmap);
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0068 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r0v0, types: [int] */
    /* JADX WARN: Type inference failed for: r0v1, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r0v10, types: [java.io.FileOutputStream, java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r0v11 */
    /* JADX WARN: Type inference failed for: r0v12 */
    /* JADX WARN: Type inference failed for: r0v4 */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v7 */
    /* JADX WARN: Type inference failed for: r0v8, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r0v9 */
    /* JADX WARN: Type inference failed for: r1v4, types: [android.graphics.Bitmap] */
    @Override // android.os.AsyncTask
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Void doInBackground(java.lang.Void... r8) {
        /*
            r7 = this;
            r8 = 0
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L50
            java.io.File r1 = r7.mNewFile     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L50
            r0.<init>(r1)     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L50
            java.lang.ref.SoftReference<android.graphics.Bitmap> r1 = r7.mBitmap     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            java.lang.Object r1 = r1.get()     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            android.graphics.Bitmap r1 = (android.graphics.Bitmap) r1     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            android.graphics.Bitmap$CompressFormat r2 = android.graphics.Bitmap.CompressFormat.PNG     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            r3 = 100
            r1.compress(r2, r3, r0)     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            r0.flush()     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            android.content.Context r1 = r7.mContext     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            android.content.Intent r2 = new android.content.Intent     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            java.lang.String r3 = "android.intent.action.MEDIA_SCANNER_SCAN_FILE"
            java.io.File r4 = r7.mNewFile     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            android.net.Uri r4 = android.net.Uri.fromFile(r4)     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            r2.<init>(r3, r4)     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            r1.sendBroadcast(r2)     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            android.content.Context r1 = r7.mContext     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            int r2 = cn.bingoogolapple.photopicker.R.string.bga_pp_save_img_success_folder     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            java.io.File r4 = r7.mNewFile     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            java.io.File r4 = r4.getParentFile()     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            java.lang.String r4 = r4.getAbsolutePath()     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            r5 = 0
            r3[r5] = r4     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            java.lang.String r1 = r1.getString(r2, r3)     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil.showSafe(r1)     // Catch: java.lang.Exception -> L51 java.lang.Throwable -> L65
            r0.close()     // Catch: java.io.IOException -> L5c
            goto L61
        L4b:
            r0 = move-exception
            r6 = r0
            r0 = r8
            r8 = r6
            goto L66
        L50:
            r0 = r8
        L51:
            int r1 = cn.bingoogolapple.photopicker.R.string.bga_pp_save_img_failure     // Catch: java.lang.Throwable -> L65
            cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil.showSafe(r1)     // Catch: java.lang.Throwable -> L65
            if (r0 == 0) goto L61
            r0.close()     // Catch: java.io.IOException -> L5c
            goto L61
        L5c:
            int r0 = cn.bingoogolapple.photopicker.R.string.bga_pp_save_img_failure
            cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil.showSafe(r0)
        L61:
            r7.recycleBitmap()
            return r8
        L65:
            r8 = move-exception
        L66:
            if (r0 == 0) goto L71
            r0.close()     // Catch: java.io.IOException -> L6c
            goto L71
        L6c:
            int r0 = cn.bingoogolapple.photopicker.R.string.bga_pp_save_img_failure
            cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil.showSafe(r0)
        L71:
            r7.recycleBitmap()
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.bingoogolapple.photopicker.util.BGASavePhotoTask.doInBackground(java.lang.Void[]):java.lang.Void");
    }

    @Override // cn.bingoogolapple.photopicker.util.BGAAsyncTask, android.os.AsyncTask
    protected void onCancelled() {
        super.onCancelled();
        recycleBitmap();
    }

    private void recycleBitmap() {
        SoftReference<Bitmap> softReference = this.mBitmap;
        if (softReference == null || softReference.get() == null || this.mBitmap.get().isRecycled()) {
            return;
        }
        this.mBitmap.get().recycle();
        this.mBitmap = null;
    }
}