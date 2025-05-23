package com.belon.camera.callback;

import com.belon.camera.ErrorCode;

/* loaded from: classes.dex */
public interface VersionCallback {
    void onError(ErrorCode errorCode, Exception exc);

    void onGetVersion(String str);
}