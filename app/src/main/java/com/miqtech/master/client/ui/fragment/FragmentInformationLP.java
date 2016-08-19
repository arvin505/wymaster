package com.miqtech.master.client.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.CommodityInfo;
import com.miqtech.master.client.entity.LiveRoomAnchorInfo;
import com.miqtech.master.client.entity.LiveRoomInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.LiveRoomActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/7/26.
 */
public class FragmentInformationLP extends BaseFragment implements View.OnClickListener{

    @Bind(R.id.anchorHeader)
    CircleImageView anchorHeader;//主播头像
    @Bind(R.id.anchorTitle)
    TextView anchorTitle;  //主播标题
    @Bind(R.id.fansNum)
    TextView fansNum ;// 粉丝数量
    @Bind(R.id.tvAttention)
    TextView tvAttention; //关注按钮
    @Bind(R.id.anchorContent)
    TextView anchorContent; //主播内容
    private int id;//主播id
    private LiveRoomAnchorInfo info;//主播信息

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information_lp,container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }
    public void setAnchorData(LiveRoomAnchorInfo info){
        setData(info);
    }
    private void setData(LiveRoomAnchorInfo info) {
        this.info=info;
        this.id=info.getUpUserId();
        AsyncImage.loadAvatar(getActivity(), HttpConstant.SERVICE_UPLOAD_AREA + info.getIcon(), anchorHeader);
        anchorTitle.setText(info.getNickname());
        setFontDiffrentColor(getString(R.string.live_play_fans_num, Utils.calculate(info.getFans(),10000,"W")),3,getString(R.string.live_play_fans_num, Utils.calculate(info.getFans(),10000,"W")).length(),fansNum);
        if(!TextUtils.isEmpty(info.getIntroduce())){
            anchorContent.setText(info.getIntroduce());
        }else {
            anchorContent.setVisibility(View.GONE);
        }
        setSubscribeState(info.getIsSubscibe()==1?true:false);
        tvAttention.setOnClickListener(this);

    }
    public void setSubscribeState(boolean isSubscribe){
        GradientDrawable bgShape=(GradientDrawable)tvAttention.getBackground();
        if(isSubscribe){
            tvAttention.setText(getResources().getString(R.string.live_room_attentioned));
            tvAttention.setTextColor(getContext().getResources().getColor(R.color.shop_buy_record_gray));
            bgShape.setStroke(Utils.dp2px(1),getContext().getResources().getColor(R.color.shop_buy_record_gray));
        }else{
            tvAttention.setText(getResources().getString(R.string.live_room_attention));
            tvAttention.setTextColor(getContext().getResources().getColor(R.color.light_orange));
            bgShape.setStroke(Utils.dp2px(1),getContext().getResources().getColor(R.color.orange));
        }
    }
    private void setFontDiffrentColor(String content, int start, int end, TextView tv) {
        if (tv == null) {
            return;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.orange)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(style);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvAttention:
                getAttentionRequest();
                break;
        }
    }
    private void getAttentionRequest(){
        showLoading();
        Map<String, String> params = new HashMap();
        if(WangYuApplication.getUser(getActivity())!=null){
            params.put("userId",WangYuApplication.getUser(getActivity()).getId()+"");
            params.put("token",WangYuApplication.getUser(getActivity()).getToken());
        }
        params.put("upUserId",id+"");
        //TODO 传递数据

        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LIVE_SUBSCRIBE, params, HttpConstant.LIVE_SUBSCRIBE);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        if (HttpConstant.LIVE_SUBSCRIBE.equals(method)) {
            try {
                if(object.getInt("code")==0 && "success".equals(object.getString("result"))){
                    info.setIsSubscibe(info.getIsSubscibe()==1?0:1);
                    ((LiveRoomActivity)getActivity()).updataSubscribeState(info.getIsSubscibe()==1?true:false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
    }
}
