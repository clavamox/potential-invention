package com.wy.ui.activity;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.xzf.camera.R;

/* loaded from: classes.dex */
public class ProductListActivity_ViewBinding implements Unbinder {
    private ProductListActivity target;

    public ProductListActivity_ViewBinding(ProductListActivity productListActivity) {
        this(productListActivity, productListActivity.getWindow().getDecorView());
    }

    public ProductListActivity_ViewBinding(ProductListActivity productListActivity, View view) {
        this.target = productListActivity;
        productListActivity.recyclerView = (RecyclerView) Utils.findRequiredViewAsType(view, R.id.rv_productlist, "field 'recyclerView'", RecyclerView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        ProductListActivity productListActivity = this.target;
        if (productListActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        productListActivity.recyclerView = null;
    }
}