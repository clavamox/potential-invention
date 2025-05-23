package com.belon.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

/* loaded from: classes.dex */
public class CameraImageView extends AppCompatImageView {
    private static final String TAG = "CameraImageView";
    private Bitmap bitmap;
    private boolean circleAngle;
    private int degree;
    private boolean rotate;

    public CameraImageView(Context context) {
        super(context);
        this.rotate = true;
    }

    public CameraImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.rotate = true;
    }

    public CameraImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.rotate = true;
    }

    public void setRotate(boolean z) {
        this.rotate = z;
    }

    public void reDrawBitmap() {
        Bitmap bitmap = this.bitmap;
        if (bitmap != null) {
            drawBitmap(bitmap);
        }
    }

    public void drawBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (this.circleAngle) {
            setImageDrawable(new BitmapDrawable(toCircleBitmap(bitmap)));
        } else {
            setImageDrawable(new BitmapDrawable(bitmap));
        }
        if (this.rotate) {
            setPivotX(getWidth() / 2);
            setPivotY(getHeight() / 2);
            setRotation(this.degree);
        } else if (this.degree != 0) {
            setPivotX(getWidth() / 2);
            setPivotY(getHeight() / 2);
            setRotation(0.0f);
        }
    }

    public void setCircleAngle(boolean z) {
        this.circleAngle = z;
    }

    public void setRotateAngle(int i) {
        this.degree = i;
    }

    public Bitmap toCircleBitmap(Bitmap bitmap) {
        float f;
        float f2;
        float f3;
        float f4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width <= height) {
            f4 = width / 2;
            f3 = width;
            f = 0.0f;
            f2 = f3;
        } else {
            f = (width - height) / 2;
            f2 = height;
            f3 = width - f;
            width = height;
            f4 = height / 2;
        }
        Bitmap createBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect((int) f, (int) 0.0f, (int) f3, (int) f2);
        Rect rect2 = new Rect((int) 0.0f, (int) 0.0f, (int) f2, (int) f2);
        new RectF(rect2);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawCircle(f4, f4, f4, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect2, paint);
        return createBitmap;
    }

    public void setDefaultDrawable(int i) {
        setBackgroundDrawable(getResources().getDrawable(i));
    }
}