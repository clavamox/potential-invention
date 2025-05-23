package com.wy.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.internal.Utils;
import com.wy.ui.view.CameraSegmentControl;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class CameraMouthFragment_ViewBinding extends CameraBaseFragment_ViewBinding {
    private CameraMouthFragment target;

    public CameraMouthFragment_ViewBinding(CameraMouthFragment cameraMouthFragment, View view) {
        super(cameraMouthFragment, view);
        this.target = cameraMouthFragment;
        cameraMouthFragment.segment = (CameraSegmentControl) Utils.findRequiredViewAsType(view, R.id.segment, "field 'segment'", CameraSegmentControl.class);
        cameraMouthFragment.iv_camera = (ImageView) Utils.findRequiredViewAsType(view, R.id.iv_camera, "field 'iv_camera'", ImageView.class);
        cameraMouthFragment.tv_vedio = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_vedio, "field 'tv_vedio'", TextView.class);
        cameraMouthFragment.tv_picture = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_picture, "field 'tv_picture'", TextView.class);
        cameraMouthFragment.cb_compare = (RadioButton) Utils.findRequiredViewAsType(view, R.id.cb_compare, "field 'cb_compare'", RadioButton.class);
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment_ViewBinding, butterknife.Unbinder
    public void unbind() {
        CameraMouthFragment cameraMouthFragment = this.target;
        if (cameraMouthFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        cameraMouthFragment.segment = null;
        cameraMouthFragment.iv_camera = null;
        cameraMouthFragment.tv_vedio = null;
        cameraMouthFragment.tv_picture = null;
        cameraMouthFragment.cb_compare = null;
        super.unbind();
    }
}