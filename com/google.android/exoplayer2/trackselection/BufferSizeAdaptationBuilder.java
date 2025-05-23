package com.google.android.exoplayer2.trackselection;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.trackselection.BufferSizeAdaptationBuilder;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionUtil;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import java.util.List;

/* loaded from: classes.dex */
public final class BufferSizeAdaptationBuilder {
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;
    public static final int DEFAULT_HYSTERESIS_BUFFER_MS = 5000;
    public static final int DEFAULT_MAX_BUFFER_MS = 50000;
    public static final int DEFAULT_MIN_BUFFER_MS = 15000;
    public static final float DEFAULT_START_UP_BANDWIDTH_FRACTION = 0.7f;
    public static final int DEFAULT_START_UP_MIN_BUFFER_FOR_QUALITY_INCREASE_MS = 10000;
    private DefaultAllocator allocator;
    private boolean buildCalled;
    private Clock clock = Clock.DEFAULT;
    private int minBufferMs = 15000;
    private int maxBufferMs = 50000;
    private int bufferForPlaybackMs = 2500;
    private int bufferForPlaybackAfterRebufferMs = 5000;
    private int hysteresisBufferMs = 5000;
    private float startUpBandwidthFraction = 0.7f;
    private int startUpMinBufferForQualityIncreaseMs = 10000;
    private DynamicFormatFilter dynamicFormatFilter = DynamicFormatFilter.NO_FILTER;

    public interface DynamicFormatFilter {
        public static final DynamicFormatFilter NO_FILTER = new DynamicFormatFilter() { // from class: com.google.android.exoplayer2.trackselection.BufferSizeAdaptationBuilder$DynamicFormatFilter$$ExternalSyntheticLambda0
            @Override // com.google.android.exoplayer2.trackselection.BufferSizeAdaptationBuilder.DynamicFormatFilter
            public final boolean isFormatAllowed(Format format, int i, boolean z) {
                return BufferSizeAdaptationBuilder.DynamicFormatFilter.lambda$static$0(format, i, z);
            }
        };

        static /* synthetic */ boolean lambda$static$0(Format format, int i, boolean z) {
            return true;
        }

        boolean isFormatAllowed(Format format, int i, boolean z);
    }

    public BufferSizeAdaptationBuilder setClock(Clock clock) {
        Assertions.checkState(!this.buildCalled);
        this.clock = clock;
        return this;
    }

    public BufferSizeAdaptationBuilder setAllocator(DefaultAllocator defaultAllocator) {
        Assertions.checkState(!this.buildCalled);
        this.allocator = defaultAllocator;
        return this;
    }

    public BufferSizeAdaptationBuilder setBufferDurationsMs(int i, int i2, int i3, int i4) {
        Assertions.checkState(!this.buildCalled);
        this.minBufferMs = i;
        this.maxBufferMs = i2;
        this.bufferForPlaybackMs = i3;
        this.bufferForPlaybackAfterRebufferMs = i4;
        return this;
    }

    public BufferSizeAdaptationBuilder setHysteresisBufferMs(int i) {
        Assertions.checkState(!this.buildCalled);
        this.hysteresisBufferMs = i;
        return this;
    }

    public BufferSizeAdaptationBuilder setStartUpTrackSelectionParameters(float f, int i) {
        Assertions.checkState(!this.buildCalled);
        this.startUpBandwidthFraction = f;
        this.startUpMinBufferForQualityIncreaseMs = i;
        return this;
    }

    public BufferSizeAdaptationBuilder setDynamicFormatFilter(DynamicFormatFilter dynamicFormatFilter) {
        Assertions.checkState(!this.buildCalled);
        this.dynamicFormatFilter = dynamicFormatFilter;
        return this;
    }

    public Pair<TrackSelection.Factory, LoadControl> buildPlayerComponents() {
        Assertions.checkArgument(this.hysteresisBufferMs < this.maxBufferMs - this.minBufferMs);
        Assertions.checkState(!this.buildCalled);
        this.buildCalled = true;
        DefaultLoadControl.Builder targetBufferBytes = new DefaultLoadControl.Builder().setTargetBufferBytes(Integer.MAX_VALUE);
        int i = this.maxBufferMs;
        DefaultLoadControl.Builder bufferDurationsMs = targetBufferBytes.setBufferDurationsMs(i, i, this.bufferForPlaybackMs, this.bufferForPlaybackAfterRebufferMs);
        DefaultAllocator defaultAllocator = this.allocator;
        if (defaultAllocator != null) {
            bufferDurationsMs.setAllocator(defaultAllocator);
        }
        return Pair.create(new AnonymousClass1(), bufferDurationsMs.createDefaultLoadControl());
    }

    /* renamed from: com.google.android.exoplayer2.trackselection.BufferSizeAdaptationBuilder$1, reason: invalid class name */
    class AnonymousClass1 implements TrackSelection.Factory {
        AnonymousClass1() {
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection.Factory
        public TrackSelection[] createTrackSelections(TrackSelection.Definition[] definitionArr, final BandwidthMeter bandwidthMeter) {
            return TrackSelectionUtil.createTrackSelectionsForDefinitions(definitionArr, new TrackSelectionUtil.AdaptiveTrackSelectionFactory() { // from class: com.google.android.exoplayer2.trackselection.BufferSizeAdaptationBuilder$1$$ExternalSyntheticLambda0
                @Override // com.google.android.exoplayer2.trackselection.TrackSelectionUtil.AdaptiveTrackSelectionFactory
                public final TrackSelection createAdaptiveTrackSelection(TrackSelection.Definition definition) {
                    return BufferSizeAdaptationBuilder.AnonymousClass1.this.m50x55bdf059(bandwidthMeter, definition);
                }
            });
        }

        /* renamed from: lambda$createTrackSelections$0$com-google-android-exoplayer2-trackselection-BufferSizeAdaptationBuilder$1, reason: not valid java name */
        /* synthetic */ TrackSelection m50x55bdf059(BandwidthMeter bandwidthMeter, TrackSelection.Definition definition) {
            return new BufferSizeAdaptiveTrackSelection(definition.group, definition.tracks, bandwidthMeter, BufferSizeAdaptationBuilder.this.minBufferMs, BufferSizeAdaptationBuilder.this.maxBufferMs, BufferSizeAdaptationBuilder.this.hysteresisBufferMs, BufferSizeAdaptationBuilder.this.startUpBandwidthFraction, BufferSizeAdaptationBuilder.this.startUpMinBufferForQualityIncreaseMs, BufferSizeAdaptationBuilder.this.dynamicFormatFilter, BufferSizeAdaptationBuilder.this.clock, null);
        }
    }

    private static final class BufferSizeAdaptiveTrackSelection extends BaseTrackSelection {
        private static final int BITRATE_BLACKLISTED = -1;
        private final BandwidthMeter bandwidthMeter;
        private final double bitrateToBufferFunctionIntercept;
        private final double bitrateToBufferFunctionSlope;
        private final Clock clock;
        private final DynamicFormatFilter dynamicFormatFilter;
        private final int[] formatBitrates;
        private final long hysteresisBufferUs;
        private boolean isInSteadyState;
        private final int maxBitrate;
        private final long maxBufferUs;
        private final int minBitrate;
        private final long minBufferUs;
        private float playbackSpeed;
        private int selectedIndex;
        private int selectionReason;
        private final float startUpBandwidthFraction;
        private final long startUpMinBufferForQualityIncreaseUs;

        private static long getCurrentPeriodBufferedDurationUs(long j, long j2) {
            return j >= 0 ? j2 : j2 + j;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public Object getSelectionData() {
            return null;
        }

        /* synthetic */ BufferSizeAdaptiveTrackSelection(TrackGroup trackGroup, int[] iArr, BandwidthMeter bandwidthMeter, int i, int i2, int i3, float f, int i4, DynamicFormatFilter dynamicFormatFilter, Clock clock, AnonymousClass1 anonymousClass1) {
            this(trackGroup, iArr, bandwidthMeter, i, i2, i3, f, i4, dynamicFormatFilter, clock);
        }

        private BufferSizeAdaptiveTrackSelection(TrackGroup trackGroup, int[] iArr, BandwidthMeter bandwidthMeter, int i, int i2, int i3, float f, int i4, DynamicFormatFilter dynamicFormatFilter, Clock clock) {
            super(trackGroup, iArr);
            this.bandwidthMeter = bandwidthMeter;
            long msToUs = C.msToUs(i);
            this.minBufferUs = msToUs;
            this.maxBufferUs = C.msToUs(i2);
            this.hysteresisBufferUs = C.msToUs(i3);
            this.startUpBandwidthFraction = f;
            this.startUpMinBufferForQualityIncreaseUs = C.msToUs(i4);
            this.dynamicFormatFilter = dynamicFormatFilter;
            this.clock = clock;
            this.formatBitrates = new int[this.length];
            int i5 = getFormat(0).bitrate;
            this.maxBitrate = i5;
            int i6 = getFormat(this.length - 1).bitrate;
            this.minBitrate = i6;
            this.selectionReason = 0;
            this.playbackSpeed = 1.0f;
            double log = ((r3 - r5) - msToUs) / Math.log(i5 / i6);
            this.bitrateToBufferFunctionSlope = log;
            this.bitrateToBufferFunctionIntercept = msToUs - (log * Math.log(i6));
        }

        @Override // com.google.android.exoplayer2.trackselection.BaseTrackSelection, com.google.android.exoplayer2.trackselection.TrackSelection
        public void onPlaybackSpeed(float f) {
            this.playbackSpeed = f;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public void onDiscontinuity() {
            this.isInSteadyState = false;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public int getSelectedIndex() {
            return this.selectedIndex;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public int getSelectionReason() {
            return this.selectionReason;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public void updateSelectedTrack(long j, long j2, long j3, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIteratorArr) {
            updateFormatBitrates(this.clock.elapsedRealtime());
            if (this.selectionReason == 0) {
                this.selectionReason = 1;
                this.selectedIndex = selectIdealIndexUsingBandwidth(true);
                return;
            }
            long currentPeriodBufferedDurationUs = getCurrentPeriodBufferedDurationUs(j, j2);
            int i = this.selectedIndex;
            if (this.isInSteadyState) {
                selectIndexSteadyState(currentPeriodBufferedDurationUs);
            } else {
                selectIndexStartUpPhase(currentPeriodBufferedDurationUs);
            }
            if (this.selectedIndex != i) {
                this.selectionReason = 3;
            }
        }

        private void selectIndexSteadyState(long j) {
            if (isOutsideHysteresis(j)) {
                this.selectedIndex = selectIdealIndexUsingBufferSize(j);
            }
        }

        private boolean isOutsideHysteresis(long j) {
            int i = this.formatBitrates[this.selectedIndex];
            return i == -1 || Math.abs(j - getTargetBufferForBitrateUs(i)) > this.hysteresisBufferUs;
        }

        private int selectIdealIndexUsingBufferSize(long j) {
            int i = 0;
            int i2 = 0;
            while (true) {
                int[] iArr = this.formatBitrates;
                if (i >= iArr.length) {
                    return i2;
                }
                int i3 = iArr[i];
                if (i3 != -1) {
                    if (getTargetBufferForBitrateUs(i3) <= j && this.dynamicFormatFilter.isFormatAllowed(getFormat(i), this.formatBitrates[i], false)) {
                        return i;
                    }
                    i2 = i;
                }
                i++;
            }
        }

        private void selectIndexStartUpPhase(long j) {
            int selectIdealIndexUsingBandwidth = selectIdealIndexUsingBandwidth(false);
            int selectIdealIndexUsingBufferSize = selectIdealIndexUsingBufferSize(j);
            int i = this.selectedIndex;
            if (selectIdealIndexUsingBufferSize <= i) {
                this.selectedIndex = selectIdealIndexUsingBufferSize;
                this.isInSteadyState = true;
            } else if (j >= this.startUpMinBufferForQualityIncreaseUs || selectIdealIndexUsingBandwidth >= i || this.formatBitrates[i] == -1) {
                this.selectedIndex = selectIdealIndexUsingBandwidth;
            }
        }

        private int selectIdealIndexUsingBandwidth(boolean z) {
            long bitrateEstimate = (long) (this.bandwidthMeter.getBitrateEstimate() * this.startUpBandwidthFraction);
            int i = 0;
            int i2 = 0;
            while (true) {
                int[] iArr = this.formatBitrates;
                if (i >= iArr.length) {
                    return i2;
                }
                if (iArr[i] != -1) {
                    if (Math.round(r4 * this.playbackSpeed) <= bitrateEstimate && this.dynamicFormatFilter.isFormatAllowed(getFormat(i), this.formatBitrates[i], z)) {
                        return i;
                    }
                    i2 = i;
                }
                i++;
            }
        }

        private void updateFormatBitrates(long j) {
            for (int i = 0; i < this.length; i++) {
                if (j == Long.MIN_VALUE || !isBlacklisted(i, j)) {
                    this.formatBitrates[i] = getFormat(i).bitrate;
                } else {
                    this.formatBitrates[i] = -1;
                }
            }
        }

        private long getTargetBufferForBitrateUs(int i) {
            if (i <= this.minBitrate) {
                return this.minBufferUs;
            }
            if (i >= this.maxBitrate) {
                return this.maxBufferUs - this.hysteresisBufferUs;
            }
            return (int) ((this.bitrateToBufferFunctionSlope * Math.log(i)) + this.bitrateToBufferFunctionIntercept);
        }
    }
}