package com.google.android.exoplayer2.video;

import android.os.Handler;
import android.os.SystemClock;
import android.view.Surface;
import com.google.android.exoplayer2.BaseRenderer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.TimedValueQueue;
import com.google.android.exoplayer2.util.TraceUtil;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

/* loaded from: classes.dex */
public abstract class SimpleDecoderVideoRenderer extends BaseRenderer {
    private static final int REINITIALIZATION_STATE_NONE = 0;
    private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
    private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
    private final long allowedJoiningTimeMs;
    private int buffersInCodecCount;
    private int consecutiveDroppedFrameCount;
    private SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException> decoder;
    protected DecoderCounters decoderCounters;
    private DrmSession<ExoMediaCrypto> decoderDrmSession;
    private boolean decoderReceivedBuffers;
    private int decoderReinitializationState;
    private boolean drmResourcesAcquired;
    private final DrmSessionManager<ExoMediaCrypto> drmSessionManager;
    private long droppedFrameAccumulationStartTimeMs;
    private int droppedFrames;
    private final VideoRendererEventListener.EventDispatcher eventDispatcher;
    private final DecoderInputBuffer flagsOnlyBuffer;
    private final TimedValueQueue<Format> formatQueue;
    private long initialPositionUs;
    private VideoDecoderInputBuffer inputBuffer;
    private Format inputFormat;
    private boolean inputStreamEnded;
    private long joiningDeadlineMs;
    private long lastRenderTimeUs;
    private final int maxDroppedFramesToNotify;
    private VideoDecoderOutputBuffer outputBuffer;
    private VideoDecoderOutputBufferRenderer outputBufferRenderer;
    private Format outputFormat;
    private int outputMode;
    private boolean outputStreamEnded;
    private long outputStreamOffsetUs;
    private final boolean playClearSamplesWithoutKeys;
    private boolean renderedFirstFrame;
    private int reportedHeight;
    private int reportedWidth;
    private DrmSession<ExoMediaCrypto> sourceDrmSession;
    private Surface surface;
    private boolean waitingForFirstSampleInFormat;
    private boolean waitingForKeys;

    private static boolean isBufferLate(long j) {
        return j < -30000;
    }

    private static boolean isBufferVeryLate(long j) {
        return j < -500000;
    }

    protected abstract SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException> createDecoder(Format format, ExoMediaCrypto exoMediaCrypto) throws VideoDecoderException;

    protected void onQueueInputBuffer(VideoDecoderInputBuffer videoDecoderInputBuffer) {
    }

    protected abstract void renderOutputBufferToSurface(VideoDecoderOutputBuffer videoDecoderOutputBuffer, Surface surface) throws VideoDecoderException;

    protected abstract void setDecoderOutputMode(int i);

    protected abstract int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format);

    protected SimpleDecoderVideoRenderer(long j, Handler handler, VideoRendererEventListener videoRendererEventListener, int i, DrmSessionManager<ExoMediaCrypto> drmSessionManager, boolean z) {
        super(2);
        this.allowedJoiningTimeMs = j;
        this.maxDroppedFramesToNotify = i;
        this.drmSessionManager = drmSessionManager;
        this.playClearSamplesWithoutKeys = z;
        this.joiningDeadlineMs = C.TIME_UNSET;
        clearReportedVideoSize();
        this.formatQueue = new TimedValueQueue<>();
        this.flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
        this.eventDispatcher = new VideoRendererEventListener.EventDispatcher(handler, videoRendererEventListener);
        this.decoderReinitializationState = 0;
        this.outputMode = -1;
    }

    @Override // com.google.android.exoplayer2.RendererCapabilities
    public final int supportsFormat(Format format) {
        return supportsFormatInternal(this.drmSessionManager, format);
    }

    @Override // com.google.android.exoplayer2.Renderer
    public void render(long j, long j2) throws ExoPlaybackException {
        if (this.outputStreamEnded) {
            return;
        }
        if (this.inputFormat == null) {
            FormatHolder formatHolder = getFormatHolder();
            this.flagsOnlyBuffer.clear();
            int readSource = readSource(formatHolder, this.flagsOnlyBuffer, true);
            if (readSource != -5) {
                if (readSource == -4) {
                    Assertions.checkState(this.flagsOnlyBuffer.isEndOfStream());
                    this.inputStreamEnded = true;
                    this.outputStreamEnded = true;
                    return;
                }
                return;
            }
            onInputFormatChanged(formatHolder);
        }
        maybeInitDecoder();
        if (this.decoder != null) {
            try {
                TraceUtil.beginSection("drainAndFeed");
                while (drainOutputBuffer(j, j2)) {
                }
                while (feedInputBuffer()) {
                }
                TraceUtil.endSection();
                this.decoderCounters.ensureUpdated();
            } catch (VideoDecoderException e) {
                throw createRendererException(e, this.inputFormat);
            }
        }
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isEnded() {
        return this.outputStreamEnded;
    }

    @Override // com.google.android.exoplayer2.Renderer
    public boolean isReady() {
        if (this.waitingForKeys) {
            return false;
        }
        if (this.inputFormat != null && ((isSourceReady() || this.outputBuffer != null) && (this.renderedFirstFrame || !hasOutput()))) {
            this.joiningDeadlineMs = C.TIME_UNSET;
            return true;
        }
        if (this.joiningDeadlineMs == C.TIME_UNSET) {
            return false;
        }
        if (SystemClock.elapsedRealtime() < this.joiningDeadlineMs) {
            return true;
        }
        this.joiningDeadlineMs = C.TIME_UNSET;
        return false;
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onEnabled(boolean z) throws ExoPlaybackException {
        DrmSessionManager<ExoMediaCrypto> drmSessionManager = this.drmSessionManager;
        if (drmSessionManager != null && !this.drmResourcesAcquired) {
            this.drmResourcesAcquired = true;
            drmSessionManager.prepare();
        }
        DecoderCounters decoderCounters = new DecoderCounters();
        this.decoderCounters = decoderCounters;
        this.eventDispatcher.enabled(decoderCounters);
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onPositionReset(long j, boolean z) throws ExoPlaybackException {
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        clearRenderedFirstFrame();
        this.initialPositionUs = C.TIME_UNSET;
        this.consecutiveDroppedFrameCount = 0;
        if (this.decoder != null) {
            flushDecoder();
        }
        if (z) {
            setJoiningDeadlineMs();
        } else {
            this.joiningDeadlineMs = C.TIME_UNSET;
        }
        this.formatQueue.clear();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onStarted() {
        this.droppedFrames = 0;
        this.droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
        this.lastRenderTimeUs = SystemClock.elapsedRealtime() * 1000;
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onStopped() {
        this.joiningDeadlineMs = C.TIME_UNSET;
        maybeNotifyDroppedFrames();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onDisabled() {
        this.inputFormat = null;
        this.waitingForKeys = false;
        clearReportedVideoSize();
        clearRenderedFirstFrame();
        try {
            setSourceDrmSession(null);
            releaseDecoder();
        } finally {
            this.eventDispatcher.disabled(this.decoderCounters);
        }
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onReset() {
        DrmSessionManager<ExoMediaCrypto> drmSessionManager = this.drmSessionManager;
        if (drmSessionManager == null || !this.drmResourcesAcquired) {
            return;
        }
        this.drmResourcesAcquired = false;
        drmSessionManager.release();
    }

    @Override // com.google.android.exoplayer2.BaseRenderer
    protected void onStreamChanged(Format[] formatArr, long j) throws ExoPlaybackException {
        this.outputStreamOffsetUs = j;
        super.onStreamChanged(formatArr, j);
    }

    protected void onDecoderInitialized(String str, long j, long j2) {
        this.eventDispatcher.decoderInitialized(str, j, j2);
    }

    protected void flushDecoder() throws ExoPlaybackException {
        this.waitingForKeys = false;
        this.buffersInCodecCount = 0;
        if (this.decoderReinitializationState != 0) {
            releaseDecoder();
            maybeInitDecoder();
            return;
        }
        this.inputBuffer = null;
        VideoDecoderOutputBuffer videoDecoderOutputBuffer = this.outputBuffer;
        if (videoDecoderOutputBuffer != null) {
            videoDecoderOutputBuffer.release();
            this.outputBuffer = null;
        }
        this.decoder.flush();
        this.decoderReceivedBuffers = false;
    }

    protected void releaseDecoder() {
        this.inputBuffer = null;
        this.outputBuffer = null;
        this.decoderReinitializationState = 0;
        this.decoderReceivedBuffers = false;
        this.buffersInCodecCount = 0;
        SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException> simpleDecoder = this.decoder;
        if (simpleDecoder != null) {
            simpleDecoder.release();
            this.decoder = null;
            this.decoderCounters.decoderReleaseCount++;
        }
        setDecoderDrmSession(null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void onInputFormatChanged(FormatHolder formatHolder) throws ExoPlaybackException {
        this.waitingForFirstSampleInFormat = true;
        Format format = (Format) Assertions.checkNotNull(formatHolder.format);
        if (formatHolder.includesDrmSession) {
            setSourceDrmSession(formatHolder.drmSession);
        } else {
            this.sourceDrmSession = getUpdatedSourceDrmSession(this.inputFormat, format, this.drmSessionManager, this.sourceDrmSession);
        }
        this.inputFormat = format;
        if (this.sourceDrmSession != this.decoderDrmSession) {
            if (this.decoderReceivedBuffers) {
                this.decoderReinitializationState = 1;
            } else {
                releaseDecoder();
                maybeInitDecoder();
            }
        }
        this.eventDispatcher.inputFormatChanged(this.inputFormat);
    }

    protected void onProcessedOutputBuffer(long j) {
        this.buffersInCodecCount--;
    }

    protected boolean shouldDropOutputBuffer(long j, long j2) {
        return isBufferLate(j);
    }

    protected boolean shouldDropBuffersToKeyframe(long j, long j2) {
        return isBufferVeryLate(j);
    }

    protected boolean shouldForceRenderOutputBuffer(long j, long j2) {
        return isBufferLate(j) && j2 > 100000;
    }

    protected void skipOutputBuffer(VideoDecoderOutputBuffer videoDecoderOutputBuffer) {
        this.decoderCounters.skippedOutputBufferCount++;
        videoDecoderOutputBuffer.release();
    }

    protected void dropOutputBuffer(VideoDecoderOutputBuffer videoDecoderOutputBuffer) {
        updateDroppedBufferCounters(1);
        videoDecoderOutputBuffer.release();
    }

    protected boolean maybeDropBuffersToKeyframe(long j) throws ExoPlaybackException {
        int skipSource = skipSource(j);
        if (skipSource == 0) {
            return false;
        }
        this.decoderCounters.droppedToKeyframeCount++;
        updateDroppedBufferCounters(this.buffersInCodecCount + skipSource);
        flushDecoder();
        return true;
    }

    protected void updateDroppedBufferCounters(int i) {
        this.decoderCounters.droppedBufferCount += i;
        this.droppedFrames += i;
        int i2 = this.consecutiveDroppedFrameCount + i;
        this.consecutiveDroppedFrameCount = i2;
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.maxConsecutiveDroppedBufferCount = Math.max(i2, decoderCounters.maxConsecutiveDroppedBufferCount);
        int i3 = this.maxDroppedFramesToNotify;
        if (i3 <= 0 || this.droppedFrames < i3) {
            return;
        }
        maybeNotifyDroppedFrames();
    }

    protected void renderOutputBuffer(VideoDecoderOutputBuffer videoDecoderOutputBuffer, long j, Format format) throws VideoDecoderException {
        this.lastRenderTimeUs = C.msToUs(SystemClock.elapsedRealtime() * 1000);
        int i = videoDecoderOutputBuffer.mode;
        boolean z = i == 1 && this.surface != null;
        boolean z2 = i == 0 && this.outputBufferRenderer != null;
        if (!z2 && !z) {
            dropOutputBuffer(videoDecoderOutputBuffer);
            return;
        }
        maybeNotifyVideoSizeChanged(videoDecoderOutputBuffer.width, videoDecoderOutputBuffer.height);
        if (z2) {
            this.outputBufferRenderer.setOutputBuffer(videoDecoderOutputBuffer);
        } else {
            renderOutputBufferToSurface(videoDecoderOutputBuffer, this.surface);
        }
        this.consecutiveDroppedFrameCount = 0;
        this.decoderCounters.renderedOutputBufferCount++;
        maybeNotifyRenderedFirstFrame();
    }

    protected final void setOutputSurface(Surface surface) {
        if (this.surface == surface) {
            if (surface != null) {
                onOutputReset();
                return;
            }
            return;
        }
        this.surface = surface;
        if (surface != null) {
            this.outputBufferRenderer = null;
            this.outputMode = 1;
            if (this.decoder != null) {
                setDecoderOutputMode(1);
            }
            onOutputChanged();
            return;
        }
        this.outputMode = -1;
        onOutputRemoved();
    }

    protected final void setOutputBufferRenderer(VideoDecoderOutputBufferRenderer videoDecoderOutputBufferRenderer) {
        if (this.outputBufferRenderer == videoDecoderOutputBufferRenderer) {
            if (videoDecoderOutputBufferRenderer != null) {
                onOutputReset();
                return;
            }
            return;
        }
        this.outputBufferRenderer = videoDecoderOutputBufferRenderer;
        if (videoDecoderOutputBufferRenderer != null) {
            this.surface = null;
            this.outputMode = 0;
            if (this.decoder != null) {
                setDecoderOutputMode(0);
            }
            onOutputChanged();
            return;
        }
        this.outputMode = -1;
        onOutputRemoved();
    }

    private void setSourceDrmSession(DrmSession<ExoMediaCrypto> drmSession) {
        DrmSession.replaceSession(this.sourceDrmSession, drmSession);
        this.sourceDrmSession = drmSession;
    }

    private void setDecoderDrmSession(DrmSession<ExoMediaCrypto> drmSession) {
        DrmSession.replaceSession(this.decoderDrmSession, drmSession);
        this.decoderDrmSession = drmSession;
    }

    private void maybeInitDecoder() throws ExoPlaybackException {
        ExoMediaCrypto exoMediaCrypto;
        if (this.decoder != null) {
            return;
        }
        setDecoderDrmSession(this.sourceDrmSession);
        DrmSession<ExoMediaCrypto> drmSession = this.decoderDrmSession;
        if (drmSession != null) {
            exoMediaCrypto = drmSession.getMediaCrypto();
            if (exoMediaCrypto == null && this.decoderDrmSession.getError() == null) {
                return;
            }
        } else {
            exoMediaCrypto = null;
        }
        try {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.decoder = createDecoder(this.inputFormat, exoMediaCrypto);
            setDecoderOutputMode(this.outputMode);
            long elapsedRealtime2 = SystemClock.elapsedRealtime();
            onDecoderInitialized(this.decoder.getName(), elapsedRealtime2, elapsedRealtime2 - elapsedRealtime);
            this.decoderCounters.decoderInitCount++;
        } catch (VideoDecoderException e) {
            throw createRendererException(e, this.inputFormat);
        }
    }

    private boolean feedInputBuffer() throws VideoDecoderException, ExoPlaybackException {
        SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException> simpleDecoder = this.decoder;
        if (simpleDecoder == null || this.decoderReinitializationState == 2 || this.inputStreamEnded) {
            return false;
        }
        if (this.inputBuffer == null) {
            VideoDecoderInputBuffer dequeueInputBuffer = simpleDecoder.dequeueInputBuffer();
            this.inputBuffer = dequeueInputBuffer;
            if (dequeueInputBuffer == null) {
                return false;
            }
        }
        if (this.decoderReinitializationState == 1) {
            this.inputBuffer.setFlags(4);
            this.decoder.queueInputBuffer((SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException>) this.inputBuffer);
            this.inputBuffer = null;
            this.decoderReinitializationState = 2;
            return false;
        }
        FormatHolder formatHolder = getFormatHolder();
        int readSource = this.waitingForKeys ? -4 : readSource(formatHolder, this.inputBuffer, false);
        if (readSource == -3) {
            return false;
        }
        if (readSource == -5) {
            onInputFormatChanged(formatHolder);
            return true;
        }
        if (this.inputBuffer.isEndOfStream()) {
            this.inputStreamEnded = true;
            this.decoder.queueInputBuffer((SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException>) this.inputBuffer);
            this.inputBuffer = null;
            return false;
        }
        boolean shouldWaitForKeys = shouldWaitForKeys(this.inputBuffer.isEncrypted());
        this.waitingForKeys = shouldWaitForKeys;
        if (shouldWaitForKeys) {
            return false;
        }
        if (this.waitingForFirstSampleInFormat) {
            this.formatQueue.add(this.inputBuffer.timeUs, this.inputFormat);
            this.waitingForFirstSampleInFormat = false;
        }
        this.inputBuffer.flip();
        this.inputBuffer.colorInfo = this.inputFormat.colorInfo;
        onQueueInputBuffer(this.inputBuffer);
        this.decoder.queueInputBuffer((SimpleDecoder<VideoDecoderInputBuffer, ? extends VideoDecoderOutputBuffer, ? extends VideoDecoderException>) this.inputBuffer);
        this.buffersInCodecCount++;
        this.decoderReceivedBuffers = true;
        this.decoderCounters.inputBufferCount++;
        this.inputBuffer = null;
        return true;
    }

    private boolean drainOutputBuffer(long j, long j2) throws ExoPlaybackException, VideoDecoderException {
        if (this.outputBuffer == null) {
            VideoDecoderOutputBuffer dequeueOutputBuffer = this.decoder.dequeueOutputBuffer();
            this.outputBuffer = dequeueOutputBuffer;
            if (dequeueOutputBuffer == null) {
                return false;
            }
            this.decoderCounters.skippedOutputBufferCount += this.outputBuffer.skippedOutputBufferCount;
            this.buffersInCodecCount -= this.outputBuffer.skippedOutputBufferCount;
        }
        if (this.outputBuffer.isEndOfStream()) {
            if (this.decoderReinitializationState == 2) {
                releaseDecoder();
                maybeInitDecoder();
            } else {
                this.outputBuffer.release();
                this.outputBuffer = null;
                this.outputStreamEnded = true;
            }
            return false;
        }
        boolean processOutputBuffer = processOutputBuffer(j, j2);
        if (processOutputBuffer) {
            onProcessedOutputBuffer(this.outputBuffer.timeUs);
            this.outputBuffer = null;
        }
        return processOutputBuffer;
    }

    private boolean processOutputBuffer(long j, long j2) throws ExoPlaybackException, VideoDecoderException {
        if (this.initialPositionUs == C.TIME_UNSET) {
            this.initialPositionUs = j;
        }
        long j3 = this.outputBuffer.timeUs - j;
        if (!hasOutput()) {
            if (!isBufferLate(j3)) {
                return false;
            }
            skipOutputBuffer(this.outputBuffer);
            return true;
        }
        long j4 = this.outputBuffer.timeUs - this.outputStreamOffsetUs;
        Format pollFloor = this.formatQueue.pollFloor(j4);
        if (pollFloor != null) {
            this.outputFormat = pollFloor;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        boolean z = getState() == 2;
        if (!this.renderedFirstFrame || (z && shouldForceRenderOutputBuffer(j3, elapsedRealtime - this.lastRenderTimeUs))) {
            renderOutputBuffer(this.outputBuffer, j4, this.outputFormat);
            return true;
        }
        if (!z || j == this.initialPositionUs || (shouldDropBuffersToKeyframe(j3, j2) && maybeDropBuffersToKeyframe(j))) {
            return false;
        }
        if (shouldDropOutputBuffer(j3, j2)) {
            dropOutputBuffer(this.outputBuffer);
            return true;
        }
        if (j3 < 30000) {
            renderOutputBuffer(this.outputBuffer, j4, this.outputFormat);
            return true;
        }
        return false;
    }

    private boolean hasOutput() {
        return this.outputMode != -1;
    }

    private void onOutputChanged() {
        maybeRenotifyVideoSizeChanged();
        clearRenderedFirstFrame();
        if (getState() == 2) {
            setJoiningDeadlineMs();
        }
    }

    private void onOutputRemoved() {
        clearReportedVideoSize();
        clearRenderedFirstFrame();
    }

    private void onOutputReset() {
        maybeRenotifyVideoSizeChanged();
        maybeRenotifyRenderedFirstFrame();
    }

    private boolean shouldWaitForKeys(boolean z) throws ExoPlaybackException {
        DrmSession<ExoMediaCrypto> drmSession = this.decoderDrmSession;
        if (drmSession == null || (!z && (this.playClearSamplesWithoutKeys || drmSession.playClearSamplesWithoutKeys()))) {
            return false;
        }
        int state = this.decoderDrmSession.getState();
        if (state != 1) {
            return state != 4;
        }
        throw createRendererException(this.decoderDrmSession.getError(), this.inputFormat);
    }

    private void setJoiningDeadlineMs() {
        this.joiningDeadlineMs = this.allowedJoiningTimeMs > 0 ? SystemClock.elapsedRealtime() + this.allowedJoiningTimeMs : C.TIME_UNSET;
    }

    private void clearRenderedFirstFrame() {
        this.renderedFirstFrame = false;
    }

    private void maybeNotifyRenderedFirstFrame() {
        if (this.renderedFirstFrame) {
            return;
        }
        this.renderedFirstFrame = true;
        this.eventDispatcher.renderedFirstFrame(this.surface);
    }

    private void maybeRenotifyRenderedFirstFrame() {
        if (this.renderedFirstFrame) {
            this.eventDispatcher.renderedFirstFrame(this.surface);
        }
    }

    private void clearReportedVideoSize() {
        this.reportedWidth = -1;
        this.reportedHeight = -1;
    }

    private void maybeNotifyVideoSizeChanged(int i, int i2) {
        if (this.reportedWidth == i && this.reportedHeight == i2) {
            return;
        }
        this.reportedWidth = i;
        this.reportedHeight = i2;
        this.eventDispatcher.videoSizeChanged(i, i2, 0, 1.0f);
    }

    private void maybeRenotifyVideoSizeChanged() {
        int i = this.reportedWidth;
        if (i == -1 && this.reportedHeight == -1) {
            return;
        }
        this.eventDispatcher.videoSizeChanged(i, this.reportedHeight, 0, 1.0f);
    }

    private void maybeNotifyDroppedFrames() {
        if (this.droppedFrames > 0) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.eventDispatcher.droppedFrames(this.droppedFrames, elapsedRealtime - this.droppedFrameAccumulationStartTimeMs);
            this.droppedFrames = 0;
            this.droppedFrameAccumulationStartTimeMs = elapsedRealtime;
        }
    }
}