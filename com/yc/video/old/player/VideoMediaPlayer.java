package com.yc.video.old.player;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.view.Surface;
import com.yc.kernel.utils.VideoLogUtils;
import com.yc.video.old.surface.VideoTextureView;
import com.yc.video.tool.BaseToast;
import com.yc.video.tool.PlayerUtils;
import java.io.IOException;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

@Deprecated
/* loaded from: classes.dex */
public class VideoMediaPlayer {
    private AudioManager mAudioManager;
    private IMediaPlayer mMediaPlayer;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private VideoTextureView mTextureView;
    private OldVideoPlayer videoPlayer;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() { // from class: com.yc.video.old.player.VideoMediaPlayer.2
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            VideoMediaPlayer.this.videoPlayer.setCurrentState(7);
            VideoMediaPlayer.this.videoPlayer.getController().onPlayStateChanged(VideoMediaPlayer.this.videoPlayer.getCurrentState());
            VideoLogUtils.d("listener---------onCompletion ——> STATE_COMPLETED");
            VideoMediaPlayer.this.videoPlayer.getContainer().setKeepScreenOn(false);
        }
    };
    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() { // from class: com.yc.video.old.player.VideoMediaPlayer.3
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            VideoMediaPlayer.this.videoPlayer.setCurrentState(2);
            VideoMediaPlayer.this.videoPlayer.getController().onPlayStateChanged(VideoMediaPlayer.this.videoPlayer.getCurrentState());
            VideoLogUtils.d("listener---------onPrepared ——> STATE_PREPARED");
            iMediaPlayer.start();
            if (VideoMediaPlayer.this.videoPlayer.getContinueFromLastPosition()) {
                iMediaPlayer.seekTo(PlayerUtils.getSavedPlayPosition(VideoMediaPlayer.this.videoPlayer.getContext(), VideoMediaPlayer.this.videoPlayer.getUrl()));
            }
            if (VideoMediaPlayer.this.videoPlayer.getSkipToPosition() != 0) {
                iMediaPlayer.seekTo(VideoMediaPlayer.this.videoPlayer.getSkipToPosition());
            }
        }
    };
    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() { // from class: com.yc.video.old.player.VideoMediaPlayer.4
        final int MAX_PERCENT = 97;

        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            VideoMediaPlayer.this.videoPlayer.setBufferPercentage(i);
            if (i > 97) {
                VideoMediaPlayer.this.videoPlayer.setBufferPercentage(100);
            }
            VideoLogUtils.d("listener---------onBufferingUpdate ——> " + i);
        }
    };
    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() { // from class: com.yc.video.old.player.VideoMediaPlayer.5
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnSeekCompleteListener
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        }
    };
    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() { // from class: com.yc.video.old.player.VideoMediaPlayer.6
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnVideoSizeChangedListener
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i2, int i3, int i4) {
            VideoMediaPlayer.this.mTextureView.adaptVideoSize(i, i2);
            VideoLogUtils.d("listener---------onVideoSizeChanged ——> WIDTH：" + i + "， HEIGHT：" + i2);
        }
    };
    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() { // from class: com.yc.video.old.player.VideoMediaPlayer.7
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i2) {
            if (i != -38 && i != Integer.MIN_VALUE && i2 != -38 && i2 != Integer.MIN_VALUE) {
                VideoMediaPlayer.this.videoPlayer.setCurrentState(-1);
                VideoMediaPlayer.this.videoPlayer.getController().onPlayStateChanged(VideoMediaPlayer.this.videoPlayer.getCurrentState());
            }
            VideoLogUtils.d("listener---------onError ——> STATE_ERROR ———— what：" + i + ", extra: " + i2);
            return true;
        }
    };
    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() { // from class: com.yc.video.old.player.VideoMediaPlayer.8
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i2) {
            if (i == 3) {
                VideoMediaPlayer.this.videoPlayer.setCurrentState(3);
                VideoMediaPlayer.this.videoPlayer.getController().onPlayStateChanged(VideoMediaPlayer.this.videoPlayer.getCurrentState());
                VideoLogUtils.d("listener---------onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
                return true;
            }
            if (i == 701) {
                if (VideoMediaPlayer.this.videoPlayer.getCurrentState() == 4 || VideoMediaPlayer.this.videoPlayer.getCurrentState() == 6) {
                    VideoMediaPlayer.this.videoPlayer.setCurrentState(6);
                    VideoLogUtils.d("listener---------onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
                } else {
                    VideoMediaPlayer.this.videoPlayer.setCurrentState(5);
                    VideoLogUtils.d("listener---------onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
                }
                VideoMediaPlayer.this.videoPlayer.getController().onPlayStateChanged(VideoMediaPlayer.this.videoPlayer.getCurrentState());
                return true;
            }
            if (i == 702) {
                if (VideoMediaPlayer.this.videoPlayer.getCurrentState() == 5) {
                    VideoMediaPlayer.this.videoPlayer.setCurrentState(3);
                    VideoMediaPlayer.this.videoPlayer.getController().onPlayStateChanged(VideoMediaPlayer.this.videoPlayer.getCurrentState());
                    VideoLogUtils.d("listener---------onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING");
                }
                if (VideoMediaPlayer.this.videoPlayer.getCurrentState() != 6) {
                    return true;
                }
                VideoMediaPlayer.this.videoPlayer.setCurrentState(4);
                VideoMediaPlayer.this.videoPlayer.getController().onPlayStateChanged(VideoMediaPlayer.this.videoPlayer.getCurrentState());
                VideoLogUtils.d("listener---------onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED");
                return true;
            }
            if (i == 10001) {
                if (VideoMediaPlayer.this.mTextureView == null) {
                    return true;
                }
                VideoMediaPlayer.this.mTextureView.setRotation(i2);
                VideoLogUtils.d("listener---------视频旋转角度：" + i2);
                return true;
            }
            if (i == 801) {
                VideoLogUtils.d("listener---------视频不能seekTo，为直播视频");
                return true;
            }
            VideoLogUtils.d("listener---------onInfo ——> what：" + i);
            return true;
        }
    };
    private IMediaPlayer.OnTimedTextListener mOnTimedTextListener = new IMediaPlayer.OnTimedTextListener() { // from class: com.yc.video.old.player.VideoMediaPlayer.9
        @Override // tv.danmaku.ijk.media.player.IMediaPlayer.OnTimedTextListener
        public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
        }
    };

    public VideoMediaPlayer(OldVideoPlayer oldVideoPlayer) {
        this.videoPlayer = oldVideoPlayer;
    }

    private VideoMediaPlayer() {
    }

    public AudioManager initAudioManager() {
        if (this.mAudioManager == null) {
            AudioManager audioManager = (AudioManager) this.videoPlayer.getContext().getSystemService("audio");
            this.mAudioManager = audioManager;
            audioManager.requestAudioFocus(null, 3, 1);
        }
        return this.mAudioManager;
    }

    public IMediaPlayer getMediaPlayer() {
        initMediaPlayer();
        return this.mMediaPlayer;
    }

    public void setMediaPlayerNull() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer = null;
        }
    }

    public AudioManager getAudioManager() {
        initAudioManager();
        return this.mAudioManager;
    }

    public void setAudioManagerNull() {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            audioManager.abandonAudioFocus(null);
            this.mAudioManager = null;
        }
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public void releaseSurface() {
        if (this.mSurface != null) {
            this.mSurface.release();
            this.mSurface = null;
        }
    }

    public VideoTextureView getTextureView() {
        return this.mTextureView;
    }

    public void releaseSurfaceTexture() {
        if (this.mSurfaceTexture != null) {
            this.mSurfaceTexture.release();
            this.mSurfaceTexture = null;
        }
    }

    public void initMediaPlayer() {
        if (this.mMediaPlayer == null) {
            if (this.videoPlayer.mPlayerType == 2) {
                this.mMediaPlayer = new AndroidMediaPlayer();
            } else {
                this.mMediaPlayer = createIjkMediaPlayer();
            }
            this.mMediaPlayer.setAudioStreamType(3);
        }
    }

    private IMediaPlayer createIjkMediaPlayer() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        this.mMediaPlayer = ijkMediaPlayer;
        ijkMediaPlayer.setOption(1, "analyzemaxduration", 100L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(1, "analyzeduration", 1L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(1, "probesize", 10240L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "soundtouch", 0L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(1, "flush_packets", 1L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "packet-buffering", 0L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "reconnect", 5L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "max-buffer-size", 10240L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "framedrop", 1L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "max-fps", 30L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "enable-accurate-seek", 1L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "opensles", 0L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "overlay-format", 842225234L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "framedrop", 1L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "start-on-prepared", 0L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(1, "http-detect-range-support", 0L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(2, "skip_loop_filter", 48L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "mediacodec", 0L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "mediacodec-auto-rotate", 1L);
        ((IjkMediaPlayer) this.mMediaPlayer).setOption(4, "mediacodec-handle-resolution-change", 1L);
        return this.mMediaPlayer;
    }

    public void openMediaPlayer() {
        this.videoPlayer.getContainer().setKeepScreenOn(true);
        this.mMediaPlayer.setOnPreparedListener(this.mOnPreparedListener);
        this.mMediaPlayer.setOnCompletionListener(this.mOnCompletionListener);
        this.mMediaPlayer.setOnBufferingUpdateListener(this.mOnBufferingUpdateListener);
        this.mMediaPlayer.setOnSeekCompleteListener(this.mOnSeekCompleteListener);
        this.mMediaPlayer.setOnVideoSizeChangedListener(this.mOnVideoSizeChangedListener);
        this.mMediaPlayer.setOnErrorListener(this.mOnErrorListener);
        this.mMediaPlayer.setOnInfoListener(this.mOnInfoListener);
        this.mMediaPlayer.setOnTimedTextListener(this.mOnTimedTextListener);
        if (this.videoPlayer.getUrl() == null || this.videoPlayer.getUrl().length() == 0) {
            BaseToast.showRoundRectToast("视频链接不能为空");
            return;
        }
        try {
            this.mMediaPlayer.setDataSource(this.videoPlayer.getContext().getApplicationContext(), Uri.parse(this.videoPlayer.getUrl()), this.videoPlayer.getHeaders());
            if (this.mSurface == null) {
                this.mSurface = new Surface(this.mSurfaceTexture);
            }
            this.mMediaPlayer.setSurface(this.mSurface);
            this.mMediaPlayer.setScreenOnWhilePlaying(true);
            this.mMediaPlayer.prepareAsync();
            this.videoPlayer.setCurrentState(1);
            this.videoPlayer.getController().onPlayStateChanged(this.videoPlayer.getCurrentState());
            VideoLogUtils.d("STATE_PREPARING");
        } catch (IOException e) {
            e.printStackTrace();
            VideoLogUtils.e("打开播放器发生错误", e);
        }
    }

    public void initTextureView() {
        if (this.mTextureView == null) {
            VideoTextureView videoTextureView = new VideoTextureView(this.videoPlayer.getContext());
            this.mTextureView = videoTextureView;
            videoTextureView.setOnTextureListener(new VideoTextureView.OnTextureListener() { // from class: com.yc.video.old.player.VideoMediaPlayer.1
                @Override // com.yc.video.old.surface.VideoTextureView.OnTextureListener
                public void onSurfaceAvailable(SurfaceTexture surfaceTexture) {
                    if (VideoMediaPlayer.this.mSurfaceTexture == null) {
                        VideoMediaPlayer.this.mSurfaceTexture = surfaceTexture;
                        VideoMediaPlayer.this.openMediaPlayer();
                    } else {
                        VideoMediaPlayer.this.mTextureView.setSurfaceTexture(VideoMediaPlayer.this.mSurfaceTexture);
                    }
                }

                @Override // com.yc.video.old.surface.VideoTextureView.OnTextureListener
                public void onSurfaceSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                    VideoLogUtils.i("OnTextureListener----onSurfaceSizeChanged");
                }

                @Override // com.yc.video.old.surface.VideoTextureView.OnTextureListener
                public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                    VideoLogUtils.i("OnTextureListener----onSurfaceDestroyed");
                    return VideoMediaPlayer.this.mSurfaceTexture == null;
                }

                @Override // com.yc.video.old.surface.VideoTextureView.OnTextureListener
                public void onSurfaceUpdated(SurfaceTexture surfaceTexture) {
                    VideoLogUtils.i("OnTextureListener----onSurfaceUpdated");
                }
            });
        }
        this.mTextureView.addTextureView(this.videoPlayer.getContainer(), this.mTextureView);
    }
}