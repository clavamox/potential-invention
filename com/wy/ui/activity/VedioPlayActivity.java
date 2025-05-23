package com.wy.ui.activity;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import com.xzf.camera.R;
import com.yc.video.player.VideoPlayer;
import com.yc.video.ui.view.BasisVideoController;
import com.yc.video.ui.view.CustomCompleteView;
import com.yc.video.ui.view.InterControlView;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;

/* loaded from: classes.dex */
public class VedioPlayActivity extends BaseActivity {

    @BindView(R.id.iv_nav_left)
    ImageView ivBack;

    @BindView(R.id.video_player)
    VideoPlayer mVideoPlayer;

    @BindView(R.id.tv_nav_title)
    TextView tvTitle;
    String vedioPath;

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_vedio_play);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        this.tvTitle.setVisibility(4);
        this.ivNavMenu.setVisibility(4);
        this.vedioPath = getIntent().getStringExtra("vedioPath");
        BasisVideoController basisVideoController = new BasisVideoController(this);
        this.mVideoPlayer.setController(basisVideoController);
        this.mVideoPlayer.setUrl(this.vedioPath);
        this.mVideoPlayer.setScreenScaleType(1);
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
}