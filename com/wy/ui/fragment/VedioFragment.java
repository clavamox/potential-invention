package com.wy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import com.wy.listener.AlbumItemClickListener;
import com.wy.ui.adapter.VedioAdapter;
import com.xzf.camera.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class VedioFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private AlbumItemClickListener albumItemClickListener;
    GridLayoutManager gridLayoutManager;
    VedioAdapter photoAdapter;
    RecyclerView rvPhoto;
    String vedioPath = "";
    private final int column = 2;
    private final int horizontalMargin = 20;
    private int itemWidth = 0;
    List<String> filePaths = new ArrayList();

    public static VedioFragment newInstance(String str) {
        VedioFragment vedioFragment = new VedioFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        vedioFragment.setArguments(bundle);
        return vedioFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.vedioPath = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_vedio, viewGroup, false);
        this.rvPhoto = (RecyclerView) inflate.findViewById(R.id.rv_photo);
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ButterKnife.bind(view);
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        this.gridLayoutManager = new GridLayoutManager(getContext(), 2);
        this.itemWidth = (width - 60) / 2;
        VedioAdapter vedioAdapter = new VedioAdapter(getContext(), this.filePaths, this.itemWidth);
        this.photoAdapter = vedioAdapter;
        vedioAdapter.setCircle(true);
        this.rvPhoto.setHasFixedSize(false);
        this.rvPhoto.setLayoutManager(this.gridLayoutManager);
        this.rvPhoto.setAdapter(this.photoAdapter);
        new Thread(new Runnable() { // from class: com.wy.ui.fragment.VedioFragment.1
            @Override // java.lang.Runnable
            public void run() {
                VedioFragment.this.reloadData();
                if (VedioFragment.this.photoAdapter.getDataList().size() == 0) {
                    VedioFragment.this.gridLayoutManager.setSpanCount(1);
                } else {
                    VedioFragment.this.gridLayoutManager.setSpanCount(2);
                }
                VedioFragment.this.photoAdapter.notifyDataSetChanged();
            }
        }).start();
        this.photoAdapter.setItemClickListener(new VedioAdapter.ItemClickListener() { // from class: com.wy.ui.fragment.VedioFragment.2
            @Override // com.wy.ui.adapter.VedioAdapter.ItemClickListener
            public void onItemClick(int i, String str, boolean z) {
                if (VedioFragment.this.albumItemClickListener != null) {
                    VedioFragment.this.albumItemClickListener.onItemClick(this, i, str, z);
                }
            }
        });
    }

    public void setAlbumItemClickListener(AlbumItemClickListener albumItemClickListener) {
        this.albumItemClickListener = albumItemClickListener;
    }

    public void reloadData() {
        try {
            this.filePaths.clear();
            File[] listFiles = new File(this.vedioPath).listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.getAbsolutePath().endsWith(".mp4")) {
                        this.filePaths.add(file.getAbsolutePath());
                    }
                }
            }
            if (this.rvPhoto.isComputingLayout()) {
                new Thread(new Runnable() { // from class: com.wy.ui.fragment.VedioFragment.3
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        VedioFragment.this.photoAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                this.photoAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeSelected() {
        this.photoAdapter.removeSelected();
        reloadData();
        if (this.photoAdapter.getDataList().size() == 0) {
            this.gridLayoutManager.setSpanCount(1);
        } else {
            this.gridLayoutManager.setSpanCount(2);
        }
        this.photoAdapter.notifyDataSetChanged();
    }

    public void setEdit(boolean z) {
        this.photoAdapter.setEdit(z);
    }

    public Set<String> getSelectedDataListlected() {
        return this.photoAdapter.getSelectedDataList();
    }
}