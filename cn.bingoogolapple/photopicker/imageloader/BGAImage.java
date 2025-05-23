package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import cn.bingoogolapple.photopicker.imageloader.BGAImageLoader;

/* loaded from: classes.dex */
public class BGAImage {
    private static final String TAG = "BGAImage";
    private static BGAImageLoader sImageLoader;

    private BGAImage() {
    }

    private static final BGAImageLoader getImageLoader() {
        if (sImageLoader == null) {
            synchronized (BGAImage.class) {
                if (sImageLoader == null) {
                    if (isClassExists("com.bumptech.glide.Glide")) {
                        sImageLoader = new BGAGlideImageLoader();
                    } else if (isClassExists("com.squareup.picasso.Picasso")) {
                        sImageLoader = new BGAPicassoImageLoader();
                    } else if (isClassExists("com.nostra13.universalimageloader.core.ImageLoader")) {
                        sImageLoader = new BGAUILImageLoader();
                    } else if (isClassExists("org.xutils.x")) {
                        sImageLoader = new BGAXUtilsImageLoader();
                    } else {
                        throw new RuntimeException("必须在你的build.gradle文件中配置「Glide、Picasso、universal-image-loader、XUtils3」中的某一个图片加载库的依赖");
                    }
                }
            }
        }
        return sImageLoader;
    }

    public static void setImageLoader(BGAImageLoader bGAImageLoader) {
        sImageLoader = bGAImageLoader;
    }

    private static final boolean isClassExists(String str) {
        try {
            Class.forName(str);
            return true;
        } catch (ClassNotFoundException unused) {
            return false;
        }
    }

    public static void display(ImageView imageView, int i, int i2, String str, int i3, int i4, BGAImageLoader.DisplayDelegate displayDelegate) {
        try {
            getImageLoader().display(imageView, str, i, i2, i3, i4, displayDelegate);
        } catch (Exception e) {
            Log.d(TAG, "显示图片失败：" + e.getMessage());
        }
    }

    public static void display(ImageView imageView, int i, String str, int i2, int i3, BGAImageLoader.DisplayDelegate displayDelegate) {
        display(imageView, i, i, str, i2, i3, displayDelegate);
    }

    public static void display(ImageView imageView, int i, String str, int i2, int i3) {
        display(imageView, i, str, i2, i3, null);
    }

    public static void display(ImageView imageView, int i, String str, int i2) {
        display(imageView, i, str, i2, i2);
    }

    public static void download(String str, BGAImageLoader.DownloadDelegate downloadDelegate) {
        try {
            getImageLoader().download(str, downloadDelegate);
        } catch (Exception e) {
            Log.d(TAG, "下载图片失败：" + e.getMessage());
        }
    }

    public static void pause(Activity activity) {
        getImageLoader().pause(activity);
    }

    public static void resume(Activity activity) {
        getImageLoader().resume(activity);
    }
}