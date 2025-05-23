package com.wy.ui.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.Unbinder;
import com.belon.camera.BKCameraClient;
import com.belon.camera.callback.CameraCallback;
import com.belon.view.CameraImageView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.wy.ui.fragment.camera.MobileCamera;
import com.wy.ui.view.ResizeAbleSurfaceView;
import com.wy.util.FileUtil;
import com.wy.util.PixelUtil;
import com.wy.util.VedioRecord;
import com.xzf.camera.R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/* loaded from: classes.dex */
public class CameraBaseFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "CameraBaseFragment";
    Bitmap bitmap_to_save;
    Bitmap bm_sx;
    Bitmap bm_yf;
    Bitmap bm_zy;

    @BindView(R.id.iv_cmp)
    ImageView ivCompare;

    @BindView(R.id.iv_dev_camera)
    CameraImageView iv_dev_camera;

    @BindView(R.id.iv_mobile_camera)
    FrameLayout iv_mobile_camera;

    @BindView(R.id.ll_dev_camera)
    ConstraintLayout ll_dev_camera;
    private Paint mPaint;
    SurfaceHolder mSurfaceHolder;
    private MediaPlayer mediaPlayer;

    @BindView(R.id.rb_screen_change)
    RadioButton rb_screen_change;
    private int recordSeconds;

    @BindView(R.id.surface_view)
    ResizeAbleSurfaceView surface_view;
    TextView tvRecordVedio;
    protected Unbinder unbinder;
    private String vedioPath;
    private VedioRecord vedioRecord;
    int old_ang = 0;
    protected boolean isPreview = false;
    protected boolean isCapture = false;
    protected boolean isVedio = false;
    protected boolean earRight = false;
    protected boolean gyroscopeStat = true;
    protected boolean mirrorState = false;
    protected boolean is_duibi = false;
    protected Bitmap cmpBitmap = null;
    private boolean is_fenping = false;
    int this_device = 0;
    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() { // from class: com.wy.ui.fragment.CameraBaseFragment.1
        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            CameraBaseFragment.this.mSurfaceHolder = surfaceHolder;
            Log.d(CameraBaseFragment.TAG, getClass().getSimpleName() + " surfaceCreated ");
            CameraBaseFragment.this.startPreviewMobileCamera();
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.d(CameraBaseFragment.TAG, getClass().getSimpleName() + "surfaceDestroyed....");
            CameraBaseFragment.this.isPreview = false;
            CameraBaseFragment.this.mSurfaceHolder = null;
        }
    };
    CameraCallback cameraCallback = new CameraCallback() { // from class: com.wy.ui.fragment.CameraBaseFragment.2
        @Override // com.belon.camera.callback.CameraCallback
        public void receiveVedio(final Bitmap bitmap, int i, int i2, final int i3, final int i4) {
            if (CameraBaseFragment.this.this_device == 4) {
                int i5 = i + 90;
                CameraBaseFragment.this.old_ang += 90;
                Log.d(CameraBaseFragment.TAG, "new222......" + i5 + "<" + i + ">");
                if (CameraBaseFragment.this.old_ang == 0) {
                    CameraBaseFragment.this.old_ang = i5;
                }
                if (Math.abs(i5 - CameraBaseFragment.this.old_ang) > 30) {
                    if (Math.abs(i5 - CameraBaseFragment.this.old_ang) > 300) {
                        CameraBaseFragment.this.old_ang = i5;
                    } else if (Math.abs(i5 - CameraBaseFragment.this.old_ang) > 100) {
                        CameraBaseFragment cameraBaseFragment = CameraBaseFragment.this;
                        cameraBaseFragment.old_ang = ((i5 - cameraBaseFragment.old_ang) / 25) + CameraBaseFragment.this.old_ang;
                    } else {
                        CameraBaseFragment cameraBaseFragment2 = CameraBaseFragment.this;
                        cameraBaseFragment2.old_ang = ((i5 - cameraBaseFragment2.old_ang) / 2) + CameraBaseFragment.this.old_ang;
                    }
                } else {
                    CameraBaseFragment.this.old_ang = i5;
                }
                CameraBaseFragment cameraBaseFragment3 = CameraBaseFragment.this;
                cameraBaseFragment3.old_ang -= 90;
                Log.d(CameraBaseFragment.TAG, "new111....." + CameraBaseFragment.this.old_ang);
            } else {
                CameraBaseFragment.this.old_ang = i;
            }
            if (bitmap == null || CameraBaseFragment.this.getActivity() == null) {
                return;
            }
            CameraBaseFragment.this.bitmap_to_save = bitmap;
            CameraBaseFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.wy.ui.fragment.CameraBaseFragment.2.1
                public String toString() {
                    return "$classname{}";
                }

                @Override // java.lang.Runnable
                public void run() {
                    if (CameraBaseFragment.this.iv_dev_camera != null) {
                        if (CameraBaseFragment.this.this_device == 2) {
                            if (CameraBaseFragment.this.gyroscopeStat) {
                                CameraBaseFragment.this.iv_dev_camera.setRotate(true);
                            } else {
                                CameraBaseFragment.this.iv_dev_camera.setRotate(false);
                            }
                            if (!CameraBaseFragment.this.earRight) {
                                CameraBaseFragment.this.iv_dev_camera.setRotateAngle(CameraBaseFragment.this.old_ang);
                                CameraBaseFragment.this.iv_dev_camera.drawBitmap(bitmap);
                                return;
                            } else {
                                CameraBaseFragment.this.bm_zy = CameraBaseFragment.this.toHorizontalMirror(bitmap);
                                CameraBaseFragment.this.iv_dev_camera.setRotateAngle(360 - CameraBaseFragment.this.old_ang);
                                CameraBaseFragment.this.iv_dev_camera.drawBitmap(CameraBaseFragment.this.bm_zy);
                                return;
                            }
                        }
                        if (CameraBaseFragment.this.this_device == 3) {
                            if (i4 == 1 && !CameraBaseFragment.this.isVedio) {
                                CameraBaseFragment.this.isCapture = true;
                                CameraBaseFragment.this.shutter();
                            }
                            if (CameraBaseFragment.this.mirrorState) {
                                CameraBaseFragment.this.iv_dev_camera.drawBitmap(CameraBaseFragment.this.toVerticalMirror(bitmap));
                                return;
                            } else {
                                CameraBaseFragment.this.iv_dev_camera.drawBitmap(bitmap);
                                return;
                            }
                        }
                        if (CameraBaseFragment.this.this_device == 1) {
                            CameraBaseFragment.this.iv_dev_camera.drawBitmap(CameraBaseFragment.this.toHorizontalMirror(CameraBaseFragment.this.xuanzhuan_90(bitmap)));
                            CameraBaseFragment.this.bitmap_to_save = CameraBaseFragment.this.toHorizontalMirror(CameraBaseFragment.this.xuanzhuan_90(bitmap));
                            return;
                        }
                        if (CameraBaseFragment.this.this_device == 4) {
                            if (i3 == -56) {
                                if (CameraBaseFragment.this.gyroscopeStat) {
                                    if (CameraBaseFragment.this.mirrorState) {
                                        CameraBaseFragment.this.iv_dev_camera.setRotateAngle(CameraBaseFragment.this.old_ang + 180);
                                        CameraBaseFragment.this.iv_dev_camera.drawBitmap(bitmap);
                                        return;
                                    } else {
                                        CameraBaseFragment.this.iv_dev_camera.setRotateAngle((-CameraBaseFragment.this.old_ang) + 180);
                                        CameraBaseFragment.this.iv_dev_camera.drawBitmap(CameraBaseFragment.this.toVerticalMirror(bitmap));
                                        return;
                                    }
                                }
                                CameraBaseFragment.this.iv_dev_camera.setRotateAngle(0);
                                CameraBaseFragment.this.iv_dev_camera.setRotate(true);
                                if (CameraBaseFragment.this.mirrorState) {
                                    CameraBaseFragment.this.iv_dev_camera.drawBitmap(CameraBaseFragment.this.toVerticalMirror(CameraBaseFragment.this.xuanzhuan_degree(bitmap, 270.0f)));
                                    return;
                                } else {
                                    CameraBaseFragment.this.iv_dev_camera.drawBitmap(CameraBaseFragment.this.xuanzhuan_degree(bitmap, 270.0f));
                                    return;
                                }
                            }
                            if (CameraBaseFragment.this.mirrorState) {
                                CameraBaseFragment.this.iv_dev_camera.drawBitmap(CameraBaseFragment.this.toVerticalMirror(CameraBaseFragment.this.xuanzhuan_90(bitmap)));
                            } else {
                                CameraBaseFragment.this.iv_dev_camera.drawBitmap(CameraBaseFragment.this.xuanzhuan_90(bitmap));
                            }
                        }
                    }
                }
            });
            if (CameraBaseFragment.this.isVedio) {
                if (CameraBaseFragment.this.this_device == 3) {
                    CameraBaseFragment.this.vedioRecord.putBitmap(CameraBaseFragment.this.bitmap_to_save);
                } else {
                    CameraBaseFragment.this.vedioRecord.putBitmap(bitmap);
                }
            }
            if (CameraBaseFragment.this.isCapture) {
                if (CameraBaseFragment.this.this_device == 1) {
                    CameraBaseFragment cameraBaseFragment4 = CameraBaseFragment.this;
                    cameraBaseFragment4.bitmap_to_save = cameraBaseFragment4.toHorizontalMirror(cameraBaseFragment4.xuanzhuan_90(bitmap));
                } else if (CameraBaseFragment.this.this_device == 3 && CameraBaseFragment.this.mirrorState) {
                    CameraBaseFragment cameraBaseFragment5 = CameraBaseFragment.this;
                    cameraBaseFragment5.bitmap_to_save = cameraBaseFragment5.toVerticalMirror(cameraBaseFragment5.bitmap_to_save);
                } else if (CameraBaseFragment.this.this_device == 4) {
                    if (i3 == -56) {
                        Log.d(CameraBaseFragment.TAG, "zwn gyr " + CameraBaseFragment.this.gyroscopeStat + "<mior>" + CameraBaseFragment.this.mirrorState + "<>" + CameraBaseFragment.this.old_ang);
                        if (!CameraBaseFragment.this.gyroscopeStat) {
                            if (!CameraBaseFragment.this.mirrorState) {
                                CameraBaseFragment cameraBaseFragment6 = CameraBaseFragment.this;
                                cameraBaseFragment6.bitmap_to_save = cameraBaseFragment6.xuanzhuan_degree(bitmap, 270.0f);
                            } else {
                                CameraBaseFragment cameraBaseFragment7 = CameraBaseFragment.this;
                                cameraBaseFragment7.bitmap_to_save = cameraBaseFragment7.xuanzhuan_90(cameraBaseFragment7.toVerticalMirror(bitmap));
                            }
                        } else if (CameraBaseFragment.this.mirrorState) {
                            CameraBaseFragment cameraBaseFragment8 = CameraBaseFragment.this;
                            cameraBaseFragment8.bitmap_to_save = cameraBaseFragment8.xuanzhuan_degree(bitmap, 270.0f);
                        } else {
                            CameraBaseFragment cameraBaseFragment9 = CameraBaseFragment.this;
                            cameraBaseFragment9.bitmap_to_save = cameraBaseFragment9.xuanzhuan_90(cameraBaseFragment9.toVerticalMirror(bitmap));
                        }
                    } else if (CameraBaseFragment.this.mirrorState) {
                        CameraBaseFragment cameraBaseFragment10 = CameraBaseFragment.this;
                        cameraBaseFragment10.bitmap_to_save = cameraBaseFragment10.toVerticalMirror(cameraBaseFragment10.xuanzhuan_90(bitmap));
                    } else {
                        CameraBaseFragment cameraBaseFragment11 = CameraBaseFragment.this;
                        cameraBaseFragment11.bitmap_to_save = cameraBaseFragment11.xuanzhuan_90(bitmap);
                    }
                }
                CameraBaseFragment cameraBaseFragment12 = CameraBaseFragment.this;
                cameraBaseFragment12.cmpBitmap = cameraBaseFragment12.bitmap_to_save;
                CameraBaseFragment.this.isCapture = false;
                if (Build.VERSION.SDK_INT < 30) {
                    CameraBaseFragment cameraBaseFragment13 = CameraBaseFragment.this;
                    cameraBaseFragment13.saveBitmapToFile(FileUtil.setImageSaveFile(cameraBaseFragment13.getActivity().getApplicationContext()), System.currentTimeMillis() + ".jpg", CameraBaseFragment.this.cmpBitmap);
                } else {
                    CameraBaseFragment cameraBaseFragment14 = CameraBaseFragment.this;
                    cameraBaseFragment14.saveBitmapToFile(FileUtil.getPictureFilePath(cameraBaseFragment14.getActivity().getApplicationContext()), System.currentTimeMillis() + ".jpg", CameraBaseFragment.this.cmpBitmap);
                }
                CameraBaseFragment.this.getActivity().runOnUiThread(new Runnable() { // from class: com.wy.ui.fragment.CameraBaseFragment.2.2
                    @Override // java.lang.Runnable
                    public void run() {
                        CameraBaseFragment.this.ivCompare.setImageBitmap(CameraBaseFragment.this.cmpBitmap);
                        Toast.makeText(CameraBaseFragment.this.getContext(), CameraBaseFragment.this.getString(R.string.take_picture_success), 0).show();
                    }
                });
            }
        }
    };
    Runnable recordRunnable = new Runnable() { // from class: com.wy.ui.fragment.CameraBaseFragment.4
        @Override // java.lang.Runnable
        public void run() {
            CameraBaseFragment.this.recordSeconds++;
            if (CameraBaseFragment.this.vedioRecord.isRunning()) {
                CameraBaseFragment.this.handler.postDelayed(CameraBaseFragment.this.recordRunnable, 1000L);
            } else {
                CameraBaseFragment.this.handler.removeCallbacks(CameraBaseFragment.this.recordRunnable);
            }
            CameraBaseFragment.this.handler.sendEmptyMessage(1);
        }
    };
    Handler handler = new Handler() { // from class: com.wy.ui.fragment.CameraBaseFragment.5
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 1) {
                TextView textView = CameraBaseFragment.this.tvRecordVedio;
                CameraBaseFragment cameraBaseFragment = CameraBaseFragment.this;
                textView.setText(cameraBaseFragment.durationFormat(Integer.valueOf(cameraBaseFragment.recordSeconds)));
            }
        }
    };
    private final int MSG_RECORDER_TIMER = 1;

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        Unbinder unbinder = this.unbinder;
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
        Log.d(TAG, "onDestroyView isVedio:" + this.isVedio + ",vedioRecord:" + this.vedioRecord);
        if (!this.isVedio || this.vedioRecord == null) {
            return;
        }
        stopRecordVedio();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.rb_screen_change.setOnCheckedChangeListener(this);
        SurfaceHolder holder = this.surface_view.getHolder();
        holder.setFormat(-2);
        holder.addCallback(this.mSurfaceCallback);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams.startToStart = R.id.cl_preview;
        layoutParams.endToEnd = R.id.cl_preview;
        layoutParams.bottomToBottom = R.id.cl_preview;
        layoutParams.bottomMargin = PixelUtil.px2dip(getContext(), 80.0f);
        TextView textView = new TextView(getContext());
        this.tvRecordVedio = textView;
        textView.setLayoutParams(layoutParams);
        this.tvRecordVedio.setText("");
        this.tvRecordVedio.setTextColor(getActivity().getColor(R.color.red));
        this.tvRecordVedio.setVisibility(4);
        ((ConstraintLayout) view).addView(this.tvRecordVedio);
        this.mediaPlayer = new MediaPlayer();
        startChangeScreen();
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        this.iv_dev_camera.drawBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.camera_bg));
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mediaPlayer.stop();
        this.mediaPlayer.release();
        Log.d(TAG, "onDestroy");
        stopPreviewMobileCamera();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        Log.i(TAG, "进入前台 called.");
        super.onResume();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        Log.i(TAG, "进入后台 called.");
        super.onPause();
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        Drawable drawable;
        compoundButton.setChecked(!z);
        if (compoundButton.getId() != R.id.rb_screen_change) {
            return;
        }
        if (!this.isPreview) {
            drawable = getResources().getDrawable(R.mipmap.change_screen);
            this.is_fenping = true;
        } else {
            drawable = getResources().getDrawable(R.mipmap.change_screen_a);
            this.is_fenping = false;
        }
        this.rb_screen_change.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, drawable, (Drawable) null, (Drawable) null);
        startChangeScreen();
    }

    protected void startChangeScreen() {
        ViewGroup.LayoutParams layoutParams = this.ll_dev_camera.getLayoutParams();
        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) this.iv_dev_camera.getLayoutParams();
        if (layoutParams.height == -1) {
            if (!this.isPreview) {
                this.is_fenping = true;
                startPreviewMobileCamera();
                layoutParams.height = 0;
                this.iv_mobile_camera.bringToFront();
                int i = getResources().getDisplayMetrics().widthPixels;
                if (getClass().getSimpleName().equals("CameraEarFragment")) {
                    layoutParams2.dimensionRatio = "w,1:1";
                }
            }
        } else {
            this.is_fenping = false;
            layoutParams.height = -1;
            stopPreviewMobileCamera();
            if (getClass().getSimpleName().equals("CameraEarFragment")) {
                layoutParams2.dimensionRatio = "h,1:1";
            }
        }
        this.ll_dev_camera.setLayoutParams(layoutParams);
        this.iv_dev_camera.setLayoutParams(layoutParams2);
        this.iv_dev_camera.reDrawBitmap();
    }

    public void startPreviewMobileCamera() {
        try {
            if (this.mSurfaceHolder == null) {
                this.isPreview = false;
                SurfaceHolder holder = this.surface_view.getHolder();
                holder.setFormat(-2);
                holder.addCallback(this.mSurfaceCallback);
                Log.d(TAG, " zwn preview false");
                return;
            }
            if (this.isPreview) {
                return;
            }
            Log.d(TAG, " zwn preview start");
            Camera.Size optimalPreviewSize = MobileCamera.getInstance().getOptimalPreviewSize(this.iv_mobile_camera.getWidth(), this.iv_mobile_camera.getHeight());
            Log.d(TAG, "zwn size.width,size.height" + optimalPreviewSize.width + "," + optimalPreviewSize.height);
            this.surface_view.resize(optimalPreviewSize.width, optimalPreviewSize.height);
            MobileCamera.getInstance().startPreview(this.mSurfaceHolder, optimalPreviewSize.width, optimalPreviewSize.height);
            this.isPreview = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPreviewMobileCamera() {
        try {
            Log.d(TAG, getClass().getSimpleName() + " stopPreviewMobileCamera:" + this.isPreview);
            if (this.isPreview) {
                MobileCamera.getInstance().stopPreview();
            }
            this.isPreview = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCameraCallback() {
        BKCameraClient.getInstance().setCameraCallback(this.cameraCallback);
    }

    Bitmap rotateBitmap(Bitmap bitmap, float f) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(createBitmap);
        Matrix matrix = new Matrix();
        matrix.setRotate(f, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        canvas.drawBitmap(bitmap, matrix, this.mPaint);
        return createBitmap;
    }

    private Bitmap scaleBitmap(Bitmap bitmap, float f) {
        if (bitmap == null) {
            return null;
        }
        if (f == 0.0f) {
            return bitmap;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(f, f);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        if (createBitmap.equals(bitmap)) {
            return createBitmap;
        }
        bitmap.recycle();
        Log.d(TAG, "zwn......new_h>" + createBitmap.getHeight() + "..new_w>" + createBitmap.getWidth());
        return createBitmap;
    }

    Bitmap xuanzhuan_90(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setRotate(90.0f, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
        try {
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError unused) {
            return null;
        }
    }

    Bitmap xuanzhuan_degree(Bitmap bitmap, float f) {
        Matrix matrix = new Matrix();
        matrix.setRotate(f, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
        try {
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap toVerticalMirror(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap toHorizontalMirror(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    protected void startRecordVedio() {
        this.isVedio = true;
        if (this.vedioRecord == null) {
            this.vedioRecord = new VedioRecord();
        }
        if (Build.VERSION.SDK_INT < 30) {
            this.vedioPath = FileUtil.setVideoSaveFile(getContext()) + "/" + System.currentTimeMillis() + ".mp4";
        } else {
            this.vedioPath = FileUtil.getVideoFilePath(getActivity().getApplicationContext()) + "/" + System.currentTimeMillis() + ".mp4";
        }
        if (this.vedioRecord.start(this.vedioPath)) {
            this.tvRecordVedio.setText(durationFormat(0));
            this.tvRecordVedio.setVisibility(0);
            this.recordSeconds = 0;
            this.handler.postDelayed(this.recordRunnable, 1000L);
        }
    }

    protected void stopRecordVedio() {
        if (this.vedioRecord == null || !this.isVedio) {
            return;
        }
        this.isVedio = false;
        this.tvRecordVedio.setVisibility(4);
        this.handler.removeCallbacks(this.recordRunnable);
        this.vedioRecord.stop(new VedioRecord.StopRecordListener() { // from class: com.wy.ui.fragment.CameraBaseFragment.3
            @Override // com.wy.util.VedioRecord.StopRecordListener
            public void onFinished() {
                if (CameraBaseFragment.this.vedioPath != null) {
                    Log.d(CameraBaseFragment.TAG, "test ,vedioPath:" + CameraBaseFragment.this.vedioPath);
                    File file = new File(CameraBaseFragment.this.vedioPath);
                    Uri fromFile = Uri.fromFile(file);
                    if (Build.VERSION.SDK_INT < 30) {
                        Log.d("zwannian", "save video " + file.getName());
                        CameraBaseFragment.this.getActivity().sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", fromFile));
                    } else {
                        Log.d("zwannian", "save video " + file.getName());
                        CameraBaseFragment cameraBaseFragment = CameraBaseFragment.this;
                        cameraBaseFragment.saveVedioToGallery(cameraBaseFragment.getActivity().getApplicationContext(), file.getName());
                    }
                    CameraBaseFragment.this.vedioPath = null;
                    CameraBaseFragment.this.recordSeconds = 0;
                }
            }

            @Override // com.wy.util.VedioRecord.StopRecordListener
            public void onError(Exception exc) {
                Log.d(CameraBaseFragment.TAG, "vedioRecord onError");
                CameraBaseFragment.this.recordSeconds = 0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveVedioToGallery(Context context, String str) {
        OutputStream openOutputStream;
        try {
            Long valueOf = Long.valueOf(System.currentTimeMillis());
            ContentValues contentValues = new ContentValues();
            contentValues.put("_display_name", str);
            contentValues.put("mime_type", MimeTypes.VIDEO_MP4);
            contentValues.put("date_added", Long.valueOf(valueOf.longValue() / 1000));
            contentValues.put("date_modified", Long.valueOf(valueOf.longValue() / 1000));
            contentValues.put("date_expires", Long.valueOf((valueOf.longValue() + 86400000) / 1000));
            contentValues.put("is_pending", (Integer) 1);
            ContentResolver contentResolver = context.getContentResolver();
            Uri insert = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (insert != null && (openOutputStream = contentResolver.openOutputStream(insert)) != null) {
                FileInputStream fileInputStream = new FileInputStream(FileUtil.getVideoFilePath(getActivity().getApplicationContext()) + "/" + str);
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read == -1) {
                        break;
                    } else {
                        openOutputStream.write(bArr, 0, read);
                    }
                }
                openOutputStream.flush();
                fileInputStream.close();
                openOutputStream.close();
            }
            Log.d(TAG, "uri:" + insert);
            contentValues.clear();
            contentValues.put("is_pending", (Integer) 0);
            contentValues.putNull("date_expires");
            contentResolver.update(insert, contentValues, null, null);
            Log.d(TAG, "saveVedioToGallery success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void shutter() {
        try {
            AssetFileDescriptor openFd = getResources().getAssets().openFd("take.wav");
            this.mediaPlayer.reset();
            this.mediaPlayer.setDataSource(openFd.getFileDescriptor(), openFd.getStartOffset(), openFd.getLength());
            this.mediaPlayer.prepare();
            this.mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void startCapturePicture() {
        if (this.isVedio) {
            return;
        }
        this.isCapture = true;
        shutter();
    }

    protected void startComparePicture() {
        if (this.cmpBitmap == null) {
            Toast.makeText(getContext(), getString(R.string.pls_take_pictrue), 0).show();
            return;
        }
        ViewGroup.LayoutParams layoutParams = this.ll_dev_camera.getLayoutParams();
        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) this.iv_dev_camera.getLayoutParams();
        if (layoutParams.height == -1) {
            layoutParams.height = 0;
            this.ll_dev_camera.setLayoutParams(layoutParams);
            this.ivCompare.bringToFront();
            this.ivCompare.setImageBitmap(this.cmpBitmap);
            if (getClass().getSimpleName().equals("CameraEarFragment")) {
                layoutParams2.dimensionRatio = "w,1:1";
            }
            Log.d(TAG, "  zwn 开始对比");
        } else {
            layoutParams.height = -1;
            this.ll_dev_camera.setLayoutParams(layoutParams);
            Log.d(TAG, "  zwn 停止对比");
            if (getClass().getSimpleName().equals("CameraEarFragment")) {
                layoutParams2.dimensionRatio = "h,1:1";
            }
        }
        this.iv_dev_camera.setLayoutParams(layoutParams2);
        this.iv_dev_camera.reDrawBitmap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:37:0x0075 -> B:14:0x0078). Please report as a decompilation issue!!! */
    public void saveBitmapToFile(String str, String str2, Bitmap bitmap) {
        FileOutputStream fileOutputStream = null;
        try {
            try {
                try {
                    File file = new File(str);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File file2 = new File(str, str2);
                    file2.createNewFile();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    FileOutputStream fileOutputStream2 = new FileOutputStream(file2);
                    try {
                        fileOutputStream2.write(byteArray);
                        fileOutputStream2.flush();
                        fileOutputStream2.close();
                        if (Build.VERSION.SDK_INT < 30) {
                            getActivity().sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file2)));
                        } else {
                            savePhotoToGallery(((FragmentActivity) Objects.requireNonNull(getActivity())).getApplicationContext(), str2);
                        }
                        fileOutputStream2.close();
                    } catch (Exception e) {
                        e = e;
                        fileOutputStream = fileOutputStream2;
                        e.printStackTrace();
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    } catch (Throwable th) {
                        th = th;
                        fileOutputStream = fileOutputStream2;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (IOException e4) {
            e4.printStackTrace();
        }
    }

    private void savePhotoToGallery(Context context, String str) {
        OutputStream openOutputStream;
        try {
            Long valueOf = Long.valueOf(System.currentTimeMillis());
            ContentValues contentValues = new ContentValues();
            contentValues.put("_display_name", str);
            contentValues.put("mime_type", "image/jpeg");
            contentValues.put("date_added", Long.valueOf(valueOf.longValue() / 1000));
            contentValues.put("date_modified", Long.valueOf(valueOf.longValue() / 1000));
            contentValues.put("date_expires", Long.valueOf((valueOf.longValue() + 86400000) / 1000));
            contentValues.put("is_pending", (Integer) 1);
            ContentResolver contentResolver = context.getContentResolver();
            Uri insert = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (insert != null && (openOutputStream = contentResolver.openOutputStream(insert)) != null) {
                FileInputStream fileInputStream = new FileInputStream(FileUtil.getPictureFilePath(((FragmentActivity) Objects.requireNonNull(getActivity())).getApplicationContext()) + "/" + str);
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read == -1) {
                        break;
                    } else {
                        openOutputStream.write(bArr, 0, read);
                    }
                }
                openOutputStream.flush();
                fileInputStream.close();
                openOutputStream.close();
            }
            Log.d(TAG, "uri:" + insert);
            contentValues.clear();
            contentValues.put("is_pending", (Integer) 0);
            contentValues.putNull("date_expires");
            contentResolver.update(insert, contentValues, null, null);
            Log.d(TAG, "savePhotoToGallery success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getLocalVideoDuration(String str) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(str);
            mediaPlayer.prepare();
            return mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String durationFormat(Integer num) {
        int intValue = num.intValue() / 3600;
        int intValue2 = num.intValue() % 3600;
        return String.format("REC %02d:%02d:%02d", Integer.valueOf(intValue), Integer.valueOf(intValue2 / 60), Integer.valueOf(intValue2 % 60));
    }
}