package com.miqtech.master.client.entity;

/**
 * 楼中楼的  第二个楼的回复
 * Created by Administrator on 2016/1/7.
 */
public class SecondCommentDetail {
//    "content":"tttrrrrhaha",
//            "id":3,
//            "createDate":"2015-11-26 10:30:44",
//            "nickname":"hello kity",
//            "replyname":"0071",
//            "userId":3

//    content	是	string	评论内容
//    createDate	是	string	评论时间
//    nickname	是	string	用户昵称
//    userId	是	string	用户ID
//    id	是	string	评论ID
//    replyname	否	string	楼中楼中，被回复对象的昵称
//    replyUserId	否	string	楼中楼中，被回复对象的用户ID
//parent_id

    private String content;
    private String id;
    private String createDate;
    private String nickname;
    private String replyname;
    private int userId;
    private int replyUserId;

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    private String parent_id;
    private String reply_id;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getReplyname() {
        return replyname;
    }

    public void setReplyname(String replyname) {
        this.replyname = replyname;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(int replyUserId) {
        this.replyUserId = replyUserId;
    }



    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }
}
