package com.mylhyl.circledialog.view;

import android.content.Context;
import android.view.View;
import com.mylhyl.circledialog.internal.BackgroundHelper;
import com.mylhyl.circledialog.internal.CircleParams;

/* loaded from: classes.dex */
final class ItemsButton extends AbsButton {
    public ItemsButton(Context context, CircleParams circleParams) {
        super(context, circleParams);
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0024  */
    @Override // com.mylhyl.circledialog.view.AbsButton
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void initView() {
        /*
            r4 = this;
            r0 = 0
            r4.setOrientation(r0)
            android.widget.LinearLayout$LayoutParams r1 = new android.widget.LinearLayout$LayoutParams
            r2 = -1
            r3 = -2
            r1.<init>(r2, r3)
            com.mylhyl.circledialog.params.ButtonParams r2 = r4.mNegativeParams
            if (r2 != 0) goto L1e
            com.mylhyl.circledialog.params.ButtonParams r2 = r4.mNeutralParams
            if (r2 != 0) goto L1b
            com.mylhyl.circledialog.params.ButtonParams r2 = r4.mPositiveParams
            if (r2 != 0) goto L18
            goto L22
        L18:
            com.mylhyl.circledialog.params.ButtonParams r0 = r4.mPositiveParams
            goto L20
        L1b:
            com.mylhyl.circledialog.params.ButtonParams r0 = r4.mNeutralParams
            goto L20
        L1e:
            com.mylhyl.circledialog.params.ButtonParams r0 = r4.mNegativeParams
        L20:
            int r0 = r0.topMargin
        L22:
            if (r0 <= 0) goto L2f
            android.content.Context r2 = r4.getContext()
            float r0 = (float) r0
            int r0 = com.mylhyl.circledialog.internal.Controller.dp2px(r2, r0)
            r1.topMargin = r0
        L2f:
            r4.setLayoutParams(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mylhyl.circledialog.view.ItemsButton.initView():void");
    }

    @Override // com.mylhyl.circledialog.view.AbsButton
    protected void setNegativeButtonBackground(View view, int i, CircleParams circleParams) {
        BackgroundHelper.handleItemsNegativeButtonBackground(view, i, circleParams);
    }

    @Override // com.mylhyl.circledialog.view.AbsButton
    protected void setNeutralButtonBackground(View view, int i, CircleParams circleParams) {
        BackgroundHelper.handleItemsNeutralButtonBackground(view, i, circleParams);
    }

    @Override // com.mylhyl.circledialog.view.AbsButton
    protected void setPositiveButtonBackground(View view, int i, CircleParams circleParams) {
        BackgroundHelper.handleItemsPositiveButtonBackground(view, i, circleParams);
    }
}