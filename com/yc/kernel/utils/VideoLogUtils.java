package com.yc.kernel.utils;

import android.util.Log;

/* loaded from: classes.dex */
public final class VideoLogUtils {
    private static final String TAG = "YCVideoPlayer";
    private static boolean isLog = false;

    public static void setIsLog(boolean z) {
        isLog = z;
    }

    public static boolean isIsLog() {
        return isLog;
    }

    public static void d(String str) {
        if (isLog) {
            Log.d(TAG, str);
        }
    }

    public static void i(String str) {
        if (isLog) {
            Log.i(TAG, str);
        }
    }

    public static void e(String str) {
        if (isLog) {
            Log.e(TAG, str);
        }
    }

    public static void e(String str, Throwable th) {
        if (isLog) {
            Log.e(TAG, str, th);
        }
    }
}