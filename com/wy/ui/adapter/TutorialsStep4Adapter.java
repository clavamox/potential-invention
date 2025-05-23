package com.wy.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.xzf.camera.R;
import java.util.List;

/* loaded from: classes.dex */
public class TutorialsStep4Adapter extends RecyclerView.Adapter {
    List<String> dataLists;

    public TutorialsStep4Adapter(List<String> list) {
        this.dataLists = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tutorials_step4, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ((ViewHolder) viewHolder).tvDetail.setText(this.dataLists.get(i));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.dataLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDetail;

        public ViewHolder(View view) {
            super(view);
            this.tvDetail = (TextView) view.findViewById(R.id.tv_detail);
        }
    }
}