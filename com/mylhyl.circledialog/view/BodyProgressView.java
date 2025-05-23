package com.mylhyl.circledialog.view;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mylhyl.circledialog.internal.BackgroundHelper;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.ProgressParams;
import com.mylhyl.circledialog.res.values.CircleDimen;
import com.mylhyl.circledialog.view.listener.CountDownTimerObserver;
import com.mylhyl.circledialog.view.listener.OnCreateProgressListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/* loaded from: classes.dex */
final class BodyProgressView extends LinearLayout {
    private CountDownTimerObserver mCountDownTimerObserver;
    private DialogParams mDialogParams;
    private OnCreateProgressListener mOnCreateProgressListener;
    private ProgressBar mProgressBar;
    private ProgressParams mProgressParams;
    private TextView mTextView;
    private Handler mViewUpdateHandler;

    public BodyProgressView(Context context, CircleParams circleParams) {
        super(context);
        init(circleParams);
    }

    protected static Field getDeclaredField(Object obj, String str) {
        return getDeclaredField((Class) obj.getClass(), str);
    }

    protected static void makeAccessible(Field field) {
        if (Modifier.isPublic(field.getModifiers()) && Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            return;
        }
        field.setAccessible(true);
    }

    protected static Field getDeclaredField(Class cls, String str) {
        while (cls != Object.class) {
            try {
                return cls.getDeclaredField(str);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                cls = cls.getSuperclass();
            }
        }
        return null;
    }

    private static void setFieldValue(Object obj, String str, Object obj2) {
        Field declaredField = getDeclaredField(obj, str);
        if (declaredField == null) {
            throw new IllegalArgumentException("Could not find field [" + str + "] on target [" + obj + "]");
        }
        makeAccessible(declaredField);
        try {
            declaredField.set(obj, obj2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public synchronized void refreshProgress() {
        this.mProgressBar.setMax(this.mProgressParams.max);
        this.mProgressBar.setProgress(this.mProgressParams.progress);
        this.mProgressBar.setSecondaryProgress(this.mProgressParams.progress + 10);
        onProgressChanged();
    }

    public CountDownTimerObserver getCountDownTimerObserver() {
        return this.mCountDownTimerObserver;
    }

    private void init(CircleParams circleParams) {
        this.mDialogParams = circleParams.dialogParams;
        this.mProgressParams = circleParams.progressParams;
        this.mOnCreateProgressListener = circleParams.circleListeners.createProgressListener;
        ButtonParams buttonParams = circleParams.positiveParams;
        if (buttonParams != null && buttonParams.countDownTime > 0 && buttonParams.countDownInterval > 0) {
            this.mCountDownTimerObserver = new CountDownTimerObserver() { // from class: com.mylhyl.circledialog.view.BodyProgressView.1
                @Override // com.mylhyl.circledialog.view.listener.CountDownTimerObserver
                public void onTimerTick(long j) {
                    BodyProgressView.this.mProgressBar.setVisibility(0);
                    BodyProgressView.this.mTextView.setText(BodyProgressView.this.mProgressParams.text);
                }

                @Override // com.mylhyl.circledialog.view.listener.CountDownTimerObserver
                public void onTimerFinish() {
                    BodyProgressView.this.mProgressBar.setVisibility(4);
                    BodyProgressView.this.mTextView.setText(BodyProgressView.this.mProgressParams.timeoutText);
                }
            };
        }
        setOrientation(1);
        BackgroundHelper.handleBodyBackground(this, this.mProgressParams.backgroundColor != 0 ? this.mProgressParams.backgroundColor : this.mDialogParams.backgroundColor, circleParams);
        createProgressBar();
        createText();
        OnCreateProgressListener onCreateProgressListener = this.mOnCreateProgressListener;
        if (onCreateProgressListener != null) {
            onCreateProgressListener.onCreateProgressView(this.mProgressBar, this.mTextView);
        }
    }

    private void createText() {
        this.mTextView = new TextView(getContext());
        if (this.mDialogParams.typeface != null) {
            this.mTextView.setTypeface(this.mDialogParams.typeface);
        }
        this.mTextView.setGravity(17);
        this.mTextView.setTextSize(this.mProgressParams.textSize);
        this.mTextView.setTextColor(this.mProgressParams.textColor);
        TextView textView = this.mTextView;
        textView.setTypeface(textView.getTypeface(), this.mProgressParams.styleText);
        if (this.mProgressParams.padding != null) {
            this.mTextView.setPadding(Controller.dp2px(getContext(), r0[0]), Controller.dp2px(getContext(), r0[1]), Controller.dp2px(getContext(), r0[2]), Controller.dp2px(getContext(), r0[3]));
        }
        addView(this.mTextView);
        if (!TextUtils.isEmpty(this.mProgressParams.text)) {
            this.mTextView.setText(this.mProgressParams.text);
        }
        this.mViewUpdateHandler = new Handler() { // from class: com.mylhyl.circledialog.view.BodyProgressView.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (BodyProgressView.this.mProgressParams.style != 0) {
                    BodyProgressView.this.mTextView.setText(BodyProgressView.this.mProgressParams.text);
                    return;
                }
                String str = ((int) ((BodyProgressView.this.mProgressBar.getProgress() / BodyProgressView.this.mProgressBar.getMax()) * 100.0f)) + "%";
                if (BodyProgressView.this.mProgressParams.text.contains("%s")) {
                    BodyProgressView.this.mTextView.setText(String.format(BodyProgressView.this.mProgressParams.text, str));
                } else {
                    BodyProgressView.this.mTextView.setText(BodyProgressView.this.mProgressParams.text + str);
                }
            }
        };
        this.mTextView.setText(this.mProgressParams.text);
    }

    private void createProgressBar() {
        int i = this.mProgressParams.progressDrawableId;
        if (this.mProgressParams.style == 0) {
            if (i != 0) {
                ProgressBar progressBar = new ProgressBar(getContext());
                this.mProgressBar = progressBar;
                setFieldValue(progressBar, "mOnlyIndeterminate", new Boolean(false));
                this.mProgressBar.setIndeterminate(false);
                if (Controller.SDK_LOLLIPOP) {
                    this.mProgressBar.setProgressDrawableTiled(getContext().getDrawable(i));
                } else {
                    this.mProgressBar.setProgressDrawable(getContext().getResources().getDrawable(i));
                }
            } else {
                this.mProgressBar = new ProgressBar(getContext(), null, R.attr.progressBarStyleHorizontal);
            }
            this.mProgressParams.progressHeight = CircleDimen.PROGRESS_HEIGHT_HORIZONTAL;
        } else {
            if (i != 0) {
                this.mProgressBar = new ProgressBar(getContext());
                if (Controller.SDK_LOLLIPOP) {
                    this.mProgressBar.setIndeterminateDrawableTiled(getContext().getDrawable(i));
                } else {
                    this.mProgressBar.setIndeterminateDrawable(getContext().getResources().getDrawable(i));
                }
            } else {
                this.mProgressBar = new ProgressBar(getContext(), null, R.attr.progressBarStyle);
                if (this.mProgressParams.indeterminateColor != 0) {
                    this.mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(this.mProgressParams.indeterminateColor));
                }
            }
            this.mProgressParams.progressHeight = CircleDimen.PROGRESS_HEIGHT_SPINNER;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, Controller.dp2px(getContext(), this.mProgressParams.progressHeight));
        if (this.mProgressParams.margins != null) {
            layoutParams.setMargins(Controller.dp2px(getContext(), r1[0]), Controller.dp2px(getContext(), r1[1]), Controller.dp2px(getContext(), r1[2]), Controller.dp2px(getContext(), r1[3]));
        }
        addView(this.mProgressBar, layoutParams);
    }

    private void onProgressChanged() {
        Handler handler = this.mViewUpdateHandler;
        if (handler == null || handler.hasMessages(0)) {
            return;
        }
        this.mViewUpdateHandler.sendEmptyMessage(0);
    }
}