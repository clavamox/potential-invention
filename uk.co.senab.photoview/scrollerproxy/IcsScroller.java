package uk.co.senab.photoview.scrollerproxy;

import android.content.Context;

/* loaded from: classes.dex */
public class IcsScroller extends GingerScroller {
    public IcsScroller(Context context) {
        super(context);
    }

    @Override // uk.co.senab.photoview.scrollerproxy.GingerScroller, uk.co.senab.photoview.scrollerproxy.ScrollerProxy
    public boolean computeScrollOffset() {
        return this.mScroller.computeScrollOffset();
    }
}