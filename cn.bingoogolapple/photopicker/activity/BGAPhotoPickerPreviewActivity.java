package cn.bingoogolapple.photopicker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.adapter.BGAPhotoPageAdapter;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGAHackyViewPager;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.util.ArrayList;
import uk.co.senab.photoview.PhotoViewAttacher;

/* loaded from: classes.dex */
public class BGAPhotoPickerPreviewActivity extends BGAPPToolbarActivity implements PhotoViewAttacher.OnViewTapListener {
    private static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private static final String EXTRA_IS_FROM_TAKE_PHOTO = "EXTRA_IS_FROM_TAKE_PHOTO";
    private static final String EXTRA_MAX_CHOOSE_COUNT = "EXTRA_MAX_CHOOSE_COUNT";
    private static final String EXTRA_PREVIEW_PHOTOS = "EXTRA_PREVIEW_PHOTOS";
    private static final String EXTRA_SELECTED_PHOTOS = "EXTRA_SELECTED_PHOTOS";
    private RelativeLayout mChooseRl;
    private TextView mChooseTv;
    private BGAHackyViewPager mContentHvp;
    private boolean mIsFromTakePhoto;
    private long mLastShowHiddenTime;
    private BGAPhotoPageAdapter mPhotoPageAdapter;
    private ArrayList<String> mSelectedPhotos;
    private TextView mSubmitTv;
    private TextView mTitleTv;
    private String mTopRightBtnText;
    private int mMaxChooseCount = 1;
    private boolean mIsHidden = false;

    public static class IntentBuilder {
        private Intent mIntent;

        public IntentBuilder(Context context) {
            this.mIntent = new Intent(context, (Class<?>) BGAPhotoPickerPreviewActivity.class);
        }

        public IntentBuilder maxChooseCount(int i) {
            this.mIntent.putExtra(BGAPhotoPickerPreviewActivity.EXTRA_MAX_CHOOSE_COUNT, i);
            return this;
        }

        public IntentBuilder selectedPhotos(ArrayList<String> arrayList) {
            this.mIntent.putStringArrayListExtra(BGAPhotoPickerPreviewActivity.EXTRA_SELECTED_PHOTOS, arrayList);
            return this;
        }

        public IntentBuilder previewPhotos(ArrayList<String> arrayList) {
            this.mIntent.putStringArrayListExtra(BGAPhotoPickerPreviewActivity.EXTRA_PREVIEW_PHOTOS, arrayList);
            return this;
        }

        public IntentBuilder currentPosition(int i) {
            this.mIntent.putExtra(BGAPhotoPickerPreviewActivity.EXTRA_CURRENT_POSITION, i);
            return this;
        }

        public IntentBuilder isFromTakePhoto(boolean z) {
            this.mIntent.putExtra(BGAPhotoPickerPreviewActivity.EXTRA_IS_FROM_TAKE_PHOTO, z);
            return this;
        }

        public Intent build() {
            return this.mIntent;
        }
    }

    public static ArrayList<String> getSelectedPhotos(Intent intent) {
        return intent.getStringArrayListExtra(EXTRA_SELECTED_PHOTOS);
    }

    public static boolean getIsFromTakePhoto(Intent intent) {
        return intent.getBooleanExtra(EXTRA_IS_FROM_TAKE_PHOTO, false);
    }

    @Override // cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity
    protected void initView(Bundle bundle) {
        setNoLinearContentView(R.layout.bga_pp_activity_photo_picker_preview);
        this.mContentHvp = (BGAHackyViewPager) findViewById(R.id.hvp_photo_picker_preview_content);
        this.mChooseRl = (RelativeLayout) findViewById(R.id.rl_photo_picker_preview_choose);
        this.mChooseTv = (TextView) findViewById(R.id.tv_photo_picker_preview_choose);
    }

    @Override // cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity
    protected void setListener() {
        this.mChooseTv.setOnClickListener(new BGAOnNoDoubleClickListener() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity.1
            @Override // cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener
            public void onNoDoubleClick(View view) {
                String item = BGAPhotoPickerPreviewActivity.this.mPhotoPageAdapter.getItem(BGAPhotoPickerPreviewActivity.this.mContentHvp.getCurrentItem());
                if (BGAPhotoPickerPreviewActivity.this.mSelectedPhotos.contains(item)) {
                    BGAPhotoPickerPreviewActivity.this.mSelectedPhotos.remove(item);
                    BGAPhotoPickerPreviewActivity.this.mChooseTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_normal, 0, 0, 0);
                    BGAPhotoPickerPreviewActivity.this.renderTopRightBtn();
                } else {
                    if (BGAPhotoPickerPreviewActivity.this.mMaxChooseCount == 1) {
                        BGAPhotoPickerPreviewActivity.this.mSelectedPhotos.clear();
                        BGAPhotoPickerPreviewActivity.this.mSelectedPhotos.add(item);
                        BGAPhotoPickerPreviewActivity.this.mChooseTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_checked, 0, 0, 0);
                        BGAPhotoPickerPreviewActivity.this.renderTopRightBtn();
                        return;
                    }
                    if (BGAPhotoPickerPreviewActivity.this.mMaxChooseCount == BGAPhotoPickerPreviewActivity.this.mSelectedPhotos.size()) {
                        BGAPhotoPickerUtil.show(BGAPhotoPickerPreviewActivity.this.getString(R.string.bga_pp_toast_photo_picker_max, new Object[]{Integer.valueOf(BGAPhotoPickerPreviewActivity.this.mMaxChooseCount)}));
                        return;
                    }
                    BGAPhotoPickerPreviewActivity.this.mSelectedPhotos.add(item);
                    BGAPhotoPickerPreviewActivity.this.mChooseTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_checked, 0, 0, 0);
                    BGAPhotoPickerPreviewActivity.this.renderTopRightBtn();
                }
            }
        });
        this.mContentHvp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity.2
            @Override // androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener, androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
                BGAPhotoPickerPreviewActivity.this.handlePageSelectedStatus();
            }
        });
    }

    @Override // cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity
    protected void processLogic(Bundle bundle) {
        int intExtra = getIntent().getIntExtra(EXTRA_MAX_CHOOSE_COUNT, 1);
        this.mMaxChooseCount = intExtra;
        if (intExtra < 1) {
            this.mMaxChooseCount = 1;
        }
        ArrayList<String> stringArrayListExtra = getIntent().getStringArrayListExtra(EXTRA_SELECTED_PHOTOS);
        this.mSelectedPhotos = stringArrayListExtra;
        if (stringArrayListExtra == null) {
            this.mSelectedPhotos = new ArrayList<>();
        }
        ArrayList<String> stringArrayListExtra2 = getIntent().getStringArrayListExtra(EXTRA_PREVIEW_PHOTOS);
        if (TextUtils.isEmpty(stringArrayListExtra2.get(0))) {
            stringArrayListExtra2.remove(0);
        }
        boolean booleanExtra = getIntent().getBooleanExtra(EXTRA_IS_FROM_TAKE_PHOTO, false);
        this.mIsFromTakePhoto = booleanExtra;
        if (booleanExtra) {
            this.mChooseRl.setVisibility(4);
        }
        int intExtra2 = getIntent().getIntExtra(EXTRA_CURRENT_POSITION, 0);
        this.mTopRightBtnText = getString(R.string.bga_pp_confirm);
        BGAPhotoPageAdapter bGAPhotoPageAdapter = new BGAPhotoPageAdapter(this, stringArrayListExtra2);
        this.mPhotoPageAdapter = bGAPhotoPageAdapter;
        this.mContentHvp.setAdapter(bGAPhotoPageAdapter);
        this.mContentHvp.setCurrentItem(intExtra2);
        this.mToolbar.postDelayed(new Runnable() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity.3
            @Override // java.lang.Runnable
            public void run() {
                BGAPhotoPickerPreviewActivity.this.hiddenToolBarAndChooseBar();
            }
        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_pp_menu_photo_picker_preview, menu);
        View actionView = menu.findItem(R.id.item_photo_picker_preview_title).getActionView();
        this.mTitleTv = (TextView) actionView.findViewById(R.id.tv_photo_picker_preview_title);
        TextView textView = (TextView) actionView.findViewById(R.id.tv_photo_picker_preview_submit);
        this.mSubmitTv = textView;
        textView.setOnClickListener(new BGAOnNoDoubleClickListener() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity.4
            @Override // cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener
            public void onNoDoubleClick(View view) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(BGAPhotoPickerPreviewActivity.EXTRA_SELECTED_PHOTOS, BGAPhotoPickerPreviewActivity.this.mSelectedPhotos);
                intent.putExtra(BGAPhotoPickerPreviewActivity.EXTRA_IS_FROM_TAKE_PHOTO, BGAPhotoPickerPreviewActivity.this.mIsFromTakePhoto);
                BGAPhotoPickerPreviewActivity.this.setResult(-1, intent);
                BGAPhotoPickerPreviewActivity.this.finish();
            }
        });
        renderTopRightBtn();
        handlePageSelectedStatus();
        return true;
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, this.mSelectedPhotos);
        intent.putExtra(EXTRA_IS_FROM_TAKE_PHOTO, this.mIsFromTakePhoto);
        setResult(0, intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePageSelectedStatus() {
        TextView textView = this.mTitleTv;
        if (textView == null || this.mPhotoPageAdapter == null) {
            return;
        }
        textView.setText((this.mContentHvp.getCurrentItem() + 1) + "/" + this.mPhotoPageAdapter.getCount());
        if (this.mSelectedPhotos.contains(this.mPhotoPageAdapter.getItem(this.mContentHvp.getCurrentItem()))) {
            this.mChooseTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_checked, 0, 0, 0);
        } else {
            this.mChooseTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.bga_pp_ic_cb_normal, 0, 0, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renderTopRightBtn() {
        if (this.mIsFromTakePhoto) {
            this.mSubmitTv.setEnabled(true);
            this.mSubmitTv.setText(this.mTopRightBtnText);
        } else if (this.mSelectedPhotos.size() == 0) {
            this.mSubmitTv.setEnabled(false);
            this.mSubmitTv.setText(this.mTopRightBtnText);
        } else {
            this.mSubmitTv.setEnabled(true);
            this.mSubmitTv.setText(this.mTopRightBtnText + "(" + this.mSelectedPhotos.size() + "/" + this.mMaxChooseCount + ")");
        }
    }

    @Override // uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener
    public void onViewTap(View view, float f, float f2) {
        if (System.currentTimeMillis() - this.mLastShowHiddenTime > 500) {
            this.mLastShowHiddenTime = System.currentTimeMillis();
            if (this.mIsHidden) {
                showTitleBarAndChooseBar();
            } else {
                hiddenToolBarAndChooseBar();
            }
        }
    }

    private void showTitleBarAndChooseBar() {
        RelativeLayout relativeLayout;
        if (this.mToolbar != null) {
            ViewCompat.animate(this.mToolbar).translationY(0.0f).setInterpolator(new DecelerateInterpolator(2.0f)).setListener(new ViewPropertyAnimatorListenerAdapter() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity.5
                @Override // androidx.core.view.ViewPropertyAnimatorListenerAdapter, androidx.core.view.ViewPropertyAnimatorListener
                public void onAnimationEnd(View view) {
                    BGAPhotoPickerPreviewActivity.this.mIsHidden = false;
                }
            }).start();
        }
        if (this.mIsFromTakePhoto || (relativeLayout = this.mChooseRl) == null) {
            return;
        }
        relativeLayout.setVisibility(0);
        ViewCompat.setAlpha(this.mChooseRl, 0.0f);
        ViewCompat.animate(this.mChooseRl).alpha(1.0f).setInterpolator(new DecelerateInterpolator(2.0f)).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hiddenToolBarAndChooseBar() {
        RelativeLayout relativeLayout;
        if (this.mToolbar != null) {
            ViewCompat.animate(this.mToolbar).translationY(-this.mToolbar.getHeight()).setInterpolator(new DecelerateInterpolator(2.0f)).setListener(new ViewPropertyAnimatorListenerAdapter() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity.6
                @Override // androidx.core.view.ViewPropertyAnimatorListenerAdapter, androidx.core.view.ViewPropertyAnimatorListener
                public void onAnimationEnd(View view) {
                    BGAPhotoPickerPreviewActivity.this.mIsHidden = true;
                    if (BGAPhotoPickerPreviewActivity.this.mChooseRl != null) {
                        BGAPhotoPickerPreviewActivity.this.mChooseRl.setVisibility(4);
                    }
                }
            }).start();
        }
        if (this.mIsFromTakePhoto || (relativeLayout = this.mChooseRl) == null) {
            return;
        }
        ViewCompat.animate(relativeLayout).alpha(0.0f).setInterpolator(new DecelerateInterpolator(2.0f)).start();
    }
}