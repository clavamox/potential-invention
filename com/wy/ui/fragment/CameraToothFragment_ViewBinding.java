package com.wy.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.internal.Utils;
import com.wy.ui.view.SegmentControl;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class CameraToothFragment_ViewBinding extends CameraBaseFragment_ViewBinding {
    private CameraToothFragment target;

    public CameraToothFragment_ViewBinding(CameraToothFragment cameraToothFragment, View view) {
        super(cameraToothFragment, view);
        this.target = cameraToothFragment;
        cameraToothFragment.segment = (SegmentControl) Utils.findRequiredViewAsType(view, R.id.segment, "field 'segment'", SegmentControl.class);
        cameraToothFragment.iv_camera = (ImageView) Utils.findRequiredViewAsType(view, R.id.iv_camera, "field 'iv_camera'", ImageView.class);
        cameraToothFragment.tv_vedio = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_vedio, "field 'tv_vedio'", TextView.class);
        cameraToothFragment.tv_picture = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_picture, "field 'tv_picture'", TextView.class);
        cameraToothFragment.cb_compare = (RadioButton) Utils.findRequiredViewAsType(view, R.id.cb_compare, "field 'cb_compare'", RadioButton.class);
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment_ViewBinding, butterknife.Unbinder
    public void unbind() {
        CameraToothFragment cameraToothFragment = this.target;
        if (cameraToothFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        cameraToothFragment.segment = null;
        cameraToothFragment.iv_camera = null;
        cameraToothFragment.tv_vedio = null;
        cameraToothFragment.tv_picture = null;
        cameraToothFragment.cb_compare = null;
        super.unbind();
    }
}