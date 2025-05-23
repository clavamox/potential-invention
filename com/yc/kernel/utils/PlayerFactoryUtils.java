package com.yc.kernel.utils;

import android.content.Context;
import com.yc.kernel.factory.PlayerFactory;
import com.yc.kernel.impl.exo.ExoPlayerFactory;
import com.yc.kernel.impl.ijk.IjkPlayerFactory;
import com.yc.kernel.impl.media.MediaPlayerFactory;
import com.yc.kernel.inter.AbstractVideoPlayer;

/* loaded from: classes.dex */
public final class PlayerFactoryUtils {
    public static PlayerFactory getPlayer(int i) {
        if (i == 3) {
            return ExoPlayerFactory.create();
        }
        if (i == 1) {
            return IjkPlayerFactory.create();
        }
        if (i == 2) {
            return MediaPlayerFactory.create();
        }
        if (i == 4) {
            return IjkPlayerFactory.create();
        }
        return IjkPlayerFactory.create();
    }

    public static AbstractVideoPlayer getVideoPlayer(Context context, int i) {
        if (i == 3) {
            return ExoPlayerFactory.create().createPlayer(context);
        }
        if (i == 1) {
            return IjkPlayerFactory.create().createPlayer(context);
        }
        if (i == 2) {
            return MediaPlayerFactory.create().createPlayer(context);
        }
        if (i == 4) {
            return IjkPlayerFactory.create().createPlayer(context);
        }
        return IjkPlayerFactory.create().createPlayer(context);
    }
}