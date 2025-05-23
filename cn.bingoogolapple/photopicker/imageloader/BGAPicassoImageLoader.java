package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.imageloader.BGAImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/* loaded from: classes.dex */
public class BGAPicassoImageLoader extends BGAImageLoader {
    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void display(final ImageView imageView, String str, int i, int i2, int i3, int i4, final BGAImageLoader.DisplayDelegate displayDelegate) {
        final String path = getPath(str);
        Activity activity = getActivity(imageView);
        Picasso.with(activity).load(path).tag(activity).placeholder(i).error(i2).resize(i3, i4).centerInside().into(imageView, new Callback.EmptyCallback() { // from class: cn.bingoogolapple.photopicker.imageloader.BGAPicassoImageLoader.1
            public void onSuccess() {
                BGAImageLoader.DisplayDelegate displayDelegate2 = displayDelegate;
                if (displayDelegate2 != null) {
                    displayDelegate2.onSuccess(imageView, path);
                }
            }
        });
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void download(String str, final BGAImageLoader.DownloadDelegate downloadDelegate) {
        final String path = getPath(str);
        Picasso.with(BGABaseAdapterUtil.getApp()).load(path).into(new Target() { // from class: cn.bingoogolapple.photopicker.imageloader.BGAPicassoImageLoader.2
            public void onPrepareLoad(Drawable drawable) {
            }

            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                BGAImageLoader.DownloadDelegate downloadDelegate2 = downloadDelegate;
                if (downloadDelegate2 != null) {
                    downloadDelegate2.onSuccess(path, bitmap);
                }
            }

            public void onBitmapFailed(Drawable drawable) {
                BGAImageLoader.DownloadDelegate downloadDelegate2 = downloadDelegate;
                if (downloadDelegate2 != null) {
                    downloadDelegate2.onFailed(path);
                }
            }
        });
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void pause(Activity activity) {
        Picasso.with(activity).pauseTag(activity);
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void resume(Activity activity) {
        Picasso.with(activity).resumeTag(activity);
    }
}