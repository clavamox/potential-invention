package com.google.android.exoplayer2.drm;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DefaultDrmSession;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.EventDispatcher;
import com.google.android.exoplayer2.util.Log;
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
import java.util.UUID;

/* loaded from: classes.dex */
public class DefaultDrmSessionManager<T extends ExoMediaCrypto> implements DrmSessionManager<T> {
    public static final int INITIAL_DRM_REQUEST_RETRY_COUNT = 3;
    public static final int MODE_DOWNLOAD = 2;
    public static final int MODE_PLAYBACK = 0;
    public static final int MODE_QUERY = 1;
    public static final int MODE_RELEASE = 3;
    public static final String PLAYREADY_CUSTOM_DATA_KEY = "PRCustomData";
    private static final String TAG = "DefaultDrmSessionMgr";
    private final MediaDrmCallback callback;
    private final EventDispatcher<DefaultDrmSessionEventListener> eventDispatcher;
    private ExoMediaDrm<T> exoMediaDrm;
    private final ExoMediaDrm.Provider<T> exoMediaDrmProvider;
    private final HashMap<String, String> keyRequestParameters;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    volatile DefaultDrmSessionManager<T>.MediaDrmHandler mediaDrmHandler;
    private int mode;
    private final boolean multiSession;
    private DefaultDrmSession<T> noMultiSessionDrmSession;
    private byte[] offlineLicenseKeySetId;
    private DefaultDrmSession<T> placeholderDrmSession;
    private final boolean playClearSamplesWithoutKeys;
    private Looper playbackLooper;
    private int prepareCallsCount;
    private final DefaultDrmSessionManager<T>.ProvisioningManagerImpl provisioningManagerImpl;
    private final List<DefaultDrmSession<T>> provisioningSessions;
    private final List<DefaultDrmSession<T>> sessions;
    private final int[] useDrmSessionsForClearContentTrackTypes;
    private final UUID uuid;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    public static final class Builder {
        private boolean multiSession;
        private boolean playClearSamplesWithoutKeys;
        private final HashMap<String, String> keyRequestParameters = new HashMap<>();
        private UUID uuid = C.WIDEVINE_UUID;
        private ExoMediaDrm.Provider<ExoMediaCrypto> exoMediaDrmProvider = FrameworkMediaDrm.DEFAULT_PROVIDER;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
        private int[] useDrmSessionsForClearContentTrackTypes = new int[0];

        public Builder setKeyRequestParameters(Map<String, String> map) {
            this.keyRequestParameters.clear();
            this.keyRequestParameters.putAll((Map) Assertions.checkNotNull(map));
            return this;
        }

        public Builder setUuidAndExoMediaDrmProvider(UUID uuid, ExoMediaDrm.Provider provider) {
            this.uuid = (UUID) Assertions.checkNotNull(uuid);
            this.exoMediaDrmProvider = (ExoMediaDrm.Provider) Assertions.checkNotNull(provider);
            return this;
        }

        public Builder setMultiSession(boolean z) {
            this.multiSession = z;
            return this;
        }

        public Builder setUseDrmSessionsForClearContent(int... iArr) {
            for (int i : iArr) {
                boolean z = true;
                if (i != 2 && i != 1) {
                    z = false;
                }
                Assertions.checkArgument(z);
            }
            this.useDrmSessionsForClearContentTrackTypes = (int[]) iArr.clone();
            return this;
        }

        public Builder setPlayClearSamplesWithoutKeys(boolean z) {
            this.playClearSamplesWithoutKeys = z;
            return this;
        }

        public Builder setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
            this.loadErrorHandlingPolicy = (LoadErrorHandlingPolicy) Assertions.checkNotNull(loadErrorHandlingPolicy);
            return this;
        }

        public DefaultDrmSessionManager<ExoMediaCrypto> build(MediaDrmCallback mediaDrmCallback) {
            return new DefaultDrmSessionManager<>(this.uuid, this.exoMediaDrmProvider, mediaDrmCallback, this.keyRequestParameters, this.multiSession, this.useDrmSessionsForClearContentTrackTypes, this.playClearSamplesWithoutKeys, this.loadErrorHandlingPolicy);
        }
    }

    public static final class MissingSchemeDataException extends Exception {
        private MissingSchemeDataException(UUID uuid) {
            super("Media does not support uuid: " + uuid);
        }
    }

    @Deprecated
    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> exoMediaDrm, MediaDrmCallback mediaDrmCallback, HashMap<String, String> hashMap) {
        this(uuid, exoMediaDrm, mediaDrmCallback, hashMap == null ? new HashMap<>() : hashMap, false, 3);
    }

    @Deprecated
    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> exoMediaDrm, MediaDrmCallback mediaDrmCallback, HashMap<String, String> hashMap, boolean z) {
        this(uuid, exoMediaDrm, mediaDrmCallback, hashMap == null ? new HashMap<>() : hashMap, z, 3);
    }

    @Deprecated
    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> exoMediaDrm, MediaDrmCallback mediaDrmCallback, HashMap<String, String> hashMap, boolean z, int i) {
        this(uuid, new ExoMediaDrm.AppManagedProvider(exoMediaDrm), mediaDrmCallback, hashMap == null ? new HashMap<>() : hashMap, z, new int[0], false, new DefaultLoadErrorHandlingPolicy(i));
    }

    private DefaultDrmSessionManager(UUID uuid, ExoMediaDrm.Provider<T> provider, MediaDrmCallback mediaDrmCallback, HashMap<String, String> hashMap, boolean z, int[] iArr, boolean z2, LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
        Assertions.checkNotNull(uuid);
        Assertions.checkArgument(!C.COMMON_PSSH_UUID.equals(uuid), "Use C.CLEARKEY_UUID instead");
        this.uuid = uuid;
        this.exoMediaDrmProvider = provider;
        this.callback = mediaDrmCallback;
        this.keyRequestParameters = hashMap;
        this.eventDispatcher = new EventDispatcher<>();
        this.multiSession = z;
        this.useDrmSessionsForClearContentTrackTypes = iArr;
        this.playClearSamplesWithoutKeys = z2;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.provisioningManagerImpl = new ProvisioningManagerImpl();
        this.mode = 0;
        this.sessions = new ArrayList();
        this.provisioningSessions = new ArrayList();
    }

    public final void addListener(Handler handler, DefaultDrmSessionEventListener defaultDrmSessionEventListener) {
        this.eventDispatcher.addListener(handler, defaultDrmSessionEventListener);
    }

    public final void removeListener(DefaultDrmSessionEventListener defaultDrmSessionEventListener) {
        this.eventDispatcher.removeListener(defaultDrmSessionEventListener);
    }

    public void setMode(int i, byte[] bArr) {
        Assertions.checkState(this.sessions.isEmpty());
        if (i == 1 || i == 3) {
            Assertions.checkNotNull(bArr);
        }
        this.mode = i;
        this.offlineLicenseKeySetId = bArr;
    }

    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public final void prepare() {
        int i = this.prepareCallsCount;
        this.prepareCallsCount = i + 1;
        if (i == 0) {
            Assertions.checkState(this.exoMediaDrm == null);
            ExoMediaDrm<T> acquireExoMediaDrm = this.exoMediaDrmProvider.acquireExoMediaDrm(this.uuid);
            this.exoMediaDrm = acquireExoMediaDrm;
            acquireExoMediaDrm.setOnEventListener(new MediaDrmEventListener());
        }
    }

    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public final void release() {
        int i = this.prepareCallsCount - 1;
        this.prepareCallsCount = i;
        if (i == 0) {
            ((ExoMediaDrm) Assertions.checkNotNull(this.exoMediaDrm)).release();
            this.exoMediaDrm = null;
        }
    }

    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public boolean canAcquireSession(DrmInitData drmInitData) {
        if (this.offlineLicenseKeySetId != null) {
            return true;
        }
        if (getSchemeDatas(drmInitData, this.uuid, true).isEmpty()) {
            if (drmInitData.schemeDataCount != 1 || !drmInitData.get(0).matches(C.COMMON_PSSH_UUID)) {
                return false;
            }
            Log.w(TAG, "DrmInitData only contains common PSSH SchemeData. Assuming support for: " + this.uuid);
        }
        String str = drmInitData.schemeType;
        if (str == null || C.CENC_TYPE_cenc.equals(str)) {
            return true;
        }
        return !(C.CENC_TYPE_cbc1.equals(str) || C.CENC_TYPE_cbcs.equals(str) || C.CENC_TYPE_cens.equals(str)) || Util.SDK_INT >= 25;
    }

    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public DrmSession<T> acquirePlaceholderSession(Looper looper, int i) {
        assertExpectedPlaybackLooper(looper);
        ExoMediaDrm exoMediaDrm = (ExoMediaDrm) Assertions.checkNotNull(this.exoMediaDrm);
        if ((FrameworkMediaCrypto.class.equals(exoMediaDrm.getExoMediaCryptoType()) && FrameworkMediaCrypto.WORKAROUND_DEVICE_NEEDS_KEYS_TO_CONFIGURE_CODEC) || Util.linearSearch(this.useDrmSessionsForClearContentTrackTypes, i) == -1 || exoMediaDrm.getExoMediaCryptoType() == null) {
            return null;
        }
        maybeCreateMediaDrmHandler(looper);
        if (this.placeholderDrmSession == null) {
            DefaultDrmSession<T> createNewDefaultSession = createNewDefaultSession(Collections.emptyList(), true);
            this.sessions.add(createNewDefaultSession);
            this.placeholderDrmSession = createNewDefaultSession;
        }
        this.placeholderDrmSession.acquire();
        return this.placeholderDrmSession;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v3, types: [com.google.android.exoplayer2.drm.DefaultDrmSession, com.google.android.exoplayer2.drm.DrmSession<T extends com.google.android.exoplayer2.drm.ExoMediaCrypto>] */
    /* JADX WARN: Type inference failed for: r1v5, types: [com.google.android.exoplayer2.drm.DefaultDrmSession<T extends com.google.android.exoplayer2.drm.ExoMediaCrypto>] */
    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public DrmSession<T> acquireSession(Looper looper, DrmInitData drmInitData) {
        List<DrmInitData.SchemeData> list;
        assertExpectedPlaybackLooper(looper);
        maybeCreateMediaDrmHandler(looper);
        DefaultDrmSession<T> defaultDrmSession = (DefaultDrmSession<T>) null;
        if (this.offlineLicenseKeySetId == null) {
            list = getSchemeDatas(drmInitData, this.uuid, false);
            if (list.isEmpty()) {
                final MissingSchemeDataException missingSchemeDataException = new MissingSchemeDataException(this.uuid);
                this.eventDispatcher.dispatch(new EventDispatcher.Event() { // from class: com.google.android.exoplayer2.drm.DefaultDrmSessionManager$$ExternalSyntheticLambda0
                    @Override // com.google.android.exoplayer2.util.EventDispatcher.Event
                    public final void sendTo(Object obj) {
                        ((DefaultDrmSessionEventListener) obj).onDrmSessionManagerError(DefaultDrmSessionManager.MissingSchemeDataException.this);
                    }
                });
                return new ErrorStateDrmSession(new DrmSession.DrmSessionException(missingSchemeDataException));
            }
        } else {
            list = null;
        }
        if (!this.multiSession) {
            defaultDrmSession = this.noMultiSessionDrmSession;
        } else {
            Iterator<DefaultDrmSession<T>> it = this.sessions.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                DefaultDrmSession<T> next = it.next();
                if (Util.areEqual(next.schemeDatas, list)) {
                    defaultDrmSession = next;
                    break;
                }
            }
        }
        if (defaultDrmSession == 0) {
            defaultDrmSession = createNewDefaultSession(list, false);
            if (!this.multiSession) {
                this.noMultiSessionDrmSession = defaultDrmSession;
            }
            this.sessions.add(defaultDrmSession);
        }
        ((DefaultDrmSession) defaultDrmSession).acquire();
        return (DrmSession<T>) defaultDrmSession;
    }

    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public Class<T> getExoMediaCryptoType(DrmInitData drmInitData) {
        if (canAcquireSession(drmInitData)) {
            return ((ExoMediaDrm) Assertions.checkNotNull(this.exoMediaDrm)).getExoMediaCryptoType();
        }
        return null;
    }

    private void assertExpectedPlaybackLooper(Looper looper) {
        Looper looper2 = this.playbackLooper;
        Assertions.checkState(looper2 == null || looper2 == looper);
        this.playbackLooper = looper;
    }

    private void maybeCreateMediaDrmHandler(Looper looper) {
        if (this.mediaDrmHandler == null) {
            this.mediaDrmHandler = new MediaDrmHandler(looper);
        }
    }

    private DefaultDrmSession<T> createNewDefaultSession(List<DrmInitData.SchemeData> list, boolean z) {
        Assertions.checkNotNull(this.exoMediaDrm);
        return new DefaultDrmSession<>(this.uuid, this.exoMediaDrm, this.provisioningManagerImpl, new DefaultDrmSession.ReleaseCallback() { // from class: com.google.android.exoplayer2.drm.DefaultDrmSessionManager$$ExternalSyntheticLambda1
            @Override // com.google.android.exoplayer2.drm.DefaultDrmSession.ReleaseCallback
            public final void onSessionReleased(DefaultDrmSession defaultDrmSession) {
                DefaultDrmSessionManager.this.onSessionReleased(defaultDrmSession);
            }
        }, list, this.mode, this.playClearSamplesWithoutKeys | z, z, this.offlineLicenseKeySetId, this.keyRequestParameters, this.callback, (Looper) Assertions.checkNotNull(this.playbackLooper), this.eventDispatcher, this.loadErrorHandlingPolicy);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSessionReleased(DefaultDrmSession<T> defaultDrmSession) {
        this.sessions.remove(defaultDrmSession);
        if (this.placeholderDrmSession == defaultDrmSession) {
            this.placeholderDrmSession = null;
        }
        if (this.noMultiSessionDrmSession == defaultDrmSession) {
            this.noMultiSessionDrmSession = null;
        }
        if (this.provisioningSessions.size() > 1 && this.provisioningSessions.get(0) == defaultDrmSession) {
            this.provisioningSessions.get(1).provision();
        }
        this.provisioningSessions.remove(defaultDrmSession);
    }

    private static List<DrmInitData.SchemeData> getSchemeDatas(DrmInitData drmInitData, UUID uuid, boolean z) {
        ArrayList arrayList = new ArrayList(drmInitData.schemeDataCount);
        for (int i = 0; i < drmInitData.schemeDataCount; i++) {
            DrmInitData.SchemeData schemeData = drmInitData.get(i);
            if ((schemeData.matches(uuid) || (C.CLEARKEY_UUID.equals(uuid) && schemeData.matches(C.COMMON_PSSH_UUID))) && (schemeData.data != null || z)) {
                arrayList.add(schemeData);
            }
        }
        return arrayList;
    }

    private class MediaDrmHandler extends Handler {
        public MediaDrmHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            byte[] bArr = (byte[]) message.obj;
            if (bArr == null) {
                return;
            }
            for (DefaultDrmSession defaultDrmSession : DefaultDrmSessionManager.this.sessions) {
                if (defaultDrmSession.hasSessionId(bArr)) {
                    defaultDrmSession.onMediaDrmEvent(message.what);
                    return;
                }
            }
        }
    }

    private class ProvisioningManagerImpl implements DefaultDrmSession.ProvisioningManager<T> {
        private ProvisioningManagerImpl() {
        }

        @Override // com.google.android.exoplayer2.drm.DefaultDrmSession.ProvisioningManager
        public void provisionRequired(DefaultDrmSession<T> defaultDrmSession) {
            if (DefaultDrmSessionManager.this.provisioningSessions.contains(defaultDrmSession)) {
                return;
            }
            DefaultDrmSessionManager.this.provisioningSessions.add(defaultDrmSession);
            if (DefaultDrmSessionManager.this.provisioningSessions.size() == 1) {
                defaultDrmSession.provision();
            }
        }

        @Override // com.google.android.exoplayer2.drm.DefaultDrmSession.ProvisioningManager
        public void onProvisionCompleted() {
            Iterator it = DefaultDrmSessionManager.this.provisioningSessions.iterator();
            while (it.hasNext()) {
                ((DefaultDrmSession) it.next()).onProvisionCompleted();
            }
            DefaultDrmSessionManager.this.provisioningSessions.clear();
        }

        @Override // com.google.android.exoplayer2.drm.DefaultDrmSession.ProvisioningManager
        public void onProvisionError(Exception exc) {
            Iterator it = DefaultDrmSessionManager.this.provisioningSessions.iterator();
            while (it.hasNext()) {
                ((DefaultDrmSession) it.next()).onProvisionError(exc);
            }
            DefaultDrmSessionManager.this.provisioningSessions.clear();
        }
    }

    private class MediaDrmEventListener implements ExoMediaDrm.OnEventListener<T> {
        private MediaDrmEventListener() {
        }

        @Override // com.google.android.exoplayer2.drm.ExoMediaDrm.OnEventListener
        public void onEvent(ExoMediaDrm<? extends T> exoMediaDrm, byte[] bArr, int i, int i2, byte[] bArr2) {
            ((MediaDrmHandler) Assertions.checkNotNull(DefaultDrmSessionManager.this.mediaDrmHandler)).obtainMessage(i, bArr).sendToTarget();
        }
    }
}