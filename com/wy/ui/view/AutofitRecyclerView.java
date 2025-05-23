package com.wy.ui.view;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/* loaded from: classes.dex */
public class AutofitRecyclerView extends RecyclerView {
    private int columnWidth;
    private GridLayoutManager manager;

    public AutofitRecyclerView(Context context) {
        super(context);
        this.columnWidth = -1;
        init(context, null);
    }

    public AutofitRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.columnWidth = -1;
        init(context, attributeSet);
    }

    public AutofitRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.columnWidth = -1;
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{R.attr.columnWidth});
            this.columnWidth = obtainStyledAttributes.getDimensionPixelSize(0, -1);
            obtainStyledAttributes.recycle();
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        this.manager = gridLayoutManager;
        setLayoutManager(gridLayoutManager);
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.columnWidth > 0) {
            this.manager.setSpanCount(2);
        }
        this.manager.setSpanCount(2);
    }
}