package com.yc.kernel.impl.ijk;

import android.content.Context;
import com.yc.kernel.factory.PlayerFactory;

/* loaded from: classes.dex */
public class IjkPlayerFactory extends PlayerFactory<IjkVideoPlayer> {
    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.yc.kernel.factory.PlayerFactory
    public IjkVideoPlayer createPlayer(Context context) {
        return new IjkVideoPlayer(context);
    }
}