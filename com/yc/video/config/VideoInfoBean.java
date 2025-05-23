package com.yc.video.config;

import java.io.Serializable;
import java.util.Map;

/* loaded from: classes.dex */
public class VideoInfoBean implements Serializable {
    private String cover;
    private String grade;
    private Map<String, String> headers;
    private long length;
    private String p;
    private String title;
    private String videoUrl;

    public VideoInfoBean(String str, String str2, String str3) {
        this.title = str;
        this.videoUrl = str3;
        this.cover = str2;
    }

    public VideoInfoBean(String str, String str2, String str3, String str4) {
        this.title = str;
        this.grade = str2;
        this.p = str3;
        this.videoUrl = str4;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String str) {
        this.videoUrl = str;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, String> map) {
        this.headers = map;
    }

    public String getCover() {
        return this.cover;
    }

    public void setCover(String str) {
        this.cover = str;
    }

    public long getLength() {
        return this.length;
    }

    public void setLength(long j) {
        this.length = j;
    }

    public String getGrade() {
        return this.grade;
    }

    public void setGrade(String str) {
        this.grade = str;
    }

    public String getP() {
        return this.p;
    }

    public void setP(String str) {
        this.p = str;
    }
}