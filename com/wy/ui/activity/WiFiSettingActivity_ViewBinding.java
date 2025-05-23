package com.wy.ui.activity;

import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class WiFiSettingActivity_ViewBinding implements Unbinder {
    private WiFiSettingActivity target;

    public WiFiSettingActivity_ViewBinding(WiFiSettingActivity wiFiSettingActivity) {
        this(wiFiSettingActivity, wiFiSettingActivity.getWindow().getDecorView());
    }

    public WiFiSettingActivity_ViewBinding(WiFiSettingActivity wiFiSettingActivity, View view) {
        this.target = wiFiSettingActivity;
        wiFiSettingActivity.iv_wifi = (ImageView) Utils.findRequiredViewAsType(view, R.id.iv_wifi, "field 'iv_wifi'", ImageView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        WiFiSettingActivity wiFiSettingActivity = this.target;
        if (wiFiSettingActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        wiFiSettingActivity.iv_wifi = null;
    }
}