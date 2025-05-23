package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSpec;

/* loaded from: classes.dex */
public interface CacheKeyFactory {
    String buildCacheKey(DataSpec dataSpec);
}