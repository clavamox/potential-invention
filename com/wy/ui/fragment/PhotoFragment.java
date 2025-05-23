package com.wy.ui.fragment;

import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.wy.listener.AlbumItemClickListener;
import com.wy.ui.adapter.PhotoAdapter;
import com.xzf.camera.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class PhotoFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    AlbumItemClickListener albumItemClickListener;
    GridLayoutManager gridLayoutManager;
    private PhotoAdapter photoAdapter;
    private String photoPath;
    RecyclerView rvPhoto;
    private List<String> filePaths = new ArrayList();
    private final int column = 2;
    private final int horizontalMargin = 60;
    private int itemWidth = 0;

    public List<String> getFilePaths() {
        return this.filePaths;
    }

    public static PhotoFragment newInstance(String str) {
        PhotoFragment photoFragment = new PhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        photoFragment.setArguments(bundle);
        return photoFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.photoPath = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_photo, viewGroup, false);
        this.rvPhoto = (RecyclerView) inflate.findViewById(R.id.rv_photo);
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
        this.gridLayoutManager = new GridLayoutManager(getContext(), 2);
        this.itemWidth = (defaultDisplay.getWidth() - 180) / 2;
        this.photoAdapter = new PhotoAdapter(getContext(), this.filePaths, this.itemWidth);
        this.rvPhoto.setHasFixedSize(false);
        this.rvPhoto.setLayoutManager(this.gridLayoutManager);
        this.rvPhoto.setAdapter(this.photoAdapter);
        new Thread(new Runnable() { // from class: com.wy.ui.fragment.PhotoFragment.1
            @Override // java.lang.Runnable
            public void run() {
                PhotoFragment.this.reloadData();
                if (PhotoFragment.this.photoAdapter.getDataList().size() == 0) {
                    PhotoFragment.this.gridLayoutManager.setSpanCount(1);
                } else {
                    PhotoFragment.this.gridLayoutManager.setSpanCount(2);
                }
                PhotoFragment.this.photoAdapter.notifyDataSetChanged();
            }
        }).start();
        this.photoAdapter.setItemClickListener(new PhotoAdapter.ItemClickListener() { // from class: com.wy.ui.fragment.PhotoFragment.2
            @Override // com.wy.ui.adapter.PhotoAdapter.ItemClickListener
            public void onItemClick(int i, String str, boolean z) {
                if (PhotoFragment.this.albumItemClickListener != null) {
                    PhotoFragment.this.albumItemClickListener.onItemClick(this, i, str, z);
                }
            }
        });
    }

    public void reloadData() {
        try {
            this.filePaths.clear();
            File[] listFiles = new File(this.photoPath).listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.getAbsolutePath().endsWith(".jpg")) {
                        this.filePaths.add(file.getAbsolutePath());
                    }
                }
            }
            if (this.rvPhoto.isComputingLayout()) {
                new Thread(new Runnable() { // from class: com.wy.ui.fragment.PhotoFragment.3
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            Thread.sleep(100L);
                            PhotoFragment.this.photoAdapter.notifyDataSetChanged();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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

    public void setAlbumItemClickListener(AlbumItemClickListener albumItemClickListener) {
        this.albumItemClickListener = albumItemClickListener;
    }
}