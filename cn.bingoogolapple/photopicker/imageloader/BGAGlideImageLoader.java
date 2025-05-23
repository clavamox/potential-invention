package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.imageloader.BGAImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

/* loaded from: classes.dex */
public class BGAGlideImageLoader extends BGAImageLoader {
    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void display(final ImageView imageView, String str, int i, int i2, int i3, int i4, final BGAImageLoader.DisplayDelegate displayDelegate) {
        final String path = getPath(str);
        Glide.with(getActivity(imageView)).load(path).apply((BaseRequestOptions<?>) new RequestOptions().placeholder(i).error(i2).override(i3, i4).dontAnimate()).listener(new RequestListener<Drawable>() { // from class: cn.bingoogolapple.photopicker.imageloader.BGAGlideImageLoader.1
            @Override // com.bumptech.glide.request.RequestListener
            public boolean onLoadFailed(GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                return false;
            }

            @Override // com.bumptech.glide.request.RequestListener
            public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                BGAImageLoader.DisplayDelegate displayDelegate2 = displayDelegate;
                if (displayDelegate2 == null) {
                    return false;
                }
                displayDelegate2.onSuccess(imageView, path);
                return false;
            }
        }).into(imageView);
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void download(String str, final BGAImageLoader.DownloadDelegate downloadDelegate) {
        final String path = getPath(str);
        Glide.with(BGABaseAdapterUtil.getApp()).asBitmap().load(path).into((RequestBuilder<Bitmap>) new CustomTarget<Bitmap>() { // from class: cn.bingoogolapple.photopicker.imageloader.BGAGlideImageLoader.2
            @Override // com.bumptech.glide.request.target.Target
            public void onLoadCleared(Drawable drawable) {
            }

            @Override // com.bumptech.glide.request.target.Target
            public /* bridge */ /* synthetic */ void onResourceReady(Object obj, Transition transition) {
                onResourceReady((Bitmap) obj, (Transition<? super Bitmap>) transition);
            }

            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                BGAImageLoader.DownloadDelegate downloadDelegate2 = downloadDelegate;
                if (downloadDelegate2 != null) {
                    downloadDelegate2.onSuccess(path, bitmap);
                }
            }

            @Override // com.bumptech.glide.request.target.CustomTarget, com.bumptech.glide.request.target.Target
            public void onLoadFailed(Drawable drawable) {
                BGAImageLoader.DownloadDelegate downloadDelegate2 = downloadDelegate;
                if (downloadDelegate2 != null) {
                    downloadDelegate2.onFailed(path);
                }
            }
        });
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void pause(Activity activity) {
        Glide.with(activity).pauseRequests();
    }

    @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader
    public void resume(Activity activity) {
        Glide.with(activity).resumeRequestsRecursive();
    }
}