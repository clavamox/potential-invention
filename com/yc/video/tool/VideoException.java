package com.yc.video.tool;

/* loaded from: classes.dex */
public class VideoException extends RuntimeException {
    public static final int CODE_NOT_PLAYER_FACTORY = 20;
    public static final int CODE_NOT_RENDER_FACTORY = 19;
    public static final int CODE_NOT_SET_CONTROLLER = 21;
    private int mCode;

    public VideoException(int i, String str) {
        super(str);
        this.mCode = i;
    }

    @Deprecated
    public VideoException(String str) {
        super(str);
        this.mCode = 0;
    }

    public VideoException(Throwable th) {
        super(th);
        this.mCode = 0;
        if (th instanceof VideoException) {
            this.mCode = ((VideoException) th).getCode();
        }
    }

    public int getCode() {
        return this.mCode;
    }

    public String getMsg() {
        return getMessage();
    }
}