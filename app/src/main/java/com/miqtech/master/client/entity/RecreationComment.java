package com.miqtech.master.client.entity;

/**
 * Created by Administrator on 2015/11/30 0030.
 */
public class RecreationComment {
//    "content":"给个评论吧 ",
//            "id":1,
//            "createDate":"2015-11-26 10:30:44",
//            "icon":"uploads/imgs/user/2015/04/08/20150408095557_HKptODuPPIAre0JAqxBi.jpg",
//            "nickname":"0071","userId":2
    private String content ;
    private int id ;
    private String createDate;
    private String icon;
    private String nickname;
    private int userId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
