package com.yc.kernel.factory;

import android.content.Context;
import com.yc.kernel.inter.AbstractVideoPlayer;

/* loaded from: classes.dex */
public abstract class PlayerFactory<T extends AbstractVideoPlayer> {
    public abstract T createPlayer(Context context);
}