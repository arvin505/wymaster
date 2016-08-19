package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.CityAdapter;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.PreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 城市列表
 */
public class CityListActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
    private Context context;
    private RelativeLayout rlCurrentCity;
    private TextView tvCurrentCity;
    private GridView gvCity;
    private CityAdapter adapter;

    private static City city = new City();

    private List<City> citys = new ArrayList<City>();

    @Override
    protected void init() {
        setContentView(R.layout.activity_citylist);
        super.init();
        context = this;
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        rlCurrentCity = (RelativeLayout) findViewById(R.id.rlCurrentCity);
        tvCurrentCity = (TextView) findViewById(R.id.tvCurrentCity);
        gvCity = (GridView) findViewById(R.id.gvCity);

        adapter = new CityAdapter(citys, context);
        gvCity.setAdapter(adapter);
        String lastCityStr = PreferencesUtil.getLastRecreationCity(context);
        City lastCity = GsonUtil.getBean(lastCityStr, City.class);
        if(lastCity == null){
            if(Constant.currentCity == null){
                tvCurrentCity.setText("定位失败");
            }else{
                tvCurrentCity.setText(Constant.currentCity.getName());
            }
        }else{
            tvCurrentCity.setText(lastCity.getName());
        }

        rlCurrentCity.setOnClickListener(this);
        rlCurrentCity.setTag(lastCity);
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("选择城市");
        getLeftBtn().setOnClickListener(this);
        isOpenCity(lastCity.getName());
        gvCity.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gvCity.setOnItemClickListener(this);
    }



    /**
     * 获取城市列表
     */
    private void initCityChildrenList(String cityCode) {
        showLoading();
        HashMap<String, String> params = new HashMap<>();
        params.put("code", cityCode);
        sendHttpPost(HttpConstant.COMMON_AREA, params, HttpConstant.COMMON_AREA);
    }

    /**
     * CITY LIST
     *
     * @param cityName
     */
    private void isOpenCity(String cityName) {
        HashMap<String, String> params = new HashMap<>();
        params.put("areaName", cityName);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA +HttpConstant.CHECK_CITY, params, HttpConstant.CHECK_CITY);
        showLoading();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            if (HttpConstant.COMMON_AREA.equals(method)) {
                if (object.getInt("code") == 0 && object.has("object")) {
                    List<City> citysNew = GsonUtil.getList(object.getString("object"), City.class);
                    citys.addAll(citysNew);
                    adapter.notifyDataSetChanged();
                }
            } else if (HttpConstant.CHECK_CITY.equals(method)) {
                if (object.getInt("code") == 0 && object.has("object")) {
                    initCityChildrenList(object.getString("object"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlBack:
                onBackPressed();
                break;
            case R.id.rlCurrentCity:
                Intent intent = new Intent();
                intent.setClass(context, LocationCityActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        City currentCity = citys.get(position);
        if (currentCity != null) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("areaCity", currentCity);
            context.startActivity(intent);
        }
    }

    public static City getCity() {
        return city;
    }
}
