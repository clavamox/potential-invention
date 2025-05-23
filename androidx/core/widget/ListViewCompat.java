package androidx.core.widget;

import android.widget.ListView;

/* loaded from: classes.dex */
public final class ListViewCompat {
    public static void scrollListBy(ListView listView, int i) {
        listView.scrollListBy(i);
    }

    public static boolean canScrollList(ListView listView, int i) {
        return listView.canScrollList(i);
    }

    private ListViewCompat() {
    }
}