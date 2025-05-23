package com.wy.ui.activity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import com.belon.camera.BKCameraClient;
import com.wy.CameraApplication;
import com.wy.ui.fragment.CameraBlackHeadFragment;
import com.wy.ui.fragment.CameraEarFragment;
import com.wy.ui.fragment.CameraMouthFragment;
import com.wy.ui.fragment.CameraToothFragment;
import com.wy.ui.fragment.camera.MobileCamera;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class CameraActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CameraActivity";
    CameraBlackHeadFragment fragment_black_head;
    CameraEarFragment fragment_ear;
    CameraMouthFragment fragment_mouth;
    CameraToothFragment fragment_tooth;

    @BindView(R.id.iv_lock)
    ImageView iv_lock;

    @BindView(R.id.iv_mobile_portrait)
    ImageView iv_mobile_portrait;
    String[] titles;

    @BindView(R.id.tv_black_head)
    TextView tv_black_head;

    @BindView(R.id.tv_ear)
    TextView tv_ear;

    @BindView(R.id.tv_mouth)
    TextView tv_mouth;

    @BindView(R.id.tv_tooth)
    TextView tv_tooth;
    private int productTypeIndex = 0;
    private int productItem = 0;

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_camera);
        this.productTypeIndex = getIntent().getIntExtra("productTypeIndex", 0);
        this.productItem = getIntent().getIntExtra("productItem", 0);
        ActivityFinishUtil.addActivity(this);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        this.titles = new String[]{getString(R.string.black_head), getString(R.string.ear), getString(R.string.mouth), getString(R.string.tooth)};
        this.tvNavTitle.setVisibility(4);
        this.fragment_black_head = CameraBlackHeadFragment.newInstance();
        this.fragment_ear = CameraEarFragment.newInstance();
        this.fragment_mouth = CameraMouthFragment.newInstance();
        this.fragment_tooth = CameraToothFragment.newInstance();
        BKCameraClient.getInstance().startConnect(BKCameraClient.CMD_START, null);
        setSelectedDev(this.productTypeIndex);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_lock /* 2131230963 */:
                Log.d(TAG, "lock click");
                break;
            case R.id.iv_mobile_portrait /* 2131230966 */:
                Log.d(TAG, "iv_mobile_portrait click");
                break;
            case R.id.tv_black_head /* 2131231196 */:
                this.productTypeIndex = 0;
                setSelectedDev(0);
                break;
            case R.id.tv_ear /* 2131231209 */:
                this.productTypeIndex = 1;
                setSelectedDev(1);
                break;
            case R.id.tv_mouth /* 2131231216 */:
                this.productTypeIndex = 2;
                setSelectedDev(2);
                break;
            case R.id.tv_tooth /* 2131231237 */:
                this.productTypeIndex = 3;
                setSelectedDev(3);
                break;
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        Log.i(TAG, "进入后台3333 called.");
        finish();
        CameraApplication.back_to_main = true;
        MobileCamera.getInstance().stopPreview();
    }

    @Override // com.wy.ui.activity.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        MobileCamera.getInstance().stopPreview();
        BKCameraClient.getInstance().disConnect(BKCameraClient.CMD_STOP);
    }

    private void setSelectedDev(int i) {
        this.tvNavTitle.setText(this.titles[i]);
        this.tv_black_head.setSelected(false);
        this.tv_ear.setSelected(false);
        this.tv_mouth.setSelected(false);
        this.tv_tooth.setSelected(false);
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        this.fragment_black_head.stopPreviewMobileCamera();
        this.fragment_ear.stopPreviewMobileCamera();
        this.fragment_mouth.stopPreviewMobileCamera();
        this.fragment_tooth.stopPreviewMobileCamera();
        this.iv_mobile_portrait.setVisibility(4);
        this.iv_lock.setVisibility(4);
        this.tv_black_head.setVisibility(4);
        this.tv_ear.setVisibility(4);
        this.tv_mouth.setVisibility(4);
        this.tv_tooth.setVisibility(4);
        if (i == 0) {
            this.fragment_black_head.setCameraCallback();
            this.fragment_black_head.startPreviewMobileCamera();
            this.tv_black_head.setSelected(true);
            beginTransaction.replace(R.id.fragment, this.fragment_black_head).commit();
        }
        if (i == 1) {
            this.fragment_ear.setCameraCallback();
            this.fragment_ear.startPreviewMobileCamera();
            this.tv_ear.setSelected(true);
            beginTransaction.replace(R.id.fragment, this.fragment_ear).commit();
        }
        if (i == 2) {
            this.fragment_mouth.setCameraCallback();
            this.fragment_mouth.startPreviewMobileCamera();
            this.tv_mouth.setSelected(true);
            beginTransaction.replace(R.id.fragment, this.fragment_mouth).commit();
        }
        if (i == 3) {
            this.fragment_tooth.setCameraCallback();
            this.fragment_tooth.startPreviewMobileCamera();
            this.tv_tooth.setSelected(true);
            beginTransaction.replace(R.id.fragment, this.fragment_tooth).commit();
        }
    }
}