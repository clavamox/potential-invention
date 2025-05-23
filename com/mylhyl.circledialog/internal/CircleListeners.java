package com.mylhyl.circledialog.internal;

import android.content.DialogInterface;
import android.view.View;
import com.mylhyl.circledialog.view.listener.CountDownTimerObserver;
import com.mylhyl.circledialog.view.listener.OnAdItemClickListener;
import com.mylhyl.circledialog.view.listener.OnAdPageChangeListener;
import com.mylhyl.circledialog.view.listener.OnBindBodyViewCallback;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;
import com.mylhyl.circledialog.view.listener.OnCreateButtonListener;
import com.mylhyl.circledialog.view.listener.OnCreateInputListener;
import com.mylhyl.circledialog.view.listener.OnCreateLottieListener;
import com.mylhyl.circledialog.view.listener.OnCreateProgressListener;
import com.mylhyl.circledialog.view.listener.OnCreateTextListener;
import com.mylhyl.circledialog.view.listener.OnCreateTitleListener;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.mylhyl.circledialog.view.listener.OnInputCounterChangeListener;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;
import com.mylhyl.circledialog.view.listener.OnRvItemClickListener;
import com.mylhyl.circledialog.view.listener.OnShowListener;

/* loaded from: classes.dex */
public class CircleListeners {
    public OnAdItemClickListener adItemClickListener;
    public OnAdPageChangeListener adPageChangeListener;
    public OnBindBodyViewCallback bindBodyViewCallback;
    public DialogInterface.OnCancelListener cancelListener;
    public View.OnClickListener clickNegativeListener;
    public View.OnClickListener clickNeutralListener;
    public View.OnClickListener clickPositiveListener;
    public CountDownTimerObserver countDownTimerObserver;
    public OnCreateBodyViewListener createBodyViewListener;
    public OnCreateButtonListener createButtonListener;
    public OnCreateInputListener createInputListener;
    public OnCreateLottieListener createLottieListener;
    public OnCreateProgressListener createProgressListener;
    public OnCreateTextListener createTextListener;
    public OnCreateTitleListener createTitleListener;
    public DialogInterface.OnDismissListener dismissListener;
    public OnInputCounterChangeListener inputCounterChangeListener;
    public OnInputClickListener inputListener;
    public OnLvItemClickListener itemListener;
    public DialogInterface.OnKeyListener keyListener;
    public OnRvItemClickListener rvItemListener;
    public OnShowListener showListener;

    CircleListeners() {
    }
}