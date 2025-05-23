package cn.bingoogolapple.photopicker.util;

import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/* loaded from: classes.dex */
public class BGABrowserPhotoViewAttacher extends PhotoViewAttacher {
    private boolean isSetTopCrop;

    public BGABrowserPhotoViewAttacher(ImageView imageView) {
        super(imageView);
        this.isSetTopCrop = false;
    }

    @Override // uk.co.senab.photoview.PhotoViewAttacher
    protected void updateBaseMatrix(Drawable drawable) {
        if (this.isSetTopCrop) {
            setTopCrop(drawable);
        } else {
            super.updateBaseMatrix(drawable);
        }
    }

    public void setIsSetTopCrop(boolean z) {
        this.isSetTopCrop = z;
    }

    public void setUpdateBaseMatrix() {
        ImageView imageView = getImageView();
        if (imageView == null) {
            return;
        }
        updateBaseMatrix(imageView.getDrawable());
    }

    private void setTopCrop(Drawable drawable) {
        ImageView imageView = getImageView();
        if (imageView == null || drawable == null) {
            return;
        }
        float imageViewWidth = getImageViewWidth(imageView);
        float imageViewHeight = getImageViewHeight(imageView);
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Matrix matrix = new Matrix();
        float max = Math.max(imageViewWidth / intrinsicWidth, imageViewHeight / intrinsicHeight);
        matrix.postScale(max, max);
        matrix.postTranslate(0.0f, 0.0f);
        updateBaseMatrix(matrix);
    }
}