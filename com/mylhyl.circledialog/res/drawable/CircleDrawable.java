package com.mylhyl.circledialog.res.drawable;

import android.graphics.drawable.ShapeDrawable;

/* loaded from: classes.dex */
public class CircleDrawable extends ShapeDrawable {
    public CircleDrawable(int i, int i2) {
        this(i, i2, i2, i2, i2);
    }

    public CircleDrawable(int i, int i2, int i3, int i4, int i5) {
        getPaint().setColor(i);
        setShape(DrawableHelper.getRoundRectShape(i2, i3, i4, i5));
    }
}