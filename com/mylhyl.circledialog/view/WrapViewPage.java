package com.mylhyl.circledialog.view;

import android.content.Context;
import android.view.View;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;

/* loaded from: classes.dex */
class WrapViewPage extends ViewPager {
    public WrapViewPage(Context context) {
        super(context);
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.View
    protected void onMeasure(int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < getChildCount(); i4++) {
            View childAt = getChildAt(i4);
            childAt.measure(i, View.MeasureSpec.makeMeasureSpec(0, 0));
            int measuredHeight = childAt.getMeasuredHeight();
            if (measuredHeight > i3) {
                i3 = measuredHeight;
            }
        }
        if (i3 > 0) {
            i2 = View.MeasureSpec.makeMeasureSpec(i3, C.BUFFER_FLAG_ENCRYPTED);
        }
        super.onMeasure(i, i2);
    }
}