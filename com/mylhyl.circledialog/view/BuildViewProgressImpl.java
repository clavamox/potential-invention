package com.mylhyl.circledialog.view;

import android.content.Context;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.CloseView;

/* loaded from: classes.dex */
public final class BuildViewProgressImpl extends AbsBuildView {
    private BodyProgressView mBodyProgressView;

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

    public BuildViewProgressImpl(Context context, CircleParams circleParams) {
        super(context, circleParams);
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void buildBodyView() {
        buildRootView();
        buildTitleView();
        if (this.mBodyProgressView == null) {
            BodyProgressView bodyProgressView = new BodyProgressView(this.mContext, this.mParams);
            this.mBodyProgressView = bodyProgressView;
            addViewByBody(bodyProgressView);
        }
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public BodyProgressView getBodyView() {
        return this.mBodyProgressView;
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void refreshContent() {
        BodyProgressView bodyProgressView = this.mBodyProgressView;
        if (bodyProgressView != null) {
            bodyProgressView.refreshProgress();
        }
    }
}