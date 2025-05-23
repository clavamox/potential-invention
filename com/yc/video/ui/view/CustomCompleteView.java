package com.yc.video.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.tool.BaseToast;
import com.yc.video.tool.PlayerUtils;

/* loaded from: classes.dex */
public class CustomCompleteView extends FrameLayout implements InterControlView, View.OnClickListener {
    private FrameLayout mCompleteContainer;
    private Context mContext;
    private ControlWrapper mControlWrapper;
    private ImageView mIvReplay;
    private ImageView mIvShare;
    private ImageView mIvStopFullscreen;
    private LinearLayout mLlReplay;
    private LinearLayout mLlShare;

    @Override // com.yc.video.ui.view.InterControlView
    public View getView() {
        return this;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onLockStateChanged(boolean z) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onVisibilityChanged(boolean z, Animation animation) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void setProgress(int i, int i2) {
    }

    public CustomCompleteView(Context context) {
        super(context);
        init(context);
    }

    public CustomCompleteView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public CustomCompleteView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setVisibility(8);
        initFindViewById(LayoutInflater.from(this.mContext).inflate(R.layout.custom_video_player_completed, (ViewGroup) this, true));
        initListener();
        setClickable(true);
    }

    private void initFindViewById(View view) {
        this.mCompleteContainer = (FrameLayout) view.findViewById(R.id.complete_container);
        this.mIvStopFullscreen = (ImageView) view.findViewById(R.id.iv_stop_fullscreen);
        this.mLlReplay = (LinearLayout) view.findViewById(R.id.ll_replay);
        this.mIvReplay = (ImageView) view.findViewById(R.id.iv_replay);
        this.mLlShare = (LinearLayout) view.findViewById(R.id.ll_share);
        this.mIvShare = (ImageView) view.findViewById(R.id.iv_share);
    }

    private void initListener() {
        this.mLlReplay.setOnClickListener(this);
        this.mLlShare.setOnClickListener(this);
        this.mIvStopFullscreen.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Activity scanForActivity;
        if (view == this.mLlReplay) {
            this.mControlWrapper.replay(true);
            return;
        }
        if (view == this.mLlShare) {
            BaseToast.showRoundRectToast("点击分享，后期完善");
            return;
        }
        if (view != this.mIvStopFullscreen || !this.mControlWrapper.isFullScreen() || (scanForActivity = PlayerUtils.scanForActivity(this.mContext)) == null || scanForActivity.isFinishing()) {
            return;
        }
        scanForActivity.setRequestedOrientation(1);
        this.mControlWrapper.stopFullScreen();
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void attach(ControlWrapper controlWrapper) {
        this.mControlWrapper = controlWrapper;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayStateChanged(int i) {
        if (i == 5) {
            setVisibility(0);
            this.mIvStopFullscreen.setVisibility(this.mControlWrapper.isFullScreen() ? 0 : 8);
            bringToFront();
            return;
        }
        setVisibility(8);
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayerStateChanged(int i) {
        if (i == 1002) {
            this.mIvStopFullscreen.setVisibility(0);
        } else if (i == 1001) {
            this.mIvStopFullscreen.setVisibility(8);
        }
        Activity scanForActivity = PlayerUtils.scanForActivity(this.mContext);
        if (scanForActivity == null || !this.mControlWrapper.hasCutout()) {
            return;
        }
        int requestedOrientation = scanForActivity.getRequestedOrientation();
        int cutoutHeight = this.mControlWrapper.getCutoutHeight();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mIvStopFullscreen.getLayoutParams();
        if (requestedOrientation == 1) {
            layoutParams.setMargins(0, 0, 0, 0);
        } else if (requestedOrientation == 0) {
            layoutParams.setMargins(cutoutHeight, 0, 0, 0);
        } else if (requestedOrientation == 8) {
            layoutParams.setMargins(0, 0, 0, 0);
        }
    }
}