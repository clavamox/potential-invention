package cn.bingoogolapple.photopicker.adapters;

import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class BGASortableNinePhotoLayoutAdapter {
    public static void setDelegate(BGASortableNinePhotoLayout bGASortableNinePhotoLayout, BGASortableNinePhotoLayout.Delegate delegate) {
        bGASortableNinePhotoLayout.setDelegate(delegate);
    }

    public static void setData(BGASortableNinePhotoLayout bGASortableNinePhotoLayout, ArrayList<String> arrayList) {
        bGASortableNinePhotoLayout.setData(arrayList);
    }

    public static void setData(BGASortableNinePhotoLayout bGASortableNinePhotoLayout, boolean z) {
        bGASortableNinePhotoLayout.setEditable(z);
    }
}