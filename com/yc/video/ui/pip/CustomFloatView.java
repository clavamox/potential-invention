package com.yc.video.ui.pip;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.ui.view.InterControlView;

/* loaded from: classes.dex */
public class CustomFloatView extends FrameLayout implements InterControlView, View.OnClickListener {
    private Context mContext;
    private ControlWrapper mControlWrapper;
    private boolean mIsShowBottomProgress;
    private ImageView mIvClose;
    private ImageView mIvSkip;
    private ImageView mIvStartPlay;
    private ProgressBar mPbBottomProgress;
    private ProgressBar mPbLoading;

    @Override // com.yc.video.ui.view.InterControlView
    public View getView() {
        return this;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onLockStateChanged(boolean z) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayerStateChanged(int i) {
    }

    public CustomFloatView(Context context) {
        super(context);
        this.mIsShowBottomProgress = true;
        init(context);
    }

    public CustomFloatView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsShowBottomProgress = true;
        init(context);
    }

    public CustomFloatView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsShowBottomProgress = true;
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        initFindViewById(LayoutInflater.from(getContext()).inflate(R.layout.custom_video_player_float, (ViewGroup) this, true));
        initListener();
    }

    private void initFindViewById(View view) {
        this.mIvStartPlay = (ImageView) view.findViewById(R.id.iv_start_play);
        this.mPbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        this.mIvClose = (ImageView) view.findViewById(R.id.iv_close);
        this.mIvSkip = (ImageView) view.findViewById(R.id.iv_skip);
        this.mPbBottomProgress = (ProgressBar) view.findViewById(R.id.pb_bottom_progress);
    }

    private void initListener() {
        this.mIvStartPlay.setOnClickListener(this);
        this.mIvClose.setOnClickListener(this);
        this.mIvSkip.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view == this.mIvClose) {
            FloatVideoManager.getInstance(this.mContext).stopFloatWindow();
            FloatVideoManager.getInstance(this.mContext).reset();
        } else if (view == this.mIvStartPlay) {
            this.mControlWrapper.togglePlay();
        } else {
            if (view != this.mIvSkip || FloatVideoManager.getInstance(this.mContext).getActClass() == null) {
                return;
            }
            Intent intent = new Intent(getContext(), (Class<?>) FloatVideoManager.getInstance(this.mContext).getActClass());
            intent.setFlags(268435456);
            getContext().startActivity(intent);
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void attach(ControlWrapper controlWrapper) {
        this.mControlWrapper = controlWrapper;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onVisibilityChanged(boolean z, Animation animation) {
        if (z) {
            if (this.mIvStartPlay.getVisibility() == 0) {
                return;
            }
            this.mIvStartPlay.setVisibility(0);
            this.mIvStartPlay.startAnimation(animation);
            if (this.mIsShowBottomProgress) {
                this.mPbBottomProgress.setVisibility(8);
                return;
            }
            return;
        }
        if (this.mIvStartPlay.getVisibility() == 8) {
            return;
        }
        this.mIvStartPlay.setVisibility(8);
        this.mIvStartPlay.startAnimation(animation);
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
                this.mPbLoading.setVisibility(8);
                this.mIvStartPlay.setVisibility(8);
                bringToFront();
                break;
            case 0:
                this.mIvStartPlay.setSelected(false);
                this.mIvStartPlay.setVisibility(0);
                this.mPbLoading.setVisibility(8);
                break;
            case 1:
                this.mIvStartPlay.setVisibility(8);
                this.mIvStartPlay.setVisibility(0);
                break;
            case 2:
                this.mIvStartPlay.setVisibility(8);
                this.mPbLoading.setVisibility(8);
                break;
            case 3:
                this.mIvStartPlay.setSelected(true);
                this.mIvStartPlay.setVisibility(8);
                this.mPbLoading.setVisibility(8);
                if (this.mIsShowBottomProgress) {
                    if (this.mControlWrapper.isShowing()) {
                        this.mPbBottomProgress.setVisibility(8);
                    } else {
                        this.mPbBottomProgress.setVisibility(0);
                    }
                }
                this.mControlWrapper.startProgress();
                break;
            case 4:
                this.mIvStartPlay.setSelected(false);
                this.mIvStartPlay.setVisibility(0);
                this.mPbLoading.setVisibility(8);
                break;
            case 5:
                bringToFront();
                this.mPbBottomProgress.setProgress(0);
                this.mPbBottomProgress.setSecondaryProgress(0);
                break;
            case 6:
                this.mIvStartPlay.setVisibility(8);
                this.mPbLoading.setVisibility(0);
                break;
            case 7:
                this.mIvStartPlay.setVisibility(8);
                this.mPbLoading.setVisibility(8);
                this.mIvStartPlay.setSelected(this.mControlWrapper.isPlaying());
                break;
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void setProgress(int i, int i2) {
        if (i > 0) {
            this.mPbBottomProgress.setProgress((int) (((i2 * 1.0d) / i) * this.mPbBottomProgress.getMax()));
        }
        int bufferedPercentage = this.mControlWrapper.getBufferedPercentage();
        if (bufferedPercentage >= 95) {
            ProgressBar progressBar = this.mPbBottomProgress;
            progressBar.setSecondaryProgress(progressBar.getMax());
        } else {
            this.mPbBottomProgress.setSecondaryProgress(bufferedPercentage * 10);
        }
    }
}