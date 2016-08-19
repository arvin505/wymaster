package com.miqtech.master.client.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.CommodityInfo;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/7.
 */
public class FragmentShopBaseInfo extends MyBaseFragment{

    private int position;
    private CommodityInfo commodityInfo;
    @Bind(R.id.tvDetail)
    TextView tvDetail;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
        commodityInfo = (CommodityInfo)getArguments().getSerializable("commodity_info");
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_baseinfo,container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setData();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//       if(isVisibleToUser){
//
//       }
    }
    @Override
    public void refreView() {

    }
    private void setData() {
        if(commodityInfo!=null) {
            if (position == 0) {
             tvDetail.setText(commodityInfo.getIntroduce());
               if(tvDetail.getLineCount()==1){
                   tvDetail.setPadding(0,0,0, Utils.dp2px(22));
               }
            }else if(position==1){
            tvDetail.setText(commodityInfo.getRule());
            }
        }else{
            if(position==0) {
                tvDetail.setText("暂无介绍");
            }else if(position==1){
                tvDetail.setText("暂无规则");
            }
        }
    }
}
