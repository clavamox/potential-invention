package com.wy.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.fragment.app.DialogFragment;
import com.wy.util.PixelUtil;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class WiFiSettingFragment extends DialogFragment implements View.OnClickListener {
    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.fragment_wifi_setting);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.AnimBottom);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = 80;
        attributes.width = -1;
        attributes.height = PixelUtil.dip2px(getActivity(), 310.0f);
        window.setAttributes(attributes);
        initView(dialog);
        if (getDialog() != null) {
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.wy.ui.fragment.WiFiSettingFragment.1
                @Override // android.content.DialogInterface.OnKeyListener
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    return false;
                }
            });
        }
        return dialog;
    }

    private void initView(Dialog dialog) {
        dialog.findViewById(R.id.rb_setting).setOnClickListener(this);
        dialog.findViewById(R.id.rb_wlan).setOnClickListener(this);
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rb_setting) {
            Log.d("WiFiSettingFragment", "rb_setting...");
            startActivity(new Intent("android.settings.SETTINGS"));
            dismiss();
        } else if (id != R.id.rb_wlan) {
            if (id != R.id.tv_cancel) {
                return;
            }
            dismiss();
        } else {
            Log.d("WiFiSettingFragment", "rb_wlan...");
            startActivity(new Intent("android.settings.WIFI_SETTINGS"));
            dismiss();
        }
    }
}