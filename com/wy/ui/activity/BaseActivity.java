package com.wy.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.gyf.immersionbar.ImmersionBar;
import com.wy.language.Config;
import com.wy.language.Store;
import com.xzf.camera.R;
import java.util.Locale;

/* loaded from: classes.dex */
public abstract class BaseActivity extends AppCompatActivity {
    private static final int REQ_CODE = 153;
    protected ImageView ivNavBack;
    protected ImageView ivNavMenu;
    protected TextView tvNavTitle;
    private Unbinder unbinder;
    private String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.CAMERA", "android.permission.INTERNET", "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS"};
    private String[] locationPermissions = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS"};
    BroadcastReceiver languageBroadcastReceive = new BroadcastReceiver() { // from class: com.wy.ui.activity.BaseActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(NotificationCompat.CATEGORY_MESSAGE).equals("EVENT_REFRESH_LANGUAGE")) {
                BaseActivity.this.changeAppLanguage();
                BaseActivity.this.recreate();
            }
        }
    };

    abstract void setContentView();

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(128);
        setContentView();
        this.unbinder = ButterKnife.bind(this);
        ImmersionBar.with(this).init();
        setStatusBarAndNavigation();
        initView();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Config.ACTION);
        registerReceiver(this.languageBroadcastReceive, intentFilter);
        changeAppLanguage();
        checkPermissions(this);
    }

    protected void initView() {
        try {
            ImageView imageView = (ImageView) findViewById(R.id.iv_nav_left);
            this.ivNavBack = imageView;
            if (imageView != null) {
                imageView.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.activity.BaseActivity$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        BaseActivity.this.m71lambda$initView$0$comwyuiactivityBaseActivity(view);
                    }
                });
            }
            ImageView imageView2 = (ImageView) findViewById(R.id.iv_nav_right);
            this.ivNavMenu = imageView2;
            if (imageView2 != null) {
                imageView2.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.activity.BaseActivity$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        BaseActivity.this.m72lambda$initView$1$comwyuiactivityBaseActivity(view);
                    }
                });
            }
            this.tvNavTitle = (TextView) findViewById(R.id.tv_nav_title);
        } catch (Exception unused) {
        }
    }

    /* renamed from: lambda$initView$0$com-wy-ui-activity-BaseActivity, reason: not valid java name */
    /* synthetic */ void m71lambda$initView$0$comwyuiactivityBaseActivity(View view) {
        Log.d("zwn", "zwn 返回");
        finish();
    }

    /* renamed from: lambda$initView$1$com-wy-ui-activity-BaseActivity, reason: not valid java name */
    /* synthetic */ void m72lambda$initView$1$comwyuiactivityBaseActivity(View view) {
        startActivity(new Intent(this, (Class<?>) HelpActivity.class));
    }

    protected void setStatusBarAndNavigation() {
        ImmersionBar.with(this);
        ImmersionBar.showStatusBar(getWindow());
        getWindow().getDecorView().setSystemUiVisibility(1280);
        getWindow().clearFlags(134217728);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(this.languageBroadcastReceive);
            this.unbinder.unbind();
        } catch (Exception unused) {
        }
    }

    public void changeAppLanguage() {
        String languageLocal = Store.getLanguageLocal(this);
        if (languageLocal == null || "".equals(languageLocal)) {
            return;
        }
        Locale locale = null;
        try {
            if (languageLocal.equals(Locale.UK.getLanguage() + "-" + Locale.UK.getCountry())) {
                locale = Locale.UK;
            } else if (languageLocal.equals(Locale.SIMPLIFIED_CHINESE.getLanguage() + "-" + Locale.SIMPLIFIED_CHINESE.getCountry())) {
                locale = Locale.SIMPLIFIED_CHINESE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (locale != null) {
            Log.d("BaseActivity", languageLocal + "===>>>>changeAppLanguage:" + locale.getLanguage() + "-" + locale.getCountry());
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, displayMetrics);
        }
    }

    private void checkPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= 30) {
            checkPermissions(this.locationPermissions);
        } else {
            checkPermissions(this.permissions);
        }
    }

    private void checkPermissions(String[] strArr) {
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= strArr.length) {
                z = true;
                break;
            } else if (ContextCompat.checkSelfPermission(getApplicationContext(), strArr[i]) != 0) {
                break;
            } else {
                i++;
            }
        }
        if (z) {
            return;
        }
        ActivityCompat.requestPermissions(this, strArr, REQ_CODE);
    }
}