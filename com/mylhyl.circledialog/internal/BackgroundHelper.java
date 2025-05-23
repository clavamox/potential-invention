package com.mylhyl.circledialog.internal;

import android.graphics.drawable.Drawable;
import android.view.View;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.TitleParams;
import com.mylhyl.circledialog.res.drawable.CircleDrawable;
import com.mylhyl.circledialog.res.drawable.CircleDrawableSelector;

/* loaded from: classes.dex */
public class BackgroundHelper {
    public static void handleTitleBackground(View view, int i, DialogParams dialogParams) {
        if (Controller.SDK_LOLLIPOP) {
            view.setBackgroundColor(i);
        } else {
            int dp2px = Controller.dp2px(view.getContext(), dialogParams.radius);
            handleBackground(view, new CircleDrawable(i, dp2px, dp2px, 0, 0));
        }
    }

    public static void handleBodyBackground(View view, int i, CircleParams circleParams) {
        TitleParams titleParams = circleParams.titleParams;
        ButtonParams buttonParams = circleParams.negativeParams;
        ButtonParams buttonParams2 = circleParams.positiveParams;
        ButtonParams buttonParams3 = circleParams.neutralParams;
        int dp2px = Controller.dp2px(view.getContext(), circleParams.dialogParams.radius);
        if (Controller.SDK_LOLLIPOP) {
            view.setBackgroundColor(i);
            return;
        }
        if (titleParams != null && buttonParams == null && buttonParams2 == null && buttonParams3 == null) {
            handleBackground(view, new CircleDrawable(i, 0, 0, dp2px, dp2px));
            return;
        }
        if (titleParams == null && (buttonParams != null || buttonParams2 != null || buttonParams3 != null)) {
            handleBackground(view, new CircleDrawable(i, dp2px, dp2px, 0, 0));
            return;
        }
        if (titleParams == null && buttonParams == null && buttonParams2 == null && buttonParams3 == null) {
            handleBackground(view, new CircleDrawable(i, dp2px));
        } else {
            view.setBackgroundColor(i);
        }
    }

    public static void handleNegativeButtonBackground(View view, int i, CircleParams circleParams) {
        CircleDrawableSelector circleDrawableSelector;
        DialogParams dialogParams = circleParams.dialogParams;
        ButtonParams buttonParams = circleParams.negativeParams;
        if (Controller.SDK_LOLLIPOP) {
            circleDrawableSelector = new CircleDrawableSelector(i, buttonParams.backgroundColorPress != 0 ? buttonParams.backgroundColorPress : dialogParams.backgroundColorPress);
        } else {
            int dp2px = Controller.dp2px(view.getContext(), dialogParams.radius);
            circleDrawableSelector = new CircleDrawableSelector(i, buttonParams.backgroundColorPress != 0 ? buttonParams.backgroundColorPress : dialogParams.backgroundColorPress, 0, 0, (circleParams.neutralParams == null && circleParams.positiveParams == null) ? dp2px : 0, dp2px);
        }
        handleBackground(view, circleDrawableSelector);
    }

    public static void handlePositiveButtonBackground(View view, int i, CircleParams circleParams) {
        CircleDrawableSelector circleDrawableSelector;
        DialogParams dialogParams = circleParams.dialogParams;
        ButtonParams buttonParams = circleParams.positiveParams;
        if (Controller.SDK_LOLLIPOP) {
            circleDrawableSelector = new CircleDrawableSelector(i, buttonParams.backgroundColorPress != 0 ? buttonParams.backgroundColorPress : dialogParams.backgroundColorPress);
        } else {
            int dp2px = Controller.dp2px(view.getContext(), dialogParams.radius);
            circleDrawableSelector = new CircleDrawableSelector(i, buttonParams.backgroundColorPress != 0 ? buttonParams.backgroundColorPress : dialogParams.backgroundColorPress, 0, 0, dp2px, (circleParams.negativeParams == null && circleParams.neutralParams == null) ? dp2px : 0);
        }
        handleBackground(view, circleDrawableSelector);
    }

    public static void handleNeutralButtonBackground(View view, int i, CircleParams circleParams) {
        CircleDrawableSelector circleDrawableSelector;
        DialogParams dialogParams = circleParams.dialogParams;
        ButtonParams buttonParams = circleParams.neutralParams;
        if (Controller.SDK_LOLLIPOP) {
            circleDrawableSelector = new CircleDrawableSelector(i, buttonParams.backgroundColorPress != 0 ? buttonParams.backgroundColorPress : dialogParams.backgroundColorPress);
        } else {
            int dp2px = Controller.dp2px(view.getContext(), dialogParams.radius);
            circleDrawableSelector = new CircleDrawableSelector(i, buttonParams.backgroundColorPress != 0 ? buttonParams.backgroundColorPress : dialogParams.backgroundColorPress, 0, 0, circleParams.positiveParams == null ? dp2px : 0, circleParams.negativeParams == null ? dp2px : 0);
        }
        handleBackground(view, circleDrawableSelector);
    }

    public static void handleItemsNegativeButtonBackground(View view, int i, CircleParams circleParams) {
        DialogParams dialogParams = circleParams.dialogParams;
        ButtonParams buttonParams = circleParams.negativeParams;
        int dp2px = Controller.dp2px(view.getContext(), dialogParams.radius);
        int i2 = (circleParams.neutralParams == null && circleParams.positiveParams == null) ? dp2px : 0;
        handleBackground(view, new CircleDrawableSelector(i, buttonParams.backgroundColorPress != 0 ? buttonParams.backgroundColorPress : dialogParams.backgroundColorPress, dp2px, i2, i2, dp2px));
    }

    public static void handleItemsPositiveButtonBackground(View view, int i, CircleParams circleParams) {
        DialogParams dialogParams = circleParams.dialogParams;
        ButtonParams buttonParams = circleParams.positiveParams;
        int dp2px = Controller.dp2px(view.getContext(), dialogParams.radius);
        int i2 = (circleParams.negativeParams == null && circleParams.neutralParams == null) ? dp2px : 0;
        handleBackground(view, new CircleDrawableSelector(i, buttonParams.backgroundColorPress != 0 ? buttonParams.backgroundColorPress : dialogParams.backgroundColorPress, i2, dp2px, dp2px, i2));
    }

    public static void handleItemsNeutralButtonBackground(View view, int i, CircleParams circleParams) {
        DialogParams dialogParams = circleParams.dialogParams;
        ButtonParams buttonParams = circleParams.neutralParams;
        int dp2px = Controller.dp2px(view.getContext(), dialogParams.radius);
        int i2 = circleParams.negativeParams == null ? dp2px : 0;
        int i3 = circleParams.positiveParams == null ? dp2px : 0;
        handleBackground(view, new CircleDrawableSelector(i, buttonParams.backgroundColorPress != 0 ? buttonParams.backgroundColorPress : dialogParams.backgroundColorPress, i2, i3, i3, i2));
    }

    public static void handleBackground(View view, Drawable drawable) {
        if (Controller.SDK_JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void handleCircleBackground(View view, int i, DialogParams dialogParams) {
        int dp2px = Controller.dp2px(view.getContext(), dialogParams.radius);
        CircleDrawable circleDrawable = new CircleDrawable(i, dp2px, dp2px, dp2px, dp2px);
        if (Controller.SDK_JELLY_BEAN) {
            view.setBackground(circleDrawable);
        } else {
            view.setBackgroundDrawable(circleDrawable);
        }
    }
}