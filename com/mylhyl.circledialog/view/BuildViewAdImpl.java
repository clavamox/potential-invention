package com.mylhyl.circledialog.view;

import android.R;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.CloseView;

/* loaded from: classes.dex */
public final class BuildViewAdImpl extends AbsBuildView {
    private BodyAdView mBodyAdView;

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void refreshContent() {
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ ButtonView buildButton() {
        return super.buildButton();
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ CloseView buildCloseImgView() {
        return super.buildCloseImgView();
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ void buildTitleView() {
        super.buildTitleView();
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ void refreshTitle() {
        super.refreshTitle();
    }

    public BuildViewAdImpl(Context context, CircleParams circleParams) {
        super(context, circleParams);
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public void buildRootView() {
        this.mRoot = createLinearLayout();
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void buildBodyView() {
        buildRootView();
        buildTitleView();
        if (this.mBodyAdView == null) {
            BodyAdView bodyAdView = new BodyAdView(this.mContext, this.mParams);
            this.mBodyAdView = bodyAdView;
            addViewByBody(bodyAdView);
        }
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public BodyAdView getBodyView() {
        return this.mBodyAdView;
    }

    public static class SelectorPointDrawable extends StateListDrawable {
        public SelectorPointDrawable(int i, int i2) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(1);
            gradientDrawable.setColor(i);
            gradientDrawable.setSize(i2, i2);
            GradientDrawable gradientDrawable2 = new GradientDrawable();
            gradientDrawable2.setShape(1);
            gradientDrawable2.setStroke(2, i);
            gradientDrawable2.setSize(i2, i2);
            addState(new int[]{R.attr.state_selected}, gradientDrawable);
            addState(new int[]{-16842913}, gradientDrawable2);
        }
    }
}