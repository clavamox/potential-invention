package com.google.android.exoplayer2.metadata.id3;

import com.google.android.exoplayer2.metadata.Metadata;

/* loaded from: classes.dex */
public abstract class Id3Frame implements Metadata.Entry {
    public final String id;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Id3Frame(String str) {
        this.id = str;
    }

    public String toString() {
        return this.id;
    }
}