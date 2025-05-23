package com.mylhyl.circledialog.res.drawable;

import android.R;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;

/* loaded from: classes.dex */
public class CircleDrawableSelector extends StateListDrawable {
    public CircleDrawableSelector(int i, int i2) {
        this(i, i2, 0, 0, 0, 0);
    }

    public CircleDrawableSelector(int i, int i2, int i3, int i4, int i5, int i6) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(DrawableHelper.getRoundRectShape(i3, i4, i5, i6));
        shapeDrawable.getPaint().setColor(i2);
        ShapeDrawable shapeDrawable2 = new ShapeDrawable(DrawableHelper.getRoundRectShape(i3, i4, i5, i6));
        shapeDrawable2.getPaint().setColor(i);
        addState(new int[]{R.attr.state_pressed}, shapeDrawable);
        addState(new int[]{-16842919}, shapeDrawable2);
    }
}