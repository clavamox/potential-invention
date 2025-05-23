package com.wy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class TutorialsStep3Fragment extends Fragment {
    public static TutorialsStep3Fragment newInstance() {
        return new TutorialsStep3Fragment();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_tutorials_step3, viewGroup, false);
    }
}