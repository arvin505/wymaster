package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.RecreationMatchAppeal;
import com.miqtech.master.client.entity.RecreationMatchVerify;
import com.miqtech.master.client.entity.RecreationMatchVerifyImg;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/10 0010.
 */
public class RecreationMatchScheduleActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.ivScheduleUpload)
    ImageView ivScheduleUpload;
    @Bind(R.id.ivSchedule)
    ImageView ivSchedule;
    @Bind(R.id.ivScheduling)
    ImageView ivScheduling;
    @Bind(R.id.ivScheduleGift)
    ImageView ivScheduleGift;
    @Bind(R.id.tvScheduleUpload)
    TextView tvScheduleUpload;
    @Bind(R.id.tvSchedule)
    TextView tvSchedule;
    @Bind(R.id.tvScheduling)
    TextView tvScheduling;
    @Bind(R.id.tvScheduleGift)
    TextView tvScheduleGift;
    @Bind(R.id.tv1)
    TextView tv1;
    @Bind(R.id.tv2)
    TextView tv2;
    @Bind(R.id.tv3)
    TextView tv3;
    @Bind(R.id.tv4)
    TextView tv4;
    @Bind(R.id.tv5)
    TextView tv5;
    @Bind(R.id.tv6)
    TextView tv6;
    @Bind(R.id.tvContent)
    TextView tvContent;
    @Bind(R.id.llImgs )
    LinearLayout llContent;

    private Context context;
    private String id;
    private RecreationMatchAppeal recreationMatchAppeal;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_recreationmatchschedule);
        ButterKnife.bind(this);
        context = this;
        initView();

    }

    @Override
    protected void initView() {
        super.initView();
        setLeftBtnImage(R.drawable.back_bottom_icon);
        setLeftIncludeTitle("审核进度");
        getLeftBtn().setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void initData() {
        super.initData();
        id = getIntent().getStringExtra("id");
        loadSchedule();
    }

    private void loadSchedule() {
        HashMap params = new HashMap();
        params.put("activityId", id);
        params.put("userId", WangYuApplication.getUser(context).getId());
        params.put("token", WangYuApplication.getUser(context).getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_VERIFY_CHECK, params, HttpConstant.AMUSE_VERIFY_CHECK);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.e("tag", "object :" + object.toString());
        if (HttpConstant.AMUSE_VERIFY_CHECK.equals(method)) {
            updateViews(object);
        }
    }

    private void updateViews(JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                String strObj = object.getString("object");
                if (!TextUtils.isEmpty(strObj)) {
                    JSONObject jsonObj = new JSONObject(strObj);
                    String strVerify = jsonObj.getString("verify");
                    String strAppeal = jsonObj.getString("appeal");
                    final RecreationMatchVerify recreationMatchVerify = GsonUtil.getBean(strVerify, RecreationMatchVerify.class);
                    recreationMatchAppeal = GsonUtil.getBean(strAppeal, RecreationMatchAppeal.class);
                    tvContent.setText(recreationMatchVerify.getDescribes());
                    if (recreationMatchVerify.getState() == 1) {
                        tv3.setBackgroundColor(getResources().getColor(R.color.orange));
                        tv4.setBackgroundColor(getResources().getColor(R.color.orange));
                        ivScheduling.setImageResource(R.drawable.has_scheduling_icon);
                        tvScheduling.setTextColor(getResources().getColor(R.color.orange));
                        tvSchedule.setTextColor(getResources().getColor(R.color.black_extend_intro));
                    } else if (recreationMatchVerify.getState() == 2) {
                        tv3.setBackgroundColor(getResources().getColor(R.color.orange));
                        tv4.setBackgroundColor(getResources().getColor(R.color.orange));
                        tv5.setBackgroundColor(getResources().getColor(R.color.orange));
                        tv6.setBackgroundColor(getResources().getColor(R.color.orange));
                        ivScheduling.setImageResource(R.drawable.has_scheduling_icon);
                        ivScheduleGift.setImageResource(R.drawable.failed_red_icon);
                        tvScheduling.setTextColor(getResources().getColor(R.color.black_extend_intro));
                        tvSchedule.setTextColor(getResources().getColor(R.color.black_extend_intro));
                        tvScheduleGift.setText("审核不通过");
                        tvScheduleGift.setTextColor(getResources().getColor(R.color.orange));
                    } else if (recreationMatchVerify.getState() == 3) {
                        tv3.setBackgroundColor(getResources().getColor(R.color.orange));
                        tv4.setBackgroundColor(getResources().getColor(R.color.orange));
                        tv5.setBackgroundColor(getResources().getColor(R.color.orange));
                        tv6.setBackgroundColor(getResources().getColor(R.color.orange));
                        ivScheduling.setImageResource(R.drawable.has_scheduling_icon);
                        ivScheduleGift.setImageResource(R.drawable.has_schedule_gift_icon);
                        tvScheduling.setTextColor(getResources().getColor(R.color.black_extend_intro));
                        tvSchedule.setTextColor(getResources().getColor(R.color.black_extend_intro));
                        tvScheduleGift.setText("审核通过，奖品未发放");
                        tvScheduleGift.setTextColor(getResources().getColor(R.color.orange));
                    } else if (recreationMatchVerify.getState() == 4 ) {
                        tv3.setBackgroundColor(getResources().getColor(R.color.orange));
                        tv4.setBackgroundColor(getResources().getColor(R.color.orange));
                        tv5.setBackgroundColor(getResources().getColor(R.color.orange));
                        tv6.setBackgroundColor(getResources().getColor(R.color.orange));
                        ivScheduling.setImageResource(R.drawable.has_scheduling_icon);
                        ivScheduleGift.setImageResource(R.drawable.has_schedule_gift_icon);
                        tvScheduling.setTextColor(getResources().getColor(R.color.black_extend_intro));
                        tvSchedule.setTextColor(getResources().getColor(R.color.black_extend_intro));
                        tvScheduleGift.setText("已发放");
                        tvScheduleGift.setTextColor(getResources().getColor(R.color.orange));
                    }
                    if (recreationMatchVerify.getImgs().size() > 0) {
                        for (int i = 0; i < recreationMatchVerify.getImgs().size(); i++) {
                            ImageView imageView = new ImageView(this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.width = (int) getResources().getDimension(R.dimen.image_width);
                            lp.height = (int) getResources().getDimension(R.dimen.image_width);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            lp.setMargins((int) getResources().getDimension(R.dimen.weekly_view_margin), (int) getResources().getDimension(R.dimen.weekly_view_margin), (int) getResources().getDimension(R.dimen.weekly_view_margin), (int) getResources().getDimension(R.dimen.weekly_view_margin));
                            imageView.setLayoutParams(lp);
                            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + recreationMatchVerify.getImgs().get(i).getImg(), imageView);
                            llContent.addView(imageView);
                            imageView.setTag(i);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int position = (int) v.getTag();
                                    List<RecreationMatchVerifyImg> urls = recreationMatchVerify.getImgs();
                                    List<Map<String, String>> imgs = new ArrayList<Map<String, String>>();
                                    for (int i = 0; i < urls.size(); i++) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("url",urls.get(i).getImg());
                                        imgs.add(map);
                                    }
                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("images", (Serializable) imgs);
                                    bundle.putInt("image_index", position);
                                    intent.putExtras(bundle);
                                    intent.setClass(context, ImagePagerActivity.class);
                                    context.startActivity(intent);
                                }
                            });
                        }
                    }
                    appealViewStatus(recreationMatchVerify,recreationMatchAppeal);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void appealViewStatus(RecreationMatchVerify recreationMatchVerify, RecreationMatchAppeal recreationMatchAppeal) {
        if (recreationMatchVerify.getState() >=2) {

            getRightTextview().setTextColor(getResources().getColor(R.color.orange));
            if (recreationMatchAppeal.isAppealable() == true) {
                setRightTextView("申诉");
                getRightTextview().setOnClickListener(this);
            } else {
                if (recreationMatchAppeal.getAppealState() == 0) {
                    setRightTextView("申诉中");
                    getRightTextview().setOnClickListener(this);
                } else if (recreationMatchAppeal.getAppealState() == 1) {
                    setRightTextView("申诉完成");
                    getRightTextview().setOnClickListener(this);
                }
            }
        } else {
            getRightTextview().setOnClickListener(null);
            getRightTextview().setTextColor(getResources().getColor(R.color.font_gray));
            setRightTextView("申诉");
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvRightHandle:
                if(getRightTextview().getText().toString().equals("申诉")){
                    goAppeal();
                }else{
                    if(recreationMatchAppeal!= null){
                        goAppealStatus();
                    }
                }
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }
    private void goAppeal(){
        Intent intent = new Intent();
        intent.setClass(context,RecreationMatchAppealActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }
    private void goAppealStatus(){
        Intent intent = new Intent();
        intent.setClass(context, RecreationMatchAppealStatusActivity.class);
        intent.putExtra("recreationMatchAppeal",recreationMatchAppeal);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("recreationMatchAppeal",recreationMatchAppeal);
//        intent.putExtras(bundle);
        startActivity(intent);
    }
}
