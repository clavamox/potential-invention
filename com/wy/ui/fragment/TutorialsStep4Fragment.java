package com.wy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.wy.ui.MarginDecoration;
import com.wy.ui.adapter.TutorialsStep4Adapter;
import com.xzf.camera.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TutorialsStep4Fragment extends Fragment {
    List<String> dataList = new ArrayList();
    RecyclerView recyclerView;

    public static TutorialsStep4Fragment newInstance() {
        return new TutorialsStep4Fragment();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_tutorials_step4, viewGroup, false);
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
        this.recyclerView.setAdapter(new TutorialsStep4Adapter(this.dataList));
    }

    private void initData() {
        this.dataList.clear();
        this.dataList.add(getString(R.string.tutorial_step4_1_detail));
        this.dataList.add(getString(R.string.tutorial_step4_2_detail));
        this.dataList.add(getString(R.string.tutorial_step4_3_detail));
        this.dataList.add(getString(R.string.tutorial_step4_4_detail));
    }
}