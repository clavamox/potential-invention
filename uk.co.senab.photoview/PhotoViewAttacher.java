package uk.co.senab.photoview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import androidx.core.view.MotionEventCompat;
import java.lang.ref.WeakReference;
import uk.co.senab.photoview.gestures.OnGestureListener;
import uk.co.senab.photoview.gestures.VersionedGestureDetector;
import uk.co.senab.photoview.log.LogManager;
import uk.co.senab.photoview.scrollerproxy.ScrollerProxy;

/* loaded from: classes.dex */
public class PhotoViewAttacher implements IPhotoView, View.OnTouchListener, OnGestureListener, ViewTreeObserver.OnGlobalLayoutListener {
    static final int EDGE_BOTH = 2;
    static final int EDGE_LEFT = 0;
    static final int EDGE_NONE = -1;
    static final int EDGE_RIGHT = 1;
    int ZOOM_DURATION;
    private boolean mAllowParentInterceptOnEdge;
    private final Matrix mBaseMatrix;
    private float mBaseRotation;
    private boolean mBlockParentIntercept;
    private FlingRunnable mCurrentFlingRunnable;
    private final RectF mDisplayRect;
    private final Matrix mDrawMatrix;
    private GestureDetector mGestureDetector;
    private WeakReference<ImageView> mImageView;
    private Interpolator mInterpolator;
    private int mIvBottom;
    private int mIvLeft;
    private int mIvRight;
    private int mIvTop;
    private View.OnLongClickListener mLongClickListener;
    private OnMatrixChangedListener mMatrixChangeListener;
    private final float[] mMatrixValues;
    private float mMaxScale;
    private float mMidScale;
    private float mMinScale;
    private OnPhotoTapListener mPhotoTapListener;
    private OnScaleChangeListener mScaleChangeListener;
    private uk.co.senab.photoview.gestures.GestureDetector mScaleDragDetector;
    private ImageView.ScaleType mScaleType;
    private int mScrollEdge;
    private OnSingleFlingListener mSingleFlingListener;
    private final Matrix mSuppMatrix;
    private OnViewTapListener mViewTapListener;
    private boolean mZoomEnabled;
    private static final String LOG_TAG = "PhotoViewAttacher";
    private static final boolean DEBUG = Log.isLoggable(LOG_TAG, 3);
    static int SINGLE_TOUCH = 1;

    public interface OnMatrixChangedListener {
        void onMatrixChanged(RectF rectF);
    }

    public interface OnPhotoTapListener {
        void onOutsidePhotoTap();

        void onPhotoTap(View view, float f, float f2);
    }

    public interface OnScaleChangeListener {
        void onScaleChange(float f, float f2, float f3);
    }

    public interface OnSingleFlingListener {
        boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);
    }

    public interface OnViewTapListener {
        void onViewTap(View view, float f, float f2);
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public IPhotoView getIPhotoViewImplementation() {
        return this;
    }

    private static void checkZoomLevels(float f, float f2, float f3) {
        if (f >= f2) {
            throw new IllegalArgumentException("Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value");
        }
        if (f2 >= f3) {
            throw new IllegalArgumentException("Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value");
        }
    }

    private static boolean hasDrawable(ImageView imageView) {
        return (imageView == null || imageView.getDrawable() == null) ? false : true;
    }

    /* renamed from: uk.co.senab.photoview.PhotoViewAttacher$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType;

        static {
            int[] iArr = new int[ImageView.ScaleType.values().length];
            $SwitchMap$android$widget$ImageView$ScaleType = iArr;
            try {
                iArr[ImageView.ScaleType.MATRIX.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_START.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_END.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_CENTER.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_XY.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    private static boolean isSupportedScaleType(ImageView.ScaleType scaleType) {
        if (scaleType == null) {
            return false;
        }
        if (AnonymousClass2.$SwitchMap$android$widget$ImageView$ScaleType[scaleType.ordinal()] != 1) {
            return true;
        }
        throw new IllegalArgumentException(scaleType.name() + " is not supported in PhotoView");
    }

    private static void setImageViewScaleTypeMatrix(ImageView imageView) {
        if (imageView == null || (imageView instanceof IPhotoView) || ImageView.ScaleType.MATRIX.equals(imageView.getScaleType())) {
            return;
        }
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
    }

    public PhotoViewAttacher(ImageView imageView) {
        this(imageView, true);
    }

    public PhotoViewAttacher(ImageView imageView, boolean z) {
        this.mInterpolator = new AccelerateDecelerateInterpolator();
        this.ZOOM_DURATION = 200;
        this.mMinScale = 1.0f;
        this.mMidScale = 1.75f;
        this.mMaxScale = 3.0f;
        this.mAllowParentInterceptOnEdge = true;
        this.mBlockParentIntercept = false;
        this.mBaseMatrix = new Matrix();
        this.mDrawMatrix = new Matrix();
        this.mSuppMatrix = new Matrix();
        this.mDisplayRect = new RectF();
        this.mMatrixValues = new float[9];
        this.mScrollEdge = 2;
        this.mScaleType = ImageView.ScaleType.FIT_CENTER;
        this.mImageView = new WeakReference<>(imageView);
        imageView.setDrawingCacheEnabled(true);
        imageView.setOnTouchListener(this);
        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        if (viewTreeObserver != null) {
            viewTreeObserver.addOnGlobalLayoutListener(this);
        }
        setImageViewScaleTypeMatrix(imageView);
        if (imageView.isInEditMode()) {
            return;
        }
        this.mScaleDragDetector = VersionedGestureDetector.newInstance(imageView.getContext(), this);
        GestureDetector gestureDetector = new GestureDetector(imageView.getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: uk.co.senab.photoview.PhotoViewAttacher.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent motionEvent) {
                if (PhotoViewAttacher.this.mLongClickListener != null) {
                    PhotoViewAttacher.this.mLongClickListener.onLongClick(PhotoViewAttacher.this.getImageView());
                }
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (PhotoViewAttacher.this.mSingleFlingListener == null || PhotoViewAttacher.this.getScale() > 1.0f || MotionEventCompat.getPointerCount(motionEvent) > PhotoViewAttacher.SINGLE_TOUCH || MotionEventCompat.getPointerCount(motionEvent2) > PhotoViewAttacher.SINGLE_TOUCH) {
                    return false;
                }
                return PhotoViewAttacher.this.mSingleFlingListener.onFling(motionEvent, motionEvent2, f, f2);
            }
        });
        this.mGestureDetector = gestureDetector;
        gestureDetector.setOnDoubleTapListener(new DefaultOnDoubleTapListener(this));
        this.mBaseRotation = 0.0f;
        setZoomable(z);
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        if (onDoubleTapListener != null) {
            this.mGestureDetector.setOnDoubleTapListener(onDoubleTapListener);
        } else {
            this.mGestureDetector.setOnDoubleTapListener(new DefaultOnDoubleTapListener(this));
        }
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setOnScaleChangeListener(OnScaleChangeListener onScaleChangeListener) {
        this.mScaleChangeListener = onScaleChangeListener;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.mSingleFlingListener = onSingleFlingListener;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public boolean canZoom() {
        return this.mZoomEnabled;
    }

    public void cleanup() {
        WeakReference<ImageView> weakReference = this.mImageView;
        if (weakReference == null) {
            return;
        }
        ImageView imageView = weakReference.get();
        if (imageView != null) {
            ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
            if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
                viewTreeObserver.removeGlobalOnLayoutListener(this);
            }
            imageView.setOnTouchListener(null);
            cancelFling();
        }
        GestureDetector gestureDetector = this.mGestureDetector;
        if (gestureDetector != null) {
            gestureDetector.setOnDoubleTapListener(null);
        }
        this.mMatrixChangeListener = null;
        this.mPhotoTapListener = null;
        this.mViewTapListener = null;
        this.mImageView = null;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public RectF getDisplayRect() {
        checkMatrixBounds();
        return getDisplayRect(getDrawMatrix());
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public boolean setDisplayMatrix(Matrix matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }
        ImageView imageView = getImageView();
        if (imageView == null || imageView.getDrawable() == null) {
            return false;
        }
        this.mSuppMatrix.set(matrix);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
        return true;
    }

    public void setBaseRotation(float f) {
        this.mBaseRotation = f % 360.0f;
        update();
        setRotationBy(this.mBaseRotation);
        checkAndDisplayMatrix();
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setPhotoViewRotation(float f) {
        this.mSuppMatrix.setRotate(f % 360.0f);
        checkAndDisplayMatrix();
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setRotationTo(float f) {
        this.mSuppMatrix.setRotate(f % 360.0f);
        checkAndDisplayMatrix();
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setRotationBy(float f) {
        this.mSuppMatrix.postRotate(f % 360.0f);
        checkAndDisplayMatrix();
    }

    public ImageView getImageView() {
        WeakReference<ImageView> weakReference = this.mImageView;
        ImageView imageView = weakReference != null ? weakReference.get() : null;
        if (imageView == null) {
            cleanup();
            LogManager.getLogger().i(LOG_TAG, "ImageView no longer exists. You should not use this PhotoViewAttacher any more.");
        }
        return imageView;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    @Deprecated
    public float getMinScale() {
        return getMinimumScale();
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public float getMinimumScale() {
        return this.mMinScale;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    @Deprecated
    public float getMidScale() {
        return getMediumScale();
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public float getMediumScale() {
        return this.mMidScale;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    @Deprecated
    public float getMaxScale() {
        return getMaximumScale();
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public float getMaximumScale() {
        return this.mMaxScale;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public float getScale() {
        return (float) Math.sqrt(((float) Math.pow(getValue(this.mSuppMatrix, 0), 2.0d)) + ((float) Math.pow(getValue(this.mSuppMatrix, 3), 2.0d)));
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public ImageView.ScaleType getScaleType() {
        return this.mScaleType;
    }

    @Override // uk.co.senab.photoview.gestures.OnGestureListener
    public void onDrag(float f, float f2) {
        if (this.mScaleDragDetector.isScaling()) {
            return;
        }
        if (DEBUG) {
            LogManager.getLogger().d(LOG_TAG, String.format("onDrag: dx: %.2f. dy: %.2f", Float.valueOf(f), Float.valueOf(f2)));
        }
        ImageView imageView = getImageView();
        this.mSuppMatrix.postTranslate(f, f2);
        checkAndDisplayMatrix();
        ViewParent parent = imageView.getParent();
        if (!this.mAllowParentInterceptOnEdge || this.mScaleDragDetector.isScaling() || this.mBlockParentIntercept) {
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
                return;
            }
            return;
        }
        int i = this.mScrollEdge;
        if ((i == 2 || ((i == 0 && f >= 1.0f) || (i == 1 && f <= -1.0f))) && parent != null) {
            parent.requestDisallowInterceptTouchEvent(false);
        }
    }

    @Override // uk.co.senab.photoview.gestures.OnGestureListener
    public void onFling(float f, float f2, float f3, float f4) {
        if (DEBUG) {
            LogManager.getLogger().d(LOG_TAG, "onFling. sX: " + f + " sY: " + f2 + " Vx: " + f3 + " Vy: " + f4);
        }
        ImageView imageView = getImageView();
        FlingRunnable flingRunnable = new FlingRunnable(imageView.getContext());
        this.mCurrentFlingRunnable = flingRunnable;
        flingRunnable.fling(getImageViewWidth(imageView), getImageViewHeight(imageView), (int) f3, (int) f4);
        imageView.post(this.mCurrentFlingRunnable);
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        ImageView imageView = getImageView();
        if (imageView != null) {
            if (this.mZoomEnabled) {
                int top = imageView.getTop();
                int right = imageView.getRight();
                int bottom = imageView.getBottom();
                int left = imageView.getLeft();
                if (top == this.mIvTop && bottom == this.mIvBottom && left == this.mIvLeft && right == this.mIvRight) {
                    return;
                }
                updateBaseMatrix(imageView.getDrawable());
                this.mIvTop = top;
                this.mIvRight = right;
                this.mIvBottom = bottom;
                this.mIvLeft = left;
                return;
            }
            updateBaseMatrix(imageView.getDrawable());
        }
    }

    @Override // uk.co.senab.photoview.gestures.OnGestureListener
    public void onScale(float f, float f2, float f3) {
        if (DEBUG) {
            LogManager.getLogger().d(LOG_TAG, String.format("onScale: scale: %.2f. fX: %.2f. fY: %.2f", Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3)));
        }
        if (getScale() < this.mMaxScale || f < 1.0f) {
            if (getScale() > this.mMinScale || f > 1.0f) {
                OnScaleChangeListener onScaleChangeListener = this.mScaleChangeListener;
                if (onScaleChangeListener != null) {
                    onScaleChangeListener.onScaleChange(f, f2, f3);
                }
                this.mSuppMatrix.postScale(f, f, f2, f3);
                checkAndDisplayMatrix();
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0062  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0095  */
    @Override // android.view.View.OnTouchListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouch(android.view.View r11, android.view.MotionEvent r12) {
        /*
            r10 = this;
            boolean r0 = r10.mZoomEnabled
            r1 = 0
            if (r0 == 0) goto La1
            r0 = r11
            android.widget.ImageView r0 = (android.widget.ImageView) r0
            boolean r0 = hasDrawable(r0)
            if (r0 == 0) goto La1
            android.view.ViewParent r0 = r11.getParent()
            int r2 = r12.getAction()
            r3 = 1
            if (r2 == 0) goto L49
            if (r2 == r3) goto L1f
            r0 = 3
            if (r2 == r0) goto L1f
            goto L5d
        L1f:
            float r0 = r10.getScale()
            float r2 = r10.mMinScale
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L5d
            android.graphics.RectF r0 = r10.getDisplayRect()
            if (r0 == 0) goto L5d
            uk.co.senab.photoview.PhotoViewAttacher$AnimatedZoomRunnable r2 = new uk.co.senab.photoview.PhotoViewAttacher$AnimatedZoomRunnable
            float r6 = r10.getScale()
            float r7 = r10.mMinScale
            float r8 = r0.centerX()
            float r9 = r0.centerY()
            r4 = r2
            r5 = r10
            r4.<init>(r6, r7, r8, r9)
            r11.post(r2)
            r11 = r3
            goto L5e
        L49:
            if (r0 == 0) goto L4f
            r0.requestDisallowInterceptTouchEvent(r3)
            goto L5a
        L4f:
            uk.co.senab.photoview.log.Logger r11 = uk.co.senab.photoview.log.LogManager.getLogger()
            java.lang.String r0 = "PhotoViewAttacher"
            java.lang.String r2 = "onTouch getParent() returned null"
            r11.i(r0, r2)
        L5a:
            r10.cancelFling()
        L5d:
            r11 = r1
        L5e:
            uk.co.senab.photoview.gestures.GestureDetector r0 = r10.mScaleDragDetector
            if (r0 == 0) goto L95
            boolean r11 = r0.isScaling()
            uk.co.senab.photoview.gestures.GestureDetector r0 = r10.mScaleDragDetector
            boolean r0 = r0.isDragging()
            uk.co.senab.photoview.gestures.GestureDetector r2 = r10.mScaleDragDetector
            boolean r2 = r2.onTouchEvent(r12)
            if (r11 != 0) goto L7e
            uk.co.senab.photoview.gestures.GestureDetector r11 = r10.mScaleDragDetector
            boolean r11 = r11.isScaling()
            if (r11 != 0) goto L7e
            r11 = r3
            goto L7f
        L7e:
            r11 = r1
        L7f:
            if (r0 != 0) goto L8b
            uk.co.senab.photoview.gestures.GestureDetector r0 = r10.mScaleDragDetector
            boolean r0 = r0.isDragging()
            if (r0 != 0) goto L8b
            r0 = r3
            goto L8c
        L8b:
            r0 = r1
        L8c:
            if (r11 == 0) goto L91
            if (r0 == 0) goto L91
            r1 = r3
        L91:
            r10.mBlockParentIntercept = r1
            r1 = r2
            goto L96
        L95:
            r1 = r11
        L96:
            android.view.GestureDetector r11 = r10.mGestureDetector
            if (r11 == 0) goto La1
            boolean r11 = r11.onTouchEvent(r12)
            if (r11 == 0) goto La1
            r1 = r3
        La1:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: uk.co.senab.photoview.PhotoViewAttacher.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setAllowParentInterceptOnEdge(boolean z) {
        this.mAllowParentInterceptOnEdge = z;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    @Deprecated
    public void setMinScale(float f) {
        setMinimumScale(f);
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setMinimumScale(float f) {
        checkZoomLevels(f, this.mMidScale, this.mMaxScale);
        this.mMinScale = f;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    @Deprecated
    public void setMidScale(float f) {
        setMediumScale(f);
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setMediumScale(float f) {
        checkZoomLevels(this.mMinScale, f, this.mMaxScale);
        this.mMidScale = f;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    @Deprecated
    public void setMaxScale(float f) {
        setMaximumScale(f);
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setMaximumScale(float f) {
        checkZoomLevels(this.mMinScale, this.mMidScale, f);
        this.mMaxScale = f;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setScaleLevels(float f, float f2, float f3) {
        checkZoomLevels(f, f2, f3);
        this.mMinScale = f;
        this.mMidScale = f2;
        this.mMaxScale = f3;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mLongClickListener = onLongClickListener;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setOnMatrixChangeListener(OnMatrixChangedListener onMatrixChangedListener) {
        this.mMatrixChangeListener = onMatrixChangedListener;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setOnPhotoTapListener(OnPhotoTapListener onPhotoTapListener) {
        this.mPhotoTapListener = onPhotoTapListener;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    @Deprecated
    public OnPhotoTapListener getOnPhotoTapListener() {
        return this.mPhotoTapListener;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setOnViewTapListener(OnViewTapListener onViewTapListener) {
        this.mViewTapListener = onViewTapListener;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    @Deprecated
    public OnViewTapListener getOnViewTapListener() {
        return this.mViewTapListener;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setScale(float f) {
        setScale(f, false);
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setScale(float f, boolean z) {
        if (getImageView() != null) {
            setScale(f, r0.getRight() / 2, r0.getBottom() / 2, z);
        }
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setScale(float f, float f2, float f3, boolean z) {
        ImageView imageView = getImageView();
        if (imageView != null) {
            if (f < this.mMinScale || f > this.mMaxScale) {
                LogManager.getLogger().i(LOG_TAG, "Scale must be within the range of minScale and maxScale");
            } else if (z) {
                imageView.post(new AnimatedZoomRunnable(getScale(), f, f2, f3));
            } else {
                this.mSuppMatrix.setScale(f, f, f2, f3);
                checkAndDisplayMatrix();
            }
        }
    }

    public void setZoomInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setScaleType(ImageView.ScaleType scaleType) {
        if (!isSupportedScaleType(scaleType) || scaleType == this.mScaleType) {
            return;
        }
        this.mScaleType = scaleType;
        update();
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setZoomable(boolean z) {
        this.mZoomEnabled = z;
        update();
    }

    public void update() {
        ImageView imageView = getImageView();
        if (imageView != null) {
            if (this.mZoomEnabled) {
                setImageViewScaleTypeMatrix(imageView);
                updateBaseMatrix(imageView.getDrawable());
            } else {
                resetMatrix();
            }
        }
    }

    @Override // uk.co.senab.photoview.IPhotoView
    @Deprecated
    public Matrix getDisplayMatrix() {
        return new Matrix(getDrawMatrix());
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void getDisplayMatrix(Matrix matrix) {
        matrix.set(getDrawMatrix());
    }

    public void getSuppMatrix(Matrix matrix) {
        matrix.set(this.mSuppMatrix);
    }

    @Deprecated
    public Matrix getDrawMatrix() {
        this.mDrawMatrix.set(this.mBaseMatrix);
        this.mDrawMatrix.postConcat(this.mSuppMatrix);
        return this.mDrawMatrix;
    }

    private void cancelFling() {
        FlingRunnable flingRunnable = this.mCurrentFlingRunnable;
        if (flingRunnable != null) {
            flingRunnable.cancelFling();
            this.mCurrentFlingRunnable = null;
        }
    }

    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix());
        }
    }

    private void checkImageViewScaleType() {
        ImageView imageView = getImageView();
        if (imageView != null && !(imageView instanceof IPhotoView) && !ImageView.ScaleType.MATRIX.equals(imageView.getScaleType())) {
            throw new IllegalStateException("The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher. You should call setScaleType on the PhotoViewAttacher instead of on the ImageView");
        }
    }

    private boolean checkMatrixBounds() {
        RectF displayRect;
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        ImageView imageView = getImageView();
        if (imageView == null || (displayRect = getDisplayRect(getDrawMatrix())) == null) {
            return false;
        }
        float height = displayRect.height();
        float width = displayRect.width();
        float imageViewHeight = getImageViewHeight(imageView);
        float f7 = 0.0f;
        if (height <= imageViewHeight) {
            int i = AnonymousClass2.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()];
            if (i != 2) {
                if (i == 3) {
                    imageViewHeight -= height;
                    f2 = displayRect.top;
                } else {
                    imageViewHeight = (imageViewHeight - height) / 2.0f;
                    f2 = displayRect.top;
                }
                f = imageViewHeight - f2;
            } else {
                f3 = displayRect.top;
                f = -f3;
            }
        } else if (displayRect.top > 0.0f) {
            f3 = displayRect.top;
            f = -f3;
        } else if (displayRect.bottom < imageViewHeight) {
            f2 = displayRect.bottom;
            f = imageViewHeight - f2;
        } else {
            f = 0.0f;
        }
        float imageViewWidth = getImageViewWidth(imageView);
        if (width <= imageViewWidth) {
            int i2 = AnonymousClass2.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()];
            if (i2 != 2) {
                if (i2 == 3) {
                    f5 = imageViewWidth - width;
                    f6 = displayRect.left;
                } else {
                    f5 = (imageViewWidth - width) / 2.0f;
                    f6 = displayRect.left;
                }
                f4 = f5 - f6;
            } else {
                f4 = -displayRect.left;
            }
            f7 = f4;
            this.mScrollEdge = 2;
        } else if (displayRect.left > 0.0f) {
            this.mScrollEdge = 0;
            f7 = -displayRect.left;
        } else if (displayRect.right < imageViewWidth) {
            f7 = imageViewWidth - displayRect.right;
            this.mScrollEdge = 1;
        } else {
            this.mScrollEdge = -1;
        }
        this.mSuppMatrix.postTranslate(f7, f);
        return true;
    }

    private RectF getDisplayRect(Matrix matrix) {
        Drawable drawable;
        ImageView imageView = getImageView();
        if (imageView == null || (drawable = imageView.getDrawable()) == null) {
            return null;
        }
        this.mDisplayRect.set(0.0f, 0.0f, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        matrix.mapRect(this.mDisplayRect);
        return this.mDisplayRect;
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public Bitmap getVisibleRectangleBitmap() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return null;
        }
        return imageView.getDrawingCache();
    }

    @Override // uk.co.senab.photoview.IPhotoView
    public void setZoomTransitionDuration(int i) {
        if (i < 0) {
            i = 200;
        }
        this.ZOOM_DURATION = i;
    }

    private float getValue(Matrix matrix, int i) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[i];
    }

    private void resetMatrix() {
        this.mSuppMatrix.reset();
        setRotationBy(this.mBaseRotation);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setImageViewMatrix(Matrix matrix) {
        RectF displayRect;
        ImageView imageView = getImageView();
        if (imageView != null) {
            checkImageViewScaleType();
            imageView.setImageMatrix(matrix);
            if (this.mMatrixChangeListener == null || (displayRect = getDisplayRect(matrix)) == null) {
                return;
            }
            this.mMatrixChangeListener.onMatrixChanged(displayRect);
        }
    }

    protected void updateBaseMatrix(Drawable drawable) {
        ImageView imageView = getImageView();
        if (imageView == null || drawable == null) {
            return;
        }
        float imageViewWidth = getImageViewWidth(imageView);
        float imageViewHeight = getImageViewHeight(imageView);
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        this.mBaseMatrix.reset();
        float f = intrinsicWidth;
        float f2 = imageViewWidth / f;
        float f3 = intrinsicHeight;
        float f4 = imageViewHeight / f3;
        if (this.mScaleType == ImageView.ScaleType.CENTER) {
            this.mBaseMatrix.postTranslate((imageViewWidth - f) / 2.0f, (imageViewHeight - f3) / 2.0f);
        } else if (this.mScaleType == ImageView.ScaleType.CENTER_CROP) {
            float max = Math.max(f2, f4);
            this.mBaseMatrix.postScale(max, max);
            this.mBaseMatrix.postTranslate((imageViewWidth - (f * max)) / 2.0f, (imageViewHeight - (f3 * max)) / 2.0f);
        } else if (this.mScaleType == ImageView.ScaleType.CENTER_INSIDE) {
            float min = Math.min(1.0f, Math.min(f2, f4));
            this.mBaseMatrix.postScale(min, min);
            this.mBaseMatrix.postTranslate((imageViewWidth - (f * min)) / 2.0f, (imageViewHeight - (f3 * min)) / 2.0f);
        } else {
            RectF rectF = new RectF(0.0f, 0.0f, f, f3);
            RectF rectF2 = new RectF(0.0f, 0.0f, imageViewWidth, imageViewHeight);
            if (((int) this.mBaseRotation) % 180 != 0) {
                rectF = new RectF(0.0f, 0.0f, f3, f);
            }
            int i = AnonymousClass2.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()];
            if (i == 2) {
                this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.START);
            } else if (i == 3) {
                this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.END);
            } else if (i == 4) {
                this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.CENTER);
            } else if (i == 5) {
                this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.FILL);
            }
        }
        resetMatrix();
    }

    protected void updateBaseMatrix(Matrix matrix) {
        this.mBaseMatrix.reset();
        this.mBaseMatrix.set(matrix);
        resetMatrix();
    }

    protected int getImageViewWidth(ImageView imageView) {
        if (imageView == null) {
            return 0;
        }
        return (imageView.getWidth() - imageView.getPaddingLeft()) - imageView.getPaddingRight();
    }

    protected int getImageViewHeight(ImageView imageView) {
        if (imageView == null) {
            return 0;
        }
        return (imageView.getHeight() - imageView.getPaddingTop()) - imageView.getPaddingBottom();
    }

    private class AnimatedZoomRunnable implements Runnable {
        private final float mFocalX;
        private final float mFocalY;
        private final long mStartTime = System.currentTimeMillis();
        private final float mZoomEnd;
        private final float mZoomStart;

        public AnimatedZoomRunnable(float f, float f2, float f3, float f4) {
            this.mFocalX = f3;
            this.mFocalY = f4;
            this.mZoomStart = f;
            this.mZoomEnd = f2;
        }

        @Override // java.lang.Runnable
        public void run() {
            ImageView imageView = PhotoViewAttacher.this.getImageView();
            if (imageView == null) {
                return;
            }
            float interpolate = interpolate();
            float f = this.mZoomStart;
            PhotoViewAttacher.this.onScale((f + ((this.mZoomEnd - f) * interpolate)) / PhotoViewAttacher.this.getScale(), this.mFocalX, this.mFocalY);
            if (interpolate < 1.0f) {
                Compat.postOnAnimation(imageView, this);
            }
        }

        private float interpolate() {
            return PhotoViewAttacher.this.mInterpolator.getInterpolation(Math.min(1.0f, ((System.currentTimeMillis() - this.mStartTime) * 1.0f) / PhotoViewAttacher.this.ZOOM_DURATION));
        }
    }

    private class FlingRunnable implements Runnable {
        private int mCurrentX;
        private int mCurrentY;
        private final ScrollerProxy mScroller;

        public FlingRunnable(Context context) {
            this.mScroller = ScrollerProxy.getScroller(context);
        }

        public void cancelFling() {
            if (PhotoViewAttacher.DEBUG) {
                LogManager.getLogger().d(PhotoViewAttacher.LOG_TAG, "Cancel Fling");
            }
            this.mScroller.forceFinished(true);
        }

        public void fling(int i, int i2, int i3, int i4) {
            int i5;
            int i6;
            int i7;
            int i8;
            RectF displayRect = PhotoViewAttacher.this.getDisplayRect();
            if (displayRect == null) {
                return;
            }
            int round = Math.round(-displayRect.left);
            float f = i;
            if (f < displayRect.width()) {
                i6 = Math.round(displayRect.width() - f);
                i5 = 0;
            } else {
                i5 = round;
                i6 = i5;
            }
            int round2 = Math.round(-displayRect.top);
            float f2 = i2;
            if (f2 < displayRect.height()) {
                i8 = Math.round(displayRect.height() - f2);
                i7 = 0;
            } else {
                i7 = round2;
                i8 = i7;
            }
            this.mCurrentX = round;
            this.mCurrentY = round2;
            if (PhotoViewAttacher.DEBUG) {
                LogManager.getLogger().d(PhotoViewAttacher.LOG_TAG, "fling. StartX:" + round + " StartY:" + round2 + " MaxX:" + i6 + " MaxY:" + i8);
            }
            if (round == i6 && round2 == i8) {
                return;
            }
            this.mScroller.fling(round, round2, i3, i4, i5, i6, i7, i8, 0, 0);
        }

        @Override // java.lang.Runnable
        public void run() {
            ImageView imageView;
            if (this.mScroller.isFinished() || (imageView = PhotoViewAttacher.this.getImageView()) == null || !this.mScroller.computeScrollOffset()) {
                return;
            }
            int currX = this.mScroller.getCurrX();
            int currY = this.mScroller.getCurrY();
            if (PhotoViewAttacher.DEBUG) {
                LogManager.getLogger().d(PhotoViewAttacher.LOG_TAG, "fling run(). CurrentX:" + this.mCurrentX + " CurrentY:" + this.mCurrentY + " NewX:" + currX + " NewY:" + currY);
            }
            PhotoViewAttacher.this.mSuppMatrix.postTranslate(this.mCurrentX - currX, this.mCurrentY - currY);
            PhotoViewAttacher photoViewAttacher = PhotoViewAttacher.this;
            photoViewAttacher.setImageViewMatrix(photoViewAttacher.getDrawMatrix());
            this.mCurrentX = currX;
            this.mCurrentY = currY;
            Compat.postOnAnimation(imageView, this);
        }
    }
}