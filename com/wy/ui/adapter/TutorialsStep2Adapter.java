package com.wy.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.wy.bean.TutorialsStep2Mode;
import com.xzf.camera.R;
import java.util.List;

/* loaded from: classes.dex */
public class TutorialsStep2Adapter extends RecyclerView.Adapter {
    List<TutorialsStep2Mode> dataLists;

    public TutorialsStep2Adapter(List<TutorialsStep2Mode> list) {
        this.dataLists = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tutorials_step2, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        TutorialsStep2Mode tutorialsStep2Mode = this.dataLists.get(i);
        viewHolder2.tvIcon.setImageResource(tutorialsStep2Mode.getProductImage());
        viewHolder2.tvTitle.setText(tutorialsStep2Mode.getProductName());
        viewHolder2.tvDetail.setText(tutorialsStep2Mode.getProductDeail());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        Log.d("TutorialsStep2Adapter", "dataLists.size===>>>():" + this.dataLists.size());
        return this.dataLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDetail;
        private ImageView tvIcon;
        private TextView tvTitle;

        public ViewHolder(View view) {
            super(view);
            this.tvIcon = (ImageView) view.findViewById(R.id.ic_icon);
            this.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            this.tvDetail = (TextView) view.findViewById(R.id.tv_detail);
        }
    }
}