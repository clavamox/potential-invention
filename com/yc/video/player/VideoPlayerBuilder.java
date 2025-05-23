package com.yc.video.player;

import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
public class VideoPlayerBuilder {
    public final int mColor;
    public final int mCurrentPosition;
    public final boolean mEnableAudioFocus;
    public final int[] mTinyScreenSize;

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private int mColor = 0;
        private int mCurrentPosition = -1;
        private boolean mEnableAudioFocus = true;
        private int[] mTinyScreenSize;

        public Builder setPlayerBackgroundColor(int i) {
            if (i == 0) {
                this.mColor = ViewCompat.MEASURED_STATE_MASK;
            } else {
                this.mColor = i;
            }
            return this;
        }

        public Builder setTinyScreenSize(int[] iArr) {
            this.mTinyScreenSize = iArr;
            return this;
        }

        public Builder skipPositionWhenPlay(int i) {
            this.mCurrentPosition = i;
            return this;
        }

        public Builder setEnableAudioFocus(boolean z) {
            this.mEnableAudioFocus = z;
            return this;
        }

        public VideoPlayerBuilder build() {
            return new VideoPlayerBuilder(this);
        }
    }

    public VideoPlayerBuilder(Builder builder) {
        this.mColor = builder.mColor;
        this.mTinyScreenSize = builder.mTinyScreenSize;
        this.mCurrentPosition = builder.mCurrentPosition;
        this.mEnableAudioFocus = builder.mEnableAudioFocus;
    }
}