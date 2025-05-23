package androidx.core.graphics;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import androidx.core.util.Pair;

/* loaded from: classes.dex */
public final class PaintCompat {
    private static final String EM_STRING = "m";
    private static final String TOFU_STRING = "\udfffd";
    private static final ThreadLocal<Pair<Rect, Rect>> sRectThreadLocal = new ThreadLocal<>();

    public static boolean hasGlyph(Paint paint, String str) {
        return paint.hasGlyph(str);
    }

    public static boolean setBlendMode(Paint paint, BlendModeCompat blendModeCompat) {
        if (Build.VERSION.SDK_INT >= 29) {
            paint.setBlendMode(blendModeCompat != null ? BlendModeUtils.obtainBlendModeFromCompat(blendModeCompat) : null);
            return true;
        }
        if (blendModeCompat != null) {
            PorterDuff.Mode obtainPorterDuffFromCompat = BlendModeUtils.obtainPorterDuffFromCompat(blendModeCompat);
            paint.setXfermode(obtainPorterDuffFromCompat != null ? new PorterDuffXfermode(obtainPorterDuffFromCompat) : null);
            return obtainPorterDuffFromCompat != null;
        }
        paint.setXfermode(null);
        return true;
    }

    private static Pair<Rect, Rect> obtainEmptyRects() {
        ThreadLocal<Pair<Rect, Rect>> threadLocal = sRectThreadLocal;
        Pair<Rect, Rect> pair = threadLocal.get();
        if (pair == null) {
            Pair<Rect, Rect> pair2 = new Pair<>(new Rect(), new Rect());
            threadLocal.set(pair2);
            return pair2;
        }
        pair.first.setEmpty();
        pair.second.setEmpty();
        return pair;
    }

    private PaintCompat() {
    }
}