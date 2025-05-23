package com.yc.kernel.impl.ijk;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.yc.kernel.inter.AbstractVideoPlayer;
import com.yc.kernel.inter.VideoPlayerListener;
import com.yc.kernel.utils.VideoLogUtils;
import java.util.Map;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

/* loaded from: classes.dex */
public class IjkVideoPlayer extends AbstractVideoPlayer {
    private Context mAppContext;
    private int mBufferedPercent;
    protected IjkMediaPlayer mMediaPlayer;
    private IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() { // from class: com.yc.kernel.impl.ijk.IjkVideoPlayer.3
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i2) {
            IjkVideoPlayer.this.mPlayerEventListener.onError(3, "监听异常" + i + ", extra: " + i2);
            VideoLogUtils.d("IjkVideoPlayer----listener---------onError ——> STATE_ERROR ———— what：" + i + ", extra: " + i2);
            return true;
        }
    };
    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() { // from class: com.yc.kernel.impl.ijk.IjkVideoPlayer.4
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            IjkVideoPlayer.this.mPlayerEventListener.onCompletion();
            VideoLogUtils.d("IjkVideoPlayer----listener---------onCompletion ——> STATE_COMPLETED");
        }
    };
    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() { // from class: com.yc.kernel.impl.ijk.IjkVideoPlayer.5
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i2) {
            IjkVideoPlayer.this.mPlayerEventListener.onInfo(i, i2);
            VideoLogUtils.d("IjkVideoPlayer----listener---------onInfo ——> ———— what：" + i + ", extra: " + i2);
            return true;
        }
    };
    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() { // from class: com.yc.kernel.impl.ijk.IjkVideoPlayer.6
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            IjkVideoPlayer.this.mBufferedPercent = i;
        }
    };
    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() { // from class: com.yc.kernel.impl.ijk.IjkVideoPlayer.7
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            IjkVideoPlayer.this.mPlayerEventListener.onPrepared();
            VideoLogUtils.d("IjkVideoPlayer----listener---------onPrepared ——> STATE_PREPARED");
        }
    };
    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() { // from class: com.yc.kernel.impl.ijk.IjkVideoPlayer.8
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnVideoSizeChangedListener
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i2, int i3, int i4) {
            int videoWidth = iMediaPlayer.getVideoWidth();
            int videoHeight = iMediaPlayer.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                IjkVideoPlayer.this.mPlayerEventListener.onVideoSizeChanged(videoWidth, videoHeight);
            }
            VideoLogUtils.d("IjkVideoPlayer----listener---------onVideoSizeChanged ——> WIDTH：" + i + "， HEIGHT：" + i2);
        }
    };
    private IMediaPlayer.OnTimedTextListener onTimedTextListener = new IMediaPlayer.OnTimedTextListener() { // from class: com.yc.kernel.impl.ijk.IjkVideoPlayer.9
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnTimedTextListener
        public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
        }
    };
    private IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() { // from class: com.yc.kernel.impl.ijk.IjkVideoPlayer.10
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnSeekCompleteListener
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        }
    };

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setOptions() {
    }

    public IjkVideoPlayer(Context context) {
        if (context instanceof Application) {
            this.mAppContext = context;
        } else {
            this.mAppContext = context.getApplicationContext();
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void initPlayer() {
        this.mMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.native_setLogLevel(VideoLogUtils.isIsLog() ? 4 : 8);
        setOptions();
        this.mMediaPlayer.setAudioStreamType(3);
        initListener();
    }

    private void initListener() {
        this.mMediaPlayer.setOnErrorListener(this.onErrorListener);
        this.mMediaPlayer.setOnCompletionListener(this.onCompletionListener);
        this.mMediaPlayer.setOnInfoListener(this.onInfoListener);
        this.mMediaPlayer.setOnBufferingUpdateListener(this.onBufferingUpdateListener);
        this.mMediaPlayer.setOnPreparedListener(this.onPreparedListener);
        this.mMediaPlayer.setOnVideoSizeChangedListener(this.onVideoSizeChangedListener);
        this.mMediaPlayer.setOnSeekCompleteListener(this.onSeekCompleteListener);
        this.mMediaPlayer.setOnTimedTextListener(this.onTimedTextListener);
        this.mMediaPlayer.setOnNativeInvokeListener(new IjkMediaPlayer.OnNativeInvokeListener() { // from class: com.yc.kernel.impl.ijk.IjkVideoPlayer.1
            @Override // tv.danmaku.ijk.media.player.IjkMediaPlayer.OnNativeInvokeListener
            public boolean onNativeInvoke(int i, Bundle bundle) {
                return true;
            }
        });
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setDataSource(String str, Map<String, String> map) {
        if (str == null || str.length() == 0) {
            if (this.mPlayerEventListener != null) {
                this.mPlayerEventListener.onInfo(-1, 0);
                return;
            }
            return;
        }
        try {
            Uri parse = Uri.parse(str);
            if ("android.resource".equals(parse.getScheme())) {
                this.mMediaPlayer.setDataSource(RawDataSourceProvider.create(this.mAppContext, parse));
                return;
            }
            if (map != null) {
                String str2 = map.get("User-Agent");
                if (!TextUtils.isEmpty(str2)) {
                    this.mMediaPlayer.setOption(1, "user_agent", str2);
                }
            }
            this.mMediaPlayer.setDataSource(this.mAppContext, parse, map);
        } catch (Exception e) {
            this.mPlayerEventListener.onError(2, e.getMessage());
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setDataSource(AssetFileDescriptor assetFileDescriptor) {
        try {
            this.mMediaPlayer.setDataSource(new RawDataSourceProvider(assetFileDescriptor));
        } catch (Exception e) {
            this.mPlayerEventListener.onError(3, e.getMessage());
        }
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
    public void prepareAsync() {
        try {
            this.mMediaPlayer.prepareAsync();
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
    public void start() {
        try {
            this.mMediaPlayer.start();
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
    public void reset() {
        this.mMediaPlayer.reset();
        this.mMediaPlayer.setOnVideoSizeChangedListener(this.onVideoSizeChangedListener);
        setOptions();
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

    /* JADX WARN: Type inference failed for: r0v6, types: [com.yc.kernel.impl.ijk.IjkVideoPlayer$2] */
    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void release() {
        this.mMediaPlayer.setOnErrorListener(null);
        this.mMediaPlayer.setOnCompletionListener(null);
        this.mMediaPlayer.setOnInfoListener(null);
        this.mMediaPlayer.setOnBufferingUpdateListener(null);
        this.mMediaPlayer.setOnPreparedListener(null);
        this.mMediaPlayer.setOnVideoSizeChangedListener(null);
        new Thread() { // from class: com.yc.kernel.impl.ijk.IjkVideoPlayer.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    IjkVideoPlayer.this.mMediaPlayer.release();
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
    public void setDisplay(SurfaceHolder surfaceHolder) {
        this.mMediaPlayer.setDisplay(surfaceHolder);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setVolume(float f, float f2) {
        this.mMediaPlayer.setVolume(f, f2);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setLooping(boolean z) {
        this.mMediaPlayer.setLooping(z);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setSpeed(float f) {
        this.mMediaPlayer.setSpeed(f);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public float getSpeed() {
        return this.mMediaPlayer.getSpeed(0.0f);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public long getTcpSpeed() {
        return this.mMediaPlayer.getTcpSpeed();
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setPlayerEventListener(VideoPlayerListener videoPlayerListener) {
        super.setPlayerEventListener(videoPlayerListener);
    }
}