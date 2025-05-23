package com.yc.video.player;

import android.graphics.Bitmap;

/* loaded from: classes.dex */
public interface InterVideoPlayer {
    Bitmap doScreenShot();

    int getBufferedPercentage();

    long getCurrentPosition();

    long getDuration();

    float getSpeed();

    long getTcpSpeed();

    String getUrl();

    int[] getVideoSize();

    boolean isFullScreen();

    boolean isMute();

    boolean isPlaying();

    boolean isTinyScreen();

    void pause();

    void replay(boolean z);

    void seekTo(long j);

    void setMirrorRotation(boolean z);

    void setMute(boolean z);

    void setRotation(float f);

    void setScreenScaleType(int i);

    void setSpeed(float f);

    void setUrl(String str);

    void start();

    void startFullScreen();

    void startTinyScreen();

    void stopFullScreen();

    void stopTinyScreen();
}