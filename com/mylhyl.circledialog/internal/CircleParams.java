package com.mylhyl.circledialog.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import com.mylhyl.circledialog.params.AdParams;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.CloseParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.params.ItemsParams;
import com.mylhyl.circledialog.params.LottieParams;
import com.mylhyl.circledialog.params.ProgressParams;
import com.mylhyl.circledialog.params.SubTitleParams;
import com.mylhyl.circledialog.params.TextParams;
import com.mylhyl.circledialog.params.TitleParams;

/* loaded from: classes.dex */
public class CircleParams implements Parcelable {
    public static final Parcelable.Creator<CircleParams> CREATOR = new Parcelable.Creator<CircleParams>() { // from class: com.mylhyl.circledialog.internal.CircleParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CircleParams createFromParcel(Parcel parcel) {
            return new CircleParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CircleParams[] newArray(int i) {
            return new CircleParams[i];
        }
    };
    public AdParams adParams;
    public View bodyView;
    public int bodyViewId;
    public CircleListeners circleListeners = new CircleListeners();
    public CloseParams closeParams;
    public DialogParams dialogParams;
    public InputParams inputParams;
    public boolean itemListViewType;
    public ItemsParams itemsParams;
    public LottieParams lottieParams;
    public ButtonParams negativeParams;
    public ButtonParams neutralParams;
    public ButtonParams positiveParams;
    public ProgressParams progressParams;
    public SubTitleParams subTitleParams;
    public TextParams textParams;
    public TitleParams titleParams;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CircleParams() {
    }

    protected CircleParams(Parcel parcel) {
        this.dialogParams = (DialogParams) parcel.readParcelable(DialogParams.class.getClassLoader());
        this.titleParams = (TitleParams) parcel.readParcelable(TitleParams.class.getClassLoader());
        this.subTitleParams = (SubTitleParams) parcel.readParcelable(SubTitleParams.class.getClassLoader());
        this.textParams = (TextParams) parcel.readParcelable(TextParams.class.getClassLoader());
        this.negativeParams = (ButtonParams) parcel.readParcelable(ButtonParams.class.getClassLoader());
        this.positiveParams = (ButtonParams) parcel.readParcelable(ButtonParams.class.getClassLoader());
        this.itemsParams = (ItemsParams) parcel.readParcelable(ItemsParams.class.getClassLoader());
        this.progressParams = (ProgressParams) parcel.readParcelable(ProgressParams.class.getClassLoader());
        this.lottieParams = (LottieParams) parcel.readParcelable(LottieParams.class.getClassLoader());
        this.inputParams = (InputParams) parcel.readParcelable(InputParams.class.getClassLoader());
        this.neutralParams = (ButtonParams) parcel.readParcelable(ButtonParams.class.getClassLoader());
        this.bodyViewId = parcel.readInt();
        this.itemListViewType = parcel.readByte() != 0;
        this.closeParams = (CloseParams) parcel.readParcelable(CloseParams.class.getClassLoader());
        this.adParams = (AdParams) parcel.readParcelable(AdParams.class.getClassLoader());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.dialogParams, i);
        parcel.writeParcelable(this.titleParams, i);
        parcel.writeParcelable(this.subTitleParams, i);
        parcel.writeParcelable(this.textParams, i);
        parcel.writeParcelable(this.negativeParams, i);
        parcel.writeParcelable(this.positiveParams, i);
        parcel.writeParcelable(this.itemsParams, i);
        parcel.writeParcelable(this.progressParams, i);
        parcel.writeParcelable(this.lottieParams, i);
        parcel.writeParcelable(this.inputParams, i);
        parcel.writeParcelable(this.neutralParams, i);
        parcel.writeInt(this.bodyViewId);
        parcel.writeByte(this.itemListViewType ? (byte) 1 : (byte) 0);
        parcel.writeParcelable(this.closeParams, i);
        parcel.writeParcelable(this.adParams, i);
    }
}