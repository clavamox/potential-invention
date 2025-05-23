package cn.bingoogolapple.photopicker.util;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* loaded from: classes.dex */
public class BGAPhotoHelper {
    private static final SimpleDateFormat PHOTO_NAME_POSTFIX_SDF = new SimpleDateFormat("yyyy-MM-dd_HH-mm_ss", Locale.getDefault());
    private static final String STATE_CAMERA_FILE_PATH = "STATE_CAMERA_FILE_PATH";
    private static final String STATE_CROP_FILE_PATH = "STATE_CROP_FILE_PATH";
    private File mCameraFileDir;
    private String mCameraFilePath;
    private String mCropFilePath;

    public BGAPhotoHelper(File file) {
        this.mCameraFileDir = file;
        if (file.exists()) {
            return;
        }
        this.mCameraFileDir.mkdirs();
    }

    private File createCameraFile() throws IOException {
        File createTempFile = File.createTempFile("Capture_" + PHOTO_NAME_POSTFIX_SDF.format(new Date()), ".jpg", this.mCameraFileDir);
        this.mCameraFilePath = createTempFile.getAbsolutePath();
        return createTempFile;
    }

    private File createCropFile() throws IOException {
        File createTempFile = File.createTempFile("Crop_" + PHOTO_NAME_POSTFIX_SDF.format(new Date()), ".jpg", BGABaseAdapterUtil.getApp().getExternalCacheDir());
        this.mCropFilePath = createTempFile.getAbsolutePath();
        return createTempFile;
    }

    public Intent getChooseSystemGalleryIntent() {
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.addFlags(3);
        return intent;
    }

    public Intent getTakePhotoIntent() throws IOException {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", createFileUri(createCameraFile()));
        return intent;
    }

    public void refreshGallery() {
        if (TextUtils.isEmpty(this.mCameraFilePath)) {
            return;
        }
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setData(createFileUri(new File(this.mCameraFilePath)));
        BGABaseAdapterUtil.getApp().sendBroadcast(intent);
        this.mCameraFilePath = null;
    }

    public void deleteCameraFile() {
        deleteFile(this.mCameraFilePath);
        this.mCameraFilePath = null;
    }

    public void deleteCropFile() {
        deleteFile(this.mCropFilePath);
        this.mCropFilePath = null;
    }

    private void deleteFile(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        new File(str).deleteOnExit();
    }

    public String getCameraFilePath() {
        return this.mCameraFilePath;
    }

    public String getCropFilePath() {
        return this.mCropFilePath;
    }

    public Intent getCropIntent(String str, int i, int i2) throws IOException {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(3);
        intent.setDataAndType(createFileUri(new File(str)), "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", i);
        intent.putExtra("aspectY", i2);
        intent.putExtra("outputX", i);
        intent.putExtra("outputY", i2);
        intent.putExtra("return-data", false);
        intent.putExtra("scale", true);
        intent.putExtra("output", Uri.fromFile(createCropFile()));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        return intent;
    }

    public static Uri createFileUri(File file) {
        return BGAPhotoFileProvider.getUriForFile(BGABaseAdapterUtil.getApp(), BGABaseAdapterUtil.getApp().getApplicationInfo().packageName + ".bga_photo_picker.file_provider", file);
    }

    public static String getFilePathFromUri(Uri uri) {
        int columnIndex;
        String str = null;
        if (uri == null) {
            return null;
        }
        String scheme = uri.getScheme();
        if (TextUtils.isEmpty(scheme) || TextUtils.equals("file", scheme)) {
            return uri.getPath();
        }
        if (!TextUtils.equals("content", scheme)) {
            return null;
        }
        String[] strArr = {"_data"};
        Cursor query = BGABaseAdapterUtil.getApp().getContentResolver().query(uri, strArr, null, null, null);
        if (query == null) {
            return null;
        }
        if (query.moveToFirst() && (columnIndex = query.getColumnIndex(strArr[0])) > -1) {
            str = query.getString(columnIndex);
        }
        query.close();
        return str;
    }

    public static void onRestoreInstanceState(BGAPhotoHelper bGAPhotoHelper, Bundle bundle) {
        if (bGAPhotoHelper == null || bundle == null) {
            return;
        }
        bGAPhotoHelper.mCameraFilePath = bundle.getString(STATE_CAMERA_FILE_PATH);
        bGAPhotoHelper.mCropFilePath = bundle.getString(STATE_CROP_FILE_PATH);
    }

    public static void onSaveInstanceState(BGAPhotoHelper bGAPhotoHelper, Bundle bundle) {
        if (bGAPhotoHelper == null || bundle == null) {
            return;
        }
        bundle.putString(STATE_CAMERA_FILE_PATH, bGAPhotoHelper.mCameraFilePath);
        bundle.putString(STATE_CROP_FILE_PATH, bGAPhotoHelper.mCropFilePath);
    }
}