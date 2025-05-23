package com.belon.camera.callback;

import com.belon.camera.ErrorCode;

/* loaded from: classes.dex */
public interface OTACallback {
    void onError(ErrorCode errorCode, Exception exc);

    void onPercent(int i);
}