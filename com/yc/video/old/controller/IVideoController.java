package com.yc.video.old.controller;

import android.widget.ImageView;

@Deprecated
/* loaded from: classes.dex */
public interface IVideoController {
    void destroy();

    boolean getLock();

    void hideChangeBrightness();

    void hideChangePosition();

    void hideChangeVolume();

    ImageView imageView();

    void onBatterStateChanged(int i);

    void onPlayModeChanged(int i);

    void onPlayStateChanged(int i);

    void reset();

    void setHideTime(long j);

    void setImage(int i);

    void setLength(long j);

    void setLength(String str);

    void setLoadingType(int i);

    void setTitle(String str);

    void setTopPadding(float f);

    void setTopVisibility(boolean z);

    void setTvAndAudioVisibility(boolean z, boolean z2);

    void showChangeBrightness(int i);

    void showChangePosition(long j, int i);

    void showChangeVolume(int i);

    void updateNetSpeedProgress();

    void updateProgress();
}