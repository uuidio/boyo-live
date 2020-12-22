package com.kol.jumhz.common.assistant;

/**
 * @ClassName: LotteryBean
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:01
 * @Description: 抽奖类
 */
public class LotteryBean {

    String name;
    int id;
    String info;
    String time;

    public LotteryBean(String name, int id, String info, String time) {
        this.name = name;
        this.id = id;
        this.info = info;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
