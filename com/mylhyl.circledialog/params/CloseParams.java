package com.mylhyl.circledialog.params;

import android.os.Parcel;
import android.os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes.dex */
public class CloseParams implements Parcelable {
    public static final int CLOSE_BOTTOM_CENTER = 781;
    public static final int CLOSE_BOTTOM_LEFT = 783;
    public static final int CLOSE_BOTTOM_RIGHT = 785;
    public static final int CLOSE_TOP_CENTER = 349;
    public static final int CLOSE_TOP_LEFT = 351;
    public static final int CLOSE_TOP_RIGHT = 353;
    public static final Parcelable.Creator<CloseParams> CREATOR = new Parcelable.Creator<CloseParams>() { // from class: com.mylhyl.circledialog.params.CloseParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CloseParams createFromParcel(Parcel parcel) {
            return new CloseParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CloseParams[] newArray(int i) {
            return new CloseParams[i];
        }
    };
    public int closeGravity;
    public int[] closePadding;
    public int closeResId;
    public int closeSize;
    public int connectorColor;
    public int connectorHeight;
    public int connectorWidth;

    @Retention(RetentionPolicy.SOURCE)
    public @interface CloseGravity {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CloseParams() {
        this.closeGravity = CLOSE_TOP_RIGHT;
        this.connectorColor = -1;
    }

    protected CloseParams(Parcel parcel) {
        this.closeGravity = CLOSE_TOP_RIGHT;
        this.connectorColor = -1;
        this.closeResId = parcel.readInt();
        this.closeSize = parcel.readInt();
        this.closePadding = parcel.createIntArray();
        this.closeGravity = parcel.readInt();
        this.connectorWidth = parcel.readInt();
        this.connectorHeight = parcel.readInt();
        this.connectorColor = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.closeResId);
        parcel.writeInt(this.closeSize);
        parcel.writeIntArray(this.closePadding);
        parcel.writeInt(this.closeGravity);
        parcel.writeInt(this.connectorWidth);
        parcel.writeInt(this.connectorHeight);
        parcel.writeInt(this.connectorColor);
    }
}