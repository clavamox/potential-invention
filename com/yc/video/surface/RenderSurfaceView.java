package com.yc.video.surface;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import com.yc.kernel.inter.AbstractVideoPlayer;

/* loaded from: classes.dex */
public class RenderSurfaceView extends SurfaceView implements InterSurfaceView {
    private SurfaceHolder.Callback callback;
    private MeasureHelper mMeasureHelper;
    private AbstractVideoPlayer mMediaPlayer;

    @Override // com.yc.video.surface.InterSurfaceView
    public View getView() {
        return this;
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.callback != null) {
            getHolder().removeCallback(this.callback);
        }
    }

    public RenderSurfaceView(Context context) {
        super(context);
        this.callback = new SurfaceHolder.Callback() { // from class: com.yc.video.surface.RenderSurfaceView.1
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (RenderSurfaceView.this.mMediaPlayer != null) {
                    RenderSurfaceView.this.mMediaPlayer.setSurface(surfaceHolder.getSurface());
                }
            }
        };
        init(context);
    }

    private void init(Context context) {
        this.mMeasureHelper = new MeasureHelper();
        getHolder().addCallback(this.callback);
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
        return getDrawingCache();
    }

    @Override // com.yc.video.surface.InterSurfaceView
    public void release() {
        if (this.callback != null) {
            getHolder().removeCallback(this.callback);
        }
    }

    @Override // android.view.SurfaceView, android.view.View
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