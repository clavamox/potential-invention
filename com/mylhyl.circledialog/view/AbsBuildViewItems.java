package com.mylhyl.circledialog.view;

import android.content.Context;
import android.widget.LinearLayout;
import androidx.cardview.widget.CardView;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.ItemsView;

/* loaded from: classes.dex */
abstract class AbsBuildViewItems extends AbsBuildView {
    protected ItemsView mItemsView;

    public AbsBuildViewItems(Context context, CircleParams circleParams) {
        super(context, circleParams);
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public ItemsView getBodyView() {
        return this.mItemsView;
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void refreshContent() {
        ItemsView itemsView = this.mItemsView;
        if (itemsView != null) {
            itemsView.refreshItems();
        }
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public void buildRootView() {
        LinearLayout linearLayout = new LinearLayout(this.mContext);
        linearLayout.setOrientation(1);
        CardView createCardView = createCardView();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2, 1.0f);
        layoutParams.bottomMargin = Controller.dp2px(this.mContext, this.mParams.itemsParams.bottomMargin);
        createCardView.setLayoutParams(layoutParams);
        linearLayout.addView(createCardView);
        createCardView.addView(createLinearLayout());
        this.mRoot = linearLayout;
    }

    @Override // com.mylhyl.circledialog.view.AbsBuildView, com.mylhyl.circledialog.internal.BuildView
    public ButtonView buildButton() {
        ItemsButton itemsButton = new ItemsButton(this.mContext, this.mParams);
        this.mRoot.addView(itemsButton);
        return itemsButton;
    }
}