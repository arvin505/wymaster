package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.BuyRecordAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.BuyRecordData;
import com.miqtech.master.client.entity.ShopDetailInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.view.MyHasErrorListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 商城下的购买记录
 * Created by Administrator on 2016/3/8.
 */
public class FragmentShopBuyRecord extends MyBaseFragment {

    @Bind(R.id.fragment_comment_mylistview)
    MyHasErrorListView myListView;
    private Context context;
    private BuyRecordAdapter adapter;
    private View footerView;
    RelativeLayout llFooterView;
    private ArrayList<BuyRecordData> buyRecords=new ArrayList<BuyRecordData>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lengthCoding = UMengStatisticsUtil.CODE_2005;
        ArrayList<BuyRecordData> datas=getArguments().getParcelableArrayList("data");
        if(datas!=null && !datas.isEmpty()){
            buyRecords.addAll(datas);
        }
      LogUtil.d("FragmentShopBuyRecord","FragmentShopBuyRecord"+buyRecords.size());
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buy_record, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getContext();
        lengthCoding = UMengStatisticsUtil.CODE_2003;
        init();
    }
    /**
     * 初始化
     */
    private void init() {
        myListView.setErrorView("该网吧暂时有点低调哦~");
        myListView.setBackgroundColor(this.getResources().getColor(R.color.white));
        footerView = LayoutInflater.from(context).inflate(R.layout.layout_footer_view, null);
        llFooterView = (RelativeLayout) footerView.findViewById(R.id.footerView);
        llFooterView.setVisibility(View.GONE);
        myListView.addFooterView(footerView);
        adapter=new BuyRecordAdapter(context,buyRecords);
        myListView.setAdapter(adapter);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);

    }

    public  void setData(ArrayList<BuyRecordData> datas,int page) {
            if(page==1){
                buyRecords.clear();
                buyRecords.addAll(datas);
                myListView.setAdapter(adapter);
            }else{
                buyRecords.addAll(datas);
                adapter.notifyDataSetChanged();
            }
            if (llFooterView != null) {
                    llFooterView.setVisibility(View.GONE);
            }
    }


    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);

    }

    @Override
    public void refreView() {

    }
    public void showFooter(){
        if (llFooterView != null) {
            llFooterView.setVisibility(View.VISIBLE);
        }
    }
    public void hideFooter(){
        if (llFooterView != null) {
            llFooterView.setVisibility(View.GONE);
        }
    }
}
