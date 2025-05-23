package com.yc.video.player;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;
import com.google.android.material.badge.BadgeDrawable;
import com.yc.kernel.factory.PlayerFactory;
import com.yc.kernel.inter.AbstractVideoPlayer;
import com.yc.kernel.inter.VideoPlayerListener;
import com.yc.kernel.utils.VideoLogUtils;
import com.yc.video.R;
import com.yc.video.config.VideoPlayerConfig;
import com.yc.video.controller.BaseVideoController;
import com.yc.video.surface.InterSurfaceView;
import com.yc.video.surface.SurfaceFactory;
import com.yc.video.tool.BaseToast;
import com.yc.video.tool.PlayerUtils;
import com.yc.video.tool.VideoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class VideoPlayer<P extends AbstractVideoPlayer> extends FrameLayout implements InterVideoPlayer, VideoPlayerListener {
    protected AssetFileDescriptor mAssetFileDescriptor;
    protected AudioFocusHelper mAudioFocusHelper;
    private Context mContext;
    protected int mCurrentPlayState;
    protected int mCurrentPlayerState;
    protected long mCurrentPosition;
    protected int mCurrentScreenScaleType;
    protected boolean mEnableAudioFocus;
    protected Map<String, String> mHeaders;
    protected boolean mIsFullScreen;
    protected boolean mIsLooping;
    protected boolean mIsMute;
    protected boolean mIsTinyScreen;
    protected P mMediaPlayer;
    protected List<OnVideoStateListener> mOnStateChangeListeners;
    private int mPlayerBackgroundColor;
    protected FrameLayout mPlayerContainer;
    protected PlayerFactory<P> mPlayerFactory;
    protected ProgressManager mProgressManager;
    protected InterSurfaceView mRenderView;
    protected SurfaceFactory mRenderViewFactory;
    protected int[] mTinyScreenSize;
    protected String mUrl;
    protected BaseVideoController mVideoController;
    protected int[] mVideoSize;

    protected void setInitOptions() {
    }

    public VideoPlayer(Context context) {
        this(context, null);
    }

    public VideoPlayer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VideoPlayer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mVideoSize = new int[]{0, 0};
        this.mCurrentPlayState = 0;
        this.mCurrentPlayerState = 1001;
        this.mIsFullScreen = false;
        this.mTinyScreenSize = new int[]{0, 0};
        this.mContext = context;
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        BaseToast.init(this.mContext.getApplicationContext());
        initConfig();
        initAttrs(attributeSet);
        initView();
    }

    private void initConfig() {
        VideoPlayerConfig config = VideoViewManager.getConfig();
        this.mEnableAudioFocus = config.mEnableAudioFocus;
        this.mProgressManager = config.mProgressManager;
        this.mPlayerFactory = config.mPlayerFactory;
        this.mCurrentScreenScaleType = config.mScreenScaleType;
        this.mRenderViewFactory = config.mRenderViewFactory;
        VideoLogUtils.setIsLog(config.mIsEnableLog);
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
        BaseVideoController baseVideoController = this.mVideoController;
        if (baseVideoController != null) {
            baseVideoController.destroy();
        }
        release();
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z && this.mIsFullScreen) {
            VideoPlayerHelper.instance().hideSysBar(VideoPlayerHelper.instance().getDecorView(this.mContext, this.mVideoController), this.mContext, this.mVideoController);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        VideoLogUtils.d("onSaveInstanceState: " + this.mCurrentPosition);
        saveProgress();
        return super.onSaveInstanceState();
    }

    private void initAttrs(AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, R.styleable.VideoPlayer);
        this.mEnableAudioFocus = obtainStyledAttributes.getBoolean(R.styleable.VideoPlayer_enableAudioFocus, this.mEnableAudioFocus);
        this.mIsLooping = obtainStyledAttributes.getBoolean(R.styleable.VideoPlayer_looping, false);
        this.mCurrentScreenScaleType = obtainStyledAttributes.getInt(R.styleable.VideoPlayer_screenScaleType, this.mCurrentScreenScaleType);
        this.mPlayerBackgroundColor = obtainStyledAttributes.getColor(R.styleable.VideoPlayer_playerBackgroundColor, ViewCompat.MEASURED_STATE_MASK);
        obtainStyledAttributes.recycle();
    }

    protected void initView() {
        FrameLayout frameLayout = new FrameLayout(getContext());
        this.mPlayerContainer = frameLayout;
        frameLayout.setBackgroundColor(this.mPlayerBackgroundColor);
        addView(this.mPlayerContainer, new FrameLayout.LayoutParams(-1, -1));
    }

    public void setController(BaseVideoController baseVideoController) {
        this.mPlayerContainer.removeView(this.mVideoController);
        this.mVideoController = baseVideoController;
        if (baseVideoController != null) {
            baseVideoController.setMediaPlayer(this);
            this.mPlayerContainer.addView(this.mVideoController, new FrameLayout.LayoutParams(-1, -1));
        }
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void start() {
        boolean startPlay;
        if (this.mVideoController == null) {
            throw new VideoException(21, "Controller must not be null , please setController first");
        }
        if (isInIdleState() || isInStartAbortState()) {
            startPlay = startPlay();
        } else if (isInPlaybackState()) {
            startInPlaybackState();
            startPlay = true;
        } else {
            startPlay = false;
        }
        if (startPlay) {
            this.mPlayerContainer.setKeepScreenOn(true);
            AudioFocusHelper audioFocusHelper = this.mAudioFocusHelper;
            if (audioFocusHelper != null) {
                audioFocusHelper.requestFocus();
            }
        }
    }

    protected boolean startPlay() {
        if (showNetWarning()) {
            setPlayState(8);
            return false;
        }
        if (this.mEnableAudioFocus) {
            this.mAudioFocusHelper = new AudioFocusHelper(this);
        }
        ProgressManager progressManager = this.mProgressManager;
        if (progressManager != null) {
            this.mCurrentPosition = progressManager.getSavedProgress(this.mUrl);
        }
        initPlayer();
        addDisplay();
        startPrepare(false);
        return true;
    }

    protected void initPlayer() {
        P createPlayer = this.mPlayerFactory.createPlayer(this.mContext);
        this.mMediaPlayer = createPlayer;
        createPlayer.setPlayerEventListener(this);
        setInitOptions();
        this.mMediaPlayer.initPlayer();
        setOptions();
    }

    protected boolean showNetWarning() {
        BaseVideoController baseVideoController;
        return (VideoPlayerHelper.instance().isLocalDataSource(this.mUrl, this.mAssetFileDescriptor) || (baseVideoController = this.mVideoController) == null || !baseVideoController.showNetWarning()) ? false : true;
    }

    protected void setOptions() {
        this.mMediaPlayer.setLooping(this.mIsLooping);
    }

    protected void addDisplay() {
        InterSurfaceView interSurfaceView = this.mRenderView;
        if (interSurfaceView != null) {
            this.mPlayerContainer.removeView(interSurfaceView.getView());
            this.mRenderView.release();
        }
        InterSurfaceView createRenderView = this.mRenderViewFactory.createRenderView(this.mContext);
        this.mRenderView = createRenderView;
        createRenderView.attachToPlayer(this.mMediaPlayer);
        this.mPlayerContainer.addView(this.mRenderView.getView(), 0, new FrameLayout.LayoutParams(-1, -1, 17));
    }

    protected void startPrepare(boolean z) {
        int i;
        if (z) {
            this.mMediaPlayer.reset();
            setOptions();
        }
        if (prepareDataSource()) {
            this.mMediaPlayer.prepareAsync();
            setPlayState(1);
            if (isFullScreen()) {
                i = 1002;
            } else {
                i = isTinyScreen() ? 1003 : 1001;
            }
            setPlayerState(i);
        }
    }

    protected boolean prepareDataSource() {
        AssetFileDescriptor assetFileDescriptor = this.mAssetFileDescriptor;
        if (assetFileDescriptor != null) {
            this.mMediaPlayer.setDataSource(assetFileDescriptor);
            return true;
        }
        if (TextUtils.isEmpty(this.mUrl)) {
            return false;
        }
        this.mMediaPlayer.setDataSource(this.mUrl, this.mHeaders);
        return true;
    }

    protected void startInPlaybackState() {
        this.mMediaPlayer.start();
        setPlayState(3);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void pause() {
        if (isInPlaybackState() && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
            setPlayState(4);
            AudioFocusHelper audioFocusHelper = this.mAudioFocusHelper;
            if (audioFocusHelper != null) {
                audioFocusHelper.abandonFocus();
            }
            this.mPlayerContainer.setKeepScreenOn(false);
        }
    }

    public void resume() {
        if (!isInPlaybackState() || this.mMediaPlayer.isPlaying()) {
            return;
        }
        this.mMediaPlayer.start();
        setPlayState(3);
        AudioFocusHelper audioFocusHelper = this.mAudioFocusHelper;
        if (audioFocusHelper != null) {
            audioFocusHelper.requestFocus();
        }
        this.mPlayerContainer.setKeepScreenOn(true);
    }

    public void release() {
        if (isInIdleState()) {
            return;
        }
        VideoPlayerConfig config = VideoViewManager.getConfig();
        if (config != null && config.mBuriedPointEvent != null) {
            config.mBuriedPointEvent.playerDestroy(this.mUrl);
            long duration = getDuration();
            config.mBuriedPointEvent.playerOutProgress(this.mUrl, (getCurrentPosition() * 1.0f) / (duration * 1.0f));
            config.mBuriedPointEvent.playerOutProgress(this.mUrl, duration, this.mCurrentPosition);
        }
        P p = this.mMediaPlayer;
        if (p != null) {
            p.release();
            this.mMediaPlayer = null;
        }
        InterSurfaceView interSurfaceView = this.mRenderView;
        if (interSurfaceView != null) {
            this.mPlayerContainer.removeView(interSurfaceView.getView());
            this.mRenderView.release();
            this.mRenderView = null;
        }
        AssetFileDescriptor assetFileDescriptor = this.mAssetFileDescriptor;
        if (assetFileDescriptor != null) {
            try {
                assetFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        AudioFocusHelper audioFocusHelper = this.mAudioFocusHelper;
        if (audioFocusHelper != null) {
            audioFocusHelper.abandonFocus();
            this.mAudioFocusHelper.release();
            this.mAudioFocusHelper = null;
        }
        this.mPlayerContainer.setKeepScreenOn(false);
        saveProgress();
        this.mCurrentPosition = 0L;
        setPlayState(0);
    }

    protected void saveProgress() {
        if (this.mProgressManager == null || this.mCurrentPosition <= 0) {
            return;
        }
        VideoLogUtils.d("saveProgress: " + this.mCurrentPosition);
        this.mProgressManager.saveProgress(this.mUrl, this.mCurrentPosition);
    }

    protected boolean isInPlaybackState() {
        int i;
        return (this.mMediaPlayer == null || (i = this.mCurrentPlayState) == -1 || i == 0 || i == 1 || i == 8 || i == 5) ? false : true;
    }

    protected boolean isInIdleState() {
        return this.mCurrentPlayState == 0;
    }

    private boolean isInStartAbortState() {
        return this.mCurrentPlayState == 8;
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void replay(boolean z) {
        if (z) {
            this.mCurrentPosition = 0L;
        }
        addDisplay();
        startPrepare(true);
        this.mPlayerContainer.setKeepScreenOn(true);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public long getDuration() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getDuration();
        }
        return 0L;
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public long getCurrentPosition() {
        if (!isInPlaybackState()) {
            return 0L;
        }
        long currentPosition = this.mMediaPlayer.getCurrentPosition();
        this.mCurrentPosition = currentPosition;
        return currentPosition;
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void seekTo(long j) {
        if (j < 0) {
            VideoLogUtils.d("设置参数-------设置开始跳转播放位置不能小于0");
            j = 0;
        }
        if (isInPlaybackState()) {
            this.mMediaPlayer.seekTo(j);
        }
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public boolean isPlaying() {
        return isInPlaybackState() && this.mMediaPlayer.isPlaying();
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public int getBufferedPercentage() {
        P p = this.mMediaPlayer;
        if (p != null) {
            return p.getBufferedPercentage();
        }
        return 0;
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void setMute(boolean z) {
        P p = this.mMediaPlayer;
        if (p != null) {
            this.mIsMute = z;
            float f = z ? 0.0f : 1.0f;
            p.setVolume(f, f);
        }
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public boolean isMute() {
        return this.mIsMute;
    }

    @Override // com.yc.kernel.inter.VideoPlayerListener
    public void onError(int i, String str) {
        this.mPlayerContainer.setKeepScreenOn(false);
        if (!PlayerUtils.isConnected(this.mContext)) {
            setPlayState(-2);
        } else if (i == 3) {
            setPlayState(-1);
        } else if (i == 2) {
            setPlayState(-3);
        } else if (i == 1) {
            setPlayState(-1);
        } else {
            setPlayState(-1);
        }
        setPlayState(-1);
        VideoPlayerConfig config = VideoViewManager.getConfig();
        if (config == null || config.mBuriedPointEvent == null) {
            return;
        }
        if (PlayerUtils.isConnected(this.mContext)) {
            config.mBuriedPointEvent.onError(this.mUrl, false);
        } else {
            config.mBuriedPointEvent.onError(this.mUrl, true);
        }
    }

    @Override // com.yc.kernel.inter.VideoPlayerListener
    public void onCompletion() {
        this.mPlayerContainer.setKeepScreenOn(false);
        this.mCurrentPosition = 0L;
        ProgressManager progressManager = this.mProgressManager;
        if (progressManager != null) {
            progressManager.saveProgress(this.mUrl, 0L);
        }
        setPlayState(5);
        VideoPlayerConfig config = VideoViewManager.getConfig();
        if (config == null || config.mBuriedPointEvent == null) {
            return;
        }
        config.mBuriedPointEvent.playerCompletion(this.mUrl);
    }

    @Override // com.yc.kernel.inter.VideoPlayerListener
    public void onInfo(int i, int i2) {
        if (i == 3) {
            setPlayState(3);
            if (this.mPlayerContainer.getWindowVisibility() != 0) {
                pause();
                return;
            }
            return;
        }
        if (i == 10001) {
            InterSurfaceView interSurfaceView = this.mRenderView;
            if (interSurfaceView != null) {
                interSurfaceView.setVideoRotation(i2);
                return;
            }
            return;
        }
        if (i == 701) {
            setPlayState(6);
        } else {
            if (i != 702) {
                return;
            }
            setPlayState(7);
        }
    }

    @Override // com.yc.kernel.inter.VideoPlayerListener
    public void onPrepared() {
        setPlayState(2);
        long j = this.mCurrentPosition;
        if (j > 0) {
            seekTo(j);
        }
    }

    public int getCurrentPlayerState() {
        return this.mCurrentPlayerState;
    }

    public int getCurrentPlayState() {
        return this.mCurrentPlayState;
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public long getTcpSpeed() {
        P p = this.mMediaPlayer;
        if (p != null) {
            return p.getTcpSpeed();
        }
        return 0L;
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void setSpeed(float f) {
        if (isInPlaybackState()) {
            this.mMediaPlayer.setSpeed(f);
        }
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public float getSpeed() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getSpeed();
        }
        return 1.0f;
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void setUrl(String str) {
        setUrl(str, null);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public String getUrl() {
        return this.mUrl;
    }

    public void setUrl(String str, Map<String, String> map) {
        this.mAssetFileDescriptor = null;
        this.mUrl = str;
        this.mHeaders = map;
        VideoPlayerConfig config = VideoViewManager.getConfig();
        if (config == null || config.mBuriedPointEvent == null) {
            return;
        }
        config.mBuriedPointEvent.playerIn(str);
    }

    public void setAssetFileDescriptor(AssetFileDescriptor assetFileDescriptor) {
        this.mUrl = null;
        this.mAssetFileDescriptor = assetFileDescriptor;
    }

    public void setVolume(float f, float f2) {
        P p = this.mMediaPlayer;
        if (p != null) {
            p.setVolume(f, f2);
        }
    }

    public void setProgressManager(ProgressManager progressManager) {
        this.mProgressManager = progressManager;
    }

    public void setLooping(boolean z) {
        this.mIsLooping = z;
        P p = this.mMediaPlayer;
        if (p != null) {
            p.setLooping(z);
        }
    }

    public void setPlayerFactory(PlayerFactory<P> playerFactory) {
        if (playerFactory == null) {
            throw new VideoException(20, "PlayerFactory can not be null!");
        }
        this.mPlayerFactory = playerFactory;
    }

    public void setRenderViewFactory(SurfaceFactory surfaceFactory) {
        if (surfaceFactory == null) {
            throw new VideoException(19, "RenderViewFactory can not be null!");
        }
        this.mRenderViewFactory = surfaceFactory;
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void startFullScreen() {
        ViewGroup decorView;
        if (this.mIsFullScreen || (decorView = VideoPlayerHelper.instance().getDecorView(this.mContext, this.mVideoController)) == null) {
            return;
        }
        this.mIsFullScreen = true;
        VideoPlayerHelper.instance().hideSysBar(decorView, this.mContext, this.mVideoController);
        removeView(this.mPlayerContainer);
        decorView.addView(this.mPlayerContainer);
        setPlayerState(1002);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void stopFullScreen() {
        ViewGroup decorView;
        if (this.mIsFullScreen && (decorView = VideoPlayerHelper.instance().getDecorView(this.mContext, this.mVideoController)) != null) {
            this.mIsFullScreen = false;
            VideoPlayerHelper.instance().showSysBar(decorView, this.mContext, this.mVideoController);
            decorView.removeView(this.mPlayerContainer);
            addView(this.mPlayerContainer);
            setPlayerState(1001);
        }
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public boolean isFullScreen() {
        return this.mIsFullScreen;
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void startTinyScreen() {
        ViewGroup contentView;
        if (this.mIsTinyScreen || (contentView = VideoPlayerHelper.instance().getContentView(this.mContext, this.mVideoController)) == null) {
            return;
        }
        removeView(this.mPlayerContainer);
        int i = this.mTinyScreenSize[0];
        if (i <= 0) {
            i = PlayerUtils.getScreenWidth(getContext(), false) / 2;
        }
        int i2 = this.mTinyScreenSize[1];
        if (i2 <= 0) {
            i2 = (i * 9) / 16;
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(i, i2);
        layoutParams.gravity = BadgeDrawable.BOTTOM_END;
        contentView.addView(this.mPlayerContainer, layoutParams);
        this.mIsTinyScreen = true;
        setPlayerState(1003);
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void stopTinyScreen() {
        ViewGroup contentView;
        if (this.mIsTinyScreen && (contentView = VideoPlayerHelper.instance().getContentView(this.mContext, this.mVideoController)) != null) {
            contentView.removeView(this.mPlayerContainer);
            addView(this.mPlayerContainer, new FrameLayout.LayoutParams(-1, -1));
            this.mIsTinyScreen = false;
            setPlayerState(1001);
        }
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public boolean isTinyScreen() {
        return this.mIsTinyScreen;
    }

    @Override // com.yc.kernel.inter.VideoPlayerListener
    public void onVideoSizeChanged(int i, int i2) {
        int[] iArr = this.mVideoSize;
        iArr[0] = i;
        iArr[1] = i2;
        InterSurfaceView interSurfaceView = this.mRenderView;
        if (interSurfaceView != null) {
            interSurfaceView.setScaleType(this.mCurrentScreenScaleType);
            this.mRenderView.setVideoSize(i, i2);
        }
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void setScreenScaleType(int i) {
        this.mCurrentScreenScaleType = i;
        InterSurfaceView interSurfaceView = this.mRenderView;
        if (interSurfaceView != null) {
            interSurfaceView.setScaleType(i);
        }
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public void setMirrorRotation(boolean z) {
        InterSurfaceView interSurfaceView = this.mRenderView;
        if (interSurfaceView != null) {
            interSurfaceView.getView().setScaleX(z ? -1.0f : 1.0f);
        }
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public Bitmap doScreenShot() {
        InterSurfaceView interSurfaceView = this.mRenderView;
        if (interSurfaceView != null) {
            return interSurfaceView.doScreenShot();
        }
        return null;
    }

    @Override // com.yc.video.player.InterVideoPlayer
    public int[] getVideoSize() {
        return this.mVideoSize;
    }

    @Override // android.view.View, com.yc.video.player.InterVideoPlayer
    public void setRotation(float f) {
        InterSurfaceView interSurfaceView = this.mRenderView;
        if (interSurfaceView != null) {
            interSurfaceView.setVideoRotation((int) f);
        }
    }

    protected void setPlayState(int i) {
        this.mCurrentPlayState = i;
        BaseVideoController baseVideoController = this.mVideoController;
        if (baseVideoController != null) {
            baseVideoController.setPlayState(i);
        }
        List<OnVideoStateListener> list = this.mOnStateChangeListeners;
        if (list != null) {
            for (OnVideoStateListener onVideoStateListener : PlayerUtils.getSnapshot(list)) {
                if (onVideoStateListener != null) {
                    onVideoStateListener.onPlayStateChanged(i);
                }
            }
        }
    }

    protected void setPlayerState(int i) {
        this.mCurrentPlayerState = i;
        BaseVideoController baseVideoController = this.mVideoController;
        if (baseVideoController != null) {
            baseVideoController.setPlayerState(i);
        }
        List<OnVideoStateListener> list = this.mOnStateChangeListeners;
        if (list != null) {
            for (OnVideoStateListener onVideoStateListener : PlayerUtils.getSnapshot(list)) {
                if (onVideoStateListener != null) {
                    onVideoStateListener.onPlayerStateChanged(i);
                }
            }
        }
    }

    public void addOnStateChangeListener(OnVideoStateListener onVideoStateListener) {
        if (this.mOnStateChangeListeners == null) {
            this.mOnStateChangeListeners = new ArrayList();
        }
        this.mOnStateChangeListeners.add(onVideoStateListener);
    }

    public void removeOnStateChangeListener(OnVideoStateListener onVideoStateListener) {
        List<OnVideoStateListener> list = this.mOnStateChangeListeners;
        if (list != null) {
            list.remove(onVideoStateListener);
        }
    }

    public void setOnStateChangeListener(OnVideoStateListener onVideoStateListener) {
        List<OnVideoStateListener> list = this.mOnStateChangeListeners;
        if (list == null) {
            this.mOnStateChangeListeners = new ArrayList();
        } else {
            list.clear();
        }
        this.mOnStateChangeListeners.add(onVideoStateListener);
    }

    public void clearOnStateChangeListeners() {
        List<OnVideoStateListener> list = this.mOnStateChangeListeners;
        if (list != null) {
            list.clear();
        }
    }

    public boolean onBackPressed() {
        BaseVideoController baseVideoController = this.mVideoController;
        return baseVideoController != null && baseVideoController.onBackPressed();
    }

    public void setVideoBuilder(VideoPlayerBuilder videoPlayerBuilder) {
        FrameLayout frameLayout = this.mPlayerContainer;
        if (frameLayout == null || videoPlayerBuilder == null) {
            return;
        }
        frameLayout.setBackgroundColor(videoPlayerBuilder.mColor);
        if (videoPlayerBuilder.mTinyScreenSize != null && videoPlayerBuilder.mTinyScreenSize.length > 0) {
            this.mTinyScreenSize = videoPlayerBuilder.mTinyScreenSize;
        }
        if (videoPlayerBuilder.mCurrentPosition > 0) {
            this.mCurrentPosition = videoPlayerBuilder.mCurrentPosition;
        }
        this.mEnableAudioFocus = videoPlayerBuilder.mEnableAudioFocus;
    }
}