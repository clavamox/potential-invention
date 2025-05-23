package com.wy.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import com.wy.ui.fragment.TutorialsStep1Fragment;
import com.wy.ui.fragment.TutorialsStep2Fragment;
import com.wy.ui.fragment.TutorialsStep3Fragment;
import com.wy.ui.fragment.TutorialsStep4Fragment;
import com.wy.util.PixelUtil;
import com.xzf.camera.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TutorialsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_start_opt)
    Button btnStartOpt;
    List<Fragment> fragmentLists = new ArrayList();

    @BindView(R.id.point)
    LinearLayout layoutPoint;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_tutorials);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        this.fragmentLists.add(TutorialsStep1Fragment.newInstance());
        this.fragmentLists.add(TutorialsStep2Fragment.newInstance());
        this.fragmentLists.add(TutorialsStep3Fragment.newInstance());
        this.fragmentLists.add(TutorialsStep4Fragment.newInstance());
        for (int i = 0; i < this.fragmentLists.size(); i++) {
            initPoint(i);
        }
        this.viewpager.setAdapter(new TutorialAdapter(getSupportFragmentManager()));
        this.viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.wy.ui.activity.TutorialsActivity.1
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int i2) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int i2, float f, int i3) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i2) {
                if (i2 == TutorialsActivity.this.fragmentLists.size() - 1) {
                    TutorialsActivity.this.btnStartOpt.setVisibility(0);
                } else {
                    TutorialsActivity.this.btnStartOpt.setVisibility(4);
                }
                TutorialsActivity.this.updatePoint(i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePoint(int i) {
        for (int i2 = 0; i2 < this.fragmentLists.size(); i2++) {
            ImageView imageView = (ImageView) this.layoutPoint.getChildAt(i2);
            if (i2 == i) {
                imageView.setImageResource(R.drawable.point_checked);
            } else {
                imageView.setImageResource(R.drawable.point_normal);
            }
        }
    }

    private void initPoint(int i) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -1, 1.0f);
        layoutParams.setMarginEnd(PixelUtil.dip2px(this, 4.0f));
        layoutParams.setMarginStart(PixelUtil.dip2px(this, 4.0f));
        imageView.setLayoutParams(layoutParams);
        if (i == 0) {
            imageView.setImageResource(R.drawable.point_checked);
        } else {
            imageView.setImageResource(R.drawable.point_normal);
        }
        this.layoutPoint.addView(imageView);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() != R.id.btn_start_opt) {
            return;
        }
        finish();
    }

    public class TutorialAdapter extends FragmentPagerAdapter {
        public TutorialAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override // androidx.fragment.app.FragmentPagerAdapter
        public Fragment getItem(int i) {
            return TutorialsActivity.this.fragmentLists.get(i);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return TutorialsActivity.this.fragmentLists.size();
        }
    }
}