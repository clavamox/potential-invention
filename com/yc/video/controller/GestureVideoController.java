package com.yc.video.controller;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.yc.kernel.utils.VideoLogUtils;
import com.yc.video.tool.PlayerUtils;
import com.yc.video.ui.view.InterControlView;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class GestureVideoController extends BaseVideoController implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, View.OnTouchListener {
    private AudioManager mAudioManager;
    private float mBrightness;
    private boolean mCanChangePosition;
    private boolean mCanSlide;
    private boolean mChangeBrightness;
    private boolean mChangePosition;
    private boolean mChangeVolume;
    private int mCurPlayState;
    private boolean mEnableInNormal;
    private boolean mFirstTouch;
    private GestureDetector mGestureDetector;
    private int mHalfScreen;
    private boolean mIsGestureEnabled;
    private int mSeekPosition;
    private int mStreamVolume;

    @Override // android.view.GestureDetector.OnDoubleTapListener
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public GestureVideoController(Context context) {
        super(context);
        this.mIsGestureEnabled = true;
        this.mCanChangePosition = true;
    }

    public GestureVideoController(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsGestureEnabled = true;
        this.mCanChangePosition = true;
    }

    public GestureVideoController(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsGestureEnabled = true;
        this.mCanChangePosition = true;
    }

    @Override // com.yc.video.controller.BaseVideoController
    protected void initView(Context context) {
        super.initView(context);
        this.mHalfScreen = PlayerUtils.getScreenWidth(getContext(), true) / 2;
        this.mAudioManager = (AudioManager) getContext().getSystemService("audio");
        this.mGestureDetector = new GestureDetector(getContext(), this);
        setOnTouchListener(this);
    }

    public void setCanChangePosition(boolean z) {
        this.mCanChangePosition = z;
    }

    public void setEnableInNormal(boolean z) {
        this.mEnableInNormal = z;
    }

    public void setGestureEnabled(boolean z) {
        this.mIsGestureEnabled = z;
    }

    @Override // com.yc.video.controller.BaseVideoController
    public void setPlayerState(int i) {
        super.setPlayerState(i);
        if (i == 1001) {
            this.mCanSlide = this.mEnableInNormal;
        } else if (i == 1002) {
            this.mCanSlide = true;
        }
    }

    @Override // com.yc.video.controller.BaseVideoController
    public void setPlayState(int i) {
        super.setPlayState(i);
        this.mCurPlayState = i;
    }

    private boolean isInPlaybackState() {
        int i;
        return (this.mControlWrapper == null || (i = this.mCurPlayState) == -1 || i == 0 || i == 1 || i == 2 || i == 8 || i == 5) ? false : true;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.mGestureDetector.onTouchEvent(motionEvent);
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent motionEvent) {
        if (isInPlaybackState() && this.mIsGestureEnabled && !PlayerUtils.isEdge(getContext(), motionEvent)) {
            this.mStreamVolume = this.mAudioManager.getStreamVolume(3);
            Activity scanForActivity = PlayerUtils.scanForActivity(getContext());
            if (scanForActivity == null) {
                this.mBrightness = 0.0f;
            } else {
                this.mBrightness = scanForActivity.getWindow().getAttributes().screenBrightness;
            }
            this.mFirstTouch = true;
            this.mChangePosition = false;
            this.mChangeBrightness = false;
            this.mChangeVolume = false;
        }
        return true;
    }

    @Override // android.view.GestureDetector.OnDoubleTapListener
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if (!isInPlaybackState()) {
            return true;
        }
        this.mControlWrapper.toggleShowState();
        return true;
    }

    @Override // android.view.GestureDetector.OnDoubleTapListener
    public boolean onDoubleTap(MotionEvent motionEvent) {
        if (isLocked() || !isInPlaybackState()) {
            return true;
        }
        togglePlay();
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (isInPlaybackState() && this.mIsGestureEnabled && this.mCanSlide && !isLocked() && !PlayerUtils.isEdge(getContext(), motionEvent)) {
            float x = motionEvent.getX() - motionEvent2.getX();
            float y = motionEvent.getY() - motionEvent2.getY();
            if (this.mFirstTouch) {
                boolean z = Math.abs(f) >= Math.abs(f2);
                this.mChangePosition = z;
                if (!z) {
                    if (this.mHalfScreen == 0) {
                        this.mHalfScreen = PlayerUtils.getScreenWidth(getContext(), true) / 2;
                    }
                    if (motionEvent2.getX() > this.mHalfScreen) {
                        this.mChangeVolume = true;
                    } else {
                        this.mChangeBrightness = true;
                    }
                }
                if (this.mChangePosition) {
                    this.mChangePosition = this.mCanChangePosition;
                }
                if (this.mChangePosition || this.mChangeBrightness || this.mChangeVolume) {
                    Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
                    while (it.hasNext()) {
                        InterControlView key = it.next().getKey();
                        if (key instanceof IGestureComponent) {
                            ((IGestureComponent) key).onStartSlide();
                        }
                    }
                }
                this.mFirstTouch = false;
            }
            if (this.mChangePosition) {
                slideToChangePosition(x);
            } else if (this.mChangeBrightness) {
                slideToChangeBrightness(y);
            } else if (this.mChangeVolume) {
                slideToChangeVolume(y);
            }
        }
        return true;
    }

    protected void slideToChangePosition(float f) {
        int measuredWidth = getMeasuredWidth();
        int duration = (int) this.mControlWrapper.getDuration();
        int currentPosition = (int) this.mControlWrapper.getCurrentPosition();
        int i = (int) ((((-f) / measuredWidth) * 120000.0f) + currentPosition);
        if (i > duration) {
            i = duration;
        }
        if (i < 0) {
            i = 0;
        }
        Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            InterControlView key = it.next().getKey();
            if (key instanceof IGestureComponent) {
                ((IGestureComponent) key).onPositionChange(i, currentPosition, duration);
            }
        }
        this.mSeekPosition = i;
    }

    protected void slideToChangeBrightness(float f) {
        Activity scanForActivity = PlayerUtils.scanForActivity(getContext());
        if (PlayerUtils.isActivityLiving(scanForActivity)) {
            Window window = scanForActivity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            int measuredHeight = getMeasuredHeight();
            if (this.mBrightness == -1.0f) {
                this.mBrightness = 0.5f;
            }
            float f2 = (((f * 2.0f) / measuredHeight) * 1.0f) + this.mBrightness;
            if (f2 < 0.0f) {
                f2 = 0.0f;
            }
            float f3 = f2 <= 1.0f ? f2 : 1.0f;
            int i = (int) (100.0f * f3);
            attributes.screenBrightness = f3;
            window.setAttributes(attributes);
            Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
            while (it.hasNext()) {
                InterControlView key = it.next().getKey();
                if (key instanceof IGestureComponent) {
                    ((IGestureComponent) key).onBrightnessChange(i);
                }
            }
        }
    }

    protected void slideToChangeVolume(float f) {
        float streamMaxVolume = this.mAudioManager.getStreamMaxVolume(3);
        float measuredHeight = this.mStreamVolume + (((f * 2.0f) / getMeasuredHeight()) * streamMaxVolume);
        if (measuredHeight > streamMaxVolume) {
            measuredHeight = streamMaxVolume;
        }
        if (measuredHeight < 0.0f) {
            measuredHeight = 0.0f;
        }
        int i = (int) ((measuredHeight / streamMaxVolume) * 100.0f);
        this.mAudioManager.setStreamVolume(3, (int) measuredHeight, 0);
        Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            InterControlView key = it.next().getKey();
            if (key instanceof IGestureComponent) {
                ((IGestureComponent) key).onVolumeChange(i);
            }
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        VideoLogUtils.e("事件----------事件拦截----------");
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        VideoLogUtils.e("事件----------事件分发----------");
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        VideoLogUtils.e("事件----------事件触摸----------");
        if (!this.mGestureDetector.onTouchEvent(motionEvent)) {
            int action = motionEvent.getAction();
            if (action == 1) {
                stopSlide();
                if (this.mSeekPosition > 0) {
                    this.mControlWrapper.seekTo(this.mSeekPosition);
                    this.mSeekPosition = 0;
                }
            } else if (action == 3) {
                stopSlide();
                this.mSeekPosition = 0;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    private void stopSlide() {
        Iterator<Map.Entry<InterControlView, Boolean>> it = this.mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            InterControlView key = it.next().getKey();
            if (key instanceof IGestureComponent) {
                ((IGestureComponent) key).onStopSlide();
            }
        }
    }
}