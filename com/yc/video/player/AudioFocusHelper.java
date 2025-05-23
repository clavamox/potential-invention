package com.yc.video.player;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
    private AudioManager mAudioManager;
    private WeakReference<VideoPlayer> mWeakVideoView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mStartRequested = false;
    private boolean mPausedForLoss = false;
    private int mCurrentFocus = 0;

    public AudioFocusHelper(VideoPlayer videoPlayer) {
        this.mWeakVideoView = new WeakReference<>(videoPlayer);
        this.mAudioManager = (AudioManager) videoPlayer.getContext().getApplicationContext().getSystemService("audio");
    }

    @Override // android.media.AudioManager.OnAudioFocusChangeListener
    public void onAudioFocusChange(final int i) {
        if (this.mCurrentFocus == i) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.yc.video.player.AudioFocusHelper.1
            @Override // java.lang.Runnable
            public void run() {
                AudioFocusHelper.this.handleAudioFocusChange(i);
            }
        });
        this.mCurrentFocus = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAudioFocusChange(int i) {
        VideoPlayer videoPlayer = this.mWeakVideoView.get();
        if (videoPlayer == null) {
            return;
        }
        if (i == -3) {
            if (!videoPlayer.isPlaying() || videoPlayer.isMute()) {
                return;
            }
            videoPlayer.setVolume(0.1f, 0.1f);
            return;
        }
        if (i == -2 || i == -1) {
            if (videoPlayer.isPlaying()) {
                this.mPausedForLoss = true;
                videoPlayer.pause();
                return;
            }
            return;
        }
        if (i == 1 || i == 2) {
            if (this.mStartRequested || this.mPausedForLoss) {
                videoPlayer.start();
                this.mStartRequested = false;
                this.mPausedForLoss = false;
            }
            if (videoPlayer.isMute()) {
                return;
            }
            videoPlayer.setVolume(1.0f, 1.0f);
        }
    }

    public void requestFocus() {
        AudioManager audioManager;
        if (this.mCurrentFocus == 1 || (audioManager = this.mAudioManager) == null) {
            return;
        }
        if (1 == audioManager.requestAudioFocus(this, 3, 1)) {
            this.mCurrentFocus = 1;
        } else {
            this.mStartRequested = true;
        }
    }

    public void abandonFocus() {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager == null) {
            return;
        }
        this.mStartRequested = false;
        audioManager.abandonAudioFocus(this);
    }

    public void release() {
        abandonFocus();
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            this.mHandler = null;
        }
    }
}