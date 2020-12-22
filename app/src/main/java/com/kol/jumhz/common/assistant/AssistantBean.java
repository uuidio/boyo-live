package com.kol.jumhz.common.assistant;

/**
 * @ClassName: AssistantBean
 * @Description: 助理类
 * @Author: Lanlnk
 * @CreateDate: 2020/3/30 15:56
 */
public class AssistantBean {

    String login_account;
    String img_url;
    String username;

    public AssistantBean(String login_account, String img_url, String username) {
        this.login_account = login_account;
        this.img_url = img_url;
        this.username = username;
    }

    public String getLogin_account() {
        return login_account;
    }

    public void setLogin_account(String login_account) {
        this.login_account = login_account;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
