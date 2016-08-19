package com.miqtech.master.client.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.http.RequestUtil;
import com.miqtech.master.client.http.ResponseListener;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 定位service
 */
public class MyLocationService extends Service {
    public MyLocationService() {
    }

    private LocationClient mClinet;
    private MyLocation myLocation = new MyLocation();
    private String city;
    private boolean isOK = true;

    public static final String ACTIVITY_RESERVE = "com.miqtech.master.clinet.ui.MainActivity";
    public static String CODE_TYPE = "code_type";
    public static final int LOCATION_END = 1;

    private static int LOCATION_ERROR = 1;
    private static int LOCATION_SUCCESS = 2;

    private MyHandler myHandler = new MyHandler();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOCATION_ERROR) {
                notifLoca();
            } else {
                BDLocation bdLocation = (BDLocation) msg.obj;
                Constant.isLocation = true;
                Constant.latitude = bdLocation.getLatitude();
                Constant.longitude = bdLocation.getLongitude();
                Constant.cityName = bdLocation.getCity();
                Constant.locCity.setAreaCode(bdLocation.getCityCode());
                Constant.locCity.setName(bdLocation.getCity());
                PreferencesUtil.saveLatitude$Longitude(MyLocationService.this, Constant.latitude + "", Constant.longitude + "");
                getAeraCode(bdLocation.getCity());
                LogUtil.e("定位完成------------------------------", Constant.longitude + "----------------------------" + Constant.latitude);
            }
        }
    }

    /**
     * 定位信息配置
     */
    private void locationStation() {
        mClinet = new LocationClient(this.getApplicationContext());
        mClinet.registerLocationListener(myLocation);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(500);//请求间隔时间
        option.setIsNeedAddress(true);//地理反编码
        mClinet.setLocOption(option);
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        mClinet.start();
    }

    private class MyLocation implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                return;
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                // 运营商信息
                Message msg = myHandler.obtainMessage();
                msg.what = LOCATION_SUCCESS;
                msg.obj = bdLocation;
                myHandler.sendMessage(msg);
            } else {
                myHandler.sendEmptyMessage(LOCATION_ERROR);
            }
            finishLocation(bdLocation);
        }
    }

    private void finishLocation(BDLocation bdLocation) {
        if (bdLocation != null && mClinet.isStarted()) {
            mClinet.stop();
            mClinet.unRegisterLocationListener(myLocation);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        locationStation();
        startLocation();
    }

    /**
     * 通知acticity定位完毕
     */
    private void notifLoca() {
        Intent ii = new Intent(ACTIVITY_RESERVE);
        ii.putExtra(CODE_TYPE, LOCATION_END);
        sendBroadcast(ii);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void getAeraCode(String city) {
        Map<String, String> params = new HashMap<>();
        params.put("areaName", city);
        RequestUtil.getInstance().excutePostRequest(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.VALIDATEAERAINFO, params,
                new ResponseListener() {
                    @Override
                    public void onSuccess(JSONObject object, String method) {
                        try {
                            if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
                                JSONObject objectJson = object.getJSONObject("object");
                                City city = new Gson().fromJson(objectJson.toString(), City.class);

                                Constant.locCity.setAreaCode(city.getAreaCode());
                                if (TextUtils.isEmpty(PreferencesUtil.getLastRecreationCity(MyLocationService.this))) {
                                    Constant.currentCity = Constant.locCity;
                                    PreferencesUtil.setLastRecreationCity(new Gson().toJson(Constant.currentCity), getApplicationContext());
                                }
                                PreferencesUtil.saveAeraCode(MyLocationService.this, Constant.currentCity.getAreaCode());
                            }
                            notifLoca();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String errMsg, String method) {
                        notifLoca();
                    }

                    @Override
                    public void onFaild(JSONObject object, String method) {
                        notifLoca();
                    }
                },
                HttpConstant.VALIDATEAERAINFO);
    }


}
