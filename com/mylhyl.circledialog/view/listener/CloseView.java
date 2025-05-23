package com.mylhyl.circledialog.view.listener;

import android.view.View;

/* loaded from: classes.dex */
public interface CloseView {
    View getView();

    void regOnCloseClickListener(View.OnClickListener onClickListener);
}