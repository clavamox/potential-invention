package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

/* loaded from: classes.dex */
public class BGARVOnScrollListener extends RecyclerView.OnScrollListener {
    private Activity mActivity;

    public BGARVOnScrollListener(Activity activity) {
        this.mActivity = activity;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
    public void onScrollStateChanged(RecyclerView recyclerView, int i) {
        if (i == 0) {
            BGAImage.resume(this.mActivity);
        } else if (i == 1) {
            BGAImage.pause(this.mActivity);
        }
    }
}