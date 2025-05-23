package com.mylhyl.circledialog.params;

import android.os.Parcel;
import android.os.Parcelable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;

/* loaded from: classes.dex */
public class TextParams implements Parcelable {
    public static final Parcelable.Creator<TextParams> CREATOR = new Parcelable.Creator<TextParams>() { // from class: com.mylhyl.circledialog.params.TextParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextParams createFromParcel(Parcel parcel) {
            return new TextParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextParams[] newArray(int i) {
            return new TextParams[i];
        }
    };
    public int backgroundColor;
    public int gravity;
    public int height;
    public int[] padding;
    public int styleText;
    public String text;
    public int textColor;
    public int textSize;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public TextParams() {
        this.padding = CircleDimen.TEXT_PADDING;
        this.text = "";
        this.height = CircleDimen.TEXT_HEIGHT;
        this.textColor = CircleColor.CONTENT;
        this.textSize = CircleDimen.CONTENT_TEXT_SIZE;
        this.gravity = 17;
        this.styleText = 0;
    }

    protected TextParams(Parcel parcel) {
        this.padding = CircleDimen.TEXT_PADDING;
        this.text = "";
        this.height = CircleDimen.TEXT_HEIGHT;
        this.textColor = CircleColor.CONTENT;
        this.textSize = CircleDimen.CONTENT_TEXT_SIZE;
        this.gravity = 17;
        this.styleText = 0;
        this.padding = parcel.createIntArray();
        this.text = parcel.readString();
        this.height = parcel.readInt();
        this.backgroundColor = parcel.readInt();
        this.textColor = parcel.readInt();
        this.textSize = parcel.readInt();
        this.gravity = parcel.readInt();
        this.styleText = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(this.padding);
        parcel.writeString(this.text);
        parcel.writeInt(this.height);
        parcel.writeInt(this.backgroundColor);
        parcel.writeInt(this.textColor);
        parcel.writeInt(this.textSize);
        parcel.writeInt(this.gravity);
        parcel.writeInt(this.styleText);
    }
}