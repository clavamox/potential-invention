package com.wy.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.gyf.immersionbar.ImmersionBar;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class SplashActivity extends AppCompatActivity {
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() { // from class: com.wy.ui.activity.SplashActivity.1
            @Override // java.lang.Runnable
            public void run() {
                SplashActivity.this.startActivity(new Intent(SplashActivity.this, (Class<?>) MainActivity.class));
                SplashActivity.this.finish();
            }
        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
    }

    protected void setStatusBarAndNavigation() {
        ImmersionBar.with(this);
        ImmersionBar.showStatusBar(getWindow());
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
    }
}