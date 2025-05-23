package com.yc.video.old.surface;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

@Deprecated
/* loaded from: classes.dex */
public class VideoSurfaceView extends SurfaceView {
    private static final float EQUAL_FLOAT = 1.0E-7f;
    private SurfaceHolder.Callback callback;
    private OnSurfaceListener onSurfaceListener;
    private int videoHeight;
    private int videoWidth;

    public interface OnSurfaceListener {
        void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3);

        void surfaceCreated(SurfaceHolder surfaceHolder);

        void surfaceDestroyed(SurfaceHolder surfaceHolder);
    }

    public VideoSurfaceView(Context context) {
        super(context);
        this.callback = new SurfaceHolder.Callback() { // from class: com.yc.video.old.surface.VideoSurfaceView.1
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (VideoSurfaceView.this.onSurfaceListener != null) {
                    VideoSurfaceView.this.onSurfaceListener.surfaceCreated(surfaceHolder);
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                if (VideoSurfaceView.this.onSurfaceListener != null) {
                    VideoSurfaceView.this.onSurfaceListener.surfaceChanged(surfaceHolder, i, i2, i3);
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (VideoSurfaceView.this.onSurfaceListener != null) {
                    VideoSurfaceView.this.onSurfaceListener.surfaceDestroyed(surfaceHolder);
                }
            }
        };
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.callback != null) {
            getHolder().removeCallback(this.callback);
        }
    }

    public OnSurfaceListener getOnSurfaceListener() {
        return this.onSurfaceListener;
    }

    public void setOnSurfaceListener(OnSurfaceListener onSurfaceListener) {
        this.onSurfaceListener = onSurfaceListener;
        getHolder().addCallback(this.callback);
    }

    public void addTextureView(FrameLayout frameLayout, VideoSurfaceView videoSurfaceView) {
        frameLayout.removeView(videoSurfaceView);
        frameLayout.addView(videoSurfaceView, 0, new FrameLayout.LayoutParams(-1, -1, 17));
    }

    public void adaptVideoSize(int i, int i2) {
        if (this.videoWidth == i || this.videoHeight == i2) {
            return;
        }
        this.videoWidth = i;
        this.videoHeight = i2;
        requestLayout();
    }

    @Override // android.view.View
    public void setRotation(float f) {
        if (f != getRotation()) {
            super.setRotation(f);
            requestLayout();
        }
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        float rotation = getRotation();
        if ((Math.abs(rotation - 90.0f) > EQUAL_FLOAT && Math.abs(90.0f - rotation) > EQUAL_FLOAT) || (Math.abs(rotation - 270.0f) > EQUAL_FLOAT && Math.abs(270.0f - rotation) > EQUAL_FLOAT)) {
            i2 = i;
            i = i2;
        }
        int defaultSize = getDefaultSize(this.videoWidth, i);
        int defaultSize2 = getDefaultSize(this.videoHeight, i2);
        if (this.videoWidth > 0 && this.videoHeight > 0) {
            int mode = View.MeasureSpec.getMode(i);
            int size = View.MeasureSpec.getSize(i);
            int mode2 = View.MeasureSpec.getMode(i2);
            int size2 = View.MeasureSpec.getSize(i2);
            if (mode == 1073741824 && mode2 == 1073741824) {
                int i4 = this.videoWidth;
                int i5 = i4 * size2;
                int i6 = this.videoHeight;
                if (i5 < size * i6) {
                    defaultSize = (i4 * size2) / i6;
                } else if (i4 * size2 > size * i6) {
                    defaultSize2 = (i6 * size) / i4;
                    defaultSize = size;
                } else {
                    defaultSize = size;
                }
                defaultSize2 = size2;
            } else if (mode == 1073741824) {
                int i7 = this.videoHeight;
                int i8 = this.videoWidth;
                int i9 = (size * i7) / i8;
                if (mode2 != Integer.MIN_VALUE || i9 <= size2) {
                    defaultSize = size;
                    defaultSize2 = i9;
                } else {
                    defaultSize = (i8 * size2) / i7;
                    defaultSize2 = size2;
                }
            } else {
                if (mode2 == 1073741824) {
                    int i10 = this.videoWidth;
                    int i11 = this.videoHeight;
                    int i12 = (size2 * i10) / i11;
                    if (mode != Integer.MIN_VALUE || i12 <= size) {
                        defaultSize2 = size2;
                        defaultSize = i12;
                    } else {
                        defaultSize2 = (i11 * size) / i10;
                    }
                } else {
                    int i13 = this.videoWidth;
                    int i14 = this.videoHeight;
                    if (mode2 != Integer.MIN_VALUE || i14 <= size2) {
                        i3 = i13;
                        size2 = i14;
                    } else {
                        i3 = (size2 * i13) / i14;
                    }
                    if (mode != Integer.MIN_VALUE || i3 <= size) {
                        defaultSize = i3;
                        defaultSize2 = size2;
                    } else {
                        defaultSize2 = (i14 * size) / i13;
                    }
                }
                defaultSize = size;
            }
        }
        setMeasuredDimension(defaultSize, defaultSize2);
    }
}