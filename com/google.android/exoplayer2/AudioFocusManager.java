package com.google.android.exoplayer2;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Handler;
import com.google.android.exoplayer2.AudioFocusManager;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* loaded from: classes.dex */
final class AudioFocusManager {
    private static final int AUDIO_FOCUS_STATE_HAVE_FOCUS = 1;
    private static final int AUDIO_FOCUS_STATE_LOSS_TRANSIENT = 2;
    private static final int AUDIO_FOCUS_STATE_LOSS_TRANSIENT_DUCK = 3;
    private static final int AUDIO_FOCUS_STATE_LOST_FOCUS = -1;
    private static final int AUDIO_FOCUS_STATE_NO_FOCUS = 0;
    public static final int PLAYER_COMMAND_DO_NOT_PLAY = -1;
    public static final int PLAYER_COMMAND_PLAY_WHEN_READY = 1;
    public static final int PLAYER_COMMAND_WAIT_FOR_CALLBACK = 0;
    private static final String TAG = "AudioFocusManager";
    private static final float VOLUME_MULTIPLIER_DEFAULT = 1.0f;
    private static final float VOLUME_MULTIPLIER_DUCK = 0.2f;
    private AudioAttributes audioAttributes;
    private AudioFocusRequest audioFocusRequest;
    private final AudioManager audioManager;
    private int focusGain;
    private final AudioFocusListener focusListener;
    private final PlayerControl playerControl;
    private boolean rebuildAudioFocusRequest;
    private float volumeMultiplier = 1.0f;
    private int audioFocusState = 0;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayerCommand {
    }

    public interface PlayerControl {
        void executePlayerCommand(int i);

        void setVolumeMultiplier(float f);
    }

    private int handleIdle(boolean z) {
        return z ? 1 : -1;
    }

    public AudioFocusManager(Context context, Handler handler, PlayerControl playerControl) {
        this.audioManager = (AudioManager) context.getApplicationContext().getSystemService("audio");
        this.playerControl = playerControl;
        this.focusListener = new AudioFocusListener(handler);
    }

    public float getVolumeMultiplier() {
        return this.volumeMultiplier;
    }

    public int setAudioAttributes(AudioAttributes audioAttributes, boolean z, int i) {
        if (!Util.areEqual(this.audioAttributes, audioAttributes)) {
            this.audioAttributes = audioAttributes;
            int convertAudioAttributesToFocusGain = convertAudioAttributesToFocusGain(audioAttributes);
            this.focusGain = convertAudioAttributesToFocusGain;
            Assertions.checkArgument(convertAudioAttributesToFocusGain == 1 || convertAudioAttributesToFocusGain == 0, "Automatic handling of audio focus is only available for USAGE_MEDIA and USAGE_GAME.");
            if (z && (i == 2 || i == 3)) {
                return requestAudioFocus();
            }
        }
        if (i == 1) {
            return handleIdle(z);
        }
        return handlePrepare(z);
    }

    public int handlePrepare(boolean z) {
        if (z) {
            return requestAudioFocus();
        }
        return -1;
    }

    public int handleSetPlayWhenReady(boolean z, int i) {
        if (z) {
            return i == 1 ? handleIdle(z) : requestAudioFocus();
        }
        abandonAudioFocus();
        return -1;
    }

    public void handleStop() {
        abandonAudioFocus(true);
    }

    AudioManager.OnAudioFocusChangeListener getFocusListener() {
        return this.focusListener;
    }

    private int requestAudioFocus() {
        int requestAudioFocusDefault;
        if (this.focusGain == 0) {
            if (this.audioFocusState != 0) {
                abandonAudioFocus(true);
            }
            return 1;
        }
        if (this.audioFocusState == 0) {
            if (Util.SDK_INT >= 26) {
                requestAudioFocusDefault = requestAudioFocusV26();
            } else {
                requestAudioFocusDefault = requestAudioFocusDefault();
            }
            this.audioFocusState = requestAudioFocusDefault == 1 ? 1 : 0;
        }
        int i = this.audioFocusState;
        if (i == 0) {
            return -1;
        }
        return i == 2 ? 0 : 1;
    }

    private void abandonAudioFocus() {
        abandonAudioFocus(false);
    }

    private void abandonAudioFocus(boolean z) {
        int i = this.focusGain;
        if (i == 0 && this.audioFocusState == 0) {
            return;
        }
        if (i != 1 || this.audioFocusState == -1 || z) {
            if (Util.SDK_INT >= 26) {
                abandonAudioFocusV26();
            } else {
                abandonAudioFocusDefault();
            }
            this.audioFocusState = 0;
        }
    }

    private int requestAudioFocusDefault() {
        return this.audioManager.requestAudioFocus(this.focusListener, Util.getStreamTypeForAudioUsage(((AudioAttributes) Assertions.checkNotNull(this.audioAttributes)).usage), this.focusGain);
    }

    private int requestAudioFocusV26() {
        AudioFocusRequest audioFocusRequest = this.audioFocusRequest;
        if (audioFocusRequest == null || this.rebuildAudioFocusRequest) {
            this.audioFocusRequest = (audioFocusRequest == null ? new AudioFocusRequest.Builder(this.focusGain) : new AudioFocusRequest.Builder(this.audioFocusRequest)).setAudioAttributes(((AudioAttributes) Assertions.checkNotNull(this.audioAttributes)).getAudioAttributesV21()).setWillPauseWhenDucked(willPauseWhenDucked()).setOnAudioFocusChangeListener(this.focusListener).build();
            this.rebuildAudioFocusRequest = false;
        }
        return this.audioManager.requestAudioFocus(this.audioFocusRequest);
    }

    private void abandonAudioFocusDefault() {
        this.audioManager.abandonAudioFocus(this.focusListener);
    }

    private void abandonAudioFocusV26() {
        AudioFocusRequest audioFocusRequest = this.audioFocusRequest;
        if (audioFocusRequest != null) {
            this.audioManager.abandonAudioFocusRequest(audioFocusRequest);
        }
    }

    private boolean willPauseWhenDucked() {
        AudioAttributes audioAttributes = this.audioAttributes;
        return audioAttributes != null && audioAttributes.contentType == 1;
    }

    private static int convertAudioAttributesToFocusGain(AudioAttributes audioAttributes) {
        if (audioAttributes == null) {
            return 0;
        }
        switch (audioAttributes.usage) {
            case 0:
                Log.w(TAG, "Specify a proper usage in the audio attributes for audio focus handling. Using AUDIOFOCUS_GAIN by default.");
                return 1;
            case 1:
            case 14:
                return 1;
            case 2:
            case 4:
                return 2;
            case 3:
                return 0;
            case 11:
                if (audioAttributes.contentType == 1) {
                    return 2;
                }
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 12:
            case 13:
                return 3;
            case 15:
            default:
                Log.w(TAG, "Unidentified audio usage: " + audioAttributes.usage);
                return 0;
            case 16:
                return Util.SDK_INT >= 19 ? 4 : 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAudioFocusChange(int i) {
        if (i != -3) {
            if (i == -2) {
                this.audioFocusState = 2;
            } else if (i == -1) {
                this.audioFocusState = -1;
            } else if (i == 1) {
                this.audioFocusState = 1;
            } else {
                Log.w(TAG, "Unknown focus change type: " + i);
                return;
            }
        } else if (willPauseWhenDucked()) {
            this.audioFocusState = 2;
        } else {
            this.audioFocusState = 3;
        }
        int i2 = this.audioFocusState;
        if (i2 == -1) {
            this.playerControl.executePlayerCommand(-1);
            abandonAudioFocus(true);
        } else if (i2 != 0) {
            if (i2 == 1) {
                this.playerControl.executePlayerCommand(1);
            } else if (i2 == 2) {
                this.playerControl.executePlayerCommand(0);
            } else if (i2 != 3) {
                throw new IllegalStateException("Unknown audio focus state: " + this.audioFocusState);
            }
        }
        float f = this.audioFocusState == 3 ? VOLUME_MULTIPLIER_DUCK : 1.0f;
        if (this.volumeMultiplier != f) {
            this.volumeMultiplier = f;
            this.playerControl.setVolumeMultiplier(f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class AudioFocusListener implements AudioManager.OnAudioFocusChangeListener {
        private final Handler eventHandler;

        public AudioFocusListener(Handler handler) {
            this.eventHandler = handler;
        }

        /* renamed from: lambda$onAudioFocusChange$0$com-google-android-exoplayer2-AudioFocusManager$AudioFocusListener, reason: not valid java name */
        /* synthetic */ void m9xa83e850b(int i) {
            AudioFocusManager.this.handleAudioFocusChange(i);
        }

        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public void onAudioFocusChange(final int i) {
            this.eventHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.AudioFocusManager$AudioFocusListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AudioFocusManager.AudioFocusListener.this.m9xa83e850b(i);
                }
            });
        }
    }
}