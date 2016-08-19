package com.miqtech.master.client.entity;

/**
 * Created by Administrator on 2015/12/2.
 */
public class AtlasDetail {

    //    faved	是否收藏：0-否;1-是;
//    id	资讯ID
//    title	资讯标题
//    read_num	阅读数
//    brief	简介
//    read_num	阅读数
//    remark	资讯内容
//    create_date	创建时间
//    imgs	图集图片
//    type	类型:1图文 2专题 3图集
//    praised	是否点赞 1点赞 0未点赞
//    favNum	收藏数
//    praiseNum	点赞数

    private int faved;
    private String id;
    private String title;
    private int read_num;
    private String brief;
    private String remark;
    private String create_date;
    private String imgs;
    private int praised;
    private int favNum;
    private int praiseNum;
    private int comments_num;

    public int getComments_num() {
        return comments_num;
    }

    public void setComments_num(int comments_num) {
        this.comments_num = comments_num;
    }

    private int type;
    private String introduces;//图集资讯每张图片下的文字介绍,用|||隔开

    public int getFaved() {
        return faved;
    }

    public void setFaved(int faved) {
        this.faved = faved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRead_num() {
        return read_num;
    }

    public void setRead_num(int read_num) {
        this.read_num = read_num;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public int getPraised() {
        return praised;
    }

    public void setPraised(int praised) {
        this.praised = praised;
    }

    public int getFavNum() {
        return favNum;
    }

    public void setFavNum(int favNum) {
        this.favNum = favNum;
    }

    public int getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIntroduces() {
        return introduces;
    }

    public void setIntroduces(String introduces) {
        this.introduces = introduces;
    }
}
