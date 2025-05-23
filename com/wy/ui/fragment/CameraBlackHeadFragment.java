package com.wy.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.wy.ui.activity.TutorialsActivity;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class CameraBlackHeadFragment extends CameraBaseFragment implements View.OnClickListener {
    private static final String TAG = "BlackHeadFragmeng";

    @BindView(R.id.rb_compare)
    RadioButton rb_compare;

    @BindView(R.id.rb_take_picture)
    RadioButton rb_take_picture;

    @BindView(R.id.rb_tutorials)
    RadioButton rb_tutorials;

    @BindView(R.id.rb_vedio_record)
    RadioButton rb_vedio_record;
    private boolean is_luxiang = false;
    private Drawable drawable = null;

    public static CameraBlackHeadFragment newInstance() {
        return new CameraBlackHeadFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_camera_black_head, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.gyroscopeStat = false;
        this.this_device = 1;
        return inflate;
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.rb_vedio_record.setOnCheckedChangeListener(this);
        this.rb_take_picture.setOnCheckedChangeListener(this);
        this.rb_tutorials.setOnCheckedChangeListener(this);
        this.rb_compare.setOnCheckedChangeListener(this);
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment, android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        super.onCheckedChanged(compoundButton, z);
        switch (compoundButton.getId()) {
            case R.id.rb_compare /* 2131231080 */:
                this.drawable = getResources().getDrawable(R.mipmap.change_screen_a);
                this.rb_screen_change.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.drawable, (Drawable) null, (Drawable) null);
                if (!this.is_duibi) {
                    this.drawable = getResources().getDrawable(R.mipmap.compare);
                    this.is_duibi = true;
                } else {
                    this.drawable = getResources().getDrawable(R.mipmap.compare_a);
                    this.is_duibi = false;
                }
                this.rb_compare.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.drawable, (Drawable) null, (Drawable) null);
                Log.d(TAG, "rb_compare");
                startComparePicture();
                break;
            case R.id.rb_take_picture /* 2131231085 */:
                startCapturePicture();
                break;
            case R.id.rb_tutorials /* 2131231086 */:
                startActivity(new Intent(getContext(), (Class<?>) TutorialsActivity.class));
                break;
            case R.id.rb_vedio_record /* 2131231088 */:
                if (!this.is_luxiang) {
                    this.drawable = getResources().getDrawable(R.mipmap.vedio_record);
                    this.is_luxiang = true;
                } else {
                    this.drawable = getResources().getDrawable(R.mipmap.vedio_record_a);
                    this.is_luxiang = false;
                }
                this.rb_vedio_record.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.drawable, (Drawable) null, (Drawable) null);
                if (!this.isVedio) {
                    startRecordVedio();
                    break;
                } else {
                    stopRecordVedio();
                    break;
                }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        view.getId();
    }
}