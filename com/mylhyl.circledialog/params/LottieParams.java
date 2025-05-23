package com.mylhyl.circledialog.params;

import android.os.Parcel;
import android.os.Parcelable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;

/* loaded from: classes.dex */
public class LottieParams implements Parcelable {
    public static final Parcelable.Creator<LottieParams> CREATOR = new Parcelable.Creator<LottieParams>() { // from class: com.mylhyl.circledialog.params.LottieParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LottieParams createFromParcel(Parcel parcel) {
            return new LottieParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LottieParams[] newArray(int i) {
            return new LottieParams[i];
        }
    };
    public String animationFileName;
    public int animationResId;
    public boolean autoPlay;
    public int backgroundColor;
    public String imageAssetsFolder;
    public boolean loop;
    public int lottieHeight;
    public int lottieWidth;
    public int[] margins;
    public int styleText;
    public String text;
    public int textColor;
    public int[] textMargins;
    public int[] textPadding;
    public int textSize;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public LottieParams() {
        this.textMargins = CircleDimen.LOTTIE_TEXT_MARGINS;
        this.lottieHeight = CircleDimen.LOTTIE_HEIGHT;
        this.lottieWidth = CircleDimen.LOTTIE_WIDTH;
        this.text = "";
        this.textColor = CircleColor.LOADING_TEXT;
        this.textSize = CircleDimen.LOADING_TEXT_SIZE;
        this.styleText = 0;
    }

    protected LottieParams(Parcel parcel) {
        this.textMargins = CircleDimen.LOTTIE_TEXT_MARGINS;
        this.lottieHeight = CircleDimen.LOTTIE_HEIGHT;
        this.lottieWidth = CircleDimen.LOTTIE_WIDTH;
        this.text = "";
        this.textColor = CircleColor.LOADING_TEXT;
        this.textSize = CircleDimen.LOADING_TEXT_SIZE;
        this.styleText = 0;
        this.margins = parcel.createIntArray();
        this.textPadding = parcel.createIntArray();
        this.textMargins = parcel.createIntArray();
        this.lottieHeight = parcel.readInt();
        this.lottieWidth = parcel.readInt();
        this.animationResId = parcel.readInt();
        this.animationFileName = parcel.readString();
        this.imageAssetsFolder = parcel.readString();
        this.autoPlay = parcel.readByte() != 0;
        this.loop = parcel.readByte() != 0;
        this.text = parcel.readString();
        this.backgroundColor = parcel.readInt();
        this.textColor = parcel.readInt();
        this.textSize = parcel.readInt();
        this.styleText = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(this.margins);
        parcel.writeIntArray(this.textPadding);
        parcel.writeIntArray(this.textMargins);
        parcel.writeInt(this.lottieHeight);
        parcel.writeInt(this.lottieWidth);
        parcel.writeInt(this.animationResId);
        parcel.writeString(this.animationFileName);
        parcel.writeString(this.imageAssetsFolder);
        parcel.writeByte(this.autoPlay ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.loop ? (byte) 1 : (byte) 0);
        parcel.writeString(this.text);
        parcel.writeInt(this.backgroundColor);
        parcel.writeInt(this.textColor);
        parcel.writeInt(this.textSize);
        parcel.writeInt(this.styleText);
    }
}