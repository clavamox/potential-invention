package com.mylhyl.circledialog.view;

import android.R;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.internal.CountDownTimer;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.CountDownTimerObserver;
import com.mylhyl.circledialog.view.listener.OnCreateButtonListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
abstract class AbsButton extends LinearLayout implements ButtonView {
    private CountDownTimer mCountDownTimer;
    private final List<CountDownTimerObserver> mCountDownTimerObservers;
    private DialogParams mDialogParams;
    private TextView mNegativeButton;
    protected ButtonParams mNegativeParams;
    private TextView mNeutralButton;
    protected ButtonParams mNeutralParams;
    private OnCreateButtonListener mOnCreateButtonListener;
    private TextView mPositiveButton;
    protected ButtonParams mPositiveParams;

    @Override // com.mylhyl.circledialog.view.listener.ButtonView
    public final View getView() {
        return this;
    }

    protected abstract void initView();

    protected abstract void setNegativeButtonBackground(View view, int i, CircleParams circleParams);

    protected abstract void setNeutralButtonBackground(View view, int i, CircleParams circleParams);

    protected abstract void setPositiveButtonBackground(View view, int i, CircleParams circleParams);

    public AbsButton(Context context, CircleParams circleParams) {
        super(context);
        this.mCountDownTimerObservers = new ArrayList();
        init(circleParams);
    }

    public void addCountDownTimerObserver(CountDownTimerObserver countDownTimerObserver) {
        if (countDownTimerObserver == null || this.mCountDownTimerObservers.contains(countDownTimerObserver)) {
            return;
        }
        this.mCountDownTimerObservers.add(countDownTimerObserver);
    }

    @Override // com.mylhyl.circledialog.view.listener.ButtonView
    public final void regNegativeListener(View.OnClickListener onClickListener) {
        TextView textView = this.mNegativeButton;
        if (textView != null) {
            textView.setOnClickListener(onClickListener);
        }
    }

    @Override // com.mylhyl.circledialog.view.listener.ButtonView
    public final void regPositiveListener(View.OnClickListener onClickListener) {
        if (this.mPositiveButton != null) {
            timerRestart();
            this.mPositiveButton.setOnClickListener(onClickListener);
        }
    }

    @Override // com.mylhyl.circledialog.view.listener.ButtonView
    public final void regNeutralListener(View.OnClickListener onClickListener) {
        TextView textView = this.mNeutralButton;
        if (textView != null) {
            textView.setOnClickListener(onClickListener);
        }
    }

    @Override // com.mylhyl.circledialog.view.listener.ButtonView
    public final void refreshText() {
        if (this.mNegativeParams != null && this.mNegativeButton != null) {
            handleNegativeStyle();
        }
        if (this.mPositiveParams != null && this.mPositiveButton != null) {
            handlePositiveStyle();
        }
        if (this.mNeutralParams == null || this.mNeutralButton == null) {
            return;
        }
        handleNeutralStyle();
    }

    @Override // com.mylhyl.circledialog.view.listener.ButtonView
    public final boolean isEmpty() {
        return this.mNegativeParams == null && this.mPositiveParams == null && this.mNeutralParams == null;
    }

    @Override // com.mylhyl.circledialog.view.listener.ButtonView
    public void timerRestart() {
        CountDownTimer countDownTimer = this.mCountDownTimer;
        if (countDownTimer != null) {
            countDownTimer.restart();
        }
    }

    @Override // com.mylhyl.circledialog.view.listener.ButtonView
    public void timerCancel() {
        CountDownTimer countDownTimer = this.mCountDownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void init(CircleParams circleParams) {
        this.mDialogParams = circleParams.dialogParams;
        this.mNegativeParams = circleParams.negativeParams;
        this.mPositiveParams = circleParams.positiveParams;
        this.mNeutralParams = circleParams.neutralParams;
        this.mOnCreateButtonListener = circleParams.circleListeners.createButtonListener;
        addCountDownTimerObserver(circleParams.circleListeners.countDownTimerObserver);
        initView();
        if (this.mNegativeParams != null) {
            createNegative();
            setNegativeButtonBackground(this.mNegativeButton, this.mNegativeParams.backgroundColor != 0 ? this.mNegativeParams.backgroundColor : this.mDialogParams.backgroundColor, circleParams);
        }
        if (this.mNeutralParams != null) {
            if (this.mNegativeButton != null) {
                createDivider();
            }
            createNeutral();
            setNeutralButtonBackground(this.mNeutralButton, this.mNeutralParams.backgroundColor != 0 ? this.mNeutralParams.backgroundColor : this.mDialogParams.backgroundColor, circleParams);
        }
        if (this.mPositiveParams != null) {
            if (this.mNeutralButton != null || this.mNegativeButton != null) {
                createDivider();
            }
            createPositive();
            setPositiveButtonBackground(this.mPositiveButton, this.mPositiveParams.backgroundColor != 0 ? this.mPositiveParams.backgroundColor : this.mDialogParams.backgroundColor, circleParams);
        }
        OnCreateButtonListener onCreateButtonListener = this.mOnCreateButtonListener;
        if (onCreateButtonListener != null) {
            onCreateButtonListener.onCreateButton(this.mNegativeButton, this.mPositiveButton, this.mNeutralButton);
        }
    }

    private void createNegative() {
        TextView textView = new TextView(getContext());
        this.mNegativeButton = textView;
        textView.setId(R.id.button1);
        this.mNegativeButton.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        handleNegativeStyle();
        addView(this.mNegativeButton);
    }

    private void createDivider() {
        addView(new DividerView(getContext()));
    }

    private void createNeutral() {
        TextView textView = new TextView(getContext());
        this.mNeutralButton = textView;
        textView.setId(R.id.button2);
        this.mNeutralButton.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        handleNeutralStyle();
        addView(this.mNeutralButton);
    }

    private void createPositive() {
        TextView textView = new TextView(getContext());
        this.mPositiveButton = textView;
        textView.setId(R.id.button3);
        this.mPositiveButton.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        handlerCountDownTimer();
        handlePositiveStyle();
        addView(this.mPositiveButton);
    }

    private void handlerCountDownTimer() {
        if (this.mPositiveParams.countDownTime <= 0 || this.mPositiveParams.countDownInterval <= 0) {
            return;
        }
        this.mCountDownTimer = new CountDownTimer(this.mPositiveParams.countDownTime, this.mPositiveParams.countDownInterval) { // from class: com.mylhyl.circledialog.view.AbsButton.1
            @Override // com.mylhyl.circledialog.internal.CountDownTimer
            public void onTick(long j) {
                AbsButton.this.mPositiveParams.disable = true;
                AbsButton.this.handlePositiveEnabled();
                String str = AbsButton.this.mPositiveParams.countDownText;
                if (TextUtils.isEmpty(str)) {
                    str = AbsButton.this.mPositiveParams.text.concat(ButtonParams.COUNT_DOWN_TEXT_FORMAT);
                }
                AbsButton.this.mPositiveButton.setText(String.format(str, Long.valueOf((j / 1000) + 1)));
                Iterator it = AbsButton.this.mCountDownTimerObservers.iterator();
                while (it.hasNext()) {
                    ((CountDownTimerObserver) it.next()).onTimerTick(j);
                }
            }

            @Override // com.mylhyl.circledialog.internal.CountDownTimer
            public void onFinish() {
                AbsButton.this.mPositiveParams.disable = false;
                AbsButton.this.handlePositiveEnabled();
                AbsButton.this.mPositiveButton.setText(AbsButton.this.mPositiveParams.text);
                Iterator it = AbsButton.this.mCountDownTimerObservers.iterator();
                while (it.hasNext()) {
                    ((CountDownTimerObserver) it.next()).onTimerFinish();
                }
            }
        }.start();
    }

    private void handleNegativeStyle() {
        if (this.mDialogParams.typeface != null) {
            this.mNegativeButton.setTypeface(this.mDialogParams.typeface);
        }
        this.mNegativeButton.setGravity(17);
        this.mNegativeButton.setText(this.mNegativeParams.text);
        this.mNegativeButton.setEnabled(!this.mNegativeParams.disable);
        this.mNegativeButton.setTextColor(this.mNegativeParams.disable ? this.mNegativeParams.textColorDisable : this.mNegativeParams.textColor);
        this.mNegativeButton.setTextSize(this.mNegativeParams.textSize);
        this.mNegativeButton.setHeight(Controller.dp2px(getContext(), this.mNegativeParams.height));
        TextView textView = this.mNegativeButton;
        textView.setTypeface(textView.getTypeface(), this.mNegativeParams.styleText);
    }

    private void handleNeutralStyle() {
        if (this.mDialogParams.typeface != null) {
            this.mNeutralButton.setTypeface(this.mDialogParams.typeface);
        }
        this.mNeutralButton.setGravity(17);
        this.mNeutralButton.setText(this.mNeutralParams.text);
        this.mNeutralButton.setEnabled(!this.mNeutralParams.disable);
        this.mNeutralButton.setTextColor(this.mNeutralParams.disable ? this.mNeutralParams.textColorDisable : this.mNeutralParams.textColor);
        this.mNeutralButton.setTextSize(this.mNeutralParams.textSize);
        this.mNeutralButton.setHeight(Controller.dp2px(getContext(), this.mNeutralParams.height));
        TextView textView = this.mNeutralButton;
        textView.setTypeface(textView.getTypeface(), this.mNeutralParams.styleText);
    }

    private void handlePositiveStyle() {
        if (this.mDialogParams.typeface != null) {
            this.mPositiveButton.setTypeface(this.mDialogParams.typeface);
        }
        this.mPositiveButton.setGravity(17);
        this.mPositiveButton.setText(this.mPositiveParams.text);
        handlePositiveEnabled();
        this.mPositiveButton.setTextSize(this.mPositiveParams.textSize);
        this.mPositiveButton.setHeight(Controller.dp2px(getContext(), this.mPositiveParams.height));
        TextView textView = this.mPositiveButton;
        textView.setTypeface(textView.getTypeface(), this.mPositiveParams.styleText);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePositiveEnabled() {
        this.mPositiveButton.setEnabled(!this.mPositiveParams.disable);
        this.mPositiveButton.setTextColor(this.mPositiveParams.disable ? this.mPositiveParams.textColorDisable : this.mPositiveParams.textColor);
    }
}