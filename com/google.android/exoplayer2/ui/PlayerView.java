package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.flac.PictureFrame;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.spherical.SingleTapListener;
import com.google.android.exoplayer2.ui.spherical.SphericalGLSurfaceView;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoDecoderGLSurfaceView;
import com.google.android.exoplayer2.video.VideoListener;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

/* loaded from: classes.dex */
public class PlayerView extends FrameLayout implements AdsLoader.AdViewProvider {
    private static final int PICTURE_TYPE_FRONT_COVER = 3;
    private static final int PICTURE_TYPE_NOT_SET = -1;
    public static final int SHOW_BUFFERING_ALWAYS = 2;
    public static final int SHOW_BUFFERING_NEVER = 0;
    public static final int SHOW_BUFFERING_WHEN_PLAYING = 1;
    private static final int SURFACE_TYPE_NONE = 0;
    private static final int SURFACE_TYPE_SPHERICAL_GL_SURFACE_VIEW = 3;
    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;
    private static final int SURFACE_TYPE_VIDEO_DECODER_GL_SURFACE_VIEW = 4;
    private final FrameLayout adOverlayFrameLayout;
    private final ImageView artworkView;
    private final View bufferingView;
    private final ComponentListener componentListener;
    private final AspectRatioFrameLayout contentFrame;
    private final PlayerControlView controller;
    private boolean controllerAutoShow;
    private boolean controllerHideDuringAds;
    private boolean controllerHideOnTouch;
    private int controllerShowTimeoutMs;
    private PlayerControlView.VisibilityListener controllerVisibilityListener;
    private CharSequence customErrorMessage;
    private Drawable defaultArtwork;
    private ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider;
    private final TextView errorMessageView;
    private boolean isTouching;
    private boolean keepContentOnPlayerReset;
    private final FrameLayout overlayFrameLayout;
    private Player player;
    private int showBuffering;
    private final View shutterView;
    private final SubtitleView subtitleView;
    private final View surfaceView;
    private int textureViewRotation;
    private boolean useArtwork;
    private boolean useController;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShowBuffering {
    }

    private boolean isDpadKey(int i) {
        return i == 19 || i == 270 || i == 22 || i == 271 || i == 20 || i == 269 || i == 21 || i == 268 || i == 23;
    }

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PlayerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int i2;
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        int i3;
        int i4;
        boolean z6;
        int i5;
        int i6;
        int i7;
        int i8;
        boolean z7;
        ComponentListener componentListener = new ComponentListener();
        this.componentListener = componentListener;
        if (isInEditMode()) {
            this.contentFrame = null;
            this.shutterView = null;
            this.surfaceView = null;
            this.artworkView = null;
            this.subtitleView = null;
            this.bufferingView = null;
            this.errorMessageView = null;
            this.controller = null;
            this.adOverlayFrameLayout = null;
            this.overlayFrameLayout = null;
            ImageView imageView = new ImageView(context);
            if (Util.SDK_INT >= 23) {
                configureEditModeLogoV23(getResources(), imageView);
            } else {
                configureEditModeLogo(getResources(), imageView);
            }
            addView(imageView);
            return;
        }
        int i9 = R.layout.exo_player_view;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.PlayerView, 0, 0);
            try {
                boolean hasValue = obtainStyledAttributes.hasValue(R.styleable.PlayerView_shutter_background_color);
                int color = obtainStyledAttributes.getColor(R.styleable.PlayerView_shutter_background_color, 0);
                int resourceId = obtainStyledAttributes.getResourceId(R.styleable.PlayerView_player_layout_id, i9);
                boolean z8 = obtainStyledAttributes.getBoolean(R.styleable.PlayerView_use_artwork, true);
                int resourceId2 = obtainStyledAttributes.getResourceId(R.styleable.PlayerView_default_artwork, 0);
                boolean z9 = obtainStyledAttributes.getBoolean(R.styleable.PlayerView_use_controller, true);
                int i10 = obtainStyledAttributes.getInt(R.styleable.PlayerView_surface_type, 1);
                int i11 = obtainStyledAttributes.getInt(R.styleable.PlayerView_resize_mode, 0);
                int i12 = obtainStyledAttributes.getInt(R.styleable.PlayerView_show_timeout, 5000);
                boolean z10 = obtainStyledAttributes.getBoolean(R.styleable.PlayerView_hide_on_touch, true);
                boolean z11 = obtainStyledAttributes.getBoolean(R.styleable.PlayerView_auto_show, true);
                i4 = obtainStyledAttributes.getInteger(R.styleable.PlayerView_show_buffering, 0);
                this.keepContentOnPlayerReset = obtainStyledAttributes.getBoolean(R.styleable.PlayerView_keep_content_on_player_reset, this.keepContentOnPlayerReset);
                boolean z12 = obtainStyledAttributes.getBoolean(R.styleable.PlayerView_hide_during_ads, true);
                obtainStyledAttributes.recycle();
                z2 = z12;
                i2 = i10;
                i8 = i11;
                i6 = resourceId2;
                z5 = z9;
                i5 = color;
                z4 = z8;
                z6 = hasValue;
                z3 = z10;
                z = z11;
                i3 = i12;
                i7 = resourceId;
            } catch (Throwable th) {
                obtainStyledAttributes.recycle();
                throw th;
            }
        } else {
            i2 = 1;
            z = true;
            z2 = true;
            z3 = true;
            z4 = true;
            z5 = true;
            i3 = 5000;
            i4 = 0;
            z6 = false;
            i5 = 0;
            i6 = 0;
            i7 = i9;
            i8 = 0;
        }
        LayoutInflater.from(context).inflate(i7, this);
        setDescendantFocusability(262144);
        AspectRatioFrameLayout aspectRatioFrameLayout = (AspectRatioFrameLayout) findViewById(R.id.exo_content_frame);
        this.contentFrame = aspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            setResizeModeRaw(aspectRatioFrameLayout, i8);
        }
        View findViewById = findViewById(R.id.exo_shutter);
        this.shutterView = findViewById;
        if (findViewById != null && z6) {
            findViewById.setBackgroundColor(i5);
        }
        if (aspectRatioFrameLayout != null && i2 != 0) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
            if (i2 == 2) {
                this.surfaceView = new TextureView(context);
            } else if (i2 == 3) {
                SphericalGLSurfaceView sphericalGLSurfaceView = new SphericalGLSurfaceView(context);
                sphericalGLSurfaceView.setSingleTapListener(componentListener);
                this.surfaceView = sphericalGLSurfaceView;
            } else if (i2 == 4) {
                this.surfaceView = new VideoDecoderGLSurfaceView(context);
            } else {
                this.surfaceView = new SurfaceView(context);
            }
            this.surfaceView.setLayoutParams(layoutParams);
            aspectRatioFrameLayout.addView(this.surfaceView, 0);
        } else {
            this.surfaceView = null;
        }
        this.adOverlayFrameLayout = (FrameLayout) findViewById(R.id.exo_ad_overlay);
        this.overlayFrameLayout = (FrameLayout) findViewById(R.id.exo_overlay);
        ImageView imageView2 = (ImageView) findViewById(R.id.exo_artwork);
        this.artworkView = imageView2;
        this.useArtwork = z4 && imageView2 != null;
        if (i6 != 0) {
            this.defaultArtwork = ContextCompat.getDrawable(getContext(), i6);
        }
        SubtitleView subtitleView = (SubtitleView) findViewById(R.id.exo_subtitles);
        this.subtitleView = subtitleView;
        if (subtitleView != null) {
            subtitleView.setUserDefaultStyle();
            subtitleView.setUserDefaultTextSize();
        }
        View findViewById2 = findViewById(R.id.exo_buffering);
        this.bufferingView = findViewById2;
        if (findViewById2 != null) {
            findViewById2.setVisibility(8);
        }
        this.showBuffering = i4;
        TextView textView = (TextView) findViewById(R.id.exo_error_message);
        this.errorMessageView = textView;
        if (textView != null) {
            textView.setVisibility(8);
        }
        PlayerControlView playerControlView = (PlayerControlView) findViewById(R.id.exo_controller);
        View findViewById3 = findViewById(R.id.exo_controller_placeholder);
        if (playerControlView != null) {
            this.controller = playerControlView;
            z7 = false;
        } else if (findViewById3 != null) {
            z7 = false;
            PlayerControlView playerControlView2 = new PlayerControlView(context, null, 0, attributeSet);
            this.controller = playerControlView2;
            playerControlView2.setId(R.id.exo_controller);
            playerControlView2.setLayoutParams(findViewById3.getLayoutParams());
            ViewGroup viewGroup = (ViewGroup) findViewById3.getParent();
            int indexOfChild = viewGroup.indexOfChild(findViewById3);
            viewGroup.removeView(findViewById3);
            viewGroup.addView(playerControlView2, indexOfChild);
        } else {
            z7 = false;
            this.controller = null;
        }
        PlayerControlView playerControlView3 = this.controller;
        this.controllerShowTimeoutMs = playerControlView3 != null ? i3 : z7 ? 1 : 0;
        this.controllerHideOnTouch = z3;
        this.controllerAutoShow = z;
        this.controllerHideDuringAds = z2;
        if (z5 && playerControlView3 != null) {
            z7 = true;
        }
        this.useController = z7;
        hideController();
        updateContentDescription();
        PlayerControlView playerControlView4 = this.controller;
        if (playerControlView4 != null) {
            playerControlView4.addVisibilityListener(componentListener);
        }
    }

    public static void switchTargetView(Player player, PlayerView playerView, PlayerView playerView2) {
        if (playerView == playerView2) {
            return;
        }
        if (playerView2 != null) {
            playerView2.setPlayer(player);
        }
        if (playerView != null) {
            playerView.setPlayer(null);
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        Assertions.checkArgument(player == null || player.getApplicationLooper() == Looper.getMainLooper());
        Player player2 = this.player;
        if (player2 == player) {
            return;
        }
        if (player2 != null) {
            player2.removeListener(this.componentListener);
            Player.VideoComponent videoComponent = player2.getVideoComponent();
            if (videoComponent != null) {
                videoComponent.removeVideoListener(this.componentListener);
                View view = this.surfaceView;
                if (view instanceof TextureView) {
                    videoComponent.clearVideoTextureView((TextureView) view);
                } else if (view instanceof SphericalGLSurfaceView) {
                    ((SphericalGLSurfaceView) view).setVideoComponent(null);
                } else if (view instanceof VideoDecoderGLSurfaceView) {
                    videoComponent.setVideoDecoderOutputBufferRenderer(null);
                } else if (view instanceof SurfaceView) {
                    videoComponent.clearVideoSurfaceView((SurfaceView) view);
                }
            }
            Player.TextComponent textComponent = player2.getTextComponent();
            if (textComponent != null) {
                textComponent.removeTextOutput(this.componentListener);
            }
        }
        this.player = player;
        if (useController()) {
            this.controller.setPlayer(player);
        }
        SubtitleView subtitleView = this.subtitleView;
        if (subtitleView != null) {
            subtitleView.setCues(null);
        }
        updateBuffering();
        updateErrorMessage();
        updateForCurrentTrackSelections(true);
        if (player != null) {
            Player.VideoComponent videoComponent2 = player.getVideoComponent();
            if (videoComponent2 != null) {
                View view2 = this.surfaceView;
                if (view2 instanceof TextureView) {
                    videoComponent2.setVideoTextureView((TextureView) view2);
                } else if (view2 instanceof SphericalGLSurfaceView) {
                    ((SphericalGLSurfaceView) view2).setVideoComponent(videoComponent2);
                } else if (view2 instanceof VideoDecoderGLSurfaceView) {
                    videoComponent2.setVideoDecoderOutputBufferRenderer(((VideoDecoderGLSurfaceView) view2).getVideoDecoderOutputBufferRenderer());
                } else if (view2 instanceof SurfaceView) {
                    videoComponent2.setVideoSurfaceView((SurfaceView) view2);
                }
                videoComponent2.addVideoListener(this.componentListener);
            }
            Player.TextComponent textComponent2 = player.getTextComponent();
            if (textComponent2 != null) {
                textComponent2.addTextOutput(this.componentListener);
            }
            player.addListener(this.componentListener);
            maybeShowController(false);
            return;
        }
        hideController();
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        View view = this.surfaceView;
        if (view instanceof SurfaceView) {
            view.setVisibility(i);
        }
    }

    public void setResizeMode(int i) {
        Assertions.checkStateNotNull(this.contentFrame);
        this.contentFrame.setResizeMode(i);
    }

    public int getResizeMode() {
        Assertions.checkStateNotNull(this.contentFrame);
        return this.contentFrame.getResizeMode();
    }

    public boolean getUseArtwork() {
        return this.useArtwork;
    }

    public void setUseArtwork(boolean z) {
        Assertions.checkState((z && this.artworkView == null) ? false : true);
        if (this.useArtwork != z) {
            this.useArtwork = z;
            updateForCurrentTrackSelections(false);
        }
    }

    public Drawable getDefaultArtwork() {
        return this.defaultArtwork;
    }

    @Deprecated
    public void setDefaultArtwork(Bitmap bitmap) {
        setDefaultArtwork(bitmap == null ? null : new BitmapDrawable(getResources(), bitmap));
    }

    public void setDefaultArtwork(Drawable drawable) {
        if (this.defaultArtwork != drawable) {
            this.defaultArtwork = drawable;
            updateForCurrentTrackSelections(false);
        }
    }

    public boolean getUseController() {
        return this.useController;
    }

    public void setUseController(boolean z) {
        Assertions.checkState((z && this.controller == null) ? false : true);
        if (this.useController == z) {
            return;
        }
        this.useController = z;
        if (useController()) {
            this.controller.setPlayer(this.player);
        } else {
            PlayerControlView playerControlView = this.controller;
            if (playerControlView != null) {
                playerControlView.hide();
                this.controller.setPlayer(null);
            }
        }
        updateContentDescription();
    }

    public void setShutterBackgroundColor(int i) {
        View view = this.shutterView;
        if (view != null) {
            view.setBackgroundColor(i);
        }
    }

    public void setKeepContentOnPlayerReset(boolean z) {
        if (this.keepContentOnPlayerReset != z) {
            this.keepContentOnPlayerReset = z;
            updateForCurrentTrackSelections(false);
        }
    }

    @Deprecated
    public void setShowBuffering(boolean z) {
        setShowBuffering(z ? 1 : 0);
    }

    public void setShowBuffering(int i) {
        if (this.showBuffering != i) {
            this.showBuffering = i;
            updateBuffering();
        }
    }

    public void setErrorMessageProvider(ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider) {
        if (this.errorMessageProvider != errorMessageProvider) {
            this.errorMessageProvider = errorMessageProvider;
            updateErrorMessage();
        }
    }

    public void setCustomErrorMessage(CharSequence charSequence) {
        Assertions.checkState(this.errorMessageView != null);
        this.customErrorMessage = charSequence;
        updateErrorMessage();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        Player player = this.player;
        if (player != null && player.isPlayingAd()) {
            return super.dispatchKeyEvent(keyEvent);
        }
        boolean isDpadKey = isDpadKey(keyEvent.getKeyCode());
        if (isDpadKey && useController() && !this.controller.isVisible()) {
            maybeShowController(true);
            return true;
        }
        if (dispatchMediaKeyEvent(keyEvent) || super.dispatchKeyEvent(keyEvent)) {
            maybeShowController(true);
            return true;
        }
        if (isDpadKey && useController()) {
            maybeShowController(true);
        }
        return false;
    }

    public boolean dispatchMediaKeyEvent(KeyEvent keyEvent) {
        return useController() && this.controller.dispatchMediaKeyEvent(keyEvent);
    }

    public boolean isControllerVisible() {
        PlayerControlView playerControlView = this.controller;
        return playerControlView != null && playerControlView.isVisible();
    }

    public void showController() {
        showController(shouldShowControllerIndefinitely());
    }

    public void hideController() {
        PlayerControlView playerControlView = this.controller;
        if (playerControlView != null) {
            playerControlView.hide();
        }
    }

    public int getControllerShowTimeoutMs() {
        return this.controllerShowTimeoutMs;
    }

    public void setControllerShowTimeoutMs(int i) {
        Assertions.checkStateNotNull(this.controller);
        this.controllerShowTimeoutMs = i;
        if (this.controller.isVisible()) {
            showController();
        }
    }

    public boolean getControllerHideOnTouch() {
        return this.controllerHideOnTouch;
    }

    public void setControllerHideOnTouch(boolean z) {
        Assertions.checkStateNotNull(this.controller);
        this.controllerHideOnTouch = z;
        updateContentDescription();
    }

    public boolean getControllerAutoShow() {
        return this.controllerAutoShow;
    }

    public void setControllerAutoShow(boolean z) {
        this.controllerAutoShow = z;
    }

    public void setControllerHideDuringAds(boolean z) {
        this.controllerHideDuringAds = z;
    }

    public void setControllerVisibilityListener(PlayerControlView.VisibilityListener visibilityListener) {
        Assertions.checkStateNotNull(this.controller);
        PlayerControlView.VisibilityListener visibilityListener2 = this.controllerVisibilityListener;
        if (visibilityListener2 == visibilityListener) {
            return;
        }
        if (visibilityListener2 != null) {
            this.controller.removeVisibilityListener(visibilityListener2);
        }
        this.controllerVisibilityListener = visibilityListener;
        if (visibilityListener != null) {
            this.controller.addVisibilityListener(visibilityListener);
        }
    }

    public void setPlaybackPreparer(PlaybackPreparer playbackPreparer) {
        Assertions.checkStateNotNull(this.controller);
        this.controller.setPlaybackPreparer(playbackPreparer);
    }

    public void setControlDispatcher(ControlDispatcher controlDispatcher) {
        Assertions.checkStateNotNull(this.controller);
        this.controller.setControlDispatcher(controlDispatcher);
    }

    public void setRewindIncrementMs(int i) {
        Assertions.checkStateNotNull(this.controller);
        this.controller.setRewindIncrementMs(i);
    }

    public void setFastForwardIncrementMs(int i) {
        Assertions.checkStateNotNull(this.controller);
        this.controller.setFastForwardIncrementMs(i);
    }

    public void setRepeatToggleModes(int i) {
        Assertions.checkStateNotNull(this.controller);
        this.controller.setRepeatToggleModes(i);
    }

    public void setShowShuffleButton(boolean z) {
        Assertions.checkStateNotNull(this.controller);
        this.controller.setShowShuffleButton(z);
    }

    public void setShowMultiWindowTimeBar(boolean z) {
        Assertions.checkStateNotNull(this.controller);
        this.controller.setShowMultiWindowTimeBar(z);
    }

    public void setExtraAdGroupMarkers(long[] jArr, boolean[] zArr) {
        Assertions.checkStateNotNull(this.controller);
        this.controller.setExtraAdGroupMarkers(jArr, zArr);
    }

    public void setAspectRatioListener(AspectRatioFrameLayout.AspectRatioListener aspectRatioListener) {
        Assertions.checkStateNotNull(this.contentFrame);
        this.contentFrame.setAspectRatioListener(aspectRatioListener);
    }

    public View getVideoSurfaceView() {
        return this.surfaceView;
    }

    public FrameLayout getOverlayFrameLayout() {
        return this.overlayFrameLayout;
    }

    public SubtitleView getSubtitleView() {
        return this.subtitleView;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!useController() || this.player == null) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            this.isTouching = true;
            return true;
        }
        if (action != 1 || !this.isTouching) {
            return false;
        }
        this.isTouching = false;
        performClick();
        return true;
    }

    @Override // android.view.View
    public boolean performClick() {
        super.performClick();
        return toggleControllerVisibility();
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        if (!useController() || this.player == null) {
            return false;
        }
        maybeShowController(true);
        return true;
    }

    public void onResume() {
        View view = this.surfaceView;
        if (view instanceof SphericalGLSurfaceView) {
            ((SphericalGLSurfaceView) view).onResume();
        }
    }

    public void onPause() {
        View view = this.surfaceView;
        if (view instanceof SphericalGLSurfaceView) {
            ((SphericalGLSurfaceView) view).onPause();
        }
    }

    protected void onContentAspectRatioChanged(float f, AspectRatioFrameLayout aspectRatioFrameLayout, View view) {
        if (aspectRatioFrameLayout != null) {
            if (view instanceof SphericalGLSurfaceView) {
                f = 0.0f;
            }
            aspectRatioFrameLayout.setAspectRatio(f);
        }
    }

    @Override // com.google.android.exoplayer2.source.ads.AdsLoader.AdViewProvider
    public ViewGroup getAdViewGroup() {
        return (ViewGroup) Assertions.checkStateNotNull(this.adOverlayFrameLayout, "exo_ad_overlay must be present for ad playback");
    }

    @Override // com.google.android.exoplayer2.source.ads.AdsLoader.AdViewProvider
    public View[] getAdOverlayViews() {
        ArrayList arrayList = new ArrayList();
        FrameLayout frameLayout = this.overlayFrameLayout;
        if (frameLayout != null) {
            arrayList.add(frameLayout);
        }
        PlayerControlView playerControlView = this.controller;
        if (playerControlView != null) {
            arrayList.add(playerControlView);
        }
        return (View[]) arrayList.toArray(new View[0]);
    }

    @EnsuresNonNullIf(expression = {"controller"}, result = true)
    private boolean useController() {
        if (!this.useController) {
            return false;
        }
        Assertions.checkStateNotNull(this.controller);
        return true;
    }

    @EnsuresNonNullIf(expression = {"artworkView"}, result = true)
    private boolean useArtwork() {
        if (!this.useArtwork) {
            return false;
        }
        Assertions.checkStateNotNull(this.artworkView);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean toggleControllerVisibility() {
        if (!useController() || this.player == null) {
            return false;
        }
        if (!this.controller.isVisible()) {
            maybeShowController(true);
        } else if (this.controllerHideOnTouch) {
            this.controller.hide();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeShowController(boolean z) {
        if (!(isPlayingAd() && this.controllerHideDuringAds) && useController()) {
            boolean z2 = this.controller.isVisible() && this.controller.getShowTimeoutMs() <= 0;
            boolean shouldShowControllerIndefinitely = shouldShowControllerIndefinitely();
            if (z || z2 || shouldShowControllerIndefinitely) {
                showController(shouldShowControllerIndefinitely);
            }
        }
    }

    private boolean shouldShowControllerIndefinitely() {
        Player player = this.player;
        if (player == null) {
            return true;
        }
        int playbackState = player.getPlaybackState();
        return this.controllerAutoShow && (playbackState == 1 || playbackState == 4 || !this.player.getPlayWhenReady());
    }

    private void showController(boolean z) {
        if (useController()) {
            this.controller.setShowTimeoutMs(z ? 0 : this.controllerShowTimeoutMs);
            this.controller.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPlayingAd() {
        Player player = this.player;
        return player != null && player.isPlayingAd() && this.player.getPlayWhenReady();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForCurrentTrackSelections(boolean z) {
        Player player = this.player;
        if (player == null || player.getCurrentTrackGroups().isEmpty()) {
            if (this.keepContentOnPlayerReset) {
                return;
            }
            hideArtwork();
            closeShutter();
            return;
        }
        if (z && !this.keepContentOnPlayerReset) {
            closeShutter();
        }
        TrackSelectionArray currentTrackSelections = player.getCurrentTrackSelections();
        for (int i = 0; i < currentTrackSelections.length; i++) {
            if (player.getRendererType(i) == 2 && currentTrackSelections.get(i) != null) {
                hideArtwork();
                return;
            }
        }
        closeShutter();
        if (useArtwork()) {
            for (int i2 = 0; i2 < currentTrackSelections.length; i2++) {
                TrackSelection trackSelection = currentTrackSelections.get(i2);
                if (trackSelection != null) {
                    for (int i3 = 0; i3 < trackSelection.length(); i3++) {
                        Metadata metadata = trackSelection.getFormat(i3).metadata;
                        if (metadata != null && setArtworkFromMetadata(metadata)) {
                            return;
                        }
                    }
                }
            }
            if (setDrawableArtwork(this.defaultArtwork)) {
                return;
            }
        }
        hideArtwork();
    }

    @RequiresNonNull({"artworkView"})
    private boolean setArtworkFromMetadata(Metadata metadata) {
        byte[] bArr;
        int i;
        boolean z = false;
        int i2 = -1;
        for (int i3 = 0; i3 < metadata.length(); i3++) {
            Metadata.Entry entry = metadata.get(i3);
            if (entry instanceof ApicFrame) {
                ApicFrame apicFrame = (ApicFrame) entry;
                bArr = apicFrame.pictureData;
                i = apicFrame.pictureType;
            } else if (entry instanceof PictureFrame) {
                PictureFrame pictureFrame = (PictureFrame) entry;
                bArr = pictureFrame.pictureData;
                i = pictureFrame.pictureType;
            } else {
                continue;
            }
            if (i2 == -1 || i == 3) {
                z = setDrawableArtwork(new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(bArr, 0, bArr.length)));
                if (i == 3) {
                    break;
                }
                i2 = i;
            }
        }
        return z;
    }

    @RequiresNonNull({"artworkView"})
    private boolean setDrawableArtwork(Drawable drawable) {
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                onContentAspectRatioChanged(intrinsicWidth / intrinsicHeight, this.contentFrame, this.artworkView);
                this.artworkView.setImageDrawable(drawable);
                this.artworkView.setVisibility(0);
                return true;
            }
        }
        return false;
    }

    private void hideArtwork() {
        ImageView imageView = this.artworkView;
        if (imageView != null) {
            imageView.setImageResource(android.R.color.transparent);
            this.artworkView.setVisibility(4);
        }
    }

    private void closeShutter() {
        View view = this.shutterView;
        if (view != null) {
            view.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x001d, code lost:
    
        if (r4.player.getPlayWhenReady() == false) goto L14;
     */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0026  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void updateBuffering() {
        /*
            r4 = this;
            android.view.View r0 = r4.bufferingView
            if (r0 == 0) goto L2b
            com.google.android.exoplayer2.Player r0 = r4.player
            r1 = 0
            if (r0 == 0) goto L20
            int r0 = r0.getPlaybackState()
            r2 = 2
            if (r0 != r2) goto L20
            int r0 = r4.showBuffering
            r3 = 1
            if (r0 == r2) goto L21
            if (r0 != r3) goto L20
            com.google.android.exoplayer2.Player r0 = r4.player
            boolean r0 = r0.getPlayWhenReady()
            if (r0 == 0) goto L20
            goto L21
        L20:
            r3 = r1
        L21:
            android.view.View r0 = r4.bufferingView
            if (r3 == 0) goto L26
            goto L28
        L26:
            r1 = 8
        L28:
            r0.setVisibility(r1)
        L2b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ui.PlayerView.updateBuffering():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateErrorMessage() {
        ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider;
        TextView textView = this.errorMessageView;
        if (textView != null) {
            CharSequence charSequence = this.customErrorMessage;
            if (charSequence != null) {
                textView.setText(charSequence);
                this.errorMessageView.setVisibility(0);
                return;
            }
            Player player = this.player;
            ExoPlaybackException playbackError = player != null ? player.getPlaybackError() : null;
            if (playbackError != null && (errorMessageProvider = this.errorMessageProvider) != null) {
                this.errorMessageView.setText((CharSequence) errorMessageProvider.getErrorMessage(playbackError).second);
                this.errorMessageView.setVisibility(0);
            } else {
                this.errorMessageView.setVisibility(8);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateContentDescription() {
        PlayerControlView playerControlView = this.controller;
        if (playerControlView == null || !this.useController) {
            setContentDescription(null);
        } else if (playerControlView.getVisibility() == 0) {
            setContentDescription(this.controllerHideOnTouch ? getResources().getString(R.string.exo_controls_hide) : null);
        } else {
            setContentDescription(getResources().getString(R.string.exo_controls_show));
        }
    }

    private static void configureEditModeLogoV23(Resources resources, ImageView imageView) {
        imageView.setImageDrawable(resources.getDrawable(R.drawable.exo_edit_mode_logo, null));
        imageView.setBackgroundColor(resources.getColor(R.color.exo_edit_mode_background_color, null));
    }

    private static void configureEditModeLogo(Resources resources, ImageView imageView) {
        imageView.setImageDrawable(resources.getDrawable(R.drawable.exo_edit_mode_logo));
        imageView.setBackgroundColor(resources.getColor(R.color.exo_edit_mode_background_color));
    }

    private static void setResizeModeRaw(AspectRatioFrameLayout aspectRatioFrameLayout, int i) {
        aspectRatioFrameLayout.setResizeMode(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void applyTextureViewRotation(TextureView textureView, int i) {
        Matrix matrix = new Matrix();
        float width = textureView.getWidth();
        float height = textureView.getHeight();
        if (width != 0.0f && height != 0.0f && i != 0) {
            float f = width / 2.0f;
            float f2 = height / 2.0f;
            matrix.postRotate(i, f, f2);
            RectF rectF = new RectF(0.0f, 0.0f, width, height);
            RectF rectF2 = new RectF();
            matrix.mapRect(rectF2, rectF);
            matrix.postScale(width / rectF2.width(), height / rectF2.height(), f, f2);
        }
        textureView.setTransform(matrix);
    }

    private final class ComponentListener implements Player.EventListener, TextOutput, VideoListener, View.OnLayoutChangeListener, SingleTapListener, PlayerControlView.VisibilityListener {
        private ComponentListener() {
        }

        @Override // com.google.android.exoplayer2.text.TextOutput
        public void onCues(List<Cue> list) {
            if (PlayerView.this.subtitleView != null) {
                PlayerView.this.subtitleView.onCues(list);
            }
        }

        @Override // com.google.android.exoplayer2.video.VideoListener
        public void onVideoSizeChanged(int i, int i2, int i3, float f) {
            float f2 = (i2 == 0 || i == 0) ? 1.0f : (i * f) / i2;
            if (PlayerView.this.surfaceView instanceof TextureView) {
                if (i3 == 90 || i3 == 270) {
                    f2 = 1.0f / f2;
                }
                if (PlayerView.this.textureViewRotation != 0) {
                    PlayerView.this.surfaceView.removeOnLayoutChangeListener(this);
                }
                PlayerView.this.textureViewRotation = i3;
                if (PlayerView.this.textureViewRotation != 0) {
                    PlayerView.this.surfaceView.addOnLayoutChangeListener(this);
                }
                PlayerView.applyTextureViewRotation((TextureView) PlayerView.this.surfaceView, PlayerView.this.textureViewRotation);
            }
            PlayerView playerView = PlayerView.this;
            playerView.onContentAspectRatioChanged(f2, playerView.contentFrame, PlayerView.this.surfaceView);
        }

        @Override // com.google.android.exoplayer2.video.VideoListener
        public void onRenderedFirstFrame() {
            if (PlayerView.this.shutterView != null) {
                PlayerView.this.shutterView.setVisibility(4);
            }
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
            PlayerView.this.updateForCurrentTrackSelections(false);
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onPlayerStateChanged(boolean z, int i) {
            PlayerView.this.updateBuffering();
            PlayerView.this.updateErrorMessage();
            if (!PlayerView.this.isPlayingAd() || !PlayerView.this.controllerHideDuringAds) {
                PlayerView.this.maybeShowController(false);
            } else {
                PlayerView.this.hideController();
            }
        }

        @Override // com.google.android.exoplayer2.Player.EventListener
        public void onPositionDiscontinuity(int i) {
            if (PlayerView.this.isPlayingAd() && PlayerView.this.controllerHideDuringAds) {
                PlayerView.this.hideController();
            }
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            PlayerView.applyTextureViewRotation((TextureView) view, PlayerView.this.textureViewRotation);
        }

        @Override // com.google.android.exoplayer2.ui.spherical.SingleTapListener
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return PlayerView.this.toggleControllerVisibility();
        }

        @Override // com.google.android.exoplayer2.ui.PlayerControlView.VisibilityListener
        public void onVisibilityChange(int i) {
            PlayerView.this.updateContentDescription();
        }
    }
}