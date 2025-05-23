package com.wy.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import com.bumptech.glide.Glide;
import com.xzf.camera.R;
import java.io.File;

/* loaded from: classes.dex */
public class PicturePreviewActivtiy extends BaseActivity implements View.OnClickListener {
    private String filePath = "";

    @BindView(R.id.imageview)
    ImageView imageView;

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_picture_preview_activtiy);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        this.ivNavMenu.setVisibility(4);
        this.tvNavTitle.setVisibility(4);
        this.filePath = getIntent().getStringExtra("filePath");
        Glide.with((FragmentActivity) this).load(this.filePath).into(this.imageView);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() != R.id.iv_delete) {
            return;
        }
        try {
            new File(this.filePath).delete();
            Toast.makeText(this, getString(R.string.opt_success), 0).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), 0).show();
        }
    }
}