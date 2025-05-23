package cn.bingoogolapple.photopicker.util;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.model.BGAPhotoFolderModel;
import cn.bingoogolapple.photopicker.util.BGAAsyncTask;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes.dex */
public class BGALoadPhotoTask extends BGAAsyncTask<Void, ArrayList<BGAPhotoFolderModel>> {
    private Context mContext;
    private boolean mTakePhotoEnabled;

    public BGALoadPhotoTask(BGAAsyncTask.Callback<ArrayList<BGAPhotoFolderModel>> callback, Context context, boolean z) {
        super(callback);
        this.mContext = context.getApplicationContext();
        this.mTakePhotoEnabled = z;
    }

    private static boolean isNotImageFile(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        File file = new File(str);
        return !file.exists() || file.length() == 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public ArrayList<BGAPhotoFolderModel> doInBackground(Void... voidArr) {
        BGAPhotoFolderModel bGAPhotoFolderModel;
        int lastIndexOf;
        ArrayList<BGAPhotoFolderModel> arrayList = new ArrayList<>();
        BGAPhotoFolderModel bGAPhotoFolderModel2 = new BGAPhotoFolderModel(this.mTakePhotoEnabled);
        bGAPhotoFolderModel2.name = this.mContext.getString(R.string.bga_pp_all_image);
        arrayList.add(bGAPhotoFolderModel2);
        HashMap hashMap = new HashMap();
        Cursor cursor = null;
        try {
            try {
                Cursor query = this.mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_data"}, "mime_type=? or mime_type=? or mime_type=?", new String[]{"image/jpeg", "image/png", "image/jpg"}, "date_added DESC");
                if (query != null) {
                    try {
                        if (query.getCount() > 0) {
                            boolean z = true;
                            while (query.moveToNext()) {
                                String string = query.getString(query.getColumnIndex("_data"));
                                if (!isNotImageFile(string)) {
                                    if (z) {
                                        bGAPhotoFolderModel2.coverPath = string;
                                        z = false;
                                    }
                                    bGAPhotoFolderModel2.addLastPhoto(string);
                                    File parentFile = new File(string).getParentFile();
                                    String absolutePath = parentFile != null ? parentFile.getAbsolutePath() : null;
                                    if (TextUtils.isEmpty(absolutePath) && (lastIndexOf = string.lastIndexOf(File.separator)) != -1) {
                                        absolutePath = string.substring(0, lastIndexOf);
                                    }
                                    if (!TextUtils.isEmpty(absolutePath)) {
                                        if (hashMap.containsKey(absolutePath)) {
                                            bGAPhotoFolderModel = (BGAPhotoFolderModel) hashMap.get(absolutePath);
                                        } else {
                                            String substring = absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1);
                                            if (TextUtils.isEmpty(substring)) {
                                                substring = "/";
                                            }
                                            BGAPhotoFolderModel bGAPhotoFolderModel3 = new BGAPhotoFolderModel(substring, string);
                                            hashMap.put(absolutePath, bGAPhotoFolderModel3);
                                            bGAPhotoFolderModel = bGAPhotoFolderModel3;
                                        }
                                        bGAPhotoFolderModel.addLastPhoto(string);
                                    }
                                }
                            }
                            arrayList.addAll(hashMap.values());
                        }
                    } catch (Exception e) {
                        e = e;
                        cursor = query;
                        e.printStackTrace();
                        if (cursor != null) {
                            cursor.close();
                        }
                        return arrayList;
                    } catch (Throwable th) {
                        th = th;
                        cursor = query;
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                }
                if (query != null) {
                    query.close();
                }
            } catch (Exception e2) {
                e = e2;
            }
            return arrayList;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public BGALoadPhotoTask perform() {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        return this;
    }
}