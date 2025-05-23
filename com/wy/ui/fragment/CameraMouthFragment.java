package com.wy.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.wy.ui.view.CameraSegmentControl;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class CameraMouthFragment extends CameraBaseFragment implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "CameraMouthFragment";

    @BindView(R.id.cb_compare)
    RadioButton cb_compare;

    @BindView(R.id.iv_camera)
    ImageView iv_camera;

    @BindView(R.id.segment)
    CameraSegmentControl segment;

    @BindView(R.id.tv_picture)
    TextView tv_picture;

    @BindView(R.id.tv_vedio)
    TextView tv_vedio;
    private boolean is_luxiang = false;
    private boolean now_v_or_p = false;
    private Drawable drawable = null;

    public static CameraMouthFragment newInstance() {
        return new CameraMouthFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_camera_mouth, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.gyroscopeStat = false;
        this.this_device = 3;
        return inflate;
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.segment.setTitles(new String[]{getString(R.string.level), getString(R.string.wide_angel), getString(R.string.mirroring)});
        this.segment.setOnCameraSegmentListener(new CameraSegmentControl.OnCameraSegmentListener() { // from class: com.wy.ui.fragment.CameraMouthFragment.1
            @Override // com.wy.ui.view.CameraSegmentControl.OnCameraSegmentListener
            public void onGyroscopeState(boolean z) {
                Log.d(CameraMouthFragment.TAG, getClass().getSimpleName() + " 陀螺仪状态:" + z);
                CameraMouthFragment.this.gyroscopeStat = z;
            }

            @Override // com.wy.ui.view.CameraSegmentControl.OnCameraSegmentListener
            public void onMirrorState(boolean z) {
                Log.d(CameraMouthFragment.TAG, getClass().getSimpleName() + " 镜像状态:" + z);
                if (CameraMouthFragment.this.mirrorState) {
                    CameraMouthFragment.this.mirrorState = false;
                } else {
                    CameraMouthFragment.this.mirrorState = true;
                }
            }
        });
        this.mirrorState = true;
        this.segment.setGyroscopeState(this.gyroscopeStat);
        this.segment.setMirrorState(this.mirrorState);
        this.iv_camera.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.fragment.CameraMouthFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                CameraMouthFragment.this.onClick(view2);
            }
        });
        this.tv_vedio.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.fragment.CameraMouthFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                CameraMouthFragment.this.onClick(view2);
            }
        });
        this.tv_picture.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.fragment.CameraMouthFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                CameraMouthFragment.this.onClick(view2);
            }
        });
        this.cb_compare.setOnCheckedChangeListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_camera) {
            if (this.now_v_or_p) {
                if (!this.isVedio) {
                    startRecordVedio();
                } else {
                    stopRecordVedio();
                }
            } else {
                startCapturePicture();
            }
            Log.d(TAG, "iv_camera onclick");
            return;
        }
        if (id == R.id.tv_picture) {
            this.now_v_or_p = false;
            this.iv_camera.setImageResource(R.mipmap.camera_photo);
            Log.d(TAG, "tv_picture onclick");
        } else {
            if (id != R.id.tv_vedio) {
                return;
            }
            this.now_v_or_p = true;
            this.iv_camera.setImageResource(R.mipmap.vedio_record);
            Log.d(TAG, "tv_vedio onclick");
        }
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment, android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        super.onCheckedChanged(compoundButton, z);
        if (compoundButton.getId() != R.id.cb_compare) {
            return;
        }
        this.drawable = getResources().getDrawable(R.mipmap.change_screen_a);
        this.rb_screen_change.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.drawable, (Drawable) null, (Drawable) null);
        if (!this.is_duibi) {
            this.drawable = getResources().getDrawable(R.mipmap.compare);
            this.is_duibi = true;
        } else {
            this.drawable = getResources().getDrawable(R.mipmap.compare_a);
            this.is_duibi = false;
        }
        this.cb_compare.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.drawable, (Drawable) null, (Drawable) null);
        Log.d(TAG, "cb_compare onclick");
        startComparePicture();
    }
}