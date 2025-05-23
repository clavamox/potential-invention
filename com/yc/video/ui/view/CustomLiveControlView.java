package com.yc.video.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.tool.PlayerUtils;

/* loaded from: classes.dex */
public class CustomLiveControlView extends FrameLayout implements InterControlView, View.OnClickListener {
    private Context mContext;
    private ControlWrapper mControlWrapper;
    private ImageView mIvFullScreen;
    private ImageView mIvPlay;
    private ImageView mIvRefresh;
    private LinearLayout mLlBottomContainer;

    @Override // com.yc.video.ui.view.InterControlView
    public View getView() {
        return this;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void setProgress(int i, int i2) {
    }

    public CustomLiveControlView(Context context) {
        super(context);
        init(context);
    }

    public CustomLiveControlView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public CustomLiveControlView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setVisibility(8);
        initFindViewById(LayoutInflater.from(getContext()).inflate(R.layout.custom_video_player_live, (ViewGroup) this, true));
        initListener();
    }

    private void initFindViewById(View view) {
        this.mLlBottomContainer = (LinearLayout) view.findViewById(R.id.ll_bottom_container);
        this.mIvPlay = (ImageView) view.findViewById(R.id.iv_play);
        this.mIvRefresh = (ImageView) view.findViewById(R.id.iv_refresh);
        this.mIvFullScreen = (ImageView) view.findViewById(R.id.iv_full_screen);
    }

    private void initListener() {
        this.mIvFullScreen.setOnClickListener(this);
        this.mIvRefresh.setOnClickListener(this);
        this.mIvPlay.setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_full_screen) {
            toggleFullScreen();
        } else if (id == R.id.iv_play) {
            this.mControlWrapper.togglePlay();
        } else if (id == R.id.iv_refresh) {
            this.mControlWrapper.replay(true);
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void attach(ControlWrapper controlWrapper) {
        this.mControlWrapper = controlWrapper;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onVisibilityChanged(boolean z, Animation animation) {
        if (z) {
            if (getVisibility() == 8) {
                setVisibility(0);
                if (animation != null) {
                    startAnimation(animation);
                    return;
                }
                return;
            }
            return;
        }
        if (getVisibility() == 0) {
            setVisibility(8);
            if (animation != null) {
                startAnimation(animation);
            }
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayStateChanged(int i) {
        switch (i) {
            case -1:
            case 0:
            case 1:
            case 2:
            case 5:
            case 8:
            case 9:
                setVisibility(8);
                break;
            case 3:
                this.mIvPlay.setSelected(true);
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
            this.mIvFullScreen.setSelected(false);
        } else if (i == 1002) {
            this.mIvFullScreen.setSelected(true);
        }
        Activity scanForActivity = PlayerUtils.scanForActivity(this.mContext);
        if (scanForActivity == null || !this.mControlWrapper.hasCutout()) {
            return;
        }
        int requestedOrientation = scanForActivity.getRequestedOrientation();
        int cutoutHeight = this.mControlWrapper.getCutoutHeight();
        if (requestedOrientation == 1) {
            this.mLlBottomContainer.setPadding(0, 0, 0, 0);
        } else if (requestedOrientation == 0) {
            this.mLlBottomContainer.setPadding(cutoutHeight, 0, 0, 0);
        } else if (requestedOrientation == 8) {
            this.mLlBottomContainer.setPadding(0, 0, cutoutHeight, 0);
        }
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onLockStateChanged(boolean z) {
        onVisibilityChanged(!z, (Animation) null);
    }

    public void toggleFullScreen() {
        this.mControlWrapper.toggleFullScreen(PlayerUtils.scanForActivity(getContext()));
    }
}