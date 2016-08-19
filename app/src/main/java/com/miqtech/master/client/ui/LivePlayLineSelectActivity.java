package com.miqtech.master.client.ui;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/7/29.
 */
public class LivePlayLineSelectActivity extends BaseActivity {
    @Bind(R.id.tvLineTitle)
    TextView tvLineTitle; //直播线路选择
    @Bind(R.id.spinner)
    Spinner spinner; //
    String[] lines;
    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_line_select_layout);
        ButterKnife.bind(this);
        initView();
    }
    @Override
  protected  void initView(){
        lines=new String[]{"rtmp://pili-live-rtmp.wangyuhudong.com/wyds/wyds_player",
                "rtmp://pili-live-rtmp.wangyuhudong.com/wyds/wyds_test_android_01",
        "rtmp://pili-live-rtmp.wangyuhudong.com/wyds/wyds_test4",
        "rtmp://pili-live-rtmp.wangyuhudong.com/wyds/wyds_test03",
        "rtmp://pili-live-rtmp.wangyuhudong.com/wyds/wyds_test02"
        ,"rtmp://pili-live-rtmp.wangyuhudong.com/wyds/wyds_test1"};
         final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_live_play_line,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("信息：");
        spinner.setSelection(0, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
               Intent  intent=new Intent(LivePlayLineSelectActivity.this,PLMediaPlayerActivity.class);
                LogUtil.d(TAG,"arg2"+arg2+"::::"+lines[arg2]);
                intent.putExtra("videoPath",lines[arg2]);
                startActivity(intent);
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }
}
