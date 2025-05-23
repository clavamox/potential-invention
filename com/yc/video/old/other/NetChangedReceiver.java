package com.yc.video.old.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.yc.kernel.utils.VideoLogUtils;
import com.yc.video.old.controller.AbsVideoPlayerController;
import com.yc.video.old.player.OldVideoPlayer;
import com.yc.video.tool.NetworkUtils;

@Deprecated
/* loaded from: classes.dex */
public class NetChangedReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        VideoLogUtils.i("网络状态监听广播接收到数据了");
        OldVideoPlayer currentVideoPlayer = VideoPlayerManager.instance().getCurrentVideoPlayer();
        if (currentVideoPlayer != null) {
            AbsVideoPlayerController controller = currentVideoPlayer.getController();
            int i = AnonymousClass1.$SwitchMap$com$yc$video$tool$NetworkUtils$State[NetworkUtils.getConnectState(context).ordinal()];
            if (i == 1) {
                VideoLogUtils.i("当网络状态监听前连接了移动数据");
                return;
            }
            if (i == 2) {
                VideoLogUtils.i("网络状态监听当前连接了Wifi");
                return;
            }
            if (i == 3) {
                VideoLogUtils.i("网络状态监听当前没有网络连接");
                if (currentVideoPlayer.isPlaying() || currentVideoPlayer.isBufferingPlaying()) {
                    VideoLogUtils.i("网络状态监听当前没有网络连接---设置暂停播放");
                    currentVideoPlayer.pause();
                }
                if (controller != null) {
                    controller.onPlayStateChanged(-1);
                    return;
                }
                return;
            }
            VideoLogUtils.i("网络状态监听其他情况");
        }
    }

    /* renamed from: com.yc.video.old.other.NetChangedReceiver$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$yc$video$tool$NetworkUtils$State;

        static {
            int[] iArr = new int[NetworkUtils.State.values().length];
            $SwitchMap$com$yc$video$tool$NetworkUtils$State = iArr;
            try {
                iArr[NetworkUtils.State.MOBILE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$yc$video$tool$NetworkUtils$State[NetworkUtils.State.WIFI.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$yc$video$tool$NetworkUtils$State[NetworkUtils.State.UN_CONNECTED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }
}