package com.belon.camera;

import android.util.Log;
import com.belon.util.ByteUtils;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

/* loaded from: classes.dex */
public class VertifyLicense {
    private static final int STEP_VERTIFY_DEV_RESPONSE = 4;
    private static final int STEP_VERTIFY_RESPONSE_LICENSE = 3;
    private static final int STEP_VERTIFY_START = 1;
    private static final int STEP_VERTIFY_WAIT_LICENSE = 2;
    public static final String TAG = "Vertify";
    private String cameraIp;
    private int cameraPort;
    private LicenseCallback licenseCallback;
    private DatagramSocket vertifySocket;
    private int step = 0;
    Runnable runnable = new Runnable() { // from class: com.belon.camera.VertifyLicense.1
        @Override // java.lang.Runnable
        public void run() {
            try {
                long currentTimeMillis = System.currentTimeMillis();
                while (true) {
                    if (VertifyLicense.this.step > 0) {
                        if (System.currentTimeMillis() - currentTimeMillis > 10000) {
                            VertifyLicense.this.licenseCallback.vertify(false);
                            VertifyLicense.this.step = -1;
                            break;
                        }
                        if (VertifyLicense.this.step == 1) {
                            VertifyLicense.this.startCmd();
                            VertifyLicense.this.step = 2;
                        }
                        if (VertifyLicense.this.step == 2) {
                            byte[] receiveLicense = VertifyLicense.this.receiveLicense();
                            if (receiveLicense != null) {
                                VertifyLicense.this.step = 3;
                                VertifyLicense.this.sendLicense(receiveLicense);
                            } else {
                                VertifyLicense.this.licenseCallback.vertify(false);
                                VertifyLicense.this.step = -1;
                                break;
                            }
                        }
                        if (VertifyLicense.this.step == 3 && VertifyLicense.this.receiveLicense2()) {
                            VertifyLicense.this.licenseCallback.vertify(true);
                            VertifyLicense.this.step = -1;
                            break;
                        }
                        Thread.sleep(50L);
                    } else {
                        break;
                    }
                }
                Log.d(VertifyLicense.TAG, "runnable exit");
            } catch (Exception unused) {
            }
        }
    };

    public interface LicenseCallback {
        void vertify(boolean z);
    }

    public void startVertify(String str, int i, LicenseCallback licenseCallback) {
        this.cameraIp = str;
        this.cameraPort = i;
        try {
            DatagramSocket datagramSocket = new DatagramSocket((SocketAddress) null);
            this.vertifySocket = datagramSocket;
            datagramSocket.setReuseAddress(true);
            this.vertifySocket.bind(new InetSocketAddress(i));
            this.vertifySocket.setSoTimeout(5000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.licenseCallback = licenseCallback;
        this.step = 1;
        new Thread(this.runnable).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendLicense(byte[] bArr) {
        try {
            Log.d(TAG, "sendLicense");
            int length = bArr.length + 5;
            byte[] bArr2 = new byte[length];
            bArr2[0] = 71;
            bArr2[1] = 56;
            bArr2[2] = (byte) bArr.length;
            System.arraycopy(bArr, 0, bArr2, 3, bArr.length);
            bArr2[length - 2] = getCrc(bArr2, bArr.length + 3);
            bArr2[length - 1] = -103;
            Log.d(TAG, "send:" + ByteUtils.byte2HexStr(bArr2));
            this.vertifySocket.send(new DatagramPacket(bArr2, length, InetAddress.getByName(this.cameraIp), this.cameraPort));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean receiveLicense2() {
        byte[] bArr;
        long currentTimeMillis;
        try {
            bArr = new byte[256];
            currentTimeMillis = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (System.currentTimeMillis() - currentTimeMillis <= 10000) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(bArr, 256);
                this.vertifySocket.receive(datagramPacket);
                byte[] data = datagramPacket.getData();
                Log.d(TAG, "receiveLicense=>" + ByteUtils.byte2HexStr(data));
                if (data[0] == -57 && data[1] == 57 && data[datagramPacket.getLength() - 1] == -103) {
                    if (data[datagramPacket.getLength() - 2] == getCrc(data, (data[2] & 255) + 3)) {
                        Log.d(TAG, "Vertify license success");
                        return true;
                    }
                    Log.d(TAG, "222CRC校验错误");
                    return false;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                Thread.sleep(50L);
            }
        }
        Log.d(TAG, "receiveLicense 2 timeout");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] receiveLicense() {
        byte[] bArr;
        long currentTimeMillis;
        try {
            bArr = new byte[1024];
            currentTimeMillis = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (System.currentTimeMillis() - currentTimeMillis <= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(bArr, 1024);
                this.vertifySocket.receive(datagramPacket);
                byte[] data = datagramPacket.getData();
                if (data[0] == -73 && data[1] == 57 && data[datagramPacket.getLength() - 1] == -103) {
                    if (data[datagramPacket.getLength() - 2] == getCrc(data, (data[2] & 255) + 3)) {
                        int i = data[2] & 255;
                        byte[] bArr2 = new byte[i];
                        System.arraycopy(data, 3, bArr2, 0, i);
                        Log.d(TAG, "license:" + new String(bArr2));
                        return bArr2;
                    }
                    Log.d(TAG, "111CRC校验错误");
                    return null;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                Thread.sleep(50L);
            }
        }
        Log.d(TAG, "读取超时了");
        return null;
    }

    private byte getCrc(byte[] bArr, int i) {
        int i2 = 0;
        for (int i3 = 1; i3 < i; i3++) {
            i2 += bArr[i3];
        }
        Log.d(TAG, i2 + "==>endIndex:" + i);
        return (byte) (i2 & 255);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startCmd() {
        try {
            Log.d(TAG, "startCmd");
            byte[] bArr = {55, 56, 0, 0, 0, 0, 56, -103};
            DatagramPacket datagramPacket = new DatagramPacket(bArr, 8, InetAddress.getByName(this.cameraIp), this.cameraPort);
            Log.d(TAG, "send data:" + ByteUtils.byte2HexStr(bArr));
            this.vertifySocket.send(datagramPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}