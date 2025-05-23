package com.google.android.exoplayer2.source;

import android.net.Uri;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.offline.StreamKey;
import java.util.List;

/* loaded from: classes.dex */
public interface MediaSourceFactory {
    MediaSource createMediaSource(Uri uri);

    int[] getSupportedTypes();

    MediaSourceFactory setDrmSessionManager(DrmSessionManager<?> drmSessionManager);

    default MediaSourceFactory setStreamKeys(List<StreamKey> list) {
        return this;
    }
}