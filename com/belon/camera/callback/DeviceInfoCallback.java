package com.belon.camera.callback;

import com.belon.camera.ErrorCode;

/* loaded from: classes.dex */
public interface DeviceInfoCallback {
    void onError(ErrorCode errorCode, Exception exc);

    void receiveDeviceInfo(byte[] bArr);
}