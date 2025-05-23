package com.mylhyl.circledialog.view.listener;

import android.content.Context;
import android.widget.ImageView;

/* loaded from: classes.dex */
public abstract class OnAdPageChangeListener {
    public void onPageScrollStateChanged(int i) {
    }

    public void onPageScrolled(Context context, ImageView imageView, String str, int i, float f, int i2) {
    }

    public abstract void onPageSelected(Context context, ImageView imageView, String str, int i);
}