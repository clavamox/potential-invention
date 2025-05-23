package com.google.android.exoplayer2.drm;

import android.os.Looper;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;

/* loaded from: classes.dex */
public interface DrmSessionManager<T extends ExoMediaCrypto> {
    public static final DrmSessionManager<ExoMediaCrypto> DUMMY = new DrmSessionManager<ExoMediaCrypto>() { // from class: com.google.android.exoplayer2.drm.DrmSessionManager.1
        @Override // com.google.android.exoplayer2.drm.DrmSessionManager
        public boolean canAcquireSession(DrmInitData drmInitData) {
            return false;
        }

        @Override // com.google.android.exoplayer2.drm.DrmSessionManager
        public Class<ExoMediaCrypto> getExoMediaCryptoType(DrmInitData drmInitData) {
            return null;
        }

        @Override // com.google.android.exoplayer2.drm.DrmSessionManager
        public DrmSession<ExoMediaCrypto> acquireSession(Looper looper, DrmInitData drmInitData) {
            return new ErrorStateDrmSession(new DrmSession.DrmSessionException(new UnsupportedDrmException(1)));
        }
    };

    default DrmSession<T> acquirePlaceholderSession(Looper looper, int i) {
        return null;
    }

    DrmSession<T> acquireSession(Looper looper, DrmInitData drmInitData);

    boolean canAcquireSession(DrmInitData drmInitData);

    Class<? extends ExoMediaCrypto> getExoMediaCryptoType(DrmInitData drmInitData);

    default void prepare() {
    }

    default void release() {
    }

    static <T extends ExoMediaCrypto> DrmSessionManager<T> getDummyDrmSessionManager() {
        return (DrmSessionManager<T>) DUMMY;
    }
}