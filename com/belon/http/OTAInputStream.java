package com.belon.http;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
class OTAInputStream extends InputStream {
    private static final String TAG = "OTAInputStream";
    protected byte[] data;
    protected int ptr = 0;
    protected int mark = 0;

    public OTAInputStream(byte[] bArr) {
        this.data = bArr;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return this.data.length - this.ptr;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int i = this.ptr;
        byte[] bArr = this.data;
        if (i >= bArr.length) {
            return -1;
        }
        this.ptr = i + 1;
        return bArr[i];
    }

    @Override // java.io.InputStream
    public synchronized void mark(int i) {
        this.mark = i;
    }

    @Override // java.io.InputStream
    public synchronized void reset() throws IOException {
        int i = this.mark;
        if (i < 0 || i >= this.data.length) {
            throw new IOException("标识不对");
        }
        this.ptr = i;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.ptr >= this.data.length || i2 < 0) {
            return -1;
        }
        if (i2 == 0) {
            return 0;
        }
        if (available() < i2) {
            i2 = available();
        }
        System.arraycopy(this.data, this.ptr, bArr, i, i2);
        this.ptr += i2;
        return i2;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
    }
}