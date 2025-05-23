package com.wy.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class LanguageAdapter extends RecyclerView.Adapter {
    private int[] icons;
    private OnClickLanguageListener listener;
    private String[] titles;

    public interface OnClickLanguageListener {
        void onClick(int i);
    }

    public LanguageAdapter(String[] strArr, int[] iArr, OnClickLanguageListener onClickLanguageListener) {
        this.titles = strArr;
        this.icons = iArr;
        this.listener = onClickLanguageListener;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_language, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        viewHolder2.tvIcon.setImageResource(this.icons[i]);
        viewHolder2.tvTitle.setText(this.titles[i]);
        viewHolder2.linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.adapter.LanguageAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                LanguageAdapter.this.m85lambda$onBindViewHolder$0$comwyuiadapterLanguageAdapter(i, view);
            }
        });
    }

    /* renamed from: lambda$onBindViewHolder$0$com-wy-ui-adapter-LanguageAdapter, reason: not valid java name */
    /* synthetic */ void m85lambda$onBindViewHolder$0$comwyuiadapterLanguageAdapter(int i, View view) {
        Log.d("LanguageAdapter", "position:" + i);
        OnClickLanguageListener onClickLanguageListener = this.listener;
        if (onClickLanguageListener != null) {
            onClickLanguageListener.onClick(i);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.titles.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private ImageView tvIcon;
        private TextView tvTitle;

        public ViewHolder(View view) {
            super(view);
            this.tvIcon = (ImageView) view.findViewById(R.id.ic_icon);
            this.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.ll_item_layout);
        }
    }
}