package com.miqtech.master.client.entity;

/**
 * 我的消息里的评论
 * Created by Administrator on 2016/1/18.
 */
public class Mycomment {

//    "id": 138,
//            "icon": "uploads/imgs/user/random/35.jpg",
//            "create_date": "2016-01-12 19:12:11",
//            "nickname": "138****3466",
//            "reply_nickname": "138****3466",
//            "my_content": "uhuh",
//            "user_id": 122946,
//            "parent_id": 124

//    id	消息id
//    icon	用户头像
//    create_date	时间

//    parent_id	跳转至评论详情页的id
//    user_id	用户id
//activity_id	赛事id
//    activity_type	赛事类型1娱乐赛2官方赛
//    type	0系统消息 1红包消息 2会员消息 3预定消息 4支付消息 5赛事消息 6约战消息7娱乐赛提示消息8评论点赞消息9评论消息
//    is_read	=0未读>0已读
//obj_id	跳转详情时使用的对象id 默认值是0


//comment_id	上面的评论id
//content	上面的评论
//    nickname	上面评论的昵称
//    reply_nickname	下面评论的昵称
//    my_content	下面的评论


    private String id;
    private String icon;
    private String create_date;
    private String nickname;
    private String reply_nickname;
    private String my_content;
    private int user_id;
    private String parent_id;
    private int activity_id;
    private int type;
    private int activity_type;
    private int is_read;
    private int obj_id;
    private int comment_id;
    private String content;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getReply_nickname() {
        return reply_nickname;
    }

    public void setReply_nickname(String reply_nickname) {
        this.reply_nickname = reply_nickname;
    }

    public String getMy_content() {
        return my_content;
    }

    public void setMy_content(String my_content) {
        this.my_content = my_content;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(int activity_type) {
        this.activity_type = activity_type;
    }


    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public int getObj_id() {
        return obj_id;
    }

    public void setObj_id(int obj_id) {
        this.obj_id = obj_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
