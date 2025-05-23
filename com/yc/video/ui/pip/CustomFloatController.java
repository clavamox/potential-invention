package com.yc.video.ui.pip;

import android.content.Context;
import android.util.AttributeSet;
import com.yc.video.controller.GestureVideoController;
import com.yc.video.ui.view.CustomCompleteView;
import com.yc.video.ui.view.CustomErrorView;

/* loaded from: classes.dex */
public class CustomFloatController extends GestureVideoController {
    @Override // com.yc.video.controller.InterVideoController
    public void destroy() {
    }

    @Override // com.yc.video.controller.BaseVideoController
    protected int getLayoutId() {
        return 0;
    }

    public CustomFloatController(Context context) {
        super(context);
    }

    public CustomFloatController(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.yc.video.controller.GestureVideoController, com.yc.video.controller.BaseVideoController
    protected void initView(Context context) {
        super.initView(context);
        addControlComponent(new CustomCompleteView(getContext()));
        addControlComponent(new CustomErrorView(getContext()));
        addControlComponent(new CustomFloatView(getContext()));
    }
}