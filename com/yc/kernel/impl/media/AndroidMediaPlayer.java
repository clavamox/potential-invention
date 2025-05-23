package com.yc.kernel.impl.media;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.yc.kernel.inter.AbstractVideoPlayer;
import com.yc.kernel.inter.VideoPlayerListener;
import java.util.Map;

/* loaded from: classes.dex */
public class AndroidMediaPlayer extends AbstractVideoPlayer {
    private Context mAppContext;
    private int mBufferedPercent;
    private boolean mIsPreparing;
    protected MediaPlayer mMediaPlayer;
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() { // from class: com.yc.kernel.impl.media.AndroidMediaPlayer.2
        @Override // android.media.MediaPlayer.OnErrorListener
        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
            AndroidMediaPlayer.this.mPlayerEventListener.onError(3, "监听异常" + i + ", extra: " + i2);
            return true;
        }
    };
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: com.yc.kernel.impl.media.AndroidMediaPlayer.3
        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mediaPlayer) {
            AndroidMediaPlayer.this.mPlayerEventListener.onCompletion();
        }
    };
    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() { // from class: com.yc.kernel.impl.media.AndroidMediaPlayer.4
        @Override // android.media.MediaPlayer.OnInfoListener
        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
            if (i != 3) {
                AndroidMediaPlayer.this.mPlayerEventListener.onInfo(i, i2);
                return true;
            }
            if (!AndroidMediaPlayer.this.mIsPreparing) {
                return true;
            }
            AndroidMediaPlayer.this.mPlayerEventListener.onInfo(i, i2);
            AndroidMediaPlayer.this.mIsPreparing = false;
            return true;
        }
    };
    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() { // from class: com.yc.kernel.impl.media.AndroidMediaPlayer.5
        @Override // android.media.MediaPlayer.OnBufferingUpdateListener
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            AndroidMediaPlayer.this.mBufferedPercent = i;
        }
    };
    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: com.yc.kernel.impl.media.AndroidMediaPlayer.6
        @Override // android.media.MediaPlayer.OnPreparedListener
        public void onPrepared(MediaPlayer mediaPlayer) {
            AndroidMediaPlayer.this.mPlayerEventListener.onPrepared();
            AndroidMediaPlayer.this.start();
        }
    };
    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() { // from class: com.yc.kernel.impl.media.AndroidMediaPlayer.7
        @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
            int videoWidth = mediaPlayer.getVideoWidth();
            int videoHeight = mediaPlayer.getVideoHeight();
            if (videoWidth == 0 || videoHeight == 0) {
                return;
            }
            AndroidMediaPlayer.this.mPlayerEventListener.onVideoSizeChanged(videoWidth, videoHeight);
        }
    };

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public long getTcpSpeed() {
        return 0L;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setOptions() {
    }

    public AndroidMediaPlayer(Context context) {
        if (context instanceof Application) {
            this.mAppContext = context;
        } else {
            this.mAppContext = context.getApplicationContext();
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void initPlayer() {
        this.mMediaPlayer = new MediaPlayer();
        setOptions();
        initListener();
    }

    private void initListener() {
        this.mMediaPlayer.setAudioStreamType(3);
        this.mMediaPlayer.setOnErrorListener(this.onErrorListener);
        this.mMediaPlayer.setOnCompletionListener(this.onCompletionListener);
        this.mMediaPlayer.setOnInfoListener(this.onInfoListener);
        this.mMediaPlayer.setOnBufferingUpdateListener(this.onBufferingUpdateListener);
        this.mMediaPlayer.setOnPreparedListener(this.onPreparedListener);
        this.mMediaPlayer.setOnVideoSizeChangedListener(this.onVideoSizeChangedListener);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setDataSource(String str, Map<String, String> map) {
        if (str == null || str.length() == 0) {
            if (this.mPlayerEventListener != null) {
                this.mPlayerEventListener.onInfo(-1, 0);
            }
        } else {
            try {
                this.mMediaPlayer.setDataSource(this.mAppContext, Uri.parse(str), map);
            } catch (Exception e) {
                this.mPlayerEventListener.onError(2, e.getMessage());
            }
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setDataSource(AssetFileDescriptor assetFileDescriptor) {
        try {
            this.mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
        } catch (Exception e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void start() {
        try {
            this.mMediaPlayer.start();
        } catch (IllegalStateException e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void pause() {
        try {
            this.mMediaPlayer.pause();
        } catch (IllegalStateException e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void stop() {
        try {
            this.mMediaPlayer.stop();
        } catch (IllegalStateException e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void prepareAsync() {
        try {
            this.mIsPreparing = true;
            this.mMediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void reset() {
        this.mMediaPlayer.reset();
        this.mMediaPlayer.setSurface(null);
        this.mMediaPlayer.setDisplay(null);
        this.mMediaPlayer.setVolume(1.0f, 1.0f);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public boolean isPlaying() {
        return this.mMediaPlayer.isPlaying();
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void seekTo(long j) {
        try {
            this.mMediaPlayer.seekTo((int) j);
        } catch (IllegalStateException e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
        }
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [com.yc.kernel.impl.media.AndroidMediaPlayer$1] */
    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void release() {
        this.mMediaPlayer.setOnErrorListener(null);
        this.mMediaPlayer.setOnCompletionListener(null);
        this.mMediaPlayer.setOnInfoListener(null);
        this.mMediaPlayer.setOnBufferingUpdateListener(null);
        this.mMediaPlayer.setOnPreparedListener(null);
        this.mMediaPlayer.setOnVideoSizeChangedListener(null);
        new Thread() { // from class: com.yc.kernel.impl.media.AndroidMediaPlayer.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    AndroidMediaPlayer.this.mMediaPlayer.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public long getCurrentPosition() {
        return this.mMediaPlayer.getCurrentPosition();
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public long getDuration() {
        return this.mMediaPlayer.getDuration();
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public int getBufferedPercentage() {
        return this.mBufferedPercent;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setSurface(Surface surface) {
        if (surface != null) {
            try {
                this.mMediaPlayer.setSurface(surface);
            } catch (Exception e) {
                this.mPlayerEventListener.onError(3, e.getMessage());
            }
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setDisplay(SurfaceHolder surfaceHolder) {
        try {
            this.mMediaPlayer.setDisplay(surfaceHolder);
        } catch (Exception e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setVolume(float f, float f2) {
        try {
            this.mMediaPlayer.setVolume(f, f2);
        } catch (Exception e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setLooping(boolean z) {
        try {
            this.mMediaPlayer.setLooping(z);
        } catch (Exception e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setSpeed(float f) {
        try {
            MediaPlayer mediaPlayer = this.mMediaPlayer;
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(f));
        } catch (Exception e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public float getSpeed() {
        try {
            return this.mMediaPlayer.getPlaybackParams().getSpeed();
        } catch (Exception e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
            return 1.0f;
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setPlayerEventListener(VideoPlayerListener videoPlayerListener) {
        super.setPlayerEventListener(videoPlayerListener);
    }
}