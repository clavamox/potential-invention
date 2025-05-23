package com.mylhyl.circledialog.internal;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import com.mylhyl.circledialog.view.listener.OnInputCounterChangeListener;

/* loaded from: classes.dex */
public class MaxLengthByteWatcher implements TextWatcher {
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

    public MaxLengthByteWatcher(int i, EditText editText, TextView textView, OnInputCounterChangeListener onInputCounterChangeListener) {
        this.mMaxLen = i;
        this.mEditText = editText;
        this.mTvCounter = textView;
        this.mOnInputCounterChangeListener = onInputCounterChangeListener;
        if (editText == null) {
            return;
        }
        int chineseLength = i - chineseLength(editText.getText().toString());
        OnInputCounterChangeListener onInputCounterChangeListener2 = this.mOnInputCounterChangeListener;
        if (onInputCounterChangeListener2 != null) {
            String onCounterChange = onInputCounterChangeListener2.onCounterChange(i, chineseLength);
            this.mTvCounter.setText(onCounterChange == null ? "" : onCounterChange);
        } else {
            this.mTvCounter.setText(String.valueOf(chineseLength));
        }
    }

    private static int chineseLength(String str) {
        int i = 0;
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int i2 = 0;
        while (i < str.length()) {
            int i3 = i + 1;
            i2 += str.substring(i, i3).getBytes().length;
            i = i3;
        }
        return i2;
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable editable) {
        int selectionStart = this.mEditText.getSelectionStart();
        int selectionEnd = this.mEditText.getSelectionEnd();
        this.mEditText.removeTextChangedListener(this);
        if (!TextUtils.isEmpty(editable)) {
            while (chineseLength(editable.toString()) > this.mMaxLen) {
                editable.delete(selectionStart - 1, selectionEnd);
                selectionStart--;
                selectionEnd--;
            }
        }
        int chineseLength = this.mMaxLen - chineseLength(editable.toString());
        OnInputCounterChangeListener onInputCounterChangeListener = this.mOnInputCounterChangeListener;
        if (onInputCounterChangeListener != null) {
            String onCounterChange = onInputCounterChangeListener.onCounterChange(this.mMaxLen, chineseLength);
            TextView textView = this.mTvCounter;
            if (onCounterChange == null) {
                onCounterChange = "";
            }
            textView.setText(onCounterChange);
        } else {
            this.mTvCounter.setText(String.valueOf(chineseLength));
        }
        this.mEditText.setSelection(selectionStart);
        this.mEditText.addTextChangedListener(this);
    }
}