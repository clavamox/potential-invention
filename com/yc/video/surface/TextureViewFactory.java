package com.yc.video.surface;

import android.content.Context;

/* loaded from: classes.dex */
public class TextureViewFactory extends SurfaceFactory {
    public static TextureViewFactory create() {
        return new TextureViewFactory();
    }

    @Override // com.yc.video.surface.SurfaceFactory
    public InterSurfaceView createRenderView(Context context) {
        return new RenderTextureView(context);
    }
}