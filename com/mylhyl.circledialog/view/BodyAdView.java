package com.mylhyl.circledialog.view;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.mylhyl.circledialog.internal.CircleParams;
import com.mylhyl.circledialog.internal.Controller;
import com.mylhyl.circledialog.params.AdParams;
import com.mylhyl.circledialog.view.BuildViewAdImpl;
import com.mylhyl.circledialog.view.listener.AdView;
import com.mylhyl.circledialog.view.listener.OnAdItemClickListener;
import com.mylhyl.circledialog.view.listener.OnAdPageChangeListener;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
final class BodyAdView extends RelativeLayout implements AdView, ViewPager.OnPageChangeListener {
    private AdParams mAdParams;
    private OnAdItemClickListener mImageClickListener;
    private LinearLayout mLlIndicator;
    private OnAdPageChangeListener mPageChangeListener;
    private List<String> mUrls;
    private ViewPager mViewPager;
    private List<ImageView> mViews;

    @Override // com.mylhyl.circledialog.view.listener.AdView
    public View getView() {
        return this;
    }

    public BodyAdView(Context context, CircleParams circleParams) {
        super(context);
        this.mAdParams = circleParams.adParams;
        this.mPageChangeListener = circleParams.circleListeners.adPageChangeListener;
        init();
    }

    @Override // com.mylhyl.circledialog.view.listener.AdView
    public void regOnImageClickListener(OnAdItemClickListener onAdItemClickListener) {
        this.mImageClickListener = onAdItemClickListener;
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrolled(int i, float f, int i2) {
        OnAdPageChangeListener onAdPageChangeListener = this.mPageChangeListener;
        if (onAdPageChangeListener == null || this.mUrls == null) {
            return;
        }
        onAdPageChangeListener.onPageScrolled(getContext(), this.mViews.get(i), this.mUrls.get(i), i, f, i2);
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageSelected(int i) {
        pageSelectedToPoint(i % this.mViews.size());
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int i) {
        OnAdPageChangeListener onAdPageChangeListener = this.mPageChangeListener;
        if (onAdPageChangeListener != null) {
            onAdPageChangeListener.onPageScrollStateChanged(i);
        }
    }

    private void init() {
        initViewPager();
        initIndicator();
    }

    private void initViewPager() {
        WrapViewPage wrapViewPage = new WrapViewPage(getContext());
        this.mViewPager = wrapViewPage;
        wrapViewPage.setId(R.id.list);
        this.mViews = new ArrayList();
        int i = 0;
        if (this.mAdParams.urls != null) {
            this.mUrls = new ArrayList();
            String[] strArr = this.mAdParams.urls;
            int length = strArr.length;
            while (i < length) {
                String str = strArr[i];
                ImageView imageView = new ImageView(getContext());
                imageView.setAdjustViewBounds(true);
                this.mViews.add(imageView);
                this.mUrls.add(str);
                i++;
            }
        } else if (this.mAdParams.resIds != null) {
            int[] iArr = this.mAdParams.resIds;
            int length2 = iArr.length;
            while (i < length2) {
                int i2 = iArr[i];
                ImageView imageView2 = new ImageView(getContext());
                imageView2.setAdjustViewBounds(true);
                imageView2.setImageResource(i2);
                this.mViews.add(imageView2);
                i++;
            }
        }
        this.mViewPager.setAdapter(new PageAdapter());
        this.mViewPager.addOnPageChangeListener(this);
        this.mViewPager.setOverScrollMode(2);
        addView(this.mViewPager);
    }

    private void initIndicator() {
        if (this.mAdParams.isShowIndicator) {
            LinearLayout linearLayout = this.mLlIndicator;
            if (linearLayout != null) {
                linearLayout.removeAllViews();
            }
            LinearLayout linearLayout2 = new LinearLayout(getContext());
            this.mLlIndicator = linearLayout2;
            linearLayout2.setOrientation(0);
            this.mLlIndicator.setGravity(16);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, 80);
            layoutParams.addRule(14);
            layoutParams.addRule(8, R.id.list);
            this.mLlIndicator.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
            int dp2px = Controller.dp2px(getContext(), this.mAdParams.pointLeftRightMargin);
            layoutParams2.setMargins(dp2px, 0, dp2px, 0);
            for (int i = 0; i < this.mViews.size(); i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setSelected(true);
                imageView.setLayoutParams(layoutParams2);
                if (this.mAdParams.pointDrawableResId != 0) {
                    imageView.setImageResource(this.mAdParams.pointDrawableResId);
                } else {
                    imageView.setImageDrawable(new BuildViewAdImpl.SelectorPointDrawable(-1, 20));
                }
                this.mLlIndicator.addView(imageView);
            }
            addView(this.mLlIndicator);
            pageSelectedToPoint(0);
        }
    }

    private void pageSelectedToPoint(int i) {
        LinearLayout linearLayout;
        if (!this.mAdParams.isShowIndicator || (linearLayout = this.mLlIndicator) == null) {
            return;
        }
        int childCount = linearLayout.getChildCount();
        int i2 = 0;
        while (i2 < childCount) {
            View childAt = this.mLlIndicator.getChildAt(i2);
            childAt.setSelected(i2 == i);
            childAt.requestLayout();
            i2++;
        }
    }

    private class PageAdapter extends PagerAdapter {
        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getItemPosition(Object obj) {
            return -2;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        private PageAdapter() {
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            if (BodyAdView.this.mViews == null) {
                return 0;
            }
            return BodyAdView.this.mViews.size();
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            if (BodyAdView.this.mViews == null || BodyAdView.this.mViews.get(i) == null) {
                return null;
            }
            int size = i % BodyAdView.this.mViews.size();
            ImageView imageView = (ImageView) BodyAdView.this.mViews.get(size);
            imageView.setOnClickListener(new View.OnClickListener() { // from class: com.mylhyl.circledialog.view.BodyAdView.PageAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (BodyAdView.this.mImageClickListener != null) {
                        BodyAdView.this.mImageClickListener.onItemClick(view, BodyAdView.this.mViewPager.getCurrentItem() % BodyAdView.this.mViews.size());
                    }
                }
            });
            if (BodyAdView.this.mUrls != null && !BodyAdView.this.mUrls.isEmpty() && BodyAdView.this.mPageChangeListener != null) {
                BodyAdView.this.mPageChangeListener.onPageSelected(BodyAdView.this.getContext(), imageView, (String) BodyAdView.this.mUrls.get(size), size);
            }
            ViewParent parent = imageView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(imageView);
            }
            viewGroup.addView(imageView);
            return imageView;
        }
    }
}