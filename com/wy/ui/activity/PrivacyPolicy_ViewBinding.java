package com.wy.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class PrivacyPolicy_ViewBinding implements Unbinder {
    private PrivacyPolicy target;

    public PrivacyPolicy_ViewBinding(PrivacyPolicy privacyPolicy) {
        this(privacyPolicy, privacyPolicy.getWindow().getDecorView());
    }

    public PrivacyPolicy_ViewBinding(PrivacyPolicy privacyPolicy, View view) {
        this.target = privacyPolicy;
        privacyPolicy.ivNavLeft = (ImageView) Utils.findRequiredViewAsType(view, R.id.iv_nav_left, "field 'ivNavLeft'", ImageView.class);
        privacyPolicy.tvNavTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_nav_title, "field 'tvNavTitle'", TextView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        PrivacyPolicy privacyPolicy = this.target;
        if (privacyPolicy == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        privacyPolicy.ivNavLeft = null;
        privacyPolicy.tvNavTitle = null;
    }
}