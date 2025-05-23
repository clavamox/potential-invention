package com.mylhyl.circledialog.view;

import android.content.Context;
import android.view.View;
import com.mylhyl.circledialog.internal.BackgroundHelper;
import com.mylhyl.circledialog.internal.CircleParams;

/* loaded from: classes.dex */
final class ConfirmButton extends AbsButton {
    public ConfirmButton(Context context, CircleParams circleParams) {
        super(context, circleParams);
    }

    @Override // com.mylhyl.circledialog.view.AbsButton
    protected void initView() {
        setOrientation(0);
    }

    @Override // com.mylhyl.circledialog.view.AbsButton
    protected void setNegativeButtonBackground(View view, int i, CircleParams circleParams) {
        BackgroundHelper.handleNegativeButtonBackground(view, i, circleParams);
    }

    @Override // com.mylhyl.circledialog.view.AbsButton
    protected void setNeutralButtonBackground(View view, int i, CircleParams circleParams) {
        BackgroundHelper.handleNeutralButtonBackground(view, i, circleParams);
    }

    @Override // com.mylhyl.circledialog.view.AbsButton
    protected void setPositiveButtonBackground(View view, int i, CircleParams circleParams) {
        BackgroundHelper.handlePositiveButtonBackground(view, i, circleParams);
    }
}