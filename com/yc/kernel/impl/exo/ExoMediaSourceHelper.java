package com.yc.kernel.impl.exo;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import tv.danmaku.ijk.media.player.IjkMediaMeta;

/* loaded from: classes.dex */
public final class ExoMediaSourceHelper {
    private static ExoMediaSourceHelper sInstance;
    private Context mAppContext;
    private Cache mCache;
    private HttpDataSource.Factory mHttpDataSourceFactory;
    private final String mUserAgent;

    private ExoMediaSourceHelper(Context context) {
        if (context instanceof Application) {
            this.mAppContext = context;
        } else {
            this.mAppContext = context.getApplicationContext();
        }
        Context context2 = this.mAppContext;
        this.mUserAgent = Util.getUserAgent(context2, context2.getApplicationInfo().name);
    }

    public static ExoMediaSourceHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ExoMediaSourceHelper.class) {
                if (sInstance == null) {
                    sInstance = new ExoMediaSourceHelper(context);
                }
            }
        }
        return sInstance;
    }

    public MediaSource getMediaSource(String str) {
        return getMediaSource(str, null, false);
    }

    public MediaSource getMediaSource(String str, Map<String, String> map) {
        return getMediaSource(str, map, false);
    }

    public MediaSource getMediaSource(String str, boolean z) {
        return getMediaSource(str, null, z);
    }

    public MediaSource getMediaSource(String str, Map<String, String> map, boolean z) {
        DataSource.Factory dataSourceFactory;
        Uri parse = Uri.parse(str);
        if ("rtmp".equals(parse.getScheme())) {
            return new ProgressiveMediaSource.Factory(new RtmpDataSourceFactory(null)).createMediaSource(parse);
        }
        int inferContentType = inferContentType(str);
        if (z) {
            dataSourceFactory = getCacheDataSourceFactory();
        } else {
            dataSourceFactory = getDataSourceFactory();
        }
        if (this.mHttpDataSourceFactory != null) {
            setHeaders(map);
        }
        if (inferContentType == 0) {
            return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(parse);
        }
        if (inferContentType == 1) {
            return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(parse);
        }
        if (inferContentType == 2) {
            return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(parse);
        }
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(parse);
    }

    private int inferContentType(String str) {
        String lowerInvariant = Util.toLowerInvariant(str);
        if (lowerInvariant.contains(".mpd")) {
            return 0;
        }
        if (lowerInvariant.contains(".m3u8")) {
            return 2;
        }
        return lowerInvariant.matches(".*\\.ism(l)?(/manifest(\\(.+\\))?)?") ? 1 : 3;
    }

    private DataSource.Factory getCacheDataSourceFactory() {
        if (this.mCache == null) {
            this.mCache = newCache();
        }
        return new CacheDataSourceFactory(this.mCache, getDataSourceFactory(), 2);
    }

    private Cache newCache() {
        return new SimpleCache(new File(this.mAppContext.getExternalCacheDir(), "exo-video-cache"), new LeastRecentlyUsedCacheEvictor(IjkMediaMeta.AV_CH_STEREO_LEFT), new ExoDatabaseProvider(this.mAppContext));
    }

    private DataSource.Factory getDataSourceFactory() {
        return new DefaultDataSourceFactory(this.mAppContext, getHttpDataSourceFactory());
    }

    private DataSource.Factory getHttpDataSourceFactory() {
        if (this.mHttpDataSourceFactory == null) {
            this.mHttpDataSourceFactory = new DefaultHttpDataSourceFactory(this.mUserAgent, null, 8000, 8000, true);
        }
        return this.mHttpDataSourceFactory;
    }

    private void setHeaders(Map<String, String> map) {
        if (map == null || map.size() <= 0) {
            return;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (TextUtils.equals(key, "User-Agent")) {
                if (!TextUtils.isEmpty(value)) {
                    try {
                        Field declaredField = this.mHttpDataSourceFactory.getClass().getDeclaredField("userAgent");
                        declaredField.setAccessible(true);
                        declaredField.set(this.mHttpDataSourceFactory, value);
                    } catch (Exception unused) {
                    }
                }
            } else {
                this.mHttpDataSourceFactory.getDefaultRequestProperties().set(key, value);
            }
        }
    }

    public void setCache(Cache cache) {
        this.mCache = cache;
    }
}