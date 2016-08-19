package com.miqtech.master.client.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 反馈
 * Created by Administrator on 2015/12/3.
 */
public class FeedBackActivity extends BaseActivity implements View.OnClickListener{

    private EditText edtFeedBack,edtContactWay;
    private Button btnSave ;
    private Context context;
    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_feedback);
        initView();
    }
    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
    }
    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        edtFeedBack = (EditText)findViewById(R.id.edtFeedBack);
        edtContactWay = (EditText)findViewById(R.id.edtContactWay);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        setLeftIncludeTitle("意见反馈");
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        context = FeedBackActivity.this;
        User user = WangYuApplication.getUser(this);
        if (user!= null){
            edtContactWay.setText(user.getTelephone());
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if(method.equals(HttpConstant.FEEDBACK)){
            showToast("吐槽成功");
            edtContactWay.setText("");
            edtFeedBack.setText("");
            onBackPressed();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }


    private void uploadFeedBack(){
        if(edtFeedBack.getText().toString().length() == 0){
            showToast("请正确输入意见反馈的内容");
            return ;
        }
        Map<String, String> map = new HashMap<>();
        map.put("content", edtFeedBack.getText().toString());
        if(!TextUtils.isEmpty(edtContactWay.getText().toString())){
            map.put("contact", edtContactWay.getText().toString());
        }
        if(WangYuApplication.getUser(context)!= null){
            map.put("userId",WangYuApplication.getUser(context).getId()+"");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.FEEDBACK, map, HttpConstant.FEEDBACK);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnSave:
                uploadFeedBack();
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
