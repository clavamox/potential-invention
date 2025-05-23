package com.yc.video.ui.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.tool.PlayerUtils;

/* loaded from: classes.dex */
public class CustomTitleView extends FrameLayout implements InterControlView, View.OnClickListener {
    private BatteryReceiver mBatteryReceiver;
    private Context mContext;
    private ControlWrapper mControlWrapper;
    private boolean mIsRegister;
    private ImageView mIvBack;
    private ImageView mIvBattery;
    private LinearLayout mLlTitleContainer;
    private TextView mTvSysTime;
    private TextView mTvTitle;

    @Override // com.yc.video.ui.view.InterControlView
    public View getView() {
        return this;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void setProgress(int i, int i2) {
    }

    public CustomTitleView(Context context) {
        super(context);
        init(context);
    }

    public CustomTitleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public CustomTitleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setVisibility(8);
        initFindViewById(LayoutInflater.from(this.mContext).inflate(R.layout.custom_video_player_top, (ViewGroup) this, true));
        initListener();
        this.mBatteryReceiver = new BatteryReceiver(this.mIvBattery);
    }

    private void initFindViewById(View view) {
        this.mLlTitleContainer = (LinearLayout) view.findViewById(R.id.ll_title_container);
        this.mIvBack = (ImageView) view.findViewById(R.id.iv_back);
        this.mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        this.mIvBattery = (ImageView) view.findViewById(R.id.iv_battery);
        this.mTvSysTime = (TextView) view.findViewById(R.id.tv_sys_time);
    }

    private void initListener() {
        this.mIvBack.setOnClickListener(this);
    }

    public ImageView getmIvBack() {
        return this.mIvBack;
    }

    public void setTitle(String str) {
        if (str != null && str.length() > 0) {
            this.mTvTitle.setText(str);
        } else {
            this.mTvTitle.setText("");
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mIsRegister) {
            getContext().unregisterReceiver(this.mBatteryReceiver);
            this.mIsRegister = false;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIsRegister) {
            return;
        }
        getContext().registerReceiver(this.mBatteryReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        this.mIsRegister = true;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void attach(ControlWrapper controlWrapper) {
        this.mControlWrapper = controlWrapper;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onVisibilityChanged(boolean z, Animation animation) {
        if (z) {
            if (getVisibility() == 8) {
                this.mTvSysTime.setText(PlayerUtils.getCurrentSystemTime());
                setVisibility(0);
                if (animation != null) {
                    startAnimation(animation);
                }
            }
        } else if (getVisibility() == 0) {
            setVisibility(8);
            if (animation != null) {
                startAnimation(animation);
            }
        }
        if (getVisibility() == 0) {
            if (this.mControlWrapper.isFullScreen()) {
                this.mIvBattery.setVisibility(0);
                this.mTvSysTime.setVisibility(0);
            } else {
                this.mIvBattery.setVisibility(8);
                this.mTvSysTime.setVisibility(8);
            }
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayStateChanged(int i) {
        if (i == -1 || i == 0 || i == 1 || i == 2 || i == 5 || i == 8) {
            setVisibility(8);
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayerStateChanged(int i) {
        if (i == 1002) {
            if (this.mControlWrapper.isShowing() && !this.mControlWrapper.isLocked()) {
                setVisibility(0);
                this.mTvSysTime.setText(PlayerUtils.getCurrentSystemTime());
            }
            this.mTvTitle.setSelected(true);
        } else {
            setVisibility(8);
            this.mTvTitle.setSelected(false);
        }
        Activity scanForActivity = PlayerUtils.scanForActivity(this.mContext);
        if (scanForActivity == null || !this.mControlWrapper.hasCutout()) {
            return;
        }
        int requestedOrientation = scanForActivity.getRequestedOrientation();
        int cutoutHeight = this.mControlWrapper.getCutoutHeight();
        if (requestedOrientation == 1) {
            this.mLlTitleContainer.setPadding(PlayerUtils.dp2px(this.mContext, 12.0f), PlayerUtils.dp2px(this.mContext, 10.0f), PlayerUtils.dp2px(this.mContext, 12.0f), 0);
            return;
        }
        if (requestedOrientation == 0) {
            this.mLlTitleContainer.setPadding(cutoutHeight, 0, PlayerUtils.dp2px(this.mContext, 12.0f), 0);
        } else if (requestedOrientation == 8) {
            this.mLlTitleContainer.setPadding(0, 0, cutoutHeight, 0);
        } else {
            this.mLlTitleContainer.setPadding(PlayerUtils.dp2px(this.mContext, 12.0f), PlayerUtils.dp2px(this.mContext, 10.0f), PlayerUtils.dp2px(this.mContext, 12.0f), 0);
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onLockStateChanged(boolean z) {
        if (z) {
            setVisibility(8);
        } else {
            setVisibility(0);
            this.mTvSysTime.setText(PlayerUtils.getCurrentSystemTime());
        }
    }

    private static class BatteryReceiver extends BroadcastReceiver {
        private ImageView pow;

        public BatteryReceiver(ImageView imageView) {
            this.pow = imageView;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                return;
            }
            this.pow.getDrawable().setLevel((extras.getInt("level") * 100) / extras.getInt("scale"));
        }
    }
}