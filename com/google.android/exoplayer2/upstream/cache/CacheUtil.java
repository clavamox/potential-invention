package com.google.android.exoplayer2.upstream.cache;

import android.net.Uri;
import android.util.Pair;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceException;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public final class CacheUtil {
    public static final int DEFAULT_BUFFER_SIZE_BYTES = 131072;
    public static final CacheKeyFactory DEFAULT_CACHE_KEY_FACTORY = new CacheKeyFactory() { // from class: com.google.android.exoplayer2.upstream.cache.CacheUtil$$ExternalSyntheticLambda0
        @Override // com.google.android.exoplayer2.upstream.cache.CacheKeyFactory
        public final String buildCacheKey(DataSpec dataSpec) {
            return CacheUtil.lambda$static$0(dataSpec);
        }
    };

    public interface ProgressListener {
        void onProgress(long j, long j2, long j3);
    }

    static /* synthetic */ String lambda$static$0(DataSpec dataSpec) {
        return dataSpec.key != null ? dataSpec.key : generateKey(dataSpec.uri);
    }

    public static String generateKey(Uri uri) {
        return uri.toString();
    }

    public static Pair<Long, Long> getCached(DataSpec dataSpec, Cache cache, CacheKeyFactory cacheKeyFactory) {
        String buildCacheKey = buildCacheKey(dataSpec, cacheKeyFactory);
        long j = dataSpec.absoluteStreamPosition;
        long requestLength = getRequestLength(dataSpec, cache, buildCacheKey);
        long j2 = j;
        long j3 = requestLength;
        long j4 = 0;
        while (j3 != 0) {
            long cachedLength = cache.getCachedLength(buildCacheKey, j2, j3 != -1 ? j3 : Long.MAX_VALUE);
            if (cachedLength <= 0) {
                cachedLength = -cachedLength;
                if (cachedLength == Long.MAX_VALUE) {
                    break;
                }
            } else {
                j4 += cachedLength;
            }
            j2 += cachedLength;
            if (j3 == -1) {
                cachedLength = 0;
            }
            j3 -= cachedLength;
        }
        return Pair.create(Long.valueOf(requestLength), Long.valueOf(j4));
    }

    public static void cache(DataSpec dataSpec, Cache cache, CacheKeyFactory cacheKeyFactory, DataSource dataSource, ProgressListener progressListener, AtomicBoolean atomicBoolean) throws IOException, InterruptedException {
        cache(dataSpec, cache, cacheKeyFactory, new CacheDataSource(cache, dataSource), new byte[131072], null, 0, progressListener, atomicBoolean, false);
    }

    public static void cache(DataSpec dataSpec, Cache cache, CacheKeyFactory cacheKeyFactory, CacheDataSource cacheDataSource, byte[] bArr, PriorityTaskManager priorityTaskManager, int i, ProgressListener progressListener, AtomicBoolean atomicBoolean, boolean z) throws IOException, InterruptedException {
        long requestLength;
        ProgressNotifier progressNotifier;
        Assertions.checkNotNull(cacheDataSource);
        Assertions.checkNotNull(bArr);
        String buildCacheKey = buildCacheKey(dataSpec, cacheKeyFactory);
        if (progressListener != null) {
            progressNotifier = new ProgressNotifier(progressListener);
            Pair<Long, Long> cached = getCached(dataSpec, cache, cacheKeyFactory);
            progressNotifier.init(((Long) cached.first).longValue(), ((Long) cached.second).longValue());
            requestLength = ((Long) cached.first).longValue();
        } else {
            requestLength = getRequestLength(dataSpec, cache, buildCacheKey);
            progressNotifier = null;
        }
        ProgressNotifier progressNotifier2 = progressNotifier;
        long j = dataSpec.absoluteStreamPosition;
        boolean z2 = requestLength == -1;
        long j2 = requestLength;
        long j3 = j;
        while (j2 != 0) {
            throwExceptionIfInterruptedOrCancelled(atomicBoolean);
            long cachedLength = cache.getCachedLength(buildCacheKey, j3, z2 ? Long.MAX_VALUE : j2);
            if (cachedLength <= 0) {
                long j4 = -cachedLength;
                long j5 = j4 == Long.MAX_VALUE ? -1L : j4;
                if (readAndDiscard(dataSpec, j3, j5, cacheDataSource, bArr, priorityTaskManager, i, progressNotifier2, j5 == j2, atomicBoolean) < j4) {
                    if (z && !z2) {
                        throw new EOFException();
                    }
                    return;
                }
                cachedLength = j4;
            }
            j3 += cachedLength;
            if (!z2) {
                j2 -= cachedLength;
            }
        }
    }

    private static long getRequestLength(DataSpec dataSpec, Cache cache, String str) {
        if (dataSpec.length != -1) {
            return dataSpec.length;
        }
        long contentLength = ContentMetadata.getContentLength(cache.getContentMetadata(str));
        if (contentLength == -1) {
            return -1L;
        }
        return contentLength - dataSpec.absoluteStreamPosition;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x002d, code lost:
    
        r0 = true;
     */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0043 A[Catch: all -> 0x002f, PriorityTooLowException -> 0x0087, TryCatch #3 {PriorityTooLowException -> 0x0087, all -> 0x002f, blocks: (B:11:0x0025, B:14:0x0043, B:19:0x0053, B:23:0x005b, B:25:0x0060, B:26:0x006b, B:36:0x0075, B:28:0x0079, B:30:0x007d, B:40:0x006a, B:45:0x0034, B:47:0x003a, B:49:0x003e), top: B:10:0x0025 }] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x005b A[Catch: all -> 0x002f, PriorityTooLowException -> 0x0087, TryCatch #3 {PriorityTooLowException -> 0x0087, all -> 0x002f, blocks: (B:11:0x0025, B:14:0x0043, B:19:0x0053, B:23:0x005b, B:25:0x0060, B:26:0x006b, B:36:0x0075, B:28:0x0079, B:30:0x007d, B:40:0x006a, B:45:0x0034, B:47:0x003a, B:49:0x003e), top: B:10:0x0025 }] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x008d A[EDGE_INSN: B:41:0x008d->B:37:0x008d BREAK  A[LOOP:1: B:21:0x0057->B:32:0x0080], SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static long readAndDiscard(com.google.android.exoplayer2.upstream.DataSpec r16, long r17, long r19, com.google.android.exoplayer2.upstream.DataSource r21, byte[] r22, com.google.android.exoplayer2.util.PriorityTaskManager r23, int r24, com.google.android.exoplayer2.upstream.cache.CacheUtil.ProgressNotifier r25, boolean r26, java.util.concurrent.atomic.AtomicBoolean r27) throws java.io.IOException, java.lang.InterruptedException {
        /*
            r1 = r16
            r2 = r21
            r3 = r22
            r4 = r25
            long r5 = r1.absoluteStreamPosition
            long r5 = r17 - r5
            r7 = -1
            int r0 = (r19 > r7 ? 1 : (r19 == r7 ? 0 : -1))
            if (r0 == 0) goto L15
            long r9 = r5 + r19
            goto L16
        L15:
            r9 = r7
        L16:
            r11 = r5
        L17:
            if (r23 == 0) goto L1c
            r23.proceed(r24)
        L1c:
            throwExceptionIfInterruptedOrCancelled(r27)
            int r13 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r13 == 0) goto L3f
            long r14 = r9 - r11
            com.google.android.exoplayer2.upstream.DataSpec r0 = r1.subrange(r11, r14)     // Catch: java.lang.Throwable -> L2f java.io.IOException -> L31 com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            long r14 = r2.open(r0)     // Catch: java.lang.Throwable -> L2f java.io.IOException -> L31 com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            r0 = 1
            goto L41
        L2f:
            r0 = move-exception
            goto L83
        L31:
            r0 = move-exception
            if (r26 == 0) goto L3e
            boolean r14 = isCausedByPositionOutOfRange(r0)     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            if (r14 == 0) goto L3e
            com.google.android.exoplayer2.util.Util.closeQuietly(r21)     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            goto L3f
        L3e:
            throw r0     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
        L3f:
            r14 = r7
            r0 = 0
        L41:
            if (r0 != 0) goto L4b
            com.google.android.exoplayer2.upstream.DataSpec r0 = r1.subrange(r11, r7)     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            long r14 = r2.open(r0)     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
        L4b:
            if (r26 == 0) goto L57
            if (r4 == 0) goto L57
            int r0 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1))
            if (r0 == 0) goto L57
            long r14 = r14 + r11
            r4.onRequestLengthResolved(r14)     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
        L57:
            int r0 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r0 == 0) goto L8d
            throwExceptionIfInterruptedOrCancelled(r27)     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            if (r13 == 0) goto L6a
            int r0 = r3.length     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            long r14 = (long) r0     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            long r7 = r9 - r11
            long r7 = java.lang.Math.min(r14, r7)     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            int r0 = (int) r7     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            goto L6b
        L6a:
            int r0 = r3.length     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
        L6b:
            r7 = 0
            int r0 = r2.read(r3, r7, r0)     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            r8 = -1
            if (r0 != r8) goto L79
            if (r4 == 0) goto L8d
            r4.onRequestLengthResolved(r11)     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            goto L8d
        L79:
            long r14 = (long) r0     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
            long r11 = r11 + r14
            if (r4 == 0) goto L80
            r4.onBytesCached(r14)     // Catch: java.lang.Throwable -> L2f com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException -> L87
        L80:
            r7 = -1
            goto L57
        L83:
            com.google.android.exoplayer2.util.Util.closeQuietly(r21)
            throw r0
        L87:
            com.google.android.exoplayer2.util.Util.closeQuietly(r21)
            r7 = -1
            goto L17
        L8d:
            long r11 = r11 - r5
            com.google.android.exoplayer2.util.Util.closeQuietly(r21)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.CacheUtil.readAndDiscard(com.google.android.exoplayer2.upstream.DataSpec, long, long, com.google.android.exoplayer2.upstream.DataSource, byte[], com.google.android.exoplayer2.util.PriorityTaskManager, int, com.google.android.exoplayer2.upstream.cache.CacheUtil$ProgressNotifier, boolean, java.util.concurrent.atomic.AtomicBoolean):long");
    }

    public static void remove(DataSpec dataSpec, Cache cache, CacheKeyFactory cacheKeyFactory) {
        remove(cache, buildCacheKey(dataSpec, cacheKeyFactory));
    }

    public static void remove(Cache cache, String str) {
        Iterator<CacheSpan> it = cache.getCachedSpans(str).iterator();
        while (it.hasNext()) {
            try {
                cache.removeSpan(it.next());
            } catch (Cache.CacheException unused) {
            }
        }
    }

    static boolean isCausedByPositionOutOfRange(IOException iOException) {
        for (IOException iOException2 = iOException; iOException2 != null; iOException2 = iOException2.getCause()) {
            if ((iOException2 instanceof DataSourceException) && ((DataSourceException) iOException2).reason == 0) {
                return true;
            }
        }
        return false;
    }

    private static String buildCacheKey(DataSpec dataSpec, CacheKeyFactory cacheKeyFactory) {
        if (cacheKeyFactory == null) {
            cacheKeyFactory = DEFAULT_CACHE_KEY_FACTORY;
        }
        return cacheKeyFactory.buildCacheKey(dataSpec);
    }

    private static void throwExceptionIfInterruptedOrCancelled(AtomicBoolean atomicBoolean) throws InterruptedException {
        if (Thread.interrupted() || (atomicBoolean != null && atomicBoolean.get())) {
            throw new InterruptedException();
        }
    }

    private CacheUtil() {
    }

    private static final class ProgressNotifier {
        private long bytesCached;
        private final ProgressListener listener;
        private long requestLength;

        public ProgressNotifier(ProgressListener progressListener) {
            this.listener = progressListener;
        }

        public void init(long j, long j2) {
            this.requestLength = j;
            this.bytesCached = j2;
            this.listener.onProgress(j, j2, 0L);
        }

        public void onRequestLengthResolved(long j) {
            if (this.requestLength != -1 || j == -1) {
                return;
            }
            this.requestLength = j;
            this.listener.onProgress(j, this.bytesCached, 0L);
        }

        public void onBytesCached(long j) {
            long j2 = this.bytesCached + j;
            this.bytesCached = j2;
            this.listener.onProgress(this.requestLength, j2, j);
        }
    }
}