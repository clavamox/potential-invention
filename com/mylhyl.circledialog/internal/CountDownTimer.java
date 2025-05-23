package com.mylhyl.circledialog.internal;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/* loaded from: classes.dex */
public abstract class CountDownTimer {
    private static final int MSG = 1;
    private final long mCountdownInterval;
    private final long mMillisInFuture;
    private long mStopTimeInFuture;
    private long mMillisLeft = 0;
    private boolean mCancelled = false;
    private final Handler mHandler = new Handler() { // from class: com.mylhyl.circledialog.internal.CountDownTimer.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            synchronized (CountDownTimer.this) {
                if (CountDownTimer.this.mCancelled) {
                    return;
                }
                long j = CountDownTimer.this.mStopTimeInFuture;
                CountDownTimer.this.mStopTimeInFuture = SystemClock.elapsedRealtime();
                CountDownTimer.this.mMillisLeft += CountDownTimer.this.mStopTimeInFuture - j;
                if (CountDownTimer.this.mMillisInFuture > CountDownTimer.this.mMillisLeft) {
                    long j2 = CountDownTimer.this.mMillisInFuture - CountDownTimer.this.mMillisLeft;
                    CountDownTimer.this.onTick(j2);
                    if (j2 > CountDownTimer.this.mCountdownInterval) {
                        j2 = ((CountDownTimer.this.mStopTimeInFuture + CountDownTimer.this.mCountdownInterval) - SystemClock.elapsedRealtime()) - (CountDownTimer.this.mMillisLeft % CountDownTimer.this.mCountdownInterval);
                    }
                    while (j2 < 0) {
                        j2 += CountDownTimer.this.mCountdownInterval;
                    }
                    sendMessageDelayed(obtainMessage(1), j2);
                } else {
                    removeMessages(1);
                    CountDownTimer.this.onFinish();
                }
            }
        }
    };

    public abstract void onFinish();

    public abstract void onTick(long j);

    public CountDownTimer(long j, long j2) {
        this.mMillisInFuture = j;
        this.mCountdownInterval = j2;
    }

    public final synchronized void cancel() {
        this.mCancelled = true;
        this.mHandler.removeMessages(1);
    }

    public final synchronized CountDownTimer start() {
        this.mCancelled = false;
        if (this.mMillisInFuture <= this.mMillisLeft) {
            this.mCancelled = true;
            onFinish();
            return this;
        }
        this.mMillisLeft = 0L;
        this.mStopTimeInFuture = SystemClock.elapsedRealtime();
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(1));
        return this;
    }

    public final synchronized void restart() {
        this.mMillisLeft = 0L;
        this.mStopTimeInFuture = SystemClock.elapsedRealtime();
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(1));
    }

    public final synchronized CountDownTimer pause() {
        this.mHandler.removeMessages(1);
        if (this.mMillisInFuture > this.mMillisLeft) {
            long j = this.mStopTimeInFuture;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.mStopTimeInFuture = elapsedRealtime;
            long j2 = this.mMillisLeft + (elapsedRealtime - j);
            this.mMillisLeft = j2;
            onTick(j2);
        }
        return this;
    }

    public final synchronized void resume() {
        if (this.mMillisInFuture > this.mMillisLeft) {
            this.mStopTimeInFuture = SystemClock.elapsedRealtime();
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(1));
        }
    }
}