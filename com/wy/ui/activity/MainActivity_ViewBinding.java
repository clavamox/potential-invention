package com.wy.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class MainActivity_ViewBinding implements Unbinder {
    private MainActivity target;

    public MainActivity_ViewBinding(MainActivity mainActivity) {
        this(mainActivity, mainActivity.getWindow().getDecorView());
    }

    public MainActivity_ViewBinding(MainActivity mainActivity, View view) {
        this.target = mainActivity;
        mainActivity.tv_con_state = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_con_state, "field 'tv_con_state'", TextView.class);
        mainActivity.tv_remain_battery_title = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_remain_battery_title, "field 'tv_remain_battery_title'", TextView.class);
        mainActivity.tv_remain_battery_number = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_remain_battery_number, "field 'tv_remain_battery_number'", TextView.class);
        mainActivity.tv_remain_time_title = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_remain_time_title, "field 'tv_remain_time_title'", TextView.class);
        mainActivity.tv_remain_time_number = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_remain_time_number, "field 'tv_remain_time_number'", TextView.class);
        mainActivity.rb_tutorials = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_tutorials, "field 'rb_tutorials'", RadioButton.class);
        mainActivity.iv_start = (ImageView) Utils.findRequiredViewAsType(view, R.id.iv_start, "field 'iv_start'", ImageView.class);
        mainActivity.rb_con_help = (RadioButton) Utils.findRequiredViewAsType(view, R.id.rb_con_help, "field 'rb_con_help'", RadioButton.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        MainActivity mainActivity = this.target;
        if (mainActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        mainActivity.tv_con_state = null;
        mainActivity.tv_remain_battery_title = null;
        mainActivity.tv_remain_battery_number = null;
        mainActivity.tv_remain_time_title = null;
        mainActivity.tv_remain_time_number = null;
        mainActivity.rb_tutorials = null;
        mainActivity.iv_start = null;
        mainActivity.rb_con_help = null;
    }
}