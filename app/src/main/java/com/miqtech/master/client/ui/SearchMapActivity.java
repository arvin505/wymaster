package com.miqtech.master.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;

import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;

import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;

import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.InternetBarInfo;

import com.miqtech.master.client.http.HttpConstant;

import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import com.miqtech.master.client.utils.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 地图搜索附近网吧
 *
 * @author zhangp
 *         http://192.168.16.106:8080/searchLocalNetbar?userId=21&token=sZsSuV
 *         +5U9eJakz3JLmqNQ==&longitude=120&latitude=30
 */
public class SearchMapActivity extends BaseActivity implements OnClickListener, OnMapLongClickListener {
    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private InfoWindow mInfoWindow;
    private Context mContext;


    // 初始化全局 bitmap 信息，不用时及时 recycle
//    BitmapDescriptor natbarIcon = BitmapDescriptorFactory.fromResource(R.drawable.center);
    BitmapDescriptor natbarIcon = BitmapDescriptorFactory.fromResource(R.drawable.location_netbar);
    //    BitmapDescriptor myselfIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_loc_netbar);
    BitmapDescriptor myselfIcon = BitmapDescriptorFactory.fromResource(R.drawable.location);
    BitmapDescriptor mapCenter = BitmapDescriptorFactory.fromResource(R.drawable.node);
    private List<InternetBarInfo> internetBars = new ArrayList<InternetBarInfo>();


    LatLng llNew01 = new LatLng(30.1909420000, 120.1509330000);
    LatLng llNew02 = new LatLng(30.1873840000, 120.1477890000);
    LatLng llNew03 = new LatLng(30.1868220000, 120.1580650000);
    LatLng llNew04 = new LatLng(30.1843240000, 120.1589990000);
    LatLng llNew05 = new LatLng(30.1938770000, 120.1505910000);
    LatLng llNew06 = new LatLng(30.1887570000, 120.1402430000);
    LatLng llNew07 = new LatLng(30.1883200000, 120.1429740000);
    LatLng llNew08 = new LatLng(30.2086720000, 120.1466390000);
    LatLng llNew09 = new LatLng(30.1851980000, 120.1431890000);
    LatLng llNew10 = new LatLng(30.2109810000, 120.1383740000);
    private LayoutInflater inflater;
    private ImageView mBack;
    private ImageView iv_location;
    private LatLng location;
    private OverlayOptions center;

    double setatLoadTime;
    double loadTime;
    double onSusTime;
    double startTime;
    double endTime;

    @Override
    protected void init() {
        super.init();
        mContext = this;
        setContentView(R.layout.search_map);
        inflater = LayoutInflater.from(mContext);
        location = new LatLng(Constant.latitude, Constant.longitude);
        initMapView();
        initView();
        setTheCenter(location);
    }

    protected void initView() {
        center = new MarkerOptions().position(location).icon(myselfIcon);
        mBack = (ImageView) findViewById(R.id.back_map);
        iv_location = (ImageView) findViewById(R.id.local_icon);
        mBack.setOnClickListener(this);
        iv_location.setOnClickListener(this);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, null));
        Double longitude = Constant.longitude;
        Double latitude = Constant.latitude;

        setatLoadTime = System.currentTimeMillis();
        loadData(longitude, latitude);
    }

    private void initMapView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mMapView.showZoomControls(false);
    }

    private void refthMapView() {
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            private InternetBarInfo netbar;
            private Bitmap bmp;

            public boolean onMarkerClick(final Marker marker) {
                View nodeView = inflater.inflate(R.layout.search_netbar_node, null);
                ImageView netbarIcon = (ImageView) nodeView.findViewById(R.id.netbatIcon);
                ImageView iv_Node = (ImageView) nodeView.findViewById(R.id.sendToNetbar);
                TextView tv_Node_name = (TextView) nodeView.findViewById(R.id.node_netbar_name);
                TextView tv_Node_price = (TextView) nodeView.findViewById(R.id.node_netbar_price);
                OnInfoWindowClickListener listener = null;
                int index = marker.getZIndex();
                netbar = internetBars.get(index);
                tv_Node_name.setText(netbar.getNetbar_name());
                tv_Node_price.setText("¥" + netbar.getPrice_per_hour() + "/小时");
                LogUtil.i("asd", "mainThread" + Thread.currentThread().getName());
                Thread imageLoder = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bmp = ImageLoader.getInstance().loadImageSync(
                                HttpConstant.SERVICE_UPLOAD_AREA + netbar.getIcon());
                    }
                });
                try {
                    imageLoder.start();
                    imageLoder.join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (bmp != null) {
                    netbarIcon.setImageBitmap(bmp);
                } else {
                    netbarIcon.setImageResource(R.drawable.default_img);
                }

                final String tag = netbar.getId();
                listener = new OnInfoWindowClickListener() {
                    private Intent intent;

                    @Override
                    public void onInfoWindowClick() {
                        intent = new Intent();
                        intent.setClass(mContext, InternetBarActivityV2.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("netbarId", tag);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                };
                LatLng ll = marker.getPosition();
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(nodeView), ll, -47, listener);
                mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
        mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                mBaiduMap.hideInfoWindow();
            }
        });
        mBaiduMap.setOnMapLongClickListener(this);
    }

    private void loadData(Double longitude, Double latitude) {
        loadTime = System.currentTimeMillis() - setatLoadTime;
        LogUtil.e("loadData-----------------------------------", loadTime + "");
        mBaiduMap.clear();
        Map<String, String> params = new HashMap<>();
        params.put("longitude", longitude + "");
        params.put("latitude", latitude + "");
        params.put("city", Constant.currentCity.getAreaCode());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SEARCHNETBARFORMAP, params, HttpConstant.SEARCHNETBARFORMAP);
        showLoading();
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.e("object", " ob == " + object.toString());
        if (HttpConstant.SEARCHNETBARFORMAP.equals(method)) {
            try {
                double ss;
                ss = System.currentTimeMillis() - loadTime;
                LogUtil.e("json_start-----------------------------------", ss + "");
                List<InternetBarInfo> list = initNetBarData(object);
                if (list != null && !list.isEmpty()) {
                    internetBars.clear();
                    internetBars.addAll(list);
                    onSusTime = System.currentTimeMillis() - ss;
                    LogUtil.e("json_end_and_addOvery-----------------------------------", onSusTime + "");
                    initOverlay();
                    refthMapView();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.e("xiaoyi", "eeeeee");
            }
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    private List<InternetBarInfo> initNetBarData(JSONObject object) throws JSONException {
        List<InternetBarInfo> netbars = new ArrayList<>();
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONArray objectJson = object.getJSONArray("object");
//           netbars = GsonUtil.getList(objectJson.toString(), InternetBarInfo.class);
            netbars = new Gson().fromJson(objectJson.toString(),  new TypeToken<List<InternetBarInfo>>() {
            }.getType());
            return netbars;
        }
        return netbars;
    }

    public void initOverlay() {
        LatLng llA = null;
        InternetBarInfo natbarInfo;
        OverlayOptions ooA;
        startTime = System.currentTimeMillis() - onSusTime;
        LogUtil.e("initOverlay____start-----------------------------------", startTime + "");
        for (int i = 0; i < internetBars.size(); i++) {
            natbarInfo = internetBars.get(i);
            llA = new LatLng(natbarInfo.getLatitude(), natbarInfo.getLongitude());
            ooA = new MarkerOptions().position(llA).icon(natbarIcon).zIndex(i);
            mBaiduMap.addOverlay(ooA);
        }
        // 添加圆
        // OverlayOptions ooCircle = new CircleOptions().fillColor(0x687093DB)
        // .center(location).stroke(new Stroke(1, 0x887093DB))
        // .radius(20000);
        // mBaiduMap.addOverlay(ooCircle);
//        setTheCenter(location);
        endTime = System.currentTimeMillis() - startTime;
        LogUtil.e("initOverlay____end-----------------------------------", endTime + "");
        hideLoading();
    }

    private void setTheCenter(LatLng location) {
        MapStatusUpdate u2 = MapStatusUpdateFactory.zoomTo(12.0f);
        mBaiduMap.animateMapStatus(u2);
        mBaiduMap.addOverlay(center);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(location, 18);
        mBaiduMap.animateMapStatus(u);
    }


    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        natbarIcon.recycle();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_map) {
            this.finish();
        }
        if (v.getId() == R.id.local_icon) {
            location = new LatLng(Constant.latitude, Constant.longitude);
            center = new MarkerOptions().position(location).icon(myselfIcon);
            setTheCenter(location);
        }
    }

    @Override
    public void onMapLongClick(LatLng arg0) {
        Double longitude = arg0.longitude;
        Double latitude = arg0.latitude;
        location = new LatLng(latitude, longitude);
        center = new MarkerOptions().position(location).icon(mapCenter);
        // startHttpRequest(longitude, latitude);
    }
}
