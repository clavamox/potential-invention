package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.upstream.DataSpec;
import java.util.List;

/* loaded from: classes.dex */
public final class MediaChunkListIterator extends BaseMediaChunkIterator {
    private final List<? extends MediaChunk> chunks;
    private final boolean reverseOrder;

    public MediaChunkListIterator(List<? extends MediaChunk> list, boolean z) {
        super(0L, list.size() - 1);
        this.chunks = list;
        this.reverseOrder = z;
    }

    @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
    public DataSpec getDataSpec() {
        return getCurrentChunk().dataSpec;
    }

    @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
    public long getChunkStartTimeUs() {
        return getCurrentChunk().startTimeUs;
    }

    @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
    public long getChunkEndTimeUs() {
        return getCurrentChunk().endTimeUs;
    }

    private MediaChunk getCurrentChunk() {
        int currentIndex = (int) super.getCurrentIndex();
        if (this.reverseOrder) {
            currentIndex = (this.chunks.size() - 1) - currentIndex;
        }
        return this.chunks.get(currentIndex);
    }
}