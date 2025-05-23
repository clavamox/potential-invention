package com.yc.kernel.impl.exo;

import android.content.Context;
import com.yc.kernel.factory.PlayerFactory;

/* loaded from: classes.dex */
public class ExoPlayerFactory extends PlayerFactory<ExoMediaPlayer> {
    public static ExoPlayerFactory create() {
        return new ExoPlayerFactory();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.yc.kernel.factory.PlayerFactory
    public ExoMediaPlayer createPlayer(Context context) {
        return new ExoMediaPlayer(context);
    }
}