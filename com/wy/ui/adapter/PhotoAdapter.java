package com.wy.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.xzf.camera.R;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class PhotoAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<String> dataList;
    private boolean edit;
    private ItemClickListener itemClickListener;
    private int itemWidth;
    private Set<String> selectedDataList = new HashSet();

    public interface ItemClickListener {
        void onItemClick(int i, String str, boolean z);
    }

    public PhotoAdapter(Context context, List<String> list, int i) {
        this.context = context;
        this.dataList = list;
        this.itemWidth = i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (this.dataList.size() == 0) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_album_empty, viewGroup, false)) { // from class: com.wy.ui.adapter.PhotoAdapter.1
            };
        }
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_album_photo, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        final ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        final String str = this.dataList.get(i);
        RequestBuilder<Drawable> load = Glide.with(this.context).load(str);
        int i2 = this.itemWidth;
        load.override(i2, i2).into(viewHolder2.imageview);
        if (isEdit()) {
            viewHolder2.ivDel.setVisibility(0);
            if (this.selectedDataList.contains(this.dataList.get(i))) {
                viewHolder2.ivDel.setImageResource(R.mipmap.del_checked);
            } else {
                viewHolder2.ivDel.setImageResource(R.mipmap.del_unchecked);
            }
        } else {
            viewHolder2.ivDel.setVisibility(4);
        }
        viewHolder2.layout.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.adapter.PhotoAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (PhotoAdapter.this.itemClickListener != null) {
                    if (PhotoAdapter.this.edit) {
                        if (!PhotoAdapter.this.selectedDataList.contains(PhotoAdapter.this.dataList.get(i))) {
                            PhotoAdapter.this.selectedDataList.add((String) PhotoAdapter.this.dataList.get(i));
                            viewHolder2.ivDel.setImageResource(R.mipmap.del_checked);
                            return;
                        } else {
                            PhotoAdapter.this.selectedDataList.remove(PhotoAdapter.this.dataList.get(i));
                            viewHolder2.ivDel.setImageResource(R.mipmap.del_unchecked);
                            return;
                        }
                    }
                    PhotoAdapter.this.itemClickListener.onItemClick(i, str, PhotoAdapter.this.edit);
                }
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.dataList.size();
    }

    public void removeSelected() {
        this.selectedDataList.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        ImageView ivDel;
        ConstraintLayout layout;

        public ViewHolder(View view) {
            super(view);
            this.layout = (ConstraintLayout) view.findViewById(R.id.layout);
            this.imageview = (ImageView) view.findViewById(R.id.iv_vedio);
            this.ivDel = (ImageView) view.findViewById(R.id.iv_del);
        }
    }

    public ItemClickListener getItemClickListener() {
        return this.itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Set<String> getSelectedDataList() {
        return this.selectedDataList;
    }

    public List<String> getDataList() {
        return this.dataList;
    }

    public boolean isEdit() {
        return this.edit;
    }

    public void setEdit(boolean z) {
        this.edit = z;
        this.selectedDataList.clear();
        notifyDataSetChanged();
    }
}