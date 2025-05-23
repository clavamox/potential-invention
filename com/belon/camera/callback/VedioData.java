package com.belon.camera.callback;

/* loaded from: classes.dex */
public class VedioData {
    private byte[] data;
    private int len;

    public VedioData(byte[] bArr, int i) {
        this.data = bArr;
        this.len = i;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] bArr) {
        this.data = bArr;
    }

    public int getLen() {
        return this.len;
    }

    public void setLen(int i) {
        this.len = i;
    }
}