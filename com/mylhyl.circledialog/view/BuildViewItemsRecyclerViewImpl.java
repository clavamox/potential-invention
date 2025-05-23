package com.mylhyl.circledialog.view;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.params.ItemsParams;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.CloseView;
import com.mylhyl.circledialog.view.listener.ItemsView;

/* loaded from: classes.dex */
public final class BuildViewItemsRecyclerViewImpl extends AbsBuildViewItems {
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

    public BuildViewItemsRecyclerViewImpl(Context context, CircleParams circleParams) {
        super(context, circleParams);
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void buildBodyView() {
        buildRootView();
        buildTitleView();
        if (this.mItemsView != null) {
            return;
        }
        ItemsParams itemsParams = this.mParams.itemsParams;
        this.mItemsView = new BodyRecyclerView(this.mContext, itemsParams, this.mParams.dialogParams);
        RecyclerView.LayoutManager layoutManager = itemsParams.layoutManager;
        int i = itemsParams.dividerHeight;
        if (itemsParams.itemDecoration != null && i > 0 && layoutManager != null && (layoutManager instanceof LinearLayoutManager) && ((LinearLayoutManager) layoutManager).getOrientation() == 0) {
            addViewByBody(new DividerView(this.mContext, 0, i));
        }
        addViewByBody(this.mItemsView.getView());
    }
}