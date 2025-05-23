package com.yc.video.controller;

import com.yc.video.ui.view.InterControlView;

/* loaded from: classes.dex */
public interface IGestureComponent extends InterControlView {
    void onBrightnessChange(int i);

    void onPositionChange(int i, int i2, int i3);

    void onStartSlide();

    void onStopSlide();

    void onVolumeChange(int i);
}