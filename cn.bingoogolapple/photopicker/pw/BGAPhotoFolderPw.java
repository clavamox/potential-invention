package cn.bingoogolapple.photopicker.pw;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.model.BGAPhotoFolderModel;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class BGAPhotoFolderPw extends BGABasePopupWindow implements BGAOnRVItemClickListener {
    public static final int ANIM_DURATION = 300;
    private RecyclerView mContentRv;
    private int mCurrentPosition;
    private Delegate mDelegate;
    private FolderAdapter mFolderAdapter;
    private LinearLayout mRootLl;

    public interface Delegate {
        void executeDismissAnim();

        void onSelectedFolder(int i);
    }

    public BGAPhotoFolderPw(Activity activity, View view, Delegate delegate) {
        super(activity, R.layout.bga_pp_pw_photo_folder, view, -1, -1);
        this.mDelegate = delegate;
    }

    @Override // cn.bingoogolapple.photopicker.pw.BGABasePopupWindow
    protected void initView() {
        this.mRootLl = (LinearLayout) findViewById(R.id.ll_photo_folder_root);
        this.mContentRv = (RecyclerView) findViewById(R.id.rv_photo_folder_content);
    }

    @Override // cn.bingoogolapple.photopicker.pw.BGABasePopupWindow
    protected void setListener() {
        this.mRootLl.setOnClickListener(this);
        FolderAdapter folderAdapter = new FolderAdapter(this.mContentRv);
        this.mFolderAdapter = folderAdapter;
        folderAdapter.setOnRVItemClickListener(this);
    }

    @Override // cn.bingoogolapple.photopicker.pw.BGABasePopupWindow
    protected void processLogic() {
        setAnimationStyle(android.R.style.Animation);
        setBackgroundDrawable(new ColorDrawable(-1879048192));
        this.mContentRv.setLayoutManager(new LinearLayoutManager(this.mActivity));
        this.mContentRv.setAdapter(this.mFolderAdapter);
    }

    public void setData(ArrayList<BGAPhotoFolderModel> arrayList) {
        this.mFolderAdapter.setData(arrayList);
    }

    @Override // cn.bingoogolapple.photopicker.pw.BGABasePopupWindow
    public void show() {
        int[] iArr = new int[2];
        this.mAnchorView.getLocationInWindow(iArr);
        int height = iArr[1] + this.mAnchorView.getHeight();
        if (Build.VERSION.SDK_INT > 24) {
            setHeight(BGAPhotoPickerUtil.getScreenHeight() - height);
        }
        showAtLocation(this.mAnchorView, 0, 0, height);
        ViewCompat.animate(this.mContentRv).translationY(-this.mWindowRootView.getHeight()).setDuration(0L).start();
        ViewCompat.animate(this.mContentRv).translationY(0.0f).setDuration(300L).start();
        ViewCompat.animate(this.mRootLl).alpha(0.0f).setDuration(0L).start();
        ViewCompat.animate(this.mRootLl).alpha(1.0f).setDuration(300L).start();
    }

    @Override // cn.bingoogolapple.photopicker.pw.BGABasePopupWindow, android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.ll_photo_folder_root) {
            dismiss();
        }
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        ViewCompat.animate(this.mContentRv).translationY(-this.mWindowRootView.getHeight()).setDuration(300L).start();
        ViewCompat.animate(this.mRootLl).alpha(1.0f).setDuration(0L).start();
        ViewCompat.animate(this.mRootLl).alpha(0.0f).setDuration(300L).start();
        Delegate delegate = this.mDelegate;
        if (delegate != null) {
            delegate.executeDismissAnim();
        }
        this.mContentRv.postDelayed(new Runnable() { // from class: cn.bingoogolapple.photopicker.pw.BGAPhotoFolderPw.1
            @Override // java.lang.Runnable
            public void run() {
                BGAPhotoFolderPw.super.dismiss();
            }
        }, 300L);
    }

    public int getCurrentPosition() {
        return this.mCurrentPosition;
    }

    @Override // cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener
    public void onRVItemClick(ViewGroup viewGroup, View view, int i) {
        Delegate delegate = this.mDelegate;
        if (delegate != null && this.mCurrentPosition != i) {
            delegate.onSelectedFolder(i);
        }
        this.mCurrentPosition = i;
        dismiss();
    }

    private class FolderAdapter extends BGARecyclerViewAdapter<BGAPhotoFolderModel> {
        private int mImageSize;

        public FolderAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.bga_pp_item_photo_folder);
            this.mData = new ArrayList();
            this.mImageSize = BGAPhotoPickerUtil.getScreenWidth() / 10;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
        public void fillData(BGAViewHolderHelper bGAViewHolderHelper, int i, BGAPhotoFolderModel bGAPhotoFolderModel) {
            bGAViewHolderHelper.setText(R.id.tv_item_photo_folder_name, bGAPhotoFolderModel.name);
            bGAViewHolderHelper.setText(R.id.tv_item_photo_folder_count, String.valueOf(bGAPhotoFolderModel.getCount()));
            BGAImage.display(bGAViewHolderHelper.getImageView(R.id.iv_item_photo_folder_photo), R.mipmap.bga_pp_ic_holder_light, bGAPhotoFolderModel.coverPath, this.mImageSize);
        }
    }
}