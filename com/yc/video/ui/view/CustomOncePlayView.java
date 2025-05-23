package com.yc.video.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.tool.BaseToast;
import com.yc.video.tool.PlayerUtils;

/* loaded from: classes.dex */
public class CustomOncePlayView extends LinearLayout implements InterControlView {
    private Context mContext;
    private ControlWrapper mControlWrapper;
    private float mDownX;
    private float mDownY;
    private TextView mTvMessage;
    private TextView mTvRetry;
    private int playState;

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

    public CustomOncePlayView(Context context) {
        super(context);
        init(context);
    }

    public CustomOncePlayView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public CustomOncePlayView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setVisibility(8);
        initFindViewById(LayoutInflater.from(getContext()).inflate(R.layout.custom_video_player_once_live, (ViewGroup) this, true));
        initListener();
        setClickable(true);
    }

    private void initFindViewById(View view) {
        this.mTvMessage = (TextView) view.findViewById(R.id.tv_message);
        this.mTvRetry = (TextView) view.findViewById(R.id.tv_retry);
    }

    private void initListener() {
        this.mTvRetry.setOnClickListener(new View.OnClickListener() { // from class: com.yc.video.ui.view.CustomOncePlayView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (CustomOncePlayView.this.playState == 9) {
                    if (PlayerUtils.isConnected(CustomOncePlayView.this.mContext)) {
                        CustomOncePlayView.this.mControlWrapper.start();
                        return;
                    } else {
                        BaseToast.showRoundRectToast("请查看网络是否连接");
                        return;
                    }
                }
                BaseToast.showRoundRectToast("时间还未到，请稍后再试");
            }
        });
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void attach(ControlWrapper controlWrapper) {
        this.mControlWrapper = controlWrapper;
    }

    @Override // com.yc.video.ui.view.InterControlView
    public void onPlayStateChanged(int i) {
        this.playState = i;
        if (i == 9) {
            setVisibility(0);
        } else {
            setVisibility(8);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mDownX = motionEvent.getX();
            this.mDownY = motionEvent.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (action == 2) {
            float abs = Math.abs(motionEvent.getX() - this.mDownX);
            float abs2 = Math.abs(motionEvent.getY() - this.mDownY);
            if (abs > ViewConfiguration.get(getContext()).getScaledTouchSlop() || abs2 > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public TextView getTvMessage() {
        return this.mTvMessage;
    }
}