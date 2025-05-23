package com.wy.ui.fragment;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.belon.view.CameraImageView;
import com.wy.ui.view.ResizeAbleSurfaceView;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class CameraBaseFragment_ViewBinding implements Unbinder {
    private CameraBaseFragment target;

    public CameraBaseFragment_ViewBinding(CameraBaseFragment cameraBaseFragment, View view) {
        this.target = cameraBaseFragment;
        cameraBaseFragment.ll_dev_camera = (ConstraintLayout) Utils.findRequiredViewAsType(view, R.id.ll_dev_camera, "field 'll_dev_camera'", ConstraintLayout.class);
        cameraBaseFragment.iv_dev_camera = (CameraImageView) Utils.findRequiredViewAsType(view, R.id.iv_dev_camera, "field 'iv_dev_camera'", CameraImageView.class);
        cameraBaseFragment.iv_mobile_camera = (FrameLayout) Utils.findRequiredViewAsType(view, R.id.iv_mobile_camera, "field 'iv_mobile_camera'", FrameLayout.class);
        cameraBaseFragment.surface_view = (ResizeAbleSurfaceView) Utils.findRequiredViewAsType(view, R.id.surface_view, "field 'surface_view'", ResizeAbleSurfaceView.class);
        cameraBaseFragment.rb_screen_change = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_screen_change, "field 'rb_screen_change'", RadioButton.class);
        cameraBaseFragment.ivCompare = (ImageView) Utils.findRequiredViewAsType(view, R.id.iv_cmp, "field 'ivCompare'", ImageView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        CameraBaseFragment cameraBaseFragment = this.target;
        if (cameraBaseFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        cameraBaseFragment.ll_dev_camera = null;
        cameraBaseFragment.iv_dev_camera = null;
        cameraBaseFragment.iv_mobile_camera = null;
        cameraBaseFragment.surface_view = null;
        cameraBaseFragment.rb_screen_change = null;
        cameraBaseFragment.ivCompare = null;
    }
}