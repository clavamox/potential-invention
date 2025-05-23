package com.yc.kernel.inter;

import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class AbstractVideoPlayer {
    protected VideoPlayerListener mPlayerEventListener;

    public abstract int getBufferedPercentage();

    public abstract long getCurrentPosition();

    public abstract long getDuration();

    public abstract float getSpeed();

    public abstract long getTcpSpeed();

    public abstract void initPlayer();

    public abstract boolean isPlaying();

    public abstract void pause();

    public abstract void prepareAsync();

    public abstract void release();

    public abstract void reset();

    public abstract void seekTo(long j);

    public abstract void setDataSource(AssetFileDescriptor assetFileDescriptor);

    public abstract void setDataSource(String str, Map<String, String> map);

    public abstract void setDisplay(SurfaceHolder surfaceHolder);

    public abstract void setLooping(boolean z);

    public abstract void setOptions();

    public abstract void setSpeed(float f);

    public abstract void setSurface(Surface surface);

    public abstract void setVolume(float f, float f2);

    public abstract void start();

    public abstract void stop();

    public void setPlayerEventListener(VideoPlayerListener videoPlayerListener) {
        this.mPlayerEventListener = videoPlayerListener;
    }
}