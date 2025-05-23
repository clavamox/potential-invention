package com.mylhyl.circledialog.params;

import android.os.Parcel;
import android.os.Parcelable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;

/* loaded from: classes.dex */
public class SubTitleParams implements Parcelable {
    public static final Parcelable.Creator<SubTitleParams> CREATOR = new Parcelable.Creator<SubTitleParams>() { // from class: com.mylhyl.circledialog.params.SubTitleParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SubTitleParams createFromParcel(Parcel parcel) {
            return new SubTitleParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SubTitleParams[] newArray(int i) {
            return new SubTitleParams[i];
        }
    };
    public int backgroundColor;
    public int gravity;
    public int height;
    public boolean isShowBottomDivider;
    public int[] padding;
    public int styleText;
    public String text;
    public int textColor;
    public int textSize;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SubTitleParams() {
        this.padding = CircleDimen.SUBTITLE_PADDING;
        this.textSize = CircleDimen.SUBTITLE_TEXT_SIZE;
        this.textColor = CircleColor.SUBTITLE_TEXT;
        this.gravity = 17;
        this.styleText = 0;
    }

    protected SubTitleParams(Parcel parcel) {
        this.padding = CircleDimen.SUBTITLE_PADDING;
        this.textSize = CircleDimen.SUBTITLE_TEXT_SIZE;
        this.textColor = CircleColor.SUBTITLE_TEXT;
        this.gravity = 17;
        this.styleText = 0;
        this.text = parcel.readString();
        this.padding = parcel.createIntArray();
        this.height = parcel.readInt();
        this.textSize = parcel.readInt();
        this.textColor = parcel.readInt();
        this.backgroundColor = parcel.readInt();
        this.gravity = parcel.readInt();
        this.styleText = parcel.readInt();
        this.isShowBottomDivider = parcel.readByte() != 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.text);
        parcel.writeIntArray(this.padding);
        parcel.writeInt(this.height);
        parcel.writeInt(this.textSize);
        parcel.writeInt(this.textColor);
        parcel.writeInt(this.backgroundColor);
        parcel.writeInt(this.gravity);
        parcel.writeInt(this.styleText);
        parcel.writeByte(this.isShowBottomDivider ? (byte) 1 : (byte) 0);
    }
}