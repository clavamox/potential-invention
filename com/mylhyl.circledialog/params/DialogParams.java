package com.mylhyl.circledialog.params;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;

/* loaded from: classes.dex */
public class DialogParams implements Parcelable {
    public static final Parcelable.Creator<DialogParams> CREATOR = new Parcelable.Creator<DialogParams>() { // from class: com.mylhyl.circledialog.params.DialogParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DialogParams createFromParcel(Parcel parcel) {
            return new DialogParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DialogParams[] newArray(int i) {
            return new DialogParams[i];
        }
    };
    public float alpha;
    public int animStyle;
    public int backgroundColor;
    public int backgroundColorPress;
    public boolean cancelable;
    public boolean canceledOnTouchOutside;
    public int delayShow;
    public float dimAmount;
    public int gravity;
    public boolean isDimEnabled;
    public int[] mPadding;
    public boolean manualClose;
    public float maxHeight;
    public int radius;
    public int refreshAnimation;
    public int systemUiVisibility;
    public Typeface typeface;
    public float width;
    public int xOff;
    public int yOff;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DialogParams() {
        this.gravity = 0;
        this.canceledOnTouchOutside = true;
        this.cancelable = true;
        this.alpha = CircleDimen.DIALOG_ALPHA;
        this.width = CircleDimen.DIALOG_WIDTH;
        this.isDimEnabled = true;
        this.dimAmount = CircleDimen.DIM_AMOUNT;
        this.backgroundColor = CircleColor.DIALOG_BACKGROUND;
        this.radius = CircleDimen.DIALOG_RADIUS;
        this.yOff = -1;
        this.backgroundColorPress = CircleColor.DIALOG_BACKGROUND_PRESS;
    }

    protected DialogParams(Parcel parcel) {
        this.gravity = 0;
        this.canceledOnTouchOutside = true;
        this.cancelable = true;
        this.alpha = CircleDimen.DIALOG_ALPHA;
        this.width = CircleDimen.DIALOG_WIDTH;
        this.isDimEnabled = true;
        this.dimAmount = CircleDimen.DIM_AMOUNT;
        this.backgroundColor = CircleColor.DIALOG_BACKGROUND;
        this.radius = CircleDimen.DIALOG_RADIUS;
        this.yOff = -1;
        this.backgroundColorPress = CircleColor.DIALOG_BACKGROUND_PRESS;
        this.gravity = parcel.readInt();
        this.canceledOnTouchOutside = parcel.readByte() != 0;
        this.cancelable = parcel.readByte() != 0;
        this.alpha = parcel.readFloat();
        this.width = parcel.readFloat();
        this.mPadding = parcel.createIntArray();
        this.animStyle = parcel.readInt();
        this.refreshAnimation = parcel.readInt();
        this.isDimEnabled = parcel.readByte() != 0;
        this.dimAmount = parcel.readFloat();
        this.backgroundColor = parcel.readInt();
        this.radius = parcel.readInt();
        this.xOff = parcel.readInt();
        this.yOff = parcel.readInt();
        this.backgroundColorPress = parcel.readInt();
        this.maxHeight = parcel.readFloat();
        this.systemUiVisibility = parcel.readInt();
        this.delayShow = parcel.readInt();
        this.manualClose = parcel.readByte() != 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.gravity);
        parcel.writeByte(this.canceledOnTouchOutside ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.cancelable ? (byte) 1 : (byte) 0);
        parcel.writeFloat(this.alpha);
        parcel.writeFloat(this.width);
        parcel.writeIntArray(this.mPadding);
        parcel.writeInt(this.animStyle);
        parcel.writeInt(this.refreshAnimation);
        parcel.writeByte(this.isDimEnabled ? (byte) 1 : (byte) 0);
        parcel.writeFloat(this.dimAmount);
        parcel.writeInt(this.backgroundColor);
        parcel.writeInt(this.radius);
        parcel.writeInt(this.xOff);
        parcel.writeInt(this.yOff);
        parcel.writeInt(this.backgroundColorPress);
        parcel.writeFloat(this.maxHeight);
        parcel.writeInt(this.systemUiVisibility);
        parcel.writeInt(this.delayShow);
        parcel.writeByte(this.manualClose ? (byte) 1 : (byte) 0);
    }
}