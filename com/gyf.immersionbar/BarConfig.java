package com.gyf.immersionbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;

/* loaded from: classes.dex */
class BarConfig {
    private final int mActionBarHeight;
    private final boolean mHasNavigationBar;
    private final boolean mInPortrait;
    private final int mNavigationBarHeight;
    private final int mNavigationBarWidth;
    private final float mSmallestWidthDp;
    private final int mStatusBarHeight;

    BarConfig(Activity activity) {
        this.mInPortrait = activity.getResources().getConfiguration().orientation == 1;
        this.mSmallestWidthDp = getSmallestWidthDp(activity);
        this.mStatusBarHeight = getInternalDimensionSize(activity, "status_bar_height");
        this.mActionBarHeight = getActionBarHeight(activity);
        int navigationBarHeight = getNavigationBarHeight(activity);
        this.mNavigationBarHeight = navigationBarHeight;
        this.mNavigationBarWidth = getNavigationBarWidth(activity);
        this.mHasNavigationBar = navigationBarHeight > 0;
    }

    private int getActionBarHeight(Activity activity) {
        View findViewById = activity.getWindow().findViewById(R.id.action_bar_container);
        int measuredHeight = findViewById != null ? findViewById.getMeasuredHeight() : 0;
        if (measuredHeight != 0) {
            return measuredHeight;
        }
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
        return TypedValue.complexToDimensionPixelSize(typedValue.data, activity.getResources().getDisplayMetrics());
    }

    private int getNavigationBarHeight(Context context) {
        if (hasNavBar((Activity) context)) {
            return getInternalDimensionSize(context, this.mInPortrait ? "navigation_bar_height" : "navigation_bar_height_landscape");
        }
        return 0;
    }

    private int getNavigationBarWidth(Context context) {
        if (hasNavBar((Activity) context)) {
            return getInternalDimensionSize(context, "navigation_bar_width");
        }
        return 0;
    }

    private boolean hasNavBar(Activity activity) {
        if (Settings.Global.getInt(activity.getContentResolver(), "force_fsg_nav_bar", 0) != 0) {
            return false;
        }
        if (OSUtils.isEMUI()) {
            if (OSUtils.isEMUI3_x()) {
                if (Settings.System.getInt(activity.getContentResolver(), "navigationbar_is_min", 0) != 0) {
                    return false;
                }
            } else if (Settings.Global.getInt(activity.getContentResolver(), "navigationbar_is_min", 0) != 0) {
                return false;
            }
        }
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(displayMetrics);
        int i = displayMetrics.heightPixels;
        int i2 = displayMetrics.widthPixels;
        DisplayMetrics displayMetrics2 = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics2);
        return i2 - displayMetrics2.widthPixels > 0 || i - displayMetrics2.heightPixels > 0;
    }

    private int getInternalDimensionSize(Context context, String str) {
        try {
            int identifier = Resources.getSystem().getIdentifier(str, "dimen", "android");
            if (identifier > 0) {
                int dimensionPixelSize = context.getResources().getDimensionPixelSize(identifier);
                int dimensionPixelSize2 = Resources.getSystem().getDimensionPixelSize(identifier);
                if (dimensionPixelSize2 >= dimensionPixelSize) {
                    return dimensionPixelSize2;
                }
                float f = (dimensionPixelSize * Resources.getSystem().getDisplayMetrics().density) / context.getResources().getDisplayMetrics().density;
                return (int) (f >= 0.0f ? f + 0.5f : f - 0.5f);
            }
        } catch (Resources.NotFoundException unused) {
        }
        return 0;
    }

    private float getSmallestWidthDp(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return Math.min(displayMetrics.widthPixels / displayMetrics.density, displayMetrics.heightPixels / displayMetrics.density);
    }

    boolean isNavigationAtBottom() {
        return this.mSmallestWidthDp >= 600.0f || this.mInPortrait;
    }

    int getStatusBarHeight() {
        return this.mStatusBarHeight;
    }

    int getActionBarHeight() {
        return this.mActionBarHeight;
    }

    boolean hasNavigationBar() {
        return this.mHasNavigationBar;
    }

    int getNavigationBarHeight() {
        return this.mNavigationBarHeight;
    }

    int getNavigationBarWidth() {
        return this.mNavigationBarWidth;
    }
}