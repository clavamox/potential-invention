package com.google.android.exoplayer2.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.media.session.MediaSessionCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.app.NotificationCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class PlayerNotificationManager {
    private static final String ACTION_DISMISS = "com.google.android.exoplayer.dismiss";
    public static final String ACTION_FAST_FORWARD = "com.google.android.exoplayer.ffwd";
    public static final String ACTION_NEXT = "com.google.android.exoplayer.next";
    public static final String ACTION_PAUSE = "com.google.android.exoplayer.pause";
    public static final String ACTION_PLAY = "com.google.android.exoplayer.play";
    public static final String ACTION_PREVIOUS = "com.google.android.exoplayer.prev";
    public static final String ACTION_REWIND = "com.google.android.exoplayer.rewind";
    public static final String ACTION_STOP = "com.google.android.exoplayer.stop";
    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    public static final int DEFAULT_REWIND_MS = 5000;
    public static final String EXTRA_INSTANCE_ID = "INSTANCE_ID";
    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;
    private static final int MSG_START_OR_UPDATE_NOTIFICATION = 0;
    private static final int MSG_UPDATE_NOTIFICATION_BITMAP = 1;
    private static int instanceIdCounter;
    private int badgeIconType;
    private NotificationCompat.Builder builder;
    private ArrayList<NotificationCompat.Action> builderActions;
    private final String channelId;
    private int color;
    private boolean colorized;
    private final Context context;
    private ControlDispatcher controlDispatcher;
    private int currentNotificationTag;
    private final CustomActionReceiver customActionReceiver;
    private final Map<String, NotificationCompat.Action> customActions;
    private int defaults;
    private final PendingIntent dismissPendingIntent;
    private long fastForwardMs;
    private final int instanceId;
    private final IntentFilter intentFilter;
    private boolean isNotificationStarted;
    private final Handler mainHandler;
    private final MediaDescriptionAdapter mediaDescriptionAdapter;
    private MediaSessionCompat.Token mediaSessionToken;
    private final NotificationBroadcastReceiver notificationBroadcastReceiver;
    private final int notificationId;
    private NotificationListener notificationListener;
    private final NotificationManagerCompat notificationManager;
    private final Map<String, NotificationCompat.Action> playbackActions;
    private PlaybackPreparer playbackPreparer;
    private Player player;
    private final Player.EventListener playerListener;
    private int priority;
    private long rewindMs;
    private int smallIconResourceId;
    private boolean useChronometer;
    private boolean useNavigationActions;
    private boolean useNavigationActionsInCompactView;
    private boolean usePlayPauseActions;
    private boolean useStopAction;
    private int visibility;
    private final Timeline.Window window;

    public interface CustomActionReceiver {
        Map<String, NotificationCompat.Action> createCustomActions(Context context, int i);

        List<String> getCustomActions(Player player);

        void onCustomAction(Player player, String str, Intent intent);
    }

    public interface MediaDescriptionAdapter {
        PendingIntent createCurrentContentIntent(Player player);

        String getCurrentContentText(Player player);

        String getCurrentContentTitle(Player player);

        Bitmap getCurrentLargeIcon(Player player, BitmapCallback bitmapCallback);

        default String getCurrentSubText(Player player) {
            return null;
        }
    }

    public interface NotificationListener {
        @Deprecated
        default void onNotificationCancelled(int i) {
        }

        default void onNotificationCancelled(int i, boolean z) {
        }

        default void onNotificationPosted(int i, Notification notification, boolean z) {
        }

        @Deprecated
        default void onNotificationStarted(int i, Notification notification) {
        }
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Priority {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }

    public final class BitmapCallback {
        private final int notificationTag;

        private BitmapCallback(int i) {
            this.notificationTag = i;
        }

        public void onBitmap(Bitmap bitmap) {
            if (bitmap != null) {
                PlayerNotificationManager.this.postUpdateNotificationBitmap(bitmap, this.notificationTag);
            }
        }
    }

    @Deprecated
    public static PlayerNotificationManager createWithNotificationChannel(Context context, String str, int i, int i2, MediaDescriptionAdapter mediaDescriptionAdapter) {
        return createWithNotificationChannel(context, str, i, 0, i2, mediaDescriptionAdapter);
    }

    public static PlayerNotificationManager createWithNotificationChannel(Context context, String str, int i, int i2, int i3, MediaDescriptionAdapter mediaDescriptionAdapter) {
        NotificationUtil.createNotificationChannel(context, str, i, i2, 2);
        return new PlayerNotificationManager(context, str, i3, mediaDescriptionAdapter);
    }

    @Deprecated
    public static PlayerNotificationManager createWithNotificationChannel(Context context, String str, int i, int i2, MediaDescriptionAdapter mediaDescriptionAdapter, NotificationListener notificationListener) {
        return createWithNotificationChannel(context, str, i, 0, i2, mediaDescriptionAdapter, notificationListener);
    }

    public static PlayerNotificationManager createWithNotificationChannel(Context context, String str, int i, int i2, int i3, MediaDescriptionAdapter mediaDescriptionAdapter, NotificationListener notificationListener) {
        NotificationUtil.createNotificationChannel(context, str, i, i2, 2);
        return new PlayerNotificationManager(context, str, i3, mediaDescriptionAdapter, notificationListener);
    }

    public PlayerNotificationManager(Context context, String str, int i, MediaDescriptionAdapter mediaDescriptionAdapter) {
        this(context, str, i, mediaDescriptionAdapter, null, null);
    }

    public PlayerNotificationManager(Context context, String str, int i, MediaDescriptionAdapter mediaDescriptionAdapter, NotificationListener notificationListener) {
        this(context, str, i, mediaDescriptionAdapter, notificationListener, null);
    }

    public PlayerNotificationManager(Context context, String str, int i, MediaDescriptionAdapter mediaDescriptionAdapter, CustomActionReceiver customActionReceiver) {
        this(context, str, i, mediaDescriptionAdapter, null, customActionReceiver);
    }

    public PlayerNotificationManager(Context context, String str, int i, MediaDescriptionAdapter mediaDescriptionAdapter, NotificationListener notificationListener, CustomActionReceiver customActionReceiver) {
        Map<String, NotificationCompat.Action> emptyMap;
        Context applicationContext = context.getApplicationContext();
        this.context = applicationContext;
        this.channelId = str;
        this.notificationId = i;
        this.mediaDescriptionAdapter = mediaDescriptionAdapter;
        this.notificationListener = notificationListener;
        this.customActionReceiver = customActionReceiver;
        this.controlDispatcher = new DefaultControlDispatcher();
        this.window = new Timeline.Window();
        int i2 = instanceIdCounter;
        instanceIdCounter = i2 + 1;
        this.instanceId = i2;
        this.mainHandler = Util.createHandler(Looper.getMainLooper(), new Handler.Callback() { // from class: com.google.android.exoplayer2.ui.PlayerNotificationManager$$ExternalSyntheticLambda0
            @Override // android.os.Handler.Callback
            public final boolean handleMessage(Message message) {
                return PlayerNotificationManager.this.m54x4036bc0f(message);
            }
        });
        this.notificationManager = NotificationManagerCompat.from(applicationContext);
        this.playerListener = new PlayerListener();
        this.notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        this.intentFilter = new IntentFilter();
        this.useNavigationActions = true;
        this.usePlayPauseActions = true;
        this.colorized = true;
        this.useChronometer = true;
        this.color = 0;
        this.smallIconResourceId = R.drawable.exo_notification_small_icon;
        this.defaults = 0;
        this.priority = -1;
        this.fastForwardMs = 15000L;
        this.rewindMs = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
        this.badgeIconType = 1;
        this.visibility = 1;
        Map<String, NotificationCompat.Action> createPlaybackActions = createPlaybackActions(applicationContext, i2);
        this.playbackActions = createPlaybackActions;
        Iterator<String> it = createPlaybackActions.keySet().iterator();
        while (it.hasNext()) {
            this.intentFilter.addAction(it.next());
        }
        if (customActionReceiver != null) {
            emptyMap = customActionReceiver.createCustomActions(applicationContext, this.instanceId);
        } else {
            emptyMap = Collections.emptyMap();
        }
        this.customActions = emptyMap;
        Iterator<String> it2 = emptyMap.keySet().iterator();
        while (it2.hasNext()) {
            this.intentFilter.addAction(it2.next());
        }
        this.dismissPendingIntent = createBroadcastIntent(ACTION_DISMISS, applicationContext, this.instanceId);
        this.intentFilter.addAction(ACTION_DISMISS);
    }

    public final void setPlayer(Player player) {
        boolean z = true;
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        if (player != null && player.getApplicationLooper() != Looper.getMainLooper()) {
            z = false;
        }
        Assertions.checkArgument(z);
        Player player2 = this.player;
        if (player2 == player) {
            return;
        }
        if (player2 != null) {
            player2.removeListener(this.playerListener);
            if (player == null) {
                stopNotification(false);
            }
        }
        this.player = player;
        if (player != null) {
            player.addListener(this.playerListener);
            postStartOrUpdateNotification();
        }
    }

    public void setPlaybackPreparer(PlaybackPreparer playbackPreparer) {
        this.playbackPreparer = playbackPreparer;
    }

    public final void setControlDispatcher(ControlDispatcher controlDispatcher) {
        if (controlDispatcher == null) {
            controlDispatcher = new DefaultControlDispatcher();
        }
        this.controlDispatcher = controlDispatcher;
    }

    @Deprecated
    public final void setNotificationListener(NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    public final void setFastForwardIncrementMs(long j) {
        if (this.fastForwardMs == j) {
            return;
        }
        this.fastForwardMs = j;
        invalidate();
    }

    public final void setRewindIncrementMs(long j) {
        if (this.rewindMs == j) {
            return;
        }
        this.rewindMs = j;
        invalidate();
    }

    public final void setUseNavigationActions(boolean z) {
        if (this.useNavigationActions != z) {
            this.useNavigationActions = z;
            invalidate();
        }
    }

    public final void setUseNavigationActionsInCompactView(boolean z) {
        if (this.useNavigationActionsInCompactView != z) {
            this.useNavigationActionsInCompactView = z;
            invalidate();
        }
    }

    public final void setUsePlayPauseActions(boolean z) {
        if (this.usePlayPauseActions != z) {
            this.usePlayPauseActions = z;
            invalidate();
        }
    }

    public final void setUseStopAction(boolean z) {
        if (this.useStopAction == z) {
            return;
        }
        this.useStopAction = z;
        invalidate();
    }

    public final void setMediaSessionToken(MediaSessionCompat.Token token) {
        if (Util.areEqual(this.mediaSessionToken, token)) {
            return;
        }
        this.mediaSessionToken = token;
        invalidate();
    }

    public final void setBadgeIconType(int i) {
        if (this.badgeIconType == i) {
            return;
        }
        if (i == 0 || i == 1 || i == 2) {
            this.badgeIconType = i;
            invalidate();
            return;
        }
        throw new IllegalArgumentException();
    }

    public final void setColorized(boolean z) {
        if (this.colorized != z) {
            this.colorized = z;
            invalidate();
        }
    }

    public final void setDefaults(int i) {
        if (this.defaults != i) {
            this.defaults = i;
            invalidate();
        }
    }

    public final void setColor(int i) {
        if (this.color != i) {
            this.color = i;
            invalidate();
        }
    }

    public final void setPriority(int i) {
        if (this.priority == i) {
            return;
        }
        if (i == -2 || i == -1 || i == 0 || i == 1 || i == 2) {
            this.priority = i;
            invalidate();
            return;
        }
        throw new IllegalArgumentException();
    }

    public final void setSmallIcon(int i) {
        if (this.smallIconResourceId != i) {
            this.smallIconResourceId = i;
            invalidate();
        }
    }

    public final void setUseChronometer(boolean z) {
        if (this.useChronometer != z) {
            this.useChronometer = z;
            invalidate();
        }
    }

    public final void setVisibility(int i) {
        if (this.visibility == i) {
            return;
        }
        if (i == -1 || i == 0 || i == 1) {
            this.visibility = i;
            invalidate();
            return;
        }
        throw new IllegalStateException();
    }

    public void invalidate() {
        if (this.isNotificationStarted) {
            postStartOrUpdateNotification();
        }
    }

    private void startOrUpdateNotification(Player player, Bitmap bitmap) {
        boolean ongoing = getOngoing(player);
        NotificationCompat.Builder createNotification = createNotification(player, this.builder, ongoing, bitmap);
        this.builder = createNotification;
        if (createNotification == null) {
            stopNotification(false);
            return;
        }
        Notification build = createNotification.build();
        this.notificationManager.notify(this.notificationId, build);
        if (!this.isNotificationStarted) {
            this.isNotificationStarted = true;
            this.context.registerReceiver(this.notificationBroadcastReceiver, this.intentFilter);
            NotificationListener notificationListener = this.notificationListener;
            if (notificationListener != null) {
                notificationListener.onNotificationStarted(this.notificationId, build);
            }
        }
        NotificationListener notificationListener2 = this.notificationListener;
        if (notificationListener2 != null) {
            notificationListener2.onNotificationPosted(this.notificationId, build, ongoing);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopNotification(boolean z) {
        if (this.isNotificationStarted) {
            this.isNotificationStarted = false;
            this.mainHandler.removeMessages(0);
            this.notificationManager.cancel(this.notificationId);
            this.context.unregisterReceiver(this.notificationBroadcastReceiver);
            NotificationListener notificationListener = this.notificationListener;
            if (notificationListener != null) {
                notificationListener.onNotificationCancelled(this.notificationId, z);
                this.notificationListener.onNotificationCancelled(this.notificationId);
            }
        }
    }

    protected NotificationCompat.Builder createNotification(Player player, NotificationCompat.Builder builder, boolean z, Bitmap bitmap) {
        NotificationCompat.Action action;
        if (player.getPlaybackState() == 1 && (player.getCurrentTimeline().isEmpty() || this.playbackPreparer == null)) {
            this.builderActions = null;
            return null;
        }
        List<String> actions = getActions(player);
        ArrayList<NotificationCompat.Action> arrayList = new ArrayList<>(actions.size());
        for (int i = 0; i < actions.size(); i++) {
            String str = actions.get(i);
            if (this.playbackActions.containsKey(str)) {
                action = this.playbackActions.get(str);
            } else {
                action = this.customActions.get(str);
            }
            if (action != null) {
                arrayList.add(action);
            }
        }
        if (builder == null || !arrayList.equals(this.builderActions)) {
            builder = new NotificationCompat.Builder(this.context, this.channelId);
            this.builderActions = arrayList;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                builder.addAction(arrayList.get(i2));
            }
        }
        NotificationCompat.MediaStyle mediaStyle = new NotificationCompat.MediaStyle();
        MediaSessionCompat.Token token = this.mediaSessionToken;
        if (token != null) {
            mediaStyle.setMediaSession(token);
        }
        mediaStyle.setShowActionsInCompactView(getActionIndicesForCompactView(actions, player));
        mediaStyle.setShowCancelButton(!z);
        mediaStyle.setCancelButtonIntent(this.dismissPendingIntent);
        builder.setStyle(mediaStyle);
        builder.setDeleteIntent(this.dismissPendingIntent);
        builder.setBadgeIconType(this.badgeIconType).setOngoing(z).setColor(this.color).setColorized(this.colorized).setSmallIcon(this.smallIconResourceId).setVisibility(this.visibility).setPriority(this.priority).setDefaults(this.defaults);
        if (Util.SDK_INT >= 21 && this.useChronometer && player.isPlaying() && !player.isPlayingAd() && !player.isCurrentWindowDynamic() && player.getPlaybackParameters().speed == 1.0f) {
            builder.setWhen(System.currentTimeMillis() - player.getContentPosition()).setShowWhen(true).setUsesChronometer(true);
        } else {
            builder.setShowWhen(false).setUsesChronometer(false);
        }
        builder.setContentTitle(this.mediaDescriptionAdapter.getCurrentContentTitle(player));
        builder.setContentText(this.mediaDescriptionAdapter.getCurrentContentText(player));
        builder.setSubText(this.mediaDescriptionAdapter.getCurrentSubText(player));
        if (bitmap == null) {
            MediaDescriptionAdapter mediaDescriptionAdapter = this.mediaDescriptionAdapter;
            int i3 = this.currentNotificationTag + 1;
            this.currentNotificationTag = i3;
            bitmap = mediaDescriptionAdapter.getCurrentLargeIcon(player, new BitmapCallback(i3));
        }
        setLargeIcon(builder, bitmap);
        builder.setContentIntent(this.mediaDescriptionAdapter.createCurrentContentIntent(player));
        return builder;
    }

    protected List<String> getActions(Player player) {
        boolean z;
        boolean z2;
        boolean z3;
        Timeline currentTimeline = player.getCurrentTimeline();
        if (currentTimeline.isEmpty() || player.isPlayingAd()) {
            z = false;
            z2 = false;
            z3 = false;
        } else {
            currentTimeline.getWindow(player.getCurrentWindowIndex(), this.window);
            boolean z4 = this.window.isSeekable || !this.window.isDynamic || player.hasPrevious();
            z2 = this.rewindMs > 0;
            z3 = this.fastForwardMs > 0;
            r2 = z4;
            z = this.window.isDynamic || player.hasNext();
        }
        ArrayList arrayList = new ArrayList();
        if (this.useNavigationActions && r2) {
            arrayList.add(ACTION_PREVIOUS);
        }
        if (z2) {
            arrayList.add(ACTION_REWIND);
        }
        if (this.usePlayPauseActions) {
            if (shouldShowPauseButton(player)) {
                arrayList.add(ACTION_PAUSE);
            } else {
                arrayList.add(ACTION_PLAY);
            }
        }
        if (z3) {
            arrayList.add(ACTION_FAST_FORWARD);
        }
        if (this.useNavigationActions && z) {
            arrayList.add(ACTION_NEXT);
        }
        CustomActionReceiver customActionReceiver = this.customActionReceiver;
        if (customActionReceiver != null) {
            arrayList.addAll(customActionReceiver.getCustomActions(player));
        }
        if (this.useStopAction) {
            arrayList.add(ACTION_STOP);
        }
        return arrayList;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0047  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected int[] getActionIndicesForCompactView(java.util.List<java.lang.String> r7, com.google.android.exoplayer2.Player r8) {
        /*
            r6 = this;
            java.lang.String r0 = "com.google.android.exoplayer.pause"
            int r0 = r7.indexOf(r0)
            java.lang.String r1 = "com.google.android.exoplayer.play"
            int r1 = r7.indexOf(r1)
            boolean r2 = r6.useNavigationActionsInCompactView
            r3 = -1
            if (r2 == 0) goto L18
            java.lang.String r2 = "com.google.android.exoplayer.prev"
            int r2 = r7.indexOf(r2)
            goto L19
        L18:
            r2 = r3
        L19:
            boolean r4 = r6.useNavigationActionsInCompactView
            if (r4 == 0) goto L24
            java.lang.String r4 = "com.google.android.exoplayer.next"
            int r7 = r7.indexOf(r4)
            goto L25
        L24:
            r7 = r3
        L25:
            r4 = 3
            int[] r4 = new int[r4]
            r5 = 0
            if (r2 == r3) goto L2e
            r4[r5] = r2
            r5 = 1
        L2e:
            boolean r8 = r6.shouldShowPauseButton(r8)
            if (r0 == r3) goto L3c
            if (r8 == 0) goto L3c
            int r8 = r5 + 1
            r4[r5] = r0
        L3a:
            r5 = r8
            goto L45
        L3c:
            if (r1 == r3) goto L45
            if (r8 != 0) goto L45
            int r8 = r5 + 1
            r4[r5] = r1
            goto L3a
        L45:
            if (r7 == r3) goto L4c
            int r8 = r5 + 1
            r4[r5] = r7
            r5 = r8
        L4c:
            int[] r7 = java.util.Arrays.copyOf(r4, r5)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ui.PlayerNotificationManager.getActionIndicesForCompactView(java.util.List, com.google.android.exoplayer2.Player):int[]");
    }

    protected boolean getOngoing(Player player) {
        int playbackState = player.getPlaybackState();
        return (playbackState == 2 || playbackState == 3) && player.getPlayWhenReady();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void previous(Player player) {
        Timeline currentTimeline = player.getCurrentTimeline();
        if (currentTimeline.isEmpty() || player.isPlayingAd()) {
            return;
        }
        int currentWindowIndex = player.getCurrentWindowIndex();
        currentTimeline.getWindow(currentWindowIndex, this.window);
        int previousWindowIndex = player.getPreviousWindowIndex();
        if (previousWindowIndex != -1 && (player.getCurrentPosition() <= MAX_POSITION_FOR_SEEK_TO_PREVIOUS || (this.window.isDynamic && !this.window.isSeekable))) {
            seekTo(player, previousWindowIndex, C.TIME_UNSET);
        } else {
            seekTo(player, currentWindowIndex, 0L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void next(Player player) {
        Timeline currentTimeline = player.getCurrentTimeline();
        if (currentTimeline.isEmpty() || player.isPlayingAd()) {
            return;
        }
        int currentWindowIndex = player.getCurrentWindowIndex();
        int nextWindowIndex = player.getNextWindowIndex();
        if (nextWindowIndex != -1) {
            seekTo(player, nextWindowIndex, C.TIME_UNSET);
        } else if (currentTimeline.getWindow(currentWindowIndex, this.window).isDynamic) {
            seekTo(player, currentWindowIndex, C.TIME_UNSET);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rewind(Player player) {
        if (player.isCurrentWindowSeekable()) {
            long j = this.rewindMs;
            if (j > 0) {
                seekToOffset(player, -j);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fastForward(Player player) {
        if (player.isCurrentWindowSeekable()) {
            long j = this.fastForwardMs;
            if (j > 0) {
                seekToOffset(player, j);
            }
        }
    }

    private void seekToOffset(Player player, long j) {
        long currentPosition = player.getCurrentPosition() + j;
        long duration = player.getDuration();
        if (duration != C.TIME_UNSET) {
            currentPosition = Math.min(currentPosition, duration);
        }
        seekTo(player, player.getCurrentWindowIndex(), Math.max(currentPosition, 0L));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void seekTo(Player player, int i, long j) {
        this.controlDispatcher.dispatchSeekTo(player, i, j);
    }

    private boolean shouldShowPauseButton(Player player) {
        return (player.getPlaybackState() == 4 || player.getPlaybackState() == 1 || !player.getPlayWhenReady()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postStartOrUpdateNotification() {
        if (this.mainHandler.hasMessages(0)) {
            return;
        }
        this.mainHandler.sendEmptyMessage(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postUpdateNotificationBitmap(Bitmap bitmap, int i) {
        this.mainHandler.obtainMessage(1, i, -1, bitmap).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: handleMessage, reason: merged with bridge method [inline-methods] */
    public boolean m54x4036bc0f(Message message) {
        int i = message.what;
        if (i == 0) {
            Player player = this.player;
            if (player != null) {
                startOrUpdateNotification(player, null);
            }
        } else {
            if (i != 1) {
                return false;
            }
            if (this.player != null && this.isNotificationStarted && this.currentNotificationTag == message.arg1) {
                startOrUpdateNotification(this.player, (Bitmap) message.obj);
            }
        }
        return true;
    }

    private static Map<String, NotificationCompat.Action> createPlaybackActions(Context context, int i) {
        HashMap hashMap = new HashMap();
        hashMap.put(ACTION_PLAY, new NotificationCompat.Action(R.drawable.exo_notification_play, context.getString(R.string.exo_controls_play_description), createBroadcastIntent(ACTION_PLAY, context, i)));
        hashMap.put(ACTION_PAUSE, new NotificationCompat.Action(R.drawable.exo_notification_pause, context.getString(R.string.exo_controls_pause_description), createBroadcastIntent(ACTION_PAUSE, context, i)));
        hashMap.put(ACTION_STOP, new NotificationCompat.Action(R.drawable.exo_notification_stop, context.getString(R.string.exo_controls_stop_description), createBroadcastIntent(ACTION_STOP, context, i)));
        hashMap.put(ACTION_REWIND, new NotificationCompat.Action(R.drawable.exo_notification_rewind, context.getString(R.string.exo_controls_rewind_description), createBroadcastIntent(ACTION_REWIND, context, i)));
        hashMap.put(ACTION_FAST_FORWARD, new NotificationCompat.Action(R.drawable.exo_notification_fastforward, context.getString(R.string.exo_controls_fastforward_description), createBroadcastIntent(ACTION_FAST_FORWARD, context, i)));
        hashMap.put(ACTION_PREVIOUS, new NotificationCompat.Action(R.drawable.exo_notification_previous, context.getString(R.string.exo_controls_previous_description), createBroadcastIntent(ACTION_PREVIOUS, context, i)));
        hashMap.put(ACTION_NEXT, new NotificationCompat.Action(R.drawable.exo_notification_next, context.getString(R.string.exo_controls_next_description), createBroadcastIntent(ACTION_NEXT, context, i)));
        return hashMap;
    }

    private static PendingIntent createBroadcastIntent(String str, Context context, int i) {
        Intent intent = new Intent(str).setPackage(context.getPackageName());
        intent.putExtra(EXTRA_INSTANCE_ID, i);
        return PendingIntent.getBroadcast(context, i, intent, 134217728);
    }

    private static void setLargeIcon(NotificationCompat.Builder builder, Bitmap bitmap) {
        builder.setLargeIcon(bitmap);
    }

    private class PlayerListener implements Player.EventListener {
        private PlayerListener() {
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onPlayerStateChanged(boolean z, int i) {
            PlayerNotificationManager.this.postStartOrUpdateNotification();
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onIsPlayingChanged(boolean z) {
            PlayerNotificationManager.this.postStartOrUpdateNotification();
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onTimelineChanged(Timeline timeline, int i) {
            PlayerNotificationManager.this.postStartOrUpdateNotification();
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            PlayerNotificationManager.this.postStartOrUpdateNotification();
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onPositionDiscontinuity(int i) {
            PlayerNotificationManager.this.postStartOrUpdateNotification();
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onRepeatModeChanged(int i) {
            PlayerNotificationManager.this.postStartOrUpdateNotification();
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onShuffleModeEnabledChanged(boolean z) {
            PlayerNotificationManager.this.postStartOrUpdateNotification();
        }
    }

    private class NotificationBroadcastReceiver extends BroadcastReceiver {
        private NotificationBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Player player = PlayerNotificationManager.this.player;
            if (player != null && PlayerNotificationManager.this.isNotificationStarted && intent.getIntExtra(PlayerNotificationManager.EXTRA_INSTANCE_ID, PlayerNotificationManager.this.instanceId) == PlayerNotificationManager.this.instanceId) {
                String action = intent.getAction();
                if (PlayerNotificationManager.ACTION_PLAY.equals(action)) {
                    if (player.getPlaybackState() == 1) {
                        if (PlayerNotificationManager.this.playbackPreparer != null) {
                            PlayerNotificationManager.this.playbackPreparer.preparePlayback();
                        }
                    } else if (player.getPlaybackState() == 4) {
                        PlayerNotificationManager.this.seekTo(player, player.getCurrentWindowIndex(), C.TIME_UNSET);
                    }
                    PlayerNotificationManager.this.controlDispatcher.dispatchSetPlayWhenReady(player, true);
                    return;
                }
                if (PlayerNotificationManager.ACTION_PAUSE.equals(action)) {
                    PlayerNotificationManager.this.controlDispatcher.dispatchSetPlayWhenReady(player, false);
                    return;
                }
                if (PlayerNotificationManager.ACTION_PREVIOUS.equals(action)) {
                    PlayerNotificationManager.this.previous(player);
                    return;
                }
                if (PlayerNotificationManager.ACTION_REWIND.equals(action)) {
                    PlayerNotificationManager.this.rewind(player);
                    return;
                }
                if (PlayerNotificationManager.ACTION_FAST_FORWARD.equals(action)) {
                    PlayerNotificationManager.this.fastForward(player);
                    return;
                }
                if (PlayerNotificationManager.ACTION_NEXT.equals(action)) {
                    PlayerNotificationManager.this.next(player);
                    return;
                }
                if (PlayerNotificationManager.ACTION_STOP.equals(action)) {
                    PlayerNotificationManager.this.controlDispatcher.dispatchStop(player, true);
                    return;
                }
                if (PlayerNotificationManager.ACTION_DISMISS.equals(action)) {
                    PlayerNotificationManager.this.stopNotification(true);
                } else {
                    if (action == null || PlayerNotificationManager.this.customActionReceiver == null || !PlayerNotificationManager.this.customActions.containsKey(action)) {
                        return;
                    }
                    PlayerNotificationManager.this.customActionReceiver.onCustomAction(player, action, intent);
                }
            }
        }
    }
}