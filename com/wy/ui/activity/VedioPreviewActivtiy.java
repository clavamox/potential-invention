package com.wy.ui.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.BindView;
import com.xzf.camera.R;
import com.yc.video.player.VideoPlayer;
import com.yc.video.ui.view.BasisVideoController;
import com.yc.video.ui.view.CustomCompleteView;
import com.yc.video.ui.view.InterControlView;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;

/* loaded from: classes.dex */
public class VedioPreviewActivtiy extends BaseActivity implements View.OnClickListener {
    private String filePath = "";

    @BindView(R.id.video_player)
    VideoPlayer mVideoPlayer;

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_vedio_preview_activtiy);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        this.ivNavMenu.setVisibility(4);
        this.tvNavTitle.setVisibility(4);
        this.filePath = getIntent().getStringExtra("filePath");
        BasisVideoController basisVideoController = new BasisVideoController(this);
        this.mVideoPlayer.setController(basisVideoController);
        this.mVideoPlayer.setUrl(this.filePath);
        this.mVideoPlayer.start();
        hideShare(basisVideoController);
    }

    private void hideShare(BasisVideoController basisVideoController) {
        CustomCompleteView customCompleteView;
        try {
            Field declaredField = basisVideoController.getClass().getSuperclass().getSuperclass().getDeclaredField("mControlComponents");
            declaredField.setAccessible(true);
            Iterator it = ((LinkedHashMap) declaredField.get(basisVideoController)).keySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    customCompleteView = null;
                    break;
                }
                InterControlView interControlView = (InterControlView) it.next();
                if (interControlView instanceof CustomCompleteView) {
                    customCompleteView = (CustomCompleteView) interControlView;
                    break;
                }
            }
            if (customCompleteView != null) {
                Field declaredField2 = customCompleteView.getClass().getDeclaredField("mLlShare");
                declaredField2.setAccessible(true);
                ((LinearLayout) declaredField2.get(customCompleteView)).setVisibility(8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() != R.id.iv_delete) {
            return;
        }
        try {
            new File(this.filePath).delete();
            Toast.makeText(this, getString(R.string.opt_success), 0).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), 0).show();
        }
    }
}