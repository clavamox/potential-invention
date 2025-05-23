package com.wy.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class CameraActivity_ViewBinding implements Unbinder {
    private CameraActivity target;

    public CameraActivity_ViewBinding(CameraActivity cameraActivity) {
        this(cameraActivity, cameraActivity.getWindow().getDecorView());
    }

    public CameraActivity_ViewBinding(CameraActivity cameraActivity, View view) {
        this.target = cameraActivity;
        cameraActivity.tv_black_head = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_black_head, "field 'tv_black_head'", TextView.class);
        cameraActivity.tv_ear = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_ear, "field 'tv_ear'", TextView.class);
        cameraActivity.tv_mouth = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_mouth, "field 'tv_mouth'", TextView.class);
        cameraActivity.tv_tooth = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_tooth, "field 'tv_tooth'", TextView.class);
        cameraActivity.iv_mobile_portrait = (ImageView) Utils.findRequiredViewAsType(view, R.id.iv_mobile_portrait, "field 'iv_mobile_portrait'", ImageView.class);
        cameraActivity.iv_lock = (ImageView) Utils.findRequiredViewAsType(view, R.id.iv_lock, "field 'iv_lock'", ImageView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        CameraActivity cameraActivity = this.target;
        if (cameraActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        cameraActivity.tv_black_head = null;
        cameraActivity.tv_ear = null;
        cameraActivity.tv_mouth = null;
        cameraActivity.tv_tooth = null;
        cameraActivity.iv_mobile_portrait = null;
        cameraActivity.iv_lock = null;
    }
}