package com.wy.ui.fragment.camera;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import java.util.List;

/* loaded from: classes.dex */
public class MobileCamera {
    private static String TAG = "MobileCamera";
    private static MobileCamera instance;
    private Camera camera;
    private volatile boolean previewing = false;

    private MobileCamera() {
    }

    public static synchronized MobileCamera getInstance() {
        MobileCamera mobileCamera;
        synchronized (MobileCamera.class) {
            if (instance == null) {
                instance = new MobileCamera();
            }
            mobileCamera = instance;
        }
        return mobileCamera;
    }

    public void startPreview(SurfaceHolder surfaceHolder) {
        try {
            if (this.camera == null) {
                getCamera();
            }
            Log.d(TAG, "camera:" + this.camera);
            Camera camera = this.camera;
            if (camera != null) {
                camera.getParameters().setPreviewSize(1600, 1200);
                this.camera.setPreviewDisplay(surfaceHolder);
                this.camera.setDisplayOrientation(90);
                this.camera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPreview(SurfaceHolder surfaceHolder, int i, int i2) {
        try {
            if (this.camera == null) {
                getCamera();
            }
            Log.d(TAG, "zwn width:" + i + ",height:" + i2);
            if (this.camera != null) {
                Camera.Size optimalPreviewSize = getOptimalPreviewSize(i, i2);
                if (optimalPreviewSize != null) {
                    Camera.Parameters parameters = this.camera.getParameters();
                    Log.d(TAG, "zwn Camera size width:" + optimalPreviewSize.width + ",height:" + optimalPreviewSize.height);
                    parameters.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
                    this.camera.setParameters(parameters);
                }
                this.camera.setPreviewDisplay(surfaceHolder);
                this.camera.setDisplayOrientation(90);
                this.camera.startPreview();
                this.previewing = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Camera.Size getOptimalPreviewSize(int i, int i2) {
        if (this.camera == null) {
            this.camera = getCamera();
        }
        if (this.previewing) {
            this.camera.stopPreview();
            this.previewing = false;
        }
        List<Camera.Size> supportedPreviewSizes = this.camera.getParameters().getSupportedPreviewSizes();
        Camera.Size size = null;
        if (supportedPreviewSizes == null) {
            return null;
        }
        for (Camera.Size size2 : supportedPreviewSizes) {
            if (i <= size2.width && i2 <= size2.height && i2 <= size2.width && i <= size2.height) {
                Log.d(TAG, "zwn===>>>找到的摄像头分辨率 size width:" + size2.width + ",height:" + size2.height);
                size = size2;
            }
        }
        if (size != null) {
            return size;
        }
        for (Camera.Size size3 : supportedPreviewSizes) {
            if (size3.width == 800 && size3.height == 600) {
                return size3;
            }
        }
        return supportedPreviewSizes.get(0);
    }

    public void stopPreview() {
        try {
            Camera camera = this.camera;
            if (camera != null) {
                camera.setPreviewDisplay(null);
                this.camera.stopPreview();
                this.previewing = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Camera getCamera() {
        Camera camera = this.camera;
        if (camera != null) {
            return camera;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int numberOfCameras = Camera.getNumberOfCameras();
        int i = 0;
        for (int i2 = 0; i2 < numberOfCameras; i2++) {
            Camera.getCameraInfo(i2, cameraInfo);
            if (cameraInfo.facing == 1) {
                i = i2;
            }
        }
        Camera open = Camera.open(i);
        this.camera = open;
        return open;
    }
}