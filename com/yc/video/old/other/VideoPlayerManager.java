package com.yc.video.old.other;

import com.yc.video.old.player.OldVideoPlayer;

@Deprecated
/* loaded from: classes.dex */
public final class VideoPlayerManager {
    private static volatile VideoPlayerManager sInstance;
    private OldVideoPlayer mVideoPlayer;

    private VideoPlayerManager() {
    }

    public static VideoPlayerManager instance() {
        if (sInstance == null) {
            synchronized (VideoPlayerManager.class) {
                if (sInstance == null) {
                    sInstance = new VideoPlayerManager();
                }
            }
        }
        return sInstance;
    }

    public OldVideoPlayer getCurrentVideoPlayer() {
        return this.mVideoPlayer;
    }

    public void setCurrentVideoPlayer(OldVideoPlayer oldVideoPlayer) {
        if (this.mVideoPlayer != oldVideoPlayer) {
            releaseVideoPlayer();
            this.mVideoPlayer = oldVideoPlayer;
        }
    }

    public void suspendVideoPlayer() {
        OldVideoPlayer oldVideoPlayer = this.mVideoPlayer;
        if (oldVideoPlayer != null) {
            if (oldVideoPlayer.isPlaying() || this.mVideoPlayer.isBufferingPlaying()) {
                this.mVideoPlayer.pause();
            }
        }
    }

    public void resumeVideoPlayer() {
        OldVideoPlayer oldVideoPlayer = this.mVideoPlayer;
        if (oldVideoPlayer != null) {
            if (oldVideoPlayer.isPaused() || this.mVideoPlayer.isBufferingPaused()) {
                this.mVideoPlayer.restart();
            }
        }
    }

    public void releaseVideoPlayer() {
        OldVideoPlayer oldVideoPlayer = this.mVideoPlayer;
        if (oldVideoPlayer != null) {
            oldVideoPlayer.release();
            this.mVideoPlayer = null;
        }
    }

    public boolean onBackPressed() {
        OldVideoPlayer oldVideoPlayer = this.mVideoPlayer;
        if (oldVideoPlayer == null) {
            return false;
        }
        if (oldVideoPlayer.isFullScreen()) {
            return this.mVideoPlayer.exitFullScreen();
        }
        if (this.mVideoPlayer.isTinyWindow()) {
            return this.mVideoPlayer.exitTinyWindow();
        }
        return false;
    }
}