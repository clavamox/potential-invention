package com.yc.kernel.impl.tx;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.yc.kernel.inter.AbstractVideoPlayer;
import java.util.Map;

/* loaded from: classes.dex */
public class TxMediaPlayer extends AbstractVideoPlayer {
    private Context mAppContext;

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public int getBufferedPercentage() {
        return 0;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public long getCurrentPosition() {
        return 0L;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public long getDuration() {
        return 0L;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public float getSpeed() {
        return 0.0f;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public long getTcpSpeed() {
        return 0L;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void initPlayer() {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public boolean isPlaying() {
        return false;
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void pause() {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void prepareAsync() {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void release() {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void reset() {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void seekTo(long j) {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setDataSource(AssetFileDescriptor assetFileDescriptor) {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setDataSource(String str, Map<String, String> map) {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setDisplay(SurfaceHolder surfaceHolder) {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setLooping(boolean z) {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setOptions() {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setSpeed(float f) {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setSurface(Surface surface) {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void setVolume(float f, float f2) {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void start() {
    }

    @Override // com.yc.kernel.inter.AbstractVideoPlayer
    public void stop() {
    }

    public TxMediaPlayer(Context context) {
        if (context instanceof Application) {
            this.mAppContext = context;
        } else {
            this.mAppContext = context.getApplicationContext();
        }
    }
}