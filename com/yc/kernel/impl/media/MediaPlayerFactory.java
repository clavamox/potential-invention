package com.yc.kernel.impl.media;

import android.content.Context;
import com.yc.kernel.factory.PlayerFactory;

/* loaded from: classes.dex */
public class MediaPlayerFactory extends PlayerFactory<AndroidMediaPlayer> {
    public static MediaPlayerFactory create() {
        return new MediaPlayerFactory();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.yc.kernel.factory.PlayerFactory
    public AndroidMediaPlayer createPlayer(Context context) {
        return new AndroidMediaPlayer(context);
    }
}