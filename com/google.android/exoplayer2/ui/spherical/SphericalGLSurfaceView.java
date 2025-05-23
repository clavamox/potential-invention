package com.google.android.exoplayer2.ui.spherical;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.WindowManager;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.spherical.OrientationListener;
import com.google.android.exoplayer2.ui.spherical.TouchTracker;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* loaded from: classes.dex */
public final class SphericalGLSurfaceView extends GLSurfaceView {
    private static final int FIELD_OF_VIEW_DEGREES = 90;
    private static final float PX_PER_DEGREES = 25.0f;
    static final float UPRIGHT_ROLL = 3.1415927f;
    private static final float Z_FAR = 100.0f;
    private static final float Z_NEAR = 0.1f;
    private final Handler mainHandler;
    private final OrientationListener orientationListener;
    private final Sensor orientationSensor;
    private final SceneRenderer scene;
    private final SensorManager sensorManager;
    private Surface surface;
    private SurfaceTexture surfaceTexture;
    private final TouchTracker touchTracker;
    private Player.VideoComponent videoComponent;

    public SphericalGLSurfaceView(Context context) {
        this(context, null);
    }

    public SphericalGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mainHandler = new Handler(Looper.getMainLooper());
        SensorManager sensorManager = (SensorManager) Assertions.checkNotNull(context.getSystemService("sensor"));
        this.sensorManager = sensorManager;
        Sensor defaultSensor = Util.SDK_INT >= 18 ? sensorManager.getDefaultSensor(15) : null;
        this.orientationSensor = defaultSensor == null ? sensorManager.getDefaultSensor(11) : defaultSensor;
        SceneRenderer sceneRenderer = new SceneRenderer();
        this.scene = sceneRenderer;
        Renderer renderer = new Renderer(sceneRenderer);
        TouchTracker touchTracker = new TouchTracker(context, renderer, PX_PER_DEGREES);
        this.touchTracker = touchTracker;
        this.orientationListener = new OrientationListener(((WindowManager) Assertions.checkNotNull((WindowManager) context.getSystemService("window"))).getDefaultDisplay(), touchTracker, renderer);
        setEGLContextClientVersion(2);
        setRenderer(renderer);
        setOnTouchListener(touchTracker);
    }

    public void setDefaultStereoMode(int i) {
        this.scene.setDefaultStereoMode(i);
    }

    public void setVideoComponent(Player.VideoComponent videoComponent) {
        Player.VideoComponent videoComponent2 = this.videoComponent;
        if (videoComponent == videoComponent2) {
            return;
        }
        if (videoComponent2 != null) {
            Surface surface = this.surface;
            if (surface != null) {
                videoComponent2.clearVideoSurface(surface);
            }
            this.videoComponent.clearVideoFrameMetadataListener(this.scene);
            this.videoComponent.clearCameraMotionListener(this.scene);
        }
        this.videoComponent = videoComponent;
        if (videoComponent != null) {
            videoComponent.setVideoFrameMetadataListener(this.scene);
            this.videoComponent.setCameraMotionListener(this.scene);
            this.videoComponent.setVideoSurface(this.surface);
        }
    }

    public void setSingleTapListener(SingleTapListener singleTapListener) {
        this.touchTracker.setSingleTapListener(singleTapListener);
    }

    @Override // android.opengl.GLSurfaceView
    public void onResume() {
        super.onResume();
        Sensor sensor = this.orientationSensor;
        if (sensor != null) {
            this.sensorManager.registerListener(this.orientationListener, sensor, 0);
        }
    }

    @Override // android.opengl.GLSurfaceView
    public void onPause() {
        if (this.orientationSensor != null) {
            this.sensorManager.unregisterListener(this.orientationListener);
        }
        super.onPause();
    }

    @Override // android.opengl.GLSurfaceView, android.view.SurfaceView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mainHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.ui.spherical.SphericalGLSurfaceView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SphericalGLSurfaceView.this.m57xd13a4557();
            }
        });
    }

    /* renamed from: lambda$onDetachedFromWindow$0$com-google-android-exoplayer2-ui-spherical-SphericalGLSurfaceView, reason: not valid java name */
    /* synthetic */ void m57xd13a4557() {
        Surface surface = this.surface;
        if (surface != null) {
            Player.VideoComponent videoComponent = this.videoComponent;
            if (videoComponent != null) {
                videoComponent.clearVideoSurface(surface);
            }
            releaseSurface(this.surfaceTexture, this.surface);
            this.surfaceTexture = null;
            this.surface = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSurfaceTextureAvailable(final SurfaceTexture surfaceTexture) {
        this.mainHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.ui.spherical.SphericalGLSurfaceView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SphericalGLSurfaceView.this.m58xb36faa8f(surfaceTexture);
            }
        });
    }

    /* renamed from: lambda$onSurfaceTextureAvailable$1$com-google-android-exoplayer2-ui-spherical-SphericalGLSurfaceView, reason: not valid java name */
    /* synthetic */ void m58xb36faa8f(SurfaceTexture surfaceTexture) {
        SurfaceTexture surfaceTexture2 = this.surfaceTexture;
        Surface surface = this.surface;
        this.surfaceTexture = surfaceTexture;
        Surface surface2 = new Surface(surfaceTexture);
        this.surface = surface2;
        Player.VideoComponent videoComponent = this.videoComponent;
        if (videoComponent != null) {
            videoComponent.setVideoSurface(surface2);
        }
        releaseSurface(surfaceTexture2, surface);
    }

    private static void releaseSurface(SurfaceTexture surfaceTexture, Surface surface) {
        if (surfaceTexture != null) {
            surfaceTexture.release();
        }
        if (surface != null) {
            surface.release();
        }
    }

    class Renderer implements GLSurfaceView.Renderer, TouchTracker.Listener, OrientationListener.Listener {
        private final float[] deviceOrientationMatrix;
        private float deviceRoll;
        private final SceneRenderer scene;
        private float touchPitch;
        private final float[] touchPitchMatrix;
        private final float[] touchYawMatrix;
        private final float[] projectionMatrix = new float[16];
        private final float[] viewProjectionMatrix = new float[16];
        private final float[] viewMatrix = new float[16];
        private final float[] tempMatrix = new float[16];

        public Renderer(SceneRenderer sceneRenderer) {
            float[] fArr = new float[16];
            this.deviceOrientationMatrix = fArr;
            float[] fArr2 = new float[16];
            this.touchPitchMatrix = fArr2;
            float[] fArr3 = new float[16];
            this.touchYawMatrix = fArr3;
            this.scene = sceneRenderer;
            Matrix.setIdentityM(fArr, 0);
            Matrix.setIdentityM(fArr2, 0);
            Matrix.setIdentityM(fArr3, 0);
            this.deviceRoll = SphericalGLSurfaceView.UPRIGHT_ROLL;
        }

        @Override // android.opengl.GLSurfaceView.Renderer
        public synchronized void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
            SphericalGLSurfaceView.this.onSurfaceTextureAvailable(this.scene.init());
        }

        @Override // android.opengl.GLSurfaceView.Renderer
        public void onSurfaceChanged(GL10 gl10, int i, int i2) {
            GLES20.glViewport(0, 0, i, i2);
            float f = i / i2;
            Matrix.perspectiveM(this.projectionMatrix, 0, calculateFieldOfViewInYDirection(f), f, 0.1f, SphericalGLSurfaceView.Z_FAR);
        }

        @Override // android.opengl.GLSurfaceView.Renderer
        public void onDrawFrame(GL10 gl10) {
            synchronized (this) {
                Matrix.multiplyMM(this.tempMatrix, 0, this.deviceOrientationMatrix, 0, this.touchYawMatrix, 0);
                Matrix.multiplyMM(this.viewMatrix, 0, this.touchPitchMatrix, 0, this.tempMatrix, 0);
            }
            Matrix.multiplyMM(this.viewProjectionMatrix, 0, this.projectionMatrix, 0, this.viewMatrix, 0);
            this.scene.drawFrame(this.viewProjectionMatrix, false);
        }

        @Override // com.google.android.exoplayer2.ui.spherical.OrientationListener.Listener
        public synchronized void onOrientationChange(float[] fArr, float f) {
            float[] fArr2 = this.deviceOrientationMatrix;
            System.arraycopy(fArr, 0, fArr2, 0, fArr2.length);
            this.deviceRoll = -f;
            updatePitchMatrix();
        }

        private void updatePitchMatrix() {
            Matrix.setRotateM(this.touchPitchMatrix, 0, -this.touchPitch, (float) Math.cos(this.deviceRoll), (float) Math.sin(this.deviceRoll), 0.0f);
        }

        @Override // com.google.android.exoplayer2.ui.spherical.TouchTracker.Listener
        public synchronized void onScrollChange(PointF pointF) {
            this.touchPitch = pointF.y;
            updatePitchMatrix();
            Matrix.setRotateM(this.touchYawMatrix, 0, -pointF.x, 0.0f, 1.0f, 0.0f);
        }

        private float calculateFieldOfViewInYDirection(float f) {
            if (f > 1.0f) {
                return (float) (Math.toDegrees(Math.atan(Math.tan(Math.toRadians(45.0d)) / f)) * 2.0d);
            }
            return 90.0f;
        }
    }
}