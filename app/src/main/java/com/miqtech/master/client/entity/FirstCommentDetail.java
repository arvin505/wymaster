package com.miqtech.master.client.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 楼中楼的评论中的第一个楼
 * Created by Administrator on 2016/1/7.
 */
public class FirstCommentDetail {

//    "id":1,
//            "content":"给个评论吧 ",
//            "icon":"uploads/imgs/user/2015/04/08/20150408095557_HKptODuPPIAre0JAqxBi.jpg",
//            "replyList":[
//    {
//        "content":"tttrrrrhaha",
//            "id":3,
//            "createDate":"2015-11-26 10:30:44",
//            "nickname":"hello kity",
//            "replyname":"0071",
//            "userId":3
//    }
//    ],
//            "replyCount":1,
//            "likeCount":110,
//            "nickname":"0071",
//            "userId":2

//    id	是	string	评论ID
//    content	是	string	评论内容
//    createDate	是	string	评论时间
//    icon	是	string	用户图像url
//    nickname	是	string	用户昵称
//    userId	是	string	用户ID
//    likeCount	是	string	点赞数
//    replyList	否	list	楼中楼回复列表
//    replyname	否	string	楼中楼中，被回复对象的昵称
//    isPraise,0 否   1 是

    private String id;
    private String content;
    private String icon;
    private int replyCount;
    private int likeCount;
    private String nickname;
    private int userId;
    private String createDate;
    private int isPraise;
    private String reply_id;
    private List<SecondCommentDetail> replyList;

    private String img;//图片
    private String parent_id;//一级评论ID

    @SerializedName("bounty_hunter_flag")
    private int bountyHunterFlag;//赏金猎人标志1是0否

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
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


    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }


    public int getIsPraise() {
        return isPraise;
    }

    public void setIsPraise(int isPraise) {
        this.isPraise = isPraise;
    }

    public List<SecondCommentDetail> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<SecondCommentDetail> replyList) {
        this.replyList = replyList;
    }

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public int getBountyHunterFlag() {
        return bountyHunterFlag;
    }

    public void setBountyHunterFlag(int bountyHunterFlag) {
        this.bountyHunterFlag = bountyHunterFlag;
    }
}
