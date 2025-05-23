package com.yc.video.surface;

import android.content.Context;

/* loaded from: classes.dex */
public class SurfaceViewFactory extends SurfaceFactory {
    public static SurfaceViewFactory create() {
        return new SurfaceViewFactory();
    }

    @Override // com.yc.video.surface.SurfaceFactory
    public InterSurfaceView createRenderView(Context context) {
        return new RenderSurfaceView(context);
    }
}