package com.yc.video.config;

import android.content.Context;
import com.yc.kernel.factory.PlayerFactory;
import com.yc.kernel.impl.media.MediaPlayerFactory;
import com.yc.video.player.ProgressManager;
import com.yc.video.surface.SurfaceFactory;
import com.yc.video.surface.TextureViewFactory;
import com.yc.video.tool.BaseToast;

/* loaded from: classes.dex */
public class VideoPlayerConfig {
    public final boolean mAdaptCutout;
    public final BuriedPointEvent mBuriedPointEvent;
    public final Context mContext;
    public final boolean mEnableAudioFocus;
    public final boolean mEnableOrientation;
    public final boolean mIsEnableLog;
    public final boolean mIsShowToast;
    public final boolean mPlayOnMobileNetwork;
    public final PlayerFactory mPlayerFactory;
    public final ProgressManager mProgressManager;
    public final SurfaceFactory mRenderViewFactory;
    public final int mScreenScaleType;
    public final long mShowToastTime;

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private BuriedPointEvent mBuriedPointEvent;
        private Context mContext;
        private boolean mEnableOrientation;
        private boolean mPlayOnMobileNetwork;
        private PlayerFactory mPlayerFactory;
        private ProgressManager mProgressManager;
        private SurfaceFactory mRenderViewFactory;
        private int mScreenScaleType;
        private boolean mIsEnableLog = false;
        private boolean mEnableAudioFocus = true;
        private boolean mAdaptCutout = true;
        private boolean mIsShowToast = false;
        private long mShowToastTime = 5;

        public Builder setContext(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setEnableOrientation(boolean z) {
            this.mEnableOrientation = z;
            return this;
        }

        public Builder setPlayOnMobileNetwork(boolean z) {
            this.mPlayOnMobileNetwork = z;
            return this;
        }

        public Builder setEnableAudioFocus(boolean z) {
            this.mEnableAudioFocus = z;
            return this;
        }

        public Builder setProgressManager(ProgressManager progressManager) {
            this.mProgressManager = progressManager;
            return this;
        }

        public Builder setLogEnabled(boolean z) {
            this.mIsEnableLog = z;
            return this;
        }

        public Builder setPlayerFactory(PlayerFactory playerFactory) {
            this.mPlayerFactory = playerFactory;
            return this;
        }

        public Builder setBuriedPointEvent(BuriedPointEvent buriedPointEvent) {
            this.mBuriedPointEvent = buriedPointEvent;
            return this;
        }

        public Builder setScreenScaleType(int i) {
            this.mScreenScaleType = i;
            return this;
        }

        public Builder setRenderViewFactory(SurfaceFactory surfaceFactory) {
            this.mRenderViewFactory = surfaceFactory;
            return this;
        }

        public Builder setAdaptCutout(boolean z) {
            this.mAdaptCutout = z;
            return this;
        }

        public Builder setIsShowToast(boolean z) {
            this.mIsShowToast = z;
            return this;
        }

        public Builder setShowToastTime(long j) {
            this.mShowToastTime = j;
            return this;
        }

        public VideoPlayerConfig build() {
            return new VideoPlayerConfig(this);
        }
    }

    private VideoPlayerConfig(Builder builder) {
        this.mIsEnableLog = builder.mIsEnableLog;
        this.mEnableOrientation = builder.mEnableOrientation;
        this.mPlayOnMobileNetwork = builder.mPlayOnMobileNetwork;
        this.mEnableAudioFocus = builder.mEnableAudioFocus;
        this.mProgressManager = builder.mProgressManager;
        this.mScreenScaleType = builder.mScreenScaleType;
        if (builder.mPlayerFactory == null) {
            this.mPlayerFactory = MediaPlayerFactory.create();
        } else {
            this.mPlayerFactory = builder.mPlayerFactory;
        }
        this.mBuriedPointEvent = builder.mBuriedPointEvent;
        if (builder.mRenderViewFactory == null) {
            this.mRenderViewFactory = TextureViewFactory.create();
        } else {
            this.mRenderViewFactory = builder.mRenderViewFactory;
        }
        this.mAdaptCutout = builder.mAdaptCutout;
        Context context = builder.mContext;
        this.mContext = context;
        if (context != null) {
            BaseToast.init(context);
        }
        this.mIsShowToast = builder.mIsShowToast;
        this.mShowToastTime = builder.mShowToastTime;
    }
}