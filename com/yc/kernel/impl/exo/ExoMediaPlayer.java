package com.yc.kernel.impl.exo;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.yc.kernel.inter.AbstractVideoPlayer;
import com.yc.kernel.inter.VideoPlayerListener;
import com.yc.kernel.utils.VideoLogUtils;
import java.util.Map;

/* loaded from: classes.dex */
public class ExoMediaPlayer extends AbstractVideoPlayer implements VideoListener, Player.EventListener {
    protected Context mAppContext;
    protected SimpleExoPlayer mInternalPlayer;
    private boolean mIsBuffering;
    private boolean mIsPreparing;
    private LoadControl mLoadControl;
    protected MediaSource mMediaSource;
    protected ExoMediaSourceHelper mMediaSourceHelper;
    private RenderersFactory mRenderersFactory;
    private PlaybackParameters mSpeedPlaybackParameters;
    private TrackSelector mTrackSelector;
    private int mLastReportedPlaybackState = 1;
    private boolean mLastReportedPlayWhenReady = false;
    private MediaSourceEventListener mMediaSourceEventListener = new MediaSourceEventListener() { // from class: com.yc.kernel.impl.exo.ExoMediaPlayer.1
        @Override // com.google.android.exoplayer2.source.MediaSourceEventListener
        public void onReadingStarted(int i, MediaSource.MediaPeriodId mediaPeriodId) {
            if (ExoMediaPlayer.this.mPlayerEventListener == null || !ExoMediaPlayer.this.mIsPreparing) {
                return;
            }
            ExoMediaPlayer.this.mPlayerEventListener.onPrepared();
        }
    };

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public long getTcpSpeed() {
        return 0L;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setDataSource(AssetFileDescriptor assetFileDescriptor) {
    }

    public ExoMediaPlayer(Context context) {
        if (context instanceof Application) {
            this.mAppContext = context;
        } else {
            this.mAppContext = context.getApplicationContext();
        }
        this.mMediaSourceHelper = ExoMediaSourceHelper.getInstance(context);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void initPlayer() {
        Context context = this.mAppContext;
        RenderersFactory renderersFactory = this.mRenderersFactory;
        if (renderersFactory == null) {
            renderersFactory = new DefaultRenderersFactory(this.mAppContext);
            this.mRenderersFactory = renderersFactory;
        }
        RenderersFactory renderersFactory2 = renderersFactory;
        TrackSelector trackSelector = this.mTrackSelector;
        if (trackSelector == null) {
            trackSelector = new DefaultTrackSelector(this.mAppContext);
            this.mTrackSelector = trackSelector;
        }
        TrackSelector trackSelector2 = trackSelector;
        LoadControl loadControl = this.mLoadControl;
        if (loadControl == null) {
            loadControl = new DefaultLoadControl();
            this.mLoadControl = loadControl;
        }
        this.mInternalPlayer = new SimpleExoPlayer.Builder(context, renderersFactory2, trackSelector2, loadControl, DefaultBandwidthMeter.getSingletonInstance(this.mAppContext), Util.getLooper(), new AnalyticsCollector(Clock.DEFAULT), true, Clock.DEFAULT).build();
        setOptions();
        if (VideoLogUtils.isIsLog() && (this.mTrackSelector instanceof MappingTrackSelector)) {
            this.mInternalPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) this.mTrackSelector, "ExoPlayer"));
        }
        initListener();
    }

    private void initListener() {
        this.mInternalPlayer.addListener(this);
        this.mInternalPlayer.addVideoListener(this);
    }

    public void setTrackSelector(TrackSelector trackSelector) {
        this.mTrackSelector = trackSelector;
    }

    public void setRenderersFactory(RenderersFactory renderersFactory) {
        this.mRenderersFactory = renderersFactory;
    }

    public void setLoadControl(LoadControl loadControl) {
        this.mLoadControl = loadControl;
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
        this.mMediaSource = this.mMediaSourceHelper.getMediaSource(str, map);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void prepareAsync() {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer == null || this.mMediaSource == null) {
            return;
        }
        PlaybackParameters playbackParameters = this.mSpeedPlaybackParameters;
        if (playbackParameters != null) {
            simpleExoPlayer.setPlaybackParameters(playbackParameters);
        }
        this.mIsPreparing = true;
        this.mMediaSource.addEventListener(new Handler(), this.mMediaSourceEventListener);
        this.mInternalPlayer.prepare(this.mMediaSource);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void start() {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer == null) {
            return;
        }
        simpleExoPlayer.setPlayWhenReady(true);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void pause() {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer == null) {
            return;
        }
        simpleExoPlayer.setPlayWhenReady(false);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void stop() {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer == null) {
            return;
        }
        simpleExoPlayer.stop();
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void reset() {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop(true);
            this.mInternalPlayer.setVideoSurface(null);
            this.mIsPreparing = false;
            this.mIsBuffering = false;
            this.mLastReportedPlaybackState = 1;
            this.mLastReportedPlayWhenReady = false;
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public boolean isPlaying() {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer == null) {
            return false;
        }
        int playbackState = simpleExoPlayer.getPlaybackState();
        if (playbackState == 2 || playbackState == 3) {
            return this.mInternalPlayer.getPlayWhenReady();
        }
        return false;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void seekTo(long j) {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer == null) {
            return;
        }
        simpleExoPlayer.seekTo(j);
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [com.yc.kernel.impl.exo.ExoMediaPlayer$2] */
    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void release() {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.removeListener(this);
            this.mInternalPlayer.removeVideoListener(this);
            final SimpleExoPlayer simpleExoPlayer2 = this.mInternalPlayer;
            this.mInternalPlayer = null;
            new Thread() { // from class: com.yc.kernel.impl.exo.ExoMediaPlayer.2
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    simpleExoPlayer2.release();
                }
            }.start();
        }
        this.mIsPreparing = false;
        this.mIsBuffering = false;
        this.mLastReportedPlaybackState = 1;
        this.mLastReportedPlayWhenReady = false;
        this.mSpeedPlaybackParameters = null;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public long getCurrentPosition() {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer == null) {
            return 0L;
        }
        return simpleExoPlayer.getCurrentPosition();
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public long getDuration() {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer == null) {
            return 0L;
        }
        return simpleExoPlayer.getDuration();
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public int getBufferedPercentage() {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer == null) {
            return 0;
        }
        return simpleExoPlayer.getBufferedPercentage();
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setSurface(Surface surface) {
        if (surface != null) {
            try {
                SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
                if (simpleExoPlayer != null) {
                    simpleExoPlayer.setVideoSurface(surface);
                }
            } catch (Exception e) {
                this.mPlayerEventListener.onError(3, e.getMessage());
            }
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setDisplay(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            setSurface(null);
        } else {
            setSurface(surfaceHolder.getSurface());
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setVolume(float f, float f2) {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume((f + f2) / 2.0f);
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setLooping(boolean z) {
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setRepeatMode(z ? 2 : 0);
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setOptions() {
        this.mInternalPlayer.setPlayWhenReady(true);
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setSpeed(float f) {
        PlaybackParameters playbackParameters = new PlaybackParameters(f);
        this.mSpeedPlaybackParameters = playbackParameters;
        SimpleExoPlayer simpleExoPlayer = this.mInternalPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlaybackParameters(playbackParameters);
        }
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public float getSpeed() {
        PlaybackParameters playbackParameters = this.mSpeedPlaybackParameters;
        if (playbackParameters != null) {
            return playbackParameters.speed;
        }
        return 1.0f;
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onPlayerStateChanged(boolean z, int i) {
        if (this.mPlayerEventListener == null || this.mIsPreparing) {
            return;
        }
        if (this.mLastReportedPlayWhenReady == z && this.mLastReportedPlaybackState == i) {
            return;
        }
        if (i == 2) {
            this.mPlayerEventListener.onInfo(701, getBufferedPercentage());
            this.mIsBuffering = true;
        } else if (i != 3) {
            if (i == 4) {
                this.mPlayerEventListener.onCompletion();
            }
        } else if (this.mIsBuffering) {
            this.mPlayerEventListener.onInfo(702, getBufferedPercentage());
            this.mIsBuffering = false;
        }
        this.mLastReportedPlaybackState = i;
        this.mLastReportedPlayWhenReady = z;
    }

    @Override // com.google.android.exoplayer2.Player.EventListener
    public void onPlayerError(ExoPlaybackException exoPlaybackException) {
        if (this.mPlayerEventListener != null) {
            int i = exoPlaybackException.type;
            if (i == 0) {
                this.mPlayerEventListener.onError(1, exoPlaybackException.getMessage());
            } else if (i == 1 || i == 2 || i == 3 || i == 4) {
                this.mPlayerEventListener.onError(3, exoPlaybackException.getMessage());
            }
        }
    }

    @Override // com.google.android.exoplayer2.video.VideoListener
    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
        if (this.mPlayerEventListener != null) {
            this.mPlayerEventListener.onVideoSizeChanged(i, i2);
            if (i3 > 0) {
                this.mPlayerEventListener.onInfo(10001, i3);
            }
        }
    }

    @Override // com.google.android.exoplayer2.video.VideoListener
    public void onRenderedFirstFrame() {
        if (this.mPlayerEventListener == null || !this.mIsPreparing) {
            return;
        }
        this.mPlayerEventListener.onInfo(3, 0);
        this.mIsPreparing = false;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setPlayerEventListener(VideoPlayerListener videoPlayerListener) {
        super.setPlayerEventListener(videoPlayerListener);
    }
}