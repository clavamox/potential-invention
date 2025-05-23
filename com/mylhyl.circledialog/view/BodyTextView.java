package com.mylhyl.circledialog.view;

import android.R;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatTextView;
import com.mylhyl.circledialog.internal.BackgroundHelper;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.TextParams;
import com.mylhyl.circledialog.view.listener.OnCreateTextListener;

/* loaded from: classes.dex */
final class BodyTextView extends AppCompatTextView {
    private DialogParams mDialogParams;
    private OnCreateTextListener mOnCreateTextListener;
    private TextParams mTextParams;

    public BodyTextView(Context context, CircleParams circleParams) {
        super(context);
        init(circleParams);
    }

    private void init(CircleParams circleParams) {
        this.mDialogParams = circleParams.dialogParams;
        this.mTextParams = circleParams.textParams;
        this.mOnCreateTextListener = circleParams.circleListeners.createTextListener;
        if (this.mTextParams == null) {
            TextParams textParams = new TextParams();
            this.mTextParams = textParams;
            textParams.height = 0;
            this.mTextParams.padding = null;
        }
        setId(R.id.text1);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        if (this.mDialogParams.typeface != null) {
            setTypeface(this.mDialogParams.typeface);
        }
        setGravity(this.mTextParams.gravity);
        BackgroundHelper.handleBodyBackground(this, this.mTextParams.backgroundColor != 0 ? this.mTextParams.backgroundColor : this.mDialogParams.backgroundColor, circleParams);
        setMovementMethod(ScrollingMovementMethod.getInstance());
        setMinHeight(this.mTextParams.height);
        setTextColor(this.mTextParams.textColor);
        setTextSize(this.mTextParams.textSize);
        setText(this.mTextParams.text);
        setTypeface(getTypeface(), this.mTextParams.styleText);
        if (this.mTextParams.padding != null) {
            setPadding(Controller.dp2px(getContext(), r6[0]), Controller.dp2px(getContext(), r6[1]), Controller.dp2px(getContext(), r6[2]), Controller.dp2px(getContext(), r6[3]));
        }
        OnCreateTextListener onCreateTextListener = this.mOnCreateTextListener;
        if (onCreateTextListener != null) {
            onCreateTextListener.onCreateText(this);
        }
    }

    public void refreshText() {
        TextParams textParams = this.mTextParams;
        if (textParams != null) {
            setText(textParams.text);
        }
    }
}