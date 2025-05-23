package com.wy.ui.activity;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;
import com.yc.video.player.VideoPlayer;

/* loaded from: classes.dex */
public class VedioTutorialActivity_ViewBinding implements Unbinder {
    private VedioTutorialActivity target;

    public VedioTutorialActivity_ViewBinding(VedioTutorialActivity vedioTutorialActivity) {
        this(vedioTutorialActivity, vedioTutorialActivity.getWindow().getDecorView());
    }

    public VedioTutorialActivity_ViewBinding(VedioTutorialActivity vedioTutorialActivity, View view) {
        this.target = vedioTutorialActivity;
        vedioTutorialActivity.mVideoPlayer = (VideoPlayer) Utils.findRequiredViewAsType(view, R.id.video_player, "field 'mVideoPlayer'", VideoPlayer.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        VedioTutorialActivity vedioTutorialActivity = this.target;
        if (vedioTutorialActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        vedioTutorialActivity.mVideoPlayer = null;
    }
}