package com.miqtech.master.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ReportAdapter;
import com.miqtech.master.client.adapter.ReportAdapter.OnMyCheckChangedListener;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

/**
 * 举报原因
 *
 * @author Administrator
 */
public class ReportActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
    private int type;// 举报类别:1.用户2.评论3.约战4网吧评论5娱乐赛评论
    private int targetId;// type=1被举报用户的id,type=2评论id,type=3约战id,type=4网吧评价id,type=5娱乐赛评论id
    private int categoryID = 1;// (举报类别)-1其他原因1政治内容2淫秽色情3泄露隐私4广告5脏话6恶意评价
    private String reamrk;// 举报内容
    private User user;
    private Context context;
    private ReportAdapter adapter;

    private TextView submit_tv;
    private GridView selectCategory;
    private EditText remark_ed;

    private ImageView back;

    private String[] itemName = new String[]{"政治内容", "淫秽色情", "泄露隐私", "广告", "脏话", "恶意评价", "其它原因"};

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_report);
        context = ReportActivity.this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();

        submit_tv = (TextView) findViewById(R.id.submit_tv);
        selectCategory = (GridView) findViewById(R.id.select_category_gd);
        remark_ed = (EditText) findViewById(R.id.remark_ed);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        setLeftIncludeTitle("举报原因");
       /* setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);*/
        submit_tv.setOnClickListener(this);

        adapter = new ReportAdapter(context, itemName);
        selectCategory.setAdapter(adapter);
        selectCategory.setOnItemClickListener(this);
        adapter.setOncheckChanged(new OnMyCheckChangedListener() {
            @Override
            public void setSelectID(int selectID) {
                adapter.setSelectID(selectID); // 选中位置
                adapter.notifyDataSetChanged(); // 刷新适配器
                if (selectID == 6) {
                    categoryID = -1;
                } else {
                    categoryID = selectID + 1;
                }
            }
        });
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        type = getIntent().getIntExtra("type", 0);
        targetId = getIntent().getIntExtra("targetId", 0);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.submit_tv:
                reamrk = remark_ed.getText().toString();
                postReportReason();
                break;
            default:
                break;
        }
    }

    private void postReportReason() {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            showLoading();
            Map<String, String> params = new HashMap<>();
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            params.put("type", type + "");
            params.put("targetId", targetId + "");
            params.put("category", categoryID + "");
            params.put("reamrk", reamrk);
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.REPORT_USER, params, HttpConstant.REPORT_USER);

        } else {
            Intent ii = new Intent(context, LoginActivity.class);
            startActivity(ii);
            //NetbarCommentDetailActivity.isRefresh = true;
        }
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        // TODO Auto-generated method stub
        super.onSuccess(object, method);
        hideLoading();
        showToast("举报成功");
        finish();
    }

    @Override
    public void onError(String method, String errorInfo) {
        // TODO Auto-generated method stub
        hideLoading();
        showToast(errorInfo);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
//		showToast(position + "=======");
        adapter.setSelectID(position);
        adapter.notifyDataSetChanged();
        if (position == 6) {
            categoryID = -1;
        } else {
            categoryID = position + 1;
        }
    }
}
