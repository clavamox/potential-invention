package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.decoder.CryptoInfo;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.upstream.Allocation;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/* loaded from: classes.dex */
class SampleDataQueue {
    private static final int INITIAL_SCRATCH_SIZE = 32;
    private final int allocationLength;
    private final Allocator allocator;
    private AllocationNode firstAllocationNode;
    private AllocationNode readAllocationNode;
    private final ParsableByteArray scratch;
    private long totalBytesWritten;
    private AllocationNode writeAllocationNode;

    public SampleDataQueue(Allocator allocator) {
        this.allocator = allocator;
        int individualAllocationLength = allocator.getIndividualAllocationLength();
        this.allocationLength = individualAllocationLength;
        this.scratch = new ParsableByteArray(32);
        AllocationNode allocationNode = new AllocationNode(0L, individualAllocationLength);
        this.firstAllocationNode = allocationNode;
        this.readAllocationNode = allocationNode;
        this.writeAllocationNode = allocationNode;
    }

    public void reset() {
        clearAllocationNodes(this.firstAllocationNode);
        AllocationNode allocationNode = new AllocationNode(0L, this.allocationLength);
        this.firstAllocationNode = allocationNode;
        this.readAllocationNode = allocationNode;
        this.writeAllocationNode = allocationNode;
        this.totalBytesWritten = 0L;
        this.allocator.trim();
    }

    public void discardUpstreamSampleBytes(long j) {
        this.totalBytesWritten = j;
        if (j == 0 || j == this.firstAllocationNode.startPosition) {
            clearAllocationNodes(this.firstAllocationNode);
            AllocationNode allocationNode = new AllocationNode(this.totalBytesWritten, this.allocationLength);
            this.firstAllocationNode = allocationNode;
            this.readAllocationNode = allocationNode;
            this.writeAllocationNode = allocationNode;
            return;
        }
        AllocationNode allocationNode2 = this.firstAllocationNode;
        while (this.totalBytesWritten > allocationNode2.endPosition) {
            allocationNode2 = allocationNode2.next;
        }
        AllocationNode allocationNode3 = allocationNode2.next;
        clearAllocationNodes(allocationNode3);
        allocationNode2.next = new AllocationNode(allocationNode2.endPosition, this.allocationLength);
        this.writeAllocationNode = this.totalBytesWritten == allocationNode2.endPosition ? allocationNode2.next : allocationNode2;
        if (this.readAllocationNode == allocationNode3) {
            this.readAllocationNode = allocationNode2.next;
        }
    }

    public void rewind() {
        this.readAllocationNode = this.firstAllocationNode;
    }

    public void readToBuffer(DecoderInputBuffer decoderInputBuffer, SampleQueue.SampleExtrasHolder sampleExtrasHolder) {
        if (decoderInputBuffer.isEncrypted()) {
            readEncryptionData(decoderInputBuffer, sampleExtrasHolder);
        }
        if (decoderInputBuffer.hasSupplementalData()) {
            this.scratch.reset(4);
            readData(sampleExtrasHolder.offset, this.scratch.data, 4);
            int readUnsignedIntToInt = this.scratch.readUnsignedIntToInt();
            sampleExtrasHolder.offset += 4;
            sampleExtrasHolder.size -= 4;
            decoderInputBuffer.ensureSpaceForWrite(readUnsignedIntToInt);
            readData(sampleExtrasHolder.offset, decoderInputBuffer.data, readUnsignedIntToInt);
            sampleExtrasHolder.offset += readUnsignedIntToInt;
            sampleExtrasHolder.size -= readUnsignedIntToInt;
            decoderInputBuffer.resetSupplementalData(sampleExtrasHolder.size);
            readData(sampleExtrasHolder.offset, decoderInputBuffer.supplementalData, sampleExtrasHolder.size);
            return;
        }
        decoderInputBuffer.ensureSpaceForWrite(sampleExtrasHolder.size);
        readData(sampleExtrasHolder.offset, decoderInputBuffer.data, sampleExtrasHolder.size);
    }

    public void discardDownstreamTo(long j) {
        if (j == -1) {
            return;
        }
        while (j >= this.firstAllocationNode.endPosition) {
            this.allocator.release(this.firstAllocationNode.allocation);
            this.firstAllocationNode = this.firstAllocationNode.clear();
        }
        if (this.readAllocationNode.startPosition < this.firstAllocationNode.startPosition) {
            this.readAllocationNode = this.firstAllocationNode;
        }
    }

    public long getTotalBytesWritten() {
        return this.totalBytesWritten;
    }

    public int sampleData(ExtractorInput extractorInput, int i, boolean z) throws IOException, InterruptedException {
        int read = extractorInput.read(this.writeAllocationNode.allocation.data, this.writeAllocationNode.translateOffset(this.totalBytesWritten), preAppend(i));
        if (read != -1) {
            postAppend(read);
            return read;
        }
        if (z) {
            return -1;
        }
        throw new EOFException();
    }

    public void sampleData(ParsableByteArray parsableByteArray, int i) {
        while (i > 0) {
            int preAppend = preAppend(i);
            parsableByteArray.readBytes(this.writeAllocationNode.allocation.data, this.writeAllocationNode.translateOffset(this.totalBytesWritten), preAppend);
            i -= preAppend;
            postAppend(preAppend);
        }
    }

    private void readEncryptionData(DecoderInputBuffer decoderInputBuffer, SampleQueue.SampleExtrasHolder sampleExtrasHolder) {
        long j = sampleExtrasHolder.offset;
        int i = 1;
        this.scratch.reset(1);
        readData(j, this.scratch.data, 1);
        long j2 = j + 1;
        byte b = this.scratch.data[0];
        boolean z = (b & 128) != 0;
        int i2 = b & Byte.MAX_VALUE;
        CryptoInfo cryptoInfo = decoderInputBuffer.cryptoInfo;
        if (cryptoInfo.iv == null) {
            cryptoInfo.iv = new byte[16];
        } else {
            Arrays.fill(cryptoInfo.iv, (byte) 0);
        }
        readData(j2, cryptoInfo.iv, i2);
        long j3 = j2 + i2;
        if (z) {
            this.scratch.reset(2);
            readData(j3, this.scratch.data, 2);
            j3 += 2;
            i = this.scratch.readUnsignedShort();
        }
        int i3 = i;
        int[] iArr = cryptoInfo.numBytesOfClearData;
        if (iArr == null || iArr.length < i3) {
            iArr = new int[i3];
        }
        int[] iArr2 = iArr;
        int[] iArr3 = cryptoInfo.numBytesOfEncryptedData;
        if (iArr3 == null || iArr3.length < i3) {
            iArr3 = new int[i3];
        }
        int[] iArr4 = iArr3;
        if (z) {
            int i4 = i3 * 6;
            this.scratch.reset(i4);
            readData(j3, this.scratch.data, i4);
            j3 += i4;
            this.scratch.setPosition(0);
            for (int i5 = 0; i5 < i3; i5++) {
                iArr2[i5] = this.scratch.readUnsignedShort();
                iArr4[i5] = this.scratch.readUnsignedIntToInt();
            }
        } else {
            iArr2[0] = 0;
            iArr4[0] = sampleExtrasHolder.size - ((int) (j3 - sampleExtrasHolder.offset));
        }
        TrackOutput.CryptoData cryptoData = sampleExtrasHolder.cryptoData;
        cryptoInfo.set(i3, iArr2, iArr4, cryptoData.encryptionKey, cryptoInfo.iv, cryptoData.cryptoMode, cryptoData.encryptedBlocks, cryptoData.clearBlocks);
        int i6 = (int) (j3 - sampleExtrasHolder.offset);
        sampleExtrasHolder.offset += i6;
        sampleExtrasHolder.size -= i6;
    }

    private void readData(long j, ByteBuffer byteBuffer, int i) {
        advanceReadTo(j);
        while (i > 0) {
            int min = Math.min(i, (int) (this.readAllocationNode.endPosition - j));
            byteBuffer.put(this.readAllocationNode.allocation.data, this.readAllocationNode.translateOffset(j), min);
            i -= min;
            j += min;
            if (j == this.readAllocationNode.endPosition) {
                this.readAllocationNode = this.readAllocationNode.next;
            }
        }
    }

    private void readData(long j, byte[] bArr, int i) {
        advanceReadTo(j);
        int i2 = i;
        while (i2 > 0) {
            int min = Math.min(i2, (int) (this.readAllocationNode.endPosition - j));
            System.arraycopy(this.readAllocationNode.allocation.data, this.readAllocationNode.translateOffset(j), bArr, i - i2, min);
            i2 -= min;
            j += min;
            if (j == this.readAllocationNode.endPosition) {
                this.readAllocationNode = this.readAllocationNode.next;
            }
        }
    }

    private void advanceReadTo(long j) {
        while (j >= this.readAllocationNode.endPosition) {
            this.readAllocationNode = this.readAllocationNode.next;
        }
    }

    private void clearAllocationNodes(AllocationNode allocationNode) {
        if (allocationNode.wasInitialized) {
            boolean z = this.writeAllocationNode.wasInitialized;
            int i = (z ? 1 : 0) + (((int) (this.writeAllocationNode.startPosition - allocationNode.startPosition)) / this.allocationLength);
            Allocation[] allocationArr = new Allocation[i];
            for (int i2 = 0; i2 < i; i2++) {
                allocationArr[i2] = allocationNode.allocation;
                allocationNode = allocationNode.clear();
            }
            this.allocator.release(allocationArr);
        }
    }

    private int preAppend(int i) {
        if (!this.writeAllocationNode.wasInitialized) {
            this.writeAllocationNode.initialize(this.allocator.allocate(), new AllocationNode(this.writeAllocationNode.endPosition, this.allocationLength));
        }
        return Math.min(i, (int) (this.writeAllocationNode.endPosition - this.totalBytesWritten));
    }

    private void postAppend(int i) {
        long j = this.totalBytesWritten + i;
        this.totalBytesWritten = j;
        if (j == this.writeAllocationNode.endPosition) {
            this.writeAllocationNode = this.writeAllocationNode.next;
        }
    }

    private static final class AllocationNode {
        public Allocation allocation;
        public final long endPosition;
        public AllocationNode next;
        public final long startPosition;
        public boolean wasInitialized;

        public AllocationNode(long j, int i) {
            this.startPosition = j;
            this.endPosition = j + i;
        }

        public void initialize(Allocation allocation, AllocationNode allocationNode) {
            this.allocation = allocation;
            this.next = allocationNode;
            this.wasInitialized = true;
        }

        public int translateOffset(long j) {
            return ((int) (j - this.startPosition)) + this.allocation.offset;
        }

        public AllocationNode clear() {
            this.allocation = null;
            AllocationNode allocationNode = this.next;
            this.next = null;
            return allocationNode;
        }
    }
}