package com.yc.video.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.player.VideoViewManager;

/* loaded from: classes.dex */
public class CustomPrepareView extends FrameLayout implements InterControlView {
    private Context mContext;
    private ControlWrapper mControlWrapper;
    private FrameLayout mFlNetWarning;
    private ImageView mIvStartPlay;
    private ImageView mIvThumb;
    private ProgressBar mPbLoading;
    private TextView mTvMessage;
    private TextView mTvStart;

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

    @Override // com.yc.video.ui.view.InterControlView
    public void onVisibilityChanged(boolean z, Animation animation) {
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void setProgress(int i, int i2) {
    }

    public CustomPrepareView(Context context) {
        super(context);
        init(context);
    }

    public CustomPrepareView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public CustomPrepareView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        initFindViewById(LayoutInflater.from(getContext()).inflate(R.layout.custom_video_player_prepare, (ViewGroup) this, true));
        initListener();
    }

    private void initFindViewById(View view) {
        this.mIvThumb = (ImageView) view.findViewById(R.id.iv_thumb);
        this.mIvStartPlay = (ImageView) view.findViewById(R.id.iv_start_play);
        this.mPbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        this.mFlNetWarning = (FrameLayout) view.findViewById(R.id.fl_net_warning);
        this.mTvMessage = (TextView) view.findViewById(R.id.tv_message);
        this.mTvStart = (TextView) view.findViewById(R.id.tv_start);
    }

    private void initListener() {
        this.mTvStart.setOnClickListener(new View.OnClickListener() { // from class: com.yc.video.ui.view.CustomPrepareView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CustomPrepareView.this.mFlNetWarning.setVisibility(8);
                VideoViewManager.instance().setPlayOnMobileNetwork(true);
                CustomPrepareView.this.mControlWrapper.start();
            }
        });
    }

    public void setClickStart() {
        setOnClickListener(new View.OnClickListener() { // from class: com.yc.video.ui.view.CustomPrepareView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CustomPrepareView.this.mControlWrapper.start();
            }
        });
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void attach(ControlWrapper controlWrapper) {
        this.mControlWrapper = controlWrapper;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayStateChanged(int i) {
        switch (i) {
            case -1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
                setVisibility(8);
                break;
            case 0:
                setVisibility(0);
                bringToFront();
                this.mPbLoading.setVisibility(8);
                this.mFlNetWarning.setVisibility(8);
                this.mIvStartPlay.setVisibility(0);
                this.mIvThumb.setVisibility(0);
                break;
            case 1:
                bringToFront();
                setVisibility(0);
                this.mIvStartPlay.setVisibility(8);
                this.mFlNetWarning.setVisibility(8);
                this.mPbLoading.setVisibility(0);
                break;
            case 8:
                setVisibility(0);
                this.mFlNetWarning.setVisibility(0);
                this.mFlNetWarning.bringToFront();
                break;
        }
    }

    public ImageView getThumb() {
        return this.mIvThumb;
    }
}