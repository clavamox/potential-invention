package cn.bingoogolapple.photopicker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.baseadapter.BGAGridDivider;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.adapter.BGAPhotoPickerAdapter;
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener;
import cn.bingoogolapple.photopicker.model.BGAPhotoFolderModel;
import cn.bingoogolapple.photopicker.pw.BGAPhotoFolderPw;
import cn.bingoogolapple.photopicker.util.BGAAsyncTask;
import cn.bingoogolapple.photopicker.util.BGALoadPhotoTask;
import cn.bingoogolapple.photopicker.util.BGAPhotoHelper;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public class BGAPhotoPickerActivity extends BGAPPToolbarActivity implements BGAOnItemChildClickListener, BGAAsyncTask.Callback<ArrayList<BGAPhotoFolderModel>> {
    private static final String EXTRA_CAMERA_FILE_DIR = "EXTRA_CAMERA_FILE_DIR";
    private static final String EXTRA_MAX_CHOOSE_COUNT = "EXTRA_MAX_CHOOSE_COUNT";
    private static final String EXTRA_PAUSE_ON_SCROLL = "EXTRA_PAUSE_ON_SCROLL";
    private static final String EXTRA_SELECTED_PHOTOS = "EXTRA_SELECTED_PHOTOS";
    private static final int RC_PREVIEW = 2;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int SPAN_COUNT = 3;
    private static final String STATE_SELECTED_PHOTOS = "STATE_SELECTED_PHOTOS";
    private ImageView mArrowIv;
    private RecyclerView mContentRv;
    private BGAPhotoFolderModel mCurrentPhotoFolderModel;
    private BGALoadPhotoTask mLoadPhotoTask;
    private AppCompatDialog mLoadingDialog;
    private int mMaxChooseCount = 1;
    private BGAOnNoDoubleClickListener mOnClickShowPhotoFolderListener = new BGAOnNoDoubleClickListener() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity.1
        @Override // cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener
        public void onNoDoubleClick(View view) {
            if (BGAPhotoPickerActivity.this.mPhotoFolderModels == null || BGAPhotoPickerActivity.this.mPhotoFolderModels.size() <= 0) {
                return;
            }
            BGAPhotoPickerActivity.this.showPhotoFolderPw();
        }
    };
    private ArrayList<BGAPhotoFolderModel> mPhotoFolderModels;
    private BGAPhotoFolderPw mPhotoFolderPw;
    private BGAPhotoHelper mPhotoHelper;
    private BGAPhotoPickerAdapter mPicAdapter;
    private TextView mSubmitTv;
    private boolean mTakePhotoEnabled;
    private TextView mTitleTv;
    private String mTopRightBtnText;

    public static class IntentBuilder {
        private Intent mIntent;

        public IntentBuilder(Context context) {
            this.mIntent = new Intent(context, (Class<?>) BGAPhotoPickerActivity.class);
        }

        public IntentBuilder cameraFileDir(File file) {
            this.mIntent.putExtra(BGAPhotoPickerActivity.EXTRA_CAMERA_FILE_DIR, file);
            return this;
        }

        public IntentBuilder maxChooseCount(int i) {
            this.mIntent.putExtra(BGAPhotoPickerActivity.EXTRA_MAX_CHOOSE_COUNT, i);
            return this;
        }

        public IntentBuilder selectedPhotos(ArrayList<String> arrayList) {
            this.mIntent.putStringArrayListExtra(BGAPhotoPickerActivity.EXTRA_SELECTED_PHOTOS, arrayList);
            return this;
        }

        public IntentBuilder pauseOnScroll(boolean z) {
            this.mIntent.putExtra(BGAPhotoPickerActivity.EXTRA_PAUSE_ON_SCROLL, z);
            return this;
        }

        public Intent build() {
            return this.mIntent;
        }
    }

    public static ArrayList<String> getSelectedPhotos(Intent intent) {
        return intent.getStringArrayListExtra(EXTRA_SELECTED_PHOTOS);
    }

    @Override // cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity
    protected void initView(Bundle bundle) {
        setContentView(R.layout.bga_pp_activity_photo_picker);
        this.mContentRv = (RecyclerView) findViewById(R.id.rv_photo_picker_content);
    }

    @Override // cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity
    protected void setListener() {
        BGAPhotoPickerAdapter bGAPhotoPickerAdapter = new BGAPhotoPickerAdapter(this.mContentRv);
        this.mPicAdapter = bGAPhotoPickerAdapter;
        bGAPhotoPickerAdapter.setOnItemChildClickListener(this);
        if (getIntent().getBooleanExtra(EXTRA_PAUSE_ON_SCROLL, false)) {
            this.mContentRv.addOnScrollListener(new BGARVOnScrollListener(this));
        }
    }

    @Override // cn.bingoogolapple.photopicker.activity.BGAPPToolbarActivity
    protected void processLogic(Bundle bundle) {
        File file = (File) getIntent().getSerializableExtra(EXTRA_CAMERA_FILE_DIR);
        if (file != null) {
            this.mTakePhotoEnabled = true;
            this.mPhotoHelper = new BGAPhotoHelper(file);
        }
        int intExtra = getIntent().getIntExtra(EXTRA_MAX_CHOOSE_COUNT, 1);
        this.mMaxChooseCount = intExtra;
        if (intExtra < 1) {
            this.mMaxChooseCount = 1;
        }
        this.mTopRightBtnText = getString(R.string.bga_pp_confirm);
        this.mContentRv.setLayoutManager(new GridLayoutManager((Context) this, 3, 1, false));
        this.mContentRv.addItemDecoration(BGAGridDivider.newInstanceWithSpaceRes(R.dimen.bga_pp_size_photo_divider));
        ArrayList<String> stringArrayListExtra = getIntent().getStringArrayListExtra(EXTRA_SELECTED_PHOTOS);
        if (stringArrayListExtra != null && stringArrayListExtra.size() > this.mMaxChooseCount) {
            String str = stringArrayListExtra.get(0);
            stringArrayListExtra.clear();
            stringArrayListExtra.add(str);
        }
        this.mContentRv.setAdapter(this.mPicAdapter);
        this.mPicAdapter.setSelectedPhotos(stringArrayListExtra);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        showLoadingDialog();
        this.mLoadPhotoTask = new BGALoadPhotoTask(this, this, this.mTakePhotoEnabled).perform();
    }

    private void showLoadingDialog() {
        if (this.mLoadingDialog == null) {
            AppCompatDialog appCompatDialog = new AppCompatDialog(this);
            this.mLoadingDialog = appCompatDialog;
            appCompatDialog.setContentView(R.layout.bga_pp_dialog_loading);
            this.mLoadingDialog.setCancelable(false);
        }
        this.mLoadingDialog.show();
    }

    private void dismissLoadingDialog() {
        AppCompatDialog appCompatDialog = this.mLoadingDialog;
        if (appCompatDialog == null || !appCompatDialog.isShowing()) {
            return;
        }
        this.mLoadingDialog.dismiss();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bga_pp_menu_photo_picker, menu);
        View actionView = menu.findItem(R.id.item_photo_picker_title).getActionView();
        this.mTitleTv = (TextView) actionView.findViewById(R.id.tv_photo_picker_title);
        this.mArrowIv = (ImageView) actionView.findViewById(R.id.iv_photo_picker_arrow);
        this.mSubmitTv = (TextView) actionView.findViewById(R.id.tv_photo_picker_submit);
        this.mTitleTv.setOnClickListener(this.mOnClickShowPhotoFolderListener);
        this.mArrowIv.setOnClickListener(this.mOnClickShowPhotoFolderListener);
        this.mSubmitTv.setOnClickListener(new BGAOnNoDoubleClickListener() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity.2
            @Override // cn.bingoogolapple.baseadapter.BGAOnNoDoubleClickListener
            public void onNoDoubleClick(View view) {
                BGAPhotoPickerActivity bGAPhotoPickerActivity = BGAPhotoPickerActivity.this;
                bGAPhotoPickerActivity.returnSelectedPhotos(bGAPhotoPickerActivity.mPicAdapter.getSelectedPhotos());
            }
        });
        this.mTitleTv.setText(R.string.bga_pp_all_image);
        BGAPhotoFolderModel bGAPhotoFolderModel = this.mCurrentPhotoFolderModel;
        if (bGAPhotoFolderModel != null) {
            this.mTitleTv.setText(bGAPhotoFolderModel.name);
        }
        renderTopRightBtn();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void returnSelectedPhotos(ArrayList<String> arrayList) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_SELECTED_PHOTOS, arrayList);
        setResult(-1, intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPhotoFolderPw() {
        if (this.mArrowIv == null) {
            return;
        }
        if (this.mPhotoFolderPw == null) {
            this.mPhotoFolderPw = new BGAPhotoFolderPw(this, this.mToolbar, new BGAPhotoFolderPw.Delegate() { // from class: cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity.3
                @Override // cn.bingoogolapple.photopicker.pw.BGAPhotoFolderPw.Delegate
                public void onSelectedFolder(int i) {
                    BGAPhotoPickerActivity.this.reloadPhotos(i);
                }

                @Override // cn.bingoogolapple.photopicker.pw.BGAPhotoFolderPw.Delegate
                public void executeDismissAnim() {
                    ViewCompat.animate(BGAPhotoPickerActivity.this.mArrowIv).setDuration(300L).rotation(0.0f).start();
                }
            });
        }
        this.mPhotoFolderPw.setData(this.mPhotoFolderModels);
        this.mPhotoFolderPw.show();
        ViewCompat.animate(this.mArrowIv).setDuration(300L).rotation(-180.0f).start();
    }

    private void toastMaxCountTip() {
        BGAPhotoPickerUtil.show(getString(R.string.bga_pp_toast_photo_picker_max, new Object[]{Integer.valueOf(this.mMaxChooseCount)}));
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 != -1) {
            if (i2 == 0 && i == 2) {
                if (BGAPhotoPickerPreviewActivity.getIsFromTakePhoto(intent)) {
                    this.mPhotoHelper.deleteCameraFile();
                    return;
                } else {
                    this.mPicAdapter.setSelectedPhotos(BGAPhotoPickerPreviewActivity.getSelectedPhotos(intent));
                    renderTopRightBtn();
                    return;
                }
            }
            return;
        }
        if (i == 1) {
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(this.mPhotoHelper.getCameraFilePath()));
            startActivityForResult(new BGAPhotoPickerPreviewActivity.IntentBuilder(this).isFromTakePhoto(true).maxChooseCount(1).previewPhotos(arrayList).selectedPhotos(arrayList).currentPosition(0).build(), 2);
        } else if (i == 2) {
            if (BGAPhotoPickerPreviewActivity.getIsFromTakePhoto(intent)) {
                this.mPhotoHelper.refreshGallery();
            }
            returnSelectedPhotos(BGAPhotoPickerPreviewActivity.getSelectedPhotos(intent));
        }
    }

    private void renderTopRightBtn() {
        if (this.mSubmitTv == null) {
            return;
        }
        if (this.mPicAdapter.getSelectedCount() == 0) {
            this.mSubmitTv.setEnabled(false);
            this.mSubmitTv.setText(this.mTopRightBtnText);
        } else {
            this.mSubmitTv.setEnabled(true);
            this.mSubmitTv.setText(this.mTopRightBtnText + "(" + this.mPicAdapter.getSelectedCount() + "/" + this.mMaxChooseCount + ")");
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        BGAPhotoHelper.onSaveInstanceState(this.mPhotoHelper, bundle);
        bundle.putStringArrayList(STATE_SELECTED_PHOTOS, this.mPicAdapter.getSelectedPhotos());
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        BGAPhotoHelper.onRestoreInstanceState(this.mPhotoHelper, bundle);
        this.mPicAdapter.setSelectedPhotos(bundle.getStringArrayList(STATE_SELECTED_PHOTOS));
    }

    @Override // cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener
    public void onItemChildClick(ViewGroup viewGroup, View view, int i) {
        if (view.getId() == R.id.iv_item_photo_camera_camera) {
            handleTakePhoto();
        } else if (view.getId() == R.id.iv_item_photo_picker_photo) {
            changeToPreview(i);
        } else if (view.getId() == R.id.iv_item_photo_picker_flag) {
            handleClickSelectFlagIv(i);
        }
    }

    private void handleTakePhoto() {
        if (this.mMaxChooseCount == 1) {
            takePhoto();
        } else if (this.mPicAdapter.getSelectedCount() == this.mMaxChooseCount) {
            toastMaxCountTip();
        } else {
            takePhoto();
        }
    }

    private void takePhoto() {
        try {
            startActivityForResult(this.mPhotoHelper.getTakePhotoIntent(), 1);
        } catch (Exception unused) {
            BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_take_photo);
        }
    }

    private void changeToPreview(int i) {
        if (this.mCurrentPhotoFolderModel.isTakePhotoEnabled()) {
            i--;
        }
        startActivityForResult(new BGAPhotoPickerPreviewActivity.IntentBuilder(this).previewPhotos((ArrayList) this.mPicAdapter.getData()).selectedPhotos(this.mPicAdapter.getSelectedPhotos()).maxChooseCount(this.mMaxChooseCount).currentPosition(i).isFromTakePhoto(false).build(), 2);
    }

    private void handleClickSelectFlagIv(int i) {
        String item = this.mPicAdapter.getItem(i);
        if (this.mMaxChooseCount == 1) {
            if (this.mPicAdapter.getSelectedCount() > 0) {
                String remove = this.mPicAdapter.getSelectedPhotos().remove(0);
                if (TextUtils.equals(remove, item)) {
                    this.mPicAdapter.notifyItemChanged(i);
                } else {
                    this.mPicAdapter.notifyItemChanged(this.mPicAdapter.getData().indexOf(remove));
                    this.mPicAdapter.getSelectedPhotos().add(item);
                    this.mPicAdapter.notifyItemChanged(i);
                }
            } else {
                this.mPicAdapter.getSelectedPhotos().add(item);
                this.mPicAdapter.notifyItemChanged(i);
            }
            renderTopRightBtn();
            return;
        }
        if (!this.mPicAdapter.getSelectedPhotos().contains(item) && this.mPicAdapter.getSelectedCount() == this.mMaxChooseCount) {
            toastMaxCountTip();
            return;
        }
        if (this.mPicAdapter.getSelectedPhotos().contains(item)) {
            this.mPicAdapter.getSelectedPhotos().remove(item);
        } else {
            this.mPicAdapter.getSelectedPhotos().add(item);
        }
        this.mPicAdapter.notifyItemChanged(i);
        renderTopRightBtn();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reloadPhotos(int i) {
        if (i < this.mPhotoFolderModels.size()) {
            BGAPhotoFolderModel bGAPhotoFolderModel = this.mPhotoFolderModels.get(i);
            this.mCurrentPhotoFolderModel = bGAPhotoFolderModel;
            TextView textView = this.mTitleTv;
            if (textView != null) {
                textView.setText(bGAPhotoFolderModel.name);
            }
            this.mPicAdapter.setPhotoFolderModel(this.mCurrentPhotoFolderModel);
        }
    }

    @Override // cn.bingoogolapple.photopicker.util.BGAAsyncTask.Callback
    public void onPostExecute(ArrayList<BGAPhotoFolderModel> arrayList) {
        dismissLoadingDialog();
        this.mLoadPhotoTask = null;
        this.mPhotoFolderModels = arrayList;
        BGAPhotoFolderPw bGAPhotoFolderPw = this.mPhotoFolderPw;
        reloadPhotos(bGAPhotoFolderPw == null ? 0 : bGAPhotoFolderPw.getCurrentPosition());
    }

    @Override // cn.bingoogolapple.photopicker.util.BGAAsyncTask.Callback
    public void onTaskCancelled() {
        dismissLoadingDialog();
        this.mLoadPhotoTask = null;
    }

    private void cancelLoadPhotoTask() {
        BGALoadPhotoTask bGALoadPhotoTask = this.mLoadPhotoTask;
        if (bGALoadPhotoTask != null) {
            bGALoadPhotoTask.cancelTask();
            this.mLoadPhotoTask = null;
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        dismissLoadingDialog();
        cancelLoadPhotoTask();
        super.onDestroy();
    }
}