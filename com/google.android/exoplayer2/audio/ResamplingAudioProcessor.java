package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.audio.AudioProcessor;

/* loaded from: classes.dex */
final class ResamplingAudioProcessor extends BaseAudioProcessor {
    ResamplingAudioProcessor() {
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    public AudioProcessor.AudioFormat onConfigure(AudioProcessor.AudioFormat audioFormat) throws AudioProcessor.UnhandledAudioFormatException {
        int i = audioFormat.encoding;
        if (i == 3 || i == 2 || i == 268435456 || i == 536870912 || i == 805306368) {
            return i != 2 ? new AudioProcessor.AudioFormat(audioFormat.sampleRate, audioFormat.channelCount, 2) : AudioProcessor.AudioFormat.NOT_SET;
        }
        throw new AudioProcessor.UnhandledAudioFormatException(audioFormat);
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0083 A[ADDED_TO_REGION, LOOP:3: B:29:0x0083->B:30:0x0085, LOOP_START, PHI: r0
      0x0083: PHI (r0v1 int) = (r0v0 int), (r0v2 int) binds: [B:12:0x0032, B:30:0x0085] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // com.google.android.exoplayer2.audio.AudioProcessor
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void queueInput(java.nio.ByteBuffer r9) {
        /*
            r8 = this;
            int r0 = r9.position()
            int r1 = r9.limit()
            int r2 = r1 - r0
            com.google.android.exoplayer2.audio.AudioProcessor$AudioFormat r3 = r8.inputAudioFormat
            int r3 = r3.encoding
            r4 = 805306368(0x30000000, float:4.656613E-10)
            r5 = 536870912(0x20000000, float:1.0842022E-19)
            r6 = 268435456(0x10000000, float:2.524355E-29)
            r7 = 3
            if (r3 == r7) goto L28
            if (r3 == r6) goto L2a
            if (r3 == r5) goto L26
            if (r3 != r4) goto L20
            int r2 = r2 / 2
            goto L2a
        L20:
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException
            r9.<init>()
            throw r9
        L26:
            int r2 = r2 / 3
        L28:
            int r2 = r2 * 2
        L2a:
            java.nio.ByteBuffer r2 = r8.replaceOutputBuffer(r2)
            com.google.android.exoplayer2.audio.AudioProcessor$AudioFormat r3 = r8.inputAudioFormat
            int r3 = r3.encoding
            if (r3 == r7) goto L83
            if (r3 == r6) goto L6e
            if (r3 == r5) goto L57
            if (r3 != r4) goto L51
        L3a:
            if (r0 >= r1) goto L98
            int r3 = r0 + 2
            byte r3 = r9.get(r3)
            r2.put(r3)
            int r3 = r0 + 3
            byte r3 = r9.get(r3)
            r2.put(r3)
            int r0 = r0 + 4
            goto L3a
        L51:
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException
            r9.<init>()
            throw r9
        L57:
            if (r0 >= r1) goto L98
            int r3 = r0 + 1
            byte r3 = r9.get(r3)
            r2.put(r3)
            int r3 = r0 + 2
            byte r3 = r9.get(r3)
            r2.put(r3)
            int r0 = r0 + 3
            goto L57
        L6e:
            if (r0 >= r1) goto L98
            int r3 = r0 + 1
            byte r3 = r9.get(r3)
            r2.put(r3)
            byte r3 = r9.get(r0)
            r2.put(r3)
            int r0 = r0 + 2
            goto L6e
        L83:
            if (r0 >= r1) goto L98
            r3 = 0
            r2.put(r3)
            byte r3 = r9.get(r0)
            r3 = r3 & 255(0xff, float:3.57E-43)
            int r3 = r3 + (-128)
            byte r3 = (byte) r3
            r2.put(r3)
            int r0 = r0 + 1
            goto L83
        L98:
            int r0 = r9.limit()
            r9.position(r0)
            r2.flip()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.audio.ResamplingAudioProcessor.queueInput(java.nio.ByteBuffer):void");
    }
}