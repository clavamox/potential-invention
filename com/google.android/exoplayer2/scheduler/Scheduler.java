package com.google.android.exoplayer2.scheduler;

/* loaded from: classes.dex */
public interface Scheduler {
    boolean cancel();

    boolean schedule(Requirements requirements, String str, String str2);
}