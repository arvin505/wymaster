package com.miqtech.master.client.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/26.
 */
public class HostSettingActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_current_host)
    TextView current;

    @Bind(R.id.lv_host)
    ListView lvHost;

    ArrayAdapter adapter;

    private String[] hosts = new String[]{"http://172.16.2.62/","http://172.16.2.14/","http://wy.api.wangyuhudong.com/", "http://api.wangyuhudong.com/"};

    private String[] hosts_1 = new String[]{"http://192.168.30.245:8077/","http://192.168.30.245:8077/","http://wy.log.wangyuhudong.com/", "http://log.wangyuhudong.com/"};

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_hostsetting);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void initView() {
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("Host设置");
        getLeftBtn().setOnClickListener(this);

        current.setText(HttpConstant.SERVICE_HTTP_AREA);

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,hosts);
        lvHost.setAdapter(adapter);

        lvHost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HttpConstant.SERVICE_HTTP_AREA = hosts[position];
                HttpConstant.SERVICE_HTTP_AREA_1 = hosts_1[position];
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }
}
