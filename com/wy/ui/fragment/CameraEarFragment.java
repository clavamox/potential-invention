package com.wy.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.wy.ui.view.SegmentControl;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class CameraEarFragment extends CameraBaseFragment implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "CameraEarFragment";

    @BindView(R.id.rb_compare)
    RadioButton rb_compare;

    @BindView(R.id.rb_ear)
    RadioButton rb_ear;

    @BindView(R.id.rb_vedio)
    RadioButton rb_vedio;

    @BindView(R.id.rv_photo)
    RadioButton rv_photo;

    @BindView(R.id.segment)
    SegmentControl segment;
    private boolean is_luxiang = false;
    private Drawable drawable = null;
    private int segmentControlPos = 0;

    public static CameraEarFragment newInstance() {
        return new CameraEarFragment();
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
                startComparePicture();
                Log.d(TAG, "rb_compare click");
                break;
            case R.id.rb_ear /* 2131231082 */:
                Log.d(TAG, "rb_ear click");
                updateEarRight(!this.earRight);
                break;
            case R.id.rb_vedio /* 2131231087 */:
                if (!this.is_luxiang) {
                    this.drawable = getResources().getDrawable(R.mipmap.vedio_record);
                    this.is_luxiang = true;
                } else {
                    this.drawable = getResources().getDrawable(R.mipmap.vedio_record_a);
                    this.is_luxiang = false;
                }
                this.rb_vedio.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.drawable, (Drawable) null, (Drawable) null);
                Log.d(TAG, "rb_vedio click");
                if (!this.isVedio) {
                    startRecordVedio();
                    break;
                } else {
                    stopRecordVedio();
                    break;
                }
            case R.id.rv_photo /* 2131231100 */:
                Log.d(TAG, "rv_photo click");
                startCapturePicture();
                break;
        }
    }

    public void onClick(View view) {
        if (view.getId() != R.id.iv_camera) {
            return;
        }
        Log.d(TAG, "iv_camera click");
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.this_device = 2;
        View inflate = layoutInflater.inflate(R.layout.fragment_camera_ear, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        return inflate;
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment, androidx.fragment.app.Fragment
    public void onPause() {
        Log.i(TAG, "进入后台 called.");
        super.onPause();
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.segment.setTitles(new String[]{getString(R.string.level_model), getString(R.string.dynamic_model)});
        this.segment.setOnSegmentListener(new SegmentControl.OnSegmentListener() { // from class: com.wy.ui.fragment.CameraEarFragment.1
            @Override // com.wy.ui.view.SegmentControl.OnSegmentListener
            public void onMirrorState(boolean z) {
                Log.d(CameraEarFragment.TAG, getClass().getSimpleName() + " 镜像状态:" + z);
            }

            @Override // com.wy.ui.view.SegmentControl.OnSegmentListener
            public void setOnSegment(View view2, int i) {
                Log.d(CameraEarFragment.TAG, getClass().getSimpleName() + " ==>position:" + i);
                CameraEarFragment.this.segmentControlPos = i;
                CameraEarFragment cameraEarFragment = CameraEarFragment.this;
                cameraEarFragment.setMode(cameraEarFragment.segmentControlPos);
            }
        });
        this.segment.setSelectPosition(this.segmentControlPos);
        setMode(this.segmentControlPos);
        updateEarRight(this.earRight);
        this.rb_compare.setOnCheckedChangeListener(this);
        this.rv_photo.setOnCheckedChangeListener(this);
        this.rb_vedio.setOnCheckedChangeListener(this);
        this.rb_ear.setOnCheckedChangeListener(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMode(int i) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) this.iv_dev_camera.getLayoutParams();
        ViewGroup.LayoutParams layoutParams2 = this.ll_dev_camera.getLayoutParams();
        if (i == 0) {
            this.iv_dev_camera.setCircleAngle(true);
            this.iv_dev_camera.setRotate(true);
            this.gyroscopeStat = true;
            if (layoutParams2.height == -1) {
                layoutParams.dimensionRatio = "h,1:1";
            } else {
                layoutParams.dimensionRatio = "w,1:1";
            }
        } else if (i == 1) {
            this.iv_dev_camera.setCircleAngle(false);
            this.iv_dev_camera.setRotate(false);
            this.iv_dev_camera.setRotateAngle(0);
            this.gyroscopeStat = false;
            layoutParams.dimensionRatio = "";
        }
        this.iv_dev_camera.setLayoutParams(layoutParams);
        this.iv_dev_camera.reDrawBitmap();
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment
    protected void startComparePicture() {
        if (this.cmpBitmap == null) {
            Toast.makeText(getContext(), getString(R.string.pls_take_pictrue), 0).show();
            return;
        }
        ViewGroup.LayoutParams layoutParams = this.ll_dev_camera.getLayoutParams();
        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) this.iv_dev_camera.getLayoutParams();
        if (this.segmentControlPos == 0) {
            if (layoutParams.height == -1) {
                layoutParams.height = 0;
                this.ll_dev_camera.setLayoutParams(layoutParams);
                this.ivCompare.bringToFront();
                this.ivCompare.setImageBitmap(this.cmpBitmap);
                layoutParams2.dimensionRatio = "w,1:1";
            } else {
                layoutParams.height = -1;
                this.ll_dev_camera.setLayoutParams(layoutParams);
                layoutParams2.dimensionRatio = "h,1:1";
            }
        } else {
            if (layoutParams.height == -1) {
                layoutParams.height = 0;
                this.ll_dev_camera.setLayoutParams(layoutParams);
                this.ivCompare.bringToFront();
                this.ivCompare.setImageBitmap(this.cmpBitmap);
            } else {
                layoutParams.height = -1;
                this.ll_dev_camera.setLayoutParams(layoutParams);
            }
            layoutParams2.dimensionRatio = "";
        }
        this.iv_dev_camera.setLayoutParams(layoutParams2);
        this.iv_dev_camera.reDrawBitmap();
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment
    protected void startChangeScreen() {
        ViewGroup.LayoutParams layoutParams = this.ll_dev_camera.getLayoutParams();
        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) this.iv_dev_camera.getLayoutParams();
        if (this.segmentControlPos == 0) {
            if (layoutParams.height == -1) {
                if (!this.isPreview) {
                    startPreviewMobileCamera();
                    layoutParams.height = 0;
                    this.iv_mobile_camera.bringToFront();
                    layoutParams2.dimensionRatio = "w,1:1";
                }
            } else {
                layoutParams.height = -1;
                stopPreviewMobileCamera();
                layoutParams2.dimensionRatio = "h,1:1";
            }
        } else {
            if (layoutParams.height == -1) {
                if (!this.isPreview) {
                    startPreviewMobileCamera();
                    layoutParams.height = 0;
                    this.iv_mobile_camera.bringToFront();
                }
            } else {
                layoutParams.height = -1;
                stopPreviewMobileCamera();
            }
            layoutParams2.dimensionRatio = "";
        }
        this.ll_dev_camera.setLayoutParams(layoutParams);
        this.iv_dev_camera.setLayoutParams(layoutParams2);
        this.iv_dev_camera.reDrawBitmap();
    }

    private void updateEarRight(boolean z) {
        this.earRight = z;
        if (this.earRight) {
            this.rb_ear.setText(R.string.right_ear);
            Drawable drawable = getResources().getDrawable(R.mipmap.ear_right);
            drawable.setBounds(this.rb_ear.getCompoundDrawables()[1].getBounds());
            this.rb_ear.setCompoundDrawables(null, drawable, null, null);
            return;
        }
        this.rb_ear.setText(R.string.left_ear);
        Drawable drawable2 = getResources().getDrawable(R.mipmap.ear_left);
        drawable2.setBounds(this.rb_ear.getCompoundDrawables()[1].getBounds());
        this.rb_ear.setCompoundDrawables(null, drawable2, null, null);
    }
}