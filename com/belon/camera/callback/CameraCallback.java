package com.belon.camera.callback;

import android.graphics.Bitmap;
import com.belon.camera.ErrorCode;

/* loaded from: classes.dex */
public abstract class CameraCallback {
    public void connectSuccess(boolean z) {
    }

    public void lostBitmap(int i, int i2, int i3) {
    }

    public void onError(ErrorCode errorCode, Exception exc) {
    }

    public void receiveAudio(byte[] bArr) {
    }

    public abstract void receiveVedio(Bitmap bitmap, int i, int i2, int i3, int i4);
}