package com.mylhyl.circledialog;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;
import com.mylhyl.circledialog.internal.Controller;

/* loaded from: classes.dex */
public class CircleViewHolder {
    private final View dialogView;
    private final SparseArray<View> views = new SparseArray<>();

    public interface TextWatcher {
        void onTextChanged(CharSequence charSequence, int i, int i2, int i3);
    }

    public interface TextWatcherAfter {
        void afterTextChanged(Editable editable);
    }

    public interface TextWatcherBefore {
        void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3);
    }

    public CircleViewHolder(View view) {
        this.dialogView = view;
    }

    public CircleViewHolder setBackgroundColor(int i) {
        this.dialogView.setBackgroundColor(i);
        return this;
    }

    public CircleViewHolder setBackgroundRes(int i) {
        this.dialogView.setBackgroundResource(i);
        return this;
    }

    public CircleViewHolder setBackgroundDrawable(Drawable drawable) {
        if (Controller.SDK_JELLY_BEAN) {
            this.dialogView.setBackground(drawable);
        } else {
            this.dialogView.setBackgroundDrawable(drawable);
        }
        return this;
    }

    public View getDialogView() {
        return this.dialogView;
    }

    public CircleViewHolder setText(int i, CharSequence charSequence) {
        ((TextView) findViewById(i)).setText(charSequence);
        return this;
    }

    public CircleViewHolder setText(int i, int i2) {
        ((TextView) findViewById(i)).setText(i2);
        return this;
    }

    public CircleViewHolder setImageResource(int i, int i2) {
        ((ImageView) findViewById(i)).setImageResource(i2);
        return this;
    }

    public CircleViewHolder setBackgroundColor(int i, int i2) {
        findViewById(i).setBackgroundColor(i2);
        return this;
    }

    public CircleViewHolder setBackgroundRes(int i, int i2) {
        findViewById(i).setBackgroundResource(i2);
        return this;
    }

    public CircleViewHolder setBackgroundDrawable(int i, Drawable drawable) {
        View findViewById = findViewById(i);
        if (Controller.SDK_JELLY_BEAN) {
            findViewById.setBackground(drawable);
        } else {
            findViewById.setBackgroundDrawable(drawable);
        }
        return this;
    }

    public CircleViewHolder setVisibility(int i, int i2) {
        findViewById(i).setVisibility(i2);
        return this;
    }

    public CircleViewHolder setChecked(int i, boolean z) {
        KeyEvent.Callback findViewById = findViewById(i);
        if (findViewById instanceof Checkable) {
            ((Checkable) findViewById).setChecked(z);
        }
        return this;
    }

    public CircleViewHolder setEnabled(int i, boolean z) {
        findViewById(i).setEnabled(z);
        return this;
    }

    public CircleViewHolder addTextChangedBeforeListener(int i, final TextWatcherBefore textWatcherBefore) {
        View findViewById = findViewById(i);
        if (findViewById instanceof TextView) {
            ((TextView) findViewById).addTextChangedListener(new android.text.TextWatcher() { // from class: com.mylhyl.circledialog.CircleViewHolder.1
                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                    textWatcherBefore.beforeTextChanged(charSequence, i2, i3, i4);
                }
            });
        }
        return this;
    }

    public CircleViewHolder addTextChangedListener(int i, TextWatcher textWatcher) {
        View findViewById = findViewById(i);
        if (findViewById instanceof TextView) {
            addTextChangedListener((TextView) findViewById, textWatcher);
        }
        return this;
    }

    public CircleViewHolder addTextChangedListener(TextView textView, final TextWatcher textWatcher) {
        textView.addTextChangedListener(new android.text.TextWatcher() { // from class: com.mylhyl.circledialog.CircleViewHolder.2
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                textWatcher.onTextChanged(charSequence, i, i2, i3);
            }
        });
        return this;
    }

    public CircleViewHolder addTextChangedAfterListener(int i, final TextWatcherAfter textWatcherAfter) {
        View findViewById = findViewById(i);
        if (findViewById instanceof TextView) {
            ((TextView) findViewById).addTextChangedListener(new android.text.TextWatcher() { // from class: com.mylhyl.circledialog.CircleViewHolder.3
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                    textWatcherAfter.afterTextChanged(editable);
                }
            });
        }
        return this;
    }

    public <T extends View> T findViewById(int i) {
        T t = (T) this.views.get(i);
        if (t != null) {
            return t;
        }
        T t2 = (T) this.dialogView.findViewById(i);
        this.views.put(i, t2);
        return t2;
    }
}