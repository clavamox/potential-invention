package com.google.android.exoplayer2;

import android.content.Context;
import android.os.PowerManager;
import com.google.android.exoplayer2.util.Log;

/* loaded from: classes.dex */
final class WakeLockManager {
    private static final String TAG = "WakeLockManager";
    private static final String WAKE_LOCK_TAG = "ExoPlayer:WakeLockManager";
    private boolean enabled;
    private final PowerManager powerManager;
    private boolean stayAwake;
    private PowerManager.WakeLock wakeLock;

    public WakeLockManager(Context context) {
        this.powerManager = (PowerManager) context.getSystemService("power");
    }

    public void setEnabled(boolean z) {
        if (z && this.wakeLock == null) {
            PowerManager powerManager = this.powerManager;
            if (powerManager == null) {
                Log.w(TAG, "PowerManager was null, therefore the WakeLock was not created.");
                return;
            }
            this.wakeLock = powerManager.newWakeLock(1, WAKE_LOCK_TAG);
        }
        this.enabled = z;
        updateWakeLock();
    }

    public void setStayAwake(boolean z) {
        this.stayAwake = z;
        updateWakeLock();
    }

    private void updateWakeLock() {
        PowerManager.WakeLock wakeLock = this.wakeLock;
        if (wakeLock != null) {
            if (this.enabled) {
                if (this.stayAwake && !wakeLock.isHeld()) {
                    this.wakeLock.acquire();
                    return;
                } else {
                    if (this.stayAwake || !this.wakeLock.isHeld()) {
                        return;
                    }
                    this.wakeLock.release();
                    return;
                }
            }
            if (wakeLock.isHeld()) {
                this.wakeLock.release();
            }
        }
    }
}