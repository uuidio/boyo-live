package com.kol.jumhz.common.liveforeshow;

import java.io.Serializable;

/**
 * @ClassName: LiveForeshowBean
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:02
 * @Description: 直播预告展示类
 */
public class LiveForeshowBean implements Serializable {

    private int id;
    private int liveId;
    private String image;
    private String title;
    private String info;
    private String date;
    private String goodsList;
    private String appImage;
    private String wechat_img;
    private String wechat_path;

    public LiveForeshowBean(int id, int liveId, String image, String title, String info, String date, String goodsList, String appImage) {
        this.id = id;
        this.liveId = liveId;
        this.image = image;
        this.title = title;
        this.info = info;
        this.date = date;
        this.goodsList = goodsList;
        this.appImage = appImage;
    }

    public LiveForeshowBean(int id, int liveId, String image, String title, String info, String date, String goodsList, String appImage, String wechat_img, String wechat_path) {
        this.id = id;
        this.liveId = liveId;
        this.image = image;
        this.title = title;
        this.info = info;
        this.date = date;
        this.goodsList = goodsList;
        this.appImage = appImage;
        this.wechat_img = wechat_img;
        this.wechat_path = wechat_path;
    }

    public String getWechat_path() {
        return wechat_path;
    }

    public void setWechat_path(String wechat_path) {
        this.wechat_path = wechat_path;
    }

    public String getWechat_img() {
        return wechat_img;
    }

    public void setWechat_img(String wechat_img) {
        this.wechat_img = wechat_img;
    }

    public int getLiveId() {
        return liveId;
    }

    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoodsList() {
        return goodsList;
    }
    public void setGoodsList(String goodsList) {
        this.goodsList = goodsList;
    }

    public String getAppImage() {
        return appImage;
    }

    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }
}
