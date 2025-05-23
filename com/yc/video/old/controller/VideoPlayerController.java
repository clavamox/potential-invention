package com.yc.video.old.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.yc.kernel.utils.VideoLogUtils;
import com.yc.video.R;
import com.yc.video.config.ConstantKeys;
import com.yc.video.config.VideoInfoBean;
import com.yc.video.old.dialog.ChangeClarityDialog;
import com.yc.video.old.listener.OnClarityChangedListener;
import com.yc.video.old.listener.OnPlayerStatesListener;
import com.yc.video.old.listener.OnPlayerTypeListener;
import com.yc.video.old.listener.OnVideoControlListener;
import com.yc.video.old.other.BatterReceiver;
import com.yc.video.old.other.NetChangedReceiver;
import com.yc.video.old.player.OldVideoPlayer;
import com.yc.video.tool.BaseToast;
import com.yc.video.tool.PlayerUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Deprecated
/* loaded from: classes.dex */
public class VideoPlayerController extends AbsVideoPlayerController implements View.OnClickListener {
    private List<VideoInfoBean> clarities;
    private int defaultClarityIndex;
    private boolean hasRegisterBatteryReceiver;
    private boolean hasRegisterNetReceiver;
    private ImageView mBack;
    private BroadcastReceiver mBatterReceiver;
    private ImageView mBattery;
    private LinearLayout mBottom;
    private ImageView mCenterStart;
    private LinearLayout mChangeBrightness;
    private ProgressBar mChangeBrightnessProgress;
    private LinearLayout mChangePosition;
    private TextView mChangePositionCurrent;
    private ProgressBar mChangePositionProgress;
    private LinearLayout mChangeVolume;
    private ProgressBar mChangeVolumeProgress;
    private TextView mClarity;
    private ChangeClarityDialog mClarityDialog;
    private LinearLayout mCompleted;
    private Context mContext;
    private CountDownTimer mDismissTopBottomCountDownTimer;
    private TextView mDuration;
    private LinearLayout mError;
    private FrameLayout mFlLock;
    private ImageView mFullScreen;
    private ImageView mImage;
    private boolean mIsAudioIconVisibility;
    private boolean mIsLock;
    private boolean mIsTopLayoutVisibility;
    private boolean mIsTvIconVisibility;
    private ImageView mIvAudio;
    private ImageView mIvDownload;
    private ImageView mIvHorAudio;
    private ImageView mIvHorTv;
    private ImageView mIvLock;
    private ImageView mIvMenu;
    private ImageView mIvShare;
    private TextView mLength;
    private LinearLayout mLine;
    private LinearLayout mLlHorizontal;
    private LinearLayout mLlTopOther;
    private TextView mLoadText;
    private LinearLayout mLoading;
    private OnPlayerStatesListener mOnPlayerStatesListener;
    private OnPlayerTypeListener mOnPlayerTypeListener;
    private ProgressBar mPbPlayBar;
    private TextView mPosition;
    private TextView mReplay;
    private ImageView mRestartPause;
    private TextView mRetry;
    private SeekBar mSeek;
    private TextView mShare;
    private TextView mTime;
    private TextView mTitle;
    private LinearLayout mTop;
    private TextView mTvError;
    private OnVideoControlListener mVideoControlListener;
    private NetChangedReceiver netChangedReceiver;
    private ProgressBar pbLoadingQq;
    private ProgressBar pbLoadingRing;
    private long time;
    private boolean topBottomVisible;

    public VideoPlayerController(Context context) {
        super(context);
        this.mIsLock = false;
        this.mIsTopLayoutVisibility = false;
        this.mIsTvIconVisibility = false;
        this.mIsAudioIconVisibility = false;
        this.mContext = context;
        init();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= 26) {
            ProgressBar progressBar = this.pbLoadingRing;
            if (progressBar != null && progressBar.getVisibility() == 0 && this.pbLoadingRing.isAnimating()) {
                this.pbLoadingRing.clearAnimation();
            }
            ProgressBar progressBar2 = this.pbLoadingQq;
            if (progressBar2 != null && progressBar2.getVisibility() == 0 && this.pbLoadingQq.isAnimating()) {
                this.pbLoadingQq.clearAnimation();
                return;
            }
            return;
        }
        ProgressBar progressBar3 = this.pbLoadingRing;
        if (progressBar3 != null && progressBar3.getVisibility() == 0) {
            this.pbLoadingRing.clearAnimation();
        }
        ProgressBar progressBar4 = this.pbLoadingQq;
        if (progressBar4 == null || progressBar4.getVisibility() != 0) {
            return;
        }
        this.pbLoadingQq.clearAnimation();
    }

    private void registerNetChangedReceiver() {
        if (this.hasRegisterNetReceiver) {
            return;
        }
        if (this.netChangedReceiver == null) {
            this.netChangedReceiver = new NetChangedReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            this.mContext.registerReceiver(this.netChangedReceiver, intentFilter);
            VideoLogUtils.i("广播监听---------注册网络监听广播");
        }
        this.hasRegisterNetReceiver = true;
    }

    private void unRegisterNetChangedReceiver() {
        if (this.hasRegisterNetReceiver) {
            NetChangedReceiver netChangedReceiver = this.netChangedReceiver;
            if (netChangedReceiver != null) {
                this.mContext.unregisterReceiver(netChangedReceiver);
                VideoLogUtils.i("广播监听---------解绑注册网络监听广播");
            }
            this.hasRegisterNetReceiver = false;
        }
    }

    private void registerBatterReceiver() {
        if (this.hasRegisterBatteryReceiver) {
            return;
        }
        BatterReceiver batterReceiver = new BatterReceiver();
        this.mBatterReceiver = batterReceiver;
        this.mContext.registerReceiver(batterReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        this.hasRegisterBatteryReceiver = true;
        VideoLogUtils.i("广播监听---------注册电池监听广播");
    }

    private void unRegisterBatterReceiver() {
        if (this.hasRegisterBatteryReceiver) {
            this.mContext.unregisterReceiver(this.mBatterReceiver);
            this.hasRegisterBatteryReceiver = false;
            VideoLogUtils.i("广播监听---------解绑电池监听广播");
        }
    }

    private void init() {
        LayoutInflater.from(this.mContext).inflate(R.layout.old_video_player, (ViewGroup) this, true);
        initFindViewById();
        initListener();
        registerNetChangedReceiver();
    }

    private void initFindViewById() {
        this.mCenterStart = (ImageView) findViewById(R.id.center_start);
        this.mImage = (ImageView) findViewById(R.id.image);
        this.mTop = (LinearLayout) findViewById(R.id.top);
        this.mBack = (ImageView) findViewById(R.id.back);
        this.mTitle = (TextView) findViewById(R.id.title);
        this.mLlTopOther = (LinearLayout) findViewById(R.id.ll_top_other);
        this.mIvDownload = (ImageView) findViewById(R.id.iv_download);
        this.mIvAudio = (ImageView) findViewById(R.id.iv_audio);
        this.mIvShare = (ImageView) findViewById(R.id.iv_share);
        this.mIvMenu = (ImageView) findViewById(R.id.iv_menu);
        this.mLlHorizontal = (LinearLayout) findViewById(R.id.ll_horizontal);
        this.mIvHorAudio = (ImageView) findViewById(R.id.iv_hor_audio);
        this.mIvHorTv = (ImageView) findViewById(R.id.iv_hor_tv);
        this.mBattery = (ImageView) findViewById(R.id.battery);
        this.mTime = (TextView) findViewById(R.id.time);
        this.mBottom = (LinearLayout) findViewById(R.id.bottom);
        this.mRestartPause = (ImageView) findViewById(R.id.restart_or_pause);
        this.mPosition = (TextView) findViewById(R.id.position);
        this.mDuration = (TextView) findViewById(R.id.duration);
        this.mSeek = (SeekBar) findViewById(R.id.seek);
        this.mFullScreen = (ImageView) findViewById(R.id.full_screen);
        this.mClarity = (TextView) findViewById(R.id.clarity);
        this.mLength = (TextView) findViewById(R.id.length);
        this.mLoading = (LinearLayout) findViewById(R.id.loading);
        this.pbLoadingRing = (ProgressBar) findViewById(R.id.pb_loading_ring);
        this.pbLoadingQq = (ProgressBar) findViewById(R.id.pb_loading_qq);
        this.mLoadText = (TextView) findViewById(R.id.load_text);
        this.mChangePosition = (LinearLayout) findViewById(R.id.change_position);
        this.mChangePositionCurrent = (TextView) findViewById(R.id.change_position_current);
        this.mChangePositionProgress = (ProgressBar) findViewById(R.id.change_position_progress);
        this.mChangeBrightness = (LinearLayout) findViewById(R.id.change_brightness);
        this.mChangeBrightnessProgress = (ProgressBar) findViewById(R.id.change_brightness_progress);
        this.mChangeVolume = (LinearLayout) findViewById(R.id.change_volume);
        this.mChangeVolumeProgress = (ProgressBar) findViewById(R.id.change_volume_progress);
        this.mError = (LinearLayout) findViewById(R.id.error);
        this.mTvError = (TextView) findViewById(R.id.tv_error);
        this.mRetry = (TextView) findViewById(R.id.retry);
        this.mCompleted = (LinearLayout) findViewById(R.id.completed);
        this.mReplay = (TextView) findViewById(R.id.replay);
        this.mShare = (TextView) findViewById(R.id.share);
        this.mFlLock = (FrameLayout) findViewById(R.id.fl_lock);
        this.mIvLock = (ImageView) findViewById(R.id.iv_lock);
        this.mLine = (LinearLayout) findViewById(R.id.line);
        this.mPbPlayBar = (ProgressBar) findViewById(R.id.pb_play_bar);
        setTopVisibility(this.mIsTopLayoutVisibility);
        this.mIvShare.setVisibility(4);
    }

    private void initListener() {
        this.mCenterStart.setOnClickListener(this);
        this.mBack.setOnClickListener(this);
        this.mIvDownload.setOnClickListener(this);
        this.mIvShare.setOnClickListener(this);
        this.mIvAudio.setOnClickListener(this);
        this.mIvMenu.setOnClickListener(this);
        this.mIvHorAudio.setOnClickListener(this);
        this.mIvHorTv.setOnClickListener(this);
        this.mRestartPause.setOnClickListener(this);
        this.mFullScreen.setOnClickListener(this);
        this.mClarity.setOnClickListener(this);
        this.mRetry.setOnClickListener(this);
        this.mReplay.setOnClickListener(this);
        this.mShare.setOnClickListener(this);
        this.mFlLock.setOnClickListener(this);
        this.mSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.yc.video.old.controller.VideoPlayerController.1
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (VideoPlayerController.this.mVideoPlayer.isBufferingPaused() || VideoPlayerController.this.mVideoPlayer.isPaused()) {
                    VideoPlayerController.this.mVideoPlayer.restart();
                }
                VideoPlayerController.this.mVideoPlayer.seekTo((long) ((VideoPlayerController.this.mVideoPlayer.getDuration() * seekBar.getProgress()) / 100.0f));
                VideoPlayerController.this.startDismissTopBottomTimer();
            }
        });
        setOnClickListener(this);
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void setTopVisibility(boolean z) {
        this.mIsTopLayoutVisibility = z;
        if (z) {
            this.mLlTopOther.setVisibility(0);
        } else {
            this.mLlTopOther.setVisibility(8);
        }
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void setTvAndAudioVisibility(boolean z, boolean z2) {
        this.mIsTvIconVisibility = z;
        this.mIsAudioIconVisibility = z2;
        this.mIvHorTv.setVisibility(z ? 0 : 8);
        this.mIvHorAudio.setVisibility(this.mIsAudioIconVisibility ? 0 : 8);
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void setLoadingType(int i) {
        if (i == 1) {
            this.pbLoadingRing.setVisibility(0);
            this.pbLoadingQq.setVisibility(8);
        } else if (i == 2) {
            this.pbLoadingRing.setVisibility(8);
            this.pbLoadingQq.setVisibility(0);
        } else {
            this.pbLoadingRing.setVisibility(0);
            this.pbLoadingQq.setVisibility(8);
        }
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void setHideTime(long j) {
        this.time = j;
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void setTitle(String str) {
        if (str == null || str.length() <= 0) {
            return;
        }
        this.mTitle.setText(str);
    }

    @Override // com.yc.video.old.controller.IVideoController
    public ImageView imageView() {
        return this.mImage;
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void setImage(int i) {
        this.mImage.setImageResource(i);
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void setLength(long j) {
        if (j > 0) {
            this.mLength.setVisibility(0);
            this.mLength.setText(PlayerUtils.formatTime(j));
        } else {
            this.mLength.setVisibility(8);
        }
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void setLength(String str) {
        if (str != null && str.length() > 0) {
            this.mLength.setVisibility(0);
            this.mLength.setText(str);
        } else {
            this.mLength.setVisibility(8);
        }
    }

    @Override // com.yc.video.old.controller.AbsVideoPlayerController
    public void setVideoPlayer(OldVideoPlayer oldVideoPlayer) {
        super.setVideoPlayer(oldVideoPlayer);
        List<VideoInfoBean> list = this.clarities;
        if (list == null || list.size() <= 1 || this.clarities.size() <= this.defaultClarityIndex) {
            return;
        }
        this.mVideoPlayer.setUp(this.clarities.get(this.defaultClarityIndex).getVideoUrl(), null);
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void setTopPadding(float f) {
        if (f == 0.0f) {
            f = 10.0f;
        }
        this.mTop.setPadding(PlayerUtils.dp2px(this.mContext, 10.0f), PlayerUtils.dp2px(this.mContext, f), PlayerUtils.dp2px(this.mContext, 10.0f), 0);
        this.mTop.invalidate();
    }

    @Override // com.yc.video.old.controller.IVideoController
    public boolean getLock() {
        return this.mIsLock;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        VideoLogUtils.i("如果锁屏2，则屏蔽返回键");
        return !getLock() && super.onTouchEvent(motionEvent);
    }

    public void setClarity(final List<VideoInfoBean> list, int i) {
        if (list == null || list.size() <= 1) {
            return;
        }
        this.clarities = list;
        this.defaultClarityIndex = i;
        ArrayList arrayList = new ArrayList();
        for (VideoInfoBean videoInfoBean : list) {
            arrayList.add(videoInfoBean.getGrade() + " " + videoInfoBean.getP());
        }
        this.mClarity.setText(list.get(i).getGrade());
        ChangeClarityDialog changeClarityDialog = new ChangeClarityDialog(this.mContext);
        this.mClarityDialog = changeClarityDialog;
        changeClarityDialog.setClarityGrade(arrayList, i);
        this.mClarityDialog.setOnClarityCheckedListener(new OnClarityChangedListener() { // from class: com.yc.video.old.controller.VideoPlayerController.2
            @Override // com.yc.video.old.listener.OnClarityChangedListener
            public void onClarityChanged(int i2) {
                VideoInfoBean videoInfoBean2 = (VideoInfoBean) list.get(i2);
                VideoPlayerController.this.mClarity.setText(videoInfoBean2.getGrade());
                long currentPosition = VideoPlayerController.this.mVideoPlayer.getCurrentPosition();
                VideoPlayerController.this.mVideoPlayer.releasePlayer();
                VideoPlayerController.this.mVideoPlayer.setUp(videoInfoBean2.getVideoUrl(), null);
                VideoPlayerController.this.mVideoPlayer.start(currentPosition);
            }

            @Override // com.yc.video.old.listener.OnClarityChangedListener
            public void onClarityNotChanged() {
                VideoPlayerController.this.setTopBottomVisible(true);
            }
        });
        if (this.mVideoPlayer != null) {
            this.mVideoPlayer.setUp(list.get(i).getVideoUrl(), null);
        }
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void onPlayStateChanged(int i) {
        switch (i) {
            case -1:
                stateError();
                break;
            case 1:
                startPreparing();
                break;
            case 2:
                startUpdateProgressTimer();
                cancelUpdateNetSpeedTimer();
                break;
            case 3:
                statePlaying();
                break;
            case 4:
                statePaused();
                break;
            case 5:
                stateBufferingPlaying();
                break;
            case 6:
                stateBufferingPaused();
                break;
            case 7:
                stateCompleted();
                break;
        }
    }

    private void startPreparing() {
        this.mLoading.setVisibility(0);
        this.mLoadText.setText("正在准备...");
        this.mImage.setVisibility(8);
        this.mError.setVisibility(8);
        this.mCompleted.setVisibility(8);
        this.mCenterStart.setVisibility(8);
        this.mLength.setVisibility(8);
        this.mPbPlayBar.setVisibility(8);
        setTopBottomVisible(false);
        startUpdateNetSpeedTimer();
        startUpdateProgressTimer();
    }

    private void statePlaying() {
        this.mLoading.setVisibility(8);
        this.mPbPlayBar.setVisibility(0);
        this.mRestartPause.setImageResource(R.drawable.ic_player_pause);
        this.mCenterStart.setImageResource(R.drawable.icon_pause_center);
        setTopBottomVisible(true);
        startDismissTopBottomTimer();
        cancelUpdateNetSpeedTimer();
    }

    private void statePaused() {
        this.mLoading.setVisibility(8);
        this.mRestartPause.setImageResource(R.drawable.ic_player_start);
        this.mCenterStart.setImageResource(R.drawable.icon_play_center);
        setTopBottomVisible(true);
        cancelDismissTopBottomTimer();
        cancelUpdateNetSpeedTimer();
    }

    private void stateBufferingPlaying() {
        this.mLoading.setVisibility(0);
        setTopBottomVisible(false);
        this.mRestartPause.setImageResource(R.drawable.ic_player_pause);
        this.mCenterStart.setImageResource(R.drawable.icon_pause_center);
        this.mLoadText.setText("正在准备...");
        startDismissTopBottomTimer();
        cancelUpdateNetSpeedTimer();
    }

    private void stateBufferingPaused() {
        this.mLoading.setVisibility(0);
        this.mRestartPause.setImageResource(R.drawable.ic_player_start);
        this.mCenterStart.setImageResource(R.drawable.icon_play_center);
        this.mLoadText.setText("正在准备...");
        setTopBottomVisible(false);
        cancelDismissTopBottomTimer();
        startUpdateNetSpeedTimer();
    }

    private void stateError() {
        this.mLoading.setVisibility(8);
        setTopBottomVisible(false);
        this.mError.setVisibility(0);
        cancelUpdateProgressTimer();
        cancelUpdateNetSpeedTimer();
        if (!PlayerUtils.isConnected(this.mContext)) {
            this.mTvError.setText("没有网络，请链接网络");
        } else {
            this.mTvError.setText("播放错误，请重试");
        }
    }

    private void stateCompleted() {
        cancelUpdateProgressTimer();
        cancelUpdateNetSpeedTimer();
        setTopBottomVisible(false);
        this.mLoading.setVisibility(8);
        this.mImage.setVisibility(0);
        this.mCompleted.setVisibility(0);
        OnPlayerStatesListener onPlayerStatesListener = this.mOnPlayerStatesListener;
        if (onPlayerStatesListener != null) {
            onPlayerStatesListener.onPlayerStates(101);
        }
        this.mPbPlayBar.setProgress(100);
        unRegisterNetChangedReceiver();
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void onPlayModeChanged(int i) {
        switch (i) {
            case 1001:
                this.mFlLock.setVisibility(8);
                this.mFullScreen.setImageResource(R.drawable.ic_player_open);
                this.mFullScreen.setVisibility(0);
                this.mClarity.setVisibility(8);
                this.mLlHorizontal.setVisibility(8);
                unRegisterBatterReceiver();
                this.mIsLock = false;
                OnPlayerTypeListener onPlayerTypeListener = this.mOnPlayerTypeListener;
                if (onPlayerTypeListener != null) {
                    onPlayerTypeListener.onPlayerPattern(1001);
                }
                setTopBottomVisible(true);
                this.mBattery.setVisibility(8);
                VideoLogUtils.d("播放模式--------普通模式");
                break;
            case 1002:
                this.mFlLock.setVisibility(0);
                this.mFullScreen.setVisibility(0);
                this.mFullScreen.setImageResource(R.drawable.ic_player_close);
                List<VideoInfoBean> list = this.clarities;
                if (list != null && list.size() > 1) {
                    this.mClarity.setVisibility(0);
                }
                this.mLlHorizontal.setVisibility(0);
                this.mIvHorTv.setVisibility(this.mIsTvIconVisibility ? 0 : 8);
                this.mIvHorAudio.setVisibility(this.mIsAudioIconVisibility ? 0 : 8);
                setTopBottomVisible(true);
                registerBatterReceiver();
                OnPlayerTypeListener onPlayerTypeListener2 = this.mOnPlayerTypeListener;
                if (onPlayerTypeListener2 != null) {
                    onPlayerTypeListener2.onPlayerPattern(1002);
                }
                VideoLogUtils.d("播放模式--------全屏模式");
                break;
            case 1003:
                this.mFlLock.setVisibility(8);
                this.mFullScreen.setImageResource(R.drawable.ic_player_open);
                this.mFullScreen.setVisibility(0);
                this.mIsLock = false;
                OnPlayerTypeListener onPlayerTypeListener3 = this.mOnPlayerTypeListener;
                if (onPlayerTypeListener3 != null) {
                    onPlayerTypeListener3.onPlayerPattern(1003);
                }
                VideoLogUtils.d("播放模式--------小窗口模式");
                break;
        }
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void reset() {
        this.topBottomVisible = false;
        cancelUpdateProgressTimer();
        cancelDismissTopBottomTimer();
        this.mSeek.setProgress(0);
        this.mSeek.setSecondaryProgress(0);
        this.mPbPlayBar.setProgress(0);
        this.mCenterStart.setVisibility(0);
        this.mLength.setVisibility(8);
        this.mFlLock.setVisibility(8);
        this.mImage.setVisibility(0);
        this.mBottom.setVisibility(8);
        this.mLoading.setVisibility(8);
        this.mError.setVisibility(8);
        this.mCompleted.setVisibility(8);
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void destroy() {
        unRegisterNetChangedReceiver();
        unRegisterBatterReceiver();
        cancelUpdateProgressTimer();
        cancelUpdateNetSpeedTimer();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mCenterStart) {
            startVideo();
            return;
        }
        if (view == this.mBack) {
            if (this.mVideoPlayer.isFullScreen()) {
                this.mVideoPlayer.exitFullScreen();
                return;
            }
            if (this.mVideoPlayer.isTinyWindow()) {
                this.mVideoPlayer.exitTinyWindow();
                return;
            }
            OnPlayerStatesListener onPlayerStatesListener = this.mOnPlayerStatesListener;
            if (onPlayerStatesListener != null) {
                onPlayerStatesListener.onPlayerStates(104);
                return;
            } else {
                VideoLogUtils.d("返回键逻辑，如果是全屏，则先退出全屏；如果是小窗口，则退出小窗口；如果两种情况都不是，执行逻辑交给使用者自己实现");
                return;
            }
        }
        if (view == this.mRestartPause) {
            if (PlayerUtils.isConnected(this.mContext)) {
                if (this.mVideoPlayer.isPlaying() || this.mVideoPlayer.isBufferingPlaying()) {
                    this.mVideoPlayer.pause();
                    OnPlayerStatesListener onPlayerStatesListener2 = this.mOnPlayerStatesListener;
                    if (onPlayerStatesListener2 != null) {
                        onPlayerStatesListener2.onPlayerStates(102);
                        return;
                    }
                    return;
                }
                if (this.mVideoPlayer.isPaused() || this.mVideoPlayer.isBufferingPaused()) {
                    this.mVideoPlayer.restart();
                    OnPlayerStatesListener onPlayerStatesListener3 = this.mOnPlayerStatesListener;
                    if (onPlayerStatesListener3 != null) {
                        onPlayerStatesListener3.onPlayerStates(103);
                        return;
                    }
                    return;
                }
                return;
            }
            BaseToast.showRoundRectToast("请检测是否有网络");
            return;
        }
        if (view == this.mFullScreen) {
            if (this.mVideoPlayer.isNormal() || this.mVideoPlayer.isTinyWindow()) {
                this.mFlLock.setVisibility(0);
                this.mIsLock = false;
                this.mIvLock.setImageResource(R.drawable.ic_player_lock_close);
                this.mVideoPlayer.enterFullScreen();
                return;
            }
            if (this.mVideoPlayer.isFullScreen()) {
                this.mFlLock.setVisibility(8);
                this.mVideoPlayer.exitFullScreen();
                return;
            }
            return;
        }
        if (view == this.mClarity) {
            setTopBottomVisible(false);
            this.mClarityDialog.show();
            return;
        }
        if (view == this.mRetry) {
            if (PlayerUtils.isConnected(this.mContext)) {
                startPreparing();
                this.mVideoPlayer.restart();
                return;
            } else {
                BaseToast.showRoundRectToast("请检测是否有网络");
                return;
            }
        }
        if (view == this.mReplay) {
            if (PlayerUtils.isConnected(this.mContext)) {
                this.mRetry.performClick();
                return;
            } else {
                BaseToast.showRoundRectToast("请检测是否有网络");
                return;
            }
        }
        if (view == this.mShare) {
            OnVideoControlListener onVideoControlListener = this.mVideoControlListener;
            if (onVideoControlListener == null) {
                VideoLogUtils.d("请在初始化的时候设置分享监听事件");
                return;
            } else {
                onVideoControlListener.onVideoControlClick(ConstantKeys.VideoControl.SHARE);
                return;
            }
        }
        if (view == this.mFlLock) {
            setLock(this.mIsLock);
            return;
        }
        if (view == this.mIvDownload) {
            OnVideoControlListener onVideoControlListener2 = this.mVideoControlListener;
            if (onVideoControlListener2 == null) {
                VideoLogUtils.d("请在初始化的时候设置下载监听事件");
                return;
            } else {
                onVideoControlListener2.onVideoControlClick(ConstantKeys.VideoControl.DOWNLOAD);
                return;
            }
        }
        if (view == this.mIvAudio) {
            OnVideoControlListener onVideoControlListener3 = this.mVideoControlListener;
            if (onVideoControlListener3 == null) {
                VideoLogUtils.d("请在初始化的时候设置切换监听事件");
                return;
            } else {
                onVideoControlListener3.onVideoControlClick(ConstantKeys.VideoControl.AUDIO);
                return;
            }
        }
        if (view == this.mIvShare) {
            OnVideoControlListener onVideoControlListener4 = this.mVideoControlListener;
            if (onVideoControlListener4 == null) {
                VideoLogUtils.d("请在初始化的时候设置分享监听事件");
                return;
            } else {
                onVideoControlListener4.onVideoControlClick(ConstantKeys.VideoControl.SHARE);
                return;
            }
        }
        if (view == this.mIvMenu) {
            OnVideoControlListener onVideoControlListener5 = this.mVideoControlListener;
            if (onVideoControlListener5 == null) {
                VideoLogUtils.d("请在初始化的时候设置分享监听事件");
                return;
            } else {
                onVideoControlListener5.onVideoControlClick(ConstantKeys.VideoControl.MENU);
                return;
            }
        }
        if (view == this.mIvHorAudio) {
            OnVideoControlListener onVideoControlListener6 = this.mVideoControlListener;
            if (onVideoControlListener6 == null) {
                VideoLogUtils.d("请在初始化的时候设置横向音频监听事件");
                return;
            } else {
                onVideoControlListener6.onVideoControlClick(ConstantKeys.VideoControl.HOR_AUDIO);
                return;
            }
        }
        if (view == this.mIvHorTv) {
            OnVideoControlListener onVideoControlListener7 = this.mVideoControlListener;
            if (onVideoControlListener7 == null) {
                VideoLogUtils.d("请在初始化的时候设置横向Tv监听事件");
                return;
            } else {
                onVideoControlListener7.onVideoControlClick(ConstantKeys.VideoControl.TV);
                return;
            }
        }
        if (view == this) {
            if (this.mVideoPlayer.isFullScreen() || this.mVideoPlayer.isNormal()) {
                if (this.mVideoPlayer.isPlaying() || this.mVideoPlayer.isPaused() || this.mVideoPlayer.isBufferingPlaying() || this.mVideoPlayer.isBufferingPaused()) {
                    setTopBottomVisible(!this.topBottomVisible);
                }
            }
        }
    }

    private void startVideo() {
        if (this.mVideoPlayer.isIdle()) {
            this.mVideoPlayer.start();
            return;
        }
        if (this.mVideoPlayer.isPlaying() || this.mVideoPlayer.isBufferingPlaying()) {
            this.mVideoPlayer.pause();
        } else if (this.mVideoPlayer.isPaused() || this.mVideoPlayer.isBufferingPaused()) {
            this.mVideoPlayer.restart();
        }
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void onBatterStateChanged(int i) {
        switch (i) {
            case 80:
                this.mBattery.setImageResource(R.drawable.battery_charging);
                break;
            case 81:
                this.mBattery.setImageResource(R.drawable.battery_full);
                break;
            case 82:
                this.mBattery.setImageResource(R.drawable.battery_10);
                break;
            case 83:
                this.mBattery.setImageResource(R.drawable.battery_20);
                break;
            case 84:
                this.mBattery.setImageResource(R.drawable.battery_50);
                break;
            case 85:
                this.mBattery.setImageResource(R.drawable.battery_80);
                break;
            case 86:
                this.mBattery.setImageResource(R.drawable.battery_100);
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTopBottomVisible(boolean z) {
        setCenterVisible(z);
        this.mTop.setVisibility(z ? 0 : 8);
        this.mBottom.setVisibility(z ? 0 : 8);
        this.mLine.setVisibility(z ? 8 : 0);
        this.topBottomVisible = z;
        if (z) {
            if (this.mVideoPlayer.isPaused() || this.mVideoPlayer.isBufferingPaused()) {
                return;
            }
            startDismissTopBottomTimer();
            return;
        }
        cancelDismissTopBottomTimer();
    }

    private void setCenterVisible(boolean z) {
        this.mCenterStart.setVisibility(z ? 0 : 8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDismissTopBottomTimer() {
        if (this.time == 0) {
            this.time = 8000L;
        }
        cancelDismissTopBottomTimer();
        if (this.mDismissTopBottomCountDownTimer == null) {
            long j = this.time;
            this.mDismissTopBottomCountDownTimer = new CountDownTimer(j, j) { // from class: com.yc.video.old.controller.VideoPlayerController.3
                @Override // android.os.CountDownTimer
                public void onTick(long j2) {
                }

                @Override // android.os.CountDownTimer
                public void onFinish() {
                    VideoPlayerController.this.setTopBottomVisible(false);
                }
            };
        }
        this.mDismissTopBottomCountDownTimer.start();
    }

    private void cancelDismissTopBottomTimer() {
        CountDownTimer countDownTimer = this.mDismissTopBottomCountDownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void setLock(boolean z) {
        if (z) {
            this.mIsLock = false;
            this.mIvLock.setImageResource(R.drawable.ic_player_lock_open);
        } else {
            this.mIsLock = true;
            this.mIvLock.setImageResource(R.drawable.ic_player_lock_close);
        }
        setTopBottomVisible(!this.mIsLock);
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void updateNetSpeedProgress() {
        long tcpSpeed = this.mVideoPlayer.getTcpSpeed();
        VideoLogUtils.i("获取网络加载速度++++++++" + tcpSpeed);
        if (tcpSpeed > 0) {
            this.mLoading.setVisibility(0);
            this.mLoadText.setText("网速" + ((int) (tcpSpeed / 1024)) + "kb");
        }
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void updateProgress() {
        long currentPosition = this.mVideoPlayer.getCurrentPosition();
        long duration = this.mVideoPlayer.getDuration();
        this.mSeek.setSecondaryProgress(this.mVideoPlayer.getBufferPercentage());
        int i = (int) ((currentPosition * 100.0f) / duration);
        this.mSeek.setProgress(i);
        this.mPbPlayBar.setProgress(i);
        this.mPosition.setText(PlayerUtils.formatTime(currentPosition));
        this.mDuration.setText(PlayerUtils.formatTime(duration));
        this.mTime.setText(new SimpleDateFormat("HH:mm", Locale.CHINA).format(new Date()));
        VideoLogUtils.i("获取网络加载速度---------" + this.mVideoPlayer.getTcpSpeed());
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void showChangePosition(long j, int i) {
        this.mChangePosition.setVisibility(0);
        long j2 = (long) ((j * i) / 100.0f);
        this.mChangePositionCurrent.setText(PlayerUtils.formatTime(j2));
        this.mChangePositionProgress.setProgress(i);
        this.mSeek.setProgress(i);
        this.mPbPlayBar.setProgress(i);
        this.mPosition.setText(PlayerUtils.formatTime(j2));
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void hideChangePosition() {
        this.mChangePosition.setVisibility(8);
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void showChangeVolume(int i) {
        this.mChangeVolume.setVisibility(0);
        this.mChangeVolumeProgress.setProgress(i);
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void hideChangeVolume() {
        this.mChangeVolume.setVisibility(8);
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void showChangeBrightness(int i) {
        this.mChangeBrightness.setVisibility(0);
        this.mChangeBrightnessProgress.setProgress(i);
    }

    @Override // com.yc.video.old.controller.IVideoController
    public void hideChangeBrightness() {
        this.mChangeBrightness.setVisibility(8);
    }

    public void setOnVideoControlListener(OnVideoControlListener onVideoControlListener) {
        this.mVideoControlListener = onVideoControlListener;
    }

    public void setOnPlayerTypeListener(OnPlayerTypeListener onPlayerTypeListener) {
        this.mOnPlayerTypeListener = onPlayerTypeListener;
    }

    public void setOnPlayerStatesListener(OnPlayerStatesListener onPlayerStatesListener) {
        this.mOnPlayerStatesListener = onPlayerStatesListener;
    }
}