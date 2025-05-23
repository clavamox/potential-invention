package com.mylhyl.circledialog.params;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
public class AdParams implements Parcelable {
    public static final Parcelable.Creator<AdParams> CREATOR = new Parcelable.Creator<AdParams>() { // from class: com.mylhyl.circledialog.params.AdParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AdParams createFromParcel(Parcel parcel) {
            return new AdParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AdParams[] newArray(int i) {
            return new AdParams[i];
        }
    };
    public boolean isShowIndicator;
    public int pointDrawableResId;
    public int pointLeftRightMargin = 2;
    public int[] resIds;
    public String[] urls;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public AdParams() {
    }

    protected AdParams(Parcel parcel) {
        this.resIds = parcel.createIntArray();
        this.urls = parcel.createStringArray();
        this.isShowIndicator = parcel.readByte() != 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(this.resIds);
        parcel.writeStringArray(this.urls);
        parcel.writeByte(this.isShowIndicator ? (byte) 1 : (byte) 0);
    }
}