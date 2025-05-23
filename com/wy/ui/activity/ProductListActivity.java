package com.wy.ui.activity;

import android.util.Log;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import com.wy.CameraApplication;
import com.wy.ui.MarginDecoration;
import com.wy.ui.adapter.ProductListAdapter;
import com.xzf.camera.R;
import java.util.Locale;

/* loaded from: classes.dex */
public class ProductListActivity extends BaseActivity {

    @BindView(R.id.rv_productlist)
    RecyclerView recyclerView;

    @Override // com.wy.ui.activity.BaseActivity
    void setContentView() {
        setContentView(R.layout.activity_product_list);
    }

    @Override // android.app.Activity
    public void recreate() {
        super.recreate();
        loadData();
    }

    @Override // com.wy.ui.activity.BaseActivity
    protected void initView() {
        super.initView();
        this.tvNavTitle.setText(R.string.select_product);
        loadData();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        Log.i("zwn", "列表进入前台 called." + CameraApplication.back_to_main);
        CameraApplication.this_device = 100;
        if (CameraApplication.back_to_main) {
            CameraApplication.back_to_main = false;
            finish();
        }
        super.onResume();
    }

    private void loadData() {
        String[] strArr = new String[4];
        String language = getResources().getConfiguration().locale.getLanguage();
        Log.e("zxy", "tjCountry: language:" + language + ",local:" + Locale.getDefault().toString() + ",country:" + getResources().getConfiguration().locale.getCountry());
        if (language.toLowerCase().contains("en")) {
            strArr[0] = "Blackhead";
            strArr[1] = "Earpick";
            strArr[2] = "Mouth";
            strArr[3] = "Tooth";
        } else if (language.toLowerCase().contains("zh")) {
            strArr[0] = "黑头仪";
            strArr[1] = "采耳棒";
            strArr[2] = "口腔仪";
            strArr[3] = "洁牙仪";
        } else {
            strArr[0] = "Blackhead";
            strArr[1] = "Earpick";
            strArr[2] = "Mouth";
            strArr[3] = "Tooth";
        }
        int[][] iArr = {new int[]{R.mipmap.a300a, R.mipmap.a_300, R.mipmap.a_301}, new int[]{R.mipmap.b_600}, new int[]{R.mipmap.b_800}, new int[]{R.mipmap.b_700}};
        this.recyclerView.addItemDecoration(new MarginDecoration(this));
        this.recyclerView.setHasFixedSize(true);
        final ProductListAdapter productListAdapter = new ProductListAdapter(strArr, iArr, new ProductListAdapter.OnClickProductItemListener() { // from class: com.wy.ui.activity.ProductListActivity.1
            /* JADX WARN: Removed duplicated region for block: B:14:0x006e  */
            /* JADX WARN: Removed duplicated region for block: B:20:0x0090  */
            /* JADX WARN: Removed duplicated region for block: B:25:0x00b1  */
            /* JADX WARN: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
            /* JADX WARN: Removed duplicated region for block: B:8:0x004c  */
            @Override // com.wy.ui.adapter.ProductListAdapter.OnClickProductItemListener
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void onClick(int r7, int r8) {
                /*
                    r6 = this;
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder
                    java.lang.String r1 = "选择的是:"
                    r0.<init>(r1)
                    java.lang.StringBuilder r0 = r0.append(r7)
                    java.lang.String r1 = ","
                    java.lang.StringBuilder r0 = r0.append(r1)
                    java.lang.String r1 = com.wy.CameraApplication.Wifiname
                    java.lang.StringBuilder r0 = r0.append(r1)
                    java.lang.String r0 = r0.toString()
                    java.lang.String r1 = "zwn"
                    android.util.Log.e(r1, r0)
                    com.wy.CameraApplication.this_device = r7
                    r0 = 2131755056(0x7f100030, float:1.914098E38)
                    r1 = 1
                    r2 = 0
                    if (r7 != 0) goto L49
                    java.lang.String r3 = com.wy.CameraApplication.Wifiname
                    java.lang.String r4 = "A30"
                    boolean r3 = r3.contains(r4)
                    if (r3 != 0) goto L47
                    com.wy.ui.activity.ProductListActivity r3 = com.wy.ui.activity.ProductListActivity.this
                    android.content.Context r3 = r3.getApplicationContext()
                    com.wy.ui.activity.ProductListActivity r4 = com.wy.ui.activity.ProductListActivity.this
                    java.lang.String r4 = r4.getString(r0)
                    android.widget.Toast r3 = android.widget.Toast.makeText(r3, r4, r2)
                    r3.show()
                    goto L49
                L47:
                    r3 = r1
                    goto L4a
                L49:
                    r3 = r2
                L4a:
                    if (r7 != r1) goto L6b
                    java.lang.String r4 = com.wy.CameraApplication.Wifiname
                    java.lang.String r5 = "B60"
                    boolean r4 = r4.contains(r5)
                    if (r4 != 0) goto L6a
                    com.wy.ui.activity.ProductListActivity r4 = com.wy.ui.activity.ProductListActivity.this
                    android.content.Context r4 = r4.getApplicationContext()
                    com.wy.ui.activity.ProductListActivity r5 = com.wy.ui.activity.ProductListActivity.this
                    java.lang.String r5 = r5.getString(r0)
                    android.widget.Toast r4 = android.widget.Toast.makeText(r4, r5, r2)
                    r4.show()
                    goto L6b
                L6a:
                    r3 = r1
                L6b:
                    r4 = 2
                    if (r7 != r4) goto L8d
                    java.lang.String r4 = com.wy.CameraApplication.Wifiname
                    java.lang.String r5 = "B80"
                    boolean r4 = r4.contains(r5)
                    if (r4 != 0) goto L8c
                    com.wy.ui.activity.ProductListActivity r4 = com.wy.ui.activity.ProductListActivity.this
                    android.content.Context r4 = r4.getApplicationContext()
                    com.wy.ui.activity.ProductListActivity r5 = com.wy.ui.activity.ProductListActivity.this
                    java.lang.String r5 = r5.getString(r0)
                    android.widget.Toast r4 = android.widget.Toast.makeText(r4, r5, r2)
                    r4.show()
                    goto L8d
                L8c:
                    r3 = r1
                L8d:
                    r4 = 3
                    if (r7 != r4) goto Laf
                    java.lang.String r4 = com.wy.CameraApplication.Wifiname
                    java.lang.String r5 = "B70"
                    boolean r4 = r4.contains(r5)
                    if (r4 != 0) goto Lae
                    com.wy.ui.activity.ProductListActivity r4 = com.wy.ui.activity.ProductListActivity.this
                    android.content.Context r4 = r4.getApplicationContext()
                    com.wy.ui.activity.ProductListActivity r5 = com.wy.ui.activity.ProductListActivity.this
                    java.lang.String r0 = r5.getString(r0)
                    android.widget.Toast r0 = android.widget.Toast.makeText(r4, r0, r2)
                    r0.show()
                    goto Laf
                Lae:
                    r3 = r1
                Laf:
                    if (r3 != r1) goto Lc9
                    android.content.Intent r0 = new android.content.Intent
                    com.wy.ui.activity.ProductListActivity r1 = com.wy.ui.activity.ProductListActivity.this
                    java.lang.Class<com.wy.ui.activity.CameraActivity> r2 = com.wy.ui.activity.CameraActivity.class
                    r0.<init>(r1, r2)
                    java.lang.String r1 = "productTypeIndex"
                    r0.putExtra(r1, r7)
                    java.lang.String r7 = "productItem"
                    r0.putExtra(r7, r8)
                    com.wy.ui.activity.ProductListActivity r7 = com.wy.ui.activity.ProductListActivity.this
                    r7.startActivity(r0)
                Lc9:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.wy.ui.activity.ProductListActivity.AnonymousClass1.onClick(int, int):void");
            }
        });
        final GridLayoutManager gridLayoutManager = (GridLayoutManager) this.recyclerView.getLayoutManager();
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: com.wy.ui.activity.ProductListActivity.2
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int i) {
                if (productListAdapter.isHeader(i)) {
                    return gridLayoutManager.getSpanCount();
                }
                return 1;
            }
        });
        this.recyclerView.setAdapter(productListAdapter);
    }
}