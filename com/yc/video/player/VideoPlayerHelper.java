package com.yc.video.player;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.yc.video.controller.BaseVideoController;
import com.yc.video.tool.PlayerUtils;

/* loaded from: classes.dex */
public class VideoPlayerHelper {
    private static VideoPlayerHelper sInstance;

    private VideoPlayerHelper() {
    }

    public static VideoPlayerHelper instance() {
        if (sInstance == null) {
            synchronized (VideoPlayerHelper.class) {
                if (sInstance == null) {
                    sInstance = new VideoPlayerHelper();
                }
            }
        }
        return sInstance;
    }

    protected ViewGroup getDecorView(Context context, BaseVideoController baseVideoController) {
        Activity activity = instance().getActivity(context, baseVideoController);
        if (activity == null) {
            return null;
        }
        return (ViewGroup) activity.getWindow().getDecorView();
    }

    protected ViewGroup getContentView(Context context, BaseVideoController baseVideoController) {
        Activity activity = instance().getActivity(context, baseVideoController);
        if (activity == null) {
            return null;
        }
        return (ViewGroup) activity.findViewById(R.id.content);
    }

    protected Activity getActivity(Context context, BaseVideoController baseVideoController) {
        if (baseVideoController != null) {
            Activity scanForActivity = PlayerUtils.scanForActivity(baseVideoController.getContext());
            return scanForActivity == null ? PlayerUtils.scanForActivity(context) : scanForActivity;
        }
        return PlayerUtils.scanForActivity(context);
    }

    protected void showSysBar(ViewGroup viewGroup, Context context, BaseVideoController baseVideoController) {
        viewGroup.setSystemUiVisibility(viewGroup.getSystemUiVisibility() & (-3) & (-4097));
        instance().getActivity(context, baseVideoController).getWindow().clearFlags(1024);
    }

    protected void hideSysBar(ViewGroup viewGroup, Context context, BaseVideoController baseVideoController) {
        viewGroup.setSystemUiVisibility(viewGroup.getSystemUiVisibility() | 2 | 4096);
        instance().getActivity(context, baseVideoController).getWindow().setFlags(1024, 1024);
    }

    protected boolean isLocalDataSource(String str, AssetFileDescriptor assetFileDescriptor) {
        if (assetFileDescriptor != null) {
            return true;
        }
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Uri parse = Uri.parse(str);
        return "android.resource".equals(parse.getScheme()) || "file".equals(parse.getScheme()) || RawResourceDataSource.RAW_RESOURCE_SCHEME.equals(parse.getScheme());
    }
}