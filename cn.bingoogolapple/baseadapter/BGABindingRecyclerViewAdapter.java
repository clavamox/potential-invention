package cn.bingoogolapple.baseadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BGABindingRecyclerViewAdapter<M, B extends ViewDataBinding> extends RecyclerView.Adapter<BGABindingViewHolder<B>> {
    protected int mDefaultItemLayoutId;
    protected BGAHeaderAndFooterAdapter mHeaderAndFooterAdapter;
    private LayoutInflater mLayoutInflater;
    protected LifecycleOwner mLifecycleOwner;
    protected Object mStatusModel;
    protected Object mUiHandler;
    protected List<M> mData = new ArrayList();
    private boolean mIsIgnoreCheckedChanged = true;

    protected void bindSpecialModel(B b, int i, M m) {
    }

    public BGABindingRecyclerViewAdapter() {
    }

    public BGABindingRecyclerViewAdapter(int i) {
        this.mDefaultItemLayoutId = i;
    }

    protected LayoutInflater getLayoutInflater(View view) {
        if (this.mLayoutInflater == null) {
            this.mLayoutInflater = (LayoutInflater) view.getContext().getSystemService("layout_inflater");
        }
        return this.mLayoutInflater;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mData.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        int i2 = this.mDefaultItemLayoutId;
        if (i2 != 0) {
            return i2;
        }
        throw new RuntimeException("请在 " + getClass().getSimpleName() + " 中重写 getItemViewType 方法返回布局资源 id，或者使用 BGABindingRecyclerViewAdapter 一个参数的构造方法 BGABindingRecyclerViewAdapter(int defaultItemLayoutId)");
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public BGABindingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new BGABindingViewHolder(this, DataBindingUtil.inflate(getLayoutInflater(viewGroup), i, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(BGABindingViewHolder<B> bGABindingViewHolder, int i) {
        this.mIsIgnoreCheckedChanged = true;
        M item = getItem(i);
        B binding = bGABindingViewHolder.getBinding();
        binding.setLifecycleOwner(this.mLifecycleOwner);
        binding.setVariable(BR.viewHolder, bGABindingViewHolder);
        binding.setVariable(BR.uiHandler, this.mUiHandler);
        binding.setVariable(BR.statusModel, this.mStatusModel);
        binding.setVariable(BR.model, item);
        bindSpecialModel(binding, i, item);
        binding.executePendingBindings();
        this.mIsIgnoreCheckedChanged = false;
    }

    public boolean isIgnoreCheckedChanged() {
        return this.mIsIgnoreCheckedChanged;
    }

    public M getItem(int i) {
        return this.mData.get(i);
    }

    public List<M> getData() {
        return this.mData;
    }

    public void setStatusModel(Object obj) {
        this.mStatusModel = obj;
    }

    public void setUiHandler(Object obj) {
        this.mUiHandler = obj;
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.mLifecycleOwner = lifecycleOwner;
    }

    public final void notifyItemRangeInsertedWrapper(int i, int i2) {
        BGAHeaderAndFooterAdapter bGAHeaderAndFooterAdapter = this.mHeaderAndFooterAdapter;
        if (bGAHeaderAndFooterAdapter == null) {
            notifyItemRangeInserted(i, i2);
        } else {
            bGAHeaderAndFooterAdapter.notifyItemRangeInserted(bGAHeaderAndFooterAdapter.getHeadersCount() + i, i2);
        }
    }

    public void addNewData(List<M> list) {
        if (BGABaseAdapterUtil.isListNotEmpty(list)) {
            this.mData.addAll(0, list);
            notifyItemRangeInsertedWrapper(0, list.size());
        }
    }

    public void addMoreData(List<M> list) {
        if (BGABaseAdapterUtil.isListNotEmpty(list)) {
            int size = this.mData.size();
            List<M> list2 = this.mData;
            list2.addAll(list2.size(), list);
            notifyItemRangeInsertedWrapper(size, list.size());
        }
    }

    public final void notifyDataSetChangedWrapper() {
        BGAHeaderAndFooterAdapter bGAHeaderAndFooterAdapter = this.mHeaderAndFooterAdapter;
        if (bGAHeaderAndFooterAdapter == null) {
            notifyDataSetChanged();
        } else {
            bGAHeaderAndFooterAdapter.notifyDataSetChanged();
        }
    }

    public void setData(List<M> list) {
        if (this.mData == list) {
            return;
        }
        if (list == null) {
            this.mData = new ArrayList();
        } else {
            this.mData = list;
        }
        notifyDataSetChanged();
    }

    public void clear() {
        this.mData.clear();
        notifyDataSetChangedWrapper();
    }

    public final void notifyItemRemovedWrapper(int i) {
        BGAHeaderAndFooterAdapter bGAHeaderAndFooterAdapter = this.mHeaderAndFooterAdapter;
        if (bGAHeaderAndFooterAdapter == null) {
            notifyItemRemoved(i);
        } else {
            bGAHeaderAndFooterAdapter.notifyItemRemoved(bGAHeaderAndFooterAdapter.getHeadersCount() + i);
        }
    }

    public void removeItem(int i) {
        this.mData.remove(i);
        notifyItemRemovedWrapper(i);
    }

    public void removeItem(RecyclerView.ViewHolder viewHolder) {
        int adapterPosition = viewHolder.getAdapterPosition();
        BGAHeaderAndFooterAdapter bGAHeaderAndFooterAdapter = this.mHeaderAndFooterAdapter;
        if (bGAHeaderAndFooterAdapter != null) {
            this.mData.remove(adapterPosition - bGAHeaderAndFooterAdapter.getHeadersCount());
            this.mHeaderAndFooterAdapter.notifyItemRemoved(adapterPosition);
        } else {
            removeItem(adapterPosition);
        }
    }

    public void removeItem(M m) {
        removeItem(this.mData.indexOf(m));
    }

    public final void notifyItemInsertedWrapper(int i) {
        BGAHeaderAndFooterAdapter bGAHeaderAndFooterAdapter = this.mHeaderAndFooterAdapter;
        if (bGAHeaderAndFooterAdapter == null) {
            notifyItemInserted(i);
        } else {
            bGAHeaderAndFooterAdapter.notifyItemInserted(bGAHeaderAndFooterAdapter.getHeadersCount() + i);
        }
    }

    public void addItem(int i, M m) {
        this.mData.add(i, m);
        notifyItemInsertedWrapper(i);
    }

    public void addFirstItem(M m) {
        addItem(0, m);
    }

    public void addLastItem(M m) {
        addItem(this.mData.size(), m);
    }

    public final void notifyItemChangedWrapper(int i) {
        BGAHeaderAndFooterAdapter bGAHeaderAndFooterAdapter = this.mHeaderAndFooterAdapter;
        if (bGAHeaderAndFooterAdapter == null) {
            notifyItemChanged(i);
        } else {
            bGAHeaderAndFooterAdapter.notifyItemChanged(bGAHeaderAndFooterAdapter.getHeadersCount() + i);
        }
    }

    public void setItem(int i, M m) {
        this.mData.set(i, m);
        notifyItemChangedWrapper(i);
    }

    public void setItem(M m, M m2) {
        setItem(this.mData.indexOf(m), (int) m2);
    }

    public final void notifyItemMovedWrapper(int i, int i2) {
        BGAHeaderAndFooterAdapter bGAHeaderAndFooterAdapter = this.mHeaderAndFooterAdapter;
        if (bGAHeaderAndFooterAdapter == null) {
            notifyItemMoved(i, i2);
        } else {
            bGAHeaderAndFooterAdapter.notifyItemMoved(bGAHeaderAndFooterAdapter.getHeadersCount() + i, this.mHeaderAndFooterAdapter.getHeadersCount() + i2);
        }
    }

    public void moveItem(int i, int i2) {
        notifyItemChangedWrapper(i);
        notifyItemChangedWrapper(i2);
        List<M> list = this.mData;
        list.add(i2, list.remove(i));
        notifyItemMovedWrapper(i, i2);
    }

    public void moveItem(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        int adapterPosition = viewHolder.getAdapterPosition();
        int adapterPosition2 = viewHolder2.getAdapterPosition();
        BGAHeaderAndFooterAdapter bGAHeaderAndFooterAdapter = this.mHeaderAndFooterAdapter;
        if (bGAHeaderAndFooterAdapter != null) {
            bGAHeaderAndFooterAdapter.notifyItemChanged(adapterPosition);
            this.mHeaderAndFooterAdapter.notifyItemChanged(adapterPosition2);
            this.mData.add(adapterPosition2 - this.mHeaderAndFooterAdapter.getHeadersCount(), this.mData.remove(adapterPosition - this.mHeaderAndFooterAdapter.getHeadersCount()));
            this.mHeaderAndFooterAdapter.notifyItemMoved(adapterPosition, adapterPosition2);
            return;
        }
        moveItem(adapterPosition, adapterPosition2);
    }

    public M getFirstItem() {
        if (getItemCount() > 0) {
            return getItem(0);
        }
        return null;
    }

    public M getLastItem() {
        if (getItemCount() > 0) {
            return getItem(getItemCount() - 1);
        }
        return null;
    }

    public void addHeaderView(View view) {
        getHeaderAndFooterAdapter().addHeaderView(view);
    }

    public void addFooterView(View view) {
        getHeaderAndFooterAdapter().addFooterView(view);
    }

    public void removeHeaderView(View view) {
        getHeaderAndFooterAdapter().removeHeaderView(view);
    }

    public void removeFooterView(View view) {
        getHeaderAndFooterAdapter().removeFooterView(view);
    }

    public int getHeadersCount() {
        BGAHeaderAndFooterAdapter bGAHeaderAndFooterAdapter = this.mHeaderAndFooterAdapter;
        if (bGAHeaderAndFooterAdapter == null) {
            return 0;
        }
        return bGAHeaderAndFooterAdapter.getHeadersCount();
    }

    public int getFootersCount() {
        BGAHeaderAndFooterAdapter bGAHeaderAndFooterAdapter = this.mHeaderAndFooterAdapter;
        if (bGAHeaderAndFooterAdapter == null) {
            return 0;
        }
        return bGAHeaderAndFooterAdapter.getFootersCount();
    }

    public BGAHeaderAndFooterAdapter getHeaderAndFooterAdapter() {
        if (this.mHeaderAndFooterAdapter == null) {
            synchronized (this) {
                if (this.mHeaderAndFooterAdapter == null) {
                    this.mHeaderAndFooterAdapter = new BGAHeaderAndFooterAdapter(this);
                }
            }
        }
        return this.mHeaderAndFooterAdapter;
    }

    public boolean isHeaderOrFooter(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition() < getHeadersCount() || viewHolder.getAdapterPosition() >= getHeadersCount() + getItemCount();
    }
}