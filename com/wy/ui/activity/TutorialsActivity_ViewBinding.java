package com.wy.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class TutorialsActivity_ViewBinding implements Unbinder {
    private TutorialsActivity target;

    public TutorialsActivity_ViewBinding(TutorialsActivity tutorialsActivity) {
        this(tutorialsActivity, tutorialsActivity.getWindow().getDecorView());
    }

    public TutorialsActivity_ViewBinding(TutorialsActivity tutorialsActivity, View view) {
        this.target = tutorialsActivity;
        tutorialsActivity.viewpager = (ViewPager) Utils.findRequiredViewAsType(view, R.id.viewpager, "field 'viewpager'", ViewPager.class);
        tutorialsActivity.layoutPoint = (LinearLayout) Utils.findRequiredViewAsType(view, R.id.point, "field 'layoutPoint'", LinearLayout.class);
        tutorialsActivity.btnStartOpt = (Button) Utils.findRequiredViewAsType(view, R.id.btn_start_opt, "field 'btnStartOpt'", Button.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        TutorialsActivity tutorialsActivity = this.target;
        if (tutorialsActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        tutorialsActivity.viewpager = null;
        tutorialsActivity.layoutPoint = null;
        tutorialsActivity.btnStartOpt = null;
    }
}