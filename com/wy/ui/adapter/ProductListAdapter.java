package com.wy.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private int[][] icons;
    private OnClickProductItemListener listener;
    private String[] titles;

    public interface OnClickProductItemListener {
        void onClick(int i, int i2);
    }

    public boolean isHeader(int i) {
        return i == 0 || i == 4 || i == 6 || i == 8;
    }

    public ProductListAdapter(String[] strArr, int[][] iArr, OnClickProductItemListener onClickProductItemListener) {
        this.titles = strArr;
        this.icons = iArr;
        this.listener = onClickProductItemListener;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return !isHeader(i) ? 1 : 0;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            return new HeaderHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product_header, viewGroup, false));
        }
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        char c = 2;
        if (i == 0 || i == 4 || i == 6 || i == 8) {
            TextView textView = ((HeaderHolder) viewHolder).tv_header;
            String[] strArr = this.titles;
            if (i == 0) {
                c = 0;
            } else if (i == 4) {
                c = 1;
            } else if (i != 6) {
                c = 3;
            }
            textView.setText(strArr[c]);
            return;
        }
        ItemHolder itemHolder = (ItemHolder) viewHolder;
        if (i > 0 && i < 4) {
            int i2 = i - 1;
            itemHolder.imageView.setImageResource(this.icons[0][i2]);
            itemHolder.imageView.setTag(Integer.valueOf(i2 | 0));
        }
        if (i == 5) {
            itemHolder.imageView.setImageResource(this.icons[1][0]);
            itemHolder.imageView.setTag(65536);
        }
        if (i == 7) {
            itemHolder.imageView.setImageResource(this.icons[2][0]);
            itemHolder.imageView.setTag(131072);
        }
        if (i == 9) {
            itemHolder.imageView.setImageResource(this.icons[3][0]);
            itemHolder.imageView.setTag(196608);
        }
        itemHolder.imageView.setOnClickListener(this);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        int length = this.titles.length;
        int i = 0;
        while (true) {
            int[][] iArr = this.icons;
            if (i >= iArr.length) {
                return length;
            }
            length += iArr[i].length;
            i++;
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        int i = (intValue >> 16) & 65535;
        int i2 = intValue & 65535;
        OnClickProductItemListener onClickProductItemListener = this.listener;
        if (onClickProductItemListener != null) {
            onClickProductItemListener.onClick(i, i2);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ItemHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.imageview);
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView tv_header;

        public HeaderHolder(View view) {
            super(view);
            this.tv_header = (TextView) view.findViewById(R.id.tv_header);
        }
    }
}