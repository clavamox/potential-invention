package com.google.android.exoplayer2;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import com.google.android.exoplayer2.BasePlayer;
import com.google.android.exoplayer2.ExoPlayerImpl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.PlayerMessage;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
final class ExoPlayerImpl extends BasePlayer implements ExoPlayer {
    private static final String TAG = "ExoPlayerImpl";
    final TrackSelectorResult emptyTrackSelectorResult;
    private final Handler eventHandler;
    private boolean foregroundMode;
    private boolean hasPendingPrepare;
    private boolean hasPendingSeek;
    private final ExoPlayerImplInternal internalPlayer;
    private final Handler internalPlayerHandler;
    private final CopyOnWriteArrayList<BasePlayer.ListenerHolder> listeners;
    private int maskingPeriodIndex;
    private int maskingWindowIndex;
    private long maskingWindowPositionMs;
    private MediaSource mediaSource;
    private final ArrayDeque<Runnable> pendingListenerNotifications;
    private int pendingOperationAcks;
    private int pendingSetPlaybackParametersAcks;
    private final Timeline.Period period;
    private boolean playWhenReady;
    private PlaybackInfo playbackInfo;
    private PlaybackParameters playbackParameters;
    private int playbackSuppressionReason;
    private final Renderer[] renderers;
    private int repeatMode;
    private SeekParameters seekParameters;
    private boolean shuffleModeEnabled;
    private final TrackSelector trackSelector;

    @Override // com.google.android.exoplayer2.Player
    public Player.AudioComponent getAudioComponent() {
        return null;
    }

    @Override // com.google.android.exoplayer2.Player
    public Player.MetadataComponent getMetadataComponent() {
        return null;
    }

    @Override // com.google.android.exoplayer2.Player
    public Player.TextComponent getTextComponent() {
        return null;
    }

    @Override // com.google.android.exoplayer2.Player
    public Player.VideoComponent getVideoComponent() {
        return null;
    }

    public ExoPlayerImpl(Renderer[] rendererArr, TrackSelector trackSelector, LoadControl loadControl, BandwidthMeter bandwidthMeter, Clock clock, Looper looper) {
        Log.i(TAG, "Init " + Integer.toHexString(System.identityHashCode(this)) + " [ExoPlayerLib/2.11.3] [" + Util.DEVICE_DEBUG_INFO + "]");
        Assertions.checkState(rendererArr.length > 0);
        this.renderers = (Renderer[]) Assertions.checkNotNull(rendererArr);
        this.trackSelector = (TrackSelector) Assertions.checkNotNull(trackSelector);
        this.playWhenReady = false;
        this.repeatMode = 0;
        this.shuffleModeEnabled = false;
        this.listeners = new CopyOnWriteArrayList<>();
        TrackSelectorResult trackSelectorResult = new TrackSelectorResult(new RendererConfiguration[rendererArr.length], new TrackSelection[rendererArr.length], null);
        this.emptyTrackSelectorResult = trackSelectorResult;
        this.period = new Timeline.Period();
        this.playbackParameters = PlaybackParameters.DEFAULT;
        this.seekParameters = SeekParameters.DEFAULT;
        this.playbackSuppressionReason = 0;
        Handler handler = new Handler(looper) { // from class: com.google.android.exoplayer2.ExoPlayerImpl.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                ExoPlayerImpl.this.handleEvent(message);
            }
        };
        this.eventHandler = handler;
        this.playbackInfo = PlaybackInfo.createDummy(0L, trackSelectorResult);
        this.pendingListenerNotifications = new ArrayDeque<>();
        ExoPlayerImplInternal exoPlayerImplInternal = new ExoPlayerImplInternal(rendererArr, trackSelector, trackSelectorResult, loadControl, bandwidthMeter, this.playWhenReady, this.repeatMode, this.shuffleModeEnabled, handler, clock);
        this.internalPlayer = exoPlayerImplInternal;
        this.internalPlayerHandler = new Handler(exoPlayerImplInternal.getPlaybackLooper());
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public Looper getPlaybackLooper() {
        return this.internalPlayer.getPlaybackLooper();
    }

    @Override // com.google.android.exoplayer2.Player
    public Looper getApplicationLooper() {
        return this.eventHandler.getLooper();
    }

    @Override // com.google.android.exoplayer2.Player
    public void addListener(Player.EventListener eventListener) {
        this.listeners.addIfAbsent(new BasePlayer.ListenerHolder(eventListener));
    }

    @Override // com.google.android.exoplayer2.Player
    public void removeListener(Player.EventListener eventListener) {
        Iterator<BasePlayer.ListenerHolder> it = this.listeners.iterator();
        while (it.hasNext()) {
            BasePlayer.ListenerHolder next = it.next();
            if (next.listener.equals(eventListener)) {
                next.release();
                this.listeners.remove(next);
            }
        }
    }

    @Override // com.google.android.exoplayer2.Player
    public int getPlaybackState() {
        return this.playbackInfo.playbackState;
    }

    @Override // com.google.android.exoplayer2.Player
    public int getPlaybackSuppressionReason() {
        return this.playbackSuppressionReason;
    }

    @Override // com.google.android.exoplayer2.Player
    public ExoPlaybackException getPlaybackError() {
        return this.playbackInfo.playbackError;
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void retry() {
        if (this.mediaSource == null || this.playbackInfo.playbackState != 1) {
            return;
        }
        prepare(this.mediaSource, false, false);
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void prepare(MediaSource mediaSource) {
        prepare(mediaSource, true, true);
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void prepare(MediaSource mediaSource, boolean z, boolean z2) {
        this.mediaSource = mediaSource;
        PlaybackInfo resetPlaybackInfo = getResetPlaybackInfo(z, z2, true, 2);
        this.hasPendingPrepare = true;
        this.pendingOperationAcks++;
        this.internalPlayer.prepare(mediaSource, z, z2);
        updatePlaybackInfo(resetPlaybackInfo, false, 4, 1, false);
    }

    @Override // com.google.android.exoplayer2.Player
    public void setPlayWhenReady(boolean z) {
        setPlayWhenReady(z, 0);
    }

    public void setPlayWhenReady(final boolean z, final int i) {
        boolean isPlaying = isPlaying();
        boolean z2 = this.playWhenReady && this.playbackSuppressionReason == 0;
        boolean z3 = z && i == 0;
        if (z2 != z3) {
            this.internalPlayer.setPlayWhenReady(z3);
        }
        final boolean z4 = this.playWhenReady != z;
        final boolean z5 = this.playbackSuppressionReason != i;
        this.playWhenReady = z;
        this.playbackSuppressionReason = i;
        final boolean isPlaying2 = isPlaying();
        final boolean z6 = isPlaying != isPlaying2;
        if (z4 || z5 || z6) {
            final int i2 = this.playbackInfo.playbackState;
            notifyListeners(new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda1
                @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                public final void invokeListener(Player.EventListener eventListener) {
                    ExoPlayerImpl.lambda$setPlayWhenReady$0(z4, z, i2, z5, i, z6, isPlaying2, eventListener);
                }
            });
        }
    }

    static /* synthetic */ void lambda$setPlayWhenReady$0(boolean z, boolean z2, int i, boolean z3, int i2, boolean z4, boolean z5, Player.EventListener eventListener) {
        if (z) {
            eventListener.onPlayerStateChanged(z2, i);
        }
        if (z3) {
            eventListener.onPlaybackSuppressionReasonChanged(i2);
        }
        if (z4) {
            eventListener.onIsPlayingChanged(z5);
        }
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean getPlayWhenReady() {
        return this.playWhenReady;
    }

    @Override // com.google.android.exoplayer2.Player
    public void setRepeatMode(final int i) {
        if (this.repeatMode != i) {
            this.repeatMode = i;
            this.internalPlayer.setRepeatMode(i);
            notifyListeners(new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda6
                @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                public final void invokeListener(Player.EventListener eventListener) {
                    eventListener.onRepeatModeChanged(i);
                }
            });
        }
    }

    @Override // com.google.android.exoplayer2.Player
    public int getRepeatMode() {
        return this.repeatMode;
    }

    @Override // com.google.android.exoplayer2.Player
    public void setShuffleModeEnabled(final boolean z) {
        if (this.shuffleModeEnabled != z) {
            this.shuffleModeEnabled = z;
            this.internalPlayer.setShuffleModeEnabled(z);
            notifyListeners(new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda2
                @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                public final void invokeListener(Player.EventListener eventListener) {
                    eventListener.onShuffleModeEnabledChanged(z);
                }
            });
        }
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean getShuffleModeEnabled() {
        return this.shuffleModeEnabled;
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean isLoading() {
        return this.playbackInfo.isLoading;
    }

    @Override // com.google.android.exoplayer2.Player
    public void seekTo(int i, long j) {
        Timeline timeline = this.playbackInfo.timeline;
        if (i < 0 || (!timeline.isEmpty() && i >= timeline.getWindowCount())) {
            throw new IllegalSeekPositionException(timeline, i, j);
        }
        this.hasPendingSeek = true;
        this.pendingOperationAcks++;
        if (isPlayingAd()) {
            Log.w(TAG, "seekTo ignored because an ad is playing");
            this.eventHandler.obtainMessage(0, 1, -1, this.playbackInfo).sendToTarget();
            return;
        }
        this.maskingWindowIndex = i;
        if (timeline.isEmpty()) {
            this.maskingWindowPositionMs = j == C.TIME_UNSET ? 0L : j;
            this.maskingPeriodIndex = 0;
        } else {
            long defaultPositionUs = j == C.TIME_UNSET ? timeline.getWindow(i, this.window).getDefaultPositionUs() : C.msToUs(j);
            Pair<Object, Long> periodPosition = timeline.getPeriodPosition(this.window, this.period, i, defaultPositionUs);
            this.maskingWindowPositionMs = C.usToMs(defaultPositionUs);
            this.maskingPeriodIndex = timeline.getIndexOfPeriod(periodPosition.first);
        }
        this.internalPlayer.seekTo(timeline, i, C.msToUs(j));
        notifyListeners(new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda0
            @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
            public final void invokeListener(Player.EventListener eventListener) {
                eventListener.onPositionDiscontinuity(1);
            }
        });
    }

    @Override // com.google.android.exoplayer2.Player
    public void setPlaybackParameters(final PlaybackParameters playbackParameters) {
        if (playbackParameters == null) {
            playbackParameters = PlaybackParameters.DEFAULT;
        }
        if (this.playbackParameters.equals(playbackParameters)) {
            return;
        }
        this.pendingSetPlaybackParametersAcks++;
        this.playbackParameters = playbackParameters;
        this.internalPlayer.setPlaybackParameters(playbackParameters);
        notifyListeners(new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda4
            @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
            public final void invokeListener(Player.EventListener eventListener) {
                eventListener.onPlaybackParametersChanged(PlaybackParameters.this);
            }
        });
    }

    @Override // com.google.android.exoplayer2.Player
    public PlaybackParameters getPlaybackParameters() {
        return this.playbackParameters;
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void setSeekParameters(SeekParameters seekParameters) {
        if (seekParameters == null) {
            seekParameters = SeekParameters.DEFAULT;
        }
        if (this.seekParameters.equals(seekParameters)) {
            return;
        }
        this.seekParameters = seekParameters;
        this.internalPlayer.setSeekParameters(seekParameters);
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public SeekParameters getSeekParameters() {
        return this.seekParameters;
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void setForegroundMode(boolean z) {
        if (this.foregroundMode != z) {
            this.foregroundMode = z;
            this.internalPlayer.setForegroundMode(z);
        }
    }

    @Override // com.google.android.exoplayer2.Player
    public void stop(boolean z) {
        if (z) {
            this.mediaSource = null;
        }
        PlaybackInfo resetPlaybackInfo = getResetPlaybackInfo(z, z, z, 1);
        this.pendingOperationAcks++;
        this.internalPlayer.stop(z);
        updatePlaybackInfo(resetPlaybackInfo, false, 4, 1, false);
    }

    @Override // com.google.android.exoplayer2.Player
    public void release() {
        Log.i(TAG, "Release " + Integer.toHexString(System.identityHashCode(this)) + " [ExoPlayerLib/2.11.3] [" + Util.DEVICE_DEBUG_INFO + "] [" + ExoPlayerLibraryInfo.registeredModules() + "]");
        this.mediaSource = null;
        this.internalPlayer.release();
        this.eventHandler.removeCallbacksAndMessages(null);
        this.playbackInfo = getResetPlaybackInfo(false, false, false, 1);
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public PlayerMessage createMessage(PlayerMessage.Target target) {
        return new PlayerMessage(this.internalPlayer, target, this.playbackInfo.timeline, getCurrentWindowIndex(), this.internalPlayerHandler);
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentPeriodIndex() {
        if (shouldMaskPosition()) {
            return this.maskingPeriodIndex;
        }
        return this.playbackInfo.timeline.getIndexOfPeriod(this.playbackInfo.periodId.periodUid);
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentWindowIndex() {
        if (shouldMaskPosition()) {
            return this.maskingWindowIndex;
        }
        return this.playbackInfo.timeline.getPeriodByUid(this.playbackInfo.periodId.periodUid, this.period).windowIndex;
    }

    @Override // com.google.android.exoplayer2.Player
    public long getDuration() {
        if (isPlayingAd()) {
            MediaSource.MediaPeriodId mediaPeriodId = this.playbackInfo.periodId;
            this.playbackInfo.timeline.getPeriodByUid(mediaPeriodId.periodUid, this.period);
            return C.usToMs(this.period.getAdDurationUs(mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup));
        }
        return getContentDuration();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getCurrentPosition() {
        if (shouldMaskPosition()) {
            return this.maskingWindowPositionMs;
        }
        if (this.playbackInfo.periodId.isAd()) {
            return C.usToMs(this.playbackInfo.positionUs);
        }
        return periodPositionUsToWindowPositionMs(this.playbackInfo.periodId, this.playbackInfo.positionUs);
    }

    @Override // com.google.android.exoplayer2.Player
    public long getBufferedPosition() {
        if (isPlayingAd()) {
            if (this.playbackInfo.loadingMediaPeriodId.equals(this.playbackInfo.periodId)) {
                return C.usToMs(this.playbackInfo.bufferedPositionUs);
            }
            return getDuration();
        }
        return getContentBufferedPosition();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getTotalBufferedDuration() {
        return C.usToMs(this.playbackInfo.totalBufferedDurationUs);
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean isPlayingAd() {
        return !shouldMaskPosition() && this.playbackInfo.periodId.isAd();
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentAdGroupIndex() {
        if (isPlayingAd()) {
            return this.playbackInfo.periodId.adGroupIndex;
        }
        return -1;
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentAdIndexInAdGroup() {
        if (isPlayingAd()) {
            return this.playbackInfo.periodId.adIndexInAdGroup;
        }
        return -1;
    }

    @Override // com.google.android.exoplayer2.Player
    public long getContentPosition() {
        if (isPlayingAd()) {
            this.playbackInfo.timeline.getPeriodByUid(this.playbackInfo.periodId.periodUid, this.period);
            if (this.playbackInfo.contentPositionUs == C.TIME_UNSET) {
                return this.playbackInfo.timeline.getWindow(getCurrentWindowIndex(), this.window).getDefaultPositionMs();
            }
            return this.period.getPositionInWindowMs() + C.usToMs(this.playbackInfo.contentPositionUs);
        }
        return getCurrentPosition();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getContentBufferedPosition() {
        if (shouldMaskPosition()) {
            return this.maskingWindowPositionMs;
        }
        if (this.playbackInfo.loadingMediaPeriodId.windowSequenceNumber != this.playbackInfo.periodId.windowSequenceNumber) {
            return this.playbackInfo.timeline.getWindow(getCurrentWindowIndex(), this.window).getDurationMs();
        }
        long j = this.playbackInfo.bufferedPositionUs;
        if (this.playbackInfo.loadingMediaPeriodId.isAd()) {
            Timeline.Period periodByUid = this.playbackInfo.timeline.getPeriodByUid(this.playbackInfo.loadingMediaPeriodId.periodUid, this.period);
            long adGroupTimeUs = periodByUid.getAdGroupTimeUs(this.playbackInfo.loadingMediaPeriodId.adGroupIndex);
            j = adGroupTimeUs == Long.MIN_VALUE ? periodByUid.durationUs : adGroupTimeUs;
        }
        return periodPositionUsToWindowPositionMs(this.playbackInfo.loadingMediaPeriodId, j);
    }

    @Override // com.google.android.exoplayer2.Player
    public int getRendererCount() {
        return this.renderers.length;
    }

    @Override // com.google.android.exoplayer2.Player
    public int getRendererType(int i) {
        return this.renderers[i].getTrackType();
    }

    @Override // com.google.android.exoplayer2.Player
    public TrackGroupArray getCurrentTrackGroups() {
        return this.playbackInfo.trackGroups;
    }

    @Override // com.google.android.exoplayer2.Player
    public TrackSelectionArray getCurrentTrackSelections() {
        return this.playbackInfo.trackSelectorResult.selections;
    }

    @Override // com.google.android.exoplayer2.Player
    public Timeline getCurrentTimeline() {
        return this.playbackInfo.timeline;
    }

    void handleEvent(Message message) {
        int i = message.what;
        if (i == 0) {
            handlePlaybackInfo((PlaybackInfo) message.obj, message.arg1, message.arg2 != -1, message.arg2);
        } else {
            if (i == 1) {
                handlePlaybackParameters((PlaybackParameters) message.obj, message.arg1 != 0);
                return;
            }
            throw new IllegalStateException();
        }
    }

    private void handlePlaybackParameters(final PlaybackParameters playbackParameters, boolean z) {
        if (z) {
            this.pendingSetPlaybackParametersAcks--;
        }
        if (this.pendingSetPlaybackParametersAcks != 0 || this.playbackParameters.equals(playbackParameters)) {
            return;
        }
        this.playbackParameters = playbackParameters;
        notifyListeners(new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda5
            @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
            public final void invokeListener(Player.EventListener eventListener) {
                eventListener.onPlaybackParametersChanged(PlaybackParameters.this);
            }
        });
    }

    private void handlePlaybackInfo(PlaybackInfo playbackInfo, int i, boolean z, int i2) {
        int i3 = this.pendingOperationAcks - i;
        this.pendingOperationAcks = i3;
        if (i3 == 0) {
            if (playbackInfo.startPositionUs == C.TIME_UNSET) {
                playbackInfo = playbackInfo.copyWithNewPosition(playbackInfo.periodId, 0L, playbackInfo.contentPositionUs, playbackInfo.totalBufferedDurationUs);
            }
            PlaybackInfo playbackInfo2 = playbackInfo;
            if (!this.playbackInfo.timeline.isEmpty() && playbackInfo2.timeline.isEmpty()) {
                this.maskingPeriodIndex = 0;
                this.maskingWindowIndex = 0;
                this.maskingWindowPositionMs = 0L;
            }
            int i4 = this.hasPendingPrepare ? 0 : 2;
            boolean z2 = this.hasPendingSeek;
            this.hasPendingPrepare = false;
            this.hasPendingSeek = false;
            updatePlaybackInfo(playbackInfo2, z, i2, i4, z2);
        }
    }

    private PlaybackInfo getResetPlaybackInfo(boolean z, boolean z2, boolean z3, int i) {
        if (z) {
            this.maskingWindowIndex = 0;
            this.maskingPeriodIndex = 0;
            this.maskingWindowPositionMs = 0L;
        } else {
            this.maskingWindowIndex = getCurrentWindowIndex();
            this.maskingPeriodIndex = getCurrentPeriodIndex();
            this.maskingWindowPositionMs = getCurrentPosition();
        }
        boolean z4 = z || z2;
        MediaSource.MediaPeriodId dummyFirstMediaPeriodId = z4 ? this.playbackInfo.getDummyFirstMediaPeriodId(this.shuffleModeEnabled, this.window, this.period) : this.playbackInfo.periodId;
        long j = z4 ? 0L : this.playbackInfo.positionUs;
        return new PlaybackInfo(z2 ? Timeline.EMPTY : this.playbackInfo.timeline, dummyFirstMediaPeriodId, j, z4 ? C.TIME_UNSET : this.playbackInfo.contentPositionUs, i, z3 ? null : this.playbackInfo.playbackError, false, z2 ? TrackGroupArray.EMPTY : this.playbackInfo.trackGroups, z2 ? this.emptyTrackSelectorResult : this.playbackInfo.trackSelectorResult, dummyFirstMediaPeriodId, j, 0L, j);
    }

    private void updatePlaybackInfo(PlaybackInfo playbackInfo, boolean z, int i, int i2, boolean z2) {
        boolean isPlaying = isPlaying();
        PlaybackInfo playbackInfo2 = this.playbackInfo;
        this.playbackInfo = playbackInfo;
        notifyListeners(new PlaybackInfoUpdate(playbackInfo, playbackInfo2, this.listeners, this.trackSelector, z, i, i2, z2, this.playWhenReady, isPlaying != isPlaying()));
    }

    private void notifyListeners(final BasePlayer.ListenerInvocation listenerInvocation) {
        final CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList(this.listeners);
        notifyListeners(new Runnable() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerImpl.invokeAll(copyOnWriteArrayList, listenerInvocation);
            }
        });
    }

    private void notifyListeners(Runnable runnable) {
        boolean z = !this.pendingListenerNotifications.isEmpty();
        this.pendingListenerNotifications.addLast(runnable);
        if (z) {
            return;
        }
        while (!this.pendingListenerNotifications.isEmpty()) {
            this.pendingListenerNotifications.peekFirst().run();
            this.pendingListenerNotifications.removeFirst();
        }
    }

    private long periodPositionUsToWindowPositionMs(MediaSource.MediaPeriodId mediaPeriodId, long j) {
        long usToMs = C.usToMs(j);
        this.playbackInfo.timeline.getPeriodByUid(mediaPeriodId.periodUid, this.period);
        return usToMs + this.period.getPositionInWindowMs();
    }

    private boolean shouldMaskPosition() {
        return this.playbackInfo.timeline.isEmpty() || this.pendingOperationAcks > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    static final class PlaybackInfoUpdate implements Runnable {
        private final boolean isLoadingChanged;
        private final boolean isPlayingChanged;
        private final CopyOnWriteArrayList<BasePlayer.ListenerHolder> listenerSnapshot;
        private final boolean playWhenReady;
        private final boolean playbackErrorChanged;
        private final PlaybackInfo playbackInfo;
        private final boolean playbackStateChanged;
        private final boolean positionDiscontinuity;
        private final int positionDiscontinuityReason;
        private final boolean seekProcessed;
        private final int timelineChangeReason;
        private final boolean timelineChanged;
        private final TrackSelector trackSelector;
        private final boolean trackSelectorResultChanged;

        public PlaybackInfoUpdate(PlaybackInfo playbackInfo, PlaybackInfo playbackInfo2, CopyOnWriteArrayList<BasePlayer.ListenerHolder> copyOnWriteArrayList, TrackSelector trackSelector, boolean z, int i, int i2, boolean z2, boolean z3, boolean z4) {
            this.playbackInfo = playbackInfo;
            this.listenerSnapshot = new CopyOnWriteArrayList<>(copyOnWriteArrayList);
            this.trackSelector = trackSelector;
            this.positionDiscontinuity = z;
            this.positionDiscontinuityReason = i;
            this.timelineChangeReason = i2;
            this.seekProcessed = z2;
            this.playWhenReady = z3;
            this.isPlayingChanged = z4;
            this.playbackStateChanged = playbackInfo2.playbackState != playbackInfo.playbackState;
            this.playbackErrorChanged = (playbackInfo2.playbackError == playbackInfo.playbackError || playbackInfo.playbackError == null) ? false : true;
            this.timelineChanged = playbackInfo2.timeline != playbackInfo.timeline;
            this.isLoadingChanged = playbackInfo2.isLoading != playbackInfo.isLoading;
            this.trackSelectorResultChanged = playbackInfo2.trackSelectorResult != playbackInfo.trackSelectorResult;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.timelineChanged || this.timelineChangeReason == 0) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda0
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m10xd48a2375(eventListener);
                    }
                });
            }
            if (this.positionDiscontinuity) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda1
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m11x52eb2754(eventListener);
                    }
                });
            }
            if (this.playbackErrorChanged) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda2
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m12xd14c2b33(eventListener);
                    }
                });
            }
            if (this.trackSelectorResultChanged) {
                this.trackSelector.onSelectionActivated(this.playbackInfo.trackSelectorResult.info);
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda3
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m13x4fad2f12(eventListener);
                    }
                });
            }
            if (this.isLoadingChanged) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda4
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m14xce0e32f1(eventListener);
                    }
                });
            }
            if (this.playbackStateChanged) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda5
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m15x4c6f36d0(eventListener);
                    }
                });
            }
            if (this.isPlayingChanged) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda6
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        ExoPlayerImpl.PlaybackInfoUpdate.this.m16xcad03aaf(eventListener);
                    }
                });
            }
            if (this.seekProcessed) {
                ExoPlayerImpl.invokeAll(this.listenerSnapshot, new BasePlayer.ListenerInvocation() { // from class: com.google.android.exoplayer2.ExoPlayerImpl$PlaybackInfoUpdate$$ExternalSyntheticLambda7
                    @Override // com.google.android.exoplayer2.BasePlayer.ListenerInvocation
                    public final void invokeListener(Player.EventListener eventListener) {
                        eventListener.onSeekProcessed();
                    }
                });
            }
        }

        /* renamed from: lambda$run$0$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate, reason: not valid java name */
        /* synthetic */ void m10xd48a2375(Player.EventListener eventListener) {
            eventListener.onTimelineChanged(this.playbackInfo.timeline, this.timelineChangeReason);
        }

        /* renamed from: lambda$run$1$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate, reason: not valid java name */
        /* synthetic */ void m11x52eb2754(Player.EventListener eventListener) {
            eventListener.onPositionDiscontinuity(this.positionDiscontinuityReason);
        }

        /* renamed from: lambda$run$2$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate, reason: not valid java name */
        /* synthetic */ void m12xd14c2b33(Player.EventListener eventListener) {
            eventListener.onPlayerError(this.playbackInfo.playbackError);
        }

        /* renamed from: lambda$run$3$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate, reason: not valid java name */
        /* synthetic */ void m13x4fad2f12(Player.EventListener eventListener) {
            eventListener.onTracksChanged(this.playbackInfo.trackGroups, this.playbackInfo.trackSelectorResult.selections);
        }

        /* renamed from: lambda$run$4$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate, reason: not valid java name */
        /* synthetic */ void m14xce0e32f1(Player.EventListener eventListener) {
            eventListener.onLoadingChanged(this.playbackInfo.isLoading);
        }

        /* renamed from: lambda$run$5$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate, reason: not valid java name */
        /* synthetic */ void m15x4c6f36d0(Player.EventListener eventListener) {
            eventListener.onPlayerStateChanged(this.playWhenReady, this.playbackInfo.playbackState);
        }

        /* renamed from: lambda$run$6$com-google-android-exoplayer2-ExoPlayerImpl$PlaybackInfoUpdate, reason: not valid java name */
        /* synthetic */ void m16xcad03aaf(Player.EventListener eventListener) {
            eventListener.onIsPlayingChanged(this.playbackInfo.playbackState == 3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void invokeAll(CopyOnWriteArrayList<BasePlayer.ListenerHolder> copyOnWriteArrayList, BasePlayer.ListenerInvocation listenerInvocation) {
        Iterator<BasePlayer.ListenerHolder> it = copyOnWriteArrayList.iterator();
        while (it.hasNext()) {
            it.next().invoke(listenerInvocation);
        }
    }
}