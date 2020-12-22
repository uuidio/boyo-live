package com.kol.jumhz.common.playback;

/**
 * @ClassName: PlayBackBean
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:06
 * @Description: 历史回放类
 */
public class PlayBackBean {

    private int id;
    private int live_id;
    private String title;
    private String surface_img;
    private String playback;
    private String start_at;
    private String end_at;
    private String created_at;
    public PlayBackBean(int id, int live_id, String title, String surface_img , String playback,
                        String start_at, String end_at, String created_at) {
        this.id = id;
        this.live_id = live_id;
        this.title = title;
        this.surface_img = surface_img;
        this.playback = playback;
        this.start_at = start_at;
        this.end_at = end_at;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLive_id() {
        return live_id;
    }

    public void setLive_id(int live_id) {
        this.live_id = live_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSurface_img() {
        return surface_img;
    }

    public void setSurface_img(String surface_img) {
        this.surface_img = surface_img;
    }

    public String getPlayback() {
        return playback;
    }

    public void setPlayback(String playback) {
        this.playback = playback;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public String getEnd_at() {
        return end_at;
    }

    public void setEnd_at(String end_at) {
        this.end_at = end_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}


