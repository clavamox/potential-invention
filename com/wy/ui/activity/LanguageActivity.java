package com.wy.ui.activity;

import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import com.wy.language.Config;
import com.wy.language.Store;
import com.wy.ui.MarginDecoration;
import com.wy.ui.adapter.LanguageAdapter;
import com.xzf.camera.R;
import java.util.Locale;

/* loaded from: classes.dex */
public class LanguageActivity extends BaseActivity {
    private static final String TAG = "LanguageActivity";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_language);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        this.tvNavTitle.setText(R.string.language);
        this.ivNavMenu.setVisibility(8);
        this.recyclerView.addItemDecoration(new MarginDecoration(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(new LanguageAdapter(new String[]{getString(R.string.language_chinese), getString(R.string.language_english)}, new int[]{R.mipmap.ic_language_china, R.mipmap.ic_language_england}, new LanguageAdapter.OnClickLanguageListener() { // from class: com.wy.ui.activity.LanguageActivity.1
            @Override // com.wy.ui.adapter.LanguageAdapter.OnClickLanguageListener
            public void onClick(int i) {
                Log.d(LanguageActivity.TAG, "click:" + i);
                if (i == 0) {
                    LanguageActivity.this.saveLanguage(Locale.SIMPLIFIED_CHINESE.getLanguage() + "-" + Locale.SIMPLIFIED_CHINESE.getCountry());
                } else if (i == 1) {
                    LanguageActivity.this.saveLanguage(Locale.UK.getLanguage() + "-" + Locale.UK.getCountry());
                }
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveLanguage(String str) {
        Log.d(TAG, "language:" + str);
        Store.setLanguageLocal(this, str);
        Intent intent = new Intent(Config.ACTION);
        intent.putExtra(NotificationCompat.CATEGORY_MESSAGE, "EVENT_REFRESH_LANGUAGE");
        sendBroadcast(intent);
    }
}