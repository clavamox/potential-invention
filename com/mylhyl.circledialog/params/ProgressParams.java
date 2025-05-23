package com.mylhyl.circledialog.params;

import android.os.Parcel;
import android.os.Parcelable;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;

/* loaded from: classes.dex */
public class ProgressParams implements Parcelable {
    public static final Parcelable.Creator<ProgressParams> CREATOR = new Parcelable.Creator<ProgressParams>() { // from class: com.mylhyl.circledialog.params.ProgressParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgressParams createFromParcel(Parcel parcel) {
            return new ProgressParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProgressParams[] newArray(int i) {
            return new ProgressParams[i];
        }
    };
    public static final int STYLE_HORIZONTAL = 0;
    public static final int STYLE_SPINNER = 1;
    public int backgroundColor;
    public int indeterminateColor;
    public int[] margins;
    public int max;
    public int[] padding;
    public int progress;
    public int progressDrawableId;
    public int progressHeight;
    public int style;
    public int styleText;
    public String text;
    public int textColor;
    public int textSize;
    public String timeoutText;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ProgressParams() {
        this.style = 0;
        this.margins = CircleDimen.PROGRESS_MARGINS;
        this.padding = CircleDimen.PROGRESS_TEXT_PADDING;
        this.text = "";
        this.textColor = CircleColor.LOADING_TEXT;
        this.textSize = CircleDimen.LOADING_TEXT_SIZE;
        this.styleText = 0;
    }

    protected ProgressParams(Parcel parcel) {
        this.style = 0;
        this.margins = CircleDimen.PROGRESS_MARGINS;
        this.padding = CircleDimen.PROGRESS_TEXT_PADDING;
        this.text = "";
        this.textColor = CircleColor.LOADING_TEXT;
        this.textSize = CircleDimen.LOADING_TEXT_SIZE;
        this.styleText = 0;
        this.style = parcel.readInt();
        this.margins = parcel.createIntArray();
        this.padding = parcel.createIntArray();
        this.progressDrawableId = parcel.readInt();
        this.progressHeight = parcel.readInt();
        this.max = parcel.readInt();
        this.progress = parcel.readInt();
        this.text = parcel.readString();
        this.backgroundColor = parcel.readInt();
        this.textColor = parcel.readInt();
        this.textSize = parcel.readInt();
        this.styleText = parcel.readInt();
        this.indeterminateColor = parcel.readInt();
        this.timeoutText = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.style);
        parcel.writeIntArray(this.margins);
        parcel.writeIntArray(this.padding);
        parcel.writeInt(this.progressDrawableId);
        parcel.writeInt(this.progressHeight);
        parcel.writeInt(this.max);
        parcel.writeInt(this.progress);
        parcel.writeString(this.text);
        parcel.writeInt(this.backgroundColor);
        parcel.writeInt(this.textColor);
        parcel.writeInt(this.textSize);
        parcel.writeInt(this.styleText);
        parcel.writeInt(this.indeterminateColor);
        parcel.writeString(this.timeoutText);
    }
}