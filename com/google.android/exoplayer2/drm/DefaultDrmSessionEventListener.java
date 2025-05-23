package com.google.android.exoplayer2.drm;

/* loaded from: classes.dex */
public interface DefaultDrmSessionEventListener {
    default void onDrmKeysLoaded() {
    }

    default void onDrmKeysRemoved() {
    }

    default void onDrmKeysRestored() {
    }

    default void onDrmSessionAcquired() {
    }

    default void onDrmSessionManagerError(Exception exc) {
    }

    default void onDrmSessionReleased() {
    }
}