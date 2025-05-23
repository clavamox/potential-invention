package com.wy.ui.activity;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;
import com.yc.video.player.VideoPlayer;

/* loaded from: classes.dex */
public class VedioPreviewActivtiy_ViewBinding implements Unbinder {
    private VedioPreviewActivtiy target;

    public VedioPreviewActivtiy_ViewBinding(VedioPreviewActivtiy vedioPreviewActivtiy) {
        this(vedioPreviewActivtiy, vedioPreviewActivtiy.getWindow().getDecorView());
    }

    public VedioPreviewActivtiy_ViewBinding(VedioPreviewActivtiy vedioPreviewActivtiy, View view) {
        this.target = vedioPreviewActivtiy;
        vedioPreviewActivtiy.mVideoPlayer = (VideoPlayer) Utils.findRequiredViewAsType(view, R.id.video_player, "field 'mVideoPlayer'", VideoPlayer.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        VedioPreviewActivtiy vedioPreviewActivtiy = this.target;
        if (vedioPreviewActivtiy == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        vedioPreviewActivtiy.mVideoPlayer = null;
    }
}