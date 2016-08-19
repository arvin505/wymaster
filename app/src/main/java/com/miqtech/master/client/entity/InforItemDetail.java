package com.miqtech.master.client.entity;

/**
 * 资讯列表、专题列表--item
 * Created by zhaosentao on 2015/11/26.
 */
public class InforItemDetail {
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


    private String title;
    private String icon;
    private String brief;
    private int type;
    private String imgs;
    private int read_num;
    private String time;
    private String keyword;

    private int faved;
    private String id;
    private String remark;
    private String create_date;
    private int praised;
    private int favNum;
    private int praiseNum;
    private String timer_date;//资讯时间
    private String introduces;//图集资讯每张图片下的文字介绍,用|||隔开
    private int comments_num;
    private String cover;
    private String video_url;
    private String source;
    

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public int getComments_num() {
        return comments_num;
    }

    public void setComments_num(int comments_num) {
        this.comments_num = comments_num;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBrief() {
        return brief;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public int getRead_num() {
        return read_num;
    }

    public void setRead_num(int read_num) {
        this.read_num = read_num;
    }

    public int getFaved() {
        return faved;
    }

    public void setFaved(int faved) {
        this.faved = faved;
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

    public String getTimer_date() {
        return timer_date;
    }

    public void setTimer_date(String timer_date) {
        this.timer_date = timer_date;
    }

    public String getIntroduces() {
        return introduces;
    }

    public void setIntroduces(String introduces) {
        this.introduces = introduces;
    }

    //    type	类型:1图文 2专题 3图集
    public static final int IMAGE_TYPE = 1;
    public static final int TOPIC_TYPE = 2;
    public static final int ATHLETICS_TYPE = 3;


    @Override
    public String toString() {
        return "InforItemDetail{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", icon='" + icon + '\'' +
                ", brief='" + brief + '\'' +
                ", type=" + type +
                ", imgs='" + imgs + '\'' +
                ", read_num=" + read_num +
                ", time='" + time + '\'' +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}
