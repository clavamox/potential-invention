package com.wy.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.xzf.camera.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class VedioAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<String> dataList;
    private boolean edit;
    private ItemClickListener itemClickListener;
    private int itemWidth;
    private List<String> filePaths = new ArrayList();
    private boolean circle = false;
    public final int TYPE_EMPTY = 0;
    public final int TYPE_NORMAL = 1;
    private Set<String> selectedDataList = new HashSet();

    public interface ItemClickListener {
        void onItemClick(int i, String str, boolean z);
    }

    public VedioAdapter(Context context, List<String> list, int i) {
        this.itemWidth = 0;
        this.context = context;
        this.dataList = list;
        this.itemWidth = i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return this.dataList.size() <= 0 ? 0 : 1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_album_empty, viewGroup, false)) { // from class: com.wy.ui.adapter.VedioAdapter.1
            };
        }
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.item_album_vedio, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof ViewHolder) {
            final ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            final String str = this.dataList.get(i);
            RequestBuilder<Drawable> load = Glide.with(this.context).load(str);
            int i2 = this.itemWidth;
            load.override(i2, i2).into(viewHolder2.imageview);
            int localVideoDuration = getLocalVideoDuration(str);
            Log.d("VedioAdapter", "duration:" + localVideoDuration + "  filePath:" + str);
            if (isEdit()) {
                viewHolder2.ivDel.setVisibility(0);
                viewHolder2.ivPlay.setVisibility(4);
                viewHolder2.tvDuration.setVisibility(4);
                if (this.selectedDataList.contains(this.dataList.get(i))) {
                    viewHolder2.ivDel.setImageResource(R.mipmap.del_checked);
                } else {
                    viewHolder2.ivDel.setImageResource(R.mipmap.del_unchecked);
                }
            } else {
                viewHolder2.ivDel.setVisibility(4);
                viewHolder2.ivPlay.setVisibility(0);
                viewHolder2.tvDuration.setVisibility(0);
                viewHolder2.tvDuration.setText(durationFormat(Integer.valueOf(localVideoDuration)));
            }
            viewHolder2.layout.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.adapter.VedioAdapter.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (VedioAdapter.this.itemClickListener != null) {
                        if (VedioAdapter.this.edit) {
                            if (!VedioAdapter.this.selectedDataList.contains(VedioAdapter.this.dataList.get(i))) {
                                VedioAdapter.this.selectedDataList.add((String) VedioAdapter.this.dataList.get(i));
                                viewHolder2.ivDel.setImageResource(R.mipmap.del_checked);
                                return;
                            } else {
                                VedioAdapter.this.selectedDataList.remove(VedioAdapter.this.dataList.get(i));
                                viewHolder2.ivDel.setImageResource(R.mipmap.del_unchecked);
                                return;
                            }
                        }
                        VedioAdapter.this.itemClickListener.onItemClick(i, str, VedioAdapter.this.edit);
                    }
                }
            });
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (this.dataList.size() <= 0) {
            return 1;
        }
        return this.dataList.size();
    }

    public int getLocalVideoDuration(String str) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(str);
            mediaPlayer.prepare();
            return mediaPlayer.getDuration() / 1000;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getLocalVideoDuration2(String str) {
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(str);
            int parseInt = Integer.parseInt(mediaMetadataRetriever.extractMetadata(9)) / 1000;
            mediaMetadataRetriever.extractMetadata(18);
            mediaMetadataRetriever.extractMetadata(19);
            return parseInt;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        ImageView ivDel;
        ImageView ivPlay;
        ConstraintLayout layout;
        TextView tvDuration;

        public ViewHolder(View view) {
            super(view);
            this.layout = (ConstraintLayout) view.findViewById(R.id.layout);
            this.imageview = (ImageView) view.findViewById(R.id.iv_vedio);
            this.ivDel = (ImageView) view.findViewById(R.id.iv_del);
            this.ivPlay = (ImageView) view.findViewById(R.id.iv_play);
            this.tvDuration = (TextView) view.findViewById(R.id.tv_duration);
        }
    }

    public void removeSelected() {
        this.selectedDataList.clear();
    }

    public ItemClickListener getItemClickListener() {
        return this.itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public boolean isCircle() {
        return this.circle;
    }

    public void setCircle(boolean z) {
        this.circle = z;
    }

    public Set<String> getSelectedDataList() {
        return this.selectedDataList;
    }

    public boolean isEdit() {
        return this.edit;
    }

    public void setEdit(boolean z) {
        this.edit = z;
        this.selectedDataList.clear();
        notifyDataSetChanged();
    }

    public List<String> getDataList() {
        return this.dataList;
    }

    public String durationFormat(Integer num) {
        int intValue = num.intValue() / 3600;
        int intValue2 = num.intValue() % 3600;
        return String.format("%02d:%02d:%02d", Integer.valueOf(intValue), Integer.valueOf(intValue2 / 60), Integer.valueOf(intValue2 % 60));
    }
}