package com.mylhyl.circledialog.view;

import android.content.Context;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.CloseView;

/* loaded from: classes.dex */
public final class BuildViewConfirmImpl extends AbsBuildView {
    private BodyTextView mBodyTextView;

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

    public BuildViewConfirmImpl(Context context, CircleParams circleParams) {
        super(context, circleParams);
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void buildBodyView() {
        buildRootView();
        buildTitleView();
        if (this.mBodyTextView == null) {
            BodyTextView bodyTextView = new BodyTextView(this.mContext, this.mParams);
            this.mBodyTextView = bodyTextView;
            addViewByBody(bodyTextView);
        }
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public BodyTextView getBodyView() {
        return this.mBodyTextView;
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void refreshContent() {
        BodyTextView bodyTextView = this.mBodyTextView;
        if (bodyTextView != null) {
            bodyTextView.refreshText();
        }
    }
}