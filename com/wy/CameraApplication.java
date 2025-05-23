package com.wy;

import android.app.Application;
import android.content.res.Resources;
import androidx.core.os.ConfigurationCompat;

/* loaded from: classes.dex */
public class CameraApplication extends Application {
    public static String Wifiname = null;
    public static boolean Willentersetting = false;
    public static boolean back_to_main = false;
    public static int is_compare = 0;
    private static CameraApplication mThis = null;
    public static int this_device = 100;

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        mThis = this;
        AppCrashHandler.init(this);
        ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
    }

    public static CameraApplication getInstance() {
        return mThis;
    }
}