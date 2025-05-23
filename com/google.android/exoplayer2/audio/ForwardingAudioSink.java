package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.audio.AudioSink;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ForwardingAudioSink implements AudioSink {
    private final AudioSink sink;

    public ForwardingAudioSink(AudioSink audioSink) {
        this.sink = audioSink;
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setListener(AudioSink.Listener listener) {
        this.sink.setListener(listener);
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public boolean supportsOutput(int i, int i2) {
        return this.sink.supportsOutput(i, i2);
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public long getCurrentPositionUs(boolean z) {
        return this.sink.getCurrentPositionUs(z);
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void configure(int i, int i2, int i3, int i4, int[] iArr, int i5, int i6) throws AudioSink.ConfigurationException {
        this.sink.configure(i, i2, i3, i4, iArr, i5, i6);
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void play() {
        this.sink.play();
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void handleDiscontinuity() {
        this.sink.handleDiscontinuity();
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public boolean handleBuffer(ByteBuffer byteBuffer, long j) throws AudioSink.InitializationException, AudioSink.WriteException {
        return this.sink.handleBuffer(byteBuffer, j);
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void playToEndOfStream() throws AudioSink.WriteException {
        this.sink.playToEndOfStream();
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public boolean isEnded() {
        return this.sink.isEnded();
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public boolean hasPendingData() {
        return this.sink.hasPendingData();
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setPlaybackParameters(PlaybackParameters playbackParameters) {
        this.sink.setPlaybackParameters(playbackParameters);
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public PlaybackParameters getPlaybackParameters() {
        return this.sink.getPlaybackParameters();
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setAudioAttributes(AudioAttributes audioAttributes) {
        this.sink.setAudioAttributes(audioAttributes);
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setAudioSessionId(int i) {
        this.sink.setAudioSessionId(i);
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setAuxEffectInfo(AuxEffectInfo auxEffectInfo) {
        this.sink.setAuxEffectInfo(auxEffectInfo);
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void enableTunnelingV21(int i) {
        this.sink.enableTunnelingV21(i);
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void disableTunneling() {
        this.sink.disableTunneling();
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void setVolume(float f) {
        this.sink.setVolume(f);
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void pause() {
        this.sink.pause();
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void flush() {
        this.sink.flush();
    }

    @Override // com.google.android.exoplayer2.audio.AudioSink
    public void reset() {
        this.sink.reset();
    }
}