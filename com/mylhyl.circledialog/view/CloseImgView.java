package com.mylhyl.circledialog.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.params.CloseParams;
import com.mylhyl.circledialog.view.listener.CloseView;

/* loaded from: classes.dex */
public class CloseImgView extends LinearLayout implements CloseView {
    private CloseParams mCloseParams;
    private ImageView mImageCloseView;

    @Override // com.mylhyl.circledialog.view.listener.CloseView
    public View getView() {
        return this;
    }

    public CloseImgView(Context context, CloseParams closeParams) {
        super(context);
        this.mCloseParams = closeParams;
        init();
    }

    @Override // com.mylhyl.circledialog.view.listener.CloseView
    public void regOnCloseClickListener(View.OnClickListener onClickListener) {
        this.mImageCloseView.setOnClickListener(onClickListener);
    }

    private void init() {
        setOrientation(1);
        setGravity(1);
        if (this.mCloseParams.closePadding != null && this.mCloseParams.closePadding.length == 4) {
            setPadding(Controller.dp2px(getContext(), this.mCloseParams.closePadding[0]), Controller.dp2px(getContext(), this.mCloseParams.closePadding[1]), Controller.dp2px(getContext(), this.mCloseParams.closePadding[2]), Controller.dp2px(getContext(), this.mCloseParams.closePadding[3]));
        }
        this.mImageCloseView = new ImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        if (this.mCloseParams.closeSize != 0) {
            int dp2px = Controller.dp2px(getContext(), this.mCloseParams.closeSize);
            layoutParams.height = dp2px;
            layoutParams.width = dp2px;
        }
        if (this.mCloseParams.closeResId != 0) {
            this.mImageCloseView.setImageResource(this.mCloseParams.closeResId);
        }
        this.mImageCloseView.setLayoutParams(layoutParams);
        this.mImageCloseView.setAdjustViewBounds(true);
        if (this.mCloseParams.connectorWidth > 0) {
            DividerView dividerView = new DividerView(getContext());
            dividerView.setBgColor(this.mCloseParams.connectorColor);
            addView(dividerView, new LinearLayout.LayoutParams(Controller.dp2px(getContext(), this.mCloseParams.connectorWidth), Controller.dp2px(getContext(), this.mCloseParams.connectorHeight)));
        }
        if (this.mCloseParams.closeGravity == 351 || this.mCloseParams.closeGravity == 349 || this.mCloseParams.closeGravity == 353) {
            addView(this.mImageCloseView, 0);
        } else {
            addView(this.mImageCloseView);
        }
    }
}