package com.google.android.exoplayer2.analytics;

import android.os.SystemClock;
import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.PlaybackSessionManager;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class PlaybackStatsListener implements AnalyticsListener, PlaybackSessionManager.Listener {
    private String activeAdPlayback;
    private String activeContentPlayback;
    private final Callback callback;
    private PlaybackStats finishedPlaybackStats;
    private boolean isSuppressed;
    private final boolean keepHistory;
    private final Timeline.Period period;
    private boolean playWhenReady;
    private float playbackSpeed;
    private int playbackState;
    private final Map<String, PlaybackStatsTracker> playbackStatsTrackers;
    private final PlaybackSessionManager sessionManager;
    private final Map<String, AnalyticsListener.EventTime> sessionStartEventTimes;

    public interface Callback {
        void onPlaybackStatsReady(AnalyticsListener.EventTime eventTime, PlaybackStats playbackStats);
    }

    public PlaybackStatsListener(boolean z, Callback callback) {
        this.callback = callback;
        this.keepHistory = z;
        DefaultPlaybackSessionManager defaultPlaybackSessionManager = new DefaultPlaybackSessionManager();
        this.sessionManager = defaultPlaybackSessionManager;
        this.playbackStatsTrackers = new HashMap();
        this.sessionStartEventTimes = new HashMap();
        this.finishedPlaybackStats = PlaybackStats.EMPTY;
        this.playWhenReady = false;
        this.playbackState = 1;
        this.playbackSpeed = 1.0f;
        this.period = new Timeline.Period();
        defaultPlaybackSessionManager.setListener(this);
    }

    public PlaybackStats getCombinedPlaybackStats() {
        int i = 1;
        PlaybackStats[] playbackStatsArr = new PlaybackStats[this.playbackStatsTrackers.size() + 1];
        playbackStatsArr[0] = this.finishedPlaybackStats;
        Iterator<PlaybackStatsTracker> it = this.playbackStatsTrackers.values().iterator();
        while (it.hasNext()) {
            playbackStatsArr[i] = it.next().build(false);
            i++;
        }
        return PlaybackStats.merge(playbackStatsArr);
    }

    public PlaybackStats getPlaybackStats() {
        PlaybackStatsTracker playbackStatsTracker;
        String str = this.activeAdPlayback;
        if (str != null) {
            playbackStatsTracker = this.playbackStatsTrackers.get(str);
        } else {
            String str2 = this.activeContentPlayback;
            playbackStatsTracker = str2 != null ? this.playbackStatsTrackers.get(str2) : null;
        }
        if (playbackStatsTracker == null) {
            return null;
        }
        return playbackStatsTracker.build(false);
    }

    public void finishAllSessions() {
        HashMap hashMap = new HashMap(this.playbackStatsTrackers);
        AnalyticsListener.EventTime eventTime = new AnalyticsListener.EventTime(SystemClock.elapsedRealtime(), Timeline.EMPTY, 0, null, 0L, 0L, 0L);
        Iterator it = hashMap.keySet().iterator();
        while (it.hasNext()) {
            onSessionFinished(eventTime, (String) it.next(), false);
        }
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager.Listener
    public void onSessionCreated(AnalyticsListener.EventTime eventTime, String str) {
        PlaybackStatsTracker playbackStatsTracker = new PlaybackStatsTracker(this.keepHistory, eventTime);
        playbackStatsTracker.onPlayerStateChanged(eventTime, this.playWhenReady, this.playbackState, true);
        playbackStatsTracker.onIsSuppressedChanged(eventTime, this.isSuppressed, true);
        playbackStatsTracker.onPlaybackSpeedChanged(eventTime, this.playbackSpeed);
        this.playbackStatsTrackers.put(str, playbackStatsTracker);
        this.sessionStartEventTimes.put(str, eventTime);
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager.Listener
    public void onSessionActive(AnalyticsListener.EventTime eventTime, String str) {
        ((PlaybackStatsTracker) Assertions.checkNotNull(this.playbackStatsTrackers.get(str))).onForeground(eventTime);
        if (eventTime.mediaPeriodId != null && eventTime.mediaPeriodId.isAd()) {
            this.activeAdPlayback = str;
        } else {
            this.activeContentPlayback = str;
        }
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager.Listener
    public void onAdPlaybackStarted(AnalyticsListener.EventTime eventTime, String str, String str2) {
        Assertions.checkState(((MediaSource.MediaPeriodId) Assertions.checkNotNull(eventTime.mediaPeriodId)).isAd());
        ((PlaybackStatsTracker) Assertions.checkNotNull(this.playbackStatsTrackers.get(str))).onInterruptedByAd(new AnalyticsListener.EventTime(eventTime.realtimeMs, eventTime.timeline, eventTime.windowIndex, new MediaSource.MediaPeriodId(eventTime.mediaPeriodId.periodUid, eventTime.mediaPeriodId.windowSequenceNumber, eventTime.mediaPeriodId.adGroupIndex), C.usToMs(eventTime.timeline.getPeriodByUid(eventTime.mediaPeriodId.periodUid, this.period).getAdGroupTimeUs(eventTime.mediaPeriodId.adGroupIndex)), eventTime.currentPlaybackPositionMs, eventTime.totalBufferedDurationMs));
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager.Listener
    public void onSessionFinished(AnalyticsListener.EventTime eventTime, String str, boolean z) {
        if (str.equals(this.activeAdPlayback)) {
            this.activeAdPlayback = null;
        } else if (str.equals(this.activeContentPlayback)) {
            this.activeContentPlayback = null;
        }
        PlaybackStatsTracker playbackStatsTracker = (PlaybackStatsTracker) Assertions.checkNotNull(this.playbackStatsTrackers.remove(str));
        AnalyticsListener.EventTime eventTime2 = (AnalyticsListener.EventTime) Assertions.checkNotNull(this.sessionStartEventTimes.remove(str));
        if (z) {
            playbackStatsTracker.onPlayerStateChanged(eventTime, true, 4, false);
        }
        playbackStatsTracker.onFinished(eventTime);
        PlaybackStats build = playbackStatsTracker.build(true);
        this.finishedPlaybackStats = PlaybackStats.merge(this.finishedPlaybackStats, build);
        Callback callback = this.callback;
        if (callback != null) {
            callback.onPlaybackStatsReady(eventTime2, build);
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean z, int i) {
        this.playWhenReady = z;
        this.playbackState = i;
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            this.playbackStatsTrackers.get(str).onPlayerStateChanged(eventTime, z, i, this.sessionManager.belongsToSession(eventTime, str));
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlaybackSuppressionReasonChanged(AnalyticsListener.EventTime eventTime, int i) {
        this.isSuppressed = i != 0;
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            this.playbackStatsTrackers.get(str).onIsSuppressedChanged(eventTime, this.isSuppressed, this.sessionManager.belongsToSession(eventTime, str));
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onTimelineChanged(AnalyticsListener.EventTime eventTime, int i) {
        this.sessionManager.handleTimelineUpdate(eventTime);
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onPositionDiscontinuity(eventTime);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime, int i) {
        this.sessionManager.handlePositionDiscontinuity(eventTime, i);
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onPositionDiscontinuity(eventTime);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onSeekStarted(eventTime);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onSeekProcessed(AnalyticsListener.EventTime eventTime) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onSeekProcessed(eventTime);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlayerError(AnalyticsListener.EventTime eventTime, ExoPlaybackException exoPlaybackException) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onFatalError(eventTime, exoPlaybackException);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onPlaybackParametersChanged(AnalyticsListener.EventTime eventTime, PlaybackParameters playbackParameters) {
        this.playbackSpeed = playbackParameters.speed;
        this.sessionManager.updateSessions(eventTime);
        Iterator<PlaybackStatsTracker> it = this.playbackStatsTrackers.values().iterator();
        while (it.hasNext()) {
            it.next().onPlaybackSpeedChanged(eventTime, this.playbackSpeed);
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onTracksChanged(AnalyticsListener.EventTime eventTime, TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onTracksChanged(eventTime, trackSelectionArray);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onLoadStarted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onLoadStarted(eventTime);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDownstreamFormatChanged(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onDownstreamFormatChanged(eventTime, mediaLoadData);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2, int i3, float f) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onVideoSizeChanged(eventTime, i, i2);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onBandwidthEstimate(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onBandwidthData(i, j);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onAudioUnderrun(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onAudioUnderrun();
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDroppedVideoFrames(AnalyticsListener.EventTime eventTime, int i, long j) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onDroppedVideoFrames(i);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onLoadError(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException iOException, boolean z) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onNonFatalError(eventTime, iOException);
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
    public void onDrmSessionManagerError(AnalyticsListener.EventTime eventTime, Exception exc) {
        this.sessionManager.updateSessions(eventTime);
        for (String str : this.playbackStatsTrackers.keySet()) {
            if (this.sessionManager.belongsToSession(eventTime, str)) {
                this.playbackStatsTrackers.get(str).onNonFatalError(eventTime, exc);
            }
        }
    }

    private static final class PlaybackStatsTracker {
        private long audioFormatBitrateTimeProduct;
        private final List<Pair<AnalyticsListener.EventTime, Format>> audioFormatHistory;
        private long audioFormatTimeMs;
        private long audioUnderruns;
        private long bandwidthBytes;
        private long bandwidthTimeMs;
        private Format currentAudioFormat;
        private float currentPlaybackSpeed;
        private int currentPlaybackState;
        private long currentPlaybackStateStartTimeMs;
        private Format currentVideoFormat;
        private long droppedFrames;
        private int fatalErrorCount;
        private final List<Pair<AnalyticsListener.EventTime, Exception>> fatalErrorHistory;
        private long firstReportedTimeMs;
        private boolean hasBeenReady;
        private boolean hasEnded;
        private boolean hasFatalError;
        private long initialAudioFormatBitrate;
        private long initialVideoFormatBitrate;
        private int initialVideoFormatHeight;
        private final boolean isAd;
        private boolean isFinished;
        private boolean isForeground;
        private boolean isInterruptedByAd;
        private boolean isJoinTimeInvalid;
        private boolean isSeeking;
        private boolean isSuppressed;
        private final boolean keepHistory;
        private long lastAudioFormatStartTimeMs;
        private long lastRebufferStartTimeMs;
        private long lastVideoFormatStartTimeMs;
        private long maxRebufferTimeMs;
        private final List<long[]> mediaTimeHistory;
        private int nonFatalErrorCount;
        private final List<Pair<AnalyticsListener.EventTime, Exception>> nonFatalErrorHistory;
        private int pauseBufferCount;
        private int pauseCount;
        private boolean playWhenReady;
        private final long[] playbackStateDurationsMs = new long[16];
        private final List<Pair<AnalyticsListener.EventTime, Integer>> playbackStateHistory;
        private int playerPlaybackState;
        private int rebufferCount;
        private int seekCount;
        private boolean startedLoading;
        private long videoFormatBitrateTimeMs;
        private long videoFormatBitrateTimeProduct;
        private long videoFormatHeightTimeMs;
        private long videoFormatHeightTimeProduct;
        private final List<Pair<AnalyticsListener.EventTime, Format>> videoFormatHistory;

        private static boolean isInvalidJoinTransition(int i, int i2) {
            return ((i != 1 && i != 2 && i != 14) || i2 == 1 || i2 == 2 || i2 == 14 || i2 == 3 || i2 == 4 || i2 == 9 || i2 == 11) ? false : true;
        }

        private static boolean isPausedState(int i) {
            return i == 4 || i == 7;
        }

        private static boolean isReadyState(int i) {
            return i == 3 || i == 4 || i == 9;
        }

        private static boolean isRebufferingState(int i) {
            return i == 6 || i == 7 || i == 10;
        }

        public PlaybackStatsTracker(boolean z, AnalyticsListener.EventTime eventTime) {
            this.keepHistory = z;
            this.playbackStateHistory = z ? new ArrayList<>() : Collections.emptyList();
            this.mediaTimeHistory = z ? new ArrayList<>() : Collections.emptyList();
            this.videoFormatHistory = z ? new ArrayList<>() : Collections.emptyList();
            this.audioFormatHistory = z ? new ArrayList<>() : Collections.emptyList();
            this.fatalErrorHistory = z ? new ArrayList<>() : Collections.emptyList();
            this.nonFatalErrorHistory = z ? new ArrayList<>() : Collections.emptyList();
            boolean z2 = false;
            this.currentPlaybackState = 0;
            this.currentPlaybackStateStartTimeMs = eventTime.realtimeMs;
            this.playerPlaybackState = 1;
            this.firstReportedTimeMs = C.TIME_UNSET;
            this.maxRebufferTimeMs = C.TIME_UNSET;
            if (eventTime.mediaPeriodId != null && eventTime.mediaPeriodId.isAd()) {
                z2 = true;
            }
            this.isAd = z2;
            this.initialAudioFormatBitrate = -1L;
            this.initialVideoFormatBitrate = -1L;
            this.initialVideoFormatHeight = -1;
            this.currentPlaybackSpeed = 1.0f;
        }

        public void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean z, int i, boolean z2) {
            this.playWhenReady = z;
            this.playerPlaybackState = i;
            if (i != 1) {
                this.hasFatalError = false;
            }
            if (i == 1 || i == 4) {
                this.isInterruptedByAd = false;
            }
            maybeUpdatePlaybackState(eventTime, z2);
        }

        public void onIsSuppressedChanged(AnalyticsListener.EventTime eventTime, boolean z, boolean z2) {
            this.isSuppressed = z;
            maybeUpdatePlaybackState(eventTime, z2);
        }

        public void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime) {
            this.isInterruptedByAd = false;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            this.isSeeking = true;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onSeekProcessed(AnalyticsListener.EventTime eventTime) {
            this.isSeeking = false;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onFatalError(AnalyticsListener.EventTime eventTime, Exception exc) {
            this.fatalErrorCount++;
            if (this.keepHistory) {
                this.fatalErrorHistory.add(Pair.create(eventTime, exc));
            }
            this.hasFatalError = true;
            this.isInterruptedByAd = false;
            this.isSeeking = false;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onLoadStarted(AnalyticsListener.EventTime eventTime) {
            this.startedLoading = true;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onForeground(AnalyticsListener.EventTime eventTime) {
            this.isForeground = true;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onInterruptedByAd(AnalyticsListener.EventTime eventTime) {
            this.isInterruptedByAd = true;
            this.isSeeking = false;
            maybeUpdatePlaybackState(eventTime, true);
        }

        public void onFinished(AnalyticsListener.EventTime eventTime) {
            this.isFinished = true;
            maybeUpdatePlaybackState(eventTime, false);
        }

        public void onTracksChanged(AnalyticsListener.EventTime eventTime, TrackSelectionArray trackSelectionArray) {
            boolean z = false;
            boolean z2 = false;
            for (TrackSelection trackSelection : trackSelectionArray.getAll()) {
                if (trackSelection != null && trackSelection.length() > 0) {
                    int trackType = MimeTypes.getTrackType(trackSelection.getFormat(0).sampleMimeType);
                    if (trackType == 2) {
                        z = true;
                    } else if (trackType == 1) {
                        z2 = true;
                    }
                }
            }
            if (!z) {
                maybeUpdateVideoFormat(eventTime, null);
            }
            if (z2) {
                return;
            }
            maybeUpdateAudioFormat(eventTime, null);
        }

        public void onDownstreamFormatChanged(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
            if (mediaLoadData.trackType == 2 || mediaLoadData.trackType == 0) {
                maybeUpdateVideoFormat(eventTime, mediaLoadData.trackFormat);
            } else if (mediaLoadData.trackType == 1) {
                maybeUpdateAudioFormat(eventTime, mediaLoadData.trackFormat);
            }
        }

        public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2) {
            Format format = this.currentVideoFormat;
            if (format == null || format.height != -1) {
                return;
            }
            maybeUpdateVideoFormat(eventTime, this.currentVideoFormat.copyWithVideoSize(i, i2));
        }

        public void onPlaybackSpeedChanged(AnalyticsListener.EventTime eventTime, float f) {
            maybeUpdateMediaTimeHistory(eventTime.realtimeMs, eventTime.eventPlaybackPositionMs);
            maybeRecordVideoFormatTime(eventTime.realtimeMs);
            maybeRecordAudioFormatTime(eventTime.realtimeMs);
            this.currentPlaybackSpeed = f;
        }

        public void onAudioUnderrun() {
            this.audioUnderruns++;
        }

        public void onDroppedVideoFrames(int i) {
            this.droppedFrames += i;
        }

        public void onBandwidthData(long j, long j2) {
            this.bandwidthTimeMs += j;
            this.bandwidthBytes += j2;
        }

        public void onNonFatalError(AnalyticsListener.EventTime eventTime, Exception exc) {
            this.nonFatalErrorCount++;
            if (this.keepHistory) {
                this.nonFatalErrorHistory.add(Pair.create(eventTime, exc));
            }
        }

        public PlaybackStats build(boolean z) {
            long[] jArr;
            List<long[]> list;
            long[] jArr2 = this.playbackStateDurationsMs;
            List<long[]> list2 = this.mediaTimeHistory;
            if (z) {
                jArr = jArr2;
                list = list2;
            } else {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long[] copyOf = Arrays.copyOf(this.playbackStateDurationsMs, 16);
                long max = Math.max(0L, elapsedRealtime - this.currentPlaybackStateStartTimeMs);
                int i = this.currentPlaybackState;
                copyOf[i] = copyOf[i] + max;
                maybeUpdateMaxRebufferTimeMs(elapsedRealtime);
                maybeRecordVideoFormatTime(elapsedRealtime);
                maybeRecordAudioFormatTime(elapsedRealtime);
                ArrayList arrayList = new ArrayList(this.mediaTimeHistory);
                if (this.keepHistory && this.currentPlaybackState == 3) {
                    arrayList.add(guessMediaTimeBasedOnElapsedRealtime(elapsedRealtime));
                }
                jArr = copyOf;
                list = arrayList;
            }
            int i2 = (this.isJoinTimeInvalid || !this.hasBeenReady) ? 1 : 0;
            long j = i2 != 0 ? C.TIME_UNSET : jArr[2];
            int i3 = jArr[1] > 0 ? 1 : 0;
            List arrayList2 = z ? this.videoFormatHistory : new ArrayList(this.videoFormatHistory);
            List arrayList3 = z ? this.audioFormatHistory : new ArrayList(this.audioFormatHistory);
            List arrayList4 = z ? this.playbackStateHistory : new ArrayList(this.playbackStateHistory);
            long j2 = this.firstReportedTimeMs;
            boolean z2 = this.isForeground;
            int i4 = !this.hasBeenReady ? 1 : 0;
            boolean z3 = this.hasEnded;
            int i5 = i2 ^ 1;
            int i6 = this.pauseCount;
            int i7 = this.pauseBufferCount;
            int i8 = this.seekCount;
            int i9 = this.rebufferCount;
            long j3 = this.maxRebufferTimeMs;
            boolean z4 = this.isAd;
            long[] jArr3 = jArr;
            long j4 = this.videoFormatHeightTimeMs;
            long j5 = this.videoFormatHeightTimeProduct;
            long j6 = this.videoFormatBitrateTimeMs;
            long j7 = this.videoFormatBitrateTimeProduct;
            long j8 = this.audioFormatTimeMs;
            long j9 = this.audioFormatBitrateTimeProduct;
            int i10 = this.initialVideoFormatHeight;
            int i11 = i10 == -1 ? 0 : 1;
            long j10 = this.initialVideoFormatBitrate;
            int i12 = j10 == -1 ? 0 : 1;
            long j11 = this.initialAudioFormatBitrate;
            int i13 = j11 == -1 ? 0 : 1;
            long j12 = this.bandwidthTimeMs;
            long j13 = this.bandwidthBytes;
            long j14 = this.droppedFrames;
            long j15 = this.audioUnderruns;
            int i14 = this.fatalErrorCount;
            return new PlaybackStats(1, jArr3, arrayList4, list, j2, z2 ? 1 : 0, i4, z3 ? 1 : 0, i3, j, i5, i6, i7, i8, i9, j3, z4 ? 1 : 0, arrayList2, arrayList3, j4, j5, j6, j7, j8, j9, i11, i12, i10, j10, i13, j11, j12, j13, j14, j15, i14 > 0 ? 1 : 0, i14, this.nonFatalErrorCount, this.fatalErrorHistory, this.nonFatalErrorHistory);
        }

        private void maybeUpdatePlaybackState(AnalyticsListener.EventTime eventTime, boolean z) {
            int resolveNewPlaybackState = resolveNewPlaybackState();
            if (resolveNewPlaybackState == this.currentPlaybackState) {
                return;
            }
            Assertions.checkArgument(eventTime.realtimeMs >= this.currentPlaybackStateStartTimeMs);
            long j = eventTime.realtimeMs - this.currentPlaybackStateStartTimeMs;
            long[] jArr = this.playbackStateDurationsMs;
            int i = this.currentPlaybackState;
            jArr[i] = jArr[i] + j;
            long j2 = this.firstReportedTimeMs;
            long j3 = C.TIME_UNSET;
            if (j2 == C.TIME_UNSET) {
                this.firstReportedTimeMs = eventTime.realtimeMs;
            }
            this.isJoinTimeInvalid |= isInvalidJoinTransition(this.currentPlaybackState, resolveNewPlaybackState);
            this.hasBeenReady |= isReadyState(resolveNewPlaybackState);
            this.hasEnded |= resolveNewPlaybackState == 11;
            if (!isPausedState(this.currentPlaybackState) && isPausedState(resolveNewPlaybackState)) {
                this.pauseCount++;
            }
            if (resolveNewPlaybackState == 5) {
                this.seekCount++;
            }
            if (!isRebufferingState(this.currentPlaybackState) && isRebufferingState(resolveNewPlaybackState)) {
                this.rebufferCount++;
                this.lastRebufferStartTimeMs = eventTime.realtimeMs;
            }
            if (isRebufferingState(this.currentPlaybackState) && this.currentPlaybackState != 7 && resolveNewPlaybackState == 7) {
                this.pauseBufferCount++;
            }
            long j4 = eventTime.realtimeMs;
            if (z) {
                j3 = eventTime.eventPlaybackPositionMs;
            }
            maybeUpdateMediaTimeHistory(j4, j3);
            maybeUpdateMaxRebufferTimeMs(eventTime.realtimeMs);
            maybeRecordVideoFormatTime(eventTime.realtimeMs);
            maybeRecordAudioFormatTime(eventTime.realtimeMs);
            this.currentPlaybackState = resolveNewPlaybackState;
            this.currentPlaybackStateStartTimeMs = eventTime.realtimeMs;
            if (this.keepHistory) {
                this.playbackStateHistory.add(Pair.create(eventTime, Integer.valueOf(this.currentPlaybackState)));
            }
        }

        private int resolveNewPlaybackState() {
            if (this.isFinished) {
                return this.currentPlaybackState == 11 ? 11 : 15;
            }
            if (this.isSeeking) {
                return 5;
            }
            if (this.hasFatalError) {
                return 13;
            }
            if (!this.isForeground) {
                return this.startedLoading ? 1 : 0;
            }
            if (this.isInterruptedByAd) {
                return 14;
            }
            int i = this.playerPlaybackState;
            if (i == 4) {
                return 11;
            }
            if (i != 2) {
                if (i == 3) {
                    if (this.playWhenReady) {
                        return this.isSuppressed ? 9 : 3;
                    }
                    return 4;
                }
                if (i != 1 || this.currentPlaybackState == 0) {
                    return this.currentPlaybackState;
                }
                return 12;
            }
            int i2 = this.currentPlaybackState;
            if (i2 == 0 || i2 == 1 || i2 == 2 || i2 == 14) {
                return 2;
            }
            if (i2 == 5 || i2 == 8) {
                return 8;
            }
            if (this.playWhenReady) {
                return this.isSuppressed ? 10 : 6;
            }
            return 7;
        }

        private void maybeUpdateMaxRebufferTimeMs(long j) {
            if (isRebufferingState(this.currentPlaybackState)) {
                long j2 = j - this.lastRebufferStartTimeMs;
                long j3 = this.maxRebufferTimeMs;
                if (j3 == C.TIME_UNSET || j2 > j3) {
                    this.maxRebufferTimeMs = j2;
                }
            }
        }

        private void maybeUpdateMediaTimeHistory(long j, long j2) {
            if (this.currentPlaybackState != 3) {
                if (j2 == C.TIME_UNSET) {
                    return;
                }
                if (!this.mediaTimeHistory.isEmpty()) {
                    List<long[]> list = this.mediaTimeHistory;
                    long j3 = list.get(list.size() - 1)[1];
                    if (j3 != j2) {
                        this.mediaTimeHistory.add(new long[]{j, j3});
                    }
                }
            }
            this.mediaTimeHistory.add(j2 == C.TIME_UNSET ? guessMediaTimeBasedOnElapsedRealtime(j) : new long[]{j, j2});
        }

        private long[] guessMediaTimeBasedOnElapsedRealtime(long j) {
            List<long[]> list = this.mediaTimeHistory;
            return new long[]{j, list.get(list.size() - 1)[1] + ((long) ((j - r0[0]) * this.currentPlaybackSpeed))};
        }

        private void maybeUpdateVideoFormat(AnalyticsListener.EventTime eventTime, Format format) {
            if (Util.areEqual(this.currentVideoFormat, format)) {
                return;
            }
            maybeRecordVideoFormatTime(eventTime.realtimeMs);
            if (format != null) {
                if (this.initialVideoFormatHeight == -1 && format.height != -1) {
                    this.initialVideoFormatHeight = format.height;
                }
                if (this.initialVideoFormatBitrate == -1 && format.bitrate != -1) {
                    this.initialVideoFormatBitrate = format.bitrate;
                }
            }
            this.currentVideoFormat = format;
            if (this.keepHistory) {
                this.videoFormatHistory.add(Pair.create(eventTime, format));
            }
        }

        private void maybeUpdateAudioFormat(AnalyticsListener.EventTime eventTime, Format format) {
            if (Util.areEqual(this.currentAudioFormat, format)) {
                return;
            }
            maybeRecordAudioFormatTime(eventTime.realtimeMs);
            if (format != null && this.initialAudioFormatBitrate == -1 && format.bitrate != -1) {
                this.initialAudioFormatBitrate = format.bitrate;
            }
            this.currentAudioFormat = format;
            if (this.keepHistory) {
                this.audioFormatHistory.add(Pair.create(eventTime, format));
            }
        }

        private void maybeRecordVideoFormatTime(long j) {
            Format format;
            if (this.currentPlaybackState == 3 && (format = this.currentVideoFormat) != null) {
                long j2 = (long) ((j - this.lastVideoFormatStartTimeMs) * this.currentPlaybackSpeed);
                if (format.height != -1) {
                    this.videoFormatHeightTimeMs += j2;
                    this.videoFormatHeightTimeProduct += this.currentVideoFormat.height * j2;
                }
                if (this.currentVideoFormat.bitrate != -1) {
                    this.videoFormatBitrateTimeMs += j2;
                    this.videoFormatBitrateTimeProduct += j2 * this.currentVideoFormat.bitrate;
                }
            }
            this.lastVideoFormatStartTimeMs = j;
        }

        private void maybeRecordAudioFormatTime(long j) {
            Format format;
            if (this.currentPlaybackState == 3 && (format = this.currentAudioFormat) != null && format.bitrate != -1) {
                long j2 = (long) ((j - this.lastAudioFormatStartTimeMs) * this.currentPlaybackSpeed);
                this.audioFormatTimeMs += j2;
                this.audioFormatBitrateTimeProduct += j2 * this.currentAudioFormat.bitrate;
            }
            this.lastAudioFormatStartTimeMs = j;
        }
    }
}