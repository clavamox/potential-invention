package com.mylhyl.circledialog;

import android.app.Activity;
import android.util.DisplayMetrics;

/* loaded from: classes.dex */
class SystemBarConfig {
    private final int mScreenHeight;
    private final int mScreenWidth;

    public SystemBarConfig(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.mScreenWidth = displayMetrics.widthPixels;
        this.mScreenHeight = displayMetrics.heightPixels;
    }

    public int[] getScreenSize() {
        return new int[]{this.mScreenWidth, this.mScreenHeight};
    }

    public int getScreenWidth() {
        return this.mScreenWidth;
    }

    public int getScreenHeight() {
        return this.mScreenHeight;
    }
}