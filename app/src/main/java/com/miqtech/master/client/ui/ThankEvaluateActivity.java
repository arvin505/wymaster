package com.miqtech.master.client.ui;

import android.view.View;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

/**
 * Created by Administrator on 2015/12/7.
 */
public class ThankEvaluateActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvGoldNum, tvSure;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_thankevaluate);
        initView();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();
        tvGoldNum = (TextView) findViewById(R.id.tvGoldNum);
        tvSure = (TextView) findViewById(R.id.tvSure);

        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("感谢评价");
        tvSure.setOnClickListener(this);
        getLeftBtn().setOnClickListener(this);

        int coinNum = getIntent().getIntExtra("coinNum", 0);
        if (coinNum > 0) {
            tvGoldNum.setText(coinNum + "");
            tvGoldNum.setVisibility(View.VISIBLE);
        } else {
            tvGoldNum.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.tvSure:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
