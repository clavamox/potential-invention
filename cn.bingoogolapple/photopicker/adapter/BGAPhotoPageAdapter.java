package cn.bingoogolapple.photopicker.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGABrowserPhotoViewAttacher;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGAImageView;
import java.util.ArrayList;
import uk.co.senab.photoview.PhotoViewAttacher;

/* loaded from: classes.dex */
public class BGAPhotoPageAdapter extends PagerAdapter {
    private PhotoViewAttacher.OnViewTapListener mOnViewTapListener;
    private ArrayList<String> mPreviewPhotos;

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public BGAPhotoPageAdapter(PhotoViewAttacher.OnViewTapListener onViewTapListener, ArrayList<String> arrayList) {
        this.mOnViewTapListener = onViewTapListener;
        this.mPreviewPhotos = arrayList;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        ArrayList<String> arrayList = this.mPreviewPhotos;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public View instantiateItem(ViewGroup viewGroup, int i) {
        BGAImageView bGAImageView = new BGAImageView(viewGroup.getContext());
        viewGroup.addView(bGAImageView, -1, -1);
        final BGABrowserPhotoViewAttacher bGABrowserPhotoViewAttacher = new BGABrowserPhotoViewAttacher(bGAImageView);
        bGABrowserPhotoViewAttacher.setOnViewTapListener(this.mOnViewTapListener);
        bGAImageView.setDelegate(new BGAImageView.Delegate() { // from class: cn.bingoogolapple.photopicker.adapter.BGAPhotoPageAdapter.1
            @Override // cn.bingoogolapple.photopicker.widget.BGAImageView.Delegate
            public void onDrawableChanged(Drawable drawable) {
                if (drawable != null && drawable.getIntrinsicHeight() > drawable.getIntrinsicWidth() && drawable.getIntrinsicHeight() > BGAPhotoPickerUtil.getScreenHeight()) {
                    bGABrowserPhotoViewAttacher.setIsSetTopCrop(true);
                    bGABrowserPhotoViewAttacher.setUpdateBaseMatrix();
                } else {
                    bGABrowserPhotoViewAttacher.update();
                }
            }
        });
        BGAImage.display(bGAImageView, R.mipmap.bga_pp_ic_holder_dark, this.mPreviewPhotos.get(i), BGAPhotoPickerUtil.getScreenWidth(), BGAPhotoPickerUtil.getScreenHeight());
        return bGAImageView;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView((View) obj);
    }

    public String getItem(int i) {
        ArrayList<String> arrayList = this.mPreviewPhotos;
        return arrayList == null ? "" : arrayList.get(i);
    }
}