package com.yc.video.ui.pip;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.yc.video.R;
import com.yc.video.tool.PlayerUtils;

/* loaded from: classes.dex */
public class FloatVideoView extends FrameLayout {
    private int mDownRawX;
    private int mDownRawY;
    private int mDownX;
    private int mDownY;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;

    public FloatVideoView(Context context, int i, int i2) {
        super(context);
        this.mDownX = i;
        this.mDownY = i2;
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.shape_float_window_bg);
        int dp2px = PlayerUtils.dp2px(getContext(), 1.0f);
        setPadding(dp2px, dp2px, dp2px, dp2px);
        initWindow();
    }

    private void initWindow() {
        this.mWindowManager = PlayerUtils.getWindowManager(getContext().getApplicationContext());
        this.mParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= 26) {
            this.mParams.type = 2038;
        } else {
            this.mParams.type = 2003;
        }
        this.mParams.format = -3;
        this.mParams.flags = 8;
        this.mParams.windowAnimations = R.style.FloatWindowAnimation;
        this.mParams.gravity = BadgeDrawable.TOP_START;
        int dp2px = PlayerUtils.dp2px(getContext(), 250.0f);
        this.mParams.width = dp2px;
        this.mParams.height = (dp2px * 9) / 16;
        this.mParams.x = this.mDownX;
        this.mParams.y = this.mDownY;
    }

    public boolean addToWindow() {
        if (this.mWindowManager == null || isAttachedToWindow()) {
            return false;
        }
        this.mWindowManager.addView(this, this.mParams);
        return true;
    }

    public boolean removeFromWindow() {
        if (this.mWindowManager == null || !isAttachedToWindow()) {
            return false;
        }
        this.mWindowManager.removeViewImmediate(this);
        return true;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action != 2) {
                return false;
            }
            return Math.abs(motionEvent.getRawX() - ((float) this.mDownRawX)) > ((float) ViewConfiguration.get(getContext()).getScaledTouchSlop()) || Math.abs(motionEvent.getRawY() - ((float) this.mDownRawY)) > ((float) ViewConfiguration.get(getContext()).getScaledTouchSlop());
        }
        this.mDownRawX = (int) motionEvent.getRawX();
        this.mDownRawY = (int) motionEvent.getRawY();
        this.mDownX = (int) motionEvent.getX();
        this.mDownY = (int) (motionEvent.getY() + PlayerUtils.getStatusBarHeight(getContext()));
        return false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 2) {
            int rawX = (int) motionEvent.getRawX();
            int rawY = (int) motionEvent.getRawY();
            this.mParams.x = rawX - this.mDownX;
            this.mParams.y = rawY - this.mDownY;
            this.mWindowManager.updateViewLayout(this, this.mParams);
        }
        return super.onTouchEvent(motionEvent);
    }
}