package com.kol.jumhz.common.msg;

/**
 * @ClassName: SimpleUserInfo
 * @Author: LanLnk
 * @CreateDate: 2020-05-06 16:04
 * @Description: 用户基本信息封装
 */
public class SimpleUserInfo {

    public String userid;       // userId
    public String nickname;     // 昵称
    public String avatar;       // 头像链接

    public SimpleUserInfo(String userId, String nickname, String avatar) {
        this.userid = userId;
        this.nickname = nickname;
        this.avatar = avatar;
    }
}
