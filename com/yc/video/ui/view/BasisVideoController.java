package com.yc.video.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.yc.video.R;
import com.yc.video.controller.GestureVideoController;
import com.yc.video.tool.BaseToast;
import com.yc.video.tool.PlayerUtils;

/* loaded from: classes.dex */
public class BasisVideoController extends GestureVideoController implements View.OnClickListener {
    public static boolean IS_LIVE = false;
    private CustomOncePlayView customOncePlayView;
    private CustomLiveControlView liveControlView;
    private Context mContext;
    private ProgressBar mLoadingProgress;
    private ImageView mLockButton;
    private ImageView thumb;
    private CustomTitleView titleView;
    private TextView tvLiveWaitMessage;
    private CustomBottomView vodControlView;

    @Override // com.yc.video.controller.InterVideoController
    public void destroy() {
    }

    public BasisVideoController(Context context) {
        this(context, null);
    }

    public BasisVideoController(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BasisVideoController(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.yc.video.controller.BaseVideoController
    protected int getLayoutId() {
        return R.layout.custom_video_player_standard;
    }

    @Override // com.yc.video.controller.GestureVideoController, com.yc.video.controller.BaseVideoController
    protected void initView(Context context) {
        super.initView(context);
        this.mContext = context;
        initFindViewById();
        initListener();
        initConfig();
    }

    private void initFindViewById() {
        this.mLockButton = (ImageView) findViewById(R.id.lock);
        this.mLoadingProgress = (ProgressBar) findViewById(R.id.loading);
    }

    private void initListener() {
        this.mLockButton.setOnClickListener(this);
    }

    private void initConfig() {
        setEnableOrientation(true);
        setCanChangePosition(true);
        setEnableInNormal(true);
        setGestureEnabled(true);
        removeAllControlComponent();
        addDefaultControlComponent("");
    }

    public void addDefaultControlComponent(String str) {
        CustomCompleteView customCompleteView = new CustomCompleteView(this.mContext);
        customCompleteView.setVisibility(8);
        addControlComponent(customCompleteView);
        CustomErrorView customErrorView = new CustomErrorView(this.mContext);
        customErrorView.setVisibility(8);
        addControlComponent(customErrorView);
        CustomPrepareView customPrepareView = new CustomPrepareView(this.mContext);
        this.thumb = customPrepareView.getThumb();
        customPrepareView.setClickStart();
        addControlComponent(customPrepareView);
        CustomTitleView customTitleView = new CustomTitleView(this.mContext);
        this.titleView = customTitleView;
        customTitleView.setTitle(str);
        this.titleView.setVisibility(0);
        addControlComponent(this.titleView);
        changePlayType();
        addControlComponent(new CustomGestureView(this.mContext));
    }

    public CustomTitleView getTitleView() {
        return this.titleView;
    }

    public void changePlayType() {
        if (IS_LIVE) {
            if (this.liveControlView == null) {
                this.liveControlView = new CustomLiveControlView(this.mContext);
            }
            removeControlComponent(this.liveControlView);
            addControlComponent(this.liveControlView);
            if (this.customOncePlayView == null) {
                CustomOncePlayView customOncePlayView = new CustomOncePlayView(this.mContext);
                this.customOncePlayView = customOncePlayView;
                this.tvLiveWaitMessage = customOncePlayView.getTvMessage();
            }
            removeControlComponent(this.customOncePlayView);
            addControlComponent(this.customOncePlayView);
            CustomBottomView customBottomView = this.vodControlView;
            if (customBottomView != null) {
                removeControlComponent(customBottomView);
            }
        } else {
            if (this.vodControlView == null) {
                CustomBottomView customBottomView2 = new CustomBottomView(this.mContext);
                this.vodControlView = customBottomView2;
                customBottomView2.showBottomProgress(true);
            }
            removeControlComponent(this.vodControlView);
            addControlComponent(this.vodControlView);
            CustomLiveControlView customLiveControlView = this.liveControlView;
            if (customLiveControlView != null) {
                removeControlComponent(customLiveControlView);
            }
            CustomOncePlayView customOncePlayView2 = this.customOncePlayView;
            if (customOncePlayView2 != null) {
                removeControlComponent(customOncePlayView2);
            }
        }
        setCanChangePosition(!IS_LIVE);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.lock) {
            this.mControlWrapper.toggleLockState();
        }
    }

    @Override // com.yc.video.controller.BaseVideoController
    protected void onLockStateChanged(boolean z) {
        if (z) {
            this.mLockButton.setSelected(true);
            BaseToast.showRoundRectToast(this.mContext.getResources().getString(R.string.locked));
        } else {
            this.mLockButton.setSelected(false);
            BaseToast.showRoundRectToast(this.mContext.getResources().getString(R.string.unlocked));
        }
    }

    @Override // com.yc.video.controller.BaseVideoController
    protected void onVisibilityChanged(boolean z, Animation animation) {
        if (this.mControlWrapper.isFullScreen()) {
            if (z) {
                if (this.mLockButton.getVisibility() == 8) {
                    this.mLockButton.setVisibility(0);
                    if (animation != null) {
                        this.mLockButton.startAnimation(animation);
                        return;
                    }
                    return;
                }
                return;
            }
            this.mLockButton.setVisibility(8);
            if (animation != null) {
                this.mLockButton.startAnimation(animation);
            }
        }
    }

    @Override // com.yc.video.controller.BaseVideoController
    protected void onPlayerStateChanged(int i) {
        super.onPlayerStateChanged(i);
        if (i == 1001) {
            setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
            this.mLockButton.setVisibility(8);
        } else if (i == 1002) {
            if (isShowing()) {
                this.mLockButton.setVisibility(0);
            } else {
                this.mLockButton.setVisibility(8);
            }
        }
        if (this.mActivity == null || !hasCutout()) {
            return;
        }
        int requestedOrientation = this.mActivity.getRequestedOrientation();
        int dp2px = PlayerUtils.dp2px(getContext(), 24.0f);
        int cutoutHeight = getCutoutHeight();
        if (requestedOrientation == 1) {
            ((FrameLayout.LayoutParams) this.mLockButton.getLayoutParams()).setMargins(dp2px, 0, dp2px, 0);
            return;
        }
        if (requestedOrientation == 0) {
            int i2 = dp2px + cutoutHeight;
            ((FrameLayout.LayoutParams) this.mLockButton.getLayoutParams()).setMargins(i2, 0, i2, 0);
        } else if (requestedOrientation == 8) {
            ((FrameLayout.LayoutParams) this.mLockButton.getLayoutParams()).setMargins(dp2px, 0, dp2px, 0);
        }
    }

    @Override // com.yc.video.controller.BaseVideoController
    protected void onPlayStateChanged(int i) {
        super.onPlayStateChanged(i);
        switch (i) {
            case -1:
            case 2:
            case 3:
            case 4:
            case 7:
                this.mLoadingProgress.setVisibility(8);
                break;
            case 0:
                this.mLockButton.setSelected(false);
                this.mLoadingProgress.setVisibility(8);
                break;
            case 1:
            case 6:
                this.mLoadingProgress.setVisibility(0);
                break;
            case 5:
                this.mLoadingProgress.setVisibility(8);
                this.mLockButton.setVisibility(8);
                this.mLockButton.setSelected(false);
                break;
        }
    }

    @Override // com.yc.video.controller.BaseVideoController
    public boolean onBackPressed() {
        if (isLocked()) {
            show();
            BaseToast.showRoundRectToast(this.mContext.getResources().getString(R.string.lock_tip));
            return true;
        }
        if (this.mControlWrapper.isFullScreen()) {
            return stopFullScreen();
        }
        Activity scanForActivity = PlayerUtils.scanForActivity(getContext());
        if (PlayerUtils.isActivityLiving(scanForActivity)) {
            scanForActivity.finish();
        }
        return super.onBackPressed();
    }

    @Override // com.yc.video.controller.BaseVideoController
    protected void setProgress(int i, int i2) {
        super.setProgress(i, i2);
    }

    public ImageView getThumb() {
        return this.thumb;
    }

    public void setTitle(String str) {
        CustomTitleView customTitleView = this.titleView;
        if (customTitleView != null) {
            customTitleView.setTitle(str);
        }
    }

    public CustomBottomView getBottomView() {
        return this.vodControlView;
    }

    public TextView getTvLiveWaitMessage() {
        return this.tvLiveWaitMessage;
    }
}