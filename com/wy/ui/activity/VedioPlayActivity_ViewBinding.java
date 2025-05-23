package com.wy.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;
import com.yc.video.player.VideoPlayer;

/* loaded from: classes.dex */
public class VedioPlayActivity_ViewBinding implements Unbinder {
    private VedioPlayActivity target;

    public VedioPlayActivity_ViewBinding(VedioPlayActivity vedioPlayActivity) {
        this(vedioPlayActivity, vedioPlayActivity.getWindow().getDecorView());
    }

    public VedioPlayActivity_ViewBinding(VedioPlayActivity vedioPlayActivity, View view) {
        this.target = vedioPlayActivity;
        vedioPlayActivity.mVideoPlayer = (VideoPlayer) Utils.findRequiredViewAsType(view, R.id.video_player, "field 'mVideoPlayer'", VideoPlayer.class);
        vedioPlayActivity.ivBack = (ImageView) Utils.findRequiredViewAsType(view, R.id.iv_nav_left, "field 'ivBack'", ImageView.class);
        vedioPlayActivity.tvTitle = (TextView) Utils.findRequiredViewAsType(view, R.id.tv_nav_title, "field 'tvTitle'", TextView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        VedioPlayActivity vedioPlayActivity = this.target;
        if (vedioPlayActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        vedioPlayActivity.mVideoPlayer = null;
        vedioPlayActivity.ivBack = null;
        vedioPlayActivity.tvTitle = null;
    }
}