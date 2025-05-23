package com.wy.ui.activity;

import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.xzf.camera.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/* loaded from: classes.dex */
public class PrivacyPolicy extends BaseActivity {

    @BindView(R.id.iv_nav_left)
    ImageView ivNavLeft;

    @BindView(R.id.tv_nav_title)
    TextView tvNavTitle;
    private TextView textview = null;
    private InputStream inputstream = null;

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_privacy_policy);
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        ButterKnife.bind(this);
        setStatusBarAndNavigation();
        this.ivNavLeft.setOnClickListener(new View.OnClickListener() { // from class: com.wy.ui.activity.PrivacyPolicy$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PrivacyPolicy.this.m79lambda$initView$0$comwyuiactivityPrivacyPolicy(view);
            }
        });
        this.tvNavTitle.setText(getString(R.string.privacy_policy));
        this.inputstream = getResources().openRawResource(R.raw.declaration);
        TextView textView = (TextView) findViewById(R.id.help_policy_textview);
        this.textview = textView;
        try {
            textView.setText(inputStreamToString(this.inputstream));
            this.textview.setMovementMethod(ScrollingMovementMethod.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* renamed from: lambda$initView$0$com-wy-ui-activity-PrivacyPolicy, reason: not valid java name */
    /* synthetic */ void m79lambda$initView$0$comwyuiactivityPrivacyPolicy(View view) {
        finish();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n\r");
            } else {
                bufferedReader.close();
                inputStream.close();
                return stringBuffer.toString();
            }
        }
    }
}