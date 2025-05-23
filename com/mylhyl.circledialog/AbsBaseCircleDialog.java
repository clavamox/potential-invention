package com.mylhyl.circledialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.mylhyl.circledialog.internal.BackgroundHelper;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.res.drawable.CircleDrawable;
import com.mylhyl.circledialog.res.values.CircleDimen;

/* loaded from: classes.dex */
public abstract class AbsBaseCircleDialog extends DialogFragment {
    private static final String SAVED_ALPHA = "circle:baseAlpha";
    private static final String SAVED_ANIM_STYLE = "circle:baseAnimStyle";
    private static final String SAVED_BACKGROUND_COLOR = "circle:baseBackgroundColor";
    private static final String SAVED_DIM_AMOUNT = "circle:baseDimAmount";
    private static final String SAVED_DIM_ENABLED = "circle:baseDimEnabled";
    private static final String SAVED_GRAVITY = "circle:baseGravity";
    private static final String SAVED_HEIGHT_MAX = "circle:baseMaxHeight";
    private static final String SAVED_PADDING = "circle:basePadding";
    private static final String SAVED_RADIUS = "circle:baseRadius";
    private static final String SAVED_TOUCH_OUT = "circle:baseTouchOut";
    private static final String SAVED_WIDTH = "circle:baseWidth";
    private static final String SAVED_X = "circle:baseX";
    private static final String SAVED_Y = "circle:baseY";
    private int mAnimStyle;
    private float mMaxHeight;
    private int[] mPadding;
    private SystemBarConfig mSystemBarConfig;
    private int mSystemUiVisibility;
    private int mX;
    private int mY;
    private int mGravity = 17;
    private boolean mCanceledOnTouchOutside = true;
    private float mWidth = CircleDimen.DIALOG_WIDTH;
    private boolean isDimEnabled = true;
    private float mDimAmount = CircleDimen.DIM_AMOUNT;
    private int mBackgroundColor = 0;
    private int mRadius = CircleDimen.DIALOG_RADIUS;
    private float mAlpha = CircleDimen.DIALOG_ALPHA;

    public abstract View createView(Context context, LayoutInflater layoutInflater, ViewGroup viewGroup);

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSystemBarConfig = new SystemBarConfig(getActivity());
        setStyle(1, 0);
        if (bundle != null) {
            this.mGravity = bundle.getInt(SAVED_GRAVITY);
            this.mCanceledOnTouchOutside = bundle.getBoolean(SAVED_TOUCH_OUT);
            this.mWidth = bundle.getFloat(SAVED_WIDTH);
            this.mMaxHeight = bundle.getFloat(SAVED_HEIGHT_MAX);
            this.mPadding = bundle.getIntArray(SAVED_PADDING);
            this.mAnimStyle = bundle.getInt(SAVED_ANIM_STYLE);
            this.isDimEnabled = bundle.getBoolean(SAVED_DIM_ENABLED);
            this.mDimAmount = bundle.getFloat(SAVED_DIM_AMOUNT);
            this.mBackgroundColor = bundle.getInt(SAVED_BACKGROUND_COLOR);
            this.mRadius = bundle.getInt(SAVED_RADIUS);
            this.mAlpha = bundle.getFloat(SAVED_ALPHA);
            this.mX = bundle.getInt(SAVED_X);
            this.mY = bundle.getInt(SAVED_Y);
        }
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        remove();
        this.mSystemBarConfig = null;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        if (getView() != null && this.mMaxHeight > 0.0f) {
            getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.mylhyl.circledialog.AbsBaseCircleDialog.1
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    int height = AbsBaseCircleDialog.this.getView().getHeight();
                    int screenHeight = (int) (AbsBaseCircleDialog.this.mSystemBarConfig.getScreenHeight() * AbsBaseCircleDialog.this.mMaxHeight);
                    if (height > screenHeight) {
                        AbsBaseCircleDialog.this.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        AbsBaseCircleDialog.this.getView().setLayoutParams(new FrameLayout.LayoutParams(-1, screenHeight));
                    }
                }
            });
        }
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(this.mCanceledOnTouchOutside);
            setDialogGravity(dialog);
            if (this.mSystemUiVisibility != 0) {
                dialog.getWindow().setFlags(8, 8);
            }
        }
        super.onStart();
        if (dialog == null || this.mSystemUiVisibility == 0) {
            return;
        }
        dialog.getWindow().getDecorView().setSystemUiVisibility(this.mSystemUiVisibility);
        dialog.getWindow().clearFlags(8);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(SAVED_GRAVITY, this.mGravity);
        bundle.putBoolean(SAVED_TOUCH_OUT, this.mCanceledOnTouchOutside);
        bundle.putFloat(SAVED_WIDTH, this.mWidth);
        bundle.putFloat(SAVED_HEIGHT_MAX, this.mMaxHeight);
        int[] iArr = this.mPadding;
        if (iArr != null) {
            bundle.putIntArray(SAVED_PADDING, iArr);
        }
        bundle.putInt(SAVED_ANIM_STYLE, this.mAnimStyle);
        bundle.putBoolean(SAVED_DIM_ENABLED, this.isDimEnabled);
        bundle.putFloat(SAVED_DIM_AMOUNT, this.mDimAmount);
        bundle.putInt(SAVED_BACKGROUND_COLOR, this.mBackgroundColor);
        bundle.putInt(SAVED_RADIUS, this.mRadius);
        bundle.putFloat(SAVED_ALPHA, this.mAlpha);
        bundle.putInt(SAVED_X, this.mX);
        bundle.putInt(SAVED_Y, this.mY);
    }

    public void remove() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        beginTransaction.remove(this);
        beginTransaction.addToBackStack(null);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return createView(getContext(), layoutInflater, viewGroup);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        BackgroundHelper.handleBackground(view, new CircleDrawable(this.mBackgroundColor, Controller.dp2px(getContext(), this.mRadius)));
        view.setAlpha(this.mAlpha);
    }

    protected void setGravity(int i) {
        this.mGravity = i;
    }

    protected void setCanceledOnTouchOutside(boolean z) {
        this.mCanceledOnTouchOutside = z;
    }

    protected void setWidth(float f) {
        this.mWidth = f;
    }

    protected void setMaxHeight(float f) {
        this.mMaxHeight = f;
    }

    protected void setPadding(int i, int i2, int i3, int i4) {
        this.mPadding = new int[]{i, i2, i3, i4};
    }

    protected void setAnimations(int i) {
        this.mAnimStyle = i;
    }

    protected void setDimEnabled(boolean z) {
        this.isDimEnabled = z;
    }

    protected void setDimAmount(float f) {
        this.mDimAmount = f;
    }

    protected void setBackgroundColor(int i) {
        this.mBackgroundColor = i;
    }

    protected void setRadius(int i) {
        this.mRadius = i;
    }

    protected void setAlpha(float f) {
        this.mAlpha = f;
    }

    protected void setX(int i) {
        this.mX = i;
    }

    protected void setY(int i) {
        this.mY = i;
    }

    protected void bottomFull() {
        this.mGravity = 80;
        this.mRadius = 0;
        this.mWidth = 1.0f;
        this.mY = 0;
    }

    protected void setSystemUiVisibility(int i) {
        this.mSystemUiVisibility = i;
    }

    protected void setSoftInputMode() {
        getDialog().getWindow().setSoftInputMode(20);
    }

    private void setDialogGravity(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams attributes = window.getAttributes();
        int screenWidth = this.mSystemBarConfig.getScreenWidth();
        float f = this.mWidth;
        if (f > 0.0f && f <= 1.0f) {
            attributes.width = (int) (screenWidth * f);
        } else {
            attributes.width = (int) f;
        }
        attributes.gravity = this.mGravity;
        attributes.x = this.mX;
        attributes.y = this.mY;
        int[] iArr = this.mPadding;
        if (iArr != null) {
            attributes.width = -1;
            window.getDecorView().setPadding(iArr[0], iArr[1], iArr[2], iArr[3]);
        }
        attributes.dimAmount = this.mDimAmount;
        window.setAttributes(attributes);
        int i = this.mAnimStyle;
        if (i != 0) {
            window.setWindowAnimations(i);
        }
        if (this.isDimEnabled) {
            window.addFlags(2);
        } else {
            window.clearFlags(2);
        }
    }
}