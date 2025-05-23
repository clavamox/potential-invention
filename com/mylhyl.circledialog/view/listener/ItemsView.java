package com.mylhyl.circledialog.view.listener;

import android.view.View;
import android.widget.AdapterView;

/* loaded from: classes.dex */
public interface ItemsView {
    View getView();

    void refreshItems();

    void regOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener);

    void regOnItemClickListener(OnRvItemClickListener onRvItemClickListener);
}