package com.kol.jumhz.common.assistant;
/**
 * @ClassName: BannedBean
 * @Author: Dzy
 * @CreateDate: 2020-04-21 19:22
 * @Description: 禁言类
 */
public class BannedBean {

    private int id;
    private int user_id;
    private int live_id;
    private String accid;
    private String roomid;
    private String created_at;
    private String updated_at;
    private String nickname;
    private String headimgurl;
    public BannedBean(int id, int user_id, int live_id, String accid, String roomid , String created_at,
                      String updated_at, String nickname, String headimgurl) {
        this.id = id;
        this.user_id = user_id;
        this.live_id = live_id;
        this.accid = accid;
        this.roomid = roomid;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.nickname = nickname;
        this.headimgurl = headimgurl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getLive_id() {
        return live_id;
    }

    public void setLive_id(int live_id) {
        this.live_id = live_id;
    }

    public String getAccid() {
        return accid;
    }

    public void setAccid(String accid) {
        this.accid = accid;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }
}


