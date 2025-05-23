package com.mylhyl.circledialog.view;

import android.R;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mylhyl.circledialog.internal.BackgroundHelper;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.SubTitleParams;
import com.mylhyl.circledialog.params.TitleParams;
import com.mylhyl.circledialog.view.listener.OnCreateTitleListener;

/* loaded from: classes.dex */
final class TitleView extends LinearLayout {
    private DialogParams mDialogParams;
    private OnCreateTitleListener mOnCreateTitleListener;
    private SubTitleParams mSubTitleParams;
    private TextView mSubTitleView;
    private TitleParams mTitleParams;
    private TextView mTitleView;

    public TitleView(Context context, CircleParams circleParams) {
        super(context);
        this.mDialogParams = circleParams.dialogParams;
        this.mTitleParams = circleParams.titleParams;
        this.mSubTitleParams = circleParams.subTitleParams;
        this.mOnCreateTitleListener = circleParams.circleListeners.createTitleListener;
        init();
    }

    public void refreshText() {
        TextView textView;
        TitleParams titleParams = this.mTitleParams;
        if (titleParams == null || (textView = this.mTitleView) == null) {
            return;
        }
        textView.setText(titleParams.text);
        TextView textView2 = this.mSubTitleView;
        if (textView2 != null) {
            textView2.setText(this.mSubTitleParams.text);
        }
    }

    private void init() {
        ImageView imageView;
        setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        setOrientation(1);
        this.mTitleView = createTitle();
        if (this.mTitleParams.icon != 0) {
            RelativeLayout createTitleLayout = createTitleLayout();
            addView(createTitleLayout);
            imageView = createTitleIcon();
            createTitleLayout.addView(imageView);
            createTitleLayout.addView(this.mTitleView);
        } else {
            this.mTitleView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            this.mTitleView.setGravity(this.mTitleParams.gravity);
            handleTitleBackground(this.mTitleView);
            addView(this.mTitleView);
            imageView = null;
        }
        createSubTitle();
        OnCreateTitleListener onCreateTitleListener = this.mOnCreateTitleListener;
        if (onCreateTitleListener != null) {
            onCreateTitleListener.onCreateTitle(imageView, this.mTitleView, this.mSubTitleView);
        }
    }

    private RelativeLayout createTitleLayout() {
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        relativeLayout.setGravity(this.mTitleParams.gravity);
        relativeLayout.setPadding(50, 0, 50, 0);
        handleTitleBackground(relativeLayout);
        return relativeLayout;
    }

    private void handleTitleBackground(View view) {
        BackgroundHelper.handleTitleBackground(view, this.mTitleParams.backgroundColor != 0 ? this.mTitleParams.backgroundColor : this.mDialogParams.backgroundColor, this.mDialogParams);
    }

    private ImageView createTitleIcon() {
        ImageView imageView = new ImageView(getContext());
        imageView.setId(R.id.icon);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(0, R.id.title);
        layoutParams.addRule(15);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(this.mTitleParams.icon);
        imageView.setVisibility(0);
        return imageView;
    }

    private TextView createTitle() {
        TextView textView = new TextView(getContext());
        if (this.mDialogParams.typeface != null) {
            textView.setTypeface(this.mDialogParams.typeface);
        }
        textView.setGravity(17);
        textView.setId(R.id.title);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(14);
        textView.setLayoutParams(layoutParams);
        if (this.mTitleParams.height != 0) {
            textView.setHeight(Controller.dp2px(getContext(), this.mTitleParams.height));
        }
        textView.setTextColor(this.mTitleParams.textColor);
        textView.setTextSize(this.mTitleParams.textSize);
        textView.setText(this.mTitleParams.text);
        textView.setTypeface(textView.getTypeface(), this.mTitleParams.styleText);
        int[] iArr = this.mTitleParams.padding;
        if (iArr != null) {
            if (this.mTitleParams.isShowBottomDivider) {
                int i = iArr[3];
                if (i == 0) {
                    i = iArr[1];
                }
                iArr[3] = i;
                addView(new DividerView(getContext(), 0));
            }
            textView.setPadding(Controller.dp2px(getContext(), iArr[0]), Controller.dp2px(getContext(), iArr[1]), Controller.dp2px(getContext(), iArr[2]), Controller.dp2px(getContext(), iArr[3]));
        }
        return textView;
    }

    private void createSubTitle() {
        if (this.mSubTitleParams == null) {
            return;
        }
        TextView textView = new TextView(getContext());
        this.mSubTitleView = textView;
        textView.setId(R.id.summary);
        addView(this.mSubTitleView);
        if (this.mDialogParams.typeface != null) {
            this.mSubTitleView.setTypeface(this.mDialogParams.typeface);
        }
        this.mSubTitleView.setGravity(17);
        setSubTitleBg(this.mSubTitleView, this.mSubTitleParams.backgroundColor, this.mDialogParams.backgroundColor);
        this.mSubTitleView.setGravity(this.mSubTitleParams.gravity);
        if (this.mSubTitleParams.height != 0) {
            this.mSubTitleView.setHeight(Controller.dp2px(getContext(), this.mSubTitleParams.height));
        }
        this.mSubTitleView.setTextColor(this.mSubTitleParams.textColor);
        this.mSubTitleView.setTextSize(this.mSubTitleParams.textSize);
        this.mSubTitleView.setText(this.mSubTitleParams.text);
        TextView textView2 = this.mSubTitleView;
        textView2.setTypeface(textView2.getTypeface(), this.mSubTitleParams.styleText);
        int[] iArr = this.mSubTitleParams.padding;
        if (iArr == null) {
            return;
        }
        if (this.mSubTitleParams.isShowBottomDivider) {
            int i = iArr[3];
            if (i == 0) {
                i = iArr[1];
            }
            iArr[3] = i;
            addView(new DividerView(getContext(), 0));
        }
        this.mSubTitleView.setPadding(Controller.dp2px(getContext(), iArr[0]), Controller.dp2px(getContext(), iArr[1]), Controller.dp2px(getContext(), iArr[2]), Controller.dp2px(getContext(), iArr[3]));
    }

    private void setSubTitleBg(TextView textView, int i, int i2) {
        if (i == 0) {
            i = i2;
        }
        textView.setBackgroundColor(i);
    }
}