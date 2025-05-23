package com.belon.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import androidx.core.view.MotionEventCompat;
import com.belon.camera.callback.BatteryCallback;
import com.belon.camera.callback.CameraCallback;
import com.belon.camera.callback.DeviceInfoCallback;
import com.belon.camera.callback.OTACallback;
import com.belon.camera.callback.VersionCallback;
import com.belon.util.ByteUtils;
import com.belon.util.FileUnit;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread;
import java.lang.ref.SoftReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public class BKCameraClient {
    public static final int CAMERA_AUDIO_PORT = 7070;
    public static final int CAMERA_BATTERY_PORT = 9090;
    public static final int CAMERA_DEVICEINFO_PORT = 9090;
    public static final String CAMERA_IP = "192.168.1.1";
    public static final int CAMERA_VEDIO_PORT = 8090;
    private static final String TAG = "BKCameraClient";
    private BatteryCallback batteryCallback;
    DatagramSocket batteryDatagramSocket;
    private int batteryPort;
    private Thread batteryThread;
    private int cameraAudioPort;
    private CameraCallback cameraCallback;
    private String cameraIP;
    private int cameraVedioPort;
    private DeviceInfoCallback deviceInfoCallback;
    private DatagramSocket deviceInfoDatagramSocket;
    private Thread deviceInfoThread;
    private byte[] getBatteryCmd;
    private byte[] getDeviceInfoCmd;
    private OTACallback otaCallback;
    DatagramSocket otaDatagramSocket;
    Thread otaThread;
    private byte[] sendbuffer;
    private VersionCallback versionCallback;
    public static final byte[] CMD_START = {42, 118};
    public static final byte[] CMD_STOP = {42, 119};
    public static final byte[] CMD_BATTERY = {42, 122};
    public static final byte[] CMD_DEVINFO = {43, 54};
    private static final byte[] CMD_STOP_IMG = {32, 55};
    private static final byte[] CMD_STOP_AUDIO = {48, 103};
    private static BKCameraClient instance = null;
    private boolean isSocketInit = false;
    private volatile boolean isRunning = false;
    private boolean startEnd = false;
    private volatile byte[] picData = new byte[102400];
    byte[] startVediobuffer = {42, 118};
    byte[] startAudiobuffer = {48, 102};
    byte[] audioBuffer = new byte[1472];
    byte[] vedioBuffer = new byte[1472];
    byte[] infoBuffer = new byte[100];
    boolean connected = false;
    private boolean debug = false;
    private DatagramPacket vedioPkt = null;
    private InetAddress mReceiverAddress = null;
    private DatagramSocket mVedioReceiveSocket = null;
    private DatagramSocket mAudioReceiveSocket = null;
    private Thread vedioThread = null;
    private Thread audioThread = null;
    private boolean isVertify = false;
    private boolean isAudio = false;
    private VertifyLicense vertifyLicense = null;
    private volatile boolean batterying = false;
    Runnable batteryRunnable = new Runnable() { // from class: com.belon.camera.BKCameraClient.1
        @Override // java.lang.Runnable
        public void run() {
            DatagramSocket datagramSocket = null;
            try {
                try {
                    InetAddress byName = InetAddress.getByName(BKCameraClient.this.cameraIP);
                    DatagramSocket datagramSocket2 = new DatagramSocket();
                    try {
                        datagramSocket2.send(new DatagramPacket(BKCameraClient.this.getBatteryCmd, BKCameraClient.this.getBatteryCmd.length, byName, BKCameraClient.this.batteryPort));
                        byte[] bArr = new byte[32];
                        long currentTimeMillis = System.currentTimeMillis();
                        while (BKCameraClient.this.batterying) {
                            DatagramPacket datagramPacket = new DatagramPacket(bArr, 32);
                            try {
                            } catch (IOException e) {
                                e.printStackTrace();
                                try {
                                    Thread.sleep(50L);
                                } catch (InterruptedException e2) {
                                    e2.printStackTrace();
                                }
                            }
                            if (System.currentTimeMillis() - currentTimeMillis > DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                                if (BKCameraClient.this.batteryCallback != null) {
                                    BKCameraClient.this.batteryCallback.onError(ErrorCode.TimeOut, null);
                                }
                            } else {
                                datagramSocket2.receive(datagramPacket);
                                byte[] data = datagramPacket.getData();
                                if (datagramPacket.getLength() == 8 && data[0] == 102) {
                                    boolean z = true;
                                    if (data[1] == 34 && data[7] == -103) {
                                        if (BKCameraClient.this.getCrc(data, 6) != data[6]) {
                                            if (BKCameraClient.this.batteryCallback != null) {
                                                BKCameraClient.this.batteryCallback.onError(ErrorCode.CRC, null);
                                            }
                                        } else {
                                            byte b = data[2];
                                            if ((b & 128) != 128) {
                                                z = false;
                                            }
                                            byte b2 = (byte) (b & Byte.MAX_VALUE);
                                            if (b2 <= 100 && b2 >= 0) {
                                                if (BKCameraClient.this.batteryCallback != null) {
                                                    BKCameraClient.this.batteryCallback.receiveBattery(z, b2);
                                                }
                                            }
                                            if (BKCameraClient.this.batteryCallback != null) {
                                                BKCameraClient.this.batteryCallback.onError(ErrorCode.DataFormat, null);
                                            }
                                        }
                                    }
                                }
                            }
                            BKCameraClient.this.batterying = false;
                            BKCameraClient.this.batteryThread = null;
                            datagramSocket2.close();
                        }
                        BKCameraClient.this.batterying = false;
                        BKCameraClient.this.batteryThread = null;
                        datagramSocket2.close();
                    } catch (Exception unused) {
                        datagramSocket = datagramSocket2;
                        BKCameraClient.this.batterying = false;
                        datagramSocket.close();
                    }
                } catch (Exception unused2) {
                }
            } catch (Exception e3) {
                e3.printStackTrace();
                BKCameraClient.this.batterying = false;
            }
        }
    };
    private boolean InfoChecking = false;
    Runnable deviceInfoRunnable = new Runnable() { // from class: com.belon.camera.BKCameraClient.2
        @Override // java.lang.Runnable
        public void run() {
            try {
                try {
                    BKCameraClient.this.deviceInfoDatagramSocket.send(new DatagramPacket(BKCameraClient.this.getDeviceInfoCmd, BKCameraClient.this.getDeviceInfoCmd.length, InetAddress.getByName(BKCameraClient.this.cameraIP), 9090));
                    DatagramPacket datagramPacket = new DatagramPacket(new byte[100], 100);
                    try {
                        BKCameraClient.this.deviceInfoDatagramSocket.receive(datagramPacket);
                        byte[] data = datagramPacket.getData();
                        if (datagramPacket.getLength() == 64 && data[0] == 43 && BKCameraClient.this.deviceInfoCallback != null) {
                            BKCameraClient.this.deviceInfoCallback.receiveDeviceInfo(data);
                            Log.d(BKCameraClient.TAG, " get heart data " + ((int) data[0]) + " devicetype " + ((int) data[1]));
                        }
                    } catch (IOException e) {
                        if (BKCameraClient.this.deviceInfoCallback != null) {
                            BKCameraClient.this.deviceInfoCallback.onError(ErrorCode.TimeOut, null);
                        }
                        BKCameraClient.this.InfoChecking = false;
                        e.printStackTrace();
                    }
                    BKCameraClient.this.InfoChecking = false;
                    BKCameraClient.this.deviceInfoThread = null;
                    BKCameraClient.this.deviceInfoDatagramSocket.close();
                } catch (Exception unused) {
                    Log.d(BKCameraClient.TAG, "sendSocket send deviceInfo cmd false");
                    BKCameraClient.this.deviceInfoDatagramSocket.close();
                    if (BKCameraClient.this.deviceInfoCallback != null) {
                        BKCameraClient.this.deviceInfoCallback.onError(ErrorCode.TimeOut, null);
                    }
                    BKCameraClient.this.InfoChecking = false;
                }
            } catch (Exception e2) {
                BKCameraClient.this.InfoChecking = false;
                e2.printStackTrace();
            }
        }
    };
    Runnable vedioRunnable = new Runnable() { // from class: com.belon.camera.BKCameraClient.3
        @Override // java.lang.Runnable
        public void run() {
            BKCameraClient.this.sendStart();
            while (BKCameraClient.this.isRunning) {
                try {
                    BKCameraClient.this.vedioPkt = new DatagramPacket(new byte[1472], 1472);
                    if (BKCameraClient.this.vedioPkt != null) {
                        BKCameraClient.this.mVedioReceiveSocket.receive(BKCameraClient.this.vedioPkt);
                        BKCameraClient bKCameraClient = BKCameraClient.this;
                        bKCameraClient.parseAFrame(bKCameraClient.vedioPkt.getData(), BKCameraClient.this.vedioPkt.getLength());
                    }
                } catch (IOException e) {
                    BKCameraClient.this.isRunning = false;
                    if (BKCameraClient.this.mVedioReceiveSocket != null) {
                        BKCameraClient.this.mVedioReceiveSocket.close();
                    }
                    BKCameraClient.this.cameraCallback.onError(ErrorCode.TimeOut, null);
                    e.printStackTrace();
                }
            }
            if (BKCameraClient.this.debug) {
                Log.d(BKCameraClient.TAG, "vedio thread exit");
            }
        }
    };
    int saveIndex = 0;
    private int frame_id = 0;
    private int frame_count = 0;
    private int frame_id_pre = 4095;
    private int frame_data_len = 0;
    private byte[] zeroDate = new byte[102400];
    private float ax_1 = 0.0f;
    private float az_1 = 0.0f;
    private float pre_degrees = 0.0f;
    private Lock lock = new ReentrantLock();
    GSENSOR_DEVICE_INFO sensor_data = new GSENSOR_DEVICE_INFO();
    Runnable audioRunnable = new Runnable() { // from class: com.belon.camera.BKCameraClient.5
        @Override // java.lang.Runnable
        public void run() {
            if (BKCameraClient.this.debug) {
                Log.d(BKCameraClient.TAG, "start isRunning Auido");
            }
            while (BKCameraClient.this.isRunning) {
                DatagramPacket datagramPacket = new DatagramPacket(BKCameraClient.this.audioBuffer, BKCameraClient.this.audioBuffer.length);
                try {
                    BKCameraClient.this.mAudioReceiveSocket.receive(datagramPacket);
                    if (BKCameraClient.this.cameraCallback != null) {
                        BKCameraClient.this.cameraCallback.receiveAudio(datagramPacket.getData());
                    }
                } catch (IOException unused) {
                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException unused2) {
                    }
                }
            }
            if (BKCameraClient.this.debug) {
                Log.d(BKCameraClient.TAG, "audioRunnable thread exit");
            }
        }
    };
    boolean getVersioning = false;
    private boolean isOTAing = false;
    byte[] otaCmdBuffer = null;
    Runnable otaRunnable = new Runnable() { // from class: com.belon.camera.BKCameraClient.7
        @Override // java.lang.Runnable
        public void run() {
            DatagramPacket datagramPacket;
            try {
                BKCameraClient.this.otaDatagramSocket.send(new DatagramPacket(BKCameraClient.this.otaCmdBuffer, BKCameraClient.this.otaCmdBuffer.length, InetAddress.getByName(BKCameraClient.this.cameraIP), 9090));
                byte[] bArr = new byte[256];
                long currentTimeMillis = System.currentTimeMillis();
                while (BKCameraClient.this.isOTAing) {
                    try {
                        try {
                            datagramPacket = new DatagramPacket(bArr, 256);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    } catch (Exception unused) {
                        Thread.sleep(50L);
                    }
                    if (System.currentTimeMillis() - currentTimeMillis > 10000) {
                        if (BKCameraClient.this.versionCallback != null) {
                            BKCameraClient.this.versionCallback.onError(ErrorCode.TimeOut, null);
                        }
                    } else {
                        BKCameraClient.this.otaDatagramSocket.receive(datagramPacket);
                        byte[] data = datagramPacket.getData();
                        Log.d(BKCameraClient.TAG, "ota data:" + ByteUtils.byte2HexStr(data));
                        if (data[0] == -105 && data[1] == 57) {
                            int i = ((byte) (data[3] & Byte.MAX_VALUE)) & 255;
                            Log.d(BKCameraClient.TAG, "percent<<<========>>>:" + i);
                            if (BKCameraClient.this.otaCallback != null) {
                                BKCameraClient.this.otaCallback.onPercent(i);
                            }
                        }
                    }
                    BKCameraClient.this.isOTAing = false;
                    BKCameraClient.this.otaDatagramSocket.close();
                    BKCameraClient.this.otaDatagramSocket = null;
                    BKCameraClient.this.otaThread.interrupt();
                    BKCameraClient.this.otaThread = null;
                }
                BKCameraClient.this.isOTAing = false;
                BKCameraClient.this.otaDatagramSocket.close();
                BKCameraClient.this.otaDatagramSocket = null;
                BKCameraClient.this.otaThread.interrupt();
                BKCameraClient.this.otaThread = null;
                if (BKCameraClient.this.debug) {
                    Log.d(BKCameraClient.TAG, "otaRunnable exit");
                }
            } catch (Exception unused2) {
                Thread.sleep(50L);
            }
        }
    };

    public static BKCameraClient getInstance() {
        if (instance == null) {
            BKCameraClient bKCameraClient = new BKCameraClient(CAMERA_IP, CAMERA_VEDIO_PORT, CAMERA_AUDIO_PORT);
            instance = bKCameraClient;
            bKCameraClient.setAudio(false);
            instance.setDebug(false);
            instance.setBatteryPort(9090);
        }
        return instance;
    }

    private BKCameraClient(String str, int i, int i2) {
        this.sendbuffer = null;
        Log.d(TAG, "============BKCameraClient V2.0.2 init=============");
        this.cameraVedioPort = i;
        this.cameraAudioPort = i2;
        this.cameraIP = str;
        this.sendbuffer = this.startVediobuffer;
    }

    public void setCameraCallback(CameraCallback cameraCallback) {
        this.cameraCallback = cameraCallback;
    }

    public String getCameraIP() {
        return this.cameraIP;
    }

    public void setCameraIP(String str) {
        this.cameraIP = str;
    }

    public int getCameraVedioPort() {
        return this.cameraVedioPort;
    }

    public void setCameraVedioPort(int i) {
        this.cameraVedioPort = i;
    }

    public int getCameraAudioPort() {
        return this.cameraAudioPort;
    }

    public void setCameraAudioPort(int i) {
        this.cameraAudioPort = i;
    }

    public boolean isAudio() {
        return this.isAudio;
    }

    public void setAudio(boolean z) {
        this.isAudio = z;
    }

    public int getBatteryPort() {
        return this.batteryPort;
    }

    public void setBatteryPort(int i) {
        this.batteryPort = i;
    }

    public void setDebug(boolean z) {
        this.debug = z;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte getCrc(byte[] bArr, int i) {
        int i2 = 0;
        for (int i3 = 1; i3 < i; i3++) {
            byte b = bArr[i3];
            bArr[i3] = b;
            if (i3 > 0) {
                i2 += b;
            }
        }
        return (byte) (i2 & 255);
    }

    public boolean startGetBattery(byte[] bArr, BatteryCallback batteryCallback) {
        if (this.batterying) {
            this.batterying = false;
            this.batteryThread.interrupt();
            this.batteryThread = null;
        }
        this.getBatteryCmd = bArr;
        this.batteryCallback = batteryCallback;
        this.batterying = true;
        try {
            DatagramSocket datagramSocket = new DatagramSocket((SocketAddress) null);
            this.batteryDatagramSocket = datagramSocket;
            datagramSocket.setReuseAddress(true);
            this.batteryDatagramSocket.bind(new InetSocketAddress(this.batteryPort));
            this.batteryDatagramSocket.setSoTimeout(5000);
            Thread thread = new Thread(this.batteryRunnable);
            this.batteryThread = thread;
            thread.start();
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean startGetDeviceInfo(byte[] bArr, DeviceInfoCallback deviceInfoCallback) {
        if (this.InfoChecking) {
            return true;
        }
        this.InfoChecking = true;
        this.getDeviceInfoCmd = bArr;
        this.deviceInfoCallback = deviceInfoCallback;
        try {
            DatagramSocket datagramSocket = new DatagramSocket((SocketAddress) null);
            this.deviceInfoDatagramSocket = datagramSocket;
            datagramSocket.setReuseAddress(true);
            this.deviceInfoDatagramSocket.bind(new InetSocketAddress(this.batteryPort));
            this.deviceInfoDatagramSocket.setSoTimeout(2000);
            Thread thread = new Thread(this.deviceInfoRunnable);
            this.deviceInfoThread = thread;
            thread.start();
            return true;
        } catch (SocketException e) {
            Log.e(TAG, "deviceInfoDatagramSocket false");
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean sendStart() {
        DatagramSocket datagramSocket;
        this.connected = false;
        if (this.debug) {
            Log.e(TAG, "in run");
        }
        if (this.isSocketInit) {
            this.isRunning = true;
        }
        InetAddress inetAddress = this.mReceiverAddress;
        if (inetAddress == null) {
            if (this.debug) {
                Log.e(TAG, "mReceiverAddress is null");
            }
        } else if (this.debug) {
            Log.e(TAG, inetAddress.toString());
        }
        byte[] bArr = this.sendbuffer;
        DatagramPacket datagramPacket = new DatagramPacket(bArr, bArr.length, this.mReceiverAddress, this.cameraVedioPort);
        try {
            this.mVedioReceiveSocket.setSoTimeout(2500);
            this.mVedioReceiveSocket.send(datagramPacket);
        } catch (IOException e) {
            if (this.debug) {
                Log.e(TAG, "error for catch data " + e.getMessage());
            }
            e.printStackTrace();
        }
        if (this.isAudio) {
            byte[] bArr2 = this.startAudiobuffer;
            DatagramPacket datagramPacket2 = new DatagramPacket(bArr2, bArr2.length, this.mReceiverAddress, this.cameraAudioPort);
            try {
                this.mAudioReceiveSocket.setSoTimeout(2000);
                this.mAudioReceiveSocket.send(datagramPacket2);
            } catch (IOException e2) {
                if (this.debug) {
                    Log.e(TAG, "error for catch data " + e2.getMessage());
                }
            }
        }
        if (!this.isRunning) {
            DatagramSocket datagramSocket2 = this.mVedioReceiveSocket;
            if (datagramSocket2 != null) {
                datagramSocket2.close();
            }
            if (this.isAudio && (datagramSocket = this.mAudioReceiveSocket) != null) {
                datagramSocket.close();
            }
        }
        return true;
    }

    public void disConnect(byte[] bArr) {
        if (this.debug) {
            Log.d(TAG, "disConnect isRunning:" + this.isRunning);
        }
        if (this.isRunning) {
            try {
                this.sendbuffer = bArr;
                sendStopCmd();
                this.isRunning = false;
                this.batterying = false;
                close();
                if (this.debug) {
                    Log.d(TAG, "disConnect close:" + this.isRunning);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendStopCmd() {
        new Thread(new Runnable() { // from class: com.belon.camera.BKCameraClient.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    InetAddress byName = InetAddress.getByName(BKCameraClient.this.cameraIP);
                    DatagramSocket datagramSocket = new DatagramSocket();
                    datagramSocket.send(new DatagramPacket(BKCameraClient.this.sendbuffer, BKCameraClient.this.sendbuffer.length, byName, BKCameraClient.this.cameraVedioPort));
                    if (BKCameraClient.this.isAudio) {
                        datagramSocket.send(new DatagramPacket(BKCameraClient.CMD_STOP_AUDIO, BKCameraClient.CMD_STOP_AUDIO.length, byName, BKCameraClient.this.cameraAudioPort));
                    }
                    datagramSocket.disconnect();
                    datagramSocket.close();
                    if (BKCameraClient.this.debug) {
                        Log.d(BKCameraClient.TAG, "send cmd stop finish");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void saveErrorMjpegToFile() {
        File file = new File(FileUnit.getTempPath(), "/Image-" + this.saveIndex + "_error.jpeg");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(this.picData, 0, this.frame_data_len);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "save error, " + e.getMessage());
            e.printStackTrace();
        }
        this.saveIndex++;
    }

    private void saveMjpegToFile() {
        File file = new File(FileUnit.getTempPath(), "/Image-" + this.saveIndex + ".jpeg");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(this.picData, 0, this.frame_data_len);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "save error, " + e.getMessage());
            e.printStackTrace();
        }
        this.saveIndex++;
    }

    public Bitmap byteToBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.picData, 0, this.frame_data_len);
        Bitmap bitmap = (Bitmap) new SoftReference(BitmapFactory.decodeStream(byteArrayInputStream, null, options)).get();
        try {
            byteArrayInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseAFrame(byte[] bArr, int i) {
        addPicData(bArr, i);
    }

    private class GSENSOR_DEVICE_INFO {
        byte[] att_reserve;
        byte battery_level;
        byte but_id;
        byte cam_direction;
        byte custom_id;
        byte device_type;
        byte fw_version;
        short x;
        short y;
        short z;

        private GSENSOR_DEVICE_INFO() {
        }
    }

    private void addPicData(byte[] bArr, int i) {
        this.lock.lock();
        try {
            try {
                int i2 = bArr[0] & 255;
                this.frame_id = i2;
                if (i2 == this.frame_id_pre) {
                    if (bArr[1] == 1) {
                        this.frame_count++;
                        int i3 = i - 16;
                        System.arraycopy(bArr, 4, this.picData, this.frame_data_len, i3);
                        this.frame_data_len += i3;
                        this.sensor_data.x = bArr[i - 11];
                        GSENSOR_DEVICE_INFO gsensor_device_info = this.sensor_data;
                        gsensor_device_info.x = (short) ((gsensor_device_info.x << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK);
                        GSENSOR_DEVICE_INFO gsensor_device_info2 = this.sensor_data;
                        int i4 = i - 12;
                        gsensor_device_info2.x = (short) (gsensor_device_info2.x + bArr[i4]);
                        this.sensor_data.y = bArr[i - 9];
                        GSENSOR_DEVICE_INFO gsensor_device_info3 = this.sensor_data;
                        gsensor_device_info3.y = (short) ((gsensor_device_info3.y << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK);
                        GSENSOR_DEVICE_INFO gsensor_device_info4 = this.sensor_data;
                        gsensor_device_info4.y = (short) (gsensor_device_info4.y + bArr[i - 10]);
                        this.sensor_data.z = bArr[i - 7];
                        GSENSOR_DEVICE_INFO gsensor_device_info5 = this.sensor_data;
                        gsensor_device_info5.z = (short) ((gsensor_device_info5.z << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK);
                        GSENSOR_DEVICE_INFO gsensor_device_info6 = this.sensor_data;
                        gsensor_device_info6.z = (short) (gsensor_device_info6.z + bArr[i - 8]);
                        this.sensor_data.device_type = bArr[i - 6];
                        this.sensor_data.battery_level = bArr[i - 5];
                        this.sensor_data.fw_version = bArr[i - 4];
                        this.sensor_data.cam_direction = bArr[i - 3];
                        this.sensor_data.custom_id = bArr[i - 2];
                        this.sensor_data.but_id = (byte) 0;
                        if (this.sensor_data.device_type == 5 && bArr[i4] == 1) {
                            this.sensor_data.but_id = (byte) 1;
                        }
                        if (this.frame_count == bArr[2]) {
                            Bitmap byteToBitmap = byteToBitmap();
                            if (byteToBitmap != null) {
                                this.cameraCallback.receiveVedio(byteToBitmap, (int) calculateRotate(), this.sensor_data.battery_level, this.sensor_data.device_type, this.sensor_data.but_id);
                            }
                        } else {
                            this.frame_id = 0;
                            this.frame_id_pre = 4095;
                            this.frame_data_len = 0;
                            this.frame_count = 0;
                            System.arraycopy(this.zeroDate, 0, this.picData, 0, 102400);
                        }
                        this.frame_count = 0;
                    } else {
                        int i5 = i - 4;
                        System.arraycopy(bArr, 4, this.picData, this.frame_data_len, i5);
                        this.frame_data_len += i5;
                        this.frame_count++;
                    }
                    if (!this.connected) {
                        this.connected = true;
                        this.cameraCallback.connectSuccess(true);
                    }
                } else {
                    System.arraycopy(this.zeroDate, 0, this.picData, 0, 102400);
                    this.frame_data_len = i - 4;
                    this.frame_count++;
                    System.arraycopy(bArr, 4, this.picData, 0, this.frame_data_len);
                }
                this.frame_id_pre = this.frame_id;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            this.lock.unlock();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0032, code lost:
    
        if (r1 < (-1.0d)) goto L4;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private float calculateRotate() {
        /*
            r7 = this;
            com.belon.camera.BKCameraClient$GSENSOR_DEVICE_INFO r0 = r7.sensor_data
            short r0 = r0.x
            float r0 = (float) r0
            r1 = 1073741824(0x40000000, float:2.0)
            float r0 = r0 / r1
            float r2 = r7.ax_1
            float r2 = r2 / r1
            float r0 = r0 + r2
            com.belon.camera.BKCameraClient$GSENSOR_DEVICE_INFO r2 = r7.sensor_data
            short r2 = r2.z
            float r2 = (float) r2
            float r2 = r2 / r1
            float r3 = r7.az_1
            float r3 = r3 / r1
            float r2 = r2 + r3
            r7.ax_1 = r0
            r7.az_1 = r2
            float r1 = r0 * r0
            float r3 = r2 * r2
            float r1 = r1 + r3
            double r3 = (double) r1
            double r3 = java.lang.Math.sqrt(r3)
            double r1 = (double) r2
            double r1 = r1 / r3
            r3 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 <= 0) goto L2e
        L2c:
            r1 = r3
            goto L35
        L2e:
            r3 = -4616189618054758400(0xbff0000000000000, double:-1.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 >= 0) goto L35
            goto L2c
        L35:
            double r1 = java.lang.Math.acos(r1)
            float r1 = (float) r1
            double r1 = (double) r1
            r3 = 0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L47
            r3 = 4618760256179416344(0x401921fb54442d18, double:6.283185307179586)
            double r1 = r3 - r1
        L47:
            com.belon.camera.BKCameraClient$GSENSOR_DEVICE_INFO r0 = r7.sensor_data
            byte r0 = r0.cam_direction
            com.belon.camera.BKCameraClient$GSENSOR_DEVICE_INFO r0 = r7.sensor_data
            byte r0 = r0.cam_direction
            r3 = 1
            if (r0 != r3) goto L55
            r0 = 270(0x10e, float:3.78E-43)
            goto L57
        L55:
            r0 = 360(0x168, float:5.04E-43)
        L57:
            com.belon.camera.BKCameraClient$GSENSOR_DEVICE_INFO r3 = r7.sensor_data
            byte r3 = r3.cam_direction
            r4 = 2
            if (r3 != r4) goto L60
            r0 = 180(0xb4, float:2.52E-43)
        L60:
            com.belon.camera.BKCameraClient$GSENSOR_DEVICE_INFO r3 = r7.sensor_data
            byte r3 = r3.cam_direction
            r4 = 3
            if (r3 != r4) goto L69
            r0 = 90
        L69:
            double r3 = (double) r0
            r5 = 4640537203540230144(0x4066800000000000, double:180.0)
            double r1 = r1 * r5
            r5 = 4614256656552045848(0x400921fb54442d18, double:3.141592653589793)
            double r1 = r1 / r5
            double r3 = r3 - r1
            float r0 = (float) r3
            double r0 = (double) r0
            r2 = 4602678819172646912(0x3fe0000000000000, double:0.5)
            double r0 = r0 + r2
            int r0 = (int) r0
            float r0 = (float) r0
            float r1 = r7.pre_degrees
            float r1 = r1 - r0
            float r1 = java.lang.Math.abs(r1)
            r2 = 1084227584(0x40a00000, float:5.0)
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L99
            float r1 = r7.pre_degrees
            float r1 = r1 - r0
            float r1 = java.lang.Math.abs(r1)
            r2 = 1135706112(0x43b18000, float:355.0)
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L9b
        L99:
            float r0 = r7.pre_degrees
        L9b:
            r7.pre_degrees = r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.belon.camera.BKCameraClient.calculateRotate():float");
    }

    private boolean initSocket() {
        try {
            this.mReceiverAddress = InetAddress.getByName(this.cameraIP);
            DatagramSocket datagramSocket = new DatagramSocket(this.cameraAudioPort);
            this.mVedioReceiveSocket = datagramSocket;
            datagramSocket.setReuseAddress(true);
            this.mVedioReceiveSocket.setSoTimeout(2000);
            if (this.isAudio) {
                DatagramSocket datagramSocket2 = new DatagramSocket((SocketAddress) null);
                this.mAudioReceiveSocket = datagramSocket2;
                datagramSocket2.setReuseAddress(true);
                this.mAudioReceiveSocket.bind(new InetSocketAddress(this.cameraAudioPort));
                this.mAudioReceiveSocket.setSoTimeout(2000);
            }
            this.isSocketInit = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            CameraCallback cameraCallback = this.cameraCallback;
            if (cameraCallback == null) {
                return false;
            }
            cameraCallback.onError(ErrorCode.InitSocket, e);
            return false;
        }
    }

    public void startConnect(byte[] bArr, CameraCallback cameraCallback) {
        try {
            Log.d(TAG, "start :" + this.audioThread + ",isVertify:" + this.isVertify);
            if (this.audioThread != null) {
                Log.e(TAG, "audioThread thread  is already running!");
            } else {
                this.cameraCallback = cameraCallback;
                startCamera(bArr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean startCamera(byte[] bArr) {
        this.saveIndex = 0;
        if (this.isRunning) {
            return false;
        }
        this.sendbuffer = bArr;
        if (initSocket()) {
            this.isRunning = true;
            Thread thread = new Thread(this.vedioRunnable);
            this.vedioThread = thread;
            thread.start();
            if (this.isAudio) {
                Thread thread2 = new Thread(this.audioRunnable);
                this.audioThread = thread2;
                thread2.start();
            }
        }
        return true;
    }

    private void close() {
        try {
            Thread thread = this.audioThread;
            if (thread != null) {
                thread.interrupt();
                while (this.audioThread.getState() != Thread.State.TERMINATED) {
                }
                this.audioThread = null;
                Log.d(TAG, "audioThread stop");
            }
            Thread thread2 = this.vedioThread;
            if (thread2 != null) {
                thread2.interrupt();
                while (this.vedioThread.getState() != Thread.State.TERMINATED) {
                }
                this.vedioThread = null;
                Log.d(TAG, "vedioThread stop");
            }
            DatagramSocket datagramSocket = this.mVedioReceiveSocket;
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean startGetVersion(final VersionCallback versionCallback) {
        this.versionCallback = versionCallback;
        if (this.getVersioning) {
            Log.e(TAG, "正在获取版本号");
            return false;
        }
        final byte[] bArr = new byte[8];
        bArr[0] = 39;
        bArr[1] = 56;
        bArr[6] = getCrc(bArr, 2);
        bArr[7] = -103;
        try {
            DatagramSocket datagramSocket = new DatagramSocket((SocketAddress) null);
            datagramSocket.setReuseAddress(true);
            datagramSocket.bind(new InetSocketAddress(9090));
            datagramSocket.setSoTimeout(5000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() { // from class: com.belon.camera.BKCameraClient.6
            /* JADX WARN: Can't wrap try/catch for region: R(7:10|(5:45|46|(1:48)|49|(2:51|53)(1:61))(6:12|13|(1:15)|16|(3:21|22|(3:28|29|(3:31|32|33)(1:34))(3:24|25|26))|27)|37|38|39|41|27) */
            /* JADX WARN: Code restructure failed: missing block: B:42:0x014b, code lost:
            
                r6 = move-exception;
             */
            /* JADX WARN: Code restructure failed: missing block: B:43:0x014c, code lost:
            
                r6.printStackTrace();
             */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void run() {
                /*
                    Method dump skipped, instructions count: 342
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: com.belon.camera.BKCameraClient.AnonymousClass6.run():void");
            }
        }).start();
        return true;
    }

    public boolean startOTA(String str, int i, OTACallback oTACallback) {
        if (this.isOTAing) {
            Log.d(TAG, "正在OTA");
            return false;
        }
        this.isOTAing = true;
        this.otaCallback = oTACallback;
        String str2 = "{\"url\":\"" + str + "\",\"port\":" + i + "}";
        if (this.debug) {
            Log.d(TAG, "json:" + str2);
        }
        byte[] bytes = str2.getBytes();
        int length = bytes.length;
        byte[] bArr = new byte[length + 5];
        this.otaCmdBuffer = bArr;
        bArr[0] = 23;
        bArr[1] = 56;
        bArr[2] = (byte) length;
        for (int i2 = 0; i2 < length; i2++) {
            this.otaCmdBuffer[i2 + 3] = bytes[i2];
        }
        byte[] bArr2 = this.otaCmdBuffer;
        int i3 = length + 3;
        bArr2[i3] = getCrc(bArr2, i3);
        this.otaCmdBuffer[i3 + 1] = -103;
        if (this.debug) {
            Log.d(TAG, "send cmd:" + ByteUtils.byte2HexStr(this.otaCmdBuffer));
        }
        try {
            if (this.otaDatagramSocket == null) {
                DatagramSocket datagramSocket = new DatagramSocket((SocketAddress) null);
                this.otaDatagramSocket = datagramSocket;
                datagramSocket.setReuseAddress(true);
                this.otaDatagramSocket.bind(new InetSocketAddress(9090));
                this.otaDatagramSocket.setSoTimeout(1500);
            }
            Thread thread = new Thread(this.otaRunnable);
            this.otaThread = thread;
            thread.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}