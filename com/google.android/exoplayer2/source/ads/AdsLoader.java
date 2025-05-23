package com.google.android.exoplayer2.source.ads;

import android.view.View;
import android.view.ViewGroup;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import java.io.IOException;

/* loaded from: classes.dex */
public interface AdsLoader {

    public interface AdViewProvider {
        View[] getAdOverlayViews();

        ViewGroup getAdViewGroup();
    }

    public interface EventListener {
        default void onAdClicked() {
        }

        default void onAdLoadError(AdsMediaSource.AdLoadException adLoadException, DataSpec dataSpec) {
        }

        default void onAdPlaybackState(AdPlaybackState adPlaybackState) {
        }

        default void onAdTapped() {
        }
    }

    void handlePrepareError(int i, int i2, IOException iOException);

    void release();

    void setPlayer(Player player);

    void setSupportedContentTypes(int... iArr);

    void start(EventListener eventListener, AdViewProvider adViewProvider);

    void stop();
}