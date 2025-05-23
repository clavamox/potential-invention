package com.mylhyl.circledialog.internal;

import android.view.View;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.CloseView;

/* loaded from: classes.dex */
public interface BuildView {
    void buildBodyView();

    ButtonView buildButton();

    CloseView buildCloseImgView();

    void buildRootView();

    void buildTitleView();

    <T> T getBodyView();

    View getRootView();

    void refreshButton();

    void refreshContent();

    void refreshTitle();
}