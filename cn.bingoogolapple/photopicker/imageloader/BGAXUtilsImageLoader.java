package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.imageloader.BGAImageLoader;
import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/* loaded from: classes.dex */
public class BGAXUtilsImageLoader extends BGAImageLoader {
    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void pause(Activity activity) {
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void resume(Activity activity) {
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void display(final ImageView imageView, String str, int i, int i2, int i3, int i4, final BGAImageLoader.DisplayDelegate displayDelegate) {
        x.Ext.init(BGABaseAdapterUtil.getApp());
        ImageOptions build = new ImageOptions.Builder().setLoadingDrawableId(i).setFailureDrawableId(i2).setSize(i3, i4).build();
        final String path = getPath(str);
        x.image().bind(imageView, path, build, new Callback.CommonCallback<Drawable>() { // from class: cn.bingoogolapple.photopicker.imageloader.BGAXUtilsImageLoader.1
            public void onCancelled(Callback.CancelledException cancelledException) {
            }

            public void onError(Throwable th, boolean z) {
            }

            public void onFinished() {
            }

            public void onSuccess(Drawable drawable) {
                BGAImageLoader.DisplayDelegate displayDelegate2 = displayDelegate;
                if (displayDelegate2 != null) {
                    displayDelegate2.onSuccess(imageView, path);
                }
            }
        });
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void download(String str, final BGAImageLoader.DownloadDelegate downloadDelegate) {
        x.Ext.init(BGABaseAdapterUtil.getApp());
        final String path = getPath(str);
        x.image().loadDrawable(path, new ImageOptions.Builder().build(), new Callback.CommonCallback<Drawable>() { // from class: cn.bingoogolapple.photopicker.imageloader.BGAXUtilsImageLoader.2
            public void onCancelled(Callback.CancelledException cancelledException) {
            }

            public void onFinished() {
            }

            public void onSuccess(Drawable drawable) {
                BGAImageLoader.DownloadDelegate downloadDelegate2 = downloadDelegate;
                if (downloadDelegate2 != null) {
                    downloadDelegate2.onSuccess(path, ((BitmapDrawable) drawable).getBitmap());
                }
            }

            public void onError(Throwable th, boolean z) {
                BGAImageLoader.DownloadDelegate downloadDelegate2 = downloadDelegate;
                if (downloadDelegate2 != null) {
                    downloadDelegate2.onFailed(path);
                }
            }
        });
    }
}