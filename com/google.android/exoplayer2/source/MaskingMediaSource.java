package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

/* loaded from: classes.dex */
public final class MaskingMediaSource extends CompositeMediaSource<Void> {
    private boolean hasStartedPreparing;
    private boolean isPrepared;
    private final MediaSource mediaSource;
    private MaskingTimeline timeline;
    private MaskingMediaPeriod unpreparedMaskingMediaPeriod;
    private MediaSourceEventListener.EventDispatcher unpreparedMaskingMediaPeriodEventDispatcher;
    private final boolean useLazyPreparation;
    private final Timeline.Window window = new Timeline.Window();
    private final Timeline.Period period = new Timeline.Period();

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.MediaSource
    public void maybeThrowSourceInfoRefreshError() throws IOException {
    }

    public MaskingMediaSource(MediaSource mediaSource, boolean z) {
        this.mediaSource = mediaSource;
        this.useLazyPreparation = z;
        this.timeline = MaskingTimeline.createWithDummyTimeline(mediaSource.getTag());
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    public void prepareSourceInternal(TransferListener transferListener) {
        super.prepareSourceInternal(transferListener);
        if (this.useLazyPreparation) {
            return;
        }
        this.hasStartedPreparing = true;
        prepareChildSource(null, this.mediaSource);
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public Object getTag() {
        return this.mediaSource.getTag();
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public MaskingMediaPeriod createPeriod(MediaSource.MediaPeriodId mediaPeriodId, Allocator allocator, long j) {
        MaskingMediaPeriod maskingMediaPeriod = new MaskingMediaPeriod(this.mediaSource, mediaPeriodId, allocator, j);
        if (this.isPrepared) {
            maskingMediaPeriod.createPeriod(mediaPeriodId.copyWithPeriodUid(getInternalPeriodUid(mediaPeriodId.periodUid)));
        } else {
            this.unpreparedMaskingMediaPeriod = maskingMediaPeriod;
            MediaSourceEventListener.EventDispatcher createEventDispatcher = createEventDispatcher(0, mediaPeriodId, 0L);
            this.unpreparedMaskingMediaPeriodEventDispatcher = createEventDispatcher;
            createEventDispatcher.mediaPeriodCreated();
            if (!this.hasStartedPreparing) {
                this.hasStartedPreparing = true;
                prepareChildSource(null, this.mediaSource);
            }
        }
        return maskingMediaPeriod;
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((MaskingMediaPeriod) mediaPeriod).releasePeriod();
        if (mediaPeriod == this.unpreparedMaskingMediaPeriod) {
            ((MediaSourceEventListener.EventDispatcher) Assertions.checkNotNull(this.unpreparedMaskingMediaPeriodEventDispatcher)).mediaPeriodReleased();
            this.unpreparedMaskingMediaPeriodEventDispatcher = null;
            this.unpreparedMaskingMediaPeriod = null;
        }
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource, com.google.android.exoplayer2.source.BaseMediaSource
    public void releaseSourceInternal() {
        this.isPrepared = false;
        this.hasStartedPreparing = false;
        super.releaseSourceInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:17:0x005d  */
    @Override // com.google.android.exoplayer2.source.CompositeMediaSource
    /* renamed from: onChildSourceInfoRefreshed, reason: avoid collision after fix types in other method and merged with bridge method [inline-methods] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void m33x365769cd(java.lang.Void r10, com.google.android.exoplayer2.source.MediaSource r11, com.google.android.exoplayer2.Timeline r12) {
        /*
            r9 = this;
            boolean r10 = r9.isPrepared
            if (r10 == 0) goto Ld
            com.google.android.exoplayer2.source.MaskingMediaSource$MaskingTimeline r10 = r9.timeline
            com.google.android.exoplayer2.source.MaskingMediaSource$MaskingTimeline r10 = r10.cloneWithUpdatedTimeline(r12)
            r9.timeline = r10
            goto L71
        Ld:
            boolean r10 = r12.isEmpty()
            if (r10 == 0) goto L1e
            java.lang.Object r10 = com.google.android.exoplayer2.Timeline.Window.SINGLE_WINDOW_UID
            java.lang.Object r11 = com.google.android.exoplayer2.source.MaskingMediaSource.MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID
            com.google.android.exoplayer2.source.MaskingMediaSource$MaskingTimeline r10 = com.google.android.exoplayer2.source.MaskingMediaSource.MaskingTimeline.createWithRealTimeline(r12, r10, r11)
            r9.timeline = r10
            goto L71
        L1e:
            r10 = 0
            com.google.android.exoplayer2.Timeline$Window r11 = r9.window
            r12.getWindow(r10, r11)
            com.google.android.exoplayer2.Timeline$Window r10 = r9.window
            long r10 = r10.getDefaultPositionUs()
            com.google.android.exoplayer2.source.MaskingMediaPeriod r0 = r9.unpreparedMaskingMediaPeriod
            if (r0 == 0) goto L3a
            long r0 = r0.getPreparePositionUs()
            r2 = 0
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 == 0) goto L3a
            r7 = r0
            goto L3b
        L3a:
            r7 = r10
        L3b:
            com.google.android.exoplayer2.Timeline$Window r10 = r9.window
            java.lang.Object r10 = r10.uid
            com.google.android.exoplayer2.Timeline$Window r4 = r9.window
            com.google.android.exoplayer2.Timeline$Period r5 = r9.period
            r6 = 0
            r3 = r12
            android.util.Pair r11 = r3.getPeriodPosition(r4, r5, r6, r7)
            java.lang.Object r0 = r11.first
            java.lang.Object r11 = r11.second
            java.lang.Long r11 = (java.lang.Long) r11
            long r1 = r11.longValue()
            com.google.android.exoplayer2.source.MaskingMediaSource$MaskingTimeline r10 = com.google.android.exoplayer2.source.MaskingMediaSource.MaskingTimeline.createWithRealTimeline(r12, r10, r0)
            r9.timeline = r10
            com.google.android.exoplayer2.source.MaskingMediaPeriod r10 = r9.unpreparedMaskingMediaPeriod
            if (r10 == 0) goto L71
            r10.overridePreparePositionUs(r1)
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r11 = r10.id
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r12 = r10.id
            java.lang.Object r12 = r12.periodUid
            java.lang.Object r12 = r9.getInternalPeriodUid(r12)
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r11 = r11.copyWithPeriodUid(r12)
            r10.createPeriod(r11)
        L71:
            r10 = 1
            r9.isPrepared = r10
            com.google.android.exoplayer2.source.MaskingMediaSource$MaskingTimeline r10 = r9.timeline
            r9.refreshSourceInfo(r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.MaskingMediaSource.m33x365769cd(java.lang.Void, com.google.android.exoplayer2.source.MediaSource, com.google.android.exoplayer2.Timeline):void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.exoplayer2.source.CompositeMediaSource
    public MediaSource.MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(Void r1, MediaSource.MediaPeriodId mediaPeriodId) {
        return mediaPeriodId.copyWithPeriodUid(getExternalPeriodUid(mediaPeriodId.periodUid));
    }

    @Override // com.google.android.exoplayer2.source.CompositeMediaSource
    protected boolean shouldDispatchCreateOrReleaseEvent(MediaSource.MediaPeriodId mediaPeriodId) {
        MaskingMediaPeriod maskingMediaPeriod = this.unpreparedMaskingMediaPeriod;
        return maskingMediaPeriod == null || !mediaPeriodId.equals(maskingMediaPeriod.id);
    }

    private Object getInternalPeriodUid(Object obj) {
        return obj.equals(MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID) ? this.timeline.replacedInternalPeriodUid : obj;
    }

    private Object getExternalPeriodUid(Object obj) {
        return this.timeline.replacedInternalPeriodUid.equals(obj) ? MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID : obj;
    }

    private static final class MaskingTimeline extends ForwardingTimeline {
        public static final Object DUMMY_EXTERNAL_PERIOD_UID = new Object();
        private final Object replacedInternalPeriodUid;
        private final Object replacedInternalWindowUid;

        public static MaskingTimeline createWithDummyTimeline(Object obj) {
            return new MaskingTimeline(new DummyTimeline(obj), Timeline.Window.SINGLE_WINDOW_UID, DUMMY_EXTERNAL_PERIOD_UID);
        }

        public static MaskingTimeline createWithRealTimeline(Timeline timeline, Object obj, Object obj2) {
            return new MaskingTimeline(timeline, obj, obj2);
        }

        private MaskingTimeline(Timeline timeline, Object obj, Object obj2) {
            super(timeline);
            this.replacedInternalWindowUid = obj;
            this.replacedInternalPeriodUid = obj2;
        }

        public MaskingTimeline cloneWithUpdatedTimeline(Timeline timeline) {
            return new MaskingTimeline(timeline, this.replacedInternalWindowUid, this.replacedInternalPeriodUid);
        }

        public Timeline getTimeline() {
            return this.timeline;
        }

        @Override // com.google.android.exoplayer2.source.ForwardingTimeline, com.google.android.exoplayer2.Timeline
        public Timeline.Window getWindow(int i, Timeline.Window window, long j) {
            this.timeline.getWindow(i, window, j);
            if (Util.areEqual(window.uid, this.replacedInternalWindowUid)) {
                window.uid = Timeline.Window.SINGLE_WINDOW_UID;
            }
            return window;
        }

        @Override // com.google.android.exoplayer2.source.ForwardingTimeline, com.google.android.exoplayer2.Timeline
        public Timeline.Period getPeriod(int i, Timeline.Period period, boolean z) {
            this.timeline.getPeriod(i, period, z);
            if (Util.areEqual(period.uid, this.replacedInternalPeriodUid)) {
                period.uid = DUMMY_EXTERNAL_PERIOD_UID;
            }
            return period;
        }

        @Override // com.google.android.exoplayer2.source.ForwardingTimeline, com.google.android.exoplayer2.Timeline
        public int getIndexOfPeriod(Object obj) {
            Timeline timeline = this.timeline;
            if (DUMMY_EXTERNAL_PERIOD_UID.equals(obj)) {
                obj = this.replacedInternalPeriodUid;
            }
            return timeline.getIndexOfPeriod(obj);
        }

        @Override // com.google.android.exoplayer2.source.ForwardingTimeline, com.google.android.exoplayer2.Timeline
        public Object getUidOfPeriod(int i) {
            Object uidOfPeriod = this.timeline.getUidOfPeriod(i);
            return Util.areEqual(uidOfPeriod, this.replacedInternalPeriodUid) ? DUMMY_EXTERNAL_PERIOD_UID : uidOfPeriod;
        }
    }

    private static final class DummyTimeline extends Timeline {
        private final Object tag;

        @Override // com.google.android.exoplayer2.Timeline
        public int getPeriodCount() {
            return 1;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getWindowCount() {
            return 1;
        }

        public DummyTimeline(Object obj) {
            this.tag = obj;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Timeline.Window getWindow(int i, Timeline.Window window, long j) {
            return window.set(Timeline.Window.SINGLE_WINDOW_UID, this.tag, null, C.TIME_UNSET, C.TIME_UNSET, false, true, false, 0L, C.TIME_UNSET, 0, 0, 0L);
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Timeline.Period getPeriod(int i, Timeline.Period period, boolean z) {
            return period.set(0, MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID, 0, C.TIME_UNSET, 0L);
        }

        @Override // com.google.android.exoplayer2.Timeline
        public int getIndexOfPeriod(Object obj) {
            return obj == MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID ? 0 : -1;
        }

        @Override // com.google.android.exoplayer2.Timeline
        public Object getUidOfPeriod(int i) {
            return MaskingTimeline.DUMMY_EXTERNAL_PERIOD_UID;
        }
    }
}