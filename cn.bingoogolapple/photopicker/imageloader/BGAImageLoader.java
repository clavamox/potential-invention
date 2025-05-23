package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

/* loaded from: classes.dex */
public abstract class BGAImageLoader {

    public interface DisplayDelegate {
        void onSuccess(View view, String str);
    }

    public interface DownloadDelegate {
        void onFailed(String str);

        void onSuccess(String str, Bitmap bitmap);
    }

    public abstract void display(ImageView imageView, String str, int i, int i2, int i3, int i4, DisplayDelegate displayDelegate);

    public abstract void download(String str, DownloadDelegate downloadDelegate);

    public abstract void pause(Activity activity);

    public abstract void resume(Activity activity);

    protected String getPath(String str) {
        if (str == null) {
            str = "";
        }
        return (str.startsWith("http") || str.startsWith("file")) ? str : "file://" + str;
    }

    protected Activity getActivity(View view) {
        for (Context context = view.getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
        }
        return null;
    }
}