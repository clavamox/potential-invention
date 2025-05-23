package com.yc.video.controller;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import com.yc.kernel.utils.VideoLogUtils;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.controller.OrientationHelper;
import com.yc.video.player.InterVideoPlayer;
import com.yc.video.player.VideoViewManager;
import com.yc.video.tool.NetworkUtils;
import com.yc.video.tool.PlayerUtils;
import com.yc.video.tool.StatesCutoutUtils;
import com.yc.video.ui.view.InterControlView;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class BaseVideoController extends FrameLayout implements InterVideoController, InterViewController, OrientationHelper.OnOrientationChangeListener {
    protected Activity mActivity;
    private boolean mAdaptCutout;
    protected LinkedHashMap<InterControlView, Boolean> mControlComponents;
    protected ControlWrapper mControlWrapper;
    private int mCutoutHeight;
    protected int mDefaultTimeout;
    private boolean mEnableOrientation;
    protected final Runnable mFadeOut;
    private Boolean mHasCutout;
    private Animation mHideAnim;
    protected boolean mIsLocked;
    private boolean mIsStartProgress;
    private int mOrientation;
    protected OrientationHelper mOrientationHelper;
    private Animation mShowAnim;
    protected Runnable mShowProgress;
    protected boolean mShowing;

    protected abstract int getLayoutId();

    public boolean onBackPressed() {
        return false;
    }

    protected void onLockStateChanged(boolean z) {
    }

    protected void onVisibilityChanged(boolean z, Animation animation) {
    }

    protected void setProgress(int i, int i2) {
    }

    public BaseVideoController(Context context) {
        this(context, null);
    }

    public BaseVideoController(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BaseVideoController(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDefaultTimeout = 5000;
        this.mControlComponents = new LinkedHashMap<>();
        this.mFadeOut = new Runnable() { // from class: com.yc.video.controller.BaseVideoController.2
            @Override // java.lang.Runnable
            public void run() {
                BaseVideoController.this.hide();
            }
        };
        this.mShowProgress = new Runnable() { // from class: com.yc.video.controller.BaseVideoController.3
            @Override // java.lang.Runnable
            public void run() {
                int progress = BaseVideoController.this.setProgress();
                if (BaseVideoController.this.mControlWrapper.isPlaying()) {
                    BaseVideoController.this.postDelayed(this, (long) ((1000 - (progress % 1000)) / BaseVideoController.this.mControlWrapper.getSpeed()));
                } else {
                    BaseVideoController.this.mIsStartProgress = false;
                }
            }
        };
        this.mOrientation = 0;
        initView(context);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Animation animation = this.mShowAnim;
        if (animation != null) {
            animation.cancel();
            this.mShowAnim = null;
        }
        Animation animation2 = this.mHideAnim;
        if (animation2 != null) {
            animation2.cancel();
            this.mHideAnim = null;
        }
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (this.mControlWrapper.isPlaying()) {
            if (this.mEnableOrientation || this.mControlWrapper.isFullScreen()) {
                if (z) {
                    postDelayed(new Runnable() { // from class: com.yc.video.controller.BaseVideoController.1
                        @Override // java.lang.Runnable
                        public void run() {
                            BaseVideoController.this.mOrientationHelper.enable();
                        }
                    }, 800L);
                } else {
                    this.mOrientationHelper.disable();
                }
            }
        }
    }

    protected void initView(Context context) {
        if (getLayoutId() != 0) {
            LayoutInflater.from(getContext()).inflate(getLayoutId(), (ViewGroup) this, true);
        }
        this.mOrientationHelper = new OrientationHelper(context.getApplicationContext());
        this.mEnableOrientation = VideoViewManager.getConfig().mEnableOrientation;
        this.mAdaptCutout = VideoViewManager.getConfig().mAdaptCutout;
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        this.mShowAnim = alphaAnimation;
        alphaAnimation.setDuration(300L);
        AlphaAnimation alphaAnimation2 = new AlphaAnimation(1.0f, 0.0f);
        this.mHideAnim = alphaAnimation2;
        alphaAnimation2.setDuration(300L);
        this.mActivity = PlayerUtils.scanForActivity(context);
    }

    public void setMediaPlayer(InterVideoPlayer interVideoPlayer) {
        this.mControlWrapper = new ControlWrapper(interVideoPlayer, this);
        Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getKey().attach(this.mControlWrapper);
        }
        this.mOrientationHelper.setOnOrientationChangeListener(this);
    }

    @Override // com.yc.video.controller.InterViewController
    public void addControlComponent(InterControlView... interControlViewArr) {
        for (InterControlView interControlView : interControlViewArr) {
            addControlComponent(interControlView, false);
        }
    }

    @Override // com.yc.video.controller.InterViewController
    public void addControlComponent(InterControlView interControlView, boolean z) {
        this.mControlComponents.put(interControlView, Boolean.valueOf(z));
        ControlWrapper controlWrapper = this.mControlWrapper;
        if (controlWrapper != null) {
            interControlView.attach(controlWrapper);
        }
        View view = interControlView.getView();
        if (view == null || z) {
            return;
        }
        addView(view, 0);
    }

    @Override // com.yc.video.controller.InterViewController
    public void removeControlComponent(InterControlView interControlView) {
        removeView(interControlView.getView());
        this.mControlComponents.remove(interControlView);
    }

    @Override // com.yc.video.controller.InterViewController
    public void removeAllControlComponent() {
        Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            removeView(it.next().getKey().getView());
        }
        this.mControlComponents.clear();
    }

    @Override // com.yc.video.controller.InterViewController
    public void removeAllPrivateComponents() {
        Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().booleanValue()) {
                it.remove();
            }
        }
    }

    public void setPlayState(int i) {
        handlePlayStateChanged(i);
    }

    public void setPlayerState(int i) {
        handlePlayerStateChanged(i);
    }

    public void setDismissTimeout(int i) {
        if (i > 0) {
            this.mDefaultTimeout = i;
        } else {
            this.mDefaultTimeout = 5000;
        }
    }

    @Override // com.yc.video.controller.InterVideoController
    public void hide() {
        if (this.mShowing) {
            stopFadeOut();
            handleVisibilityChanged(false, this.mHideAnim);
            this.mShowing = false;
        }
    }

    @Override // com.yc.video.controller.InterVideoController
    public void show() {
        if (this.mShowing) {
            return;
        }
        handleVisibilityChanged(true, this.mShowAnim);
        startFadeOut();
        this.mShowing = true;
    }

    @Override // com.yc.video.controller.InterVideoController
    public boolean isShowing() {
        return this.mShowing;
    }

    @Override // com.yc.video.controller.InterVideoController
    public void startFadeOut() {
        stopFadeOut();
        postDelayed(this.mFadeOut, this.mDefaultTimeout);
    }

    @Override // com.yc.video.controller.InterVideoController
    public void stopFadeOut() {
        Runnable runnable = this.mFadeOut;
        if (runnable != null) {
            removeCallbacks(runnable);
        }
    }

    @Override // com.yc.video.controller.InterVideoController
    public void setLocked(boolean z) {
        this.mIsLocked = z;
        handleLockStateChanged(z);
    }

    @Override // com.yc.video.controller.InterVideoController
    public boolean isLocked() {
        return this.mIsLocked;
    }

    @Override // com.yc.video.controller.InterVideoController
    public void startProgress() {
        if (this.mIsStartProgress) {
            return;
        }
        post(this.mShowProgress);
        this.mIsStartProgress = true;
    }

    @Override // com.yc.video.controller.InterVideoController
    public void stopProgress() {
        if (this.mIsStartProgress) {
            removeCallbacks(this.mShowProgress);
            this.mIsStartProgress = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int setProgress() {
        int currentPosition = (int) this.mControlWrapper.getCurrentPosition();
        handleSetProgress((int) this.mControlWrapper.getDuration(), currentPosition);
        return currentPosition;
    }

    public void setAdaptCutout(boolean z) {
        this.mAdaptCutout = z;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkCutout();
    }

    private void checkCutout() {
        if (this.mAdaptCutout) {
            Activity activity = this.mActivity;
            if (activity != null && this.mHasCutout == null) {
                Boolean valueOf = Boolean.valueOf(StatesCutoutUtils.allowDisplayToCutout(activity));
                this.mHasCutout = valueOf;
                if (valueOf.booleanValue()) {
                    this.mCutoutHeight = (int) PlayerUtils.getStatusBarHeightPortrait(this.mActivity);
                }
            }
            VideoLogUtils.d("hasCutout: " + this.mHasCutout + " cutout height: " + this.mCutoutHeight);
        }
    }

    @Override // com.yc.video.controller.InterVideoController
    public boolean hasCutout() {
        Boolean bool = this.mHasCutout;
        return bool != null && bool.booleanValue();
    }

    @Override // com.yc.video.controller.InterVideoController
    public int getCutoutHeight() {
        return this.mCutoutHeight;
    }

    public boolean showNetWarning() {
        return NetworkUtils.getNetworkType(getContext()) == 4 && !VideoViewManager.instance().playOnMobileNetwork();
    }

    protected void togglePlay() {
        this.mControlWrapper.togglePlay();
    }

    protected void toggleFullScreen() {
        if (PlayerUtils.isActivityLiving(this.mActivity)) {
            this.mControlWrapper.toggleFullScreen(this.mActivity);
        }
    }

    protected boolean startFullScreen() {
        if (!PlayerUtils.isActivityLiving(this.mActivity)) {
            return false;
        }
        this.mActivity.setRequestedOrientation(0);
        this.mControlWrapper.startFullScreen();
        return true;
    }

    protected boolean stopFullScreen() {
        if (!PlayerUtils.isActivityLiving(this.mActivity)) {
            return false;
        }
        this.mActivity.setRequestedOrientation(1);
        this.mControlWrapper.stopFullScreen();
        return true;
    }

    public void setEnableOrientation(boolean z) {
        this.mEnableOrientation = z;
    }

    @Override // com.yc.video.controller.OrientationHelper.OnOrientationChangeListener
    public void onOrientationChanged(int i) {
        Activity activity = this.mActivity;
        if (activity == null || activity.isFinishing()) {
            return;
        }
        int i2 = this.mOrientation;
        if (i == -1) {
            this.mOrientation = -1;
            return;
        }
        if (i > 350 || i < 10) {
            if ((this.mActivity.getRequestedOrientation() == 0 && i2 == 0) || this.mOrientation == 0) {
                return;
            }
            this.mOrientation = 0;
            onOrientationPortrait(this.mActivity);
            return;
        }
        if (i > 80 && i < 100) {
            if ((this.mActivity.getRequestedOrientation() == 1 && i2 == 90) || this.mOrientation == 90) {
                return;
            }
            this.mOrientation = 90;
            onOrientationReverseLandscape(this.mActivity);
            return;
        }
        if (i <= 260 || i >= 280) {
            return;
        }
        if ((this.mActivity.getRequestedOrientation() == 1 && i2 == 270) || this.mOrientation == 270) {
            return;
        }
        this.mOrientation = 270;
        onOrientationLandscape(this.mActivity);
    }

    protected void onOrientationPortrait(Activity activity) {
        if (!this.mIsLocked && this.mEnableOrientation) {
            activity.setRequestedOrientation(1);
            this.mControlWrapper.stopFullScreen();
        }
    }

    protected void onOrientationLandscape(Activity activity) {
        activity.setRequestedOrientation(0);
        if (this.mControlWrapper.isFullScreen()) {
            handlePlayerStateChanged(1002);
        } else {
            this.mControlWrapper.startFullScreen();
        }
    }

    protected void onOrientationReverseLandscape(Activity activity) {
        activity.setRequestedOrientation(8);
        if (this.mControlWrapper.isFullScreen()) {
            handlePlayerStateChanged(1002);
        } else {
            this.mControlWrapper.startFullScreen();
        }
    }

    private void handleVisibilityChanged(boolean z, Animation animation) {
        if (!this.mIsLocked) {
            Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
            while (it.hasNext()) {
                it.next().getKey().onVisibilityChanged(z, animation);
            }
        }
        onVisibilityChanged(z, animation);
    }

    private void handlePlayStateChanged(int i) {
        Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getKey().onPlayStateChanged(i);
        }
        onPlayStateChanged(i);
    }

    protected void onPlayStateChanged(int i) {
        if (i == -1) {
            this.mShowing = false;
            return;
        }
        if (i != 0) {
            if (i != 5) {
                return;
            }
            this.mIsLocked = false;
            this.mShowing = false;
            return;
        }
        this.mOrientationHelper.disable();
        this.mOrientation = 0;
        this.mIsLocked = false;
        this.mShowing = false;
        removeAllPrivateComponents();
    }

    private void handlePlayerStateChanged(int i) {
        Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getKey().onPlayerStateChanged(i);
        }
        onPlayerStateChanged(i);
    }

    protected void onPlayerStateChanged(int i) {
        switch (i) {
            case 1001:
                if (this.mEnableOrientation) {
                    this.mOrientationHelper.enable();
                } else {
                    this.mOrientationHelper.disable();
                }
                if (hasCutout()) {
                    StatesCutoutUtils.adaptCutoutAboveAndroidP(getContext(), false);
                    break;
                }
                break;
            case 1002:
                this.mOrientationHelper.enable();
                if (hasCutout()) {
                    StatesCutoutUtils.adaptCutoutAboveAndroidP(getContext(), true);
                    break;
                }
                break;
            case 1003:
                this.mOrientationHelper.disable();
                break;
        }
    }

    private void handleSetProgress(int i, int i2) {
        Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getKey().setProgress(i, i2);
        }
        setProgress(i, i2);
    }

    private void handleLockStateChanged(boolean z) {
        Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getKey().onLockStateChanged(z);
        }
        onLockStateChanged(z);
    }
}