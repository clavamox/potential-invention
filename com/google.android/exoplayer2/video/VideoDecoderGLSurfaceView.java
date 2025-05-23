package com.google.android.exoplayer2.video;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class VideoDecoderGLSurfaceView extends GLSurfaceView {
    private final VideoDecoderRenderer renderer;

    public VideoDecoderGLSurfaceView(Context context) {
        this(context, null);
    }

    public VideoDecoderGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        VideoDecoderRenderer videoDecoderRenderer = new VideoDecoderRenderer(this);
        this.renderer = videoDecoderRenderer;
        setPreserveEGLContextOnPause(true);
        setEGLContextClientVersion(2);
        setRenderer(videoDecoderRenderer);
        setRenderMode(0);
    }

    public VideoDecoderOutputBufferRenderer getVideoDecoderOutputBufferRenderer() {
        return this.renderer;
    }
}