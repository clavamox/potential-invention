package com.wy.language;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/* loaded from: classes.dex */
public class LanguageUtils {
    private static final String TAG = "LanguageUtils";

    public static void initLanguage(Context context) {
        String readLanguageName = readLanguageName(context);
        if (TextUtils.isEmpty(readLanguageName)) {
            return;
        }
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(readLanguageName);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    private static String readLanguageName(Context context) {
        StringBuilder sb = new StringBuilder();
        AssetManager assets = context.getAssets();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assets.open("language.txt"), getCode(assets.open("language.txt"))));
            String readLine = bufferedReader.readLine();
            int i = 0;
            while (readLine != null) {
                sb.append(readLine + "\n");
                readLine = bufferedReader.readLine();
                i++;
                if (i == 200) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString().trim();
    }

    public static String getCode(InputStream inputStream) {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            int read = (bufferedInputStream.read() << 8) + bufferedInputStream.read();
            String str = read != 61371 ? read != 65279 ? read != 65534 ? "GBK" : "Unicode" : "UTF-16BE" : "UTF-8";
            inputStream.close();
            return str;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurrentLanguage() {
        try {
            Locale sysPreferredLocale = getSysPreferredLocale();
            Log.e(TAG, "getCurrentLanguage:locale  ----  >  " + sysPreferredLocale);
            String language = sysPreferredLocale.getLanguage();
            Log.e(TAG, "getCurrentLanguage:  ----  >  " + sysPreferredLocale.getCountry());
            Log.e(TAG, "getCurrentLanguage:  获取到语言 " + language);
            return language.equals("en") ? "en_US" : language.equals("zh") ? (sysPreferredLocale.getCountry().equals("TW") || sysPreferredLocale.getCountry().equals("HK") || sysPreferredLocale.toString().contains("#Hant")) ? "zh_TW" : "zh_CN" : language.equals("ko") ? "ko_KR" : "en_US";
        } catch (Exception e) {
            e.printStackTrace();
            return "en_US";
        }
    }

    public static Locale getSysPreferredLocale() {
        return LocaleList.getDefault().get(0);
    }
}