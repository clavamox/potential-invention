package com.wy.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import com.mylhyl.circledialog.CircleDialog;
import com.wy.CameraApplication;
import com.wy.listener.AlbumItemClickListener;
import com.wy.ui.fragment.PhotoFragment;
import com.wy.ui.fragment.VedioFragment;
import com.wy.util.FileUtil;
import com.xzf.camera.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class AlbumActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AlbumActivity";
    private AlbumAdapter albumAdapter;
    private List<Fragment> fragmentLists = new ArrayList();

    @BindView(R.id.ib_photo)
    ImageButton ib_photo;

    @BindView(R.id.ib_vedio)
    ImageButton ib_vedio;
    PhotoFragment photoFragment;

    @BindView(R.id.tv_complete)
    TextView tvComplete;

    @BindView(R.id.tv_delete)
    TextView tvDelete;
    VedioFragment vedioFragment;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_album);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        this.ib_photo.setOnClickListener(this);
        this.ib_vedio.setOnClickListener(this);
        this.ivNavMenu.setOnClickListener(this);
        if (Build.VERSION.SDK_INT < 30) {
            this.photoFragment = PhotoFragment.newInstance(FileUtil.setImageSaveFile(getApplicationContext()));
            this.vedioFragment = VedioFragment.newInstance(FileUtil.setVideoSaveFile(getApplicationContext()));
        } else {
            this.photoFragment = PhotoFragment.newInstance(FileUtil.getPictureFilePath(getApplicationContext()));
            this.vedioFragment = VedioFragment.newInstance(FileUtil.getVideoFilePath(getApplicationContext()));
        }
        this.photoFragment.setAlbumItemClickListener(new AlbumItemClickListener() { // from class: com.wy.ui.activity.AlbumActivity$$ExternalSyntheticLambda0
            @Override // com.wy.listener.AlbumItemClickListener
            public final void onItemClick(Fragment fragment, int i, String str, boolean z) {
                AlbumActivity.this.onItemClick(fragment, i, str, z);
            }
        });
        this.vedioFragment.setAlbumItemClickListener(new AlbumItemClickListener() { // from class: com.wy.ui.activity.AlbumActivity$$ExternalSyntheticLambda0
            @Override // com.wy.listener.AlbumItemClickListener
            public final void onItemClick(Fragment fragment, int i, String str, boolean z) {
                AlbumActivity.this.onItemClick(fragment, i, str, z);
            }
        });
        this.fragmentLists.add(this.photoFragment);
        this.fragmentLists.add(this.vedioFragment);
        AlbumAdapter albumAdapter = new AlbumAdapter(getSupportFragmentManager());
        this.albumAdapter = albumAdapter;
        this.viewpager.setAdapter(albumAdapter);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        this.photoFragment.reloadData();
        this.vedioFragment.reloadData();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Log.d(TAG, "zwn click......====>>>" + view.getId());
        switch (view.getId()) {
            case R.id.ib_photo /* 2131230920 */:
                this.viewpager.setCurrentItem(0);
                break;
            case R.id.ib_vedio /* 2131230921 */:
                this.viewpager.setCurrentItem(1);
                break;
            case R.id.iv_nav_leftxiangce /* 2131230968 */:
                Log.d(TAG, "zwn 相册click......====>>>" + CameraApplication.this_device);
                if (CameraApplication.this_device == 100) {
                    Log.d(TAG, "zwn 设备无效.....====>>>");
                    finish();
                    break;
                } else {
                    Intent intent = new Intent(this, (Class<?>) CameraActivity.class);
                    intent.putExtra("productTypeIndex", CameraApplication.this_device);
                    intent.putExtra("productItem", 0);
                    startActivity(intent);
                    finish();
                    break;
                }
            case R.id.iv_nav_right /* 2131230969 */:
                Log.d(TAG, "click......====>>>");
                edit(true);
                break;
            case R.id.tv_complete /* 2131231200 */:
                Log.d(TAG, "zwn tv_complete");
                break;
            case R.id.tv_delete /* 2131231206 */:
                Log.d(TAG, "zwn 点击了删除");
                break;
        }
    }

    private void deleteFile() {
        if (this.viewpager.getCurrentItem() == 0) {
            if (this.photoFragment.getSelectedDataListlected().size() > 0) {
                new CircleDialog.Builder().setTitle(getString(R.string.tip)).setText(getString(R.string.delete_confirm)).setPositive(getString(R.string.ok), new View.OnClickListener() { // from class: com.wy.ui.activity.AlbumActivity.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        Iterator<String> it = AlbumActivity.this.photoFragment.getSelectedDataListlected().iterator();
                        while (it.hasNext()) {
                            new File(it.next()).delete();
                        }
                        AlbumActivity.this.photoFragment.removeSelected();
                    }
                }).setNegative(getString(R.string.cancel), null).show(getSupportFragmentManager());
                return;
            } else {
                Toast.makeText(this, getString(R.string.pls_select_file), 0).show();
                return;
            }
        }
        if (this.vedioFragment.getSelectedDataListlected().size() > 0) {
            new CircleDialog.Builder().setTitle(getString(R.string.tip)).setText(getString(R.string.delete_confirm)).setPositive(getString(R.string.ok), new View.OnClickListener() { // from class: com.wy.ui.activity.AlbumActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Iterator<String> it = AlbumActivity.this.vedioFragment.getSelectedDataListlected().iterator();
                    while (it.hasNext()) {
                        new File(it.next()).delete();
                    }
                    AlbumActivity.this.vedioFragment.removeSelected();
                }
            }).setNegative(getString(R.string.cancel), null).show(getSupportFragmentManager());
        } else {
            Toast.makeText(this, getString(R.string.pls_select_file), 0).show();
        }
    }

    private void edit(boolean z) {
        if (z) {
            this.tvComplete.setVisibility(0);
            this.tvDelete.setVisibility(0);
            this.ivNavBack.setVisibility(4);
            this.ivNavMenu.setVisibility(4);
        } else {
            this.tvComplete.setVisibility(4);
            this.tvDelete.setVisibility(4);
            this.ivNavBack.setVisibility(0);
            this.ivNavMenu.setVisibility(0);
        }
        this.vedioFragment.setEdit(z);
        this.photoFragment.setEdit(z);
    }

    public void onItemClick(Fragment fragment, int i, String str, boolean z) {
        if (fragment == this.vedioFragment) {
            Intent intent = new Intent(this, (Class<?>) VedioPreviewActivtiy.class);
            intent.putExtra("filePath", str);
            startActivity(intent);
        }
        if (fragment == this.photoFragment) {
            Intent intent2 = new Intent(this, (Class<?>) PicturePreviewActivtiy.class);
            intent2.putExtra("filePath", str);
            startActivity(intent2);
        }
    }

    private void previewPicuture(int i) {
        BGAPhotoPreviewActivity.IntentBuilder intentBuilder = new BGAPhotoPreviewActivity.IntentBuilder(this);
        intentBuilder.previewPhotos((ArrayList) this.photoFragment.getFilePaths());
        intentBuilder.saveImgDir(null);
        intentBuilder.currentPosition(i);
        startActivity(intentBuilder.build());
    }

    public class AlbumAdapter extends FragmentPagerAdapter {
        @Override // androidx.viewpager.widget.PagerAdapter
        public CharSequence getPageTitle(int i) {
            return "";
        }

        public AlbumAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return AlbumActivity.this.fragmentLists.size();
        }

        @Override // androidx.fragment.app.FragmentPagerAdapter
        public Fragment getItem(int i) {
            return (Fragment) AlbumActivity.this.fragmentLists.get(i);
        }
    }
}