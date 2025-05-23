package com.belon.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class NetworkUtils {
    public static String getWifiIpAddress(Context context) {
        return getIpAddress(((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getIpAddress());
    }

    public static String getIpAddress(int i) {
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            i = Integer.reverseBytes(i);
        }
        try {
            return InetAddress.getByAddress(BigInteger.valueOf(i).toByteArray()).getHostAddress();
        } catch (UnknownHostException unused) {
            return null;
        }
    }
}