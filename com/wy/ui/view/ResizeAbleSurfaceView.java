package com.wy.ui.view;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;

/* loaded from: classes.dex */
public class ResizeAbleSurfaceView extends SurfaceView {
    private int mHeight;
    private int mWidth;

    public ResizeAbleSurfaceView(Context context) {
        super(context);
        this.mWidth = -1;
        this.mHeight = -1;
    }

    public ResizeAbleSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mWidth = -1;
        this.mHeight = -1;
    }

    public ResizeAbleSurfaceView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mWidth = -1;
        this.mHeight = -1;
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4 = this.mWidth;
        if (-1 == i4 || -1 == (i3 = this.mHeight)) {
            super.onMeasure(i, i2);
        } else {
            setMeasuredDimension(i4, i3);
        }
        Log.d("ResizeAbleSurfaceView", "zwn  onMeasure resize:" + getWidth() + ",height:" + getHeight());
    }

    public Matrix calculateSurfaceHolderTransform(int i, int i2) {
        float f;
        int height = getHeight();
        float f2 = i / i2;
        float width = getWidth();
        float f3 = height;
        float f4 = width / f3;
        float f5 = 1.0f;
        if (f4 < f2) {
            float f6 = f2 / f4;
            f = 1.0f;
            f5 = f6;
        } else {
            f = f4 / f2;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(f5, f);
        matrix.postTranslate((width - (width * f5)) / 2.0f, (f3 - (f3 * f)) / 2.0f);
        return matrix;
    }

    public void resize(int i, int i2) {
        Log.d("ResizeAbleSurfaceView", "zwn 11 mWidth:" + this.mWidth + "<>" + this.mHeight);
        this.mHeight = i;
        this.mWidth = i2;
        Log.d("ResizeAbleSurfaceView", "zwn 22 mWidth:" + this.mWidth + "<>" + this.mHeight);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = this.mWidth;
        layoutParams.height = this.mHeight;
        setLayoutParams(layoutParams);
        requestLayout();
        invalidate();
        Log.d("ResizeAbleSurfaceView", "zwn resize:" + i + ",height:" + i2);
        Log.d("ResizeAbleSurfaceView", "zwn resize:" + getWidth() + ",height:" + getHeight());
    }
}