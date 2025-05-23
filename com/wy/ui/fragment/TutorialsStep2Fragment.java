package com.wy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.wy.bean.TutorialsStep2Mode;
import com.wy.ui.MarginDecoration;
import com.wy.ui.adapter.TutorialsStep2Adapter;
import com.xzf.camera.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TutorialsStep2Fragment extends Fragment {
    List<TutorialsStep2Mode> dataList = new ArrayList();
    RecyclerView recyclerView;

    public static TutorialsStep2Fragment newInstance() {
        return new TutorialsStep2Fragment();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_tutorials_step2, viewGroup, false);
        this.recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerView);
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initData();
        this.recyclerView.addItemDecoration(new MarginDecoration(getContext()));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(new TutorialsStep2Adapter(this.dataList));
    }

    private void initData() {
        this.dataList.clear();
        this.dataList.add(new TutorialsStep2Mode(getString(R.string.tutorial_step2_1_title), getString(R.string.tutorial_step2_1_detail), R.mipmap.tutorials_step2_1));
        this.dataList.add(new TutorialsStep2Mode(getString(R.string.tutorial_step2_2_title), getString(R.string.tutorial_step2_2_detail), R.mipmap.tutorials_step2_1));
        this.dataList.add(new TutorialsStep2Mode(getString(R.string.tutorial_step2_3_title), getString(R.string.tutorial_step2_3_detail), R.mipmap.tutorials_step2_1));
        this.dataList.add(new TutorialsStep2Mode(getString(R.string.tutorial_step2_4_title), getString(R.string.tutorial_step2_4_detail), R.mipmap.tutorials_step2_1));
    }
}