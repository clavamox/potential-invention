package androidx.core.os;

import android.os.Build;

/* loaded from: classes.dex */
public class BuildCompat {
    @Deprecated
    public static boolean isAtLeastN() {
        return true;
    }

    private BuildCompat() {
    }

    @Deprecated
    public static boolean isAtLeastNMR1() {
        return Build.VERSION.SDK_INT >= 25;
    }

    @Deprecated
    public static boolean isAtLeastO() {
        return Build.VERSION.SDK_INT >= 26;
    }

    @Deprecated
    public static boolean isAtLeastOMR1() {
        return Build.VERSION.SDK_INT >= 27;
    }

    @Deprecated
    public static boolean isAtLeastP() {
        return Build.VERSION.SDK_INT >= 28;
    }

    @Deprecated
    public static boolean isAtLeastQ() {
        return Build.VERSION.SDK_INT >= 29;
    }

    public static boolean isAtLeastR() {
        return Build.VERSION.CODENAME.length() == 1 && Build.VERSION.CODENAME.charAt(0) >= 'R' && Build.VERSION.CODENAME.charAt(0) <= 'Z';
    }
}