package com.mylhyl.circledialog.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.mylhyl.circledialog.callback.CircleItemLabel;
import com.mylhyl.circledialog.callback.CircleItemViewBinder;
import com.mylhyl.circledialog.internal.BackgroundHelper;
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
class BodyRecyclerView extends RecyclerView implements ItemsView {
    private RecyclerView.Adapter mAdapter;
    private Context mContext;
    protected DialogParams mDialogParams;
    private ItemsParams mItemsParams;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override // com.mylhyl.circledialog.view.listener.ItemsView
    public View getView() {
        return this;
    }

    @Override // com.mylhyl.circledialog.view.listener.ItemsView
    public void regOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
    }

    public BodyRecyclerView(Context context, ItemsParams itemsParams, DialogParams dialogParams) {
        super(context);
        this.mContext = context;
        this.mItemsParams = itemsParams;
        this.mDialogParams = dialogParams;
        init();
    }

    @Override // com.mylhyl.circledialog.view.listener.ItemsView
    public void refreshItems() {
        this.mAdapter.notifyDataSetChanged();
    }

    @Override // com.mylhyl.circledialog.view.listener.ItemsView
    public void regOnItemClickListener(OnRvItemClickListener onRvItemClickListener) {
        RecyclerView.Adapter adapter = this.mAdapter;
        if (adapter == null || !(adapter instanceof ItemsAdapter)) {
            return;
        }
        ((ItemsAdapter) adapter).setOnItemClickListener(onRvItemClickListener);
    }

    private void init() {
        configBackground();
        createLayoutManager();
        createItemDecoration();
        createAdapter();
    }

    private void configBackground() {
        setBackgroundColor(this.mItemsParams.backgroundColor != 0 ? this.mItemsParams.backgroundColor : this.mDialogParams.backgroundColor);
    }

    private void createLayoutManager() {
        if (this.mItemsParams.layoutManager == null) {
            this.mLayoutManager = new LinearLayoutManager(this.mContext, this.mItemsParams.linearLayoutManagerOrientation, false);
        } else if (this.mItemsParams.layoutManager instanceof StaggeredGridLayoutManager) {
            this.mLayoutManager = new StaggeredGridLayoutManagerWrapper((StaggeredGridLayoutManager) this.mItemsParams.layoutManager);
        } else if (this.mItemsParams.layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) this.mItemsParams.layoutManager;
            if (gridLayoutManager.getSpanCount() == 1) {
                this.mLayoutManager = new LinearLayoutManager(this.mContext, this.mItemsParams.linearLayoutManagerOrientation, false);
            } else {
                this.mLayoutManager = new GridLayoutManagerWrapper(this.mContext, gridLayoutManager);
            }
        } else if (this.mItemsParams.layoutManager instanceof LinearLayoutManager) {
            this.mLayoutManager = new LinearLayoutManagerWrapper(this.mContext, (LinearLayoutManager) this.mItemsParams.layoutManager);
        } else {
            this.mLayoutManager = this.mItemsParams.layoutManager;
        }
        setLayoutManager(this.mLayoutManager);
        setHasFixedSize(true);
    }

    private void createItemDecoration() {
        if (this.mItemsParams.dividerHeight > 0) {
            RecyclerView.LayoutManager layoutManager = this.mLayoutManager;
            if (layoutManager instanceof RecyclerView.LayoutManager) {
                if ((layoutManager instanceof GridLayoutManager) && this.mItemsParams.itemDecoration == null) {
                    this.mItemsParams.itemDecoration = new GridItemDecoration(new ColorDrawable(CircleColor.divider), Controller.dp2px(this.mContext, this.mItemsParams.dividerHeight));
                } else if ((this.mLayoutManager instanceof LinearLayoutManager) && this.mItemsParams.itemDecoration == null) {
                    this.mItemsParams.itemDecoration = new LinearItemDecoration(new ColorDrawable(CircleColor.divider), Controller.dp2px(this.mContext, this.mItemsParams.dividerHeight), ((LinearLayoutManager) this.mLayoutManager).getOrientation());
                }
                addItemDecoration(this.mItemsParams.itemDecoration);
            }
        }
    }

    private void createAdapter() {
        RecyclerView.Adapter adapter = this.mItemsParams.adapterRv;
        this.mAdapter = adapter;
        if (adapter == null) {
            this.mAdapter = new ItemsAdapter(this.mContext, this.mItemsParams, this.mDialogParams, this.mLayoutManager);
            RecyclerView.LayoutManager layoutManager = this.mLayoutManager;
            if (layoutManager instanceof GridLayoutManager) {
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                if (gridLayoutManager.getSpanSizeLookup() instanceof GridLayoutManager.DefaultSpanSizeLookup) {
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: com.mylhyl.circledialog.view.BodyRecyclerView.1
                        @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                        public int getSpanSize(int i) {
                            int itemCount = BodyRecyclerView.this.mAdapter.getItemCount();
                            int spanCount = gridLayoutManager.getSpanCount();
                            int i2 = itemCount % spanCount;
                            if (i2 == 0 || i < itemCount - 1) {
                                return 1;
                            }
                            return (spanCount - i2) + 1;
                        }
                    });
                }
            }
        }
        setAdapter(this.mAdapter);
    }

    static class ItemsAdapter<T> extends RecyclerView.Adapter<Holder> {
        private int mBackgroundColorPress;
        private Context mContext;
        private OnRvItemClickListener mItemClickListener;
        private List<T> mItems;
        private ItemsParams mItemsParams;
        private RecyclerView.LayoutManager mLayoutManager;

        public ItemsAdapter(Context context, ItemsParams itemsParams, DialogParams dialogParams, RecyclerView.LayoutManager layoutManager) {
            this.mContext = context;
            this.mItemsParams = itemsParams;
            this.mLayoutManager = layoutManager;
            this.mBackgroundColorPress = itemsParams.backgroundColorPress != 0 ? itemsParams.backgroundColorPress : dialogParams.backgroundColorPress;
            Object obj = itemsParams.items;
            if (obj != null && (obj instanceof Iterable)) {
                this.mItems = (List) obj;
            } else if (obj != null && obj.getClass().isArray()) {
                this.mItems = Arrays.asList((Object[]) obj);
            } else if (obj != null) {
                throw new IllegalArgumentException("entity must be an Array or an Iterable.");
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextView textView = new TextView(this.mContext);
            textView.setGravity(17);
            RecyclerView.LayoutManager layoutManager = this.mLayoutManager;
            if (layoutManager instanceof LinearLayoutManager) {
                if (((LinearLayoutManager) layoutManager).getOrientation() == 0) {
                    textView.setLayoutParams(new RecyclerView.LayoutParams(-2, -2));
                    if (this.mItemsParams.padding != null) {
                        textView.setPadding(Controller.dp2px(this.mContext, this.mItemsParams.padding[0]), Controller.dp2px(this.mContext, this.mItemsParams.padding[1]), Controller.dp2px(this.mContext, this.mItemsParams.padding[2]), Controller.dp2px(this.mContext, this.mItemsParams.padding[3]));
                    } else {
                        textView.setPadding(10, 0, 10, 0);
                    }
                } else {
                    if (this.mItemsParams.padding != null) {
                        textView.setPadding(Controller.dp2px(this.mContext, this.mItemsParams.padding[0]), Controller.dp2px(this.mContext, this.mItemsParams.padding[1]), Controller.dp2px(this.mContext, this.mItemsParams.padding[2]), Controller.dp2px(this.mContext, this.mItemsParams.padding[3]));
                    }
                    textView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                }
            } else if ((layoutManager instanceof StaggeredGridLayoutManager) || (layoutManager instanceof GridLayoutManager)) {
                textView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            } else {
                textView.setLayoutParams(new RecyclerView.LayoutParams(-2, -2));
            }
            textView.setTextSize(this.mItemsParams.textSize);
            textView.setTextColor(this.mItemsParams.textColor);
            if (this.mItemsParams.textGravity != 0) {
                textView.setGravity(this.mItemsParams.textGravity);
            }
            textView.setHeight(Controller.dp2px(this.mContext, this.mItemsParams.itemHeight));
            return new Holder(textView, this.mItemClickListener);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(Holder holder, int i) {
            String obj;
            BackgroundHelper.handleBackground(holder.item, new CircleDrawableSelector(0, this.mBackgroundColorPress));
            T t = this.mItems.get(i);
            if (t instanceof CircleItemLabel) {
                obj = ((CircleItemLabel) t).getItemLabel();
            } else {
                obj = t.toString();
            }
            holder.item.setText(String.valueOf(obj));
            CircleItemViewBinder circleItemViewBinder = this.mItemsParams.viewBinder;
            if (circleItemViewBinder != null) {
                circleItemViewBinder.onBinder(holder.item, t, i);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            List<T> list = this.mItems;
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        public void setOnItemClickListener(OnRvItemClickListener onRvItemClickListener) {
            this.mItemClickListener = onRvItemClickListener;
        }

        static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView item;
            OnRvItemClickListener mOnRvItemClickListener;

            public Holder(TextView textView, OnRvItemClickListener onRvItemClickListener) {
                super(textView);
                this.item = textView;
                this.mOnRvItemClickListener = onRvItemClickListener;
                textView.setOnClickListener(this);
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OnRvItemClickListener onRvItemClickListener = this.mOnRvItemClickListener;
                if (onRvItemClickListener != null) {
                    onRvItemClickListener.onItemClick(view, getAdapterPosition());
                }
            }
        }
    }

    static class GridItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;
        private int mDividerHeight;

        public GridItemDecoration(Drawable drawable, int i) {
            this.mDivider = drawable;
            this.mDividerHeight = i;
        }

        private static boolean isLastColumn(RecyclerView recyclerView, int i, int i2, int i3) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                return (i + 1) % i2 == 0;
            }
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                return ((StaggeredGridLayoutManager) layoutManager).getOrientation() == 1 ? (i + 1) % i2 == 0 : i >= i3 - (i3 % i2);
            }
            return false;
        }

        private static boolean isLastRaw(RecyclerView recyclerView, int i, int i2, int i3) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                return i >= i3 - (i3 % i2);
            }
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                return ((StaggeredGridLayoutManager) layoutManager).getOrientation() == 1 ? i >= i3 - (i3 % i2) : (i + 1) % i2 == 0;
            }
            return false;
        }

        private static int getSpanCount(RecyclerView recyclerView) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                return ((GridLayoutManager) layoutManager).getSpanCount();
            }
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            }
            return layoutManager.getItemCount();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
            drawHorizontal(canvas, recyclerView);
            drawVertical(canvas, recyclerView);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(Rect rect, int i, RecyclerView recyclerView) {
            int spanCount = getSpanCount(recyclerView);
            int itemCount = recyclerView.getAdapter().getItemCount();
            if (isLastRaw(recyclerView, i, spanCount, itemCount)) {
                rect.set(0, 0, this.mDividerHeight, 0);
            } else if (isLastColumn(recyclerView, i, spanCount, itemCount)) {
                rect.set(0, 0, 0, this.mDividerHeight);
            } else {
                int i2 = this.mDividerHeight;
                rect.set(0, 0, i2, i2);
            }
        }

        private void drawHorizontal(Canvas canvas, RecyclerView recyclerView) {
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = recyclerView.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) childAt.getLayoutParams();
                int left = childAt.getLeft() - layoutParams.leftMargin;
                int bottom = childAt.getBottom() + layoutParams.bottomMargin;
                int right = childAt.getRight() + layoutParams.rightMargin;
                int i2 = this.mDividerHeight;
                this.mDivider.setBounds(left, bottom, right + i2, i2 + bottom);
                this.mDivider.draw(canvas);
            }
        }

        private void drawVertical(Canvas canvas, RecyclerView recyclerView) {
            int childCount = recyclerView.getChildCount() - 1;
            for (int i = 0; i < childCount; i++) {
                View childAt = recyclerView.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) childAt.getLayoutParams();
                int right = childAt.getRight() + layoutParams.rightMargin;
                this.mDivider.setBounds(right, childAt.getTop() - layoutParams.topMargin, this.mDividerHeight + right, childAt.getBottom() + layoutParams.bottomMargin);
                this.mDivider.draw(canvas);
            }
        }
    }

    static class LinearItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;
        private int mDividerHeight;
        private int mOrientation;

        public LinearItemDecoration(Drawable drawable, int i, int i2) {
            this.mDivider = drawable;
            this.mDividerHeight = i;
            this.mOrientation = i2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void onDrawOver(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
            if (this.mOrientation == 1) {
                drawVertical(canvas, recyclerView);
            } else {
                drawHorizontal(canvas, recyclerView);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(Rect rect, int i, RecyclerView recyclerView) {
            if (this.mOrientation == 1) {
                rect.set(0, 0, 0, this.mDividerHeight);
            } else if (i != recyclerView.getAdapter().getItemCount() - 1) {
                rect.set(0, 0, this.mDividerHeight, 0);
            }
        }

        private void drawVertical(Canvas canvas, RecyclerView recyclerView) {
            int paddingLeft = recyclerView.getPaddingLeft();
            int width = recyclerView.getWidth() - recyclerView.getPaddingRight();
            int childCount = recyclerView.getChildCount() - 1;
            for (int i = 0; i < childCount; i++) {
                View childAt = recyclerView.getChildAt(i);
                int bottom = childAt.getBottom() + ((RecyclerView.LayoutParams) childAt.getLayoutParams()).bottomMargin;
                this.mDivider.setBounds(paddingLeft, bottom, width, this.mDividerHeight + bottom);
                this.mDivider.draw(canvas);
            }
        }

        private void drawHorizontal(Canvas canvas, RecyclerView recyclerView) {
            int paddingTop = recyclerView.getPaddingTop();
            int height = recyclerView.getHeight() - recyclerView.getPaddingBottom();
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = recyclerView.getChildAt(i);
                int right = childAt.getRight() + ((RecyclerView.LayoutParams) childAt.getLayoutParams()).rightMargin;
                this.mDivider.setBounds(right, paddingTop, this.mDividerHeight + right, height);
                this.mDivider.draw(canvas);
            }
        }
    }

    static class LinearLayoutManagerWrapper extends LinearLayoutManager {
        public LinearLayoutManagerWrapper(Context context, LinearLayoutManager linearLayoutManager) {
            super(context, linearLayoutManager.getOrientation(), linearLayoutManager.getReverseLayout());
        }
    }

    static class GridLayoutManagerWrapper extends GridLayoutManager {
        public GridLayoutManagerWrapper(Context context, GridLayoutManager gridLayoutManager) {
            super(context, gridLayoutManager.getSpanCount(), gridLayoutManager.getOrientation(), gridLayoutManager.getReverseLayout());
        }
    }

    static class StaggeredGridLayoutManagerWrapper extends StaggeredGridLayoutManager {
        public StaggeredGridLayoutManagerWrapper(StaggeredGridLayoutManager staggeredGridLayoutManager) {
            super(staggeredGridLayoutManager.getSpanCount(), staggeredGridLayoutManager.getOrientation());
        }
    }
}