package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.imageloader.BGAImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/* loaded from: classes.dex */
public class BGAUILImageLoader extends BGAImageLoader {
    private void initImageLoader() {
        if (ImageLoader.getInstance().isInited()) {
            return;
        }
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(BGABaseAdapterUtil.getApp()).threadPoolSize(3).defaultDisplayImageOptions(new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build()).build());
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void display(ImageView imageView, String str, int i, int i2, int i3, int i4, final BGAImageLoader.DisplayDelegate displayDelegate) {
        initImageLoader();
        ImageLoader.getInstance().displayImage(getPath(str), new ImageViewAware(imageView), new DisplayImageOptions.Builder().showImageOnLoading(i).showImageOnFail(i2).cacheInMemory(true).build(), new ImageSize(i3, i4), new SimpleImageLoadingListener() { // from class: cn.bingoogolapple.photopicker.imageloader.BGAUILImageLoader.1
            public void onLoadingComplete(String str2, View view, Bitmap bitmap) {
                BGAImageLoader.DisplayDelegate displayDelegate2 = displayDelegate;
                if (displayDelegate2 != null) {
                    displayDelegate2.onSuccess(view, str2);
                }
            }
        }, (ImageLoadingProgressListener) null);
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void download(String str, final BGAImageLoader.DownloadDelegate downloadDelegate) {
        initImageLoader();
        ImageLoader.getInstance().loadImage(getPath(str), new SimpleImageLoadingListener() { // from class: cn.bingoogolapple.photopicker.imageloader.BGAUILImageLoader.2
            public void onLoadingComplete(String str2, View view, Bitmap bitmap) {
                BGAImageLoader.DownloadDelegate downloadDelegate2 = downloadDelegate;
                if (downloadDelegate2 != null) {
                    downloadDelegate2.onSuccess(str2, bitmap);
                }
            }

            public void onLoadingFailed(String str2, View view, FailReason failReason) {
                BGAImageLoader.DownloadDelegate downloadDelegate2 = downloadDelegate;
                if (downloadDelegate2 != null) {
                    downloadDelegate2.onFailed(str2);
                }
            }
        });
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void pause(Activity activity) {
        initImageLoader();
        ImageLoader.getInstance().pause();
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void resume(Activity activity) {
        initImageLoader();
        ImageLoader.getInstance().resume();
    }
}