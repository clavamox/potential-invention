package com.mylhyl.circledialog.params;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.BaseAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.mylhyl.circledialog.callback.CircleItemViewBinder;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;

/* loaded from: classes.dex */
public class ItemsParams implements Parcelable {
    public static final Parcelable.Creator<ItemsParams> CREATOR = new Parcelable.Creator<ItemsParams>() { // from class: com.mylhyl.circledialog.params.ItemsParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ItemsParams createFromParcel(Parcel parcel) {
            return new ItemsParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ItemsParams[] newArray(int i) {
            return new ItemsParams[i];
        }
    };
    public BaseAdapter adapter;
    public RecyclerView.Adapter adapterRv;
    public int backgroundColor;
    public int backgroundColorPress;
    public int bottomMargin;
    public int dividerHeight;
    public RecyclerView.ItemDecoration itemDecoration;
    public int itemHeight;
    public Object items;
    public RecyclerView.LayoutManager layoutManager;
    public int linearLayoutManagerOrientation;
    public int[] padding;
    public int textColor;
    public int textGravity;
    public int textSize;
    public CircleItemViewBinder viewBinder;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ItemsParams() {
        this.itemHeight = CircleDimen.ITEM_HEIGHT;
        this.dividerHeight = 1;
        this.textColor = CircleColor.ITEM_CONTENT_TEXT;
        this.textSize = CircleDimen.ITEM_CONTENT_TEXT_SIZE;
        this.linearLayoutManagerOrientation = 1;
        this.bottomMargin = CircleDimen.BUTTON_ITEMS_MARGIN;
        this.textGravity = 0;
    }

    protected ItemsParams(Parcel parcel) {
        this.itemHeight = CircleDimen.ITEM_HEIGHT;
        this.dividerHeight = 1;
        this.textColor = CircleColor.ITEM_CONTENT_TEXT;
        this.textSize = CircleDimen.ITEM_CONTENT_TEXT_SIZE;
        this.linearLayoutManagerOrientation = 1;
        this.bottomMargin = CircleDimen.BUTTON_ITEMS_MARGIN;
        this.textGravity = 0;
        this.itemHeight = parcel.readInt();
        this.dividerHeight = parcel.readInt();
        this.padding = parcel.createIntArray();
        this.backgroundColor = parcel.readInt();
        this.textColor = parcel.readInt();
        this.textSize = parcel.readInt();
        this.backgroundColorPress = parcel.readInt();
        this.linearLayoutManagerOrientation = parcel.readInt();
        this.bottomMargin = parcel.readInt();
        this.textGravity = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.itemHeight);
        parcel.writeInt(this.dividerHeight);
        parcel.writeIntArray(this.padding);
        parcel.writeInt(this.backgroundColor);
        parcel.writeInt(this.textColor);
        parcel.writeInt(this.textSize);
        parcel.writeInt(this.backgroundColorPress);
        parcel.writeInt(this.linearLayoutManagerOrientation);
        parcel.writeInt(this.bottomMargin);
        parcel.writeInt(this.textGravity);
    }
}