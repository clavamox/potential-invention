package com.wy.ui.fragment;

import android.view.View;
import android.widget.RadioButton;
import butterknife.internal.Utils;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class CameraBlackHeadFragment_ViewBinding extends CameraBaseFragment_ViewBinding {
    private CameraBlackHeadFragment target;

    public CameraBlackHeadFragment_ViewBinding(CameraBlackHeadFragment cameraBlackHeadFragment, View view) {
        super(cameraBlackHeadFragment, view);
        this.target = cameraBlackHeadFragment;
        cameraBlackHeadFragment.rb_vedio_record = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_vedio_record, "field 'rb_vedio_record'", RadioButton.class);
        cameraBlackHeadFragment.rb_take_picture = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_take_picture, "field 'rb_take_picture'", RadioButton.class);
        cameraBlackHeadFragment.rb_tutorials = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_tutorials, "field 'rb_tutorials'", RadioButton.class);
        cameraBlackHeadFragment.rb_compare = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_compare, "field 'rb_compare'", RadioButton.class);
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment_ViewBinding, butterknife.Unbinder
    public void unbind() {
        CameraBlackHeadFragment cameraBlackHeadFragment = this.target;
        if (cameraBlackHeadFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        cameraBlackHeadFragment.rb_vedio_record = null;
        cameraBlackHeadFragment.rb_take_picture = null;
        cameraBlackHeadFragment.rb_tutorials = null;
        cameraBlackHeadFragment.rb_compare = null;
        super.unbind();
    }
}