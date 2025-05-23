package com.yc.video.old.player;

import java.util.Map;

@Deprecated
/* loaded from: classes.dex */
public interface IVideoPlayer {
    void continueFromLastPosition(boolean z);

    void enterFullScreen();

    void enterTinyWindow();

    void enterVerticalScreenScreen();

    boolean exitFullScreen();

    boolean exitTinyWindow();

    int getBufferPercentage();

    long getCurrentPosition();

    long getDuration();

    int getMaxVolume();

    int getPlayType();

    float getSpeed(float f);

    long getTcpSpeed();

    int getVolume();

    boolean isBufferingPaused();

    boolean isBufferingPlaying();

    boolean isCompleted();

    boolean isError();

    boolean isFullScreen();

    boolean isIdle();

    boolean isNormal();

    boolean isPaused();

    boolean isPlaying();

    boolean isPrepared();

    boolean isPreparing();

    boolean isTinyWindow();

    void pause();

    void release();

    void releasePlayer();

    void restart();

    void seekTo(long j);

    void setSpeed(float f);

    void setUp(String str, Map<String, String> map);

    void setVolume(int i);

    void start();

    void start(long j);
}