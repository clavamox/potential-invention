package com.gyf.immersionbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

/* loaded from: classes.dex */
class ImmersionDelegate implements Runnable {
    private BarProperties mBarProperties;
    private ImmersionBar mImmersionBar;
    private int mNotchHeight;
    private OnBarListener mOnBarListener;

    ImmersionDelegate(Object obj) {
        if (obj instanceof Activity) {
            if (this.mImmersionBar == null) {
                this.mImmersionBar = new ImmersionBar((Activity) obj);
                return;
            }
            return;
        }
        if (obj instanceof Fragment) {
            if (this.mImmersionBar == null) {
                if (obj instanceof DialogFragment) {
                    this.mImmersionBar = new ImmersionBar((DialogFragment) obj);
                    return;
                } else {
                    this.mImmersionBar = new ImmersionBar((Fragment) obj);
                    return;
                }
            }
            return;
        }
        if ((obj instanceof android.app.Fragment) && this.mImmersionBar == null) {
            if (obj instanceof android.app.DialogFragment) {
                this.mImmersionBar = new ImmersionBar((android.app.DialogFragment) obj);
            } else {
                this.mImmersionBar = new ImmersionBar((android.app.Fragment) obj);
            }
        }
    }

    ImmersionDelegate(Activity activity, Dialog dialog) {
        if (this.mImmersionBar == null) {
            this.mImmersionBar = new ImmersionBar(activity, dialog);
        }
    }

    public ImmersionBar get() {
        return this.mImmersionBar;
    }

    void onActivityCreated(Configuration configuration) {
        barChanged(configuration);
    }

    void onResume() {
        ImmersionBar immersionBar = this.mImmersionBar;
        if (immersionBar != null) {
            immersionBar.onResume();
        }
    }

    void onDestroy() {
        this.mBarProperties = null;
        ImmersionBar immersionBar = this.mImmersionBar;
        if (immersionBar != null) {
            immersionBar.onDestroy();
            this.mImmersionBar = null;
        }
    }

    void onConfigurationChanged(Configuration configuration) {
        ImmersionBar immersionBar = this.mImmersionBar;
        if (immersionBar != null) {
            immersionBar.onConfigurationChanged(configuration);
            barChanged(configuration);
        }
    }

    private void barChanged(Configuration configuration) {
        ImmersionBar immersionBar = this.mImmersionBar;
        if (immersionBar == null || !immersionBar.initialized()) {
            return;
        }
        OnBarListener onBarListener = this.mImmersionBar.getBarParams().onBarListener;
        this.mOnBarListener = onBarListener;
        if (onBarListener != null) {
            Activity activity = this.mImmersionBar.getActivity();
            if (this.mBarProperties == null) {
                this.mBarProperties = new BarProperties();
            }
            this.mBarProperties.setPortrait(configuration.orientation == 1);
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            if (rotation == 1) {
                this.mBarProperties.setLandscapeLeft(true);
                this.mBarProperties.setLandscapeRight(false);
            } else if (rotation == 3) {
                this.mBarProperties.setLandscapeLeft(false);
                this.mBarProperties.setLandscapeRight(true);
            } else {
                this.mBarProperties.setLandscapeLeft(false);
                this.mBarProperties.setLandscapeRight(false);
            }
            activity.getWindow().getDecorView().post(this);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        ImmersionBar immersionBar = this.mImmersionBar;
        if (immersionBar == null || immersionBar.getActivity() == null) {
            return;
        }
        Activity activity = this.mImmersionBar.getActivity();
        BarConfig barConfig = new BarConfig(activity);
        this.mBarProperties.setStatusBarHeight(barConfig.getStatusBarHeight());
        this.mBarProperties.setNavigationBar(barConfig.hasNavigationBar());
        this.mBarProperties.setNavigationBarHeight(barConfig.getNavigationBarHeight());
        this.mBarProperties.setNavigationBarWidth(barConfig.getNavigationBarWidth());
        this.mBarProperties.setActionBarHeight(barConfig.getActionBarHeight());
        boolean hasNotchScreen = NotchUtils.hasNotchScreen(activity);
        this.mBarProperties.setNotchScreen(hasNotchScreen);
        if (hasNotchScreen && this.mNotchHeight == 0) {
            int notchHeight = NotchUtils.getNotchHeight(activity);
            this.mNotchHeight = notchHeight;
            this.mBarProperties.setNotchHeight(notchHeight);
        }
        this.mOnBarListener.onBarChange(this.mBarProperties);
    }
}