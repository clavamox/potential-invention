package cn.bingoogolapple.photopicker.util;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;
import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: classes.dex */
public class BGAPhotoPickerUtil {
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private BGAPhotoPickerUtil() {
    }

    public static void runInThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static void runInUIThread(Runnable runnable) {
        sHandler.post(runnable);
    }

    public static void runInUIThread(Runnable runnable, long j) {
        sHandler.postDelayed(runnable, j);
    }

    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) BGABaseAdapterUtil.getApp().getSystemService("window");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        WindowManager windowManager = (WindowManager) BGABaseAdapterUtil.getApp().getSystemService("window");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static String md5(String... strArr) {
        if (strArr == null || strArr.length == 0) {
            throw new RuntimeException("请输入需要加密的字符串!");
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            boolean z = true;
            for (String str : strArr) {
                if (!TextUtils.isEmpty(str)) {
                    messageDigest.update(str.getBytes());
                    z = false;
                }
            }
            if (z) {
                throw new RuntimeException("请输入需要加密的字符串!");
            }
            return new BigInteger(1, messageDigest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void show(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        if (charSequence.length() < 10) {
            Toast.makeText(BGABaseAdapterUtil.getApp(), charSequence, 0).show();
        } else {
            Toast.makeText(BGABaseAdapterUtil.getApp(), charSequence, 1).show();
        }
    }

    public static void show(int i) {
        show(BGABaseAdapterUtil.getApp().getString(i));
    }

    public static void showSafe(final CharSequence charSequence) {
        runInUIThread(new Runnable() { // from class: cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil.1
            @Override // java.lang.Runnable
            public void run() {
                BGAPhotoPickerUtil.show(charSequence);
            }
        });
    }

    public static void showSafe(int i) {
        showSafe(BGABaseAdapterUtil.getApp().getString(i));
    }
}