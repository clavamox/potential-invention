package com.yc.video.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes.dex */
public final class ConstantKeys {

    @Retention(RetentionPolicy.SOURCE)
    public @interface BatterMode {
        public static final int BATTERY_10 = 82;
        public static final int BATTERY_100 = 86;
        public static final int BATTERY_20 = 83;
        public static final int BATTERY_50 = 84;
        public static final int BATTERY_80 = 85;
        public static final int BATTERY_CHARGING = 80;
        public static final int BATTERY_FULL = 81;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface CurrentState {
        public static final int STATE_BUFFERING_PAUSED = 6;
        public static final int STATE_BUFFERING_PLAYING = 5;
        public static final int STATE_COMPLETED = 7;
        public static final int STATE_ERROR = -1;
        public static final int STATE_IDLE = 0;
        public static final int STATE_NETWORK_ERROR = -2;
        public static final int STATE_ONCE_LIVE = 9;
        public static final int STATE_PARSE_ERROR = -3;
        public static final int STATE_PAUSED = 4;
        public static final int STATE_PLAYING = 3;
        public static final int STATE_PREPARED = 2;
        public static final int STATE_PREPARING = 1;
        public static final int STATE_START_ABORT = 8;
        public static final int STATE_URL_NULL = -4;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface CurrentStateType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Loading {
        public static final int LOADING_QQ = 2;
        public static final int LOADING_RING = 1;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadingType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayMode {
        public static final int MODE_FULL_SCREEN = 1002;
        public static final int MODE_NORMAL = 1001;
        public static final int MODE_TINY_WINDOW = 1003;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayModeType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayerScreenScaleType {
        public static final int SCREEN_SCALE_16_9 = 1;
        public static final int SCREEN_SCALE_4_3 = 2;
        public static final int SCREEN_SCALE_CENTER_CROP = 5;
        public static final int SCREEN_SCALE_DEFAULT = 0;
        public static final int SCREEN_SCALE_MATCH_PARENT = 3;
        public static final int SCREEN_SCALE_ORIGINAL = 4;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayerStatesType {
        public static final int BACK_CLICK = 104;
        public static final int COMPLETED = 101;
        public static final int PAUSE = 103;
        public static final int PLAYING = 102;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayerType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenScaleType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoControl {
        public static final int AUDIO = 2006;
        public static final int DOWNLOAD = 2005;
        public static final int HOR_AUDIO = 2010;
        public static final int MENU = 2008;
        public static final int SHARE = 2007;
        public static final int TV = 2009;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoControlType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoPlayerType {
        public static final int TYPE_EXO = 3;
        public static final int TYPE_IJK = 1;
        public static final int TYPE_NATIVE = 2;
        public static final int TYPE_RTC = 4;
    }
}