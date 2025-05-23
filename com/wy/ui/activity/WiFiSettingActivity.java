package com.wy.ui.activity;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import butterknife.BindView;
import com.wy.CameraApplication;
import com.wy.ui.fragment.WiFiSettingFragment;
import com.xzf.camera.R;
import java.util.Locale;

/* loaded from: classes.dex */
public class WiFiSettingActivity extends BaseActivity implements View.OnClickListener {
    private Drawable drawable = null;

    @BindView(R.id.iv_wifi)
    ImageView iv_wifi;

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_wifi_setting);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        String language = getResources().getConfiguration().locale.getLanguage();
        Log.e("zxy", "tjCountry: language:" + language + ",local:" + Locale.getDefault().toString() + ",country:" + getResources().getConfiguration().locale.getCountry());
        if (language.toLowerCase().contains("en")) {
            this.drawable = getResources().getDrawable(R.mipmap.wlan_wifi_en);
            ((ImageView) findViewById(R.id.iv_wifi)).setImageDrawable(this.drawable);
        } else if (language.toLowerCase().contains("zh")) {
            this.drawable = getResources().getDrawable(R.mipmap.wlan_wifi);
            ((ImageView) findViewById(R.id.iv_wifi)).setImageDrawable(this.drawable);
        } else {
            this.drawable = getResources().getDrawable(R.mipmap.wlan_wifi_en);
            ((ImageView) findViewById(R.id.iv_wifi)).setImageDrawable(this.drawable);
        }
        this.tvNavTitle.setText("PAKISS");
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() != R.id.btn_skip) {
            return;
        }
        CameraApplication.Willentersetting = true;
        new WiFiSettingFragment();
        finish();
    }
}