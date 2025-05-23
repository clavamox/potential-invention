package com.google.android.exoplayer2.offline;

import android.net.Uri;
import com.google.android.exoplayer2.offline.Downloader;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public final class ProgressiveDownloader implements Downloader {
    private static final int BUFFER_SIZE_BYTES = 131072;
    private final Cache cache;
    private final CacheKeyFactory cacheKeyFactory;
    private final CacheDataSource dataSource;
    private final DataSpec dataSpec;
    private final AtomicBoolean isCanceled = new AtomicBoolean();
    private final PriorityTaskManager priorityTaskManager;

    public ProgressiveDownloader(Uri uri, String str, DownloaderConstructorHelper downloaderConstructorHelper) {
        this.dataSpec = new DataSpec(uri, 0L, -1L, str, 4);
        this.cache = downloaderConstructorHelper.getCache();
        this.dataSource = downloaderConstructorHelper.createCacheDataSource();
        this.cacheKeyFactory = downloaderConstructorHelper.getCacheKeyFactory();
        this.priorityTaskManager = downloaderConstructorHelper.getPriorityTaskManager();
    }

    @Override // com.google.android.exoplayer2.offline.Downloader
    public void download(Downloader.ProgressListener progressListener) throws InterruptedException, IOException {
        this.priorityTaskManager.add(-1000);
        try {
            CacheUtil.cache(this.dataSpec, this.cache, this.cacheKeyFactory, this.dataSource, new byte[131072], this.priorityTaskManager, -1000, progressListener == null ? null : new ProgressForwarder(progressListener), this.isCanceled, true);
        } finally {
            this.priorityTaskManager.remove(-1000);
        }
    }

    @Override // com.google.android.exoplayer2.offline.Downloader
    public void cancel() {
        this.isCanceled.set(true);
    }

    @Override // com.google.android.exoplayer2.offline.Downloader
    public void remove() {
        CacheUtil.remove(this.dataSpec, this.cache, this.cacheKeyFactory);
    }

    private static final class ProgressForwarder implements CacheUtil.ProgressListener {
        private final Downloader.ProgressListener progessListener;

        public ProgressForwarder(Downloader.ProgressListener progressListener) {
            this.progessListener = progressListener;
        }

        @Override // com.google.android.exoplayer2.upstream.cache.CacheUtil.ProgressListener
        public void onProgress(long j, long j2, long j3) {
            this.progessListener.onProgress(j, j2, (j == -1 || j == 0) ? -1.0f : (j2 * 100.0f) / j);
        }
    }
}