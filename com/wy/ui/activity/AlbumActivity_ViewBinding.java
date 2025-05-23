package com.wy.ui.activity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class AlbumActivity_ViewBinding implements Unbinder {
    private AlbumActivity target;

    public AlbumActivity_ViewBinding(AlbumActivity albumActivity) {
        this(albumActivity, albumActivity.getWindow().getDecorView());
    }

    public AlbumActivity_ViewBinding(AlbumActivity albumActivity, View view) {
        this.target = albumActivity;
        albumActivity.ib_photo = (ImageButton) Utils.findRequiredViewAsType(view, R.id.ib_photo, "field 'ib_photo'", ImageButton.class);
        albumActivity.ib_vedio = (ImageButton) Utils.findRequiredViewAsType(view, R.id.ib_vedio, "field 'ib_vedio'", ImageButton.class);
        albumActivity.viewpager = (ViewPager) Utils.findRequiredViewAsType(view, R.id.viewpager, "field 'viewpager'", ViewPager.class);
        albumActivity.tvComplete = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_complete, "field 'tvComplete'", TextView.class);
        albumActivity.tvDelete = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_delete, "field 'tvDelete'", TextView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        AlbumActivity albumActivity = this.target;
        if (albumActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        albumActivity.ib_photo = null;
        albumActivity.ib_vedio = null;
        albumActivity.viewpager = null;
        albumActivity.tvComplete = null;
        albumActivity.tvDelete = null;
    }
}