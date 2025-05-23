package com.yc.kernel.inter;

/* loaded from: classes.dex */
public interface VideoPlayerListener {
    void onCompletion();

    void onError(int i, String str);

    void onInfo(int i, int i2);

    void onPrepared();

    void onVideoSizeChanged(int i, int i2);
}