package com.yushan.customphotodemo;


import android.graphics.Bitmap;

import com.yushan.customphotodemo.entity.ImageEntity;

public class LoadImageTask {
    private String url;
    private AsyncImageLoaderImpl.ImageCallback callBack;
    private Integer defaultRes;
    private ImageEntity entity;

    public LoadImageTask(String url, Integer defaultRes, AsyncImageLoaderImpl.ImageCallback callBack) {
        this.url = url;
        this.callBack = callBack;
        this.defaultRes = defaultRes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AsyncImageLoaderImpl.ImageCallback getCallBack() {
        return callBack;
    }

    public void setCallBack(AsyncImageLoaderImpl.ImageCallback callBack) {
        this.callBack = callBack;
    }

    public Integer getDefaultRes() {
        return defaultRes;
    }

    public void setDefaultRes(Integer defaultRes) {
        this.defaultRes = defaultRes;
    }

    public void back(Bitmap bm) {
        callBack.imageLoaded(bm, url);
    }

    public ImageEntity getEntity() {
        return entity;
    }

    public void setEntity(ImageEntity entity) {
        url = entity.getUrl();
        this.entity = entity;
    }
}