package com.yc.video.ui.pip;

import android.content.Context;
import com.yc.video.player.VideoPlayer;
import com.yc.video.player.VideoViewManager;
import com.yc.video.tool.PlayerUtils;

/* loaded from: classes.dex */
public class FloatVideoManager {
    public static final String PIP = "pip";
    private static FloatVideoManager instance;
    private Class mActClass;
    private CustomFloatController mFloatController;
    private FloatVideoView mFloatView;
    private boolean mIsShowing;
    private int mPlayingPosition = -1;
    private VideoPlayer mVideoPlayer;

    private FloatVideoManager(Context context) {
        this.mVideoPlayer = new VideoPlayer(context);
        VideoViewManager.instance().add(this.mVideoPlayer, PIP);
        this.mFloatController = new CustomFloatController(context);
        this.mFloatView = new FloatVideoView(context, 0, 0);
    }

    public static FloatVideoManager getInstance(Context context) {
        if (instance == null) {
            synchronized (FloatVideoManager.class) {
                if (instance == null) {
                    instance = new FloatVideoManager(context);
                }
            }
        }
        return instance;
    }

    public void startFloatWindow() {
        if (this.mIsShowing) {
            return;
        }
        PlayerUtils.removeViewFormParent(this.mVideoPlayer);
        this.mVideoPlayer.setController(this.mFloatController);
        this.mFloatController.setPlayState(this.mVideoPlayer.getCurrentPlayState());
        this.mFloatController.setPlayerState(this.mVideoPlayer.getCurrentPlayerState());
        this.mFloatView.addView(this.mVideoPlayer);
        this.mFloatView.addToWindow();
        this.mIsShowing = true;
    }

    public void stopFloatWindow() {
        if (this.mIsShowing) {
            this.mFloatView.removeFromWindow();
            PlayerUtils.removeViewFormParent(this.mVideoPlayer);
            this.mIsShowing = false;
        }
    }

    public void setPlayingPosition(int i) {
        this.mPlayingPosition = i;
    }

    public int getPlayingPosition() {
        return this.mPlayingPosition;
    }

    public void pause() {
        if (this.mIsShowing) {
            return;
        }
        this.mVideoPlayer.pause();
    }

    public void resume() {
        if (this.mIsShowing) {
            return;
        }
        this.mVideoPlayer.resume();
    }

    public void reset() {
        if (this.mIsShowing) {
            return;
        }
        PlayerUtils.removeViewFormParent(this.mVideoPlayer);
        this.mVideoPlayer.release();
        this.mVideoPlayer.setController(null);
        this.mPlayingPosition = -1;
        this.mActClass = null;
    }

    public boolean onBackPress() {
        return !this.mIsShowing && this.mVideoPlayer.onBackPressed();
    }

    public boolean isStartFloatWindow() {
        return this.mIsShowing;
    }

    public void setFloatViewVisible() {
        if (this.mIsShowing) {
            this.mVideoPlayer.resume();
            this.mFloatView.setVisibility(0);
        }
    }

    public void setActClass(Class cls) {
        this.mActClass = cls;
    }

    public Class getActClass() {
        return this.mActClass;
    }
}