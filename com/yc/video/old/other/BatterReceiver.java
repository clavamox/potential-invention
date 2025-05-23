package com.yc.video.old.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.yc.kernel.utils.VideoLogUtils;
import com.yc.video.old.controller.AbsVideoPlayerController;
import com.yc.video.old.player.OldVideoPlayer;

@Deprecated
/* loaded from: classes.dex */
public class BatterReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        AbsVideoPlayerController controller;
        VideoLogUtils.i("电量状态监听广播接收到数据了");
        int intExtra = intent.getIntExtra(NotificationCompat.CATEGORY_STATUS, 1);
        OldVideoPlayer currentVideoPlayer = VideoPlayerManager.instance().getCurrentVideoPlayer();
        if (currentVideoPlayer == null || (controller = currentVideoPlayer.getController()) == null) {
            return;
        }
        if (intExtra == 2) {
            controller.onBatterStateChanged(80);
            return;
        }
        if (intExtra == 5) {
            controller.onBatterStateChanged(81);
            return;
        }
        int intExtra2 = intent.getIntExtra("level", 0);
        int intExtra3 = intent.getIntExtra("scale", 0);
        int i = (int) ((intExtra2 / intExtra3) * 100.0f);
        VideoLogUtils.i("广播NetworkReceiver------当前电量" + intExtra2);
        VideoLogUtils.i("广播NetworkReceiver------总电量" + intExtra3);
        VideoLogUtils.i("广播NetworkReceiver------百分比" + i);
        if (i <= 10) {
            controller.onBatterStateChanged(82);
            return;
        }
        if (i <= 20) {
            controller.onBatterStateChanged(83);
            return;
        }
        if (i <= 50) {
            controller.onBatterStateChanged(84);
        } else if (i <= 80) {
            controller.onBatterStateChanged(85);
        } else if (i <= 100) {
            controller.onBatterStateChanged(86);
        }
    }
}