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
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.wy.ui.view.SegmentControl;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class CameraToothFragment extends CameraBaseFragment implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "CameraToothFragment";

    @BindView(R.id.cb_compare)
    RadioButton cb_compare;

    @BindView(R.id.iv_camera)
    ImageView iv_camera;

    @BindView(R.id.segment)
    SegmentControl segment;

    @BindView(R.id.tv_picture)
    TextView tv_picture;

    @BindView(R.id.tv_vedio)
    TextView tv_vedio;
    private boolean is_luxiang = false;
    private boolean now_v_or_p = false;
    private Drawable drawable = null;
    private int segmentControlPos = 0;

    public CameraToothFragment() {
        Log.d(TAG, "CameraToothFragment init");
    }

    public static CameraToothFragment newInstance() {
        return new CameraToothFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mirrorState = false;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_camera_tooth, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        Log.d(TAG, "onCreateView");
        this.gyroscopeStat = true;
        this.this_device = 4;
        return inflate;
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.segment.setTitles(new String[]{getString(R.string.level), getString(R.string.wide_angel), getString(R.string.mirroring)});
        this.segment.setOnSegmentListener(new SegmentControl.OnSegmentListener() { // from class: com.wy.ui.fragment.CameraToothFragment.1
            @Override // com.wy.ui.view.SegmentControl.OnSegmentListener
            public void onMirrorState(boolean z) {
                Log.d(CameraToothFragment.TAG, getClass().getSimpleName() + " 镜像状态:" + z);
                if (CameraToothFragment.this.mirrorState) {
                    CameraToothFragment.this.mirrorState = false;
                } else {
                    CameraToothFragment.this.mirrorState = true;
                }
            }

            @Override // com.wy.ui.view.SegmentControl.OnSegmentListener
            public void setOnSegment(View view2, int i) {
                Log.d(CameraToothFragment.TAG, getClass().getSimpleName() + " ==>position:" + i);
                CameraToothFragment.this.segmentControlPos = i;
                CameraToothFragment cameraToothFragment = CameraToothFragment.this;
                cameraToothFragment.setMode(cameraToothFragment.segmentControlPos);
            }
        });
        this.segment.setGyroscopeState(this.gyroscopeStat);
        this.segment.setMirrorState(this.mirrorState);
        this.iv_camera.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.fragment.CameraToothFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                CameraToothFragment.this.onClick(view2);
            }
        });
        this.tv_vedio.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.fragment.CameraToothFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                CameraToothFragment.this.onClick(view2);
            }
        });
        this.tv_picture.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.fragment.CameraToothFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                CameraToothFragment.this.onClick(view2);
            }
        });
        this.cb_compare.setOnCheckedChangeListener(this);
        this.segment.setSelectPosition(this.segmentControlPos);
        setMode(this.segmentControlPos);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMode(int i) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) this.iv_dev_camera.getLayoutParams();
        ViewGroup.LayoutParams layoutParams2 = this.ll_dev_camera.getLayoutParams();
        if (i == 0) {
            this.iv_dev_camera.setCircleAngle(true);
            this.iv_dev_camera.setRotate(true);
            this.gyroscopeStat = true;
            this.mirrorState = !this.mirrorState;
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
            this.mirrorState = !this.mirrorState;
            layoutParams.dimensionRatio = "";
        }
        this.iv_dev_camera.setLayoutParams(layoutParams);
        this.iv_dev_camera.reDrawBitmap();
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_camera) {
            Log.d(TAG, "iv_camera onclick");
            if (this.now_v_or_p) {
                if (!this.isVedio) {
                    startRecordVedio();
                    return;
                } else {
                    stopRecordVedio();
                    return;
                }
            }
            startCapturePicture();
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
                this.is_duibi = true;
            } else {
                layoutParams.height = -1;
                this.ll_dev_camera.setLayoutParams(layoutParams);
                layoutParams2.dimensionRatio = "h,1:1";
                this.is_duibi = false;
            }
        } else {
            if (layoutParams.height == -1) {
                layoutParams.height = 0;
                this.ll_dev_camera.setLayoutParams(layoutParams);
                this.ivCompare.bringToFront();
                this.ivCompare.setImageBitmap(this.cmpBitmap);
                this.is_duibi = true;
            } else {
                layoutParams.height = -1;
                this.ll_dev_camera.setLayoutParams(layoutParams);
                this.is_duibi = false;
            }
            layoutParams2.dimensionRatio = "";
        }
        this.iv_dev_camera.setLayoutParams(layoutParams2);
        this.iv_dev_camera.reDrawBitmap();
    }

    @Override // com.wy.ui.fragment.CameraBaseFragment, android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        super.onCheckedChanged(compoundButton, z);
        if (compoundButton.getId() != R.id.cb_compare) {
            return;
        }
        if (this.cmpBitmap == null) {
            Toast.makeText(getContext(), getString(R.string.pls_take_pictrue), 0).show();
            return;
        }
        this.drawable = getResources().getDrawable(R.mipmap.change_screen_a);
        this.rb_screen_change.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.drawable, (Drawable) null, (Drawable) null);
        if (!this.is_duibi) {
            this.drawable = getResources().getDrawable(R.mipmap.compare);
        } else {
            this.drawable = getResources().getDrawable(R.mipmap.compare_a);
        }
        this.cb_compare.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.drawable, (Drawable) null, (Drawable) null);
        startComparePicture();
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
}