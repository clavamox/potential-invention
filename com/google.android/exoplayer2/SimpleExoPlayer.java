package com.google.android.exoplayer2;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import com.google.android.exoplayer2.AudioBecomingNoisyManager;
import com.google.android.exoplayer2.AudioFocusManager;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.PlayerMessage;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AuxEffectInfo;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoDecoderOutputBufferRenderer;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.exoplayer2.video.spherical.CameraMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/* loaded from: classes.dex */
public class SimpleExoPlayer extends BasePlayer implements ExoPlayer, Player.AudioComponent, Player.VideoComponent, Player.TextComponent, Player.MetadataComponent {
    private static final String TAG = "SimpleExoPlayer";
    private final AnalyticsCollector analyticsCollector;
    private AudioAttributes audioAttributes;
    private final AudioBecomingNoisyManager audioBecomingNoisyManager;
    private final CopyOnWriteArraySet<AudioRendererEventListener> audioDebugListeners;
    private DecoderCounters audioDecoderCounters;
    private final AudioFocusManager audioFocusManager;
    private Format audioFormat;
    private final CopyOnWriteArraySet<AudioListener> audioListeners;
    private int audioSessionId;
    private float audioVolume;
    private final BandwidthMeter bandwidthMeter;
    private CameraMotionListener cameraMotionListener;
    private final ComponentListener componentListener;
    private List<Cue> currentCues;
    private final Handler eventHandler;
    private boolean hasNotifiedFullWrongThreadWarning;
    private boolean isPriorityTaskManagerRegistered;
    private MediaSource mediaSource;
    private final CopyOnWriteArraySet<MetadataOutput> metadataOutputs;
    private boolean ownsSurface;
    private final ExoPlayerImpl player;
    private boolean playerReleased;
    private PriorityTaskManager priorityTaskManager;
    protected final Renderer[] renderers;
    private Surface surface;
    private int surfaceHeight;
    private SurfaceHolder surfaceHolder;
    private int surfaceWidth;
    private final CopyOnWriteArraySet<TextOutput> textOutputs;
    private TextureView textureView;
    private final CopyOnWriteArraySet<VideoRendererEventListener> videoDebugListeners;
    private DecoderCounters videoDecoderCounters;
    private VideoDecoderOutputBufferRenderer videoDecoderOutputBufferRenderer;
    private Format videoFormat;
    private VideoFrameMetadataListener videoFrameMetadataListener;
    private final CopyOnWriteArraySet<com.google.android.exoplayer2.video.VideoListener> videoListeners;
    private int videoScalingMode;
    private final WakeLockManager wakeLockManager;

    @Deprecated
    public interface VideoListener extends com.google.android.exoplayer2.video.VideoListener {
    }

    @Override // com.google.android.exoplayer2.Player
    public Player.AudioComponent getAudioComponent() {
        return this;
    }

    @Override // com.google.android.exoplayer2.Player
    public Player.MetadataComponent getMetadataComponent() {
        return this;
    }

    @Override // com.google.android.exoplayer2.Player
    public Player.TextComponent getTextComponent() {
        return this;
    }

    @Override // com.google.android.exoplayer2.Player
    public Player.VideoComponent getVideoComponent() {
        return this;
    }

    public static final class Builder {
        private AnalyticsCollector analyticsCollector;
        private BandwidthMeter bandwidthMeter;
        private boolean buildCalled;
        private Clock clock;
        private final Context context;
        private LoadControl loadControl;
        private Looper looper;
        private final RenderersFactory renderersFactory;
        private TrackSelector trackSelector;
        private boolean useLazyPreparation;

        public Builder(Context context) {
            this(context, new DefaultRenderersFactory(context));
        }

        public Builder(Context context, RenderersFactory renderersFactory) {
            this(context, renderersFactory, new DefaultTrackSelector(context), new DefaultLoadControl(), DefaultBandwidthMeter.getSingletonInstance(context), Util.getLooper(), new AnalyticsCollector(Clock.DEFAULT), true, Clock.DEFAULT);
        }

        public Builder(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl, BandwidthMeter bandwidthMeter, Looper looper, AnalyticsCollector analyticsCollector, boolean z, Clock clock) {
            this.context = context;
            this.renderersFactory = renderersFactory;
            this.trackSelector = trackSelector;
            this.loadControl = loadControl;
            this.bandwidthMeter = bandwidthMeter;
            this.looper = looper;
            this.analyticsCollector = analyticsCollector;
            this.useLazyPreparation = z;
            this.clock = clock;
        }

        public Builder setTrackSelector(TrackSelector trackSelector) {
            Assertions.checkState(!this.buildCalled);
            this.trackSelector = trackSelector;
            return this;
        }

        public Builder setLoadControl(LoadControl loadControl) {
            Assertions.checkState(!this.buildCalled);
            this.loadControl = loadControl;
            return this;
        }

        public Builder setBandwidthMeter(BandwidthMeter bandwidthMeter) {
            Assertions.checkState(!this.buildCalled);
            this.bandwidthMeter = bandwidthMeter;
            return this;
        }

        public Builder setLooper(Looper looper) {
            Assertions.checkState(!this.buildCalled);
            this.looper = looper;
            return this;
        }

        public Builder setAnalyticsCollector(AnalyticsCollector analyticsCollector) {
            Assertions.checkState(!this.buildCalled);
            this.analyticsCollector = analyticsCollector;
            return this;
        }

        public Builder setUseLazyPreparation(boolean z) {
            Assertions.checkState(!this.buildCalled);
            this.useLazyPreparation = z;
            return this;
        }

        public Builder setClock(Clock clock) {
            Assertions.checkState(!this.buildCalled);
            this.clock = clock;
            return this;
        }

        public SimpleExoPlayer build() {
            Assertions.checkState(!this.buildCalled);
            this.buildCalled = true;
            return new SimpleExoPlayer(this.context, this.renderersFactory, this.trackSelector, this.loadControl, this.bandwidthMeter, this.analyticsCollector, this.clock, this.looper);
        }
    }

    protected SimpleExoPlayer(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl, BandwidthMeter bandwidthMeter, AnalyticsCollector analyticsCollector, Clock clock, Looper looper) {
        this(context, renderersFactory, trackSelector, loadControl, DrmSessionManager.getDummyDrmSessionManager(), bandwidthMeter, analyticsCollector, clock, looper);
    }

    @Deprecated
    protected SimpleExoPlayer(Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, BandwidthMeter bandwidthMeter, AnalyticsCollector analyticsCollector, Clock clock, Looper looper) {
        this.bandwidthMeter = bandwidthMeter;
        this.analyticsCollector = analyticsCollector;
        ComponentListener componentListener = new ComponentListener();
        this.componentListener = componentListener;
        CopyOnWriteArraySet<com.google.android.exoplayer2.video.VideoListener> copyOnWriteArraySet = new CopyOnWriteArraySet<>();
        this.videoListeners = copyOnWriteArraySet;
        CopyOnWriteArraySet<AudioListener> copyOnWriteArraySet2 = new CopyOnWriteArraySet<>();
        this.audioListeners = copyOnWriteArraySet2;
        this.textOutputs = new CopyOnWriteArraySet<>();
        this.metadataOutputs = new CopyOnWriteArraySet<>();
        CopyOnWriteArraySet<VideoRendererEventListener> copyOnWriteArraySet3 = new CopyOnWriteArraySet<>();
        this.videoDebugListeners = copyOnWriteArraySet3;
        CopyOnWriteArraySet<AudioRendererEventListener> copyOnWriteArraySet4 = new CopyOnWriteArraySet<>();
        this.audioDebugListeners = copyOnWriteArraySet4;
        Handler handler = new Handler(looper);
        this.eventHandler = handler;
        Renderer[] createRenderers = renderersFactory.createRenderers(handler, componentListener, componentListener, componentListener, componentListener, drmSessionManager);
        this.renderers = createRenderers;
        this.audioVolume = 1.0f;
        this.audioSessionId = 0;
        this.audioAttributes = AudioAttributes.DEFAULT;
        this.videoScalingMode = 1;
        this.currentCues = Collections.emptyList();
        ExoPlayerImpl exoPlayerImpl = new ExoPlayerImpl(createRenderers, trackSelector, loadControl, bandwidthMeter, clock, looper);
        this.player = exoPlayerImpl;
        analyticsCollector.setPlayer(exoPlayerImpl);
        addListener(analyticsCollector);
        addListener(componentListener);
        copyOnWriteArraySet3.add(analyticsCollector);
        copyOnWriteArraySet.add(analyticsCollector);
        copyOnWriteArraySet4.add(analyticsCollector);
        copyOnWriteArraySet2.add(analyticsCollector);
        addMetadataOutput(analyticsCollector);
        bandwidthMeter.addEventListener(handler, analyticsCollector);
        if (drmSessionManager instanceof DefaultDrmSessionManager) {
            ((DefaultDrmSessionManager) drmSessionManager).addListener(handler, analyticsCollector);
        }
        this.audioBecomingNoisyManager = new AudioBecomingNoisyManager(context, handler, componentListener);
        this.audioFocusManager = new AudioFocusManager(context, handler, componentListener);
        this.wakeLockManager = new WakeLockManager(context);
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void setVideoScalingMode(int i) {
        verifyApplicationThread();
        this.videoScalingMode = i;
        for (Renderer renderer : this.renderers) {
            if (renderer.getTrackType() == 2) {
                this.player.createMessage(renderer).setType(4).setPayload(Integer.valueOf(i)).send();
            }
        }
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public int getVideoScalingMode() {
        return this.videoScalingMode;
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void clearVideoSurface() {
        verifyApplicationThread();
        removeSurfaceCallbacks();
        setVideoSurfaceInternal(null, false);
        maybeNotifySurfaceSizeChanged(0, 0);
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void clearVideoSurface(Surface surface) {
        verifyApplicationThread();
        if (surface == null || surface != this.surface) {
            return;
        }
        clearVideoSurface();
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void setVideoSurface(Surface surface) {
        verifyApplicationThread();
        removeSurfaceCallbacks();
        if (surface != null) {
            clearVideoDecoderOutputBufferRenderer();
        }
        setVideoSurfaceInternal(surface, false);
        int i = surface != null ? -1 : 0;
        maybeNotifySurfaceSizeChanged(i, i);
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        verifyApplicationThread();
        removeSurfaceCallbacks();
        if (surfaceHolder != null) {
            clearVideoDecoderOutputBufferRenderer();
        }
        this.surfaceHolder = surfaceHolder;
        if (surfaceHolder == null) {
            setVideoSurfaceInternal(null, false);
            maybeNotifySurfaceSizeChanged(0, 0);
            return;
        }
        surfaceHolder.addCallback(this.componentListener);
        Surface surface = surfaceHolder.getSurface();
        if (surface != null && surface.isValid()) {
            setVideoSurfaceInternal(surface, false);
            Rect surfaceFrame = surfaceHolder.getSurfaceFrame();
            maybeNotifySurfaceSizeChanged(surfaceFrame.width(), surfaceFrame.height());
        } else {
            setVideoSurfaceInternal(null, false);
            maybeNotifySurfaceSizeChanged(0, 0);
        }
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void clearVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        verifyApplicationThread();
        if (surfaceHolder == null || surfaceHolder != this.surfaceHolder) {
            return;
        }
        setVideoSurfaceHolder(null);
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void setVideoSurfaceView(SurfaceView surfaceView) {
        setVideoSurfaceHolder(surfaceView == null ? null : surfaceView.getHolder());
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void clearVideoSurfaceView(SurfaceView surfaceView) {
        clearVideoSurfaceHolder(surfaceView == null ? null : surfaceView.getHolder());
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void setVideoTextureView(TextureView textureView) {
        verifyApplicationThread();
        removeSurfaceCallbacks();
        if (textureView != null) {
            clearVideoDecoderOutputBufferRenderer();
        }
        this.textureView = textureView;
        if (textureView == null) {
            setVideoSurfaceInternal(null, true);
            maybeNotifySurfaceSizeChanged(0, 0);
            return;
        }
        if (textureView.getSurfaceTextureListener() != null) {
            Log.w(TAG, "Replacing existing SurfaceTextureListener.");
        }
        textureView.setSurfaceTextureListener(this.componentListener);
        SurfaceTexture surfaceTexture = textureView.isAvailable() ? textureView.getSurfaceTexture() : null;
        if (surfaceTexture == null) {
            setVideoSurfaceInternal(null, true);
            maybeNotifySurfaceSizeChanged(0, 0);
        } else {
            setVideoSurfaceInternal(new Surface(surfaceTexture), true);
            maybeNotifySurfaceSizeChanged(textureView.getWidth(), textureView.getHeight());
        }
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void clearVideoTextureView(TextureView textureView) {
        verifyApplicationThread();
        if (textureView == null || textureView != this.textureView) {
            return;
        }
        setVideoTextureView(null);
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void setVideoDecoderOutputBufferRenderer(VideoDecoderOutputBufferRenderer videoDecoderOutputBufferRenderer) {
        verifyApplicationThread();
        if (videoDecoderOutputBufferRenderer != null) {
            clearVideoSurface();
        }
        setVideoDecoderOutputBufferRendererInternal(videoDecoderOutputBufferRenderer);
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void clearVideoDecoderOutputBufferRenderer() {
        verifyApplicationThread();
        setVideoDecoderOutputBufferRendererInternal(null);
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void clearVideoDecoderOutputBufferRenderer(VideoDecoderOutputBufferRenderer videoDecoderOutputBufferRenderer) {
        verifyApplicationThread();
        if (videoDecoderOutputBufferRenderer == null || videoDecoderOutputBufferRenderer != this.videoDecoderOutputBufferRenderer) {
            return;
        }
        clearVideoDecoderOutputBufferRenderer();
    }

    @Override // com.google.android.exoplayer2.Player.AudioComponent
    public void addAudioListener(AudioListener audioListener) {
        this.audioListeners.add(audioListener);
    }

    @Override // com.google.android.exoplayer2.Player.AudioComponent
    public void removeAudioListener(AudioListener audioListener) {
        this.audioListeners.remove(audioListener);
    }

    @Override // com.google.android.exoplayer2.Player.AudioComponent
    public void setAudioAttributes(AudioAttributes audioAttributes) {
        setAudioAttributes(audioAttributes, false);
    }

    @Override // com.google.android.exoplayer2.Player.AudioComponent
    public void setAudioAttributes(AudioAttributes audioAttributes, boolean z) {
        verifyApplicationThread();
        if (this.playerReleased) {
            return;
        }
        if (!Util.areEqual(this.audioAttributes, audioAttributes)) {
            this.audioAttributes = audioAttributes;
            for (Renderer renderer : this.renderers) {
                if (renderer.getTrackType() == 1) {
                    this.player.createMessage(renderer).setType(3).setPayload(audioAttributes).send();
                }
            }
            Iterator<AudioListener> it = this.audioListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioAttributesChanged(audioAttributes);
            }
        }
        AudioFocusManager audioFocusManager = this.audioFocusManager;
        if (!z) {
            audioAttributes = null;
        }
        updatePlayWhenReady(getPlayWhenReady(), audioFocusManager.setAudioAttributes(audioAttributes, getPlayWhenReady(), getPlaybackState()));
    }

    @Override // com.google.android.exoplayer2.Player.AudioComponent
    public AudioAttributes getAudioAttributes() {
        return this.audioAttributes;
    }

    @Override // com.google.android.exoplayer2.Player.AudioComponent
    public int getAudioSessionId() {
        return this.audioSessionId;
    }

    @Override // com.google.android.exoplayer2.Player.AudioComponent
    public void setAuxEffectInfo(AuxEffectInfo auxEffectInfo) {
        verifyApplicationThread();
        for (Renderer renderer : this.renderers) {
            if (renderer.getTrackType() == 1) {
                this.player.createMessage(renderer).setType(5).setPayload(auxEffectInfo).send();
            }
        }
    }

    @Override // com.google.android.exoplayer2.Player.AudioComponent
    public void clearAuxEffectInfo() {
        setAuxEffectInfo(new AuxEffectInfo(0, 0.0f));
    }

    @Override // com.google.android.exoplayer2.Player.AudioComponent
    public void setVolume(float f) {
        verifyApplicationThread();
        float constrainValue = Util.constrainValue(f, 0.0f, 1.0f);
        if (this.audioVolume == constrainValue) {
            return;
        }
        this.audioVolume = constrainValue;
        sendVolumeToRenderers();
        Iterator<AudioListener> it = this.audioListeners.iterator();
        while (it.hasNext()) {
            it.next().onVolumeChanged(constrainValue);
        }
    }

    @Override // com.google.android.exoplayer2.Player.AudioComponent
    public float getVolume() {
        return this.audioVolume;
    }

    @Deprecated
    public void setAudioStreamType(int i) {
        int audioUsageForStreamType = Util.getAudioUsageForStreamType(i);
        setAudioAttributes(new AudioAttributes.Builder().setUsage(audioUsageForStreamType).setContentType(Util.getAudioContentTypeForStreamType(i)).build());
    }

    @Deprecated
    public int getAudioStreamType() {
        return Util.getStreamTypeForAudioUsage(this.audioAttributes.usage);
    }

    public AnalyticsCollector getAnalyticsCollector() {
        return this.analyticsCollector;
    }

    public void addAnalyticsListener(AnalyticsListener analyticsListener) {
        verifyApplicationThread();
        this.analyticsCollector.addListener(analyticsListener);
    }

    public void removeAnalyticsListener(AnalyticsListener analyticsListener) {
        verifyApplicationThread();
        this.analyticsCollector.removeListener(analyticsListener);
    }

    public void setHandleAudioBecomingNoisy(boolean z) {
        verifyApplicationThread();
        if (this.playerReleased) {
            return;
        }
        this.audioBecomingNoisyManager.setEnabled(z);
    }

    public void setPriorityTaskManager(PriorityTaskManager priorityTaskManager) {
        verifyApplicationThread();
        if (Util.areEqual(this.priorityTaskManager, priorityTaskManager)) {
            return;
        }
        if (this.isPriorityTaskManagerRegistered) {
            ((PriorityTaskManager) Assertions.checkNotNull(this.priorityTaskManager)).remove(0);
        }
        if (priorityTaskManager != null && isLoading()) {
            priorityTaskManager.add(0);
            this.isPriorityTaskManagerRegistered = true;
        } else {
            this.isPriorityTaskManagerRegistered = false;
        }
        this.priorityTaskManager = priorityTaskManager;
    }

    @Deprecated
    public void setPlaybackParams(PlaybackParams playbackParams) {
        PlaybackParameters playbackParameters;
        if (playbackParams != null) {
            playbackParams.allowDefaults();
            playbackParameters = new PlaybackParameters(playbackParams.getSpeed(), playbackParams.getPitch());
        } else {
            playbackParameters = null;
        }
        setPlaybackParameters(playbackParameters);
    }

    public Format getVideoFormat() {
        return this.videoFormat;
    }

    public Format getAudioFormat() {
        return this.audioFormat;
    }

    public DecoderCounters getVideoDecoderCounters() {
        return this.videoDecoderCounters;
    }

    public DecoderCounters getAudioDecoderCounters() {
        return this.audioDecoderCounters;
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void addVideoListener(com.google.android.exoplayer2.video.VideoListener videoListener) {
        this.videoListeners.add(videoListener);
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void removeVideoListener(com.google.android.exoplayer2.video.VideoListener videoListener) {
        this.videoListeners.remove(videoListener);
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void setVideoFrameMetadataListener(VideoFrameMetadataListener videoFrameMetadataListener) {
        verifyApplicationThread();
        this.videoFrameMetadataListener = videoFrameMetadataListener;
        for (Renderer renderer : this.renderers) {
            if (renderer.getTrackType() == 2) {
                this.player.createMessage(renderer).setType(6).setPayload(videoFrameMetadataListener).send();
            }
        }
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void clearVideoFrameMetadataListener(VideoFrameMetadataListener videoFrameMetadataListener) {
        verifyApplicationThread();
        if (this.videoFrameMetadataListener != videoFrameMetadataListener) {
            return;
        }
        for (Renderer renderer : this.renderers) {
            if (renderer.getTrackType() == 2) {
                this.player.createMessage(renderer).setType(6).setPayload(null).send();
            }
        }
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void setCameraMotionListener(CameraMotionListener cameraMotionListener) {
        verifyApplicationThread();
        this.cameraMotionListener = cameraMotionListener;
        for (Renderer renderer : this.renderers) {
            if (renderer.getTrackType() == 5) {
                this.player.createMessage(renderer).setType(7).setPayload(cameraMotionListener).send();
            }
        }
    }

    @Override // com.google.android.exoplayer2.Player.VideoComponent
    public void clearCameraMotionListener(CameraMotionListener cameraMotionListener) {
        verifyApplicationThread();
        if (this.cameraMotionListener != cameraMotionListener) {
            return;
        }
        for (Renderer renderer : this.renderers) {
            if (renderer.getTrackType() == 5) {
                this.player.createMessage(renderer).setType(7).setPayload(null).send();
            }
        }
    }

    @Deprecated
    public void setVideoListener(VideoListener videoListener) {
        this.videoListeners.clear();
        if (videoListener != null) {
            addVideoListener(videoListener);
        }
    }

    @Deprecated
    public void clearVideoListener(VideoListener videoListener) {
        removeVideoListener(videoListener);
    }

    @Override // com.google.android.exoplayer2.Player.TextComponent
    public void addTextOutput(TextOutput textOutput) {
        if (!this.currentCues.isEmpty()) {
            textOutput.onCues(this.currentCues);
        }
        this.textOutputs.add(textOutput);
    }

    @Override // com.google.android.exoplayer2.Player.TextComponent
    public void removeTextOutput(TextOutput textOutput) {
        this.textOutputs.remove(textOutput);
    }

    @Deprecated
    public void setTextOutput(TextOutput textOutput) {
        this.textOutputs.clear();
        if (textOutput != null) {
            addTextOutput(textOutput);
        }
    }

    @Deprecated
    public void clearTextOutput(TextOutput textOutput) {
        removeTextOutput(textOutput);
    }

    @Override // com.google.android.exoplayer2.Player.MetadataComponent
    public void addMetadataOutput(MetadataOutput metadataOutput) {
        this.metadataOutputs.add(metadataOutput);
    }

    @Override // com.google.android.exoplayer2.Player.MetadataComponent
    public void removeMetadataOutput(MetadataOutput metadataOutput) {
        this.metadataOutputs.remove(metadataOutput);
    }

    @Deprecated
    public void setMetadataOutput(MetadataOutput metadataOutput) {
        this.metadataOutputs.retainAll(Collections.singleton(this.analyticsCollector));
        if (metadataOutput != null) {
            addMetadataOutput(metadataOutput);
        }
    }

    @Deprecated
    public void clearMetadataOutput(MetadataOutput metadataOutput) {
        removeMetadataOutput(metadataOutput);
    }

    @Deprecated
    public void setVideoDebugListener(VideoRendererEventListener videoRendererEventListener) {
        this.videoDebugListeners.retainAll(Collections.singleton(this.analyticsCollector));
        if (videoRendererEventListener != null) {
            addVideoDebugListener(videoRendererEventListener);
        }
    }

    @Deprecated
    public void addVideoDebugListener(VideoRendererEventListener videoRendererEventListener) {
        this.videoDebugListeners.add(videoRendererEventListener);
    }

    @Deprecated
    public void removeVideoDebugListener(VideoRendererEventListener videoRendererEventListener) {
        this.videoDebugListeners.remove(videoRendererEventListener);
    }

    @Deprecated
    public void setAudioDebugListener(AudioRendererEventListener audioRendererEventListener) {
        this.audioDebugListeners.retainAll(Collections.singleton(this.analyticsCollector));
        if (audioRendererEventListener != null) {
            addAudioDebugListener(audioRendererEventListener);
        }
    }

    @Deprecated
    public void addAudioDebugListener(AudioRendererEventListener audioRendererEventListener) {
        this.audioDebugListeners.add(audioRendererEventListener);
    }

    @Deprecated
    public void removeAudioDebugListener(AudioRendererEventListener audioRendererEventListener) {
        this.audioDebugListeners.remove(audioRendererEventListener);
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public Looper getPlaybackLooper() {
        return this.player.getPlaybackLooper();
    }

    @Override // com.google.android.exoplayer2.Player
    public Looper getApplicationLooper() {
        return this.player.getApplicationLooper();
    }

    @Override // com.google.android.exoplayer2.Player
    public void addListener(Player.EventListener eventListener) {
        verifyApplicationThread();
        this.player.addListener(eventListener);
    }

    @Override // com.google.android.exoplayer2.Player
    public void removeListener(Player.EventListener eventListener) {
        verifyApplicationThread();
        this.player.removeListener(eventListener);
    }

    @Override // com.google.android.exoplayer2.Player
    public int getPlaybackState() {
        verifyApplicationThread();
        return this.player.getPlaybackState();
    }

    @Override // com.google.android.exoplayer2.Player
    public int getPlaybackSuppressionReason() {
        verifyApplicationThread();
        return this.player.getPlaybackSuppressionReason();
    }

    @Override // com.google.android.exoplayer2.Player
    public ExoPlaybackException getPlaybackError() {
        verifyApplicationThread();
        return this.player.getPlaybackError();
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void retry() {
        verifyApplicationThread();
        if (this.mediaSource != null) {
            if (getPlaybackError() != null || getPlaybackState() == 1) {
                prepare(this.mediaSource, false, false);
            }
        }
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void prepare(MediaSource mediaSource) {
        prepare(mediaSource, true, true);
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void prepare(MediaSource mediaSource, boolean z, boolean z2) {
        verifyApplicationThread();
        MediaSource mediaSource2 = this.mediaSource;
        if (mediaSource2 != null) {
            mediaSource2.removeEventListener(this.analyticsCollector);
            this.analyticsCollector.resetForNewMediaSource();
        }
        this.mediaSource = mediaSource;
        mediaSource.addEventListener(this.eventHandler, this.analyticsCollector);
        updatePlayWhenReady(getPlayWhenReady(), this.audioFocusManager.handlePrepare(getPlayWhenReady()));
        this.player.prepare(mediaSource, z, z2);
    }

    @Override // com.google.android.exoplayer2.Player
    public void setPlayWhenReady(boolean z) {
        verifyApplicationThread();
        updatePlayWhenReady(z, this.audioFocusManager.handleSetPlayWhenReady(z, getPlaybackState()));
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean getPlayWhenReady() {
        verifyApplicationThread();
        return this.player.getPlayWhenReady();
    }

    @Override // com.google.android.exoplayer2.Player
    public int getRepeatMode() {
        verifyApplicationThread();
        return this.player.getRepeatMode();
    }

    @Override // com.google.android.exoplayer2.Player
    public void setRepeatMode(int i) {
        verifyApplicationThread();
        this.player.setRepeatMode(i);
    }

    @Override // com.google.android.exoplayer2.Player
    public void setShuffleModeEnabled(boolean z) {
        verifyApplicationThread();
        this.player.setShuffleModeEnabled(z);
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean getShuffleModeEnabled() {
        verifyApplicationThread();
        return this.player.getShuffleModeEnabled();
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean isLoading() {
        verifyApplicationThread();
        return this.player.isLoading();
    }

    @Override // com.google.android.exoplayer2.Player
    public void seekTo(int i, long j) {
        verifyApplicationThread();
        this.analyticsCollector.notifySeekStarted();
        this.player.seekTo(i, j);
    }

    @Override // com.google.android.exoplayer2.Player
    public void setPlaybackParameters(PlaybackParameters playbackParameters) {
        verifyApplicationThread();
        this.player.setPlaybackParameters(playbackParameters);
    }

    @Override // com.google.android.exoplayer2.Player
    public PlaybackParameters getPlaybackParameters() {
        verifyApplicationThread();
        return this.player.getPlaybackParameters();
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void setSeekParameters(SeekParameters seekParameters) {
        verifyApplicationThread();
        this.player.setSeekParameters(seekParameters);
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public SeekParameters getSeekParameters() {
        verifyApplicationThread();
        return this.player.getSeekParameters();
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public void setForegroundMode(boolean z) {
        this.player.setForegroundMode(z);
    }

    @Override // com.google.android.exoplayer2.Player
    public void stop(boolean z) {
        verifyApplicationThread();
        this.player.stop(z);
        MediaSource mediaSource = this.mediaSource;
        if (mediaSource != null) {
            mediaSource.removeEventListener(this.analyticsCollector);
            this.analyticsCollector.resetForNewMediaSource();
            if (z) {
                this.mediaSource = null;
            }
        }
        this.audioFocusManager.handleStop();
        this.currentCues = Collections.emptyList();
    }

    @Override // com.google.android.exoplayer2.Player
    public void release() {
        verifyApplicationThread();
        this.audioBecomingNoisyManager.setEnabled(false);
        this.audioFocusManager.handleStop();
        this.wakeLockManager.setStayAwake(false);
        this.player.release();
        removeSurfaceCallbacks();
        Surface surface = this.surface;
        if (surface != null) {
            if (this.ownsSurface) {
                surface.release();
            }
            this.surface = null;
        }
        MediaSource mediaSource = this.mediaSource;
        if (mediaSource != null) {
            mediaSource.removeEventListener(this.analyticsCollector);
            this.mediaSource = null;
        }
        if (this.isPriorityTaskManagerRegistered) {
            ((PriorityTaskManager) Assertions.checkNotNull(this.priorityTaskManager)).remove(0);
            this.isPriorityTaskManagerRegistered = false;
        }
        this.bandwidthMeter.removeEventListener(this.analyticsCollector);
        this.currentCues = Collections.emptyList();
        this.playerReleased = true;
    }

    @Override // com.google.android.exoplayer2.ExoPlayer
    public PlayerMessage createMessage(PlayerMessage.Target target) {
        verifyApplicationThread();
        return this.player.createMessage(target);
    }

    @Override // com.google.android.exoplayer2.Player
    public int getRendererCount() {
        verifyApplicationThread();
        return this.player.getRendererCount();
    }

    @Override // com.google.android.exoplayer2.Player
    public int getRendererType(int i) {
        verifyApplicationThread();
        return this.player.getRendererType(i);
    }

    @Override // com.google.android.exoplayer2.Player
    public TrackGroupArray getCurrentTrackGroups() {
        verifyApplicationThread();
        return this.player.getCurrentTrackGroups();
    }

    @Override // com.google.android.exoplayer2.Player
    public TrackSelectionArray getCurrentTrackSelections() {
        verifyApplicationThread();
        return this.player.getCurrentTrackSelections();
    }

    @Override // com.google.android.exoplayer2.Player
    public Timeline getCurrentTimeline() {
        verifyApplicationThread();
        return this.player.getCurrentTimeline();
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentPeriodIndex() {
        verifyApplicationThread();
        return this.player.getCurrentPeriodIndex();
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentWindowIndex() {
        verifyApplicationThread();
        return this.player.getCurrentWindowIndex();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getDuration() {
        verifyApplicationThread();
        return this.player.getDuration();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getCurrentPosition() {
        verifyApplicationThread();
        return this.player.getCurrentPosition();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getBufferedPosition() {
        verifyApplicationThread();
        return this.player.getBufferedPosition();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getTotalBufferedDuration() {
        verifyApplicationThread();
        return this.player.getTotalBufferedDuration();
    }

    @Override // com.google.android.exoplayer2.Player
    public boolean isPlayingAd() {
        verifyApplicationThread();
        return this.player.isPlayingAd();
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentAdGroupIndex() {
        verifyApplicationThread();
        return this.player.getCurrentAdGroupIndex();
    }

    @Override // com.google.android.exoplayer2.Player
    public int getCurrentAdIndexInAdGroup() {
        verifyApplicationThread();
        return this.player.getCurrentAdIndexInAdGroup();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getContentPosition() {
        verifyApplicationThread();
        return this.player.getContentPosition();
    }

    @Override // com.google.android.exoplayer2.Player
    public long getContentBufferedPosition() {
        verifyApplicationThread();
        return this.player.getContentBufferedPosition();
    }

    public void setHandleWakeLock(boolean z) {
        this.wakeLockManager.setEnabled(z);
    }

    private void removeSurfaceCallbacks() {
        TextureView textureView = this.textureView;
        if (textureView != null) {
            if (textureView.getSurfaceTextureListener() != this.componentListener) {
                Log.w(TAG, "SurfaceTextureListener already unset or replaced.");
            } else {
                this.textureView.setSurfaceTextureListener(null);
            }
            this.textureView = null;
        }
        SurfaceHolder surfaceHolder = this.surfaceHolder;
        if (surfaceHolder != null) {
            surfaceHolder.removeCallback(this.componentListener);
            this.surfaceHolder = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVideoSurfaceInternal(Surface surface, boolean z) {
        ArrayList arrayList = new ArrayList();
        for (Renderer renderer : this.renderers) {
            if (renderer.getTrackType() == 2) {
                arrayList.add(this.player.createMessage(renderer).setType(1).setPayload(surface).send());
            }
        }
        Surface surface2 = this.surface;
        if (surface2 != null && surface2 != surface) {
            try {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    ((PlayerMessage) it.next()).blockUntilDelivered();
                }
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
            }
            if (this.ownsSurface) {
                this.surface.release();
            }
        }
        this.surface = surface;
        this.ownsSurface = z;
    }

    private void setVideoDecoderOutputBufferRendererInternal(VideoDecoderOutputBufferRenderer videoDecoderOutputBufferRenderer) {
        for (Renderer renderer : this.renderers) {
            if (renderer.getTrackType() == 2) {
                this.player.createMessage(renderer).setType(8).setPayload(videoDecoderOutputBufferRenderer).send();
            }
        }
        this.videoDecoderOutputBufferRenderer = videoDecoderOutputBufferRenderer;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeNotifySurfaceSizeChanged(int i, int i2) {
        if (i == this.surfaceWidth && i2 == this.surfaceHeight) {
            return;
        }
        this.surfaceWidth = i;
        this.surfaceHeight = i2;
        Iterator<com.google.android.exoplayer2.video.VideoListener> it = this.videoListeners.iterator();
        while (it.hasNext()) {
            it.next().onSurfaceSizeChanged(i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendVolumeToRenderers() {
        float volumeMultiplier = this.audioVolume * this.audioFocusManager.getVolumeMultiplier();
        for (Renderer renderer : this.renderers) {
            if (renderer.getTrackType() == 1) {
                this.player.createMessage(renderer).setType(2).setPayload(Float.valueOf(volumeMultiplier)).send();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePlayWhenReady(boolean z, int i) {
        int i2 = 0;
        boolean z2 = z && i != -1;
        if (z2 && i != 1) {
            i2 = 1;
        }
        this.player.setPlayWhenReady(z2, i2);
    }

    private void verifyApplicationThread() {
        if (Looper.myLooper() != getApplicationLooper()) {
            Log.w(TAG, "Player is accessed on the wrong thread. See https://exoplayer.dev/issues/player-accessed-on-wrong-thread", this.hasNotifiedFullWrongThreadWarning ? null : new IllegalStateException());
            this.hasNotifiedFullWrongThreadWarning = true;
        }
    }

    private final class ComponentListener implements VideoRendererEventListener, AudioRendererEventListener, TextOutput, MetadataOutput, SurfaceHolder.Callback, TextureView.SurfaceTextureListener, AudioFocusManager.PlayerControl, AudioBecomingNoisyManager.EventListener, Player.EventListener {
        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        private ComponentListener() {
        }

        @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
        public void onVideoEnabled(DecoderCounters decoderCounters) {
            SimpleExoPlayer.this.videoDecoderCounters = decoderCounters;
            Iterator it = SimpleExoPlayer.this.videoDebugListeners.iterator();
            while (it.hasNext()) {
                ((VideoRendererEventListener) it.next()).onVideoEnabled(decoderCounters);
            }
        }

        @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
        public void onVideoDecoderInitialized(String str, long j, long j2) {
            Iterator it = SimpleExoPlayer.this.videoDebugListeners.iterator();
            while (it.hasNext()) {
                ((VideoRendererEventListener) it.next()).onVideoDecoderInitialized(str, j, j2);
            }
        }

        @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
        public void onVideoInputFormatChanged(Format format) {
            SimpleExoPlayer.this.videoFormat = format;
            Iterator it = SimpleExoPlayer.this.videoDebugListeners.iterator();
            while (it.hasNext()) {
                ((VideoRendererEventListener) it.next()).onVideoInputFormatChanged(format);
            }
        }

        @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
        public void onDroppedFrames(int i, long j) {
            Iterator it = SimpleExoPlayer.this.videoDebugListeners.iterator();
            while (it.hasNext()) {
                ((VideoRendererEventListener) it.next()).onDroppedFrames(i, j);
            }
        }

        @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
        public void onVideoSizeChanged(int i, int i2, int i3, float f) {
            Iterator it = SimpleExoPlayer.this.videoListeners.iterator();
            while (it.hasNext()) {
                com.google.android.exoplayer2.video.VideoListener videoListener = (com.google.android.exoplayer2.video.VideoListener) it.next();
                if (!SimpleExoPlayer.this.videoDebugListeners.contains(videoListener)) {
                    videoListener.onVideoSizeChanged(i, i2, i3, f);
                }
            }
            Iterator it2 = SimpleExoPlayer.this.videoDebugListeners.iterator();
            while (it2.hasNext()) {
                ((VideoRendererEventListener) it2.next()).onVideoSizeChanged(i, i2, i3, f);
            }
        }

        @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
        public void onRenderedFirstFrame(Surface surface) {
            if (SimpleExoPlayer.this.surface == surface) {
                Iterator it = SimpleExoPlayer.this.videoListeners.iterator();
                while (it.hasNext()) {
                    ((com.google.android.exoplayer2.video.VideoListener) it.next()).onRenderedFirstFrame();
                }
            }
            Iterator it2 = SimpleExoPlayer.this.videoDebugListeners.iterator();
            while (it2.hasNext()) {
                ((VideoRendererEventListener) it2.next()).onRenderedFirstFrame(surface);
            }
        }

        @Override // com.google.android.exoplayer2.video.VideoRendererEventListener
        public void onVideoDisabled(DecoderCounters decoderCounters) {
            Iterator it = SimpleExoPlayer.this.videoDebugListeners.iterator();
            while (it.hasNext()) {
                ((VideoRendererEventListener) it.next()).onVideoDisabled(decoderCounters);
            }
            SimpleExoPlayer.this.videoFormat = null;
            SimpleExoPlayer.this.videoDecoderCounters = null;
        }

        @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
        public void onAudioEnabled(DecoderCounters decoderCounters) {
            SimpleExoPlayer.this.audioDecoderCounters = decoderCounters;
            Iterator it = SimpleExoPlayer.this.audioDebugListeners.iterator();
            while (it.hasNext()) {
                ((AudioRendererEventListener) it.next()).onAudioEnabled(decoderCounters);
            }
        }

        @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
        public void onAudioSessionId(int i) {
            if (SimpleExoPlayer.this.audioSessionId == i) {
                return;
            }
            SimpleExoPlayer.this.audioSessionId = i;
            Iterator it = SimpleExoPlayer.this.audioListeners.iterator();
            while (it.hasNext()) {
                AudioListener audioListener = (AudioListener) it.next();
                if (!SimpleExoPlayer.this.audioDebugListeners.contains(audioListener)) {
                    audioListener.onAudioSessionId(i);
                }
            }
            Iterator it2 = SimpleExoPlayer.this.audioDebugListeners.iterator();
            while (it2.hasNext()) {
                ((AudioRendererEventListener) it2.next()).onAudioSessionId(i);
            }
        }

        @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
        public void onAudioDecoderInitialized(String str, long j, long j2) {
            Iterator it = SimpleExoPlayer.this.audioDebugListeners.iterator();
            while (it.hasNext()) {
                ((AudioRendererEventListener) it.next()).onAudioDecoderInitialized(str, j, j2);
            }
        }

        @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
        public void onAudioInputFormatChanged(Format format) {
            SimpleExoPlayer.this.audioFormat = format;
            Iterator it = SimpleExoPlayer.this.audioDebugListeners.iterator();
            while (it.hasNext()) {
                ((AudioRendererEventListener) it.next()).onAudioInputFormatChanged(format);
            }
        }

        @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
        public void onAudioSinkUnderrun(int i, long j, long j2) {
            Iterator it = SimpleExoPlayer.this.audioDebugListeners.iterator();
            while (it.hasNext()) {
                ((AudioRendererEventListener) it.next()).onAudioSinkUnderrun(i, j, j2);
            }
        }

        @Override // com.google.android.exoplayer2.audio.AudioRendererEventListener
        public void onAudioDisabled(DecoderCounters decoderCounters) {
            Iterator it = SimpleExoPlayer.this.audioDebugListeners.iterator();
            while (it.hasNext()) {
                ((AudioRendererEventListener) it.next()).onAudioDisabled(decoderCounters);
            }
            SimpleExoPlayer.this.audioFormat = null;
            SimpleExoPlayer.this.audioDecoderCounters = null;
            SimpleExoPlayer.this.audioSessionId = 0;
        }

        @Override // com.google.android.exoplayer2.text.TextOutput
        public void onCues(List<Cue> list) {
            SimpleExoPlayer.this.currentCues = list;
            Iterator it = SimpleExoPlayer.this.textOutputs.iterator();
            while (it.hasNext()) {
                ((TextOutput) it.next()).onCues(list);
            }
        }

        @Override // com.google.android.exoplayer2.metadata.MetadataOutput
        public void onMetadata(Metadata metadata) {
            Iterator it = SimpleExoPlayer.this.metadataOutputs.iterator();
            while (it.hasNext()) {
                ((MetadataOutput) it.next()).onMetadata(metadata);
            }
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            SimpleExoPlayer.this.setVideoSurfaceInternal(surfaceHolder.getSurface(), false);
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            SimpleExoPlayer.this.maybeNotifySurfaceSizeChanged(i2, i3);
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            SimpleExoPlayer.this.setVideoSurfaceInternal(null, false);
            SimpleExoPlayer.this.maybeNotifySurfaceSizeChanged(0, 0);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            SimpleExoPlayer.this.setVideoSurfaceInternal(new Surface(surfaceTexture), true);
            SimpleExoPlayer.this.maybeNotifySurfaceSizeChanged(i, i2);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            SimpleExoPlayer.this.maybeNotifySurfaceSizeChanged(i, i2);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            SimpleExoPlayer.this.setVideoSurfaceInternal(null, true);
            SimpleExoPlayer.this.maybeNotifySurfaceSizeChanged(0, 0);
            return true;
        }

        @Override // com.google.android.exoplayer2.AudioFocusManager.PlayerControl
        public void setVolumeMultiplier(float f) {
            SimpleExoPlayer.this.sendVolumeToRenderers();
        }

        @Override // com.google.android.exoplayer2.AudioFocusManager.PlayerControl
        public void executePlayerCommand(int i) {
            SimpleExoPlayer simpleExoPlayer = SimpleExoPlayer.this;
            simpleExoPlayer.updatePlayWhenReady(simpleExoPlayer.getPlayWhenReady(), i);
        }

        @Override // com.google.android.exoplayer2.AudioBecomingNoisyManager.EventListener
        public void onAudioBecomingNoisy() {
            SimpleExoPlayer.this.setPlayWhenReady(false);
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onLoadingChanged(boolean z) {
            if (SimpleExoPlayer.this.priorityTaskManager != null) {
                if (!z || SimpleExoPlayer.this.isPriorityTaskManagerRegistered) {
                    if (z || !SimpleExoPlayer.this.isPriorityTaskManagerRegistered) {
                        return;
                    }
                    SimpleExoPlayer.this.priorityTaskManager.remove(0);
                    SimpleExoPlayer.this.isPriorityTaskManagerRegistered = false;
                    return;
                }
                SimpleExoPlayer.this.priorityTaskManager.add(0);
                SimpleExoPlayer.this.isPriorityTaskManagerRegistered = true;
            }
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onPlayerStateChanged(boolean z, int i) {
            if (i != 1) {
                if (i == 2 || i == 3) {
                    SimpleExoPlayer.this.wakeLockManager.setStayAwake(z);
                    return;
                } else if (i != 4) {
                    return;
                }
            }
            SimpleExoPlayer.this.wakeLockManager.setStayAwake(false);
        }
    }
}