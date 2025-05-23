package com.wy.ui.activity;

import android.app.Activity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class ActivityFinishUtil {
    private static List<Activity> activityList = new ArrayList();

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static void finishOtherAllActivity(Activity activity) {
        for (Activity activity2 : activityList) {
            if (activity2 != activity) {
                activity2.finish();
            }
        }
    }

    public static void finishAllActivity() {
        Iterator<Activity> it = activityList.iterator();
        while (it.hasNext()) {
            it.next().finish();
        }
    }
}