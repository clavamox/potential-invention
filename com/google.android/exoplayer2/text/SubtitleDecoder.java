package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.decoder.Decoder;

/* loaded from: classes.dex */
public interface SubtitleDecoder extends Decoder<SubtitleInputBuffer, SubtitleOutputBuffer, SubtitleDecoderException> {
    void setPositionUs(long j);
}