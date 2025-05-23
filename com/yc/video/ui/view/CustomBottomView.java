package com.yc.video.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.config.VideoPlayerConfig;
import com.yc.video.tool.PlayerUtils;

/* loaded from: classes.dex */
public class CustomBottomView extends FrameLayout implements InterControlView, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private OnToastListener listener;
    private Context mContext;
    protected ControlWrapper mControlWrapper;
    private boolean mIsDragging;
    private boolean mIsShowBottomProgress;
    private ImageView mIvFullscreen;
    private ImageView mIvPlay;
    private LinearLayout mLlBottomContainer;
    private ProgressBar mPbBottomProgress;
    private SeekBar mSeekBar;
    private TextView mTvClarity;
    private TextView mTvCurrTime;
    private TextView mTvTotalTime;

    public interface OnToastListener {
        void showToastOrDialog();
    }

    @Override // com.yc.video.ui.view.InterControlView
    public View getView() {
        return this;
    }

    public CustomBottomView(Context context) {
        super(context);
        this.mIsShowBottomProgress = true;
        init(context);
    }

    public CustomBottomView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsShowBottomProgress = true;
        init(context);
    }

    public CustomBottomView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsShowBottomProgress = true;
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setVisibility(8);
        initFindViewById(LayoutInflater.from(getContext()).inflate(getLayoutId(), (ViewGroup) this, true));
        initListener();
    }

    private void initFindViewById(View view) {
        this.mLlBottomContainer = (LinearLayout) view.findViewById(R.id.ll_bottom_container);
        this.mIvPlay = (ImageView) view.findViewById(R.id.iv_play);
        this.mTvCurrTime = (TextView) view.findViewById(R.id.tv_curr_time);
        this.mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
        this.mTvTotalTime = (TextView) view.findViewById(R.id.tv_total_time);
        this.mTvClarity = (TextView) view.findViewById(R.id.tv_clarity);
        this.mIvFullscreen = (ImageView) view.findViewById(R.id.iv_fullscreen);
        this.mPbBottomProgress = (ProgressBar) view.findViewById(R.id.pb_bottom_progress);
    }

    private void initListener() {
        this.mIvFullscreen.setOnClickListener(this);
        this.mSeekBar.setOnSeekBarChangeListener(this);
        this.mIvPlay.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mIvFullscreen) {
            toggleFullScreen();
        } else if (view == this.mIvPlay) {
            this.mControlWrapper.togglePlay();
        }
    }

    protected int getLayoutId() {
        return R.layout.custom_video_player_bottom;
    }

    public void showBottomProgress(boolean z) {
        this.mIsShowBottomProgress = z;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void attach(ControlWrapper controlWrapper) {
        this.mControlWrapper = controlWrapper;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onVisibilityChanged(boolean z, Animation animation) {
        if (z) {
            this.mLlBottomContainer.setVisibility(0);
            if (animation != null) {
                this.mLlBottomContainer.startAnimation(animation);
            }
            if (this.mIsShowBottomProgress) {
                this.mPbBottomProgress.setVisibility(8);
                return;
            }
            return;
        }
        this.mLlBottomContainer.setVisibility(8);
        if (animation != null) {
            this.mLlBottomContainer.startAnimation(animation);
        }
        if (this.mIsShowBottomProgress) {
            this.mPbBottomProgress.setVisibility(0);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(300L);
            this.mPbBottomProgress.startAnimation(alphaAnimation);
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayStateChanged(int i) {
        switch (i) {
            case -1:
            case 1:
            case 2:
            case 8:
            case 9:
                setVisibility(8);
                break;
            case 0:
            case 5:
                setVisibility(8);
                this.mPbBottomProgress.setProgress(0);
                this.mPbBottomProgress.setSecondaryProgress(0);
                this.mSeekBar.setProgress(0);
                this.mSeekBar.setSecondaryProgress(0);
                break;
            case 3:
                this.mIvPlay.setSelected(true);
                if (this.mIsShowBottomProgress) {
                    if (this.mControlWrapper.isShowing()) {
                        this.mPbBottomProgress.setVisibility(8);
                        this.mLlBottomContainer.setVisibility(0);
                    } else {
                        this.mLlBottomContainer.setVisibility(8);
                        this.mPbBottomProgress.setVisibility(0);
                    }
                } else {
                    this.mLlBottomContainer.setVisibility(8);
                }
                setVisibility(0);
                this.mControlWrapper.startProgress();
                break;
            case 4:
                this.mIvPlay.setSelected(false);
                break;
            case 6:
            case 7:
                this.mIvPlay.setSelected(this.mControlWrapper.isPlaying());
                break;
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayerStateChanged(int i) {
        if (i == 1001) {
            this.mIvFullscreen.setSelected(false);
        } else if (i == 1002) {
            this.mIvFullscreen.setSelected(true);
        }
        Activity scanForActivity = PlayerUtils.scanForActivity(this.mContext);
        if (scanForActivity == null || !this.mControlWrapper.hasCutout()) {
            return;
        }
        int requestedOrientation = scanForActivity.getRequestedOrientation();
        int cutoutHeight = this.mControlWrapper.getCutoutHeight();
        if (requestedOrientation == 1) {
            this.mLlBottomContainer.setPadding(0, 0, 0, 0);
            this.mPbBottomProgress.setPadding(0, 0, 0, 0);
        } else if (requestedOrientation == 0) {
            this.mLlBottomContainer.setPadding(cutoutHeight, 0, 0, 0);
            this.mPbBottomProgress.setPadding(cutoutHeight, 0, 0, 0);
        } else if (requestedOrientation == 8) {
            this.mLlBottomContainer.setPadding(0, 0, cutoutHeight, 0);
            this.mPbBottomProgress.setPadding(0, 0, cutoutHeight, 0);
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void setProgress(int i, int i2) {
        if (this.mIsDragging) {
            return;
        }
        SeekBar seekBar = this.mSeekBar;
        if (seekBar != null) {
            if (i > 0) {
                seekBar.setEnabled(true);
                int max = (int) (((i2 * 1.0d) / i) * this.mSeekBar.getMax());
                this.mSeekBar.setProgress(max);
                this.mPbBottomProgress.setProgress(max);
            } else {
                seekBar.setEnabled(false);
            }
            int bufferedPercentage = this.mControlWrapper.getBufferedPercentage();
            if (bufferedPercentage >= 95) {
                SeekBar seekBar2 = this.mSeekBar;
                seekBar2.setSecondaryProgress(seekBar2.getMax());
                ProgressBar progressBar = this.mPbBottomProgress;
                progressBar.setSecondaryProgress(progressBar.getMax());
            } else {
                int i3 = bufferedPercentage * 10;
                this.mSeekBar.setSecondaryProgress(i3);
                this.mPbBottomProgress.setSecondaryProgress(i3);
            }
        }
        TextView textView = this.mTvTotalTime;
        if (textView != null) {
            textView.setText(PlayerUtils.formatTime(i));
        }
        TextView textView2 = this.mTvCurrTime;
        if (textView2 != null) {
            textView2.setText(PlayerUtils.formatTime(i2));
        }
        if (VideoPlayerConfig.newBuilder().build().mIsShowToast) {
            long j = VideoPlayerConfig.newBuilder().build().mShowToastTime;
            if (j <= 0) {
                j = 5;
            }
            long currentPosition = this.mControlWrapper.getCurrentPosition();
            Log.d("progress---", "duration---" + i + "--currentPosition--" + currentPosition);
            long j2 = i - currentPosition;
            if (j2 >= 2 * j * 1000 || (j2 / 1000) % 60 != j) {
                return;
            }
            Log.d("progress---", "即将自动为您播放下一个视频");
            OnToastListener onToastListener = this.listener;
            if (onToastListener != null) {
                onToastListener.showToastOrDialog();
            }
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onLockStateChanged(boolean z) {
        onVisibilityChanged(!z, (Animation) null);
    }

    public void toggleFullScreen() {
        this.mControlWrapper.toggleFullScreen(PlayerUtils.scanForActivity(getContext()));
    }

    public void toggleFullScreen(boolean z) {
        this.mControlWrapper.toggleFullScreen(PlayerUtils.scanForActivity(getContext()), z);
    }

    public boolean isFullScreen() {
        return this.mControlWrapper.isFullScreen();
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        this.mIsDragging = true;
        this.mControlWrapper.stopProgress();
        this.mControlWrapper.stopFadeOut();
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        this.mControlWrapper.seekTo((int) ((this.mControlWrapper.getDuration() * seekBar.getProgress()) / this.mPbBottomProgress.getMax()));
        this.mIsDragging = false;
        this.mControlWrapper.startProgress();
        this.mControlWrapper.startFadeOut();
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (z) {
            long duration = (this.mControlWrapper.getDuration() * i) / this.mPbBottomProgress.getMax();
            TextView textView = this.mTvCurrTime;
            if (textView != null) {
                textView.setText(PlayerUtils.formatTime(duration));
            }
        }
    }

    public void setListener(OnToastListener onToastListener) {
        this.listener = onToastListener;
    }
}