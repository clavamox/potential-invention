package cn.bingoogolapple.photopicker.adapter;

import android.graphics.ColorFilter;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.model.BGAPhotoFolderModel;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class BGAPhotoPickerAdapter extends BGARecyclerViewAdapter<String> {
    private int mPhotoSize;
    private ArrayList<String> mSelectedPhotos;
    private boolean mTakePhotoEnabled;

    public BGAPhotoPickerAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.bga_pp_item_photo_picker);
        this.mSelectedPhotos = new ArrayList<>();
        this.mPhotoSize = BGAPhotoPickerUtil.getScreenWidth() / 6;
    }

    @Override // cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        if (this.mTakePhotoEnabled && i == 0) {
            return R.layout.bga_pp_item_photo_camera;
        }
        return R.layout.bga_pp_item_photo_picker;
    }

    @Override // cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
    public void setItemChildListener(BGAViewHolderHelper bGAViewHolderHelper, int i) {
        if (i == R.layout.bga_pp_item_photo_camera) {
            bGAViewHolderHelper.setItemChildClickListener(R.id.iv_item_photo_camera_camera);
        } else {
            bGAViewHolderHelper.setItemChildClickListener(R.id.iv_item_photo_picker_flag);
            bGAViewHolderHelper.setItemChildClickListener(R.id.iv_item_photo_picker_photo);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
    public void fillData(BGAViewHolderHelper bGAViewHolderHelper, int i, String str) {
        if (getItemViewType(i) == R.layout.bga_pp_item_photo_picker) {
            BGAImage.display(bGAViewHolderHelper.getImageView(R.id.iv_item_photo_picker_photo), R.mipmap.bga_pp_ic_holder_dark, str, this.mPhotoSize);
            if (this.mSelectedPhotos.contains(str)) {
                bGAViewHolderHelper.setImageResource(R.id.iv_item_photo_picker_flag, R.mipmap.bga_pp_ic_cb_checked);
                bGAViewHolderHelper.getImageView(R.id.iv_item_photo_picker_photo).setColorFilter(bGAViewHolderHelper.getConvertView().getResources().getColor(R.color.bga_pp_photo_selected_mask));
            } else {
                bGAViewHolderHelper.setImageResource(R.id.iv_item_photo_picker_flag, R.mipmap.bga_pp_ic_cb_normal);
                bGAViewHolderHelper.getImageView(R.id.iv_item_photo_picker_photo).setColorFilter((ColorFilter) null);
            }
        }
    }

    public void setSelectedPhotos(ArrayList<String> arrayList) {
        if (arrayList != null) {
            this.mSelectedPhotos = arrayList;
        }
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedPhotos() {
        return this.mSelectedPhotos;
    }

    public int getSelectedCount() {
        return this.mSelectedPhotos.size();
    }

    public void setPhotoFolderModel(BGAPhotoFolderModel bGAPhotoFolderModel) {
        this.mTakePhotoEnabled = bGAPhotoFolderModel.isTakePhotoEnabled();
        setData(bGAPhotoFolderModel.getPhotos());
    }
}