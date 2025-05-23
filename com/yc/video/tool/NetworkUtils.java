package com.yc.video.tool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/* loaded from: classes.dex */
public class NetworkUtils {
    public static final int NETWORK_CLOSED = 1;
    public static final int NETWORK_ETHERNET = 2;
    public static final int NETWORK_MOBILE = 4;
    public static final int NETWORK_UNKNOWN = -1;
    public static final int NETWORK_WIFI = 3;
    public static final int NO_NETWORK = 0;
    private static State tempState;

    public enum State {
        MOBILE,
        WIFI,
        UN_CONNECTED,
        PUBLISHED
    }

    public static State getConnectState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        State state = State.UN_CONNECTED;
        if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
            if (isMobileConnected(context)) {
                state = State.MOBILE;
            } else if (isWifiConnected(context)) {
                state = State.WIFI;
            }
        }
        if (state.equals(tempState)) {
            return State.PUBLISHED;
        }
        tempState = state;
        return state;
    }

    private static boolean isMobileConnected(Context context) {
        return isConnected(context, 0);
    }

    private static boolean isWifiConnected(Context context) {
        return isConnected(context, 1);
    }

    private static boolean isConnected(Context context, int i) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        Network[] networkArr = new Network[0];
        if (connectivityManager != null) {
            networkArr = connectivityManager.getAllNetworks();
        }
        for (Network network : networkArr) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.getType() == i) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getNetworkType(Context context) {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService("connectivity");
        if (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null) {
            return 0;
        }
        if (!activeNetworkInfo.isConnected()) {
            return 1;
        }
        if (activeNetworkInfo.getType() == 9) {
            return 2;
        }
        if (activeNetworkInfo.getType() == 1) {
            return 3;
        }
        if (activeNetworkInfo.getType() != 0) {
            return -1;
        }
        switch (activeNetworkInfo.getSubtype()) {
        }
        return 0;
    }
}