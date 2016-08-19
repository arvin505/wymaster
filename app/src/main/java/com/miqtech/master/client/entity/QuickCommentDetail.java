package com.miqtech.master.client.entity;

/**
 * Created by zhaosentao on 2016/7/29.
 */
public class QuickCommentDetail {

//    comment	评论内容
//    id	快速评论id
//    img	图片

    private String comment;
    private String id;
    private String img;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
