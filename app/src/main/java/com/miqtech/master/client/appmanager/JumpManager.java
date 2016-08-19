package com.miqtech.master.client.appmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.miqtech.master.client.ui.InformationAtlasActivity;
import com.miqtech.master.client.ui.InformationDetailActivity;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.ui.StartActivity;
import com.miqtech.master.client.ui.YueZhanDetailsActivity;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;

/**
 * Created by arvin on 2016/6/17.
 */
public class JumpManager extends BaseActivity {
    private String TAG = "JumpManager";
    private static final String INFO = "info"; //资讯
    private static final String PHOTOINFO = "photoinfo"; //图集
    private static final String NETBAR = "netbar"; //网吧
    private static final String MATCH = "match"; //约战
    private static final String AMUSE = "amuse"; //娱乐赛
    private static final String ACTIVITY = "activity"; //官方活动

    private String[] schemeDatas;
    private boolean hasExtras = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getHref();
        /*if (MainActivity.isProcess) {*/
        if (!hasExtras) {
            finish();
            return;
        }

        Intent mainIntent = new Intent();
        Intent extraIntent = new Intent();
        Bundle extraBundles = new Bundle();
        switch (schemeDatas[0]) {
            case NETBAR:
                extraIntent.setClass(this, InternetBarActivityV2.class);
                extraIntent.putExtra("netbarId", schemeDatas[2]);

                extraBundles.putString("action", InternetBarActivityV2.class.getName());
                extraBundles.putString("id", schemeDatas[2]);
                extraBundles.putString("key", "netbarId");
                break;
            case MATCH:
                extraIntent.setClass(this, YueZhanDetailsActivity.class);
                extraIntent.putExtra("id", schemeDatas[2]);

                extraBundles.putString("action", YueZhanDetailsActivity.class.getName());
                extraBundles.putString("id", schemeDatas[2]);
                extraBundles.putString("key", "id");
                break;
            case INFO:
                extraIntent.setClass(this, InformationDetailActivity.class);
                extraIntent.putExtra("id", schemeDatas[2]);

                extraBundles.putString("action", InformationDetailActivity.class.getName());
                extraBundles.putString("id", schemeDatas[2]);
                extraBundles.putString("key", "id");
                break;
            case PHOTOINFO:
                extraIntent.setClass(this, InformationAtlasActivity.class);
                extraIntent.putExtra("activityId", Integer.parseInt(schemeDatas[2]));

                extraBundles.putString("action", InformationAtlasActivity.class.getName());
                extraBundles.putString("id", schemeDatas[2]);
                extraBundles.putString("key", "activityId");
                break;
            case AMUSE:
                extraIntent.setClass(this, RecreationMatchDetailsActivity.class);
                extraIntent.putExtra("id", schemeDatas[2]);

                extraBundles.putString("action", RecreationMatchDetailsActivity.class.getName());
                extraBundles.putString("id", schemeDatas[2]);
                extraBundles.putString("key", "id");
                break;
            case ACTIVITY:
                extraIntent.setClass(this, OfficalEventActivity.class);
                extraIntent.putExtra("matchId", schemeDatas[2]);

                extraBundles.putString("action", OfficalEventActivity.class.getName());
                extraBundles.putString("id", schemeDatas[2]);
                extraBundles.putString("key", "matchId");
                break;

        }

        if (AppManager.getAppManager().findActivity(MainActivity.class)) {
            mainIntent.setClass(this, MainActivity.class);
            Intent[] intents = new Intent[]{mainIntent, extraIntent};
            startActivities(intents);
            LogUtil.e(TAG, "jump .. main");

        } else {
            mainIntent.setClass(this, StartActivity.class);
            mainIntent.putExtra("data", extraBundles);
            startActivity(mainIntent);
            LogUtil.e(TAG, "jump .. start");
        }
        finish();
    }

    private void getHref() {
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        Uri uri = intent.getData();
        if (uri != null) {
            String dataString = intent.getDataString();
            LogUtil.e("tag", "jump--------------------" + dataString);
            if (null != dataString && !TextUtils.isEmpty(dataString)) {
                String[] datas = new String[3];
                if (dataString.contains("//") && dataString.contains("?") && dataString.contains("=")) {
                    int start = dataString.lastIndexOf("/");
                    int next = dataString.lastIndexOf("?");
                    int end = dataString.lastIndexOf("=");
                    datas[0] = dataString.substring(start + 1, next);
                    datas[1] = dataString.substring(next + 1, end);
                    datas[2] = dataString.substring(end + 1);
                    LogUtil.e(TAG, "data..." + datas[0] + "..." + datas[1] + ".... " + datas[2]);
                    if (datas[0] != null && !TextUtils.isEmpty(datas[0])) {
                        schemeDatas = datas;
                        hasExtras = true;
                    }

                }
            }
        }
    }

}
