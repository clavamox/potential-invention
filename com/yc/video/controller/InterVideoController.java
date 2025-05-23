package com.yc.video.controller;

/* loaded from: classes.dex */
public interface InterVideoController {
    void destroy();

    int getCutoutHeight();

    boolean hasCutout();

    void hide();

    boolean isLocked();

    boolean isShowing();

    void setLocked(boolean z);

    void show();

    void startFadeOut();

    void startProgress();

    void stopFadeOut();

    void stopProgress();
}