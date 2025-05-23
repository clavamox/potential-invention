package com.wy;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.text.SimpleDateFormat;
import java.util.Date;

/* loaded from: classes.dex */
public class AppCrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String DISK_CACHE_PATH = "/CameraAppCrash/";
    private static AppCrashHandler crashHandler;
    private Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();

    public static AppCrashHandler init(Context context) {
        if (crashHandler == null) {
            crashHandler = new AppCrashHandler(context);
        }
        return crashHandler;
    }

    private AppCrashHandler(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        StringBuilder sb = new StringBuilder("Date: ");
        sb.append(simpleDateFormat.format(new Date())).append("\n");
        sb.append("========MODEL:" + Build.MODEL + " \n");
        sb.append("Stacktrace:\n\n");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        sb.append(stringWriter.toString());
        sb.append("===========\n");
        printWriter.close();
        write2ErrorLog(sb.toString());
        Toast.makeText(CameraApplication.getInstance(), "===" + sb.toString(), 0).show();
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.handler;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th);
        }
    }

    public String getFilePath() {
        String str;
        if (Environment.getExternalStorageState().equals("mounted")) {
            str = Environment.getExternalStorageDirectory() + DISK_CACHE_PATH;
        } else {
            str = CameraApplication.getInstance().getCacheDir() + DISK_CACHE_PATH;
        }
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        return str;
    }

    private void write2ErrorLog(String str) {
        File file = new File(getFilePath() + ("/Crash_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".txt"));
        FileOutputStream fileOutputStream = null;
        try {
            try {
                try {
                    if (file.exists()) {
                        file.delete();
                    } else {
                        file.getParentFile().mkdirs();
                    }
                    file.createNewFile();
                    FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                    try {
                        fileOutputStream2.write(str.getBytes());
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
                            } catch (Exception e2) {
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
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }
}