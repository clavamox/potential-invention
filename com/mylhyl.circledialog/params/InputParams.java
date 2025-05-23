package com.mylhyl.circledialog.params;

import android.os.Parcel;
import android.os.Parcelable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;

/* loaded from: classes.dex */
public class InputParams implements Parcelable {
    public static final Parcelable.Creator<InputParams> CREATOR = new Parcelable.Creator<InputParams>() { // from class: com.mylhyl.circledialog.params.InputParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InputParams createFromParcel(Parcel parcel) {
            return new InputParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public InputParams[] newArray(int i) {
            return new InputParams[i];
        }
    };
    public int backgroundColor;
    public int counterColor;
    public int[] counterMargins;
    public int gravity;
    public String hintText;
    public int hintTextColor;
    public int inputBackgroundColor;
    public int inputBackgroundResourceId;
    public int inputHeight;
    public int inputType;

    @Deprecated
    public boolean isCounterAllEn;
    public boolean isEmojiInput;
    public int[] margins;
    public int maxLen;
    public int maxLengthType;
    public int[] padding;
    public boolean showSoftKeyboard;
    public int strokeColor;
    public int strokeWidth;
    public int styleText;
    public String text;
    public int textColor;
    public int textSize;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public InputParams() {
        this.margins = CircleDimen.INPUT_MARGINS;
        this.inputHeight = CircleDimen.INPUT_HEIGHT;
        this.hintTextColor = CircleColor.INPUT_TEXT_HINT;
        this.strokeWidth = 1;
        this.strokeColor = CircleColor.INPUT_STROKE;
        this.textSize = CircleDimen.INPUT_TEXT_SIZE;
        this.textColor = CircleColor.INPUT_TEXT;
        this.inputType = 0;
        this.gravity = 51;
        this.padding = CircleDimen.INPUT_PADDING;
        this.styleText = 0;
        this.counterMargins = CircleDimen.INPUT_COUNTER_MARGINS;
        this.counterColor = CircleColor.INPUT_COUNTER_TEXT;
    }

    protected InputParams(Parcel parcel) {
        this.margins = CircleDimen.INPUT_MARGINS;
        this.inputHeight = CircleDimen.INPUT_HEIGHT;
        this.hintTextColor = CircleColor.INPUT_TEXT_HINT;
        this.strokeWidth = 1;
        this.strokeColor = CircleColor.INPUT_STROKE;
        this.textSize = CircleDimen.INPUT_TEXT_SIZE;
        this.textColor = CircleColor.INPUT_TEXT;
        this.inputType = 0;
        this.gravity = 51;
        this.padding = CircleDimen.INPUT_PADDING;
        this.styleText = 0;
        this.counterMargins = CircleDimen.INPUT_COUNTER_MARGINS;
        this.counterColor = CircleColor.INPUT_COUNTER_TEXT;
        this.margins = parcel.createIntArray();
        this.inputHeight = parcel.readInt();
        this.hintText = parcel.readString();
        this.hintTextColor = parcel.readInt();
        this.inputBackgroundResourceId = parcel.readInt();
        this.strokeWidth = parcel.readInt();
        this.strokeColor = parcel.readInt();
        this.inputBackgroundColor = parcel.readInt();
        this.backgroundColor = parcel.readInt();
        this.textSize = parcel.readInt();
        this.textColor = parcel.readInt();
        this.inputType = parcel.readInt();
        this.gravity = parcel.readInt();
        this.text = parcel.readString();
        this.padding = parcel.createIntArray();
        this.styleText = parcel.readInt();
        this.maxLen = parcel.readInt();
        this.counterMargins = parcel.createIntArray();
        this.counterColor = parcel.readInt();
        this.showSoftKeyboard = parcel.readByte() != 0;
        this.isEmojiInput = parcel.readByte() != 0;
        this.isCounterAllEn = parcel.readByte() != 0;
        this.maxLengthType = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(this.margins);
        parcel.writeInt(this.inputHeight);
        parcel.writeString(this.hintText);
        parcel.writeInt(this.hintTextColor);
        parcel.writeInt(this.inputBackgroundResourceId);
        parcel.writeInt(this.strokeWidth);
        parcel.writeInt(this.strokeColor);
        parcel.writeInt(this.inputBackgroundColor);
        parcel.writeInt(this.backgroundColor);
        parcel.writeInt(this.textSize);
        parcel.writeInt(this.textColor);
        parcel.writeInt(this.inputType);
        parcel.writeInt(this.gravity);
        parcel.writeString(this.text);
        parcel.writeIntArray(this.padding);
        parcel.writeInt(this.styleText);
        parcel.writeInt(this.maxLen);
        parcel.writeIntArray(this.counterMargins);
        parcel.writeInt(this.counterColor);
        parcel.writeByte(this.showSoftKeyboard ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.isEmojiInput ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.isCounterAllEn ? (byte) 1 : (byte) 0);
        parcel.writeInt(this.maxLengthType);
    }
}