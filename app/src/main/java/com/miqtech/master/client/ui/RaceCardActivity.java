package com.miqtech.master.client.ui;

import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.EventAgainst;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.view.RaceCardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/7/7.
 */
public class RaceCardActivity extends BaseActivity{
    @Bind(R.id.raceCardView)
    RaceCardView raceCardView;
    @Bind(R.id.llContent)
    LinearLayout llContent;
    @Bind(R.id.hScrollView)
    HorizontalScrollView hScrollView;
    @Bind(R.id.scrollView)
    ScrollView scrollView;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_racecard);
        ButterKnife.bind(this);
        scrollView.setFillViewport(true);
        hScrollView.setFillViewport(true);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("roundId","84");
        map.put("turn","1");
        sendHttpPost("http://192.168.30.245/v2/event/eventProcessList?roundId=84&turn=1", map,"");
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            String objStr = object.getString("object");
            List<EventAgainst> datas = new Gson().fromJson(objStr, new TypeToken<List<EventAgainst>>() {
            }.getType());
            raceCardView.shuaXin(this,datas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        object
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
    }
}
