package com.wy.language;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.Locale;

/* loaded from: classes.dex */
public class Store {
    public static void setLanguageLocal(Context context, String str) {
        String systemLanguage = getSystemLanguage(context);
        SharedPreferences.Editor edit = context.getSharedPreferences("SP_LANGUAGTE", 0).edit();
        edit.putString("localLanguage", str);
        edit.putString("systemLanguage", systemLanguage);
        edit.commit();
    }

    public static String getLanguageLocal(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SP_LANGUAGTE", 0);
        String string = sharedPreferences.getString("localLanguage", "");
        String string2 = sharedPreferences.getString("systemLanguage", "");
        String systemLanguage = getSystemLanguage(context);
        if (systemLanguage.equals(string2)) {
            return string;
        }
        Log.d("Store", "修改了系统语言，以系统语言为准:" + systemLanguage);
        return systemLanguage;
    }

    private static String getSystemLanguage(Context context) {
        Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        return locale.getLanguage() + "-" + locale.getCountry();
    }
}