package com.google.android.exoplayer2.offline;

import android.net.Uri;
import java.lang.reflect.Constructor;
import java.util.List;

/* loaded from: classes.dex */
public class DefaultDownloaderFactory implements DownloaderFactory {
    private static final Constructor<? extends Downloader> DASH_DOWNLOADER_CONSTRUCTOR;
    private static final Constructor<? extends Downloader> HLS_DOWNLOADER_CONSTRUCTOR;
    private static final Constructor<? extends Downloader> SS_DOWNLOADER_CONSTRUCTOR;
    private final DownloaderConstructorHelper downloaderConstructorHelper;

    static {
        Constructor<? extends Downloader> constructor;
        Constructor<? extends Downloader> constructor2;
        Constructor<? extends Downloader> constructor3 = null;
        try {
            constructor = getDownloaderConstructor(Class.forName("com.google.android.exoplayer2.source.dash.offline.DashDownloader"));
        } catch (ClassNotFoundException unused) {
            constructor = null;
        }
        DASH_DOWNLOADER_CONSTRUCTOR = constructor;
        try {
            constructor2 = getDownloaderConstructor(Class.forName("com.google.android.exoplayer2.source.hls.offline.HlsDownloader"));
        } catch (ClassNotFoundException unused2) {
            constructor2 = null;
        }
        HLS_DOWNLOADER_CONSTRUCTOR = constructor2;
        try {
            constructor3 = getDownloaderConstructor(Class.forName("com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloader"));
        } catch (ClassNotFoundException unused3) {
        }
        SS_DOWNLOADER_CONSTRUCTOR = constructor3;
    }

    public DefaultDownloaderFactory(DownloaderConstructorHelper downloaderConstructorHelper) {
        this.downloaderConstructorHelper = downloaderConstructorHelper;
    }

    @Override // com.google.android.exoplayer2.offline.DownloaderFactory
    public Downloader createDownloader(DownloadRequest downloadRequest) {
        String str = downloadRequest.type;
        str.hashCode();
        switch (str) {
            case "ss":
                return createDownloader(downloadRequest, SS_DOWNLOADER_CONSTRUCTOR);
            case "hls":
                return createDownloader(downloadRequest, HLS_DOWNLOADER_CONSTRUCTOR);
            case "dash":
                return createDownloader(downloadRequest, DASH_DOWNLOADER_CONSTRUCTOR);
            case "progressive":
                return new ProgressiveDownloader(downloadRequest.uri, downloadRequest.customCacheKey, this.downloaderConstructorHelper);
            default:
                throw new IllegalArgumentException("Unsupported type: " + downloadRequest.type);
        }
    }

    private Downloader createDownloader(DownloadRequest downloadRequest, Constructor<? extends Downloader> constructor) {
        if (constructor == null) {
            throw new IllegalStateException("Module missing for: " + downloadRequest.type);
        }
        try {
            return constructor.newInstance(downloadRequest.uri, downloadRequest.streamKeys, this.downloaderConstructorHelper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate downloader for: " + downloadRequest.type, e);
        }
    }

    private static Constructor<? extends Downloader> getDownloaderConstructor(Class<?> cls) {
        try {
            return cls.asSubclass(Downloader.class).getConstructor(Uri.class, List.class, DownloaderConstructorHelper.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Downloader constructor missing", e);
        }
    }
}