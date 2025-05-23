package com.mylhyl.circledialog.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import androidx.cardview.widget.CardView;
import com.mylhyl.circledialog.internal.BuildView;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.params.CloseParams;
import com.mylhyl.circledialog.view.listener.ButtonView;
import com.mylhyl.circledialog.view.listener.CloseView;

/* loaded from: classes.dex */
abstract class AbsBuildView implements BuildView {
    private static final double COS_45 = Math.cos(Math.toRadians(45.0d));
    private ButtonView mButtonView;
    protected Context mContext;
    protected CircleParams mParams;
    protected ViewGroup mRoot;
    private LinearLayout mRootCardViewByLinearLayout;
    private TitleView mTitleView;

    public AbsBuildView(Context context, CircleParams circleParams) {
        this.mContext = context;
        this.mParams = circleParams;
    }

    protected final View layoutInflaterFrom(int i) {
        return LayoutInflater.from(this.mContext).inflate(i, (ViewGroup) this.mRootCardViewByLinearLayout, false);
    }

    protected final void addViewByBody(View view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(view);
        }
        this.mRootCardViewByLinearLayout.addView(view);
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void buildRootView() {
        createLinearLayout();
        if (Controller.SDK_LOLLIPOP) {
            CardView createCardView = createCardView();
            createCardView.addView(this.mRootCardViewByLinearLayout);
            if (this.mParams.closeParams == null) {
                this.mRoot = createCardView;
                return;
            }
            LinearLayout linearLayout = new LinearLayout(this.mContext);
            linearLayout.setBackgroundColor(0);
            linearLayout.setOrientation(1);
            linearLayout.addView(createCardView);
            this.mRoot = linearLayout;
            return;
        }
        this.mRoot = this.mRootCardViewByLinearLayout;
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void buildTitleView() {
        if (this.mParams.titleParams != null) {
            TitleView titleView = new TitleView(this.mContext, this.mParams);
            this.mTitleView = titleView;
            this.mRootCardViewByLinearLayout.addView(titleView);
        }
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public ButtonView buildButton() {
        ConfirmButton confirmButton = new ConfirmButton(this.mContext, this.mParams);
        this.mButtonView = confirmButton;
        if (!confirmButton.isEmpty()) {
            this.mRootCardViewByLinearLayout.addView(new DividerView(this.mContext, 0));
            Object bodyView = getBodyView();
            confirmButton.addCountDownTimerObserver(bodyView instanceof BodyProgressView ? ((BodyProgressView) bodyView).getCountDownTimerObserver() : null);
        }
        this.mRootCardViewByLinearLayout.addView(this.mButtonView.getView());
        return this.mButtonView;
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public CloseView buildCloseImgView() {
        CloseParams closeParams = this.mParams.closeParams;
        CloseImgView closeImgView = new CloseImgView(this.mContext, closeParams);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        if (closeParams.closeGravity == 351 || closeParams.closeGravity == 783) {
            layoutParams.gravity = 3;
        } else if (closeParams.closeGravity == 349 || closeParams.closeGravity == 781) {
            layoutParams.gravity = 1;
        } else {
            layoutParams.gravity = 5;
        }
        closeImgView.setLayoutParams(layoutParams);
        if (closeParams.closeGravity == 351 || closeParams.closeGravity == 349 || closeParams.closeGravity == 353) {
            this.mRoot.addView(closeImgView, 0);
        } else {
            this.mRoot.addView(closeImgView);
        }
        return closeImgView;
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public final View getRootView() {
        return this.mRoot;
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public void refreshTitle() {
        TitleView titleView = this.mTitleView;
        if (titleView != null) {
            titleView.refreshText();
        }
    }

    @Override // com.mylhyl.circledialog.internal.BuildView
    public final void refreshButton() {
        ButtonView buttonView = this.mButtonView;
        if (buttonView != null) {
            buttonView.refreshText();
        }
    }

    protected CardView createCardView() {
        int dp2px = Controller.dp2px(this.mContext, this.mParams.dialogParams.radius);
        CardView cardView = new CardView(this.mContext);
        cardView.setCardElevation(0.0f);
        if (Controller.SDK_LOLLIPOP) {
            cardView.setCardBackgroundColor(0);
        } else {
            cardView.setPreventCornerOverlap(false);
            cardView.setCardBackgroundColor(this.mParams.dialogParams.backgroundColor);
            cardView.setUseCompatPadding(true);
            double d = dp2px;
            int ceil = (int) Math.ceil(d - (COS_45 * d));
            cardView.setContentPadding(ceil, ceil, ceil, ceil);
        }
        cardView.setRadius(dp2px);
        return cardView;
    }

    protected LinearLayout createLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(this.mContext);
        linearLayout.setOrientation(1);
        this.mRootCardViewByLinearLayout = linearLayout;
        return linearLayout;
    }
}