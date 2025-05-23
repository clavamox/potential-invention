package cn.bingoogolapple.baseadapter.databinding;

import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import cn.bingoogolapple.baseadapter.BGABindingViewHolder;
import cn.bingoogolapple.baseadapter.BR;

/* loaded from: classes.dex */
public class BgaBaseadapterItemDatabindingDummyBindingImpl extends BgaBaseadapterItemDatabindingDummyBinding {
    private static final ViewDataBinding.IncludedLayouts sIncludes = null;
    private static final SparseIntArray sViewsWithIds = null;
    private long mDirtyFlags;
    private final View mboundView0;

    @Override // androidx.databinding.ViewDataBinding
    protected boolean onFieldChange(int i, Object obj, int i2) {
        return false;
    }

    public BgaBaseadapterItemDatabindingDummyBindingImpl(DataBindingComponent dataBindingComponent, View view) {
        this(dataBindingComponent, view, mapBindings(dataBindingComponent, view, 1, sIncludes, sViewsWithIds));
    }

    private BgaBaseadapterItemDatabindingDummyBindingImpl(DataBindingComponent dataBindingComponent, View view, Object[] objArr) {
        super(dataBindingComponent, view, 0);
        this.mDirtyFlags = -1L;
        View view2 = (View) objArr[0];
        this.mboundView0 = view2;
        view2.setTag(null);
        setRootTag(view);
        invalidateAll();
    }

    @Override // androidx.databinding.ViewDataBinding
    public void invalidateAll() {
        synchronized (this) {
            this.mDirtyFlags = 16L;
        }
        requestRebind();
    }

    @Override // androidx.databinding.ViewDataBinding
    public boolean hasPendingBindings() {
        synchronized (this) {
            return this.mDirtyFlags != 0;
        }
    }

    @Override // androidx.databinding.ViewDataBinding
    public boolean setVariable(int i, Object obj) {
        if (BR.model == i) {
            setModel(obj);
        } else if (BR.statusModel == i) {
            setStatusModel(obj);
        } else if (BR.uiHandler == i) {
            setUiHandler(obj);
        } else {
            if (BR.viewHolder != i) {
                return false;
            }
            setViewHolder((BGABindingViewHolder) obj);
        }
        return true;
    }

    @Override // cn.bingoogolapple.baseadapter.databinding.BgaBaseadapterItemDatabindingDummyBinding
    public void setModel(Object obj) {
        this.mModel = obj;
    }

    @Override // cn.bingoogolapple.baseadapter.databinding.BgaBaseadapterItemDatabindingDummyBinding
    public void setStatusModel(Object obj) {
        this.mStatusModel = obj;
    }

    @Override // cn.bingoogolapple.baseadapter.databinding.BgaBaseadapterItemDatabindingDummyBinding
    public void setUiHandler(Object obj) {
        this.mUiHandler = obj;
    }

    @Override // cn.bingoogolapple.baseadapter.databinding.BgaBaseadapterItemDatabindingDummyBinding
    public void setViewHolder(BGABindingViewHolder bGABindingViewHolder) {
        this.mViewHolder = bGABindingViewHolder;
    }

    @Override // androidx.databinding.ViewDataBinding
    protected void executeBindings() {
        synchronized (this) {
            this.mDirtyFlags = 0L;
        }
    }
}