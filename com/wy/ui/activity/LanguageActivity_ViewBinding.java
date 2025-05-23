package com.wy.ui.activity;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class LanguageActivity_ViewBinding implements Unbinder {
    private LanguageActivity target;

    public LanguageActivity_ViewBinding(LanguageActivity languageActivity) {
        this(languageActivity, languageActivity.getWindow().getDecorView());
    }

    public LanguageActivity_ViewBinding(LanguageActivity languageActivity, View view) {
        this.target = languageActivity;
        languageActivity.recyclerView = (RecyclerView) Utils.findRequiredViewAsType(view, R.id.recyclerView, "field 'recyclerView'", RecyclerView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        LanguageActivity languageActivity = this.target;
        if (languageActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        languageActivity.recyclerView = null;
    }
}