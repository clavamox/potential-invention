package com.yc.video.old.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yc.video.R;
import com.yc.video.old.listener.OnClarityChangedListener;
import com.yc.video.tool.PlayerUtils;
import java.util.List;

@Deprecated
/* loaded from: classes.dex */
public class ChangeClarityDialog extends Dialog {
    private int mCurrentCheckedIndex;
    private LinearLayout mLinearLayout;
    private OnClarityChangedListener mListener;

    public ChangeClarityDialog(Context context) {
        super(context, R.style.dialog_change_clarity);
        init(context);
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        OnClarityChangedListener onClarityChangedListener = this.mListener;
        if (onClarityChangedListener != null) {
            onClarityChangedListener.onClarityNotChanged();
        }
        super.onBackPressed();
    }

    private void init(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        this.mLinearLayout = linearLayout;
        linearLayout.setGravity(17);
        this.mLinearLayout.setOrientation(1);
        this.mLinearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.yc.video.old.dialog.ChangeClarityDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ChangeClarityDialog.this.mListener != null) {
                    ChangeClarityDialog.this.mListener.onClarityNotChanged();
                }
                ChangeClarityDialog.this.dismiss();
            }
        });
        setContentView(this.mLinearLayout, new ViewGroup.LayoutParams(-1, -1));
        if (getWindow() != null) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.width = PlayerUtils.getScreenHeight(context);
            attributes.height = PlayerUtils.getScreenWidth(context);
            getWindow().setAttributes(attributes);
        }
    }

    public void setClarityGrade(List<String> list, int i) {
        this.mCurrentCheckedIndex = i;
        int i2 = 0;
        while (i2 < list.size()) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.old_video_clarity, (ViewGroup) this.mLinearLayout, false);
            textView.setTag(Integer.valueOf(i2));
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.yc.video.old.dialog.ChangeClarityDialog.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (ChangeClarityDialog.this.mListener != null) {
                        int intValue = ((Integer) view.getTag()).intValue();
                        if (intValue != ChangeClarityDialog.this.mCurrentCheckedIndex) {
                            int i3 = 0;
                            while (i3 < ChangeClarityDialog.this.mLinearLayout.getChildCount()) {
                                ChangeClarityDialog.this.mLinearLayout.getChildAt(i3).setSelected(intValue == i3);
                                i3++;
                            }
                            ChangeClarityDialog.this.mListener.onClarityChanged(intValue);
                            ChangeClarityDialog.this.mCurrentCheckedIndex = intValue;
                        } else {
                            ChangeClarityDialog.this.mListener.onClarityNotChanged();
                        }
                    }
                    ChangeClarityDialog.this.dismiss();
                }
            });
            textView.setText(list.get(i2));
            textView.setSelected(i2 == i);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            marginLayoutParams.topMargin = i2 == 0 ? 0 : PlayerUtils.dp2px(getContext(), 16.0f);
            this.mLinearLayout.addView(textView, marginLayoutParams);
            i2++;
        }
    }

    public void setOnClarityCheckedListener(OnClarityChangedListener onClarityChangedListener) {
        this.mListener = onClarityChangedListener;
    }
}