package com.wy.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import com.wy.ui.MarginDecoration;
import com.wy.ui.adapter.HelpListAdapter;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class HelpActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_help);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        this.tvNavTitle.setText("");
        this.ivNavMenu.setVisibility(8);
        this.recyclerView.addItemDecoration(new MarginDecoration(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(new HelpListAdapter(new String[]{getString(R.string.product_album), getString(R.string.language), getString(R.string.soft_ver)}, new int[]{R.mipmap.ablum, R.mipmap.language, R.mipmap.soft_ver}, new HelpListAdapter.OnClickHelpListListener() { // from class: com.wy.ui.activity.HelpActivity.1
            @Override // com.wy.ui.adapter.HelpListAdapter.OnClickHelpListListener
            public void onClick(int i) {
                if (i == 0) {
                    HelpActivity.this.startActivity(new Intent(HelpActivity.this, (Class<?>) AlbumActivity.class));
                    HelpActivity.this.finish();
                } else {
                    if (i == 1) {
                        HelpActivity.this.startActivity(new Intent(HelpActivity.this, (Class<?>) LanguageActivity.class));
                        return;
                    }
                    if (i == 2) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setData(Uri.parse("http://wonshan.cn/ss/all_link_hty.html"));
                        HelpActivity.this.startActivity(intent);
                        Log.d("HelpActivity", "APP Ver:");
                    }
                }
            }
        }));
    }
}