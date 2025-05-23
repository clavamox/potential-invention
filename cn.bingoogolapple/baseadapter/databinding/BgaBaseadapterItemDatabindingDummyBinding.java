package cn.bingoogolapple.baseadapter.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import cn.bingoogolapple.baseadapter.BGABindingViewHolder;
import cn.bingoogolapple.baseadapter.R;

/* loaded from: classes.dex */
public abstract class BgaBaseadapterItemDatabindingDummyBinding extends ViewDataBinding {

    @Bindable
    protected Object mModel;

    @Bindable
    protected Object mStatusModel;

    @Bindable
    protected Object mUiHandler;

    @Bindable
    protected BGABindingViewHolder mViewHolder;

    public abstract void setModel(Object obj);

    public abstract void setStatusModel(Object obj);

    public abstract void setUiHandler(Object obj);

    public abstract void setViewHolder(BGABindingViewHolder bGABindingViewHolder);

    protected BgaBaseadapterItemDatabindingDummyBinding(Object obj, View view, int i) {
        super(obj, view, i);
    }

    public BGABindingViewHolder getViewHolder() {
        return this.mViewHolder;
    }

    public Object getModel() {
        return this.mModel;
    }

    public Object getUiHandler() {
        return this.mUiHandler;
    }

    public Object getStatusModel() {
        return this.mStatusModel;
    }

    public static BgaBaseadapterItemDatabindingDummyBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z) {
        return inflate(layoutInflater, viewGroup, z, DataBindingUtil.getDefaultComponent());
    }

    @Deprecated
    public static BgaBaseadapterItemDatabindingDummyBinding inflate(LayoutInflater layoutInflater, ViewGroup viewGroup, boolean z, Object obj) {
        return (BgaBaseadapterItemDatabindingDummyBinding) ViewDataBinding.inflateInternal(layoutInflater, R.layout.bga_baseadapter_item_databinding_dummy, viewGroup, z, obj);
    }

    public static BgaBaseadapterItemDatabindingDummyBinding inflate(LayoutInflater layoutInflater) {
        return inflate(layoutInflater, DataBindingUtil.getDefaultComponent());
    }

    @Deprecated
    public static BgaBaseadapterItemDatabindingDummyBinding inflate(LayoutInflater layoutInflater, Object obj) {
        return (BgaBaseadapterItemDatabindingDummyBinding) ViewDataBinding.inflateInternal(layoutInflater, R.layout.bga_baseadapter_item_databinding_dummy, null, false, obj);
    }

    public static BgaBaseadapterItemDatabindingDummyBinding bind(View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    @Deprecated
    public static BgaBaseadapterItemDatabindingDummyBinding bind(View view, Object obj) {
        return (BgaBaseadapterItemDatabindingDummyBinding) bind(obj, view, R.layout.bga_baseadapter_item_databinding_dummy);
    }
}