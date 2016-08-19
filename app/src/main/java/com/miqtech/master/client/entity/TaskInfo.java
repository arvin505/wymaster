package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * 任务信息
 *
 * @author Administrator
 */
public class TaskInfo implements Parcelable {
//    "code": 0,
//    "result": "success",
//    "object": {
//        "dailyTasks": [
//            {
//                "all_accomplish": 0,
//                "accomplish_time": 0,
//                "name": "发起一场约战",
//                "icon": "uploads/imgs/mall/taskIcon/release_match.png",
//                "limit": 1,
//                "remark": "<p>1.在约战界面发起一场约战</p><p><img alt=\"yue.png\" src=\"http://img.wangyuhudong.com//uploads/UEditor/20150925/1f7ffd7ea1994c4183fba00146cb9209.png\" width=\"380\" height=\"41\"><br></p><p>2.记得在下方邀请小伙伴哟</p>",
//                "id": 3,
//                "text": "向小伙伴们发起一场约战",
//                "coin": 10
//            },
//            {
//                "all_accomplish": 0,
//                "accomplish_time": 0,
//                "name": "参加一场约战",
//                "icon": "uploads/imgs/mall/taskIcon/join_match.png",
//                "limit": 1,
//                "id": 4,
//                "text": "参加一场小伙伴的约战",
//                "coin": 10
//            },

//	teachingTasks 新手任务列表 
//	limit 每人每日的限额 
//	icon 图标 
//	text 描述文本 
//	accomplish_time 完成的次数（仅在userId及token正确才出现） 
//	all_accomplish 任务是否已完成（仅在userId及token正确才出现）：1-是，0-否 
//	coin 完成任务能获得的金币数 
//	name 任务名

    int all_accomplish;//任务是否已完成（仅在userId及token正确才出现）：1-是，0-否
    int accomplish_time;//完成的次数（仅在userId及token正确才出现）
    String name;//任务名
    String icon;
    int limit;//每人每日的限额
    int id;//
    String text;//描述文本
    int coin;//完成任务能获得的金币数
    String remark;//任务详情
    int isShowText = 0;//是否显示描述文本,0不显示，1显示

    public int getAll_accomplish() {
        return all_accomplish;
    }

    public void setAll_accomplish(int all_accomplish) {
        this.all_accomplish = all_accomplish;
    }

    public int getAccomplish_time() {
        return accomplish_time;
    }

    public void setAccomplish_time(int accomplish_time) {
        this.accomplish_time = accomplish_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public int getIsShowText() {
        return isShowText;
    }

    public void setIsShowText(int isShowText) {
        this.isShowText = isShowText;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
//		int all_accomplish;//任务是否已完成（仅在userId及token正确才出现）：1-是，0-否
//		int accomplish_time;//完成的次数（仅在userId及token正确才出现）
//		String name;//任务名 
//		String icon;
//		int limit;//每人每日的限额 
//		int id;//
//		String text;//描述文本
//		int coin;//完成任务能获得的金币数 
        arg0.writeInt(all_accomplish);
        arg0.writeInt(accomplish_time);
        arg0.writeString(name);
        arg0.writeString(icon);
        arg0.writeInt(limit);
        arg0.writeInt(id);
        arg0.writeString(text);
        arg0.writeInt(coin);
        arg0.writeString(remark);
        arg0.writeInt(isShowText);
    }

    public static final Creator<TaskInfo> CREATOR = new Creator<TaskInfo>() {
        public TaskInfo createFromParcel(Parcel source) {
            TaskInfo member = new TaskInfo();
            member.all_accomplish = source.readInt();
            member.accomplish_time = source.readInt();
            member.name = source.readString();
            member.icon = source.readString();
            member.limit = source.readInt();
            member.id = source.readInt();
            member.text = source.readString();
            member.coin = source.readInt();
            member.remark = source.readString();
            member.isShowText = source.readInt();
            return member;
        }

        public TaskInfo[] newArray(int size) {
            return new TaskInfo[size];
        }
    };

}
