package com.yc.video.bridge;

import android.app.Activity;
import android.graphics.Bitmap;
import com.yc.video.controller.InterVideoController;
import com.yc.video.player.InterVideoPlayer;

/* loaded from: classes.dex */
public class ControlWrapper implements InterVideoPlayer, InterVideoController {
    private InterVideoController mController;
    private InterVideoPlayer mVideoPlayer;

    public ControlWrapper(InterVideoPlayer interVideoPlayer, InterVideoController interVideoController) {
        this.mVideoPlayer = interVideoPlayer;
        this.mController = interVideoController;
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void setUrl(String str) {
        this.mVideoPlayer.setUrl(str);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public String getUrl() {
        return this.mVideoPlayer.getUrl();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void start() {
        this.mVideoPlayer.start();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void pause() {
        this.mVideoPlayer.pause();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public long getDuration() {
        return this.mVideoPlayer.getDuration();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public long getCurrentPosition() {
        return this.mVideoPlayer.getCurrentPosition();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void seekTo(long j) {
        this.mVideoPlayer.seekTo(j);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public boolean isPlaying() {
        return this.mVideoPlayer.isPlaying();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public int getBufferedPercentage() {
        return this.mVideoPlayer.getBufferedPercentage();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void startFullScreen() {
        this.mVideoPlayer.startFullScreen();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void stopFullScreen() {
        this.mVideoPlayer.stopFullScreen();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public boolean isFullScreen() {
        return this.mVideoPlayer.isFullScreen();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void setMute(boolean z) {
        this.mVideoPlayer.setMute(z);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public boolean isMute() {
        return this.mVideoPlayer.isMute();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void setScreenScaleType(int i) {
        this.mVideoPlayer.setScreenScaleType(i);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void setSpeed(float f) {
        this.mVideoPlayer.setSpeed(f);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public float getSpeed() {
        return this.mVideoPlayer.getSpeed();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public long getTcpSpeed() {
        return this.mVideoPlayer.getTcpSpeed();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void replay(boolean z) {
        this.mVideoPlayer.replay(z);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void setMirrorRotation(boolean z) {
        this.mVideoPlayer.setMirrorRotation(z);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public Bitmap doScreenShot() {
        return this.mVideoPlayer.doScreenShot();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public int[] getVideoSize() {
        return this.mVideoPlayer.getVideoSize();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void setRotation(float f) {
        this.mVideoPlayer.setRotation(f);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void startTinyScreen() {
        this.mVideoPlayer.startTinyScreen();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void stopTinyScreen() {
        this.mVideoPlayer.stopTinyScreen();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public boolean isTinyScreen() {
        return this.mVideoPlayer.isTinyScreen();
    }

    public void togglePlay() {
        if (isPlaying()) {
            pause();
        } else {
            start();
        }
    }

    public void toggleFullScreen(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (isFullScreen()) {
            activity.setRequestedOrientation(1);
            stopFullScreen();
        } else {
            activity.setRequestedOrientation(0);
            startFullScreen();
        }
    }

    public void toggleFullScreen(Activity activity, boolean z) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (!z) {
            activity.setRequestedOrientation(1);
            stopFullScreen();
        } else {
            activity.setRequestedOrientation(0);
            startFullScreen();
        }
    }

    public void toggleFullScreen() {
        if (isFullScreen()) {
            stopFullScreen();
        } else {
            startFullScreen();
        }
    }

    public void toggleFullScreenByVideoSize(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        int[] videoSize = getVideoSize();
        int i = videoSize[0];
        int i2 = videoSize[1];
        if (isFullScreen()) {
            stopFullScreen();
            if (i > i2) {
                activity.setRequestedOrientation(1);
                return;
            }
            return;
        }
        startFullScreen();
        if (i > i2) {
            activity.setRequestedOrientation(0);
        }
    }

    @Override // com.yc.video.controller.InterVideoController
    public void startFadeOut() {
        this.mController.startFadeOut();
    }

    @Override // com.yc.video.controller.InterVideoController
    public void stopFadeOut() {
        this.mController.stopFadeOut();
    }

    @Override // com.yc.video.controller.InterVideoController
    public boolean isShowing() {
        return this.mController.isShowing();
    }

    @Override // com.yc.video.controller.InterVideoController
    public void setLocked(boolean z) {
        this.mController.setLocked(z);
    }

    @Override // com.yc.video.controller.InterVideoController
    public boolean isLocked() {
        return this.mController.isLocked();
    }

    @Override // com.yc.video.controller.InterVideoController
    public void startProgress() {
        this.mController.startProgress();
    }

    @Override // com.yc.video.controller.InterVideoController
    public void stopProgress() {
        this.mController.stopProgress();
    }

    @Override // com.yc.video.controller.InterVideoController
    public void hide() {
        this.mController.hide();
    }

    @Override // com.yc.video.controller.InterVideoController
    public void show() {
        this.mController.show();
    }

    @Override // com.yc.video.controller.InterVideoController
    public boolean hasCutout() {
        return this.mController.hasCutout();
    }

    @Override // com.yc.video.controller.InterVideoController
    public int getCutoutHeight() {
        return this.mController.getCutoutHeight();
    }

    @Override // com.yc.video.controller.InterVideoController
    public void destroy() {
        this.mController.destroy();
    }

    public void toggleLockState() {
        setLocked(!isLocked());
    }

    public void toggleShowState() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }
}