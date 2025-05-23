package cn.bingoogolapple.photopicker.model;

import java.util.ArrayList;

/* loaded from: classes.dex */
public class BGAPhotoFolderModel {
    public String coverPath;
    private ArrayList<String> mPhotos;
    private boolean mTakePhotoEnabled;
    public String name;

    public BGAPhotoFolderModel(boolean z) {
        ArrayList<String> arrayList = new ArrayList<>();
        this.mPhotos = arrayList;
        this.mTakePhotoEnabled = z;
        if (z) {
            arrayList.add("");
        }
    }

    public BGAPhotoFolderModel(String str, String str2) {
        this.mPhotos = new ArrayList<>();
        this.name = str;
        this.coverPath = str2;
    }

    public boolean isTakePhotoEnabled() {
        return this.mTakePhotoEnabled;
    }

    public void addLastPhoto(String str) {
        this.mPhotos.add(str);
    }

    public ArrayList<String> getPhotos() {
        return this.mPhotos;
    }

    public int getCount() {
        return this.mTakePhotoEnabled ? this.mPhotos.size() - 1 : this.mPhotos.size();
    }
}