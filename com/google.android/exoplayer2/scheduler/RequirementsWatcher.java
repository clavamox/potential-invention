package com.google.android.exoplayer2.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Handler;
import com.google.android.exoplayer2.scheduler.RequirementsWatcher;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

/* loaded from: classes.dex */
public final class RequirementsWatcher {
    private final Context context;
    private final Handler handler = new Handler(Util.getLooper());
    private final Listener listener;
    private NetworkCallback networkCallback;
    private int notMetRequirements;
    private DeviceStatusChangeReceiver receiver;
    private final Requirements requirements;

    public interface Listener {
        void onRequirementsStateChanged(RequirementsWatcher requirementsWatcher, int i);
    }

    public RequirementsWatcher(Context context, Listener listener, Requirements requirements) {
        this.context = context.getApplicationContext();
        this.listener = listener;
        this.requirements = requirements;
    }

    public int start() {
        this.notMetRequirements = this.requirements.getNotMetRequirements(this.context);
        IntentFilter intentFilter = new IntentFilter();
        if (this.requirements.isNetworkRequired()) {
            if (Util.SDK_INT >= 24) {
                registerNetworkCallbackV24();
            } else {
                intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            }
        }
        if (this.requirements.isChargingRequired()) {
            intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
            intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        }
        if (this.requirements.isIdleRequired()) {
            if (Util.SDK_INT >= 23) {
                intentFilter.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
            } else {
                intentFilter.addAction("android.intent.action.SCREEN_ON");
                intentFilter.addAction("android.intent.action.SCREEN_OFF");
            }
        }
        DeviceStatusChangeReceiver deviceStatusChangeReceiver = new DeviceStatusChangeReceiver();
        this.receiver = deviceStatusChangeReceiver;
        this.context.registerReceiver(deviceStatusChangeReceiver, intentFilter, null, this.handler);
        return this.notMetRequirements;
    }

    public void stop() {
        this.context.unregisterReceiver((BroadcastReceiver) Assertions.checkNotNull(this.receiver));
        this.receiver = null;
        if (Util.SDK_INT < 24 || this.networkCallback == null) {
            return;
        }
        unregisterNetworkCallbackV24();
    }

    public Requirements getRequirements() {
        return this.requirements;
    }

    private void registerNetworkCallbackV24() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Assertions.checkNotNull((ConnectivityManager) this.context.getSystemService("connectivity"));
        NetworkCallback networkCallback = new NetworkCallback();
        this.networkCallback = networkCallback;
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    private void unregisterNetworkCallbackV24() {
        ((ConnectivityManager) this.context.getSystemService("connectivity")).unregisterNetworkCallback((ConnectivityManager.NetworkCallback) Assertions.checkNotNull(this.networkCallback));
        this.networkCallback = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkRequirements() {
        int notMetRequirements = this.requirements.getNotMetRequirements(this.context);
        if (this.notMetRequirements != notMetRequirements) {
            this.notMetRequirements = notMetRequirements;
            this.listener.onRequirementsStateChanged(this, notMetRequirements);
        }
    }

    private class DeviceStatusChangeReceiver extends BroadcastReceiver {
        private DeviceStatusChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (isInitialStickyBroadcast()) {
                return;
            }
            RequirementsWatcher.this.checkRequirements();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class NetworkCallback extends ConnectivityManager.NetworkCallback {
        boolean networkValidated;
        boolean receivedCapabilitiesChange;

        private NetworkCallback() {
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onAvailable(Network network) {
            onNetworkCallback();
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            onNetworkCallback();
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            boolean hasCapability = networkCapabilities.hasCapability(16);
            if (this.receivedCapabilitiesChange && this.networkValidated == hasCapability) {
                return;
            }
            this.receivedCapabilitiesChange = true;
            this.networkValidated = hasCapability;
            onNetworkCallback();
        }

        private void onNetworkCallback() {
            RequirementsWatcher.this.handler.post(new Runnable() { // from class: com.google.android.exoplayer2.scheduler.RequirementsWatcher$NetworkCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RequirementsWatcher.NetworkCallback.this.m32x7fde75d6();
                }
            });
        }

        /* renamed from: lambda$onNetworkCallback$0$com-google-android-exoplayer2-scheduler-RequirementsWatcher$NetworkCallback, reason: not valid java name */
        /* synthetic */ void m32x7fde75d6() {
            if (RequirementsWatcher.this.networkCallback != null) {
                RequirementsWatcher.this.checkRequirements();
            }
        }
    }
}