package com.google.android.exoplayer2.source.ads;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.CompositeMediaSource;
import com.google.android.exoplayer2.source.MaskingMediaPeriod;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class AdsMediaSource extends CompositeMediaSource<MediaSource.MediaPeriodId> {
    private static final MediaSource.MediaPeriodId DUMMY_CONTENT_MEDIA_PERIOD_ID = new MediaSource.MediaPeriodId(new Object());
    private MediaSource[][] adGroupMediaSources;
    private Timeline[][] adGroupTimelines;
    private final MediaSourceFactory adMediaSourceFactory;
    private AdPlaybackState adPlaybackState;
    private final AdsLoader.AdViewProvider adViewProvider;
    private final AdsLoader adsLoader;
    private ComponentListener componentListener;
    private final MediaSource contentMediaSource;
    private Timeline contentTimeline;
    private final Handler mainHandler;
    private final Map<MediaSource, List<MaskingMediaPeriod>> maskingMediaPeriodByAdMediaSource;
    private final Timeline.Period period;

    public static final class AdLoadException extends IOException {
        public static final int TYPE_AD = 0;
        public static final int TYPE_AD_GROUP = 1;
        public static final int TYPE_ALL_ADS = 2;
        public static final int TYPE_UNEXPECTED = 3;
        public final int type;

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        public @interface Type {
        }

        public static AdLoadException createForAd(Exception exc) {
            return new AdLoadException(0, exc);
        }

        public static AdLoadException createForAdGroup(Exception exc, int i) {
            return new AdLoadException(1, new IOException("Failed to load ad group " + i, exc));
        }

        public static AdLoadException createForAllAds(Exception exc) {
            return new AdLoadException(2, exc);
        }

        public static AdLoadException createForUnexpected(RuntimeException runtimeException) {
            return new AdLoadException(3, runtimeException);
        }

        private AdLoadException(int i, Exception exc) {
            super(exc);
            this.type = i;
        }

        public RuntimeException getRuntimeExceptionForUnexpected() {
            Assertions.checkState(this.type == 3);
            return (RuntimeException) Assertions.checkNotNull(getCause());
        }
    }

    public AdsMediaSource(MediaSource mediaSource, DataSource.Factory factory, AdsLoader adsLoader, AdsLoader.AdViewProvider adViewProvider) {
        this(mediaSource, new ProgressiveMediaSource.Factory(factory), adsLoader, adViewProvider);
    }

    public AdsMediaSource(MediaSource mediaSource, MediaSourceFactory mediaSourceFactory, AdsLoader adsLoader, AdsLoader.AdViewProvider adViewProvider) {
        this.contentMediaSource = mediaSource;
        this.adMediaSourceFactory = mediaSourceFactory;
        this.adsLoader = adsLoader;
        this.adViewProvider = adViewProvider;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.maskingMediaPeriodByAdMediaSource = new HashMap();
        this.period = new Timeline.Period();
        this.adGroupMediaSources = new MediaSource[0][];
        this.adGroupTimelines = new Timeline[0][];
        adsLoader.setSupportedContentTypes(mediaSourceFactory.getSupportedTypes());
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public Object getTag() {
        return this.contentMediaSource.getTag();
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    protected void prepareSourceInternal(TransferListener transferListener) {
        super.prepareSourceInternal(transferListener);
        final ComponentListener componentListener = new ComponentListener();
        this.componentListener = componentListener;
        prepareChildSource(DUMMY_CONTENT_MEDIA_PERIOD_ID, this.contentMediaSource);
        this.mainHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.source.ads.AdsMediaSource$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AdsMediaSource.this.m45x4dfad2b6(componentListener);
            }
        });
    }

    /* renamed from: lambda$prepareSourceInternal$0$com-google-android-exoplayer2-source-ads-AdsMediaSource, reason: not valid java name */
    /* synthetic */ void m45x4dfad2b6(ComponentListener componentListener) {
        this.adsLoader.start(componentListener, this.adViewProvider);
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public MediaPeriod createPeriod(MediaSource.MediaPeriodId mediaPeriodId, Allocator allocator, long j) {
        AdPlaybackState adPlaybackState = (AdPlaybackState) Assertions.checkNotNull(this.adPlaybackState);
        if (adPlaybackState.adGroupCount > 0 && mediaPeriodId.isAd()) {
            int i = mediaPeriodId.adGroupIndex;
            int i2 = mediaPeriodId.adIndexInAdGroup;
            Uri uri = (Uri) Assertions.checkNotNull(adPlaybackState.adGroups[i].uris[i2]);
            MediaSource[][] mediaSourceArr = this.adGroupMediaSources;
            MediaSource[] mediaSourceArr2 = mediaSourceArr[i];
            if (mediaSourceArr2.length <= i2) {
                int i3 = i2 + 1;
                mediaSourceArr[i] = (MediaSource[]) Arrays.copyOf(mediaSourceArr2, i3);
                Timeline[][] timelineArr = this.adGroupTimelines;
                timelineArr[i] = (Timeline[]) Arrays.copyOf(timelineArr[i], i3);
            }
            MediaSource mediaSource = this.adGroupMediaSources[i][i2];
            if (mediaSource == null) {
                mediaSource = this.adMediaSourceFactory.createMediaSource(uri);
                this.adGroupMediaSources[i][i2] = mediaSource;
                this.maskingMediaPeriodByAdMediaSource.put(mediaSource, new ArrayList());
                prepareChildSource(mediaPeriodId, mediaSource);
            }
            MediaSource mediaSource2 = mediaSource;
            MaskingMediaPeriod maskingMediaPeriod = new MaskingMediaPeriod(mediaSource2, mediaPeriodId, allocator, j);
            maskingMediaPeriod.setPrepareErrorListener(new AdPrepareErrorListener(uri, i, i2));
            List<MaskingMediaPeriod> list = this.maskingMediaPeriodByAdMediaSource.get(mediaSource2);
            if (list == null) {
                maskingMediaPeriod.createPeriod(new MediaSource.MediaPeriodId(((Timeline) Assertions.checkNotNull(this.adGroupTimelines[i][i2])).getUidOfPeriod(0), mediaPeriodId.windowSequenceNumber));
            } else {
                list.add(maskingMediaPeriod);
            }
            return maskingMediaPeriod;
        }
        MaskingMediaPeriod maskingMediaPeriod2 = new MaskingMediaPeriod(this.contentMediaSource, mediaPeriodId, allocator, j);
        maskingMediaPeriod2.createPeriod(mediaPeriodId);
        return maskingMediaPeriod2;
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void releasePeriod(MediaPeriod mediaPeriod) {
        MaskingMediaPeriod maskingMediaPeriod = (MaskingMediaPeriod) mediaPeriod;
        List<MaskingMediaPeriod> list = this.maskingMediaPeriodByAdMediaSource.get(maskingMediaPeriod.mediaSource);
        if (list != null) {
            list.remove(maskingMediaPeriod);
        }
        maskingMediaPeriod.releasePeriod();
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    protected void releaseSourceInternal() {
        super.releaseSourceInternal();
        ((ComponentListener) Assertions.checkNotNull(this.componentListener)).release();
        this.componentListener = null;
        this.maskingMediaPeriodByAdMediaSource.clear();
        this.contentTimeline = null;
        this.adPlaybackState = null;
        this.adGroupMediaSources = new MediaSource[0][];
        this.adGroupTimelines = new Timeline[0][];
        Handler handler = this.mainHandler;
        final AdsLoader adsLoader = this.adsLoader;
        adsLoader.getClass();
        handler.post(new Runnable() { // from class: com.google.android.exoplayer2.source.ads.AdsMediaSource$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AdsLoader.this.stop();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.exoplayer2.source.CompositeMediaSource
    /* renamed from: onChildSourceInfoRefreshed, reason: avoid collision after fix types in other method and merged with bridge method [inline-methods] */
    public void m33x365769cd(MediaSource.MediaPeriodId mediaPeriodId, MediaSource mediaSource, Timeline timeline) {
        if (mediaPeriodId.isAd()) {
            onAdSourceInfoRefreshed(mediaSource, mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup, timeline);
        } else {
            onContentSourceInfoRefreshed(timeline);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.exoplayer2.source.CompositeMediaSource
    public MediaSource.MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(MediaSource.MediaPeriodId mediaPeriodId, MediaSource.MediaPeriodId mediaPeriodId2) {
        return mediaPeriodId.isAd() ? mediaPeriodId : mediaPeriodId2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAdPlaybackState(AdPlaybackState adPlaybackState) {
        if (this.adPlaybackState == null) {
            MediaSource[][] mediaSourceArr = new MediaSource[adPlaybackState.adGroupCount][];
            this.adGroupMediaSources = mediaSourceArr;
            Arrays.fill(mediaSourceArr, new MediaSource[0]);
            Timeline[][] timelineArr = new Timeline[adPlaybackState.adGroupCount][];
            this.adGroupTimelines = timelineArr;
            Arrays.fill(timelineArr, new Timeline[0]);
        }
        this.adPlaybackState = adPlaybackState;
        maybeUpdateSourceInfo();
    }

    private void onContentSourceInfoRefreshed(Timeline timeline) {
        Assertions.checkArgument(timeline.getPeriodCount() == 1);
        this.contentTimeline = timeline;
        maybeUpdateSourceInfo();
    }

    private void onAdSourceInfoRefreshed(MediaSource mediaSource, int i, int i2, Timeline timeline) {
        Assertions.checkArgument(timeline.getPeriodCount() == 1);
        this.adGroupTimelines[i][i2] = timeline;
        List<MaskingMediaPeriod> remove = this.maskingMediaPeriodByAdMediaSource.remove(mediaSource);
        if (remove != null) {
            Object uidOfPeriod = timeline.getUidOfPeriod(0);
            for (int i3 = 0; i3 < remove.size(); i3++) {
                MaskingMediaPeriod maskingMediaPeriod = remove.get(i3);
                maskingMediaPeriod.createPeriod(new MediaSource.MediaPeriodId(uidOfPeriod, maskingMediaPeriod.id.windowSequenceNumber));
            }
        }
        maybeUpdateSourceInfo();
    }

    private void maybeUpdateSourceInfo() {
        Timeline timeline = this.contentTimeline;
        AdPlaybackState adPlaybackState = this.adPlaybackState;
        if (adPlaybackState == null || timeline == null) {
            return;
        }
        AdPlaybackState withAdDurationsUs = adPlaybackState.withAdDurationsUs(getAdDurations(this.adGroupTimelines, this.period));
        this.adPlaybackState = withAdDurationsUs;
        if (withAdDurationsUs.adGroupCount != 0) {
            timeline = new SinglePeriodAdTimeline(timeline, this.adPlaybackState);
        }
        refreshSourceInfo(timeline);
    }

    private static long[][] getAdDurations(Timeline[][] timelineArr, Timeline.Period period) {
        long[][] jArr = new long[timelineArr.length][];
        for (int i = 0; i < timelineArr.length; i++) {
            jArr[i] = new long[timelineArr[i].length];
            int i2 = 0;
            while (true) {
                Timeline[] timelineArr2 = timelineArr[i];
                if (i2 < timelineArr2.length) {
                    long[] jArr2 = jArr[i];
                    Timeline timeline = timelineArr2[i2];
                    jArr2[i2] = timeline == null ? C.TIME_UNSET : timeline.getPeriod(0, period).getDurationUs();
                    i2++;
                }
            }
        }
        return jArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class ComponentListener implements AdsLoader.EventListener {
        private final Handler playerHandler = new Handler();
        private volatile boolean released;

        public ComponentListener() {
        }

        public void release() {
            this.released = true;
            this.playerHandler.removeCallbacksAndMessages(null);
        }

        @Override // com.google.android.exoplayer2.source.ads.AdsLoader.EventListener
        public void onAdPlaybackState(final AdPlaybackState adPlaybackState) {
            if (this.released) {
                return;
            }
            this.playerHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.source.ads.AdsMediaSource$ComponentListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AdsMediaSource.ComponentListener.this.m47x9c1f3358(adPlaybackState);
                }
            });
        }

        /* renamed from: lambda$onAdPlaybackState$0$com-google-android-exoplayer2-source-ads-AdsMediaSource$ComponentListener, reason: not valid java name */
        /* synthetic */ void m47x9c1f3358(AdPlaybackState adPlaybackState) {
            if (this.released) {
                return;
            }
            AdsMediaSource.this.onAdPlaybackState(adPlaybackState);
        }

        @Override // com.google.android.exoplayer2.source.ads.AdsLoader.EventListener
        public void onAdLoadError(AdLoadException adLoadException, DataSpec dataSpec) {
            if (this.released) {
                return;
            }
            AdsMediaSource.this.createEventDispatcher(null).loadError(dataSpec, dataSpec.uri, Collections.emptyMap(), 6, -1L, 0L, 0L, adLoadException, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class AdPrepareErrorListener implements MaskingMediaPeriod.PrepareErrorListener {
        private final int adGroupIndex;
        private final int adIndexInAdGroup;
        private final Uri adUri;

        public AdPrepareErrorListener(Uri uri, int i, int i2) {
            this.adUri = uri;
            this.adGroupIndex = i;
            this.adIndexInAdGroup = i2;
        }

        @Override // com.google.android.exoplayer2.source.MaskingMediaPeriod.PrepareErrorListener
        public void onPrepareError(MediaSource.MediaPeriodId mediaPeriodId, final IOException iOException) {
            AdsMediaSource.this.createEventDispatcher(mediaPeriodId).loadError(new DataSpec(this.adUri), this.adUri, Collections.emptyMap(), 6, -1L, 0L, 0L, AdLoadException.createForAd(iOException), true);
            AdsMediaSource.this.mainHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.source.ads.AdsMediaSource$AdPrepareErrorListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AdsMediaSource.AdPrepareErrorListener.this.m46xf7f1398b(iOException);
                }
            });
        }

        /* renamed from: lambda$onPrepareError$0$com-google-android-exoplayer2-source-ads-AdsMediaSource$AdPrepareErrorListener, reason: not valid java name */
        /* synthetic */ void m46xf7f1398b(IOException iOException) {
            AdsMediaSource.this.adsLoader.handlePrepareError(this.adGroupIndex, this.adIndexInAdGroup, iOException);
        }
    }
}