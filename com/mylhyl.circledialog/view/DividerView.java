package com.mylhyl.circledialog.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import com.mylhyl.circledialog.res.values.CircleColor;

/* loaded from: classes.dex */
final class DividerView extends View {
    static final int DEFAULT_ORIENTATION = 1;
    private int mOrientation;

    public DividerView(Context context) {
        this(context, 1);
    }

    public DividerView(Context context, int i) {
        this(context, i, 1);
    }

    public DividerView(Context context, int i, int i2) {
        super(context);
        this.mOrientation = i;
        setBackgroundColor(CircleColor.divider);
        if (this.mOrientation == 1) {
            setLayoutParams(new LinearLayout.LayoutParams(i2, -1));
        } else {
            setLayoutParams(new LinearLayout.LayoutParams(-1, i2));
        }
    }

    public void setBgColor(int i) {
        setBackgroundColor(i);
    }
}