package com.google.android.exoplayer2.source;

import android.net.Uri;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaPeriod;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;

/* loaded from: classes.dex */
public final class ProgressiveMediaSource extends BaseMediaSource implements ProgressiveMediaPeriod.Listener {
    public static final int DEFAULT_LOADING_CHECK_INTERVAL_BYTES = 1048576;
    private final int continueLoadingCheckIntervalBytes;
    private final String customCacheKey;
    private final DataSource.Factory dataSourceFactory;
    private final DrmSessionManager<?> drmSessionManager;
    private final ExtractorsFactory extractorsFactory;
    private final LoadErrorHandlingPolicy loadableLoadErrorHandlingPolicy;
    private final Object tag;
    private long timelineDurationUs = C.TIME_UNSET;
    private boolean timelineIsLive;
    private boolean timelineIsSeekable;
    private TransferListener transferListener;
    private final Uri uri;

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void maybeThrowSourceInfoRefreshError() throws IOException {
    }

    public static final class Factory implements MediaSourceFactory {
        private int continueLoadingCheckIntervalBytes;
        private String customCacheKey;
        private final DataSource.Factory dataSourceFactory;
        private DrmSessionManager<?> drmSessionManager;
        private ExtractorsFactory extractorsFactory;
        private boolean isCreateCalled;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
        private Object tag;

        @Override // com.google.android.exoplayer2.source.MediaSourceFactory
        public int[] getSupportedTypes() {
            return new int[]{3};
        }

        @Override // com.google.android.exoplayer2.source.MediaSourceFactory
        public /* bridge */ /* synthetic */ MediaSourceFactory setDrmSessionManager(DrmSessionManager drmSessionManager) {
            return setDrmSessionManager((DrmSessionManager<?>) drmSessionManager);
        }

        public Factory(DataSource.Factory factory) {
            this(factory, new DefaultExtractorsFactory());
        }

        public Factory(DataSource.Factory factory, ExtractorsFactory extractorsFactory) {
            this.dataSourceFactory = factory;
            this.extractorsFactory = extractorsFactory;
            this.drmSessionManager = DrmSessionManager.getDummyDrmSessionManager();
            this.loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
            this.continueLoadingCheckIntervalBytes = 1048576;
        }

        @Deprecated
        public Factory setExtractorsFactory(ExtractorsFactory extractorsFactory) {
            Assertions.checkState(!this.isCreateCalled);
            this.extractorsFactory = extractorsFactory;
            return this;
        }

        public Factory setCustomCacheKey(String str) {
            Assertions.checkState(!this.isCreateCalled);
            this.customCacheKey = str;
            return this;
        }

        public Factory setTag(Object obj) {
            Assertions.checkState(!this.isCreateCalled);
            this.tag = obj;
            return this;
        }

        public Factory setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
            Assertions.checkState(!this.isCreateCalled);
            this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
            return this;
        }

        public Factory setContinueLoadingCheckIntervalBytes(int i) {
            Assertions.checkState(!this.isCreateCalled);
            this.continueLoadingCheckIntervalBytes = i;
            return this;
        }

        @Override // com.google.android.exoplayer2.source.MediaSourceFactory
        public Factory setDrmSessionManager(DrmSessionManager<?> drmSessionManager) {
            Assertions.checkState(!this.isCreateCalled);
            this.drmSessionManager = drmSessionManager;
            return this;
        }

        @Override // com.google.android.exoplayer2.source.MediaSourceFactory
        public ProgressiveMediaSource createMediaSource(Uri uri) {
            this.isCreateCalled = true;
            return new ProgressiveMediaSource(uri, this.dataSourceFactory, this.extractorsFactory, this.drmSessionManager, this.loadErrorHandlingPolicy, this.customCacheKey, this.continueLoadingCheckIntervalBytes, this.tag);
        }
    }

    ProgressiveMediaSource(Uri uri, DataSource.Factory factory, ExtractorsFactory extractorsFactory, DrmSessionManager<?> drmSessionManager, LoadErrorHandlingPolicy loadErrorHandlingPolicy, String str, int i, Object obj) {
        this.uri = uri;
        this.dataSourceFactory = factory;
        this.extractorsFactory = extractorsFactory;
        this.drmSessionManager = drmSessionManager;
        this.loadableLoadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.customCacheKey = str;
        this.continueLoadingCheckIntervalBytes = i;
        this.tag = obj;
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public Object getTag() {
        return this.tag;
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource
    protected void prepareSourceInternal(TransferListener transferListener) {
        this.transferListener = transferListener;
        this.drmSessionManager.prepare();
        notifySourceInfoRefreshed(this.timelineDurationUs, this.timelineIsSeekable, this.timelineIsLive);
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public MediaPeriod createPeriod(MediaSource.MediaPeriodId mediaPeriodId, Allocator allocator, long j) {
        DataSource createDataSource = this.dataSourceFactory.createDataSource();
        TransferListener transferListener = this.transferListener;
        if (transferListener != null) {
            createDataSource.addTransferListener(transferListener);
        }
        return new ProgressiveMediaPeriod(this.uri, createDataSource, this.extractorsFactory.createExtractors(), this.drmSessionManager, this.loadableLoadErrorHandlingPolicy, createEventDispatcher(mediaPeriodId), this, allocator, this.customCacheKey, this.continueLoadingCheckIntervalBytes);
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((ProgressiveMediaPeriod) mediaPeriod).release();
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource
    protected void releaseSourceInternal() {
        this.drmSessionManager.release();
    }

    @Override // com.google.android.exoplayer2.source.ProgressiveMediaPeriod.Listener
    public void onSourceInfoRefreshed(long j, boolean z, boolean z2) {
        if (j == C.TIME_UNSET) {
            j = this.timelineDurationUs;
        }
        if (this.timelineDurationUs == j && this.timelineIsSeekable == z && this.timelineIsLive == z2) {
            return;
        }
        notifySourceInfoRefreshed(j, z, z2);
    }

    private void notifySourceInfoRefreshed(long j, boolean z, boolean z2) {
        this.timelineDurationUs = j;
        this.timelineIsSeekable = z;
        this.timelineIsLive = z2;
        refreshSourceInfo(new SinglePeriodTimeline(this.timelineDurationUs, this.timelineIsSeekable, false, this.timelineIsLive, null, this.tag));
    }
}