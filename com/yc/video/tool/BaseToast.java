package com.yc.video.tool;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import com.yc.video.R;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public final class BaseToast {
    private static Context mApp;
    private static SoftReference<Toast> mToast;
    private static Toast toast;
    private static int toastBackColor;

    public static void init(Context context) {
        if (mApp == null) {
            mApp = context;
            toastBackColor = ViewCompat.MEASURED_STATE_MASK;
        }
    }

    public static void setToastBackColor(int i) {
        toastBackColor = i;
    }

    private BaseToast() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static void checkContext() {
        if (mApp == null) {
            throw new NullPointerException("ToastUtils context is not null，please first init");
        }
    }

    public static void showToast(String str) {
        checkMainThread();
        checkContext();
        if (!checkNull(mToast)) {
            mToast.get().cancel();
        }
        Toast makeText = Toast.makeText(mApp, "", 0);
        makeText.setText(str);
        makeText.show();
        mToast = new SoftReference<>(makeText);
    }

    public static void showRoundRectToast(CharSequence charSequence) {
        checkMainThread();
        checkContext();
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        new Builder(mApp).setDuration(0).setFill(false).setGravity(17).setOffset(0).setTitle(charSequence).setTextColor(-1).setBackgroundColor(toastBackColor).setRadius(dip2px(mApp, 10.0f)).setElevation(dip2px(mApp, 0.0f)).build().show();
    }

    public static void showRoundRectToast(CharSequence charSequence, CharSequence charSequence2) {
        checkMainThread();
        checkContext();
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        new Builder(mApp).setDuration(0).setFill(false).setGravity(17).setOffset(0).setDesc(charSequence2).setTitle(charSequence).setTextColor(-1).setBackgroundColor(toastBackColor).setRadius(dip2px(mApp, 10.0f)).setElevation(dip2px(mApp, 0.0f)).build().show();
    }

    public static void showRoundRectToast(int i) {
        checkMainThread();
        checkContext();
        if (i == 0) {
            return;
        }
        new Builder(mApp).setDuration(0).setFill(false).setGravity(17).setOffset(0).setLayout(i).build().show();
    }

    public static final class Builder {
        private Context context;
        private CharSequence desc;
        private int elevation;
        private boolean isFill;
        private int layout;
        private float radius;
        private CharSequence title;
        private int yOffset;
        private int gravity = 48;
        private int duration = 0;
        private int textColor = -1;
        private int backgroundColor = ViewCompat.MEASURED_STATE_MASK;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.title = charSequence;
            return this;
        }

        public Builder setDesc(CharSequence charSequence) {
            this.desc = charSequence;
            return this;
        }

        public Builder setGravity(int i) {
            this.gravity = i;
            return this;
        }

        public Builder setFill(boolean z) {
            this.isFill = z;
            return this;
        }

        public Builder setOffset(int i) {
            this.yOffset = i;
            return this;
        }

        public Builder setDuration(int i) {
            this.duration = i;
            return this;
        }

        public Builder setTextColor(int i) {
            this.textColor = i;
            return this;
        }

        public Builder setBackgroundColor(int i) {
            this.backgroundColor = i;
            return this;
        }

        public Builder setRadius(float f) {
            this.radius = f;
            return this;
        }

        public Builder setElevation(int i) {
            this.elevation = i;
            return this;
        }

        public Builder setLayout(int i) {
            this.layout = i;
            return this;
        }

        public Toast build() {
            if (!BaseToast.checkNull(BaseToast.mToast)) {
                ((Toast) BaseToast.mToast.get()).cancel();
            }
            Toast toast = new Toast(this.context);
            HookToast.hook(toast);
            if (this.isFill) {
                toast.setGravity(this.gravity | 7, 0, this.yOffset);
            } else {
                toast.setGravity(this.gravity, 0, this.yOffset);
            }
            toast.setDuration(this.duration);
            toast.setMargin(0.0f, 0.0f);
            if (this.layout == 0) {
                CardView cardView = (CardView) LayoutInflater.from(this.context).inflate(R.layout.custom_toast_view, (ViewGroup) null);
                TextView textView = (TextView) cardView.findViewById(R.id.toastTextView);
                TextView textView2 = (TextView) cardView.findViewById(R.id.desc);
                cardView.setCardElevation(this.elevation);
                cardView.setRadius(this.radius);
                cardView.setCardBackgroundColor(this.backgroundColor);
                textView.setTextColor(this.textColor);
                textView.setText(this.title);
                if (TextUtils.isEmpty(this.desc)) {
                    textView2.setVisibility(8);
                } else {
                    textView2.setText(this.desc);
                    textView2.setVisibility(0);
                }
                toast.setView(cardView);
            } else {
                toast.setView(LayoutInflater.from(this.context).inflate(this.layout, (ViewGroup) null));
            }
            BaseToast.mToast = new SoftReference(toast);
            return toast;
        }
    }

    public static boolean checkNull(SoftReference softReference) {
        return softReference == null || softReference.get() == null;
    }

    public static void checkMainThread() {
        if (!isMainThread()) {
            throw new IllegalStateException("请不要在子线程中做弹窗操作");
        }
    }

    private static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static int dip2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static class HookToast {
        private static Field sField_TN;
        private static Field sField_TN_Handler;

        static {
            try {
                Field declaredField = Toast.class.getDeclaredField("mTN");
                sField_TN = declaredField;
                declaredField.setAccessible(true);
                Field declaredField2 = sField_TN.getType().getDeclaredField("mHandler");
                sField_TN_Handler = declaredField2;
                declaredField2.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void hook(Toast toast) {
            try {
                Object obj = sField_TN.get(toast);
                sField_TN_Handler.set(obj, new SafelyHandler((Handler) sField_TN_Handler.get(obj)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static class SafelyHandler extends Handler {
            private Handler impl;

            public SafelyHandler(Handler handler) {
                this.impl = handler;
            }

            @Override // android.os.Handler
            public void dispatchMessage(Message message) {
                try {
                    super.dispatchMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                this.impl.handleMessage(message);
            }
        }
    }
}