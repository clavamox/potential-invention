package com.mylhyl.circledialog.params;

import android.os.Parcel;
import android.os.Parcelable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;

/* loaded from: classes.dex */
public class ButtonParams implements Parcelable {
    public static final String COUNT_DOWN_TEXT_FORMAT = "(%d)秒";
    public static final Parcelable.Creator<ButtonParams> CREATOR = new Parcelable.Creator<ButtonParams>() { // from class: com.mylhyl.circledialog.params.ButtonParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ButtonParams createFromParcel(Parcel parcel) {
            return new ButtonParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ButtonParams[] newArray(int i) {
            return new ButtonParams[i];
        }
    };
    public int backgroundColor;
    public int backgroundColorPress;
    public long countDownInterval;
    public String countDownText;
    public long countDownTime;
    public boolean disable;
    public int height;
    public int styleText;
    public String text;
    public int textColor;
    public int textColorDisable;
    public int textSize;
    public int topMargin;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ButtonParams() {
        this.textColor = CircleColor.FOOTER_BUTTON_TEXT_POSITIVE;
        this.textSize = CircleDimen.FOOTER_BUTTON_TEXT_SIZE;
        this.height = CircleDimen.FOOTER_BUTTON_HEIGHT;
        this.textColorDisable = CircleColor.FOOTER_BUTTON_DISABLE;
        this.styleText = 0;
    }

    protected ButtonParams(Parcel parcel) {
        this.textColor = CircleColor.FOOTER_BUTTON_TEXT_POSITIVE;
        this.textSize = CircleDimen.FOOTER_BUTTON_TEXT_SIZE;
        this.height = CircleDimen.FOOTER_BUTTON_HEIGHT;
        this.textColorDisable = CircleColor.FOOTER_BUTTON_DISABLE;
        this.styleText = 0;
        this.topMargin = parcel.readInt();
        this.textColor = parcel.readInt();
        this.textSize = parcel.readInt();
        this.height = parcel.readInt();
        this.backgroundColor = parcel.readInt();
        this.text = parcel.readString();
        this.disable = parcel.readByte() != 0;
        this.textColorDisable = parcel.readInt();
        this.backgroundColorPress = parcel.readInt();
        this.styleText = parcel.readInt();
        this.countDownTime = parcel.readLong();
        this.countDownInterval = parcel.readLong();
        this.countDownText = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.topMargin);
        parcel.writeInt(this.textColor);
        parcel.writeInt(this.textSize);
        parcel.writeInt(this.height);
        parcel.writeInt(this.backgroundColor);
        parcel.writeString(this.text);
        parcel.writeByte(this.disable ? (byte) 1 : (byte) 0);
        parcel.writeInt(this.textColorDisable);
        parcel.writeInt(this.backgroundColorPress);
        parcel.writeInt(this.styleText);
        parcel.writeLong(this.countDownTime);
        parcel.writeLong(this.countDownInterval);
        parcel.writeString(this.countDownText);
    }
}