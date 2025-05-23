package com.yc.video.player;

/* loaded from: classes.dex */
public abstract class ProgressManager {
    public abstract long getSavedProgress(String str);

    public abstract void saveProgress(String str, long j);
}