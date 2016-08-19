package com.miqtech.master.client.entity;

import com.miqtech.master.client.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/4/14.
 */
public class Information {
    InforItemDetail info;
    List<InfoRecommend> recommend;
    InfoUpAndDown upDown;

    public void setRecommend(List<InfoRecommend> recommend) {
            this.recommend = recommend;

    }

    public InforItemDetail getInfo() {
        return info;
    }

    public void setInfo(InforItemDetail info) {
        this.info = info;
    }

    public List<InfoRecommend> getRecommend() {
        if(recommend==null) {
            return new ArrayList<InfoRecommend>();
        }else{
            return recommend;
        }
    }

    public InfoUpAndDown getUpDown() {
        return upDown;
    }

    public void setUpDown(InfoUpAndDown upDown) {
        this.upDown = upDown;
    }
}
