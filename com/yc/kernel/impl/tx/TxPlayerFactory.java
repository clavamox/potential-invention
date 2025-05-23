package com.yc.kernel.impl.tx;

import android.content.Context;
import com.yc.kernel.factory.PlayerFactory;

/* loaded from: classes.dex */
public class TxPlayerFactory extends PlayerFactory<TxMediaPlayer> {
    public static TxPlayerFactory create() {
        return new TxPlayerFactory();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.yc.kernel.factory.PlayerFactory
    public TxMediaPlayer createPlayer(Context context) {
        return new TxMediaPlayer(context);
    }
}