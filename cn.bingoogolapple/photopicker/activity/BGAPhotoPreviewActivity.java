package cn.bingoogolapple.photopicker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.adapter.BGAPhotoPageAdapter;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.imageloader.BGAImageLoader;
import cn.bingoogolapple.photopicker.util.BGAAsyncTask;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.bingoogolapple.photopicker.util.BGASavePhotoTask;
import cn.bingoogolapple.photopicker.widget.BGAHackyViewPager;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import uk.co.senab.photoview.PhotoViewAttacher;

/* loaded from: classes.dex */
public class BGAPhotoPreviewActivity extends BGAPPToolbarActivity implements PhotoViewAttacher.OnViewTapListener, BGAAsyncTask.Callback<Void> {
    private static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private static final String EXTRA_PREVIEW_PHOTOS = "EXTRA_PREVIEW_PHOTOS";
    private static final String EXTRA_SAVE_PHOTO_DIR = "EXTRA_SAVE_PHOTO_DIR";
    private BGAHackyViewPager mContentHvp;
    private ImageView mDownloadIv;
    private boolean mIsHidden = false;
    private boolean mIsSinglePreview;
    private long mLastShowHiddenTime;
    private BGAPhotoPageAdapter mPhotoPageAdapter;
    private File mSavePhotoDir;
    private BGASavePhotoTask mSavePhotoTask;
    private TextView mTitleTv;

    public static class IntentBuilder {
        private Intent mIntent;

        public IntentBuilder(Context context) {
            this.mIntent = new Intent(context, (Class<?>) BGAPhotoPreviewActivity.class);
        }

        public IntentBuilder saveImgDir(File file) {
            this.mIntent.putExtra(BGAPhotoPreviewActivity.EXTRA_SAVE_PHOTO_DIR, file);
            return this;
        }

        public IntentBuilder previewPhoto(String str) {
            this.mIntent.putStringArrayListExtra(BGAPhotoPreviewActivity.EXTRA_PREVIEW_PHOTOS, new ArrayList<>(Arrays.asList(str)));
            return this;
        }

        public IntentBuilder previewPhotos(ArrayList<String> arrayList) {
            this.mIntent.putStringArrayListExtra(BGAPhotoPreviewActivity.EXTRA_PREVIEW_PHOTOS, arrayList);
            return this;
        }

        public IntentBuilder currentPosition(int i) {
            this.mIntent.putExtra(BGAPhotoPreviewActivity.EXTRA_CURRENT_POSITION, i);
            return this;
        }

        public Intent build() {
            return this.mIntent;
        }
    }

    @Override // cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity
    protected void initView(Bundle bundle) {
        setNoLinearContentView(R.layout.bga_pp_activity_photo_preview);
        this.mContentHvp = (BGAHackyViewPager) findViewById(R.id.hvp_photo_preview_content);
    }

    @Override // cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity
    protected void setListener() {
        this.mContentHvp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity.1
            @Override // androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener, androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
                BGAPhotoPreviewActivity.this.renderTitleTv();
            }
        });
    }

    @Override // cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity
    protected void processLogic(Bundle bundle) {
        File file = (File) getIntent().getSerializableExtra(EXTRA_SAVE_PHOTO_DIR);
        this.mSavePhotoDir = file;
        if (file != null && !file.exists()) {
            this.mSavePhotoDir.mkdirs();
        }
        ArrayList<String> stringArrayListExtra = getIntent().getStringArrayListExtra(EXTRA_PREVIEW_PHOTOS);
        int intExtra = getIntent().getIntExtra(EXTRA_CURRENT_POSITION, 0);
        boolean z = stringArrayListExtra.size() == 1;
        this.mIsSinglePreview = z;
        int i = z ? 0 : intExtra;
        BGAPhotoPageAdapter bGAPhotoPageAdapter = new BGAPhotoPageAdapter(this, stringArrayListExtra);
        this.mPhotoPageAdapter = bGAPhotoPageAdapter;
        this.mContentHvp.setAdapter(bGAPhotoPageAdapter);
        this.mContentHvp.setCurrentItem(i);
        this.mToolbar.postDelayed(new Runnable() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity.2
            @Override // java.lang.Runnable
            public void run() {
                BGAPhotoPreviewActivity.this.hiddenTitleBar();
            }
        }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_pp_menu_photo_preview, menu);
        View actionView = menu.findItem(R.id.item_photo_preview_title).getActionView();
        this.mTitleTv = (TextView) actionView.findViewById(R.id.tv_photo_preview_title);
        ImageView imageView = (ImageView) actionView.findViewById(R.id.iv_photo_preview_download);
        this.mDownloadIv = imageView;
        imageView.setOnClickListener(new BGAOnNoDoubleClickListener() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity.3
            @Override // cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener
            public void onNoDoubleClick(View view) {
                if (BGAPhotoPreviewActivity.this.mSavePhotoTask == null) {
                    BGAPhotoPreviewActivity.this.savePic();
                }
            }
        });
        if (this.mSavePhotoDir == null) {
            this.mDownloadIv.setVisibility(4);
        }
        renderTitleTv();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void renderTitleTv() {
        TextView textView = this.mTitleTv;
        if (textView == null || this.mPhotoPageAdapter == null) {
            return;
        }
        if (this.mIsSinglePreview) {
            textView.setText(R.string.bga_pp_view_photo);
        } else {
            textView.setText((this.mContentHvp.getCurrentItem() + 1) + "/" + this.mPhotoPageAdapter.getCount());
        }
    }

    @Override // uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener
    public void onViewTap(View view, float f, float f2) {
        if (System.currentTimeMillis() - this.mLastShowHiddenTime > 500) {
            this.mLastShowHiddenTime = System.currentTimeMillis();
            if (this.mIsHidden) {
                showTitleBar();
            } else {
                hiddenTitleBar();
            }
        }
    }

    private void showTitleBar() {
        if (this.mToolbar != null) {
            ViewCompat.animate(this.mToolbar).translationY(0.0f).setInterpolator(new DecelerateInterpolator(2.0f)).setListener(new ViewPropertyAnimatorListenerAdapter() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity.4
                @Override // androidx.core.view.ViewPropertyAnimatorListenerAdapter, androidx.core.view.ViewPropertyAnimatorListener
                public void onAnimationEnd(View view) {
                    BGAPhotoPreviewActivity.this.mIsHidden = false;
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hiddenTitleBar() {
        if (this.mToolbar != null) {
            ViewCompat.animate(this.mToolbar).translationY(-this.mToolbar.getHeight()).setInterpolator(new DecelerateInterpolator(2.0f)).setListener(new ViewPropertyAnimatorListenerAdapter() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity.5
                @Override // androidx.core.view.ViewPropertyAnimatorListenerAdapter, androidx.core.view.ViewPropertyAnimatorListener
                public void onAnimationEnd(View view) {
                    BGAPhotoPreviewActivity.this.mIsHidden = true;
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void savePic() {
        if (this.mSavePhotoTask != null) {
            return;
        }
        String item = this.mPhotoPageAdapter.getItem(this.mContentHvp.getCurrentItem());
        if (item.startsWith("file")) {
            File file = new File(item.replace("file://", ""));
            if (file.exists()) {
                BGAPhotoPickerUtil.showSafe(getString(R.string.bga_pp_save_img_success_folder, new Object[]{file.getParentFile().getAbsolutePath()}));
                return;
            }
        }
        File file2 = new File(this.mSavePhotoDir, BGAPhotoPickerUtil.md5(item) + ".png");
        if (file2.exists()) {
            BGAPhotoPickerUtil.showSafe(getString(R.string.bga_pp_save_img_success_folder, new Object[]{this.mSavePhotoDir.getAbsolutePath()}));
        } else {
            this.mSavePhotoTask = new BGASavePhotoTask(this, this, file2);
            BGAImage.download(item, new BGAImageLoader.DownloadDelegate() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity.6
                @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader.DownloadDelegate
                public void onSuccess(String str, Bitmap bitmap) {
                    if (BGAPhotoPreviewActivity.this.mSavePhotoTask != null) {
                        BGAPhotoPreviewActivity.this.mSavePhotoTask.setBitmapAndPerform(bitmap);
                    }
                }

                @Override // cn.bingoogolapple.photopicker.imageloader.BGAImageLoader.DownloadDelegate
                public void onFailed(String str) {
                    BGAPhotoPreviewActivity.this.mSavePhotoTask = null;
                    BGAPhotoPickerUtil.show(R.string.bga_pp_save_img_failure);
                }
            });
        }
    }

    @Override // cn.bingoogolapple.photopicker.util.BGAAsyncTask.Callback
    public void onPostExecute(Void r1) {
        this.mSavePhotoTask = null;
    }

    @Override // cn.bingoogolapple.photopicker.util.BGAAsyncTask.Callback
    public void onTaskCancelled() {
        this.mSavePhotoTask = null;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        BGASavePhotoTask bGASavePhotoTask = this.mSavePhotoTask;
        if (bGASavePhotoTask != null) {
            bGASavePhotoTask.cancelTask();
            this.mSavePhotoTask = null;
        }
        super.onDestroy();
    }
}