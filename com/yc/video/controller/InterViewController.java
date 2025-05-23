package com.yc.video.controller;

import com.yc.video.ui.view.InterControlView;

/* loaded from: classes.dex */
public interface InterViewController {
    void addControlComponent(InterControlView interControlView, boolean z);

    void addControlComponent(InterControlView... interControlViewArr);

    void removeAllControlComponent();

    void removeAllPrivateComponents();

    void removeControlComponent(InterControlView interControlView);
}