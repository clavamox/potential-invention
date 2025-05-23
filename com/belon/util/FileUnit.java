package com.belon.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import androidx.core.content.FileProvider;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class FileUnit {
    public static final String TAG = "FileUnit";

    public static String getPictureFilePath() {
        File file = new File(getFilePath() + "/picture");
        if (!file.exists()) {
            Log.d("FileUnit", "mkdir rs3:" + file.mkdir());
        }
        return getFilePath() + "/picture";
    }

    public static String getVideoFilePath() {
        File file = new File(getFilePath() + "/bk_vedio");
        if (!file.exists()) {
            Log.d("FileUnit", "mkdir rs3:" + file.mkdir());
        }
        return getFilePath() + "/bk_vedio";
    }

    public static String getFilePath() {
        File file = new File(getSDPath() + "/bk_file");
        if (!file.exists()) {
            Log.d("FileUnit", "mkdir rs4:" + file.mkdir() + "," + file.getAbsolutePath());
        }
        return getSDPath() + "/bk_file";
    }

    public static String getTempPath() {
        File file = new File("/storage/sdcard0/bk_file/bk_vedio");
        if (!file.exists()) {
            file.mkdir();
        }
        return getVideoFilePath() + "/temp";
    }

    public static void clearTempFile() {
        try {
            for (File file : new File(getTempPath()).listFiles()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getSDPath() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return Environment.getDownloadCacheDirectory().toString();
    }

    public static Uri getContentUri(Context context, File file) {
        int columnIndex;
        Uri uri = null;
        if (file != null && file.exists()) {
            String absolutePath = file.getAbsolutePath();
            String mimeType = getMimeType(file.getName());
            ContentResolver contentResolver = context.getContentResolver();
            Uri contentUri = getContentUri(mimeType);
            Cursor query = contentResolver.query(contentUri, new String[]{"_id"}, "_data=?", new String[]{absolutePath}, null);
            if (query != null) {
                if (query.moveToFirst() && (columnIndex = query.getColumnIndex("_id")) >= 0) {
                    uri = ContentUris.withAppendedId(contentUri, query.getLong(columnIndex));
                }
                query.close();
            }
        }
        return uri;
    }

    public static Uri getContentUri(String str) {
        if (str.startsWith(TtmlNode.TAG_IMAGE)) {
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        if (str.startsWith("video")) {
            return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        if (str.startsWith("audio")) {
            return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        return MediaStore.Files.getContentUri("external");
    }

    public static String getMimeType(String str) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(str));
    }

    public static String getMimeType(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        return context.getContentResolver().getType(uri);
    }

    public static String getExtension(String str) {
        int lastIndexOf = str.lastIndexOf(".");
        return lastIndexOf > 0 ? str.substring(lastIndexOf + 1) : "";
    }

    public static Uri getFileUri(Context context, File file) {
        return FileProvider.getUriForFile(context, getAuthority(context), file);
    }

    public static String getAuthority(Context context) {
        return context.getPackageName() + ".FileProvider";
    }

    public static Uri getDuplicateFileUri(Context context, File file) {
        int columnIndex;
        Uri uri = null;
        if (file != null && file.exists()) {
            String name = file.getName();
            long length = file.length();
            String mimeType = getMimeType(name);
            ContentResolver contentResolver = context.getContentResolver();
            Uri contentUri = getContentUri(mimeType);
            Cursor query = contentResolver.query(contentUri, new String[]{"_id"}, "_display_name=? AND _size=?", new String[]{name, String.valueOf(length)}, null);
            if (query != null) {
                if (query.moveToFirst() && (columnIndex = query.getColumnIndex("_id")) >= 0) {
                    uri = ContentUris.withAppendedId(contentUri, query.getLong(columnIndex));
                }
                query.close();
            }
        }
        return uri;
    }

    public static Uri copyFileToExternal(Context context, String str, File file) {
        OutputStream openOutputStream;
        if (file == null || !file.exists()) {
            return null;
        }
        Uri duplicateFileUri = getDuplicateFileUri(context, file);
        if (duplicateFileUri != null) {
            return duplicateFileUri;
        }
        String name = file.getName();
        String mimeType = getMimeType(name);
        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = getContentUri(mimeType);
        ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", name);
        contentValues.put("mime_type", mimeType);
        Uri insert = contentResolver.insert(contentUri, contentValues);
        if (insert == null) {
            return null;
        }
        boolean z = false;
        try {
            openOutputStream = contentResolver.openOutputStream(insert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                z = copy(fileInputStream, openOutputStream);
                fileInputStream.close();
                if (openOutputStream != null) {
                    openOutputStream.close();
                }
                if (z) {
                    return insert;
                }
                delete(context, insert);
                return null;
            } finally {
            }
        } finally {
        }
    }

    public static String getDirName(String str) {
        if (str.startsWith(TtmlNode.TAG_IMAGE)) {
            return Environment.DIRECTORY_PICTURES;
        }
        if (str.startsWith("video")) {
            return Environment.DIRECTORY_PICTURES;
        }
        if (str.startsWith("audio")) {
            return Environment.DIRECTORY_MUSIC;
        }
        return Environment.DIRECTORY_DOCUMENTS;
    }

    public static boolean copy(InputStream inputStream, OutputStream outputStream) {
        try {
            byte[] bArr = new byte[8192];
            while (true) {
                int read = inputStream.read(bArr);
                if (read > 0) {
                    outputStream.write(bArr, 0, read);
                } else {
                    outputStream.flush();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(Context context, Uri uri) {
        try {
            return context.getContentResolver().delete(uri, null, null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static byte[] readFile(String str, int i) {
        byte[] bArr = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            bArr = new byte[i];
            fileInputStream.read(bArr);
            fileInputStream.close();
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return bArr;
        }
    }

    public static byte[] readFile(String str) {
        byte[] bArr = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            bArr = new byte[fileInputStream.available()];
            fileInputStream.read(bArr);
            fileInputStream.close();
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return bArr;
        }
    }
}