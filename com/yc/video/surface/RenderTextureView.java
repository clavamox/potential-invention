package com.yc.video.surface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import com.yc.kernel.inter.AbstractVideoPlayer;

/* loaded from: classes.dex */
public class RenderTextureView extends TextureView implements InterSurfaceView {
    private TextureView.SurfaceTextureListener listener;
    private MeasureHelper mMeasureHelper;
    private AbstractVideoPlayer mMediaPlayer;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;

    @Override // com.yc.video.surface.InterSurfaceView
    public View getView() {
        return this;
    }

    public RenderTextureView(Context context) {
        super(context);
        this.listener = new TextureView.SurfaceTextureListener() { // from class: com.yc.video.surface.RenderTextureView.1
            @Override // android.view.TextureView.SurfaceTextureListener
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                if (RenderTextureView.this.mSurfaceTexture != null) {
                    RenderTextureView renderTextureView = RenderTextureView.this;
                    renderTextureView.setSurfaceTexture(renderTextureView.mSurfaceTexture);
                    return;
                }
                RenderTextureView.this.mSurfaceTexture = surfaceTexture;
                RenderTextureView.this.mSurface = new Surface(surfaceTexture);
                if (RenderTextureView.this.mMediaPlayer != null) {
                    RenderTextureView.this.mMediaPlayer.setSurface(RenderTextureView.this.mSurface);
                }
            }
        };
        init(context);
    }

    private void init(Context context) {
        this.mMeasureHelper = new MeasureHelper();
        setSurfaceTextureListener(this.listener);
    }

    @Override // com.yc.video.surface.InterSurfaceView
    public void attachToPlayer(AbstractVideoPlayer abstractVideoPlayer) {
        this.mMediaPlayer = abstractVideoPlayer;
    }

    @Override // com.yc.video.surface.InterSurfaceView
    public void setVideoSize(int i, int i2) {
        if (i <= 0 || i2 <= 0) {
            return;
        }
        this.mMeasureHelper.setVideoSize(i, i2);
        requestLayout();
    }

    @Override // com.yc.video.surface.InterSurfaceView
    public void setVideoRotation(int i) {
        this.mMeasureHelper.setVideoRotation(i);
        setRotation(i);
    }

    @Override // com.yc.video.surface.InterSurfaceView
    public void setScaleType(int i) {
        this.mMeasureHelper.setScreenScale(i);
        requestLayout();
    }

    @Override // com.yc.video.surface.InterSurfaceView
    public Bitmap doScreenShot() {
        return getBitmap();
    }

    @Override // com.yc.video.surface.InterSurfaceView
    public void release() {
        Surface surface = this.mSurface;
        if (surface != null) {
            surface.release();
        }
        SurfaceTexture surfaceTexture = this.mSurfaceTexture;
        if (surfaceTexture != null) {
            surfaceTexture.release();
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int[] doMeasure = this.mMeasureHelper.doMeasure(i, i2);
        setMeasuredDimension(doMeasure[0], doMeasure[1]);
    }

    @Override // android.view.View
    public void setRotation(float f) {
        if (f != getRotation()) {
            super.setRotation(f);
            requestLayout();
        }
    }
}