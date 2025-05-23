package com.google.android.exoplayer2.offline;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.trackselection.BaseTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

/* loaded from: classes.dex */
public final class DownloadHelper {
    private static final Constructor<? extends MediaSourceFactory> DASH_FACTORY_CONSTRUCTOR;

    @Deprecated
    public static final DefaultTrackSelector.Parameters DEFAULT_TRACK_SELECTOR_PARAMETERS;
    public static final DefaultTrackSelector.Parameters DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_CONTEXT;

    @Deprecated
    public static final DefaultTrackSelector.Parameters DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT;
    private static final Constructor<? extends MediaSourceFactory> HLS_FACTORY_CONSTRUCTOR;
    private static final Constructor<? extends MediaSourceFactory> SS_FACTORY_CONSTRUCTOR;
    private final String cacheKey;
    private Callback callback;
    private final Handler callbackHandler;
    private final String downloadType;
    private List<TrackSelection>[][] immutableTrackSelectionsByPeriodAndRenderer;
    private boolean isPreparedWithMedia;
    private MappingTrackSelector.MappedTrackInfo[] mappedTrackInfos;
    private MediaPreparer mediaPreparer;
    private final MediaSource mediaSource;
    private final RendererCapabilities[] rendererCapabilities;
    private final SparseIntArray scratchSet;
    private TrackGroupArray[] trackGroupArrays;
    private List<TrackSelection>[][] trackSelectionsByPeriodAndRenderer;
    private final DefaultTrackSelector trackSelector;
    private final Uri uri;
    private final Timeline.Window window;

    public interface Callback {
        void onPrepareError(DownloadHelper downloadHelper, IOException iOException);

        void onPrepared(DownloadHelper downloadHelper);
    }

    public static class LiveContentUnsupportedException extends IOException {
    }

    static /* synthetic */ void lambda$new$0() {
    }

    static {
        DefaultTrackSelector.Parameters build = DefaultTrackSelector.Parameters.DEFAULT_WITHOUT_CONTEXT.buildUpon().setForceHighestSupportedBitrate(true).build();
        DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_CONTEXT = build;
        DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT = build;
        DEFAULT_TRACK_SELECTOR_PARAMETERS = build;
        DASH_FACTORY_CONSTRUCTOR = getConstructor("com.google.android.exoplayer2.source.dash.DashMediaSource$Factory");
        SS_FACTORY_CONSTRUCTOR = getConstructor("com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource$Factory");
        HLS_FACTORY_CONSTRUCTOR = getConstructor("com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory");
    }

    public static DefaultTrackSelector.Parameters getDefaultTrackSelectorParameters(Context context) {
        return DefaultTrackSelector.Parameters.getDefaults(context).buildUpon().setForceHighestSupportedBitrate(true).build();
    }

    @Deprecated
    public static DownloadHelper forProgressive(Uri uri) {
        return forProgressive(uri, (String) null);
    }

    public static DownloadHelper forProgressive(Context context, Uri uri) {
        return forProgressive(context, uri, null);
    }

    @Deprecated
    public static DownloadHelper forProgressive(Uri uri, String str) {
        return new DownloadHelper(DownloadRequest.TYPE_PROGRESSIVE, uri, str, null, DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT, new RendererCapabilities[0]);
    }

    public static DownloadHelper forProgressive(Context context, Uri uri, String str) {
        return new DownloadHelper(DownloadRequest.TYPE_PROGRESSIVE, uri, str, null, getDefaultTrackSelectorParameters(context), new RendererCapabilities[0]);
    }

    @Deprecated
    public static DownloadHelper forDash(Uri uri, DataSource.Factory factory, RenderersFactory renderersFactory) {
        return forDash(uri, factory, renderersFactory, null, DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT);
    }

    public static DownloadHelper forDash(Context context, Uri uri, DataSource.Factory factory, RenderersFactory renderersFactory) {
        return forDash(uri, factory, renderersFactory, null, getDefaultTrackSelectorParameters(context));
    }

    public static DownloadHelper forDash(Uri uri, DataSource.Factory factory, RenderersFactory renderersFactory, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, DefaultTrackSelector.Parameters parameters) {
        return new DownloadHelper(DownloadRequest.TYPE_DASH, uri, null, createMediaSourceInternal(DASH_FACTORY_CONSTRUCTOR, uri, factory, drmSessionManager, null), parameters, Util.getRendererCapabilities(renderersFactory));
    }

    @Deprecated
    public static DownloadHelper forHls(Uri uri, DataSource.Factory factory, RenderersFactory renderersFactory) {
        return forHls(uri, factory, renderersFactory, null, DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT);
    }

    public static DownloadHelper forHls(Context context, Uri uri, DataSource.Factory factory, RenderersFactory renderersFactory) {
        return forHls(uri, factory, renderersFactory, null, getDefaultTrackSelectorParameters(context));
    }

    public static DownloadHelper forHls(Uri uri, DataSource.Factory factory, RenderersFactory renderersFactory, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, DefaultTrackSelector.Parameters parameters) {
        return new DownloadHelper(DownloadRequest.TYPE_HLS, uri, null, createMediaSourceInternal(HLS_FACTORY_CONSTRUCTOR, uri, factory, drmSessionManager, null), parameters, Util.getRendererCapabilities(renderersFactory));
    }

    @Deprecated
    public static DownloadHelper forSmoothStreaming(Uri uri, DataSource.Factory factory, RenderersFactory renderersFactory) {
        return forSmoothStreaming(uri, factory, renderersFactory, null, DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_VIEWPORT);
    }

    public static DownloadHelper forSmoothStreaming(Context context, Uri uri, DataSource.Factory factory, RenderersFactory renderersFactory) {
        return forSmoothStreaming(uri, factory, renderersFactory, null, getDefaultTrackSelectorParameters(context));
    }

    public static DownloadHelper forSmoothStreaming(Uri uri, DataSource.Factory factory, RenderersFactory renderersFactory, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, DefaultTrackSelector.Parameters parameters) {
        return new DownloadHelper(DownloadRequest.TYPE_SS, uri, null, createMediaSourceInternal(SS_FACTORY_CONSTRUCTOR, uri, factory, drmSessionManager, null), parameters, Util.getRendererCapabilities(renderersFactory));
    }

    public static MediaSource createMediaSource(DownloadRequest downloadRequest, DataSource.Factory factory) {
        return createMediaSource(downloadRequest, factory, DrmSessionManager.getDummyDrmSessionManager());
    }

    public static MediaSource createMediaSource(DownloadRequest downloadRequest, DataSource.Factory factory, DrmSessionManager<?> drmSessionManager) {
        Constructor<? extends MediaSourceFactory> constructor;
        String str = downloadRequest.type;
        str.hashCode();
        switch (str) {
            case "ss":
                constructor = SS_FACTORY_CONSTRUCTOR;
                break;
            case "hls":
                constructor = HLS_FACTORY_CONSTRUCTOR;
                break;
            case "dash":
                constructor = DASH_FACTORY_CONSTRUCTOR;
                break;
            case "progressive":
                return new ProgressiveMediaSource.Factory(factory).setCustomCacheKey(downloadRequest.customCacheKey).createMediaSource(downloadRequest.uri);
            default:
                throw new IllegalStateException("Unsupported type: " + downloadRequest.type);
        }
        return createMediaSourceInternal(constructor, downloadRequest.uri, factory, drmSessionManager, downloadRequest.streamKeys);
    }

    public DownloadHelper(String str, Uri uri, String str2, MediaSource mediaSource, DefaultTrackSelector.Parameters parameters, RendererCapabilities[] rendererCapabilitiesArr) {
        this.downloadType = str;
        this.uri = uri;
        this.cacheKey = str2;
        this.mediaSource = mediaSource;
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(parameters, new DownloadTrackSelection.Factory());
        this.trackSelector = defaultTrackSelector;
        this.rendererCapabilities = rendererCapabilitiesArr;
        this.scratchSet = new SparseIntArray();
        defaultTrackSelector.init(new TrackSelector.InvalidationListener() { // from class: com.google.android.exoplayer2.offline.DownloadHelper$$ExternalSyntheticLambda0
            @Override // com.google.android.exoplayer2.trackselection.TrackSelector.InvalidationListener
            public final void onTrackSelectionsInvalidated() {
                DownloadHelper.lambda$new$0();
            }
        }, new DummyBandwidthMeter());
        this.callbackHandler = new Handler(Util.getLooper());
        this.window = new Timeline.Window();
    }

    public void prepare(final Callback callback) {
        Assertions.checkState(this.callback == null);
        this.callback = callback;
        if (this.mediaSource != null) {
            this.mediaPreparer = new MediaPreparer(this.mediaSource, this);
        } else {
            this.callbackHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.offline.DownloadHelper$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    DownloadHelper.this.m29xc541203f(callback);
                }
            });
        }
    }

    /* renamed from: lambda$prepare$1$com-google-android-exoplayer2-offline-DownloadHelper, reason: not valid java name */
    /* synthetic */ void m29xc541203f(Callback callback) {
        callback.onPrepared(this);
    }

    public void release() {
        MediaPreparer mediaPreparer = this.mediaPreparer;
        if (mediaPreparer != null) {
            mediaPreparer.release();
        }
    }

    public Object getManifest() {
        if (this.mediaSource == null) {
            return null;
        }
        assertPreparedWithMedia();
        if (this.mediaPreparer.timeline.getWindowCount() > 0) {
            return this.mediaPreparer.timeline.getWindow(0, this.window).manifest;
        }
        return null;
    }

    public int getPeriodCount() {
        if (this.mediaSource == null) {
            return 0;
        }
        assertPreparedWithMedia();
        return this.trackGroupArrays.length;
    }

    public TrackGroupArray getTrackGroups(int i) {
        assertPreparedWithMedia();
        return this.trackGroupArrays[i];
    }

    public MappingTrackSelector.MappedTrackInfo getMappedTrackInfo(int i) {
        assertPreparedWithMedia();
        return this.mappedTrackInfos[i];
    }

    public List<TrackSelection> getTrackSelections(int i, int i2) {
        assertPreparedWithMedia();
        return this.immutableTrackSelectionsByPeriodAndRenderer[i][i2];
    }

    public void clearTrackSelections(int i) {
        assertPreparedWithMedia();
        for (int i2 = 0; i2 < this.rendererCapabilities.length; i2++) {
            this.trackSelectionsByPeriodAndRenderer[i][i2].clear();
        }
    }

    public void replaceTrackSelections(int i, DefaultTrackSelector.Parameters parameters) {
        clearTrackSelections(i);
        addTrackSelection(i, parameters);
    }

    public void addTrackSelection(int i, DefaultTrackSelector.Parameters parameters) {
        assertPreparedWithMedia();
        this.trackSelector.setParameters(parameters);
        runTrackSelection(i);
    }

    public void addAudioLanguagesToSelection(String... strArr) {
        assertPreparedWithMedia();
        for (int i = 0; i < this.mappedTrackInfos.length; i++) {
            DefaultTrackSelector.ParametersBuilder buildUpon = DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_CONTEXT.buildUpon();
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = this.mappedTrackInfos[i];
            int rendererCount = mappedTrackInfo.getRendererCount();
            for (int i2 = 0; i2 < rendererCount; i2++) {
                if (mappedTrackInfo.getRendererType(i2) != 1) {
                    buildUpon.setRendererDisabled(i2, true);
                }
            }
            for (String str : strArr) {
                buildUpon.setPreferredAudioLanguage(str);
                addTrackSelection(i, buildUpon.build());
            }
        }
    }

    public void addTextLanguagesToSelection(boolean z, String... strArr) {
        assertPreparedWithMedia();
        for (int i = 0; i < this.mappedTrackInfos.length; i++) {
            DefaultTrackSelector.ParametersBuilder buildUpon = DEFAULT_TRACK_SELECTOR_PARAMETERS_WITHOUT_CONTEXT.buildUpon();
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = this.mappedTrackInfos[i];
            int rendererCount = mappedTrackInfo.getRendererCount();
            for (int i2 = 0; i2 < rendererCount; i2++) {
                if (mappedTrackInfo.getRendererType(i2) != 3) {
                    buildUpon.setRendererDisabled(i2, true);
                }
            }
            buildUpon.setSelectUndeterminedTextLanguage(z);
            for (String str : strArr) {
                buildUpon.setPreferredTextLanguage(str);
                addTrackSelection(i, buildUpon.build());
            }
        }
    }

    public void addTrackSelectionForSingleRenderer(int i, int i2, DefaultTrackSelector.Parameters parameters, List<DefaultTrackSelector.SelectionOverride> list) {
        assertPreparedWithMedia();
        DefaultTrackSelector.ParametersBuilder buildUpon = parameters.buildUpon();
        int i3 = 0;
        while (i3 < this.mappedTrackInfos[i].getRendererCount()) {
            buildUpon.setRendererDisabled(i3, i3 != i2);
            i3++;
        }
        if (list.isEmpty()) {
            addTrackSelection(i, buildUpon.build());
            return;
        }
        TrackGroupArray trackGroups = this.mappedTrackInfos[i].getTrackGroups(i2);
        for (int i4 = 0; i4 < list.size(); i4++) {
            buildUpon.setSelectionOverride(i2, trackGroups, list.get(i4));
            addTrackSelection(i, buildUpon.build());
        }
    }

    public DownloadRequest getDownloadRequest(byte[] bArr) {
        return getDownloadRequest(this.uri.toString(), bArr);
    }

    public DownloadRequest getDownloadRequest(String str, byte[] bArr) {
        if (this.mediaSource == null) {
            return new DownloadRequest(str, this.downloadType, this.uri, Collections.emptyList(), this.cacheKey, bArr);
        }
        assertPreparedWithMedia();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int length = this.trackSelectionsByPeriodAndRenderer.length;
        for (int i = 0; i < length; i++) {
            arrayList2.clear();
            int length2 = this.trackSelectionsByPeriodAndRenderer[i].length;
            for (int i2 = 0; i2 < length2; i2++) {
                arrayList2.addAll(this.trackSelectionsByPeriodAndRenderer[i][i2]);
            }
            arrayList.addAll(this.mediaPreparer.mediaPeriods[i].getStreamKeys(arrayList2));
        }
        return new DownloadRequest(str, this.downloadType, this.uri, arrayList, this.cacheKey, bArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMediaPrepared() {
        Assertions.checkNotNull(this.mediaPreparer);
        Assertions.checkNotNull(this.mediaPreparer.mediaPeriods);
        Assertions.checkNotNull(this.mediaPreparer.timeline);
        int length = this.mediaPreparer.mediaPeriods.length;
        int length2 = this.rendererCapabilities.length;
        this.trackSelectionsByPeriodAndRenderer = (List[][]) Array.newInstance((Class<?>) List.class, length, length2);
        this.immutableTrackSelectionsByPeriodAndRenderer = (List[][]) Array.newInstance((Class<?>) List.class, length, length2);
        for (int i = 0; i < length; i++) {
            for (int i2 = 0; i2 < length2; i2++) {
                this.trackSelectionsByPeriodAndRenderer[i][i2] = new ArrayList();
                this.immutableTrackSelectionsByPeriodAndRenderer[i][i2] = Collections.unmodifiableList(this.trackSelectionsByPeriodAndRenderer[i][i2]);
            }
        }
        this.trackGroupArrays = new TrackGroupArray[length];
        this.mappedTrackInfos = new MappingTrackSelector.MappedTrackInfo[length];
        for (int i3 = 0; i3 < length; i3++) {
            this.trackGroupArrays[i3] = this.mediaPreparer.mediaPeriods[i3].getTrackGroups();
            this.trackSelector.onSelectionActivated(runTrackSelection(i3).info);
            this.mappedTrackInfos[i3] = (MappingTrackSelector.MappedTrackInfo) Assertions.checkNotNull(this.trackSelector.getCurrentMappedTrackInfo());
        }
        setPreparedWithMedia();
        ((Handler) Assertions.checkNotNull(this.callbackHandler)).post(new Runnable() { // from class: com.google.android.exoplayer2.offline.DownloadHelper$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DownloadHelper.this.m28x87de83e3();
            }
        });
    }

    /* renamed from: lambda$onMediaPrepared$2$com-google-android-exoplayer2-offline-DownloadHelper, reason: not valid java name */
    /* synthetic */ void m28x87de83e3() {
        ((Callback) Assertions.checkNotNull(this.callback)).onPrepared(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMediaPreparationFailed(final IOException iOException) {
        ((Handler) Assertions.checkNotNull(this.callbackHandler)).post(new Runnable() { // from class: com.google.android.exoplayer2.offline.DownloadHelper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DownloadHelper.this.m27x7320173b(iOException);
            }
        });
    }

    /* renamed from: lambda$onMediaPreparationFailed$3$com-google-android-exoplayer2-offline-DownloadHelper, reason: not valid java name */
    /* synthetic */ void m27x7320173b(IOException iOException) {
        ((Callback) Assertions.checkNotNull(this.callback)).onPrepareError(this, iOException);
    }

    @RequiresNonNull({"trackGroupArrays", "mappedTrackInfos", "trackSelectionsByPeriodAndRenderer", "immutableTrackSelectionsByPeriodAndRenderer", "mediaPreparer", "mediaPreparer.timeline", "mediaPreparer.mediaPeriods"})
    private void setPreparedWithMedia() {
        this.isPreparedWithMedia = true;
    }

    @EnsuresNonNull({"trackGroupArrays", "mappedTrackInfos", "trackSelectionsByPeriodAndRenderer", "immutableTrackSelectionsByPeriodAndRenderer", "mediaPreparer", "mediaPreparer.timeline", "mediaPreparer.mediaPeriods"})
    private void assertPreparedWithMedia() {
        Assertions.checkState(this.isPreparedWithMedia);
    }

    @RequiresNonNull({"trackGroupArrays", "trackSelectionsByPeriodAndRenderer", "mediaPreparer", "mediaPreparer.timeline"})
    private TrackSelectorResult runTrackSelection(int i) {
        boolean z;
        try {
            TrackSelectorResult selectTracks = this.trackSelector.selectTracks(this.rendererCapabilities, this.trackGroupArrays[i], new MediaSource.MediaPeriodId(this.mediaPreparer.timeline.getUidOfPeriod(i)), this.mediaPreparer.timeline);
            for (int i2 = 0; i2 < selectTracks.length; i2++) {
                TrackSelection trackSelection = selectTracks.selections.get(i2);
                if (trackSelection != null) {
                    List<TrackSelection> list = this.trackSelectionsByPeriodAndRenderer[i][i2];
                    int i3 = 0;
                    while (true) {
                        if (i3 >= list.size()) {
                            z = false;
                            break;
                        }
                        TrackSelection trackSelection2 = list.get(i3);
                        if (trackSelection2.getTrackGroup() == trackSelection.getTrackGroup()) {
                            this.scratchSet.clear();
                            for (int i4 = 0; i4 < trackSelection2.length(); i4++) {
                                this.scratchSet.put(trackSelection2.getIndexInTrackGroup(i4), 0);
                            }
                            for (int i5 = 0; i5 < trackSelection.length(); i5++) {
                                this.scratchSet.put(trackSelection.getIndexInTrackGroup(i5), 0);
                            }
                            int[] iArr = new int[this.scratchSet.size()];
                            for (int i6 = 0; i6 < this.scratchSet.size(); i6++) {
                                iArr[i6] = this.scratchSet.keyAt(i6);
                            }
                            list.set(i3, new DownloadTrackSelection(trackSelection2.getTrackGroup(), iArr));
                            z = true;
                        } else {
                            i3++;
                        }
                    }
                    if (!z) {
                        list.add(trackSelection);
                    }
                }
            }
            return selectTracks;
        } catch (ExoPlaybackException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static Constructor<? extends MediaSourceFactory> getConstructor(String str) {
        try {
            return Class.forName(str).asSubclass(MediaSourceFactory.class).getConstructor(DataSource.Factory.class);
        } catch (ClassNotFoundException unused) {
            return null;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private static MediaSource createMediaSourceInternal(Constructor<? extends MediaSourceFactory> constructor, Uri uri, DataSource.Factory factory, DrmSessionManager<?> drmSessionManager, List<StreamKey> list) {
        if (constructor == null) {
            throw new IllegalStateException("Module missing to create media source.");
        }
        try {
            MediaSourceFactory newInstance = constructor.newInstance(factory);
            if (drmSessionManager != null) {
                newInstance.setDrmSessionManager(drmSessionManager);
            }
            if (list != null) {
                newInstance.setStreamKeys(list);
            }
            return (MediaSource) Assertions.checkNotNull(newInstance.createMediaSource(uri));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate media source.", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class MediaPreparer implements MediaSource.MediaSourceCaller, MediaPeriod.Callback, Handler.Callback {
        private static final int DOWNLOAD_HELPER_CALLBACK_MESSAGE_FAILED = 1;
        private static final int DOWNLOAD_HELPER_CALLBACK_MESSAGE_PREPARED = 0;
        private static final int MESSAGE_CHECK_FOR_FAILURE = 1;
        private static final int MESSAGE_CONTINUE_LOADING = 2;
        private static final int MESSAGE_PREPARE_SOURCE = 0;
        private static final int MESSAGE_RELEASE = 3;
        private final DownloadHelper downloadHelper;
        public MediaPeriod[] mediaPeriods;
        private final MediaSource mediaSource;
        private final Handler mediaSourceHandler;
        private final HandlerThread mediaSourceThread;
        private boolean released;
        public Timeline timeline;
        private final Allocator allocator = new DefaultAllocator(true, 65536);
        private final ArrayList<MediaPeriod> pendingMediaPeriods = new ArrayList<>();
        private final Handler downloadHelperHandler = Util.createHandler(new Handler.Callback() { // from class: com.google.android.exoplayer2.offline.DownloadHelper$MediaPreparer$$ExternalSyntheticLambda0
            @Override // android.os.Handler.Callback
            public final boolean handleMessage(Message message) {
                boolean handleDownloadHelperCallbackMessage;
                handleDownloadHelperCallbackMessage = DownloadHelper.MediaPreparer.this.handleDownloadHelperCallbackMessage(message);
                return handleDownloadHelperCallbackMessage;
            }
        });

        public MediaPreparer(MediaSource mediaSource, DownloadHelper downloadHelper) {
            this.mediaSource = mediaSource;
            this.downloadHelper = downloadHelper;
            HandlerThread handlerThread = new HandlerThread("DownloadHelper");
            this.mediaSourceThread = handlerThread;
            handlerThread.start();
            Handler createHandler = Util.createHandler(handlerThread.getLooper(), this);
            this.mediaSourceHandler = createHandler;
            createHandler.sendEmptyMessage(0);
        }

        public void release() {
            if (this.released) {
                return;
            }
            this.released = true;
            this.mediaSourceHandler.sendEmptyMessage(3);
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                this.mediaSource.prepareSource(this, null);
                this.mediaSourceHandler.sendEmptyMessage(1);
                return true;
            }
            int i2 = 0;
            if (i == 1) {
                try {
                    if (this.mediaPeriods == null) {
                        this.mediaSource.maybeThrowSourceInfoRefreshError();
                    } else {
                        while (i2 < this.pendingMediaPeriods.size()) {
                            this.pendingMediaPeriods.get(i2).maybeThrowPrepareError();
                            i2++;
                        }
                    }
                    this.mediaSourceHandler.sendEmptyMessageDelayed(1, 100L);
                } catch (IOException e) {
                    this.downloadHelperHandler.obtainMessage(1, e).sendToTarget();
                }
                return true;
            }
            if (i == 2) {
                MediaPeriod mediaPeriod = (MediaPeriod) message.obj;
                if (this.pendingMediaPeriods.contains(mediaPeriod)) {
                    mediaPeriod.continueLoading(0L);
                }
                return true;
            }
            if (i != 3) {
                return false;
            }
            MediaPeriod[] mediaPeriodArr = this.mediaPeriods;
            if (mediaPeriodArr != null) {
                int length = mediaPeriodArr.length;
                while (i2 < length) {
                    this.mediaSource.releasePeriod(mediaPeriodArr[i2]);
                    i2++;
                }
            }
            this.mediaSource.releaseSource(this);
            this.mediaSourceHandler.removeCallbacksAndMessages(null);
            this.mediaSourceThread.quit();
            return true;
        }

        @Override // com.google.android.exoplayer2.source.MediaSource.MediaSourceCaller
        public void onSourceInfoRefreshed(MediaSource mediaSource, Timeline timeline) {
            MediaPeriod[] mediaPeriodArr;
            if (this.timeline != null) {
                return;
            }
            if (timeline.getWindow(0, new Timeline.Window()).isLive) {
                this.downloadHelperHandler.obtainMessage(1, new LiveContentUnsupportedException()).sendToTarget();
                return;
            }
            this.timeline = timeline;
            this.mediaPeriods = new MediaPeriod[timeline.getPeriodCount()];
            int i = 0;
            while (true) {
                mediaPeriodArr = this.mediaPeriods;
                if (i >= mediaPeriodArr.length) {
                    break;
                }
                MediaPeriod createPeriod = this.mediaSource.createPeriod(new MediaSource.MediaPeriodId(timeline.getUidOfPeriod(i)), this.allocator, 0L);
                this.mediaPeriods[i] = createPeriod;
                this.pendingMediaPeriods.add(createPeriod);
                i++;
            }
            for (MediaPeriod mediaPeriod : mediaPeriodArr) {
                mediaPeriod.prepare(this, 0L);
            }
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod.Callback
        public void onPrepared(MediaPeriod mediaPeriod) {
            this.pendingMediaPeriods.remove(mediaPeriod);
            if (this.pendingMediaPeriods.isEmpty()) {
                this.mediaSourceHandler.removeMessages(1);
                this.downloadHelperHandler.sendEmptyMessage(0);
            }
        }

        @Override // com.google.android.exoplayer2.source.SequenceableLoader.Callback
        public void onContinueLoadingRequested(MediaPeriod mediaPeriod) {
            if (this.pendingMediaPeriods.contains(mediaPeriod)) {
                this.mediaSourceHandler.obtainMessage(2, mediaPeriod).sendToTarget();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean handleDownloadHelperCallbackMessage(Message message) {
            if (this.released) {
                return false;
            }
            int i = message.what;
            if (i == 0) {
                this.downloadHelper.onMediaPrepared();
                return true;
            }
            if (i != 1) {
                return false;
            }
            release();
            this.downloadHelper.onMediaPreparationFailed((IOException) Util.castNonNull(message.obj));
            return true;
        }
    }

    private static final class DownloadTrackSelection extends BaseTrackSelection {
        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public int getSelectedIndex() {
            return 0;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public Object getSelectionData() {
            return null;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public int getSelectionReason() {
            return 0;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public void updateSelectedTrack(long j, long j2, long j3, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIteratorArr) {
        }

        private static final class Factory implements TrackSelection.Factory {
            private Factory() {
            }

            @Override // com.google.android.exoplayer2.trackselection.TrackSelection.Factory
            public TrackSelection[] createTrackSelections(TrackSelection.Definition[] definitionArr, BandwidthMeter bandwidthMeter) {
                TrackSelection[] trackSelectionArr = new TrackSelection[definitionArr.length];
                for (int i = 0; i < definitionArr.length; i++) {
                    trackSelectionArr[i] = definitionArr[i] == null ? null : new DownloadTrackSelection(definitionArr[i].group, definitionArr[i].tracks);
                }
                return trackSelectionArr;
            }
        }

        public DownloadTrackSelection(TrackGroup trackGroup, int[] iArr) {
            super(trackGroup, iArr);
        }
    }

    private static final class DummyBandwidthMeter implements BandwidthMeter {
        @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
        public void addEventListener(Handler handler, BandwidthMeter.EventListener eventListener) {
        }

        @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
        public long getBitrateEstimate() {
            return 0L;
        }

        @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
        public TransferListener getTransferListener() {
            return null;
        }

        @Override // com.google.android.exoplayer2.upstream.BandwidthMeter
        public void removeEventListener(BandwidthMeter.EventListener eventListener) {
        }

        private DummyBandwidthMeter() {
        }
    }
}