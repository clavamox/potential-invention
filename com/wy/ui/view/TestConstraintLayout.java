package com.wy.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;

/* loaded from: classes.dex */
public class TestConstraintLayout extends ConstraintLayout {
    public TestConstraintLayout(Context context) {
        super(context);
    }

    public TestConstraintLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TestConstraintLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // androidx.constraintlayout.widget.ConstraintLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        Log.d("TestConstraintLayout", "onMeasure....");
    }

    @Override // androidx.constraintlayout.widget.ConstraintLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        Log.d("TestConstraintLayout", "onLayout....");
    }
}