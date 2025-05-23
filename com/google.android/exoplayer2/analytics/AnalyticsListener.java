package com.google.android.exoplayer2.analytics;

import android.view.Surface;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import java.io.IOException;

/* loaded from: classes.dex */
public interface AnalyticsListener {
    default void onAudioAttributesChanged(EventTime eventTime, AudioAttributes audioAttributes) {
    }

    default void onAudioSessionId(EventTime eventTime, int i) {
    }

    default void onAudioUnderrun(EventTime eventTime, int i, long j, long j2) {
    }

    default void onBandwidthEstimate(EventTime eventTime, int i, long j, long j2) {
    }

    default void onDecoderDisabled(EventTime eventTime, int i, DecoderCounters decoderCounters) {
    }

    default void onDecoderEnabled(EventTime eventTime, int i, DecoderCounters decoderCounters) {
    }

    default void onDecoderInitialized(EventTime eventTime, int i, String str, long j) {
    }

    default void onDecoderInputFormatChanged(EventTime eventTime, int i, Format format) {
    }

    default void onDownstreamFormatChanged(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    default void onDrmKeysLoaded(EventTime eventTime) {
    }

    default void onDrmKeysRemoved(EventTime eventTime) {
    }

    default void onDrmKeysRestored(EventTime eventTime) {
    }

    default void onDrmSessionAcquired(EventTime eventTime) {
    }

    default void onDrmSessionManagerError(EventTime eventTime, Exception exc) {
    }

    default void onDrmSessionReleased(EventTime eventTime) {
    }

    default void onDroppedVideoFrames(EventTime eventTime, int i, long j) {
    }

    default void onIsPlayingChanged(EventTime eventTime, boolean z) {
    }

    default void onLoadCanceled(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    default void onLoadCompleted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    default void onLoadError(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException iOException, boolean z) {
    }

    default void onLoadStarted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    default void onLoadingChanged(EventTime eventTime, boolean z) {
    }

    default void onMediaPeriodCreated(EventTime eventTime) {
    }

    default void onMediaPeriodReleased(EventTime eventTime) {
    }

    default void onMetadata(EventTime eventTime, Metadata metadata) {
    }

    default void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {
    }

    default void onPlaybackSuppressionReasonChanged(EventTime eventTime, int i) {
    }

    default void onPlayerError(EventTime eventTime, ExoPlaybackException exoPlaybackException) {
    }

    default void onPlayerStateChanged(EventTime eventTime, boolean z, int i) {
    }

    default void onPositionDiscontinuity(EventTime eventTime, int i) {
    }

    default void onReadingStarted(EventTime eventTime) {
    }

    default void onRenderedFirstFrame(EventTime eventTime, Surface surface) {
    }

    default void onRepeatModeChanged(EventTime eventTime, int i) {
    }

    default void onSeekProcessed(EventTime eventTime) {
    }

    default void onSeekStarted(EventTime eventTime) {
    }

    default void onShuffleModeChanged(EventTime eventTime, boolean z) {
    }

    default void onSurfaceSizeChanged(EventTime eventTime, int i, int i2) {
    }

    default void onTimelineChanged(EventTime eventTime, int i) {
    }

    default void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
    }

    default void onUpstreamDiscarded(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    default void onVideoSizeChanged(EventTime eventTime, int i, int i2, int i3, float f) {
    }

    default void onVolumeChanged(EventTime eventTime, float f) {
    }

    public static final class EventTime {
        public final long currentPlaybackPositionMs;
        public final long eventPlaybackPositionMs;
        public final MediaSource.MediaPeriodId mediaPeriodId;
        public final long realtimeMs;
        public final Timeline timeline;
        public final long totalBufferedDurationMs;
        public final int windowIndex;

        public EventTime(long j, Timeline timeline, int i, MediaSource.MediaPeriodId mediaPeriodId, long j2, long j3, long j4) {
            this.realtimeMs = j;
            this.timeline = timeline;
            this.windowIndex = i;
            this.mediaPeriodId = mediaPeriodId;
            this.eventPlaybackPositionMs = j2;
            this.currentPlaybackPositionMs = j3;
            this.totalBufferedDurationMs = j4;
        }
    }
}