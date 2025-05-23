package com.google.android.exoplayer2.drm;

import com.google.android.exoplayer2.util.EventDispatcher;

/* compiled from: D8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class DefaultDrmSession$$ExternalSyntheticLambda1 implements EventDispatcher.Event {
    @Override // com.google.android.exoplayer2.util.EventDispatcher.Event
    public final void sendTo(Object obj) {
        ((DefaultDrmSessionEventListener) obj).onDrmKeysRestored();
    }
}