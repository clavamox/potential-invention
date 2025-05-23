package com.belon.camera.callback;

import com.belon.camera.ErrorCode;

/* loaded from: classes.dex */
public interface BatteryCallback {
    void onError(ErrorCode errorCode, Exception exc);

    void receiveBattery(boolean z, int i);
}