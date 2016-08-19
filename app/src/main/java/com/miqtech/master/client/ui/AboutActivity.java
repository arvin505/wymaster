package com.miqtech.master.client.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

/**
 * Created by Administrator on 2015/12/8.
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_aboutus);
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("关于我们");
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        String version = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(version);
        getLeftBtn().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
    }
}
