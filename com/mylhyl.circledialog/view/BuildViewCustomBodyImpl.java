package com.mylhyl.circledialog.view;

import android.content.Context;
import android.view.View;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.CloseView;

/* loaded from: classes.dex */
public final class BuildViewCustomBodyImpl extends AbsBuildView {
    private View mCustomBodyView;

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
    public /* bridge */ /* synthetic */ void buildRootView() {
        super.buildRootView();
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ void buildTitleView() {
        super.buildTitleView();
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ void refreshTitle() {
        super.refreshTitle();
    }

    public BuildViewCustomBodyImpl(Context context, CircleParams circleParams) {
        super(context, circleParams);
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void buildBodyView() {
        buildRootView();
        buildTitleView();
        if (this.mCustomBodyView != null) {
            return;
        }
        if (this.mParams.bodyViewId != 0) {
            this.mCustomBodyView = layoutInflaterFrom(this.mParams.bodyViewId);
        } else {
            this.mCustomBodyView = this.mParams.bodyView;
        }
        View view = this.mCustomBodyView;
        if (view != null) {
            addViewByBody(view);
        }
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public View getBodyView() {
        return this.mCustomBodyView;
    }
}