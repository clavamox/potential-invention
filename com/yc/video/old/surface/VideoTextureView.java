package com.yc.video.old.surface;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import com.yc.kernel.utils.VideoLogUtils;

@Deprecated
/* loaded from: classes.dex */
public class VideoTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private static final float EQUAL_FLOAT = 1.0E-7f;
    private OnTextureListener onTextureListener;
    private int videoHeight;
    private int videoWidth;

    public interface OnTextureListener {
        void onSurfaceAvailable(SurfaceTexture surfaceTexture);

        boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceSizeChanged(SurfaceTexture surfaceTexture, int i, int i2);

        void onSurfaceUpdated(SurfaceTexture surfaceTexture);
    }

    public VideoTextureView(Context context) {
        super(context);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        OnTextureListener onTextureListener = this.onTextureListener;
        if (onTextureListener != null) {
            onTextureListener.onSurfaceAvailable(surfaceTexture);
        }
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        OnTextureListener onTextureListener = this.onTextureListener;
        if (onTextureListener != null) {
            onTextureListener.onSurfaceSizeChanged(surfaceTexture, i, i2);
        }
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        OnTextureListener onTextureListener = this.onTextureListener;
        if (onTextureListener == null) {
            return false;
        }
        onTextureListener.onSurfaceDestroyed(surfaceTexture);
        return false;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        OnTextureListener onTextureListener = this.onTextureListener;
        if (onTextureListener != null) {
            onTextureListener.onSurfaceUpdated(surfaceTexture);
        }
    }

    public OnTextureListener getonTextureListener() {
        return this.onTextureListener;
    }

    public void setOnTextureListener(OnTextureListener onTextureListener) {
        setSurfaceTextureListener(this);
        this.onTextureListener = onTextureListener;
    }

    public void addTextureView(FrameLayout frameLayout, VideoTextureView videoTextureView) {
        frameLayout.removeView(videoTextureView);
        frameLayout.addView(videoTextureView, 0, new FrameLayout.LayoutParams(-1, -1, 17));
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

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        float rotation = getRotation();
        if ((Math.abs(rotation - 90.0f) > EQUAL_FLOAT && Math.abs(90.0f - rotation) > EQUAL_FLOAT) || (Math.abs(rotation - 270.0f) > EQUAL_FLOAT && Math.abs(270.0f - rotation) > EQUAL_FLOAT)) {
            VideoLogUtils.d("TextureView---------如果是横竖屏旋转切换视图，则宽高属性互换");
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