package com.yc.video.old.player;

import android.R;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;
import com.google.android.material.badge.BadgeDrawable;
import com.yc.kernel.utils.VideoLogUtils;
import com.yc.video.old.controller.AbsVideoPlayerController;
import com.yc.video.old.other.VideoPlayerManager;
import com.yc.video.tool.BaseToast;
import com.yc.video.tool.PlayerUtils;
import java.util.Map;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

@Deprecated
/* loaded from: classes.dex */
public class OldVideoPlayer extends FrameLayout implements IVideoPlayer {
    private boolean continueFromLastPosition;
    private int mBufferPercentage;
    private FrameLayout mContainer;
    private Context mContext;
    private AbsVideoPlayerController mController;
    private int mCurrentMode;
    private int mCurrentState;
    private Map<String, String> mHeaders;
    public int mPlayerType;
    private String mUrl;
    public long skipToPosition;
    private VideoMediaPlayer videoMediaPlayer;

    public OldVideoPlayer(Context context) {
        this(context, null);
    }

    public OldVideoPlayer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OldVideoPlayer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPlayerType = 1;
        this.mCurrentState = 0;
        this.mCurrentMode = 1001;
        this.continueFromLastPosition = false;
        this.mContext = context;
        init();
    }

    private void init() {
        BaseToast.init(this.mContext.getApplicationContext());
        FrameLayout frameLayout = new FrameLayout(this.mContext);
        this.mContainer = frameLayout;
        frameLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
        this.videoMediaPlayer = new VideoMediaPlayer(this);
        addView(this.mContainer, layoutParams);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        AbsVideoPlayerController absVideoPlayerController;
        VideoLogUtils.i("如果锁屏1，则屏蔽返回键onKeyDown" + keyEvent.getAction());
        if (i == 4 && (absVideoPlayerController = this.mController) != null && absVideoPlayerController.getLock()) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        VideoLogUtils.d("onAttachedToWindow");
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        VideoLogUtils.d("onDetachedFromWindow");
        AbsVideoPlayerController absVideoPlayerController = this.mController;
        if (absVideoPlayerController != null) {
            absVideoPlayerController.destroy();
        }
        release();
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public final void setUp(String str, Map<String, String> map) {
        if (str == null || str.length() == 0) {
            VideoLogUtils.d("设置参数-------设置的视频链接不能为空");
        }
        this.mUrl = str;
        this.mHeaders = map;
    }

    public void setController(AbsVideoPlayerController absVideoPlayerController) {
        this.mContainer.removeView(this.mController);
        this.mController = absVideoPlayerController;
        absVideoPlayerController.reset();
        this.mController.setVideoPlayer(this);
        this.mContainer.addView(this.mController, new FrameLayout.LayoutParams(-1, -1));
    }

    public AbsVideoPlayerController getController() {
        return this.mController;
    }

    public FrameLayout getContainer() {
        return this.mContainer;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public Map<String, String> getHeaders() {
        return this.mHeaders;
    }

    public void setPlayerType(int i) {
        this.mPlayerType = i;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void continueFromLastPosition(boolean z) {
        this.continueFromLastPosition = z;
    }

    public boolean getContinueFromLastPosition() {
        return this.continueFromLastPosition;
    }

    public long getSkipToPosition() {
        return this.skipToPosition;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void setSpeed(float f) {
        if (f < 0.0f) {
            VideoLogUtils.d("设置参数-------设置的视频播放速度不能小于0");
        }
        if (this.videoMediaPlayer.getMediaPlayer() instanceof IjkMediaPlayer) {
            ((IjkMediaPlayer) this.videoMediaPlayer.getMediaPlayer()).setSpeed(f);
            return;
        }
        if (this.videoMediaPlayer.getMediaPlayer() instanceof AndroidMediaPlayer) {
            VideoLogUtils.d("设置参数-------只有IjkPlayer才能设置播放速度");
        } else if (this.videoMediaPlayer.getMediaPlayer() instanceof MediaPlayer) {
            VideoLogUtils.d("设置参数-------只有IjkPlayer才能设置播放速度");
        } else {
            VideoLogUtils.d("设置参数-------只有IjkPlayer才能设置播放速度");
        }
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void start() {
        if (this.mController == null) {
            throw new NullPointerException("Controller must not be null , please setController first");
        }
        if (this.mCurrentState == 0) {
            VideoPlayerManager.instance().setCurrentVideoPlayer(this);
            this.videoMediaPlayer.initAudioManager();
            this.videoMediaPlayer.initMediaPlayer();
            this.videoMediaPlayer.initTextureView();
            return;
        }
        VideoLogUtils.d("播放状态--------VideoPlayer只有在mCurrentState == STATE_IDLE时才能调用start方法.");
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void start(long j) {
        if (j < 0) {
            VideoLogUtils.d("设置参数-------设置开始播放的播放位置不能小于0");
        }
        this.skipToPosition = j;
        start();
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void restart() {
        int i = this.mCurrentState;
        if (i == 4) {
            this.videoMediaPlayer.getMediaPlayer().start();
            this.mCurrentState = 3;
            this.mController.onPlayStateChanged(3);
            VideoLogUtils.d("播放状态--------STATE_PLAYING");
            return;
        }
        if (i == 6) {
            this.videoMediaPlayer.getMediaPlayer().start();
            this.mCurrentState = 5;
            this.mController.onPlayStateChanged(5);
            VideoLogUtils.d("播放状态--------STATE_BUFFERING_PLAYING");
            return;
        }
        if (i == 7 || i == -1) {
            this.videoMediaPlayer.getMediaPlayer().reset();
            this.videoMediaPlayer.openMediaPlayer();
            VideoLogUtils.d("播放状态--------完成播放或者播放错误，则重新播放");
            return;
        }
        VideoLogUtils.d("VideoPlayer在mCurrentState == " + this.mCurrentState + "时不能调用restart()方法.");
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void pause() {
        int i = this.mCurrentState;
        if (i == 3) {
            this.videoMediaPlayer.getMediaPlayer().pause();
            this.mCurrentState = 4;
            this.mController.onPlayStateChanged(4);
            VideoLogUtils.d("播放状态--------STATE_PAUSED");
            return;
        }
        if (i == 5) {
            this.videoMediaPlayer.getMediaPlayer().pause();
            this.mCurrentState = 6;
            this.mController.onPlayStateChanged(6);
            VideoLogUtils.d("播放状态--------STATE_BUFFERING_PAUSED");
        }
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void seekTo(long j) {
        if (j < 0) {
            VideoLogUtils.d("设置参数-------设置开始跳转播放位置不能小于0");
        }
        if (this.videoMediaPlayer.getMediaPlayer() != null) {
            this.videoMediaPlayer.getMediaPlayer().seekTo(j);
        }
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void setVolume(int i) {
        if (this.videoMediaPlayer.getAudioManager() != null) {
            this.videoMediaPlayer.getAudioManager().setStreamVolume(3, i, 0);
        }
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isIdle() {
        return this.mCurrentState == 0;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isPreparing() {
        return this.mCurrentState == 1;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isPrepared() {
        return this.mCurrentState == 2;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isBufferingPlaying() {
        return this.mCurrentState == 5;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isBufferingPaused() {
        return this.mCurrentState == 6;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isPlaying() {
        return this.mCurrentState == 3;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isPaused() {
        return this.mCurrentState == 4;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isError() {
        return this.mCurrentState == -1;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isCompleted() {
        return this.mCurrentState == 7;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isFullScreen() {
        return this.mCurrentMode == 1002;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isTinyWindow() {
        return this.mCurrentMode == 1003;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean isNormal() {
        return this.mCurrentMode == 1001;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public int getMaxVolume() {
        if (this.videoMediaPlayer.getAudioManager() != null) {
            return this.videoMediaPlayer.getAudioManager().getStreamMaxVolume(3);
        }
        return 0;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public int getPlayType() {
        return this.mCurrentMode;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public int getVolume() {
        if (this.videoMediaPlayer.getAudioManager() != null) {
            return this.videoMediaPlayer.getAudioManager().getStreamVolume(3);
        }
        return 0;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public long getDuration() {
        if (this.videoMediaPlayer.getMediaPlayer() != null) {
            return this.videoMediaPlayer.getMediaPlayer().getDuration();
        }
        return 0L;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public long getCurrentPosition() {
        if (this.videoMediaPlayer.getMediaPlayer() != null) {
            return this.videoMediaPlayer.getMediaPlayer().getCurrentPosition();
        }
        return 0L;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public int getBufferPercentage() {
        return this.mBufferPercentage;
    }

    public void setBufferPercentage(int i) {
        this.mBufferPercentage = i;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public float getSpeed(float f) {
        if (this.videoMediaPlayer.getMediaPlayer() instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) this.videoMediaPlayer.getMediaPlayer()).getSpeed(f);
        }
        return 0.0f;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public long getTcpSpeed() {
        if (this.videoMediaPlayer.getMediaPlayer() instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) this.videoMediaPlayer.getMediaPlayer()).getTcpSpeed();
        }
        return 0L;
    }

    public int getCurrentState() {
        return this.mCurrentState;
    }

    public void setCurrentState(int i) {
        this.mCurrentState = i;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void enterFullScreen() {
        if (this.mCurrentMode == 1002) {
            return;
        }
        PlayerUtils.hideActionBar(this.mContext);
        PlayerUtils.scanForActivity(this.mContext).setRequestedOrientation(0);
        ViewGroup viewGroup = (ViewGroup) PlayerUtils.scanForActivity(this.mContext).findViewById(R.id.content);
        if (this.mCurrentMode == 1003) {
            viewGroup.removeView(this.mContainer);
        } else {
            removeView(this.mContainer);
        }
        viewGroup.addView(this.mContainer, new FrameLayout.LayoutParams(-1, -1));
        this.mCurrentMode = 1002;
        this.mController.onPlayModeChanged(1002);
        VideoLogUtils.d("播放模式--------MODE_FULL_SCREEN");
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void enterVerticalScreenScreen() {
        if (this.mCurrentMode == 1002) {
            return;
        }
        PlayerUtils.hideActionBar(this.mContext);
        PlayerUtils.scanForActivity(this.mContext).setRequestedOrientation(1);
        ViewGroup viewGroup = (ViewGroup) PlayerUtils.scanForActivity(this.mContext).findViewById(R.id.content);
        if (this.mCurrentMode == 1003) {
            viewGroup.removeView(this.mContainer);
        } else {
            removeView(this.mContainer);
        }
        viewGroup.addView(this.mContainer, new FrameLayout.LayoutParams(-1, -1));
        this.mCurrentMode = 1002;
        this.mController.onPlayModeChanged(1002);
        VideoLogUtils.d("播放模式--------MODE_FULL_SCREEN");
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean exitFullScreen() {
        if (this.mCurrentMode != 1002) {
            return false;
        }
        PlayerUtils.showActionBar(this.mContext);
        PlayerUtils.scanForActivity(this.mContext).setRequestedOrientation(1);
        ((ViewGroup) PlayerUtils.scanForActivity(this.mContext).findViewById(R.id.content)).removeView(this.mContainer);
        addView(this.mContainer, new FrameLayout.LayoutParams(-1, -1));
        this.mCurrentMode = 1001;
        this.mController.onPlayModeChanged(1001);
        VideoLogUtils.d("播放模式--------MODE_NORMAL");
        setOnKeyListener(null);
        return true;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void enterTinyWindow() {
        if (this.mCurrentMode == 1003) {
            return;
        }
        removeView(this.mContainer);
        ViewGroup viewGroup = (ViewGroup) PlayerUtils.scanForActivity(this.mContext).findViewById(R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (PlayerUtils.getScreenWidth(this.mContext) * 0.6f), (int) (((PlayerUtils.getScreenWidth(this.mContext) * 0.6f) * 9.0f) / 16.0f));
        layoutParams.gravity = BadgeDrawable.BOTTOM_END;
        layoutParams.rightMargin = PlayerUtils.dp2px(this.mContext, 8.0f);
        layoutParams.bottomMargin = PlayerUtils.dp2px(this.mContext, 8.0f);
        viewGroup.addView(this.mContainer, layoutParams);
        this.mCurrentMode = 1003;
        this.mController.onPlayModeChanged(1003);
        VideoLogUtils.d("播放模式-------MODE_TINY_WINDOW");
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public boolean exitTinyWindow() {
        if (this.mCurrentMode != 1003) {
            return false;
        }
        ((ViewGroup) PlayerUtils.scanForActivity(this.mContext).findViewById(R.id.content)).removeView(this.mContainer);
        addView(this.mContainer, new FrameLayout.LayoutParams(-1, -1));
        this.mCurrentMode = 1001;
        this.mController.onPlayModeChanged(1001);
        VideoLogUtils.d("播放模式-------MODE_NORMAL");
        return true;
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void release() {
        if (isPlaying() || isBufferingPlaying() || isBufferingPaused() || isPaused()) {
            PlayerUtils.savePlayPosition(this.mContext, this.mUrl, getCurrentPosition());
        } else if (isCompleted()) {
            PlayerUtils.savePlayPosition(this.mContext, this.mUrl, 0L);
        }
        if (isFullScreen()) {
            exitFullScreen();
        }
        if (isTinyWindow()) {
            exitTinyWindow();
        }
        this.mCurrentMode = 1001;
        releasePlayer();
        AbsVideoPlayerController absVideoPlayerController = this.mController;
        if (absVideoPlayerController != null) {
            absVideoPlayerController.reset();
        }
        Runtime.getRuntime().gc();
    }

    @Override // com.yc.video.old.player.IVideoPlayer
    public void releasePlayer() {
        this.videoMediaPlayer.setAudioManagerNull();
        if (this.videoMediaPlayer.getMediaPlayer() != null) {
            this.videoMediaPlayer.getMediaPlayer().release();
            this.videoMediaPlayer.setMediaPlayerNull();
        }
        FrameLayout frameLayout = this.mContainer;
        if (frameLayout != null) {
            frameLayout.removeView(this.videoMediaPlayer.getTextureView());
        }
        this.videoMediaPlayer.releaseSurface();
        this.videoMediaPlayer.releaseSurfaceTexture();
        this.mCurrentState = 0;
    }
}