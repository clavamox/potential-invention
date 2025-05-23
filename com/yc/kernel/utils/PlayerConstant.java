package com.yc.kernel.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes.dex */
public final class PlayerConstant {
    public static final int MEDIA_INFO_BUFFERING_END = 702;
    public static final int MEDIA_INFO_BUFFERING_START = 701;
    public static final int MEDIA_INFO_URL_NULL = -1;
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    public static final int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorType {
        public static final int TYPE_PARSE = 2;
        public static final int TYPE_SOURCE = 1;
        public static final int TYPE_UNEXPECTED = 3;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayerType {
        public static final int TYPE_EXO = 3;
        public static final int TYPE_IJK = 1;
        public static final int TYPE_NATIVE = 2;
        public static final int TYPE_RTC = 4;
    }
}