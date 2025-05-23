package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Assertions;

/* loaded from: classes.dex */
public final class SinglePeriodTimeline extends Timeline {
    private static final Object UID = new Object();
    private final boolean isDynamic;
    private final boolean isLive;
    private final boolean isSeekable;
    private final Object manifest;
    private final long periodDurationUs;
    private final long presentationStartTimeMs;
    private final Object tag;
    private final long windowDefaultStartPositionUs;
    private final long windowDurationUs;
    private final long windowPositionInPeriodUs;
    private final long windowStartTimeMs;

    @Override // com.google.android.exoplayer2.Timeline
    public int getPeriodCount() {
        return 1;
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getWindowCount() {
        return 1;
    }

    public SinglePeriodTimeline(long j, boolean z, boolean z2, boolean z3) {
        this(j, z, z2, z3, null, null);
    }

    public SinglePeriodTimeline(long j, boolean z, boolean z2, boolean z3, Object obj, Object obj2) {
        this(j, j, 0L, 0L, z, z2, z3, obj, obj2);
    }

    public SinglePeriodTimeline(long j, long j2, long j3, long j4, boolean z, boolean z2, boolean z3, Object obj, Object obj2) {
        this(C.TIME_UNSET, C.TIME_UNSET, j, j2, j3, j4, z, z2, z3, obj, obj2);
    }

    public SinglePeriodTimeline(long j, long j2, long j3, long j4, long j5, long j6, boolean z, boolean z2, boolean z3, Object obj, Object obj2) {
        this.presentationStartTimeMs = j;
        this.windowStartTimeMs = j2;
        this.periodDurationUs = j3;
        this.windowDurationUs = j4;
        this.windowPositionInPeriodUs = j5;
        this.windowDefaultStartPositionUs = j6;
        this.isSeekable = z;
        this.isDynamic = z2;
        this.isLive = z3;
        this.manifest = obj;
        this.tag = obj2;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0027, code lost:
    
        if (r1 > r3) goto L8;
     */
    @Override // com.google.android.exoplayer2.Timeline
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.google.android.exoplayer2.Timeline.Window getWindow(int r27, com.google.android.exoplayer2.Timeline.Window r28, long r29) {
        /*
            r26 = this;
            r0 = r26
            r1 = 0
            r2 = 1
            r3 = r27
            com.google.android.exoplayer2.util.Assertions.checkIndex(r3, r1, r2)
            long r1 = r0.windowDefaultStartPositionUs
            boolean r3 = r0.isDynamic
            if (r3 == 0) goto L2a
            r3 = 0
            int r3 = (r29 > r3 ? 1 : (r29 == r3 ? 0 : -1))
            if (r3 == 0) goto L2a
            long r3 = r0.windowDurationUs
            r5 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 != 0) goto L23
        L20:
            r18 = r5
            goto L2c
        L23:
            long r1 = r1 + r29
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 <= 0) goto L2a
            goto L20
        L2a:
            r18 = r1
        L2c:
            java.lang.Object r8 = com.google.android.exoplayer2.Timeline.Window.SINGLE_WINDOW_UID
            java.lang.Object r9 = r0.tag
            java.lang.Object r10 = r0.manifest
            long r11 = r0.presentationStartTimeMs
            long r13 = r0.windowStartTimeMs
            boolean r15 = r0.isSeekable
            boolean r1 = r0.isDynamic
            r16 = r1
            boolean r1 = r0.isLive
            r17 = r1
            long r1 = r0.windowDurationUs
            r20 = r1
            r22 = 0
            r23 = 0
            long r1 = r0.windowPositionInPeriodUs
            r24 = r1
            r7 = r28
            com.google.android.exoplayer2.Timeline$Window r1 = r7.set(r8, r9, r10, r11, r13, r15, r16, r17, r18, r20, r22, r23, r24)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.SinglePeriodTimeline.getWindow(int, com.google.android.exoplayer2.Timeline$Window, long):com.google.android.exoplayer2.Timeline$Window");
    }

    @Override // com.google.android.exoplayer2.Timeline
    public Timeline.Period getPeriod(int i, Timeline.Period period, boolean z) {
        Assertions.checkIndex(i, 0, 1);
        return period.set(null, z ? UID : null, 0, this.periodDurationUs, -this.windowPositionInPeriodUs);
    }

    @Override // com.google.android.exoplayer2.Timeline
    public int getIndexOfPeriod(Object obj) {
        return UID.equals(obj) ? 0 : -1;
    }

    @Override // com.google.android.exoplayer2.Timeline
    public Object getUidOfPeriod(int i) {
        Assertions.checkIndex(i, 0, 1);
        return UID;
    }
}