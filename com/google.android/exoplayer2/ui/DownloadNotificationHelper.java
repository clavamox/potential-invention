package com.google.android.exoplayer2.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import androidx.core.app.NotificationCompat;

/* loaded from: classes.dex */
public final class DownloadNotificationHelper {
    private static final int NULL_STRING_ID = 0;
    private final Context context;
    private final NotificationCompat.Builder notificationBuilder;

    public DownloadNotificationHelper(Context context, String str) {
        Context applicationContext = context.getApplicationContext();
        this.context = applicationContext;
        this.notificationBuilder = new NotificationCompat.Builder(applicationContext, str);
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0057  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0063  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.app.Notification buildProgressNotification(int r20, android.app.PendingIntent r21, java.lang.String r22, java.util.List<com.google.android.exoplayer2.offline.Download> r23) {
        /*
            r19 = this;
            r0 = 0
            r1 = 0
            r2 = 1
            r3 = r1
            r4 = r3
            r5 = r4
            r6 = r5
            r8 = r6
            r7 = r2
        L9:
            int r9 = r23.size()
            if (r3 >= r9) goto L49
            r9 = r23
            java.lang.Object r10 = r9.get(r3)
            com.google.android.exoplayer2.offline.Download r10 = (com.google.android.exoplayer2.offline.Download) r10
            int r11 = r10.state
            r12 = 5
            if (r11 != r12) goto L1e
            r5 = r2
            goto L46
        L1e:
            int r11 = r10.state
            r12 = 7
            if (r11 == r12) goto L29
            int r11 = r10.state
            r12 = 2
            if (r11 == r12) goto L29
            goto L46
        L29:
            float r4 = r10.getPercentDownloaded()
            r11 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r11 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r11 == 0) goto L35
            float r0 = r0 + r4
            r7 = r1
        L35:
            long r10 = r10.getBytesDownloaded()
            r12 = 0
            int r4 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r4 <= 0) goto L41
            r4 = r2
            goto L42
        L41:
            r4 = r1
        L42:
            r8 = r8 | r4
            int r6 = r6 + 1
            r4 = r2
        L46:
            int r3 = r3 + 1
            goto L9
        L49:
            if (r4 == 0) goto L4f
            int r3 = com.google.android.exoplayer2.ui.R.string.exo_download_downloading
        L4d:
            r13 = r3
            goto L55
        L4f:
            if (r5 == 0) goto L54
            int r3 = com.google.android.exoplayer2.ui.R.string.exo_download_removing
            goto L4d
        L54:
            r13 = r1
        L55:
            if (r4 == 0) goto L63
            float r3 = (float) r6
            float r0 = r0 / r3
            int r0 = (int) r0
            if (r7 == 0) goto L5f
            if (r8 == 0) goto L5f
            r1 = r2
        L5f:
            r15 = r0
            r16 = r1
            goto L66
        L63:
            r15 = r1
            r16 = r2
        L66:
            r14 = 100
            r17 = 1
            r18 = 0
            r9 = r19
            r10 = r20
            r11 = r21
            r12 = r22
            android.app.Notification r0 = r9.buildNotification(r10, r11, r12, r13, r14, r15, r16, r17, r18)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ui.DownloadNotificationHelper.buildProgressNotification(int, android.app.PendingIntent, java.lang.String, java.util.List):android.app.Notification");
    }

    public Notification buildDownloadCompletedNotification(int i, PendingIntent pendingIntent, String str) {
        return buildEndStateNotification(i, pendingIntent, str, R.string.exo_download_completed);
    }

    public Notification buildDownloadFailedNotification(int i, PendingIntent pendingIntent, String str) {
        return buildEndStateNotification(i, pendingIntent, str, R.string.exo_download_failed);
    }

    private Notification buildEndStateNotification(int i, PendingIntent pendingIntent, String str, int i2) {
        return buildNotification(i, pendingIntent, str, i2, 0, 0, false, false, true);
    }

    private Notification buildNotification(int i, PendingIntent pendingIntent, String str, int i2, int i3, int i4, boolean z, boolean z2, boolean z3) {
        this.notificationBuilder.setSmallIcon(i);
        this.notificationBuilder.setContentTitle(i2 == 0 ? null : this.context.getResources().getString(i2));
        this.notificationBuilder.setContentIntent(pendingIntent);
        this.notificationBuilder.setStyle(str != null ? new NotificationCompat.BigTextStyle().bigText(str) : null);
        this.notificationBuilder.setProgress(i3, i4, z);
        this.notificationBuilder.setOngoing(z2);
        this.notificationBuilder.setShowWhen(z3);
        return this.notificationBuilder.build();
    }
}