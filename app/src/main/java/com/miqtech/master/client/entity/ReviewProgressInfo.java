package com.miqtech.master.client.entity;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/7.
 */
public class ReviewProgressInfo {

    private ArrayList<AppealCategory> appeal_category = new ArrayList<>();   //申述状态

    private List<Img> verify_imgs = new ArrayList<>();              //审核图片

    private String verify_describes;            //审核描述

    private int appeal_state;                   //申述状态描述  0不能申诉1可以认证申诉2可以非认证申诉

    private List<State> states = new ArrayList<>();                 //审核状态

    private String appeal_describes;            //申述描述

    private List<Img> appeal_imgs = new ArrayList<>();              //申述图片

    public ArrayList<AppealCategory> getAppeal_category() {
        return appeal_category;
    }

    public void setAppeal_category(ArrayList<AppealCategory> appeal_category) {
        this.appeal_category = appeal_category;
    }

    public List<Img> getVerify_imgs() {
        return verify_imgs;
    }

    public void setVerify_imgs(List<Img> verify_imgs) {
        this.verify_imgs = verify_imgs;
    }

    public String getVerify_describes() {
        return verify_describes;
    }

    public void setVerify_describes(String verify_describes) {
        this.verify_describes = verify_describes;
    }

    public int getAppeal_state() {
        return appeal_state;
    }

    public void setAppeal_state(int appeal_state) {
        this.appeal_state = appeal_state;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public String getAppeal_describes() {
        return appeal_describes;
    }

    public void setAppeal_describes(String appeal_describes) {
        this.appeal_describes = appeal_describes;
    }

    public List<Img> getAppeal_imgs() {
        return appeal_imgs;
    }

    public void setAppeal_imgs(List<Img> appeal_imgs) {
        this.appeal_imgs = appeal_imgs;
    }

    /**
     * 审核状态
     */
    public class State {
        String create_date;
        int state;
        String msg;
        String remark;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        String date;

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getCreate_date() {
            return create_date;
        }

        public void setCreate_date(String create_date) {
            this.create_date = create_date;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }
    }


    /**
     * 图片
     */
    public class Img {
        String img;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }
}


