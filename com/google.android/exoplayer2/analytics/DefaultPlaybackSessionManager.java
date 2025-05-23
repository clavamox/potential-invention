package com.google.android.exoplayer2.analytics;

import android.util.Base64;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.analytics.PlaybackSessionManager;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

/* loaded from: classes.dex */
public final class DefaultPlaybackSessionManager implements PlaybackSessionManager {
    private static final Random RANDOM = new Random();
    private static final int SESSION_ID_LENGTH = 12;
    private String activeSessionId;
    private MediaSource.MediaPeriodId currentMediaPeriodId;
    private PlaybackSessionManager.Listener listener;
    private final Timeline.Window window = new Timeline.Window();
    private final Timeline.Period period = new Timeline.Period();
    private final HashMap<String, SessionDescriptor> sessions = new HashMap<>();
    private Timeline currentTimeline = Timeline.EMPTY;

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    public void setListener(PlaybackSessionManager.Listener listener) {
        this.listener = listener;
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    public synchronized String getSessionForMediaPeriodId(Timeline timeline, MediaSource.MediaPeriodId mediaPeriodId) {
        return getOrAddSession(timeline.getPeriodByUid(mediaPeriodId.periodUid, this.period).windowIndex, mediaPeriodId).sessionId;
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    public synchronized boolean belongsToSession(AnalyticsListener.EventTime eventTime, String str) {
        SessionDescriptor sessionDescriptor = this.sessions.get(str);
        if (sessionDescriptor == null) {
            return false;
        }
        sessionDescriptor.maybeSetWindowSequenceNumber(eventTime.windowIndex, eventTime.mediaPeriodId);
        return sessionDescriptor.belongsToSession(eventTime.windowIndex, eventTime.mediaPeriodId);
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    public synchronized void updateSessions(AnalyticsListener.EventTime eventTime) {
        if (!((eventTime.mediaPeriodId == null || this.currentMediaPeriodId == null || eventTime.mediaPeriodId.windowSequenceNumber >= this.currentMediaPeriodId.windowSequenceNumber) ? false : true)) {
            SessionDescriptor orAddSession = getOrAddSession(eventTime.windowIndex, eventTime.mediaPeriodId);
            if (!orAddSession.isCreated) {
                orAddSession.isCreated = true;
                ((PlaybackSessionManager.Listener) Assertions.checkNotNull(this.listener)).onSessionCreated(eventTime, orAddSession.sessionId);
                if (this.activeSessionId == null) {
                    updateActiveSession(eventTime, orAddSession);
                }
            }
        }
    }

    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    public synchronized void handleTimelineUpdate(AnalyticsListener.EventTime eventTime) {
        Assertions.checkNotNull(this.listener);
        Timeline timeline = this.currentTimeline;
        this.currentTimeline = eventTime.timeline;
        Iterator<SessionDescriptor> it = this.sessions.values().iterator();
        while (it.hasNext()) {
            SessionDescriptor next = it.next();
            if (!next.tryResolvingToNewTimeline(timeline, this.currentTimeline)) {
                it.remove();
                if (next.isCreated) {
                    if (next.sessionId.equals(this.activeSessionId)) {
                        this.activeSessionId = null;
                    }
                    this.listener.onSessionFinished(eventTime, next.sessionId, false);
                }
            }
        }
        handlePositionDiscontinuity(eventTime, 4);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0021 A[Catch: all -> 0x00bd, TryCatch #0 {, blocks: (B:3:0x0001, B:9:0x0011, B:10:0x001b, B:12:0x0021, B:15:0x002d, B:18:0x0036, B:23:0x0049, B:24:0x004c, B:31:0x0056, B:33:0x0062, B:35:0x006a, B:37:0x006e, B:39:0x0078, B:41:0x0082, B:43:0x008c, B:45:0x00a5, B:47:0x00ab, B:48:0x00b8), top: B:2:0x0001 }] */
    @Override // com.google.android.exoplayer2.analytics.PlaybackSessionManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized void handlePositionDiscontinuity(com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime r7, int r8) {
        /*
            r6 = this;
            monitor-enter(r6)
            com.google.android.exoplayer2.analytics.PlaybackSessionManager$Listener r0 = r6.listener     // Catch: java.lang.Throwable -> Lbd
            com.google.android.exoplayer2.util.Assertions.checkNotNull(r0)     // Catch: java.lang.Throwable -> Lbd
            r0 = 0
            r1 = 1
            if (r8 == 0) goto L10
            r2 = 3
            if (r8 != r2) goto Le
            goto L10
        Le:
            r8 = r0
            goto L11
        L10:
            r8 = r1
        L11:
            java.util.HashMap<java.lang.String, com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager$SessionDescriptor> r2 = r6.sessions     // Catch: java.lang.Throwable -> Lbd
            java.util.Collection r2 = r2.values()     // Catch: java.lang.Throwable -> Lbd
            java.util.Iterator r2 = r2.iterator()     // Catch: java.lang.Throwable -> Lbd
        L1b:
            boolean r3 = r2.hasNext()     // Catch: java.lang.Throwable -> Lbd
            if (r3 == 0) goto L56
            java.lang.Object r3 = r2.next()     // Catch: java.lang.Throwable -> Lbd
            com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager$SessionDescriptor r3 = (com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor) r3     // Catch: java.lang.Throwable -> Lbd
            boolean r4 = r3.isFinishedAtEventTime(r7)     // Catch: java.lang.Throwable -> Lbd
            if (r4 == 0) goto L1b
            r2.remove()     // Catch: java.lang.Throwable -> Lbd
            boolean r4 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$100(r3)     // Catch: java.lang.Throwable -> Lbd
            if (r4 == 0) goto L1b
            java.lang.String r4 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$000(r3)     // Catch: java.lang.Throwable -> Lbd
            java.lang.String r5 = r6.activeSessionId     // Catch: java.lang.Throwable -> Lbd
            boolean r4 = r4.equals(r5)     // Catch: java.lang.Throwable -> Lbd
            if (r8 == 0) goto L46
            if (r4 == 0) goto L46
            r5 = r1
            goto L47
        L46:
            r5 = r0
        L47:
            if (r4 == 0) goto L4c
            r4 = 0
            r6.activeSessionId = r4     // Catch: java.lang.Throwable -> Lbd
        L4c:
            com.google.android.exoplayer2.analytics.PlaybackSessionManager$Listener r4 = r6.listener     // Catch: java.lang.Throwable -> Lbd
            java.lang.String r3 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$000(r3)     // Catch: java.lang.Throwable -> Lbd
            r4.onSessionFinished(r7, r3, r5)     // Catch: java.lang.Throwable -> Lbd
            goto L1b
        L56:
            int r8 = r7.windowIndex     // Catch: java.lang.Throwable -> Lbd
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r0 = r7.mediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager$SessionDescriptor r8 = r6.getOrAddSession(r8, r0)     // Catch: java.lang.Throwable -> Lbd
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r0 = r7.mediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            if (r0 == 0) goto Lb8
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r0 = r7.mediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            boolean r0 = r0.isAd()     // Catch: java.lang.Throwable -> Lbd
            if (r0 == 0) goto Lb8
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r0 = r6.currentMediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            if (r0 == 0) goto L8c
            long r0 = r0.windowSequenceNumber     // Catch: java.lang.Throwable -> Lbd
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r2 = r7.mediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            long r2 = r2.windowSequenceNumber     // Catch: java.lang.Throwable -> Lbd
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L8c
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r0 = r6.currentMediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            int r0 = r0.adGroupIndex     // Catch: java.lang.Throwable -> Lbd
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r1 = r7.mediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            int r1 = r1.adGroupIndex     // Catch: java.lang.Throwable -> Lbd
            if (r0 != r1) goto L8c
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r0 = r6.currentMediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            int r0 = r0.adIndexInAdGroup     // Catch: java.lang.Throwable -> Lbd
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r1 = r7.mediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            int r1 = r1.adIndexInAdGroup     // Catch: java.lang.Throwable -> Lbd
            if (r0 == r1) goto Lb8
        L8c:
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r0 = new com.google.android.exoplayer2.source.MediaSource$MediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r1 = r7.mediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            java.lang.Object r1 = r1.periodUid     // Catch: java.lang.Throwable -> Lbd
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r2 = r7.mediaPeriodId     // Catch: java.lang.Throwable -> Lbd
            long r2 = r2.windowSequenceNumber     // Catch: java.lang.Throwable -> Lbd
            r0.<init>(r1, r2)     // Catch: java.lang.Throwable -> Lbd
            int r1 = r7.windowIndex     // Catch: java.lang.Throwable -> Lbd
            com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager$SessionDescriptor r0 = r6.getOrAddSession(r1, r0)     // Catch: java.lang.Throwable -> Lbd
            boolean r1 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$100(r0)     // Catch: java.lang.Throwable -> Lbd
            if (r1 == 0) goto Lb8
            boolean r1 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$100(r8)     // Catch: java.lang.Throwable -> Lbd
            if (r1 == 0) goto Lb8
            com.google.android.exoplayer2.analytics.PlaybackSessionManager$Listener r1 = r6.listener     // Catch: java.lang.Throwable -> Lbd
            java.lang.String r0 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$000(r0)     // Catch: java.lang.Throwable -> Lbd
            java.lang.String r2 = com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.SessionDescriptor.access$000(r8)     // Catch: java.lang.Throwable -> Lbd
            r1.onAdPlaybackStarted(r7, r0, r2)     // Catch: java.lang.Throwable -> Lbd
        Lb8:
            r6.updateActiveSession(r7, r8)     // Catch: java.lang.Throwable -> Lbd
            monitor-exit(r6)
            return
        Lbd:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.analytics.DefaultPlaybackSessionManager.handlePositionDiscontinuity(com.google.android.exoplayer2.analytics.AnalyticsListener$EventTime, int):void");
    }

    private SessionDescriptor getOrAddSession(int i, MediaSource.MediaPeriodId mediaPeriodId) {
        SessionDescriptor sessionDescriptor = null;
        long j = Long.MAX_VALUE;
        for (SessionDescriptor sessionDescriptor2 : this.sessions.values()) {
            sessionDescriptor2.maybeSetWindowSequenceNumber(i, mediaPeriodId);
            if (sessionDescriptor2.belongsToSession(i, mediaPeriodId)) {
                long j2 = sessionDescriptor2.windowSequenceNumber;
                if (j2 == -1 || j2 < j) {
                    sessionDescriptor = sessionDescriptor2;
                    j = j2;
                } else if (j2 == j && ((SessionDescriptor) Util.castNonNull(sessionDescriptor)).adMediaPeriodId != null && sessionDescriptor2.adMediaPeriodId != null) {
                    sessionDescriptor = sessionDescriptor2;
                }
            }
        }
        if (sessionDescriptor != null) {
            return sessionDescriptor;
        }
        String generateSessionId = generateSessionId();
        SessionDescriptor sessionDescriptor3 = new SessionDescriptor(generateSessionId, i, mediaPeriodId);
        this.sessions.put(generateSessionId, sessionDescriptor3);
        return sessionDescriptor3;
    }

    @RequiresNonNull({"listener"})
    private void updateActiveSession(AnalyticsListener.EventTime eventTime, SessionDescriptor sessionDescriptor) {
        this.currentMediaPeriodId = eventTime.mediaPeriodId;
        if (sessionDescriptor.isCreated) {
            this.activeSessionId = sessionDescriptor.sessionId;
            if (sessionDescriptor.isActive) {
                return;
            }
            sessionDescriptor.isActive = true;
            this.listener.onSessionActive(eventTime, sessionDescriptor.sessionId);
        }
    }

    private static String generateSessionId() {
        byte[] bArr = new byte[12];
        RANDOM.nextBytes(bArr);
        return Base64.encodeToString(bArr, 10);
    }

    private final class SessionDescriptor {
        private MediaSource.MediaPeriodId adMediaPeriodId;
        private boolean isActive;
        private boolean isCreated;
        private final String sessionId;
        private int windowIndex;
        private long windowSequenceNumber;

        public SessionDescriptor(String str, int i, MediaSource.MediaPeriodId mediaPeriodId) {
            this.sessionId = str;
            this.windowIndex = i;
            this.windowSequenceNumber = mediaPeriodId == null ? -1L : mediaPeriodId.windowSequenceNumber;
            if (mediaPeriodId == null || !mediaPeriodId.isAd()) {
                return;
            }
            this.adMediaPeriodId = mediaPeriodId;
        }

        public boolean tryResolvingToNewTimeline(Timeline timeline, Timeline timeline2) {
            int resolveWindowIndexToNewTimeline = resolveWindowIndexToNewTimeline(timeline, timeline2, this.windowIndex);
            this.windowIndex = resolveWindowIndexToNewTimeline;
            if (resolveWindowIndexToNewTimeline == -1) {
                return false;
            }
            MediaSource.MediaPeriodId mediaPeriodId = this.adMediaPeriodId;
            return mediaPeriodId == null || timeline2.getIndexOfPeriod(mediaPeriodId.periodUid) != -1;
        }

        public boolean belongsToSession(int i, MediaSource.MediaPeriodId mediaPeriodId) {
            return mediaPeriodId == null ? i == this.windowIndex : this.adMediaPeriodId == null ? !mediaPeriodId.isAd() && mediaPeriodId.windowSequenceNumber == this.windowSequenceNumber : mediaPeriodId.windowSequenceNumber == this.adMediaPeriodId.windowSequenceNumber && mediaPeriodId.adGroupIndex == this.adMediaPeriodId.adGroupIndex && mediaPeriodId.adIndexInAdGroup == this.adMediaPeriodId.adIndexInAdGroup;
        }

        public void maybeSetWindowSequenceNumber(int i, MediaSource.MediaPeriodId mediaPeriodId) {
            if (this.windowSequenceNumber != -1 || i != this.windowIndex || mediaPeriodId == null || mediaPeriodId.isAd()) {
                return;
            }
            this.windowSequenceNumber = mediaPeriodId.windowSequenceNumber;
        }

        public boolean isFinishedAtEventTime(AnalyticsListener.EventTime eventTime) {
            if (this.windowSequenceNumber == -1) {
                return false;
            }
            if (eventTime.mediaPeriodId == null) {
                return this.windowIndex != eventTime.windowIndex;
            }
            if (eventTime.mediaPeriodId.windowSequenceNumber > this.windowSequenceNumber) {
                return true;
            }
            if (this.adMediaPeriodId == null) {
                return false;
            }
            int indexOfPeriod = eventTime.timeline.getIndexOfPeriod(eventTime.mediaPeriodId.periodUid);
            int indexOfPeriod2 = eventTime.timeline.getIndexOfPeriod(this.adMediaPeriodId.periodUid);
            if (eventTime.mediaPeriodId.windowSequenceNumber < this.adMediaPeriodId.windowSequenceNumber || indexOfPeriod < indexOfPeriod2) {
                return false;
            }
            if (indexOfPeriod > indexOfPeriod2) {
                return true;
            }
            if (!eventTime.mediaPeriodId.isAd()) {
                return eventTime.mediaPeriodId.nextAdGroupIndex == -1 || eventTime.mediaPeriodId.nextAdGroupIndex > this.adMediaPeriodId.adGroupIndex;
            }
            int i = eventTime.mediaPeriodId.adGroupIndex;
            return i > this.adMediaPeriodId.adGroupIndex || (i == this.adMediaPeriodId.adGroupIndex && eventTime.mediaPeriodId.adIndexInAdGroup > this.adMediaPeriodId.adIndexInAdGroup);
        }

        private int resolveWindowIndexToNewTimeline(Timeline timeline, Timeline timeline2, int i) {
            if (i < timeline.getWindowCount()) {
                timeline.getWindow(i, DefaultPlaybackSessionManager.this.window);
                for (int i2 = DefaultPlaybackSessionManager.this.window.firstPeriodIndex; i2 <= DefaultPlaybackSessionManager.this.window.lastPeriodIndex; i2++) {
                    int indexOfPeriod = timeline2.getIndexOfPeriod(timeline.getUidOfPeriod(i2));
                    if (indexOfPeriod != -1) {
                        return timeline2.getPeriod(indexOfPeriod, DefaultPlaybackSessionManager.this.period).windowIndex;
                    }
                }
                return -1;
            }
            if (i < timeline2.getWindowCount()) {
                return i;
            }
            return -1;
        }
    }
}