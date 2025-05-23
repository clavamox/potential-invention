package com.wy.ui.activity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import butterknife.BindView;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.xzf.camera.R;
import com.yc.video.player.VideoPlayer;
import com.yc.video.ui.view.BasisVideoController;
import java.io.IOException;

/* loaded from: classes.dex */
public class VedioTutorialActivity extends BaseActivity {
    private static final String TAG = "VedioTutorial";
    BasisVideoController controller;

    @BindView(R.id.video_player)
    VideoPlayer mVideoPlayer;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_vedio_tutorial);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        this.ivNavMenu.setVisibility(4);
        this.tvNavTitle.setVisibility(0);
        this.tvNavTitle.setText(R.string.tutorials);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR);
        this.controller = new BasisVideoController(this);
        try {
            AssetFileDescriptor openFd = getResources().getAssets().openFd("course.mp4");
            this.mVideoPlayer.setController(this.controller);
            this.mVideoPlayer.setAssetFileDescriptor(openFd);
            this.mVideoPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.controller.getTitleView().getmIvBack().setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.activity.VedioTutorialActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VedioTutorialActivity.this.m81lambda$initView$0$comwyuiactivityVedioTutorialActivity(view);
            }
        });
    }

    /* renamed from: lambda$initView$0$com-wy-ui-activity-VedioTutorialActivity, reason: not valid java name */
    /* synthetic */ void m81lambda$initView$0$comwyuiactivityVedioTutorialActivity(View view) {
        this.controller.getBottomView().toggleFullScreen();
    }

    @Override // com.wy.ui.activity.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }
}