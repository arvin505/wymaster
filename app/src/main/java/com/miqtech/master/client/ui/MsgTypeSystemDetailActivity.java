package com.miqtech.master.client.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/5.
 */
public class MsgTypeSystemDetailActivity extends BaseActivity {

    @Bind(R.id.msg_system_title)
    TextView tvTitle;
    @Bind(R.id.msg_system_detail)
    TextView tvDetail;
    @Bind(R.id.msg_system_date)
    TextView tvDate;

    private Context context;
    private String title, detail, date;

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_msg_type_system_detail);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();

        title = getIntent().getStringExtra("title");
        detail = getIntent().getStringExtra("detail");
        date = getIntent().getStringExtra("date");
        setLeftIncludeTitle("消息详情");
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });
        tvTitle.setText(title);
        tvDetail.setText(detail);
        tvDate.setText(date);
    }

}
