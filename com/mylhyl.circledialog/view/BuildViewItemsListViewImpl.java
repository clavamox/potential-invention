package com.mylhyl.circledialog.view;

import android.content.Context;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.CloseView;
import com.mylhyl.circledialog.view.listener.ItemsView;

/* loaded from: classes.dex */
public final class BuildViewItemsListViewImpl extends AbsBuildViewItems {
    @Override // com.mylhyl.circledialog.view.AbsBuildViewItems, com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ ButtonView buildButton() {
        return super.buildButton();
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ CloseView buildCloseImgView() {
        return super.buildCloseImgView();
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildViewItems, com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ void buildRootView() {
        super.buildRootView();
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ void buildTitleView() {
        super.buildTitleView();
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildViewItems, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ ItemsView getBodyView() {
        return super.getBodyView();
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildViewItems, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ void refreshContent() {
        super.refreshContent();
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public /* bridge */ /* synthetic */ void refreshTitle() {
        super.refreshTitle();
    }

    public BuildViewItemsListViewImpl(Context context, CircleParams circleParams) {
        super(context, circleParams);
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void buildBodyView() {
        buildRootView();
        buildTitleView();
        if (this.mItemsView != null) {
            return;
        }
        this.mItemsView = new BodyListView(this.mContext, this.mParams.dialogParams, this.mParams.itemsParams);
        addViewByBody(this.mItemsView.getView());
    }
}