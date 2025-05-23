package com.yc.video.tool;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/* loaded from: classes.dex */
public class CountDownTimer {
    private static final int MSG = 520;
    private TimerListener mCountDownListener;
    private long mCountdownInterval;
    private long mCurrentMillisLeft;
    private long mMillisInFuture;
    private long mStopTimeInFuture;
    private boolean mCancelled = false;
    private boolean mPause = false;
    private Handler mHandler = new Handler() { // from class: com.yc.video.tool.CountDownTimer.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            synchronized (CountDownTimer.this) {
                if (CountDownTimer.this.mCancelled) {
                    return;
                }
                long elapsedRealtime = CountDownTimer.this.mStopTimeInFuture - SystemClock.elapsedRealtime();
                if (elapsedRealtime <= 0) {
                    CountDownTimer.this.mCurrentMillisLeft = 0L;
                    if (CountDownTimer.this.mCountDownListener != null) {
                        CountDownTimer.this.mCountDownListener.onFinish();
                    }
                } else if (elapsedRealtime < CountDownTimer.this.mCountdownInterval) {
                    CountDownTimer.this.mCurrentMillisLeft = 0L;
                    sendMessageDelayed(obtainMessage(CountDownTimer.MSG), elapsedRealtime);
                } else {
                    long elapsedRealtime2 = SystemClock.elapsedRealtime();
                    if (CountDownTimer.this.mCountDownListener != null) {
                        CountDownTimer.this.mCountDownListener.onTick(elapsedRealtime);
                    }
                    CountDownTimer.this.mCurrentMillisLeft = elapsedRealtime;
                    long elapsedRealtime3 = (elapsedRealtime2 + CountDownTimer.this.mCountdownInterval) - SystemClock.elapsedRealtime();
                    while (elapsedRealtime3 < 0) {
                        elapsedRealtime3 += CountDownTimer.this.mCountdownInterval;
                    }
                    sendMessageDelayed(obtainMessage(CountDownTimer.MSG), elapsedRealtime3);
                }
            }
        }
    };

    public interface TimerListener {
        void onFinish();

        void onStart();

        void onTick(long j);
    }

    public CountDownTimer() {
    }

    public CountDownTimer(long j, long j2) {
        this.mMillisInFuture = j;
        this.mCountdownInterval = j2;
    }

    public final synchronized void start() {
        if (this.mMillisInFuture <= 0 && this.mCountdownInterval <= 0) {
            throw new RuntimeException("you must set the millisInFuture > 0 or countdownInterval >0");
        }
        this.mCancelled = false;
        this.mStopTimeInFuture = SystemClock.elapsedRealtime() + this.mMillisInFuture;
        this.mPause = false;
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(MSG));
        TimerListener timerListener = this.mCountDownListener;
        if (timerListener != null) {
            timerListener.onStart();
        }
    }

    public final synchronized void cancel() {
        Handler handler = this.mHandler;
        if (handler != null) {
            this.mPause = false;
            handler.removeMessages(MSG);
            this.mCancelled = true;
        }
    }

    public final synchronized void pause() {
        Handler handler = this.mHandler;
        if (handler != null) {
            if (this.mCancelled) {
                return;
            }
            if (this.mCurrentMillisLeft < this.mCountdownInterval) {
                return;
            }
            if (!this.mPause) {
                handler.removeMessages(MSG);
                this.mPause = true;
            }
        }
    }

    public final synchronized void resume() {
        if (this.mMillisInFuture <= 0 && this.mCountdownInterval <= 0) {
            throw new RuntimeException("you must set the millisInFuture > 0 or countdownInterval >0");
        }
        if (this.mCancelled) {
            return;
        }
        if (this.mCurrentMillisLeft >= this.mCountdownInterval && this.mPause) {
            this.mStopTimeInFuture = SystemClock.elapsedRealtime() + this.mCurrentMillisLeft;
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(MSG));
            this.mPause = false;
        }
    }

    public void setMillisInFuture(long j) {
        this.mMillisInFuture = j;
    }

    public void setCountdownInterval(long j) {
        this.mCountdownInterval = j;
    }

    public void setCountDownListener(TimerListener timerListener) {
        this.mCountDownListener = timerListener;
    }
}