package com.yc.video.old.controller;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.yc.video.old.player.OldVideoPlayer;
import java.util.Timer;
import java.util.TimerTask;

@Deprecated
/* loaded from: classes.dex */
public abstract class AbsVideoPlayerController extends FrameLayout implements View.OnTouchListener, IVideoController {
    private static final int THRESHOLD = 80;
    private Context mContext;
    private float mDownX;
    private float mDownY;
    private float mGestureDownBrightness;
    private long mGestureDownPosition;
    private int mGestureDownVolume;
    private boolean mNeedChangeBrightness;
    private boolean mNeedChangePosition;
    private boolean mNeedChangeVolume;
    private long mNewPosition;
    private TimerTask mUpdateNetSpeedTask;
    private Timer mUpdateNetSpeedTimer;
    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;
    protected OldVideoPlayer mVideoPlayer;

    public AbsVideoPlayerController(Context context) {
        super(context);
        this.mContext = context;
        setOnTouchListener(this);
    }

    public void setVideoPlayer(OldVideoPlayer oldVideoPlayer) {
        this.mVideoPlayer = oldVideoPlayer;
    }

    protected void startUpdateNetSpeedTimer() {
        cancelUpdateNetSpeedTimer();
        if (this.mUpdateNetSpeedTimer == null) {
            this.mUpdateNetSpeedTimer = new Timer();
        }
        if (this.mUpdateNetSpeedTask == null) {
            this.mUpdateNetSpeedTask = new TimerTask() { // from class: com.yc.video.old.controller.AbsVideoPlayerController.1
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    AbsVideoPlayerController.this.post(new Runnable() { // from class: com.yc.video.old.controller.AbsVideoPlayerController.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            AbsVideoPlayerController.this.updateNetSpeedProgress();
                        }
                    });
                }
            };
        }
        this.mUpdateNetSpeedTimer.schedule(this.mUpdateNetSpeedTask, 0L, 100L);
    }

    protected void cancelUpdateNetSpeedTimer() {
        Timer timer = this.mUpdateNetSpeedTimer;
        if (timer != null) {
            timer.cancel();
            this.mUpdateNetSpeedTimer = null;
        }
        TimerTask timerTask = this.mUpdateNetSpeedTask;
        if (timerTask != null) {
            timerTask.cancel();
            this.mUpdateNetSpeedTask = null;
        }
    }

    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (this.mUpdateProgressTimer == null) {
            this.mUpdateProgressTimer = new Timer();
        }
        if (this.mUpdateProgressTimerTask == null) {
            this.mUpdateProgressTimerTask = new TimerTask() { // from class: com.yc.video.old.controller.AbsVideoPlayerController.2
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    AbsVideoPlayerController.this.post(new Runnable() { // from class: com.yc.video.old.controller.AbsVideoPlayerController.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            AbsVideoPlayerController.this.updateProgress();
                        }
                    });
                }
            };
        }
        this.mUpdateProgressTimer.schedule(this.mUpdateProgressTimerTask, 0L, 1000L);
    }

    protected void cancelUpdateProgressTimer() {
        Timer timer = this.mUpdateProgressTimer;
        if (timer != null) {
            timer.cancel();
            this.mUpdateProgressTimer = null;
        }
        TimerTask timerTask = this.mUpdateProgressTimerTask;
        if (timerTask != null) {
            timerTask.cancel();
            this.mUpdateProgressTimerTask = null;
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (this.mVideoPlayer.getPlayType() == 1002) {
            return setOnTouch(view, motionEvent);
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x0049, code lost:
    
        if (r10 != 3) goto L62;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean setOnTouch(android.view.View r9, android.view.MotionEvent r10) {
        /*
            Method dump skipped, instructions count: 379
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.yc.video.old.controller.AbsVideoPlayerController.setOnTouch(android.view.View, android.view.MotionEvent):boolean");
    }
}