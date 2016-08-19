package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.RecreationMatchAppeal;
import com.miqtech.master.client.entity.RecreationMatchAppealImg;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/14 0014.
 */
public class RecreationMatchAppealStatusActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.tvStatus)
    TextView tvStatus;
    @Bind(R.id.tvContent)
    TextView tvContent;
    @Bind(R.id.llImgs)
    LinearLayout llImgs;

    private Context context;

    private RecreationMatchAppeal recreationMatchAppeal;


    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_appealstatus);
        ButterKnife.bind(this);
        context = this;
        initData();
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        int appealState = recreationMatchAppeal.getAppealState();
        String appealRemark = recreationMatchAppeal.getAppealRemark();
        final List<RecreationMatchAppealImg> imgList = recreationMatchAppeal.getAppealImgs();
        if (appealState == 0) {
            tvStatus.setText("申诉中");
        } else if (appealState == 1) {
            tvStatus.setText("申诉完成");
        }
        tvContent.setText(appealRemark);
        getLeftBtn().setImageResource(R.drawable.back);
        setLeftIncludeTitle("申诉进度");
        getLeftBtn().setOnClickListener(this);

        if (imgList.size() > 0) {
            for (int i = 0; i < imgList.size(); i++) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.width = (int) getResources().getDimension(R.dimen.image_width);
                lp.height = (int) getResources().getDimension(R.dimen.image_width);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp.setMargins((int) getResources().getDimension(R.dimen.weekly_view_margin), (int) getResources().getDimension(R.dimen.weekly_view_margin), (int) getResources().getDimension(R.dimen.weekly_view_margin), (int) getResources().getDimension(R.dimen.weekly_view_margin));
                imageView.setLayoutParams(lp);
                AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + imgList.get(i).getImg(), imageView);
                llImgs.addView(imageView);
                imageView.setTag(i);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        List<Map<String, String>> imgs = new ArrayList<Map<String, String>>();
                        for (int i = 0; i < imgList.size(); i++) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("url", imgList.get(i).getImg());
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
    }

    @Override
    protected void initData() {
        super.initData();
//        Bundle bundle = getIntent().getExtras();
//        recreationMatchAppeal = (RecreationMatchAppeal)bundle.getSerializable("recreationMatchAppeal");
//        Log.e("recreationMatchAppeal",recreationMatchAppeal.toString());
        recreationMatchAppeal = (RecreationMatchAppeal)getIntent().getSerializableExtra("recreationMatchAppeal");
        LogUtil.e("recreationMatchAppeal", recreationMatchAppeal.toString());
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }
}
