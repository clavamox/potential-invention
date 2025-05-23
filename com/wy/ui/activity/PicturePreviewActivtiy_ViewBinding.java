package com.wy.ui.activity;

import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class PicturePreviewActivtiy_ViewBinding implements Unbinder {
    private PicturePreviewActivtiy target;

    public PicturePreviewActivtiy_ViewBinding(PicturePreviewActivtiy picturePreviewActivtiy) {
        this(picturePreviewActivtiy, picturePreviewActivtiy.getWindow().getDecorView());
    }

    public PicturePreviewActivtiy_ViewBinding(PicturePreviewActivtiy picturePreviewActivtiy, View view) {
        this.target = picturePreviewActivtiy;
        picturePreviewActivtiy.imageView = (ImageView) Utils.findRequiredViewAsType(view, R.id.imageview, "field 'imageView'", ImageView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        PicturePreviewActivtiy picturePreviewActivtiy = this.target;
        if (picturePreviewActivtiy == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        picturePreviewActivtiy.imageView = null;
    }
}