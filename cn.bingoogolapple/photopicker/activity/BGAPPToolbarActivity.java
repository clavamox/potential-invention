package cn.bingoogolapple.photopicker.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ViewStubCompat;
import cn.bingoogolapple.photopicker.R;

/* loaded from: classes.dex */
public abstract class BGAPPToolbarActivity extends AppCompatActivity {
    protected String TAG;
    protected Toolbar mToolbar;

    protected abstract void initView(Bundle bundle);

    protected abstract void processLogic(Bundle bundle);

    protected abstract void setListener();

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.TAG = getClass().getSimpleName();
        initView(bundle);
        setListener();
        processLogic(bundle);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity
    public void setContentView(int i) {
        super.setContentView(R.layout.bga_pp_toolbar_viewstub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mToolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewStubCompat viewStubCompat = (ViewStubCompat) findViewById(R.id.viewStub);
        ((RelativeLayout.LayoutParams) viewStubCompat.getLayoutParams()).addRule(3, R.id.toolbar);
        viewStubCompat.setLayoutResource(i);
        viewStubCompat.inflate();
    }

    public void setNoLinearContentView(int i) {
        super.setContentView(R.layout.bga_pp_toolbar_viewstub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mToolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewStubCompat viewStubCompat = (ViewStubCompat) findViewById(R.id.viewStub);
        viewStubCompat.setLayoutResource(i);
        viewStubCompat.inflate();
    }

    @Override // android.app.Activity
    public void setTitle(CharSequence charSequence) {
        getSupportActionBar().setTitle(charSequence);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}