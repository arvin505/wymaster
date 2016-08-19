package com.miqtech.master.client.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;

public class BaiduMapActivity extends BaseActivity implements BaiduMap.OnMapClickListener,
        OnGetRoutePlanResultListener, OnClickListener {
    public static final String LTAG = "BaiduMap";

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            LogUtil.d(LTAG, "action: " + s);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                LogUtil.i(LTAG, "key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
                showToast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                LogUtil.i(LTAG, "网络出错");
                showToast("网络出错,请打开网络!");
            }
        }
    }

    // UI相关
    private SDKReceiver mReceiver;
    private MapView mMapView;
    private ImageView mBtnPre, mBtnNext;
    private BaiduMap mBaidumap;
    int nodeIndex = -1;// 节点索引,供浏览节点时使用
    private RouteLine route = null;
    private OverlayManager routeOverlay = null;
    private boolean useDefaultIcon = false;
    private ImageView nodeIcon = null;// 泡泡view
    private Intent intent;
    // 搜索相关
    private RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private LatLng ll_end;// 终点经纬度
    private ImageView mBack;
    private ImageView mDrive;
    private ImageView mWalk;
    private ImageView mBus;
    private TextView mNetBarTitle;
    private TextView mNodeText;
    private String netbarTitle;
    private LayoutParams iv_params;
    private ImageView thridAppMap;
    private LatLng ll_start;
    // 定位相关
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_baidu_map);
        initRegister();
        // 初始化地图
        intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Double latitude = bundle.getDouble("latitude", 0);
            Double longitude = bundle.getDouble("longitude", 0);
            netbarTitle = bundle.getString("netbarTitle");
            if (latitude != 0 && longitude != 0) {
                ll_end = new LatLng(latitude, longitude);
            }
        }
        initView();
        startService();
    }

    protected void initView() {
        mMapView = (MapView) findViewById(R.id.map);
        mBtnPre = (ImageView) findViewById(R.id.pre);
        mBtnNext = (ImageView) findViewById(R.id.next);
        mBack = (ImageView) findViewById(R.id.back_map);

        thridAppMap = (ImageView) findViewById(R.id.third_app_map);

        mDrive = (ImageView) findViewById(R.id.drive);
        mWalk = (ImageView) findViewById(R.id.walk);
        mBus = (ImageView) findViewById(R.id.transit);

        mNodeText = (TextView) findViewById(R.id.map_nodetext);
        mNetBarTitle = (TextView) findViewById(R.id.map_netBarTitle);
        mNetBarTitle.setText(netbarTitle);

        changeImage(mWalk);
        mBack.setOnClickListener(this);
        mDrive.setOnClickListener(this);
        mWalk.setOnClickListener(this);
        mBus.setOnClickListener(this);
        thridAppMap.setOnClickListener(this);
        // 设置是否显示比例尺控件
        mMapView.showScaleControl(true);
        mMapView.showZoomControls(false);
        mBaidumap = mMapView.getMap();
        mBaidumap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
    }

    private void initRegister() {
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
    }

    private void startService() {
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        mLocClient.setLocOption(option);
        mLocClient.start();
        // glu = new GetLocationUtil(this);
        // glu.setHandler(new Handler() {
        // @Override
        // public void handleMessage(Message msg) {
        // mSearch.walkingSearch((new
        // WalkingRoutePlanOption()).from(PlanNode.withLocation(glu.getLl_start())).to(
        // PlanNode.withLocation(ll_end)));
        // super.handleMessage(msg);
        // }
        // });
        // glu.startLocationClient();
        showLoading();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            ll_start = new LatLng(location.getLatitude(), location.getLongitude());
            mSearch.walkingSearch((new WalkingRoutePlanOption()).from(PlanNode.withLocation(ll_start)).to(
                    PlanNode.withLocation(ll_end)));
        }
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 注销广播
        unregisterReceiver(mReceiver);
        // 关闭定位图层
        mSearch.destroy();
        mLocClient.stop();
        // 关闭定位图层
        mBaidumap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */
    public void SearchButtonProcess(View v) {
        // 重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();
        PlanNode stNode = PlanNode.withLocation(ll_start);
        PlanNode enNode = PlanNode.withLocation(ll_end);
        switch (v.getId()) {
            case R.id.drive:
                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
                break;
            case R.id.walk:
                mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
                break;
            case R.id.transit:
                mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode).city("北京").to(enNode));
                break;
        }
    }

    private void changeImage(View v) {
        switch (v.getId()) {
            case R.id.drive:
                mDrive.setImageResource(R.drawable.btn_drive_on);
                mWalk.setImageResource(R.drawable.btn_walk_off);
                mBus.setImageResource(R.drawable.btn_bus_off);
                break;
            case R.id.walk:
                mDrive.setImageResource(R.drawable.btn_drive_off);
                mWalk.setImageResource(R.drawable.btn_walk_on);
                mBus.setImageResource(R.drawable.btn_bus_off);
                break;
            case R.id.transit:
                mDrive.setImageResource(R.drawable.btn_drive_off);
                mWalk.setImageResource(R.drawable.btn_walk_off);
                mBus.setImageResource(R.drawable.btn_bus_on);
                break;
        }
    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (route == null || route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && v.getId() == R.id.pre) {
            return;
        }
        // 设置节点索引
        if (v.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
                nodeIndex++;
            } else {
                return;
            }
        } else if (v.getId() == R.id.pre) {
            if (nodeIndex > 0) {
                nodeIndex--;
            } else {
                return;
            }
        }
        // 获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        }
        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        // 移动节点至中心
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        nodeIcon = new ImageView(BaiduMapActivity.this);
        iv_params = new LayoutParams(mMapView.getLayoutParams());
        iv_params.width = 10;
        iv_params.height = 10;
        nodeIcon.setLayoutParams(iv_params);
        nodeIcon.setBackgroundResource(R.drawable.notice2_icon);
        mNodeText.setTextColor(0xFF000000);
        mNodeText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(nodeIcon, nodeLocation, 0));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_map:
                this.finish();
                break;
            case R.id.drive:
            case R.id.walk:
            case R.id.transit:
                showLoading();
                changeImage(v);
                SearchButtonProcess(v);
                break;
            case R.id.third_app_map:
                startNavi(ll_start, ll_end);
                break;
        }
    }

    /**
     * 开始导航
     *
     * @param view
     */
    public void startNavi(LatLng pt1, LatLng pt2) {
        // LatLng pt1 = new LatLng(mLat1, mLon1);
        // LatLng pt2 = new LatLng(mLat2, mLon2);
        // 构建 导航参数
        NaviPara para = new NaviPara();
        para.startPoint = pt1;
        para.startName = "从这里开始";
        para.endPoint = pt2;
        para.endName = "到这里结束";
        try {

            BaiduMapNavigation.openBaiduMapNavi(para, this);

        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
            builder.setTitle("提示");
            builder.setPositiveButton("确认", new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    BaiduMapNavigation.getLatestBaiduMapApp(BaiduMapActivity.this);
                }
            });

            builder.setNegativeButton("取消", new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }catch (Exception e){
            e.printStackTrace();
            showToast("导航失败");
        }
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        hideLoading();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(BaiduMapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        hideLoading();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(BaiduMapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        hideLoading();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(BaiduMapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
            routeOverlay = overlay;
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }
}
