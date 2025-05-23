package com.yc.video.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.tool.PlayerUtils;

/* loaded from: classes.dex */
public class CustomErrorView extends LinearLayout implements InterControlView, View.OnClickListener {
    private Context mContext;
    private ControlWrapper mControlWrapper;
    private float mDownX;
    private float mDownY;
    private ImageView mIvStopFullscreen;
    private TextView mTvMessage;
    private TextView mTvRetry;

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

    public CustomErrorView(Context context) {
        super(context);
        init(context);
    }

    public CustomErrorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public CustomErrorView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setVisibility(8);
        initFindViewById(LayoutInflater.from(getContext()).inflate(R.layout.custom_video_player_error, (ViewGroup) this, true));
        initListener();
        setClickable(true);
    }

    private void initFindViewById(View view) {
        this.mTvMessage = (TextView) view.findViewById(R.id.tv_message);
        this.mTvRetry = (TextView) view.findViewById(R.id.tv_retry);
        this.mIvStopFullscreen = (ImageView) view.findViewById(R.id.iv_stop_fullscreen);
    }

    private void initListener() {
        this.mTvRetry.setOnClickListener(this);
        this.mIvStopFullscreen.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Activity scanForActivity;
        if (view == this.mTvRetry) {
            setVisibility(8);
            this.mControlWrapper.replay(false);
        } else {
            if (view != this.mIvStopFullscreen || !this.mControlWrapper.isFullScreen() || (scanForActivity = PlayerUtils.scanForActivity(this.mContext)) == null || scanForActivity.isFinishing()) {
                return;
            }
            scanForActivity.setRequestedOrientation(1);
            this.mControlWrapper.stopFullScreen();
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void attach(ControlWrapper controlWrapper) {
        this.mControlWrapper = controlWrapper;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayStateChanged(int i) {
        if (i == -1) {
            bringToFront();
            setVisibility(0);
            this.mIvStopFullscreen.setVisibility(this.mControlWrapper.isFullScreen() ? 0 : 8);
            this.mTvMessage.setText("视频播放异常");
        }
        if (i == -2) {
            bringToFront();
            setVisibility(0);
            this.mIvStopFullscreen.setVisibility(this.mControlWrapper.isFullScreen() ? 0 : 8);
            this.mTvMessage.setText("无网络，请检查网络设置");
        }
        if (i == -3) {
            bringToFront();
            setVisibility(0);
            this.mIvStopFullscreen.setVisibility(this.mControlWrapper.isFullScreen() ? 0 : 8);
            this.mTvMessage.setText("视频加载错误");
            return;
        }
        if (i == 0) {
            setVisibility(8);
        } else if (i == 9) {
            setVisibility(8);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mDownX = motionEvent.getX();
            this.mDownY = motionEvent.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (action == 2) {
            float abs = Math.abs(motionEvent.getX() - this.mDownX);
            float abs2 = Math.abs(motionEvent.getY() - this.mDownY);
            if (abs > ViewConfiguration.get(getContext()).getScaledTouchSlop() || abs2 > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }
}