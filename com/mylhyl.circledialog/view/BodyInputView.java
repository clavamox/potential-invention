package com.mylhyl.circledialog.view;

import android.R;
import android.content.Context;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mylhyl.circledialog.internal.BackgroundHelper;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.internal.EmojiFilter;
import com.mylhyl.circledialog.internal.MaxLengthByteWatcher;
import com.mylhyl.circledialog.internal.MaxLengthEnWatcher;
import com.mylhyl.circledialog.internal.MaxLengthWatcher;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.params.SubTitleParams;
import com.mylhyl.circledialog.params.TitleParams;
import com.mylhyl.circledialog.res.drawable.InputDrawable;
import com.mylhyl.circledialog.res.values.CircleDimen;
import com.mylhyl.circledialog.view.listener.InputView;
import com.mylhyl.circledialog.view.listener.OnCreateInputListener;
import com.mylhyl.circledialog.view.listener.OnInputCounterChangeListener;

/* loaded from: classes.dex */
final class BodyInputView extends RelativeLayout implements InputView {
    private DialogParams mDialogParams;
    private EditText mEditText;
    private InputParams mInputParams;
    private OnCreateInputListener mOnCreateInputListener;
    private OnInputCounterChangeListener mOnInputCounterChangeListener;
    private SubTitleParams mSubTitleParams;
    private TitleParams mTitleParams;
    private TextView mTvCounter;

    @Override // com.mylhyl.circledialog.view.listener.InputView
    public View getView() {
        return this;
    }

    public BodyInputView(Context context, CircleParams circleParams) {
        super(context);
        init(circleParams);
    }

    @Override // com.mylhyl.circledialog.view.listener.InputView
    public EditText getInput() {
        return this.mEditText;
    }

    private void init(CircleParams circleParams) {
        int i;
        this.mDialogParams = circleParams.dialogParams;
        this.mTitleParams = circleParams.titleParams;
        this.mSubTitleParams = circleParams.subTitleParams;
        this.mInputParams = circleParams.inputParams;
        this.mOnInputCounterChangeListener = circleParams.circleListeners.inputCounterChangeListener;
        this.mOnCreateInputListener = circleParams.circleListeners.createInputListener;
        TitleParams titleParams = this.mTitleParams;
        if (titleParams == null) {
            SubTitleParams subTitleParams = this.mSubTitleParams;
            i = subTitleParams == null ? CircleDimen.TITLE_PADDING[1] : subTitleParams.padding[1];
        } else {
            i = titleParams.padding[1];
        }
        setPadding(0, Controller.dp2px(getContext(), i), 0, 0);
        BackgroundHelper.handleBodyBackground(this, this.mInputParams.backgroundColor != 0 ? this.mInputParams.backgroundColor : this.mDialogParams.backgroundColor, circleParams);
        setFocusableInTouchMode(true);
        setFocusable(true);
        createInput();
        createCounter();
        if (this.mInputParams.isEmojiInput) {
            this.mEditText.setFilters(new InputFilter[]{new EmojiFilter()});
        }
        OnCreateInputListener onCreateInputListener = this.mOnCreateInputListener;
        if (onCreateInputListener != null) {
            onCreateInputListener.onCreateText(this, this.mEditText, this.mTvCounter);
        }
    }

    private void createInput() {
        EditText editText = new EditText(getContext());
        this.mEditText = editText;
        editText.setId(R.id.input);
        if (this.mInputParams.inputType != 0) {
            this.mEditText.setInputType(this.mInputParams.inputType);
        }
        if (this.mDialogParams.typeface != null) {
            this.mEditText.setTypeface(this.mDialogParams.typeface);
        }
        this.mEditText.setHint(this.mInputParams.hintText);
        this.mEditText.setHintTextColor(this.mInputParams.hintTextColor);
        this.mEditText.setTextSize(this.mInputParams.textSize);
        this.mEditText.setTextColor(this.mInputParams.textColor);
        this.mEditText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.mylhyl.circledialog.view.BodyInputView.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                BodyInputView.this.mEditText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (BodyInputView.this.mInputParams.inputHeight > BodyInputView.this.mEditText.getMeasuredHeight()) {
                    BodyInputView.this.mEditText.setHeight(Controller.dp2px(BodyInputView.this.getContext(), BodyInputView.this.mInputParams.inputHeight));
                }
            }
        });
        this.mEditText.setGravity(this.mInputParams.gravity);
        if (!TextUtils.isEmpty(this.mInputParams.text)) {
            this.mEditText.setText(this.mInputParams.text);
            this.mEditText.setSelection(this.mInputParams.text.length());
        }
        int i = this.mInputParams.inputBackgroundResourceId;
        if (i == 0) {
            BackgroundHelper.handleBackground(this.mEditText, new InputDrawable(Controller.dp2px(getContext(), this.mInputParams.strokeWidth), this.mInputParams.strokeColor, this.mInputParams.inputBackgroundColor));
        } else {
            this.mEditText.setBackgroundResource(i);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
        if (this.mInputParams.margins != null) {
            layoutParams.setMargins(Controller.dp2px(getContext(), r1[0]), Controller.dp2px(getContext(), r1[1]), Controller.dp2px(getContext(), r1[2]), Controller.dp2px(getContext(), r1[3]));
        }
        if (this.mInputParams.padding != null) {
            this.mEditText.setPadding(Controller.dp2px(getContext(), r1[0]), Controller.dp2px(getContext(), r1[1]), Controller.dp2px(getContext(), r1[2]), Controller.dp2px(getContext(), r1[3]));
        }
        EditText editText2 = this.mEditText;
        editText2.setTypeface(editText2.getTypeface(), this.mInputParams.styleText);
        addView(this.mEditText, layoutParams);
    }

    private void createCounter() {
        if (this.mInputParams.maxLen <= 0) {
            return;
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(7, R.id.input);
        layoutParams.addRule(8, R.id.input);
        if (this.mInputParams.counterMargins != null) {
            layoutParams.setMargins(0, 0, Controller.dp2px(getContext(), this.mInputParams.counterMargins[0]), Controller.dp2px(getContext(), this.mInputParams.counterMargins[1]));
        }
        this.mTvCounter = new TextView(getContext());
        if (this.mDialogParams.typeface != null) {
            this.mTvCounter.setTypeface(this.mDialogParams.typeface);
        }
        this.mTvCounter.setTextSize(CircleDimen.INPUT_COUNTER__TEXT_SIZE);
        this.mTvCounter.setTextColor(this.mInputParams.counterColor);
        if (this.mInputParams.maxLengthType == 1) {
            this.mEditText.addTextChangedListener(new MaxLengthWatcher(this.mInputParams.maxLen, this.mEditText, this.mTvCounter, this.mOnInputCounterChangeListener));
        } else if (this.mInputParams.maxLengthType == 2) {
            this.mEditText.addTextChangedListener(new MaxLengthEnWatcher(this.mInputParams.maxLen, this.mEditText, this.mTvCounter, this.mOnInputCounterChangeListener));
        } else {
            this.mEditText.addTextChangedListener(new MaxLengthByteWatcher(this.mInputParams.maxLen, this.mEditText, this.mTvCounter, this.mOnInputCounterChangeListener));
        }
        addView(this.mTvCounter, layoutParams);
    }
}