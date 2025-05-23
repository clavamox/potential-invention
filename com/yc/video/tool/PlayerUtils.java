package com.yc.video.tool;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import com.yc.video.config.VideoPlayerConfig;
import com.yc.video.player.VideoViewManager;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/* loaded from: classes.dex */
public final class PlayerUtils {
    public static Activity scanForActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public static boolean isActivityLiving(Activity activity) {
        return (activity == null || activity.isFinishing() || activity.isDestroyed()) ? false : true;
    }

    private static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        }
        if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public static void showActionBar(Context context) {
        ActionBar supportActionBar = getAppCompActivity(context).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setShowHideAnimationEnabled(false);
            supportActionBar.show();
        }
        scanForActivity(context).getWindow().clearFlags(1024);
    }

    public static void hideActionBar(Context context) {
        ActionBar supportActionBar = getAppCompActivity(context).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setShowHideAnimationEnabled(false);
            supportActionBar.hide();
        }
        scanForActivity(context).getWindow().setFlags(1024, 1024);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static double getStatusBarHeight(Context context) {
        return context.getResources().getIdentifier("status_bar_height", "dimen", "android") > 0 ? context.getResources().getDimensionPixelSize(r0) : 0;
    }

    public static double getStatusBarHeightPortrait(Context context) {
        return context.getResources().getIdentifier("status_bar_height_portrait", "dimen", "android") > 0 ? context.getResources().getDimensionPixelSize(r0) : 0;
    }

    public static int getNavigationBarHeight(Context context) {
        if (!hasNavigationBar(context)) {
            return 0;
        }
        Resources resources = context.getResources();
        return resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"));
    }

    public static boolean hasNavigationBar(Context context) {
        Display defaultDisplay = getWindowManager(context).getDefaultDisplay();
        Point point = new Point();
        Point point2 = new Point();
        defaultDisplay.getSize(point);
        defaultDisplay.getRealSize(point2);
        return (point2.x == point.x && point2.y == point.y) ? false : true;
    }

    public static int getScreenWidth(Context context, boolean z) {
        if (z) {
            return context.getResources().getDisplayMetrics().widthPixels + getNavigationBarHeight(context);
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context, boolean z) {
        if (z) {
            return context.getResources().getDisplayMetrics().heightPixels + getNavigationBarHeight(context);
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int dp2px(Context context, float f) {
        return (int) TypedValue.applyDimension(1, f, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float f) {
        return (int) TypedValue.applyDimension(2, f, context.getResources().getDisplayMetrics());
    }

    public static <T> List<T> getSnapshot(Collection<T> collection) {
        ArrayList arrayList = new ArrayList(collection.size());
        for (T t : collection) {
            if (t != null) {
                arrayList.add(t);
            }
        }
        return arrayList;
    }

    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService("window");
    }

    public static boolean isEdge(Context context, MotionEvent motionEvent) {
        int dp2px = dp2px(context, 40.0f);
        float f = dp2px;
        return motionEvent.getRawX() < f || motionEvent.getRawX() > ((float) (getScreenWidth(context, true) - dp2px)) || motionEvent.getRawY() < f || motionEvent.getRawY() > ((float) (getScreenHeight(context, true) - dp2px));
    }

    public static String formatTime(long j) {
        if (j <= 0 || j >= 86400000) {
            return "00:00";
        }
        long j2 = j / 1000;
        long j3 = j2 % 60;
        long j4 = (j2 / 60) % 60;
        long j5 = j2 / 3600;
        Formatter formatter = new Formatter(new StringBuilder(), Locale.getDefault());
        return j5 > 0 ? formatter.format("%d:%02d:%02d", Long.valueOf(j5), Long.valueOf(j4), Long.valueOf(j3)).toString() : formatter.format("%02d:%02d", Long.valueOf(j4), Long.valueOf(j3)).toString();
    }

    public static synchronized void savePlayPosition(Context context, String str, long j) {
        synchronized (PlayerUtils.class) {
            if (context == null) {
                return;
            }
            context.getSharedPreferences("VIDEO_PLAYER_PLAY_POSITION", 0).edit().putLong(str, j).apply();
        }
    }

    public static synchronized long getSavedPlayPosition(Context context, String str) {
        synchronized (PlayerUtils.class) {
            if (context == null) {
                return 0L;
            }
            return context.getSharedPreferences("VIDEO_PLAYER_PLAY_POSITION", 0).getLong(str, 0L);
        }
    }

    public static synchronized void clearPlayPosition(Context context) {
        synchronized (PlayerUtils.class) {
            if (context == null) {
                return;
            }
            context.getSharedPreferences("VIDEO_PLAYER_PLAY_POSITION", 0).getAll().clear();
        }
    }

    public static boolean isConnected(Context context) {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo(context);
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getCurrentSystemTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService("connectivity");
        if (connectivityManager == null) {
            return null;
        }
        return connectivityManager.getActiveNetworkInfo();
    }

    public static <T> T requireNonNull(T t, String str) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(str);
    }

    public static String requireNonEmpty(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException(str2);
        }
        return str;
    }

    public static String getCountTimeByLong(long j) {
        long j2;
        long j3;
        long j4 = j / 1000;
        if (3600 <= j4) {
            j2 = j4 / 3600;
            j4 -= 3600 * j2;
        } else {
            j2 = 0;
        }
        if (60 <= j4) {
            j3 = j4 / 60;
            j4 -= 60 * j3;
        } else {
            j3 = 0;
        }
        long j5 = 0 <= j4 ? j4 : 0L;
        StringBuilder sb = new StringBuilder();
        if (j2 < 10) {
            sb.append("0").append(j2).append(":");
        } else {
            sb.append(j2).append(":");
        }
        if (j3 < 10) {
            sb.append("0").append(j3).append(":");
        } else {
            sb.append(j3).append(":");
        }
        if (j5 < 10) {
            sb.append("0").append(j5);
        } else {
            sb.append(j5);
        }
        return sb.toString();
    }

    public static String getCountTime(long j) {
        long j2;
        long j3;
        long j4 = j / 1000;
        if (3600 <= j4) {
            j2 = j4 / 3600;
            j4 -= 3600 * j2;
        } else {
            j2 = 0;
        }
        if (60 <= j4) {
            j3 = j4 / 60;
            j4 -= 60 * j3;
        } else {
            j3 = 0;
        }
        if (0 > j4) {
            j4 = 0;
        }
        StringBuilder sb = new StringBuilder();
        if (j2 > 0) {
            if (j2 < 10) {
                sb.append("0").append(j2).append(":");
            } else {
                sb.append(j2).append(":");
            }
        }
        if (j3 > 0) {
            if (j3 < 10) {
                sb.append("0").append(j3).append(":");
            } else {
                sb.append(j3).append(":");
            }
        }
        if (j4 < 10) {
            sb.append("0").append(j4);
        } else {
            sb.append(j4);
        }
        return sb.toString();
    }

    public static Object getCurrentPlayerFactory() {
        VideoPlayerConfig config = VideoViewManager.getConfig();
        try {
            Field declaredField = config.getClass().getDeclaredField("mPlayerFactory");
            declaredField.setAccessible(true);
            return declaredField.get(config);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void removeViewFormParent(View view) {
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof FrameLayout) {
            ((FrameLayout) parent).removeView(view);
        }
    }

    public static String playState2str(int i) {
        String str;
        switch (i) {
            case -1:
                str = IjkMediaPlayer.OnNativeInvokeListener.ARG_ERROR;
                break;
            case 0:
            default:
                str = "idle";
                break;
            case 1:
                str = "preparing";
                break;
            case 2:
                str = "prepared";
                break;
            case 3:
                str = "playing";
                break;
            case 4:
                str = "pause";
                break;
            case 5:
                str = "playback completed";
                break;
            case 6:
                str = "buffering";
                break;
            case 7:
                str = "buffered";
                break;
        }
        return String.format("playState: %s", str);
    }

    public static String playerState2str(int i) {
        return String.format("playerState: %s", i != 1002 ? i != 1003 ? "normal" : "tiny screen" : "full screen");
    }
}