package cn.bingoogolapple.photopicker.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/* loaded from: classes.dex */
public class BGAHeightWrapGridView extends GridView {
    public BGAHeightWrapGridView(Context context) {
        this(context, null);
    }

    public BGAHeightWrapGridView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.gridViewStyle);
    }

    public BGAHeightWrapGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setSelector(R.color.transparent);
        setOverScrollMode(2);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }

    @Override // android.widget.GridView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(536870911, Integer.MIN_VALUE));
    }
}