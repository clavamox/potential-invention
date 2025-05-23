package com.mylhyl.circledialog.internal;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import com.mylhyl.circledialog.view.listener.OnInputCounterChangeListener;

/* loaded from: classes.dex */
public class MaxLengthEnWatcher implements TextWatcher {
    private EditText mEditText;
    private int mMaxLen;
    private OnInputCounterChangeListener mOnInputCounterChangeListener;
    private TextView mTvCounter;

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public MaxLengthEnWatcher(int i, EditText editText, TextView textView, OnInputCounterChangeListener onInputCounterChangeListener) {
        this.mMaxLen = i;
        this.mEditText = editText;
        this.mTvCounter = textView;
        this.mOnInputCounterChangeListener = onInputCounterChangeListener;
        if (editText == null) {
            return;
        }
        int length = i - editText.getText().toString().length();
        OnInputCounterChangeListener onInputCounterChangeListener2 = this.mOnInputCounterChangeListener;
        if (onInputCounterChangeListener2 != null) {
            String onCounterChange = onInputCounterChangeListener2.onCounterChange(i, length);
            this.mTvCounter.setText(onCounterChange == null ? "" : onCounterChange);
        } else {
            this.mTvCounter.setText(String.valueOf(length));
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        int selectionStart = this.mEditText.getSelectionStart();
        int selectionEnd = this.mEditText.getSelectionEnd();
        this.mEditText.removeTextChangedListener(this);
        if (!TextUtils.isEmpty(editable)) {
            while (editable.toString().length() > this.mMaxLen) {
                editable.delete(selectionStart - 1, selectionEnd);
                selectionStart--;
                selectionEnd--;
            }
        }
        int length = this.mMaxLen - editable.toString().length();
        OnInputCounterChangeListener onInputCounterChangeListener = this.mOnInputCounterChangeListener;
        if (onInputCounterChangeListener != null) {
            String onCounterChange = onInputCounterChangeListener.onCounterChange(this.mMaxLen, length);
            TextView textView = this.mTvCounter;
            if (onCounterChange == null) {
                onCounterChange = "";
            }
            textView.setText(onCounterChange);
        } else {
            this.mTvCounter.setText(String.valueOf(length));
        }
        this.mEditText.setSelection(selectionStart);
        this.mEditText.addTextChangedListener(this);
    }
}