package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class DataSpec {
    public static final int FLAG_ALLOW_CACHE_FRAGMENTATION = 4;
    public static final int FLAG_ALLOW_GZIP = 1;
    public static final int FLAG_DONT_CACHE_IF_LENGTH_UNKNOWN = 2;
    public static final int HTTP_METHOD_GET = 1;
    public static final int HTTP_METHOD_HEAD = 3;
    public static final int HTTP_METHOD_POST = 2;
    public final long absoluteStreamPosition;
    public final int flags;
    public final byte[] httpBody;
    public final int httpMethod;
    public final Map<String, String> httpRequestHeaders;
    public final String key;
    public final long length;
    public final long position;
    public final Uri uri;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface HttpMethod {
    }

    private static int inferHttpMethod(byte[] bArr) {
        return bArr != null ? 2 : 1;
    }

    public DataSpec(Uri uri) {
        this(uri, 0);
    }

    public DataSpec(Uri uri, int i) {
        this(uri, 0L, -1L, null, i);
    }

    public DataSpec(Uri uri, long j, long j2, String str) {
        this(uri, j, j, j2, str, 0);
    }

    public DataSpec(Uri uri, long j, long j2, String str, int i) {
        this(uri, j, j, j2, str, i);
    }

    public DataSpec(Uri uri, long j, long j2, String str, int i, Map<String, String> map) {
        this(uri, inferHttpMethod(null), null, j, j, j2, str, i, map);
    }

    public DataSpec(Uri uri, long j, long j2, long j3, String str, int i) {
        this(uri, null, j, j2, j3, str, i);
    }

    public DataSpec(Uri uri, byte[] bArr, long j, long j2, long j3, String str, int i) {
        this(uri, inferHttpMethod(bArr), bArr, j, j2, j3, str, i);
    }

    public DataSpec(Uri uri, int i, byte[] bArr, long j, long j2, long j3, String str, int i2) {
        this(uri, i, bArr, j, j2, j3, str, i2, Collections.emptyMap());
    }

    public DataSpec(Uri uri, int i, byte[] bArr, long j, long j2, long j3, String str, int i2, Map<String, String> map) {
        byte[] bArr2 = bArr;
        boolean z = true;
        Assertions.checkArgument(j >= 0);
        Assertions.checkArgument(j2 >= 0);
        if (j3 <= 0 && j3 != -1) {
            z = false;
        }
        Assertions.checkArgument(z);
        this.uri = uri;
        this.httpMethod = i;
        this.httpBody = (bArr2 == null || bArr2.length == 0) ? null : bArr2;
        this.absoluteStreamPosition = j;
        this.position = j2;
        this.length = j3;
        this.key = str;
        this.flags = i2;
        this.httpRequestHeaders = Collections.unmodifiableMap(new HashMap(map));
    }

    public boolean isFlagSet(int i) {
        return (this.flags & i) == i;
    }

    public String toString() {
        return "DataSpec[" + getHttpMethodString() + " " + this.uri + ", " + Arrays.toString(this.httpBody) + ", " + this.absoluteStreamPosition + ", " + this.position + ", " + this.length + ", " + this.key + ", " + this.flags + "]";
    }

    public final String getHttpMethodString() {
        return getStringForHttpMethod(this.httpMethod);
    }

    public static String getStringForHttpMethod(int i) {
        if (i == 1) {
            return "GET";
        }
        if (i == 2) {
            return "POST";
        }
        if (i == 3) {
            return "HEAD";
        }
        throw new AssertionError(i);
    }

    public DataSpec subrange(long j) {
        long j2 = this.length;
        return subrange(j, j2 != -1 ? j2 - j : -1L);
    }

    public DataSpec subrange(long j, long j2) {
        return (j == 0 && this.length == j2) ? this : new DataSpec(this.uri, this.httpMethod, this.httpBody, this.absoluteStreamPosition + j, this.position + j, j2, this.key, this.flags, this.httpRequestHeaders);
    }

    public DataSpec withUri(Uri uri) {
        return new DataSpec(uri, this.httpMethod, this.httpBody, this.absoluteStreamPosition, this.position, this.length, this.key, this.flags, this.httpRequestHeaders);
    }

    public DataSpec withRequestHeaders(Map<String, String> map) {
        return new DataSpec(this.uri, this.httpMethod, this.httpBody, this.absoluteStreamPosition, this.position, this.length, this.key, this.flags, map);
    }

    public DataSpec withAdditionalHeaders(Map<String, String> map) {
        HashMap hashMap = new HashMap(this.httpRequestHeaders);
        hashMap.putAll(map);
        return new DataSpec(this.uri, this.httpMethod, this.httpBody, this.absoluteStreamPosition, this.position, this.length, this.key, this.flags, hashMap);
    }
}