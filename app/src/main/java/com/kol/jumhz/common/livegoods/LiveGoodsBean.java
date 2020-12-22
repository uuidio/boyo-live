package com.kol.jumhz.common.livegoods;

import java.io.Serializable;

/**
 * @ClassName: LiveGoodsBean
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:02
 * @Description: 直播商品类
 */
public class LiveGoodsBean implements Serializable {

    private int id;
    private String image;
    private String title;
    private String price;
    private int num;
    private boolean isChecked;
    public LiveGoodsBean(int id, String image, String title, String price , int num, boolean isChecked) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.price = price;
        this.num = num;
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
