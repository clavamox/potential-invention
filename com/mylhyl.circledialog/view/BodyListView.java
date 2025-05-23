package com.mylhyl.circledialog.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.mylhyl.circledialog.callback.CircleItemLabel;
import com.mylhyl.circledialog.callback.CircleItemViewBinder;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.ItemsParams;
import com.mylhyl.circledialog.res.drawable.CircleDrawableSelector;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.view.listener.ItemsView;
import com.mylhyl.circledialog.view.listener.OnRvItemClickListener;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
final class BodyListView extends ListView implements ItemsView {
    private BaseAdapter mAdapter;
    private int mBackgroundColor;
    private int mBackgroundColorPress;
    private DialogParams mDialogParams;
    private ItemsParams mItemsParams;

    @Override // com.mylhyl.circledialog.view.listener.ItemsView
    public View getView() {
        return this;
    }

    @Override // com.mylhyl.circledialog.view.listener.ItemsView
    public void regOnItemClickListener(OnRvItemClickListener onRvItemClickListener) {
    }

    public BodyListView(Context context, DialogParams dialogParams, ItemsParams itemsParams) {
        super(context);
        this.mDialogParams = dialogParams;
        this.mItemsParams = itemsParams;
        init();
    }

    @Override // com.mylhyl.circledialog.view.listener.ItemsView
    public void refreshItems() {
        this.mAdapter.notifyDataSetChanged();
    }

    @Override // com.mylhyl.circledialog.view.listener.ItemsView
    public void regOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        setOnItemClickListener(onItemClickListener);
    }

    private void init() {
        this.mBackgroundColor = this.mItemsParams.backgroundColor != 0 ? this.mItemsParams.backgroundColor : this.mDialogParams.backgroundColor;
        this.mBackgroundColorPress = this.mItemsParams.backgroundColorPress != 0 ? this.mItemsParams.backgroundColorPress : this.mDialogParams.backgroundColorPress;
        setBackgroundColor(this.mBackgroundColor);
        setSelector(new CircleDrawableSelector(0, this.mBackgroundColorPress));
        setDivider(new ColorDrawable(CircleColor.divider));
        setDividerHeight(Controller.dp2px(getContext(), this.mItemsParams.dividerHeight));
        BaseAdapter baseAdapter = this.mItemsParams.adapter;
        this.mAdapter = baseAdapter;
        if (baseAdapter == null) {
            this.mAdapter = new ItemsAdapter(getContext(), this.mDialogParams, this.mItemsParams);
        }
        setAdapter((ListAdapter) this.mAdapter);
    }

    static class ItemsAdapter<T> extends BaseAdapter {
        private Context mContext;
        private DialogParams mDialogParams;
        private List<T> mItems;
        private ItemsParams mItemsParams;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        public ItemsAdapter(Context context, DialogParams dialogParams, ItemsParams itemsParams) {
            this.mContext = context;
            this.mDialogParams = dialogParams;
            this.mItemsParams = itemsParams;
            Object obj = itemsParams.items;
            if (obj != null && (obj instanceof Iterable)) {
                this.mItems = (List) obj;
            } else if (obj != null && obj.getClass().isArray()) {
                this.mItems = Arrays.asList((Object[]) obj);
            } else if (obj != null) {
                throw new IllegalArgumentException("entity must be an Array or an Iterable.");
            }
        }

        @Override // android.widget.Adapter
        public int getCount() {
            List<T> list = this.mItems;
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public T getItem(int i) {
            List<T> list = this.mItems;
            if (list != null) {
                return list.get(i);
            }
            return null;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            ItemsAdapter<T>.ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                TextView textView = new TextView(this.mContext);
                if (this.mDialogParams.typeface != null) {
                    textView.setTypeface(this.mDialogParams.typeface);
                }
                textView.setGravity(17);
                textView.setTextSize(this.mItemsParams.textSize);
                textView.setTextColor(this.mItemsParams.textColor);
                textView.setHeight(Controller.dp2px(this.mContext, this.mItemsParams.itemHeight));
                if (this.mItemsParams.padding != null) {
                    textView.setPadding(Controller.dp2px(this.mContext, this.mItemsParams.padding[0]), Controller.dp2px(this.mContext, this.mItemsParams.padding[1]), Controller.dp2px(this.mContext, this.mItemsParams.padding[2]), Controller.dp2px(this.mContext, this.mItemsParams.padding[3]));
                }
                if (this.mItemsParams.textGravity != 0) {
                    textView.setGravity(this.mItemsParams.textGravity);
                }
                viewHolder.item = textView;
                textView.setTag(viewHolder);
                view2 = textView;
            } else {
                view2 = view;
                viewHolder = (ViewHolder) view.getTag();
            }
            bindView(i, viewHolder);
            return view2;
        }

        private void bindView(int i, ItemsAdapter<T>.ViewHolder viewHolder) {
            String obj;
            T item = getItem(i);
            if (item instanceof CircleItemLabel) {
                obj = ((CircleItemLabel) item).getItemLabel();
            } else {
                obj = item.toString();
            }
            viewHolder.item.setText(String.valueOf(obj));
            CircleItemViewBinder circleItemViewBinder = this.mItemsParams.viewBinder;
            if (circleItemViewBinder != null) {
                circleItemViewBinder.onBinder(viewHolder.item, item, i);
            }
        }

        class ViewHolder {
            TextView item;

            ViewHolder() {
            }
        }
    }
}