package com.mylhyl.circledialog.params;

import android.os.Parcel;
import android.os.Parcelable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;

/* loaded from: classes.dex */
public class TitleParams implements Parcelable {
    public static final Parcelable.Creator<TitleParams> CREATOR = new Parcelable.Creator<TitleParams>() { // from class: com.mylhyl.circledialog.params.TitleParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TitleParams createFromParcel(Parcel parcel) {
            return new TitleParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TitleParams[] newArray(int i) {
            return new TitleParams[i];
        }
    };
    public int backgroundColor;
    public int gravity;
    public int height;
    public int icon;
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

    public TitleParams() {
        this.padding = CircleDimen.TITLE_PADDING;
        this.height = 0;
        this.textSize = CircleDimen.TITLE_TEXT_SIZE;
        this.textColor = CircleColor.TITLE;
        this.gravity = 17;
        this.styleText = 0;
    }

    protected TitleParams(Parcel parcel) {
        this.padding = CircleDimen.TITLE_PADDING;
        this.height = 0;
        this.textSize = CircleDimen.TITLE_TEXT_SIZE;
        this.textColor = CircleColor.TITLE;
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
        this.icon = parcel.readInt();
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
        parcel.writeInt(this.icon);
        parcel.writeByte(this.isShowBottomDivider ? (byte) 1 : (byte) 0);
    }
}