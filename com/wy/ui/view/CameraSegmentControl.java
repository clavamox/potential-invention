package com.wy.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import com.wy.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class CameraSegmentControl extends LinearLayout implements View.OnClickListener {
    private Drawable defalutBg;
    private boolean gyroscopeState;
    private OnCameraSegmentListener listener;
    private Context mContext;
    private boolean mirrorState;
    private Drawable selectBg;
    private int selectedTextColor;
    private int textColor;
    private int textSize;
    private String[] titles;
    private List<TextView> viewList;

    public interface OnCameraSegmentListener {
        void onGyroscopeState(boolean z);

        void onMirrorState(boolean z);
    }

    public void setOnCameraSegmentListener(OnCameraSegmentListener onCameraSegmentListener) {
        if (onCameraSegmentListener != null) {
            this.listener = onCameraSegmentListener;
        }
    }

    public CameraSegmentControl(Context context) {
        this(context, null);
        init();
    }

    public CameraSegmentControl(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
        init();
    }

    public CameraSegmentControl(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.titles = new String[0];
        this.textSize = 12;
        this.viewList = new ArrayList();
        this.textColor = -1;
        this.selectedTextColor = ViewCompat.MEASURED_STATE_MASK;
        this.mContext = context;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Segment);
        this.defalutBg = obtainStyledAttributes.getDrawable(0);
        this.selectBg = obtainStyledAttributes.getDrawable(2);
        this.textColor = obtainStyledAttributes.getColor(1, -1);
        this.selectedTextColor = obtainStyledAttributes.getColor(3, ViewCompat.MEASURED_STATE_MASK);
        obtainStyledAttributes.recycle();
        init();
    }

    private void init() {
        removeAllViews();
        setPadding(1, 1, 1, 1);
        if (this.titles.length > 0) {
            for (int i = 0; i < this.titles.length; i++) {
                TextView textView = new TextView(getContext());
                textView.setTag(Integer.valueOf(i));
                textView.setText(this.titles[i]);
                setTextColorVal(textView);
                textView.setTextSize(this.textSize);
                textView.setGravity(17);
                textView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1, 1.0f));
                textView.setOnClickListener(this);
                textView.setId(i);
                if (i == 0) {
                    textView.setBackground(this.defalutBg);
                    textView.setSelected(true);
                    setSelectedTextColor(textView);
                } else if (i == this.titles.length - 1) {
                    textView.setSelected(false);
                    textView.setBackground(this.selectBg);
                } else {
                    textView.setSelected(false);
                    textView.setBackgroundResource(com.xzf.camera.R.drawable.middle);
                }
                addView(textView);
                if (i != this.titles.length - 1) {
                    TextView textView2 = new TextView(getContext());
                    textView2.setBackgroundColor(this.textColor);
                    textView2.setLayoutParams(new LinearLayout.LayoutParams(1, -1));
                    addView(textView2);
                }
                this.viewList.add(textView);
            }
        }
    }

    public void setTextSize(int i) {
        this.textSize = i;
        Iterator<TextView> it = this.viewList.iterator();
        while (it.hasNext()) {
            it.next().setTextSize(this.textSize);
        }
        invalidate();
    }

    public String[] getTitles() {
        return this.titles;
    }

    public void setSelectedTextColor(int i) {
        this.selectedTextColor = i;
        for (TextView textView : this.viewList) {
            if (textView.isSelected()) {
                setSelectedTextColor(textView);
            }
        }
    }

    public void setTextColor(int i) {
        this.textColor = i;
        Iterator<TextView> it = this.viewList.iterator();
        while (it.hasNext()) {
            setTextColorVal(it.next());
        }
        invalidate();
    }

    private void setTextColorVal(TextView textView) {
        textView.setTextColor(this.textColor);
    }

    public void setTitles(String[] strArr) {
        this.titles = strArr;
        init();
        invalidate();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (((Integer) view.getTag()).intValue() == 2) {
            view.setSelected(!view.isSelected());
            if (view.isSelected()) {
                setSelectedTextColor((TextView) view);
            } else {
                setTextColorVal((TextView) view);
            }
            OnCameraSegmentListener onCameraSegmentListener = this.listener;
            if (onCameraSegmentListener != null) {
                onCameraSegmentListener.onMirrorState(view.isSelected());
            }
        } else {
            for (int i = 0; i < 2; i++) {
                TextView textView = this.viewList.get(i);
                textView.setSelected(false);
                setTextColorVal(textView);
            }
            if (this.listener != null) {
                TextView textView2 = (TextView) view;
                textView2.setSelected(true);
                setSelectedTextColor(textView2);
                this.listener.onGyroscopeState(!this.viewList.get(0).isSelected());
            }
        }
        invalidate();
    }

    private void setSelectedTextColor(TextView textView) {
        textView.setTextColor(this.selectedTextColor);
    }

    public void setGyroscopeState(boolean z) {
        this.gyroscopeState = z;
        for (int i = 0; i < 2; i++) {
            TextView textView = this.viewList.get(i);
            textView.setSelected(false);
            setTextColorVal(textView);
        }
        if (!this.gyroscopeState) {
            this.viewList.get(0).setSelected(true);
            setSelectedTextColor(this.viewList.get(0));
        } else {
            this.viewList.get(1).setSelected(true);
            setSelectedTextColor(this.viewList.get(1));
        }
        invalidate();
    }

    public void setMirrorState(boolean z) {
        this.mirrorState = z;
        this.viewList.get(2).setSelected(this.mirrorState);
        if (this.mirrorState) {
            setSelectedTextColor(this.viewList.get(2));
        } else {
            setTextColorVal(this.viewList.get(2));
        }
        invalidate();
    }
}