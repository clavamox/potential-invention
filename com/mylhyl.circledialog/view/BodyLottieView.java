package com.mylhyl.circledialog.view;

import android.content.Context;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.mylhyl.circledialog.internal.BackgroundHelper;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.LottieParams;
import com.mylhyl.circledialog.view.listener.OnCreateLottieListener;

/* loaded from: classes.dex */
final class BodyLottieView extends LinearLayout {
    private DialogParams mDialogParams;
    private LottieAnimationView mLottieAnimationView;
    private LottieParams mLottieParams;
    private OnCreateLottieListener mOnCreateLottieListener;
    private TextView mTextView;

    public BodyLottieView(Context context, CircleParams circleParams) {
        super(context);
        init(circleParams);
    }

    private void init(CircleParams circleParams) {
        this.mDialogParams = circleParams.dialogParams;
        this.mLottieParams = circleParams.lottieParams;
        this.mOnCreateLottieListener = circleParams.circleListeners.createLottieListener;
        setOrientation(1);
        BackgroundHelper.handleBodyBackground(this, this.mLottieParams.backgroundColor != 0 ? this.mLottieParams.backgroundColor : this.mDialogParams.backgroundColor, circleParams);
        createLottieView();
        createText();
        OnCreateLottieListener onCreateLottieListener = this.mOnCreateLottieListener;
        if (onCreateLottieListener != null) {
            onCreateLottieListener.onCreateLottieView(this.mLottieAnimationView, this.mTextView);
        }
    }

    private void createLottieView() {
        this.mLottieAnimationView = new LottieAnimationView(getContext());
        int dp2px = Controller.dp2px(getContext(), this.mLottieParams.lottieWidth);
        int dp2px2 = Controller.dp2px(getContext(), this.mLottieParams.lottieHeight);
        if (dp2px <= 0) {
            dp2px = -2;
        }
        if (dp2px2 <= 0) {
            dp2px2 = -2;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp2px, dp2px2);
        if (this.mLottieParams.margins != null) {
            layoutParams.setMargins(Controller.dp2px(getContext(), r0[0]), Controller.dp2px(getContext(), r0[1]), Controller.dp2px(getContext(), r0[2]), Controller.dp2px(getContext(), r0[3]));
        }
        layoutParams.gravity = 17;
        if (this.mLottieParams.animationResId != 0) {
            this.mLottieAnimationView.setAnimation(this.mLottieParams.animationResId);
        }
        if (!TextUtils.isEmpty(this.mLottieParams.animationFileName)) {
            this.mLottieAnimationView.setAnimation(this.mLottieParams.animationFileName);
        }
        if (!TextUtils.isEmpty(this.mLottieParams.imageAssetsFolder)) {
            this.mLottieAnimationView.setImageAssetsFolder(this.mLottieParams.imageAssetsFolder);
        }
        if (this.mLottieParams.autoPlay) {
            this.mLottieAnimationView.playAnimation();
        }
        if (this.mLottieParams.loop) {
            this.mLottieAnimationView.setRepeatCount(-1);
        }
        addView(this.mLottieAnimationView, layoutParams);
    }

    private void createText() {
        if (TextUtils.isEmpty(this.mLottieParams.text)) {
            return;
        }
        this.mTextView = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        if (this.mLottieParams.textMargins != null) {
            layoutParams.setMargins(Controller.dp2px(getContext(), r1[0]), Controller.dp2px(getContext(), r1[1]), Controller.dp2px(getContext(), r1[2]), Controller.dp2px(getContext(), r1[3]));
        }
        if (this.mDialogParams.typeface != null) {
            this.mTextView.setTypeface(this.mDialogParams.typeface);
        }
        this.mTextView.setText(this.mLottieParams.text);
        this.mTextView.setTextSize(this.mLottieParams.textSize);
        this.mTextView.setTextColor(this.mLottieParams.textColor);
        TextView textView = this.mTextView;
        textView.setTypeface(textView.getTypeface(), this.mLottieParams.styleText);
        if (this.mLottieParams.textPadding != null) {
            this.mTextView.setPadding(Controller.dp2px(getContext(), r1[0]), Controller.dp2px(getContext(), r1[1]), Controller.dp2px(getContext(), r1[2]), Controller.dp2px(getContext(), r1[3]));
        }
        addView(this.mTextView, layoutParams);
    }

    public void refreshText() {
        LottieParams lottieParams = this.mLottieParams;
        if (lottieParams == null) {
            return;
        }
        if (this.mLottieAnimationView != null) {
            if (lottieParams.animationResId != 0) {
                this.mLottieAnimationView.setAnimation(this.mLottieParams.animationResId);
            }
            if (!TextUtils.isEmpty(this.mLottieParams.animationFileName)) {
                this.mLottieAnimationView.setAnimation(this.mLottieParams.animationFileName);
            }
            if (!TextUtils.isEmpty(this.mLottieParams.imageAssetsFolder)) {
                this.mLottieAnimationView.setImageAssetsFolder(this.mLottieParams.imageAssetsFolder);
            }
            this.mLottieAnimationView.playAnimation();
        }
        if (this.mTextView == null || TextUtils.isEmpty(this.mLottieParams.text)) {
            return;
        }
        this.mTextView.setText(this.mLottieParams.text);
    }
}