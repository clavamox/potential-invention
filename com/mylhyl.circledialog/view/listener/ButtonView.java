package com.mylhyl.circledialog.view.listener;

import android.view.View;

/* loaded from: classes.dex */
public interface ButtonView {
    View getView();

    boolean isEmpty();

    void refreshText();

    void regNegativeListener(View.OnClickListener onClickListener);

    void regNeutralListener(View.OnClickListener onClickListener);

    void regPositiveListener(View.OnClickListener onClickListener);

    void timerCancel();

    void timerRestart();
}