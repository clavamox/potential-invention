package com.yc.video.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.controller.IGestureComponent;
import com.yc.video.tool.PlayerUtils;

/* loaded from: classes.dex */
public class CustomGestureView extends FrameLayout implements IGestureComponent {
    private Context mContext;
    private ControlWrapper mControlWrapper;
    private ImageView mIvIcon;
    private LinearLayout mLlCenterContainer;
    private ProgressBar mProPercent;
    private TextView mTvPercent;

    private void initListener() {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public View getView() {
        return this;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onLockStateChanged(boolean z) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayerStateChanged(int i) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onVisibilityChanged(boolean z, Animation animation) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void setProgress(int i, int i2) {
    }

    public CustomGestureView(Context context) {
        super(context);
        init(context);
    }

    public CustomGestureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public CustomGestureView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setVisibility(8);
        initFindViewById(LayoutInflater.from(this.mContext).inflate(R.layout.custom_video_player_gesture, (ViewGroup) this, true));
        initListener();
    }

    private void initFindViewById(View view) {
        this.mLlCenterContainer = (LinearLayout) view.findViewById(R.id.ll_center_container);
        this.mIvIcon = (ImageView) view.findViewById(R.id.iv_icon);
        this.mTvPercent = (TextView) view.findViewById(R.id.tv_percent);
        this.mProPercent = (ProgressBar) view.findViewById(R.id.pro_percent);
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void attach(ControlWrapper controlWrapper) {
        this.mControlWrapper = controlWrapper;
    }

    @Override // com.yc.video.controller.IGestureComponent
    public void onStartSlide() {
        this.mControlWrapper.hide();
        this.mLlCenterContainer.setVisibility(0);
        this.mLlCenterContainer.setAlpha(1.0f);
    }

    @Override // com.yc.video.controller.IGestureComponent
    public void onStopSlide() {
        this.mLlCenterContainer.animate().alpha(0.0f).setDuration(300L).setListener(new AnimatorListenerAdapter() { // from class: com.yc.video.ui.view.CustomGestureView.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                CustomGestureView.this.mLlCenterContainer.setVisibility(8);
            }
        }).start();
    }

    @Override // com.yc.video.controller.IGestureComponent
    public void onPositionChange(int i, int i2, int i3) {
        this.mProPercent.setVisibility(8);
        if (i > i2) {
            this.mIvIcon.setImageResource(R.drawable.ic_player_fast_forward);
        } else {
            this.mIvIcon.setImageResource(R.drawable.ic_player_fast_rewind);
        }
        this.mTvPercent.setText(String.format("%s/%s", PlayerUtils.formatTime(i), PlayerUtils.formatTime(i3)));
    }

    @Override // com.yc.video.controller.IGestureComponent
    public void onBrightnessChange(int i) {
        this.mProPercent.setVisibility(0);
        this.mIvIcon.setImageResource(R.drawable.ic_palyer_brightness);
        this.mTvPercent.setText(i + "%");
        this.mProPercent.setProgress(i);
    }

    @Override // com.yc.video.controller.IGestureComponent
    public void onVolumeChange(int i) {
        this.mProPercent.setVisibility(0);
        if (i <= 0) {
            this.mIvIcon.setImageResource(R.drawable.ic_player_volume_off);
        } else {
            this.mIvIcon.setImageResource(R.drawable.ic_player_volume_up);
        }
        this.mTvPercent.setText(i + "%");
        this.mProPercent.setProgress(i);
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayStateChanged(int i) {
        if (i == 0 || i == 8 || i == 1 || i == 2 || i == -1 || i == 5 || i == 9) {
            setVisibility(8);
        } else {
            setVisibility(0);
        }
    }
}