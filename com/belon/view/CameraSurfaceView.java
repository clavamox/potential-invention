package com.belon.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.core.view.ViewCompat;
import com.belon.decoder.R;

/* loaded from: classes.dex */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "VedioSurfaceView";
    int bgColor;
    private boolean circleAngle;
    int drawableID;
    int innerLeft;
    int innerRight;
    private boolean isCreate;
    private boolean isRotate;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private SurfaceHolder mHolder;
    private Paint mPaint;
    private Paint mTextPaint;
    private Matrix matrix;
    private int picLen;
    private int rightFrame;
    private int rotateAngle;
    private boolean showFps;
    private int totalFrame;

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    public CameraSurfaceView(Context context) {
        super(context);
        this.mCanvas = null;
        this.mHolder = null;
        this.mPaint = null;
        this.mTextPaint = null;
        this.isCreate = false;
        this.showFps = false;
        this.totalFrame = 0;
        this.rightFrame = 0;
        this.picLen = 0;
        this.isRotate = false;
        this.drawableID = 0;
        this.bgColor = ViewCompat.MEASURED_STATE_MASK;
        this.innerLeft = 0;
        this.innerRight = 0;
    }

    public CameraSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCanvas = null;
        this.mHolder = null;
        this.mPaint = null;
        this.mTextPaint = null;
        this.isCreate = false;
        this.showFps = false;
        this.totalFrame = 0;
        this.rightFrame = 0;
        this.picLen = 0;
        this.isRotate = false;
        this.drawableID = 0;
        this.bgColor = ViewCompat.MEASURED_STATE_MASK;
        this.innerLeft = 0;
        this.innerRight = 0;
        init(context, attributeSet);
        setZOrderOnTop(true);
        getHolder().setFormat(-3);
    }

    public void reDrawBitmap() {
        if (this.mBitmap == null && this.drawableID != 0) {
            this.mBitmap = BitmapFactory.decodeResource(getResources(), this.drawableID);
        }
        drawBitmap(this.mBitmap);
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        reDrawBitmap();
    }

    public void drawBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        this.mBitmap = bitmap;
        Rect rect = new Rect(0, 0, getWidth(), getHeight());
        Canvas lockCanvas = this.mHolder.lockCanvas(rect);
        this.mCanvas = lockCanvas;
        if (lockCanvas != null) {
            this.mPaint.setXfermode(null);
            this.mPaint.setColor(-1);
            this.mPaint.setTextSize(40.0f);
            this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            this.mCanvas.clipRect(rect);
            this.mTextPaint.setColor(-1);
            this.mTextPaint.setTextSize(30.0f);
            if (this.isRotate) {
                this.matrix.reset();
                this.matrix.postRotate(this.rotateAngle, getWidth() / 2, getHeight() / 2);
                this.mCanvas.setMatrix(this.matrix);
                if (this.circleAngle) {
                    this.mCanvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, this.mPaint);
                    this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                }
                this.mCanvas.drawBitmap(bitmap, (Rect) null, rect, this.mPaint);
            } else {
                this.mCanvas.drawBitmap(bitmap, (Rect) null, rect, this.mPaint);
            }
            if (this.showFps) {
                this.mCanvas.drawText(this.rightFrame + "/" + this.totalFrame + " fps " + this.picLen, 30.0f, 70.0f, this.mTextPaint);
            }
            Canvas canvas = this.mCanvas;
            if (canvas != null && this.isCreate) {
                this.mHolder.unlockCanvasAndPost(canvas);
            }
            getDrawingCache();
        }
    }

    public void setRotateAngle(int i) {
        this.rotateAngle = i;
    }

    public void drawFps() {
        Bitmap bitmap = this.mBitmap;
        if (bitmap == null) {
            return;
        }
        drawBitmap(bitmap);
    }

    public void callReDraw() {
        this.isRotate = !this.isRotate;
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.matrix = new Matrix();
        SurfaceHolder holder = getHolder();
        this.mHolder = holder;
        holder.addCallback(this);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(-1);
        this.mPaint.setARGB(1, 0, 0, 255);
        this.mPaint.setAntiAlias(true);
        this.mHolder.setFormat(-2);
        Paint paint2 = new Paint();
        this.mTextPaint = paint2;
        paint2.setARGB(1, 0, 0, 255);
        this.mTextPaint.setAntiAlias(true);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.CameraView);
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                if (obtainStyledAttributes.getIndex(i) == R.styleable.CameraView_default_bg) {
                    this.drawableID = obtainStyledAttributes.getResourceId(obtainStyledAttributes.getIndex(i), this.drawableID);
                }
                if (obtainStyledAttributes.getIndex(i) == R.styleable.CameraView_bg_color) {
                    this.bgColor = obtainStyledAttributes.getColor(obtainStyledAttributes.getIndex(i), 0);
                }
                if (obtainStyledAttributes.getIndex(i) == R.styleable.CameraView_inner_left) {
                    this.innerLeft = (int) obtainStyledAttributes.getDimension(obtainStyledAttributes.getIndex(i), 0.0f);
                }
                if (obtainStyledAttributes.getIndex(i) == R.styleable.CameraView_inner_right) {
                    this.innerRight = (int) obtainStyledAttributes.getDimension(obtainStyledAttributes.getIndex(i), 0.0f);
                }
            }
            obtainStyledAttributes.recycle();
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.isCreate = true;
        int i = this.drawableID;
        if (i != 0) {
            setDefaultDrawable(i);
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.isCreate = false;
    }

    private Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap createBitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        Log.e(TAG, "getBitmap: 1");
        return createBitmap;
    }

    public void setDefaultDrawable(int i) {
        this.drawableID = i;
        Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), i);
        Log.d(TAG, "bitmap:" + decodeResource);
        drawBitmap(decodeResource);
    }

    public void setTotalFrame(int i) {
        this.totalFrame = i;
    }

    public void setRightFrame(int i) {
        this.rightFrame = i;
    }

    public void setPicLen(int i) {
        this.picLen = i;
    }

    public void setRotate(boolean z) {
        this.isRotate = z;
    }

    public void setCircleAngle(boolean z) {
        this.circleAngle = z;
    }
}