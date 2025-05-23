package com.bumptech.glide.module;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

@Deprecated
/* loaded from: classes.dex */
public final class ManifestParser {
    private static final String GLIDE_MODULE_VALUE = "GlideModule";
    private static final String TAG = "ManifestParser";
    private final Context context;

    public ManifestParser(Context context) {
        this.context = context;
    }

    public List<GlideModule> parse() {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "Loading Glide modules");
        }
        ArrayList arrayList = new ArrayList();
        try {
            ApplicationInfo applicationInfo = this.context.getPackageManager().getApplicationInfo(this.context.getPackageName(), 128);
            if (applicationInfo.metaData == null) {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "Got null app info metadata");
                }
                return arrayList;
            }
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "Got app info metadata: " + applicationInfo.metaData);
            }
            for (String str : applicationInfo.metaData.keySet()) {
                if (GLIDE_MODULE_VALUE.equals(applicationInfo.metaData.get(str))) {
                    arrayList.add(parseModule(str));
                    if (Log.isLoggable(TAG, 3)) {
                        Log.d(TAG, "Loaded Glide module: " + str);
                    }
                }
            }
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Finished loading Glide modules");
            }
            return arrayList;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Unable to find metadata to parse GlideModules", e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x002d  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x002a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static com.bumptech.glide.module.GlideModule parseModule(java.lang.String r3) {
        /*
            java.lang.Class r3 = java.lang.Class.forName(r3)     // Catch: java.lang.ClassNotFoundException -> L42
            r0 = 0
            java.lang.Class[] r1 = new java.lang.Class[r0]     // Catch: java.lang.reflect.InvocationTargetException -> L12 java.lang.NoSuchMethodException -> L17 java.lang.IllegalAccessException -> L1c java.lang.InstantiationException -> L21
            java.lang.reflect.Constructor r1 = r3.getDeclaredConstructor(r1)     // Catch: java.lang.reflect.InvocationTargetException -> L12 java.lang.NoSuchMethodException -> L17 java.lang.IllegalAccessException -> L1c java.lang.InstantiationException -> L21
            java.lang.Object[] r0 = new java.lang.Object[r0]     // Catch: java.lang.reflect.InvocationTargetException -> L12 java.lang.NoSuchMethodException -> L17 java.lang.IllegalAccessException -> L1c java.lang.InstantiationException -> L21
            java.lang.Object r3 = r1.newInstance(r0)     // Catch: java.lang.reflect.InvocationTargetException -> L12 java.lang.NoSuchMethodException -> L17 java.lang.IllegalAccessException -> L1c java.lang.InstantiationException -> L21
            goto L26
        L12:
            r0 = move-exception
            throwInstantiateGlideModuleException(r3, r0)
            goto L25
        L17:
            r0 = move-exception
            throwInstantiateGlideModuleException(r3, r0)
            goto L25
        L1c:
            r0 = move-exception
            throwInstantiateGlideModuleException(r3, r0)
            goto L25
        L21:
            r0 = move-exception
            throwInstantiateGlideModuleException(r3, r0)
        L25:
            r3 = 0
        L26:
            boolean r0 = r3 instanceof com.bumptech.glide.module.GlideModule
            if (r0 == 0) goto L2d
            com.bumptech.glide.module.GlideModule r3 = (com.bumptech.glide.module.GlideModule) r3
            return r3
        L2d:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r2 = "Expected instanceof GlideModule, but found: "
            r1.<init>(r2)
            java.lang.StringBuilder r3 = r1.append(r3)
            java.lang.String r3 = r3.toString()
            r0.<init>(r3)
            throw r0
        L42:
            r3 = move-exception
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "Unable to find GlideModule implementation"
            r0.<init>(r1, r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.module.ManifestParser.parseModule(java.lang.String):com.bumptech.glide.module.GlideModule");
    }

    private static void throwInstantiateGlideModuleException(Class<?> cls, Exception exc) {
        throw new RuntimeException("Unable to instantiate GlideModule implementation for " + cls, exc);
    }
}