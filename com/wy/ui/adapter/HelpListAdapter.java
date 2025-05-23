package com.wy.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class HelpListAdapter extends RecyclerView.Adapter<ItemHolder> implements View.OnClickListener {
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private int[] icons;
    private OnClickHelpListListener listener;
    private String[] titles;

    public interface OnClickHelpListListener {
        void onClick(int i);
    }

    public HelpListAdapter(String[] strArr, int[] iArr, OnClickHelpListListener onClickHelpListListener) {
        this.titles = strArr;
        this.icons = iArr;
        this.listener = onClickHelpListListener;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_help, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        itemHolder.imageView.setImageResource(this.icons[i]);
        itemHolder.textView.setText(this.titles[i]);
        itemHolder.layout.setTag(Integer.valueOf(i));
        itemHolder.layout.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.adapter.HelpListAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                HelpListAdapter.this.onClick(view);
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.titles.length;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        OnClickHelpListListener onClickHelpListListener = this.listener;
        if (onClickHelpListListener != null) {
            onClickHelpListListener.onClick(intValue);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private RelativeLayout layout;
        private TextView textView;

        public ItemHolder(View view) {
            super(view);
            this.layout = (RelativeLayout) view.findViewById(R.id.layout);
            this.imageView = (ImageView) view.findViewById(R.id.imageview);
            this.textView = (TextView) view.findViewById(R.id.textView);
        }
    }
}