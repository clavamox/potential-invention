package com.yc.video.ui.view;

import android.view.View;
import android.view.animation.Animation;
import com.yc.video.bridge.ControlWrapper;

/* loaded from: classes.dex */
public interface InterControlView {
    void attach(ControlWrapper controlWrapper);

    View getView();

    void onLockStateChanged(boolean z);

    void onPlayStateChanged(int i);

    void onPlayerStateChanged(int i);

    void onVisibilityChanged(boolean z, Animation animation);

    void setProgress(int i, int i2);
}