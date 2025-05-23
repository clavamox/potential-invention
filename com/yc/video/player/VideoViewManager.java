package com.yc.video.player;

import android.app.Application;
import com.yc.kernel.utils.VideoLogUtils;
import com.yc.video.config.VideoPlayerConfig;
import java.util.LinkedHashMap;

/* loaded from: classes.dex */
public class VideoViewManager {
    private static VideoPlayerConfig sConfig;
    private static volatile VideoViewManager sInstance;
    private LinkedHashMap<String, VideoPlayer> mVideoViews = new LinkedHashMap<>();
    private boolean mPlayOnMobileNetwork = getConfig().mPlayOnMobileNetwork;

    private VideoViewManager() {
    }

    public static void setConfig(VideoPlayerConfig videoPlayerConfig) {
        if (sConfig == null) {
            synchronized (VideoPlayerConfig.class) {
                if (sConfig == null) {
                    if (videoPlayerConfig == null) {
                        videoPlayerConfig = VideoPlayerConfig.newBuilder().build();
                    }
                    sConfig = videoPlayerConfig;
                }
            }
        }
    }

    public static VideoPlayerConfig getConfig() {
        setConfig(null);
        return sConfig;
    }

    public boolean playOnMobileNetwork() {
        return this.mPlayOnMobileNetwork;
    }

    public void setPlayOnMobileNetwork(boolean z) {
        this.mPlayOnMobileNetwork = z;
    }

    public static VideoViewManager instance() {
        if (sInstance == null) {
            synchronized (VideoViewManager.class) {
                if (sInstance == null) {
                    sInstance = new VideoViewManager();
                }
            }
        }
        return sInstance;
    }

    public void add(VideoPlayer videoPlayer, String str) {
        if (!(videoPlayer.getContext() instanceof Application)) {
            VideoLogUtils.i("The Context of this VideoView is not an Application Context,you must remove it after release,or it will lead to memory leek.");
        }
        VideoPlayer videoPlayer2 = get(str);
        if (videoPlayer2 != null) {
            videoPlayer2.release();
            remove(str);
        }
        this.mVideoViews.put(str, videoPlayer);
    }

    public VideoPlayer get(String str) {
        return this.mVideoViews.get(str);
    }

    public void remove(String str) {
        this.mVideoViews.remove(str);
    }

    public void removeAll() {
        this.mVideoViews.clear();
    }

    public void releaseByTag(String str) {
        releaseByTag(str, true);
    }

    public void releaseByTag(String str, boolean z) {
        VideoPlayer videoPlayer = get(str);
        if (videoPlayer != null) {
            videoPlayer.release();
            if (z) {
                remove(str);
            }
        }
    }

    public boolean onBackPress(String str) {
        VideoPlayer videoPlayer = get(str);
        if (videoPlayer == null) {
            return false;
        }
        return videoPlayer.onBackPressed();
    }
}