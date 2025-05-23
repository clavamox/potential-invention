package com.wy.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import com.belon.camera.BKCameraClient;
import com.belon.camera.ErrorCode;
import com.belon.camera.callback.DeviceInfoCallback;
import com.wy.CameraApplication;
import com.wy.ui.fragment.WiFiSettingFragment;
import com.wy.ui.view.HelpWindow;
import com.xzf.camera.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 1024;
    private static final String TAG = "MainActivity";
    static String WIFI_PREFIX = "FA";
    private BKCameraClient bkCameraClient;
    private HelpWindow helpWindow;
    private boolean isFirstUse;

    @BindView(R.id.iv_start)
    ImageView iv_start;
    SharedPreferences preferences;

    @BindView(R.id.rb_con_help)
    RadioButton rb_con_help;

    @BindView(R.id.rb_tutorials)
    RadioButton rb_tutorials;

    @BindView(R.id.tv_con_state)
    TextView tv_con_state;

    @BindView(R.id.tv_remain_battery_number)
    TextView tv_remain_battery_number;

    @BindView(R.id.tv_remain_battery_title)
    TextView tv_remain_battery_title;

    @BindView(R.id.tv_remain_time_number)
    TextView tv_remain_time_number;

    @BindView(R.id.tv_remain_time_title)
    TextView tv_remain_time_title;
    private boolean isConnected = false;
    private Timer status_timer = null;
    private TimerTask status_task = null;
    private final DeviceInfoCallback deviceInfoCallback = new DeviceInfoCallback() { // from class: com.wy.ui.activity.MainActivity.2
        @Override // com.belon.camera.callback.DeviceInfoCallback
        public void onError(ErrorCode errorCode, Exception exc) {
            if (MainActivity.this.isConnected) {
                MainActivity.this.isConnected = false;
            } else {
                Log.i(MainActivity.TAG, "deviceInfoCallback onError");
            }
        }

        @Override // com.belon.camera.callback.DeviceInfoCallback
        public void receiveDeviceInfo(byte[] bArr) {
            byte b = bArr[63];
            byte b2 = 0;
            for (byte b3 = 0; b3 < 63; b3 = (byte) (b3 + 1)) {
                b2 = (byte) (b2 ^ bArr[b3]);
            }
            if (b == b2) {
                MainActivity.this.isConnected = true;
                byte[] bArr2 = new byte[32];
                final byte b4 = bArr[2];
                final int i = (b4 * 60) / 100;
                System.arraycopy(bArr, 5, bArr2, 0, 32);
                MainActivity.WIFI_PREFIX = new String(bArr2);
                MainActivity.WIFI_PREFIX = MainActivity.WIFI_PREFIX.replace((char) 0, ' ').replaceAll(" ", "");
                Log.d(MainActivity.TAG, " get device ssid  " + MainActivity.WIFI_PREFIX);
                CameraApplication.Wifiname = MainActivity.WIFI_PREFIX;
                if (!MainActivity.WIFI_PREFIX.contains("SS") && !MainActivity.WIFI_PREFIX.contains("WY") && !MainActivity.WIFI_PREFIX.contains("FY")) {
                    MainActivity.this.isConnected = false;
                    return;
                } else {
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: com.wy.ui.activity.MainActivity.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Log.d(MainActivity.TAG, " 获取电量。。。。。  ");
                            MainActivity.this.isConnected = true;
                            MainActivity.this.tv_con_state.setText(R.string.connected);
                            MainActivity.this.rb_con_help.setVisibility(4);
                            MainActivity.this.iv_start.setVisibility(0);
                            MainActivity.this.tv_remain_battery_number.setText(String.valueOf((int) b4));
                            MainActivity.this.tv_remain_time_number.setText(String.valueOf(i));
                        }
                    });
                    return;
                }
            }
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.wy.ui.activity.MainActivity.2.2
                @Override // java.lang.Runnable
                public void run() {
                    MainActivity.this.tv_con_state.setText(R.string.unconnected);
                    MainActivity.this.tv_remain_battery_number.setText(R.string.unconnected);
                    MainActivity.this.tv_remain_time_number.setText(R.string.unconnected);
                    MainActivity.this.rb_con_help.setVisibility(0);
                    MainActivity.this.iv_start.setVisibility(4);
                }
            });
        }
    };

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        Log.i(TAG, "进入主页面");
        this.ivNavBack.setVisibility(8);
        this.tvNavTitle.setVisibility(8);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.tv_remain_battery_title.setText(R.string.tv_remain_battery_title);
        this.tv_remain_time_title.setText(R.string.tv_remain_time_title);
        this.rb_con_help.setText(R.string.rb_con_help);
        this.rb_tutorials.setText(R.string.rb_tutorials);
        this.bkCameraClient = BKCameraClient.getInstance();
        this.tv_con_state.setText(R.string.unconnected);
        this.tv_remain_battery_number.setText(R.string.unconnected);
        this.tv_remain_time_number.setText(R.string.unconnected);
        this.rb_con_help.setVisibility(0);
        this.iv_start.setVisibility(4);
        SharedPreferences sharedPreferences = getSharedPreferences("isFirstUse", 0);
        this.preferences = sharedPreferences;
        boolean z = sharedPreferences.getBoolean("isFirstUse", true);
        this.isFirstUse = z;
        if (z) {
            showPrivacyPolicyDialog();
        }
        timer_status_start();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_con_state /* 2131230945 */:
                if (this.isConnected) {
                    startActivity(new Intent(this, (Class<?>) ProductListActivity.class));
                    break;
                } else {
                    startActivity(new Intent(this, (Class<?>) WiFiSettingActivity.class));
                    break;
                }
            case R.id.iv_start /* 2131230977 */:
                startActivity(new Intent(this, (Class<?>) ProductListActivity.class));
                break;
            case R.id.rb_con_help /* 2131231081 */:
                HelpWindow helpWindow = new HelpWindow(this);
                this.helpWindow = helpWindow;
                helpWindow.showAtLocation(getWindow().getDecorView(), 17, 0, 0);
                break;
            case R.id.rb_tutorials /* 2131231086 */:
                startActivity(new Intent(this, (Class<?>) VedioTutorialActivity.class));
                break;
        }
    }

    @Override // com.wy.ui.activity.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        timer_status_start();
        super.onResume();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        timer_status_start();
        super.onStart();
        if (CameraApplication.Willentersetting) {
            new WiFiSettingFragment().show(getSupportFragmentManager(), "wiFiSettingFragment");
            CameraApplication.Willentersetting = false;
        }
        Log.i(TAG, "进入主页面2");
        Log.i(TAG, "onStart called.");
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        timer_status_stop();
        Log.i(TAG, "onPause called.");
        super.onPause();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        timer_status_stop();
        Log.i(TAG, "onStop called.");
        super.onStop();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("按下了back键   onBackPressed()");
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4 || i == 3) {
            timer_status_stop();
            ActivityFinishUtil.finishAllActivity();
        }
        return super.onKeyDown(i, keyEvent);
    }

    private void timer_status_start() {
        if (this.status_task == null || this.status_timer == null) {
            this.status_task = new TimerTask() { // from class: com.wy.ui.activity.MainActivity.1
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    Log.i(MainActivity.TAG, "startGetDeviceInfo called.");
                    MainActivity.this.bkCameraClient.startGetDeviceInfo(BKCameraClient.CMD_DEVINFO, MainActivity.this.deviceInfoCallback);
                }
            };
            Log.i(TAG, "timer_status_start called.");
            Timer timer = new Timer();
            this.status_timer = timer;
            timer.schedule(this.status_task, 50L, 500L);
        }
    }

    public void timer_status_stop() {
        Timer timer = this.status_timer;
        if (timer == null || this.status_task == null) {
            return;
        }
        timer.cancel();
        this.status_task.cancel();
        this.status_timer = null;
        this.status_task = null;
        Log.i(TAG, "timer_status_stop called.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0 && ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") == 0) {
            Log.d("权限判断--------》", "含有权限");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1024);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 1024) {
            if (ActivityCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0 && ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                Log.d("权限判断--------》", "含有权限");
            } else {
                Log.d("权限判断--------》", "获取权限失败");
            }
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 1024 || Build.VERSION.SDK_INT < 30) {
            return;
        }
        if (Environment.isExternalStorageManager()) {
            Log.d("权限判断--------》", "含有权限");
        } else {
            Log.d("权限判断--------》", "获取权限失败");
        }
    }

    public void showPrivacyPolicyDialog() {
        String initAssets = initAssets();
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_privacy_policy_style, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.tv_title)).setText(R.string.statement);
        ((TextView) inflate.findViewById(R.id.tv_content)).setText(initAssets);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflate);
        builder.setNegativeButton(R.string.disagree_privacy_policy, new DialogInterface.OnClickListener() { // from class: com.wy.ui.activity.MainActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor edit = MainActivity.this.preferences.edit();
                edit.putBoolean("isFirstUse", true);
                edit.commit();
                dialogInterface.cancel();
                MainActivity.this.finish();
            }
        });
        builder.setNeutralButton(R.string.agree_privacy_policy, new DialogInterface.OnClickListener() { // from class: com.wy.ui.activity.MainActivity.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor edit = MainActivity.this.preferences.edit();
                edit.putBoolean("isFirstUse", false);
                edit.commit();
                MainActivity.this.checkPermissions();
            }
        });
        AlertDialog create = builder.create();
        create.show();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int i = displayMetrics.heightPixels;
        int i2 = displayMetrics.widthPixels;
        WindowManager.LayoutParams attributes = create.getWindow().getAttributes();
        attributes.width = i2 - 20;
        attributes.height = i / 2;
        create.getWindow().setAttributes(attributes);
    }

    public String initAssets() {
        return getString(getResources().openRawResource(R.raw.privacypolicy));
    }

    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            inputStreamReader = null;
        }
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer("");
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }

    private void showPrivacy() {
        final PrivacyDialog privacyDialog = new PrivacyDialog(this);
        TextView textView = (TextView) privacyDialog.findViewById(R.id.tv_privacy_tips);
        TextView textView2 = (TextView) privacyDialog.findViewById(R.id.btn_exit);
        TextView textView3 = (TextView) privacyDialog.findViewById(R.id.btn_enter);
        privacyDialog.show();
        String string = getResources().getString(R.string.privacy_tips);
        String string2 = getResources().getString(R.string.privacy_tips_key1);
        int indexOf = string.indexOf(string2);
        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), indexOf, string2.length() + indexOf, 34);
        spannableString.setSpan(new AbsoluteSizeSpan(18, true), indexOf, string2.length() + indexOf, 34);
        spannableString.setSpan(new ClickableSpan() { // from class: com.wy.ui.activity.MainActivity.5
            @Override // android.text.style.ClickableSpan
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) PrivacyPolicy.class));
            }

            @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setUnderlineText(false);
            }
        }, indexOf, string2.length() + indexOf, 34);
        textView.setHighlightColor(0);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams attributes = privacyDialog.getWindow().getAttributes();
        attributes.width = (int) (defaultDisplay.getWidth() * 0.8d);
        privacyDialog.getWindow().setAttributes(attributes);
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.activity.MainActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                privacyDialog.dismiss();
                SharedPreferences.Editor edit = MainActivity.this.preferences.edit();
                edit.putBoolean("isFirstUse", true);
                edit.commit();
                MainActivity.this.finish();
            }
        });
        textView3.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.activity.MainActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                privacyDialog.dismiss();
                SharedPreferences.Editor edit = MainActivity.this.preferences.edit();
                edit.putBoolean("isFirstUse", false);
                edit.commit();
                MainActivity.this.checkPermissions();
            }
        });
    }
}