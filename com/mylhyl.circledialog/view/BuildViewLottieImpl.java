package com.mylhyl.circledialog.view;

import android.content.Context;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.CloseView;

/* loaded from: classes.dex */
public final class BuildViewLottieImpl extends AbsBuildView {
    private BodyLottieView mBodyLottieView;

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

    public BuildViewLottieImpl(Context context, CircleParams circleParams) {
        super(context, circleParams);
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void buildBodyView() {
        buildRootView();
        buildTitleView();
        if (this.mBodyLottieView != null) {
            return;
        }
        BodyLottieView bodyLottieView = new BodyLottieView(this.mContext, this.mParams);
        this.mBodyLottieView = bodyLottieView;
        addViewByBody(bodyLottieView);
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public BodyLottieView getBodyView() {
        return this.mBodyLottieView;
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void refreshContent() {
        BodyLottieView bodyLottieView = this.mBodyLottieView;
        if (bodyLottieView != null) {
            bodyLottieView.refreshText();
        }
    }
}