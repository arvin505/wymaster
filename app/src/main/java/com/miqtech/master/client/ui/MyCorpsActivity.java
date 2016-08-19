package com.miqtech.master.client.ui;

import android.os.Bundle;
import android.view.View;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.fragment.FragmentMyCorps;

/**
 * Created by xiaoyi on 2016/8/2.
 */
public class MyCorpsActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycorps);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new  FragmentMyCorps()).commit();
        setLeftIncludeTitle("我的战队");
        getLeftBtn().setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }
}
