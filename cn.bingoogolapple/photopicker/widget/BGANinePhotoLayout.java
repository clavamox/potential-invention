package cn.bingoogolapple.photopicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import cn.bingoogolapple.baseadapter.BGAAdapterViewAdapter;
import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BGANinePhotoLayout extends FrameLayout implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final int ITEM_NUM_COLUMNS = 3;
    private int mCurrentClickItemPosition;
    private Delegate mDelegate;
    private int mItemCornerRadius;
    private int mItemSpanCount;
    private int mItemWhiteSpacing;
    private int mItemWidth;
    private int mOtherWhiteSpacing;
    private PhotoAdapter mPhotoAdapter;
    private BGAHeightWrapGridView mPhotoGv;
    private BGAImageView mPhotoIv;
    private int mPlaceholderDrawableResId;
    private boolean mShowAsLargeWhenOnlyOne;

    public interface Delegate {
        void onClickNinePhotoItem(BGANinePhotoLayout bGANinePhotoLayout, View view, int i, String str, List<String> list);
    }

    public BGANinePhotoLayout(Context context) {
        this(context, null);
    }

    public BGANinePhotoLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BGANinePhotoLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initDefaultAttrs();
        initCustomAttrs(context, attributeSet);
        afterInitDefaultAndCustomAttrs();
    }

    private void initDefaultAttrs() {
        this.mItemWidth = 0;
        this.mShowAsLargeWhenOnlyOne = true;
        this.mItemCornerRadius = 0;
        this.mItemWhiteSpacing = BGABaseAdapterUtil.dp2px(4.0f);
        this.mPlaceholderDrawableResId = R.mipmap.bga_pp_ic_holder_light;
        this.mOtherWhiteSpacing = BGABaseAdapterUtil.dp2px(100.0f);
        this.mItemSpanCount = 3;
    }

    private void initCustomAttrs(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.BGANinePhotoLayout);
        int indexCount = obtainStyledAttributes.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            initCustomAttr(obtainStyledAttributes.getIndex(i), obtainStyledAttributes);
        }
        obtainStyledAttributes.recycle();
    }

    private void initCustomAttr(int i, TypedArray typedArray) {
        if (i == R.styleable.BGANinePhotoLayout_bga_npl_showAsLargeWhenOnlyOne) {
            this.mShowAsLargeWhenOnlyOne = typedArray.getBoolean(i, this.mShowAsLargeWhenOnlyOne);
            return;
        }
        if (i == R.styleable.BGANinePhotoLayout_bga_npl_itemCornerRadius) {
            this.mItemCornerRadius = typedArray.getDimensionPixelSize(i, this.mItemCornerRadius);
            return;
        }
        if (i == R.styleable.BGANinePhotoLayout_bga_npl_itemWhiteSpacing) {
            this.mItemWhiteSpacing = typedArray.getDimensionPixelSize(i, this.mItemWhiteSpacing);
            return;
        }
        if (i == R.styleable.BGANinePhotoLayout_bga_npl_otherWhiteSpacing) {
            this.mOtherWhiteSpacing = typedArray.getDimensionPixelOffset(i, this.mOtherWhiteSpacing);
            return;
        }
        if (i == R.styleable.BGANinePhotoLayout_bga_npl_placeholderDrawable) {
            this.mPlaceholderDrawableResId = typedArray.getResourceId(i, this.mPlaceholderDrawableResId);
        } else if (i == R.styleable.BGANinePhotoLayout_bga_npl_itemWidth) {
            this.mItemWidth = typedArray.getDimensionPixelSize(i, this.mItemWidth);
        } else if (i == R.styleable.BGANinePhotoLayout_bga_npl_itemSpanCount) {
            this.mItemSpanCount = typedArray.getInteger(i, this.mItemSpanCount);
        }
    }

    private void afterInitDefaultAndCustomAttrs() {
        if (this.mItemWidth == 0) {
            int screenWidth = BGAPhotoPickerUtil.getScreenWidth() - this.mOtherWhiteSpacing;
            int i = this.mItemSpanCount;
            this.mItemWidth = (screenWidth - ((i - 1) * this.mItemWhiteSpacing)) / i;
        }
        BGAImageView bGAImageView = new BGAImageView(getContext());
        this.mPhotoIv = bGAImageView;
        bGAImageView.setClickable(true);
        this.mPhotoIv.setOnClickListener(this);
        BGAHeightWrapGridView bGAHeightWrapGridView = new BGAHeightWrapGridView(getContext());
        this.mPhotoGv = bGAHeightWrapGridView;
        bGAHeightWrapGridView.setHorizontalSpacing(this.mItemWhiteSpacing);
        this.mPhotoGv.setVerticalSpacing(this.mItemWhiteSpacing);
        this.mPhotoGv.setNumColumns(3);
        this.mPhotoGv.setOnItemClickListener(this);
        PhotoAdapter photoAdapter = new PhotoAdapter(getContext());
        this.mPhotoAdapter = photoAdapter;
        this.mPhotoGv.setAdapter((ListAdapter) photoAdapter);
        addView(this.mPhotoIv, new FrameLayout.LayoutParams(-2, -2));
        addView(this.mPhotoGv);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        this.mCurrentClickItemPosition = i;
        Delegate delegate = this.mDelegate;
        if (delegate != null) {
            delegate.onClickNinePhotoItem(this, view, i, this.mPhotoAdapter.getItem(i), this.mPhotoAdapter.getData());
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        this.mCurrentClickItemPosition = 0;
        Delegate delegate = this.mDelegate;
        if (delegate != null) {
            delegate.onClickNinePhotoItem(this, view, 0, this.mPhotoAdapter.getItem(0), this.mPhotoAdapter.getData());
        }
    }

    public void setData(ArrayList<String> arrayList) {
        if (arrayList.size() == 0) {
            setVisibility(8);
            return;
        }
        setVisibility(0);
        if (arrayList.size() == 1 && this.mShowAsLargeWhenOnlyOne) {
            this.mPhotoGv.setVisibility(8);
            this.mPhotoAdapter.setData(arrayList);
            this.mPhotoIv.setVisibility(0);
            int i = this.mItemWidth;
            int i2 = (i * 2) + this.mItemWhiteSpacing + (i / 4);
            this.mPhotoIv.setMaxWidth(i2);
            this.mPhotoIv.setMaxHeight(i2);
            int i3 = this.mItemCornerRadius;
            if (i3 > 0) {
                this.mPhotoIv.setCornerRadius(i3);
            }
            BGAImage.display(this.mPhotoIv, this.mPlaceholderDrawableResId, arrayList.get(0), i2);
            return;
        }
        this.mPhotoIv.setVisibility(8);
        this.mPhotoGv.setVisibility(0);
        ViewGroup.LayoutParams layoutParams = this.mPhotoGv.getLayoutParams();
        if (this.mItemSpanCount > 3) {
            int size = arrayList.size();
            int i4 = this.mItemSpanCount;
            if (size < i4) {
                i4 = arrayList.size();
            }
            this.mPhotoGv.setNumColumns(i4);
            layoutParams.width = (this.mItemWidth * i4) + ((i4 - 1) * this.mItemWhiteSpacing);
        } else if (arrayList.size() == 1) {
            this.mPhotoGv.setNumColumns(1);
            layoutParams.width = this.mItemWidth * 1;
        } else if (arrayList.size() == 2) {
            this.mPhotoGv.setNumColumns(2);
            layoutParams.width = (this.mItemWidth * 2) + this.mItemWhiteSpacing;
        } else if (arrayList.size() == 4) {
            this.mPhotoGv.setNumColumns(2);
            layoutParams.width = (this.mItemWidth * 2) + this.mItemWhiteSpacing;
        } else {
            this.mPhotoGv.setNumColumns(3);
            layoutParams.width = (this.mItemWidth * 3) + (this.mItemWhiteSpacing * 2);
        }
        this.mPhotoGv.setLayoutParams(layoutParams);
        this.mPhotoAdapter.setData(arrayList);
    }

    public void setDelegate(Delegate delegate) {
        this.mDelegate = delegate;
    }

    public ArrayList<String> getData() {
        return (ArrayList) this.mPhotoAdapter.getData();
    }

    public int getItemCount() {
        return this.mPhotoAdapter.getCount();
    }

    public String getCurrentClickItem() {
        return this.mPhotoAdapter.getItem(this.mCurrentClickItemPosition);
    }

    public int getCurrentClickItemPosition() {
        return this.mCurrentClickItemPosition;
    }

    private class PhotoAdapter extends BGAAdapterViewAdapter<String> {
        private int mImageSize;

        public PhotoAdapter(Context context) {
            super(context, R.layout.bga_pp_item_nine_photo);
            this.mImageSize = BGAPhotoPickerUtil.getScreenWidth() / (BGANinePhotoLayout.this.mItemSpanCount > 3 ? 8 : 6);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // cn.bingoogolapple.baseadapter.BGAAdapterViewAdapter
        public void fillData(BGAViewHolderHelper bGAViewHolderHelper, int i, String str) {
            if (BGANinePhotoLayout.this.mItemCornerRadius > 0) {
                ((BGAImageView) bGAViewHolderHelper.getView(R.id.iv_item_nine_photo_photo)).setCornerRadius(BGANinePhotoLayout.this.mItemCornerRadius);
            }
            BGAImage.display(bGAViewHolderHelper.getImageView(R.id.iv_item_nine_photo_photo), BGANinePhotoLayout.this.mPlaceholderDrawableResId, str, this.mImageSize);
        }
    }
}