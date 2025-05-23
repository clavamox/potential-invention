package com.wy.bean;

/* loaded from: classes.dex */
public class TutorialsStep2Mode {
    private String productDeail;
    private int productImage;
    private String productName;

    public TutorialsStep2Mode(String str, String str2, int i) {
        this.productName = str;
        this.productDeail = str2;
        this.productImage = i;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String str) {
        this.productName = str;
    }

    public String getProductDeail() {
        return this.productDeail;
    }

    public void setProductDeail(String str) {
        this.productDeail = str;
    }

    public int getProductImage() {
        return this.productImage;
    }

    public void setProductImage(int i) {
        this.productImage = i;
    }
}