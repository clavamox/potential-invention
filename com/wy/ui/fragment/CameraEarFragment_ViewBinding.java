package com.wy.ui.fragment;

import android.view.View;
import android.widget.RadioButton;
import butterknife.internal.Utils;
import com.wy.ui.view.SegmentControl;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class CameraEarFragment_ViewBinding extends CameraBaseFragment_ViewBinding {
    private CameraEarFragment target;

    public CameraEarFragment_ViewBinding(CameraEarFragment cameraEarFragment, View view) {
        super(cameraEarFragment, view);
        this.target = cameraEarFragment;
        cameraEarFragment.segment = (SegmentControl) Utils.findRequiredViewAsType(view, R.id.segment, "field 'segment'", SegmentControl.class);
        cameraEarFragment.rb_ear = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_ear, "field 'rb_ear'", RadioButton.class);
        cameraEarFragment.rb_vedio = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_vedio, "field 'rb_vedio'", RadioButton.class);
        cameraEarFragment.rv_photo = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rv_photo, "field 'rv_photo'", RadioButton.class);
        cameraEarFragment.rb_compare = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_compare, "field 'rb_compare'", RadioButton.class);
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment_ViewBinding, butterknife.Unbinder
    public void unbind() {
        CameraEarFragment cameraEarFragment = this.target;
        if (cameraEarFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        cameraEarFragment.segment = null;
        cameraEarFragment.rb_ear = null;
        cameraEarFragment.rb_vedio = null;
        cameraEarFragment.rv_photo = null;
        cameraEarFragment.rb_compare = null;
        super.unbind();
    }
}