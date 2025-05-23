package com.wy.ui.activity;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class HelpActivity_ViewBinding implements Unbinder {
    private HelpActivity target;

    public HelpActivity_ViewBinding(HelpActivity helpActivity) {
        this(helpActivity, helpActivity.getWindow().getDecorView());
    }

    public HelpActivity_ViewBinding(HelpActivity helpActivity, View view) {
        this.target = helpActivity;
        helpActivity.recyclerView = (RecyclerView) Utils.findRequiredViewAsType(view, R.id.recyclerView, "field 'recyclerView'", RecyclerView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        HelpActivity helpActivity = this.target;
        if (helpActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        helpActivity.recyclerView = null;
    }
}