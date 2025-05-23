package com.yc.video.config;

/* loaded from: classes.dex */
public interface BuriedPointEvent {
    void clickAd(String str);

    void onError(String str, boolean z);

    void playerAndProved(String str);

    void playerCompletion(String str);

    void playerDestroy(String str);

    void playerIn(String str);

    void playerOutProgress(String str, float f);

    void playerOutProgress(String str, long j, long j2);

    void videoToMedia(String str);
}