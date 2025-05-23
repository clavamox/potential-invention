package com.yc.video.ui.more;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.ui.view.InterControlView;

/* loaded from: classes.dex */
public class CustomNetworkView extends FrameLayout implements InterControlView, View.OnClickListener {
    @Override // com.yc.video.ui.view.InterControlView
    public void attach(ControlWrapper controlWrapper) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public View getView() {
        return null;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onLockStateChanged(boolean z) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayStateChanged(int i) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayerStateChanged(int i) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onVisibilityChanged(boolean z, Animation animation) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void setProgress(int i, int i2) {
    }

    public CustomNetworkView(Context context) {
        super(context);
    }

    public CustomNetworkView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomNetworkView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
}