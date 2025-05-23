package com.wy.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class FileUtil {
    public static final String TAG = "FileUnit";

    public static String getPictureFilePath(Context context) {
        File file = new File(context.getFilesDir() + File.separator + "picture");
        if (!file.exists()) {
            file.mkdir();
            Log.d("FileUnit", "mkdir rs3:" + file.getAbsolutePath());
        }
        return file.getAbsolutePath();
    }

    public static String getVideoFilePath(Context context) {
        File file = new File(context.getFilesDir() + File.separator + "video");
        if (!file.exists()) {
            Log.d("FileUnit", "mkdir rs3:" + file.mkdir());
        }
        return file.getAbsolutePath();
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

    public static String getPath(Context context, Uri uri) {
        int columnIndex;
        String str = null;
        if (uri == null) {
            return null;
        }
        Cursor query = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
        if (query != null) {
            if (query.moveToFirst() && (columnIndex = query.getColumnIndex("_data")) >= 0) {
                str = query.getString(columnIndex);
            }
            query.close();
        }
        return str;
    }

    public static String getName(Context context, Uri uri) {
        DocumentFile fromSingleUri;
        if (uri == null || (fromSingleUri = DocumentFile.fromSingleUri(context, uri)) == null) {
            return null;
        }
        return fromSingleUri.getName();
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

    public static void shareMultiplePicture(Context context, Set<String> set, String str) {
        ArrayList arrayList = new ArrayList();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            File file = new File(it.next());
            Uri fileUri = getFileUri(context, file);
            Uri contentUri = getContentUri(context, file);
            if (contentUri == null) {
                Uri copyFileToExternal = copyFileToExternal(context, "Mask", file);
                if (copyFileToExternal != null) {
                    fileUri = copyFileToExternal;
                }
                contentUri = fileUri;
            }
            arrayList.add(contentUri);
        }
        shareMultiplePicture(context, (ArrayList<Uri>) arrayList, str);
    }

    public static void sharePicture(Context context, String str) {
        ArrayList arrayList = new ArrayList();
        File file = new File(str);
        Uri fileUri = getFileUri(context, file);
        Uri contentUri = getContentUri(context, file);
        if (contentUri == null) {
            Uri copyFileToExternal = copyFileToExternal(context, "Mask", file);
            if (copyFileToExternal != null) {
                fileUri = copyFileToExternal;
            }
            contentUri = fileUri;
        }
        arrayList.add(contentUri);
        shareMultiplePicture(context, (ArrayList<Uri>) arrayList, "image/*");
    }

    private static void shareMultiplePicture(Context context, ArrayList<Uri> arrayList, String str) {
        Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
        intent.addFlags(268435456);
        intent.addFlags(1);
        intent.addFlags(2);
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
        intent.setType(str);
        context.startActivity(Intent.createChooser(intent, "分享多个文件"));
    }

    public static void shareMultipleVedio(Context context, Set<String> set) {
        if (set == null && set.size() == 0) {
            return;
        }
        Intent intent = new Intent("android.intent.action.SEND");
        intent.addFlags(268435456);
        intent.addFlags(1);
        intent.addFlags(2);
        intent.setType("video/*");
        ArrayList arrayList = new ArrayList();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            File file = new File(it.next());
            Uri fileUri = getFileUri(context, file);
            Uri contentUri = getContentUri(context, file);
            if (contentUri == null) {
                Uri copyFileToExternal = copyFileToExternal(context, "Mask", file);
                if (copyFileToExternal != null) {
                    fileUri = copyFileToExternal;
                }
                contentUri = fileUri;
            }
            arrayList.add(contentUri);
        }
        boolean z = false;
        for (int i = 0; i < arrayList.size(); i++) {
            List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 65536);
            if (!queryIntentActivities.isEmpty()) {
                Iterator<ResolveInfo> it2 = queryIntentActivities.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    ResolveInfo next = it2.next();
                    if (next.activityInfo.name.contains("com.tencent.mm.ui.tools.ShareImgUI")) {
                        intent.putExtra("android.intent.extra.STREAM", (Parcelable) arrayList.get(i));
                        intent.setPackage(next.activityInfo.packageName);
                        intent.setClassName(next.activityInfo.packageName, next.activityInfo.name);
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    return;
                } else {
                    context.startActivity(Intent.createChooser(intent, "Select "));
                }
            }
        }
    }

    public static String setImageSaveFile(Context context) {
        return getOwnCacheDirectory(context, "wonyue/ipc/image").getPath();
    }

    public static String setVideoSaveFile(Context context) {
        return getOwnCacheDirectory(context, "wonyue/ipc/video").getPath();
    }

    private static File getOwnCacheDirectory(Context context, String str) {
        File file = ("mounted".equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) ? new File(Environment.getExternalStorageDirectory(), str) : null;
        return (file == null || !(file.exists() || file.mkdirs())) ? context.getCacheDir() : file;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }
}