package com.yc.video.surface;

import android.graphics.Bitmap;
import android.view.View;
import com.yc.kernel.inter.AbstractVideoPlayer;

/* loaded from: classes.dex */
public interface InterSurfaceView {
    void attachToPlayer(AbstractVideoPlayer abstractVideoPlayer);

    Bitmap doScreenShot();

    View getView();

    void release();

    void setScaleType(int i);

    void setVideoRotation(int i);

    void setVideoSize(int i, int i2);
}