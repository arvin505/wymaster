package com.miqtech.master.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ApplyedCorpsAdapter;
import com.miqtech.master.client.adapter.SpinnerCityAdapter;
import com.miqtech.master.client.adapter.SpinnerNetbarAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.entity.Corps;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.MySpinner;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import com.miqtech.master.client.view.RefreshLayout;
import com.miqtech.master.client.watcher.Observerable;

public class MatchCorpsActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener {

    private HasErrorListView lvMatchCorps;
    private RefreshLayout refresh_view;
    private User user;
    private Context context;
    private int matchId;
    private String netbarId = "";
    private String areaCode = "";
    private List<Corps> corpsList = new ArrayList<Corps>();
    private List<CityNetbar> cityNetbarList = new ArrayList<CityNetbar>();

    private List<City> cityList = new ArrayList<City>();
    private List<InternetBarInfo> netbarList = new ArrayList<InternetBarInfo>();
    private Map<String, Integer> netbar_city = new HashMap<String, Integer>();

    private MySpinner spinner_city;
    private MySpinner spinner_netbar;
    private SpinnerCityAdapter cityAdapter;
    private SpinnerNetbarAdapter netbarAdapter;

    private boolean loadMore = false;

    private String[] netbarStrName = new String[]{"请选择网吧"};
    private CorpsAction corpsAction = new CorpsAction() {

        @Override
        public void onJoinCorps(Corps corps) {
            if (corps != null) {
                toJoinCorps(corps);
            }
        }
    };
    private int page = 1;

    private int rows = 10;
    private int isLast;
    private ApplyedCorpsAdapter corpsAdapter;
    private ImageView ivBackUp;
    private TextView tv_LeftTitle;
    private Intent intent;

    private SpinnerInterface spinnerClick = new SpinnerInterface() {
        @Override
        public void onItemSelected(boolean isParent, int position) {
            if (isParent) {
                cityItemOnclick(position);
            } else {
                netBarItemOnclick(position);
            }
        }
    };
    public int status;

    private Observerable observerable = Observerable.getInstance();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.match_corps);
        this.context = this;
        this.matchId = Integer.parseInt(getIntent().getStringExtra("matchId"));
        LogUtil.e("info", "id ddd == " + matchId);
        this.status = getIntent().getIntExtra("isFinish", -1);
        initView();
        requestCorps();

        observerable.subscribe(Observerable.ObserverableType.APPLYSTATE, new Observerable.ISubscribe() {

            @Override
            public <T> void update(T... data) {
                requestCorps();
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        ivBackUp = (ImageView) findViewById(R.id.ivBack);
        ivBackUp.setImageResource(R.drawable.back);
        tv_LeftTitle = (TextView) findViewById(R.id.tvLeftTitle);
        tv_LeftTitle.setText("已报战队");
        refresh_view = (RefreshLayout) findViewById(R.id.refresh_view);
        refresh_view.setColorSchemeResources(R.color.colorActionBarSelected);
        refresh_view.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                requestCorps();
            }
        });
        refresh_view.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                if (isLast == 0) {
                    page++;
                    requestCorps();
                    loadMore = true;
                } else {
                    showToast(R.string.nomore);
                    page--;
                    refresh_view.setLoading(false);
                }

            }
        });
        ivBackUp.setOnClickListener(this);

        lvMatchCorps = (HasErrorListView) findViewById(R.id.lvMatchCorps);
        lvMatchCorps.setErrorView("还木有战队报名~", R.drawable.blank_fight);
        corpsAdapter = new ApplyedCorpsAdapter(context, corpsList, corpsAction);
        lvMatchCorps.setAdapter(corpsAdapter);
        lvMatchCorps.setOnItemClickListener(this);

        spinner_city = (MySpinner) findViewById(R.id.corps_city);
        cityAdapter = new SpinnerCityAdapter(context, spinner_city, cityList,
                spinnerClick);
        spinner_city.setAdapter(cityAdapter);
        spinner_netbar = (MySpinner) findViewById(R.id.corps_netbar);
        netbarAdapter = new SpinnerNetbarAdapter(context, spinner_netbar,
                netbarList, spinnerClick);
        spinner_netbar.setAdapter(netbarAdapter);
    }

    private void requestCorps() {
        showLoading();

        user = WangYuApplication.getUser(context);
        Map<String, String> params = new HashMap<>();
        if (user != null) {
            params.put("userId", user.getId());
        }
        if (!"".equals(netbarId)) {
            params.put("netbarId", netbarId);
        }
        if (!"".equals(areaCode)) {
            params.put("areaCode", areaCode);
        }
        params.put("activityId", matchId + "");
        params.put("page", page + "");
        params.put("pageSize", rows + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.APPLYED_CORPS, params, HttpConstant.APPLYED_CORPS);
    }

    private void requestCity(String cityCode) {
        showLoading();
        user = WangYuApplication.getUser(context);
        Map<String, String> params = new HashMap<>();
        if (user != null) {
            params.put("userId", user.getId());
        }
        params.put("activityId", matchId + "");
        params.put("areaCode", cityCode);
        params.put("page", page + "");
        params.put("pageSize", rows + "");

        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.APPLYED_CORPS, params, HttpConstant.APPLYED_CORPS);
    }

    private void requestNetbar(String netbarId) {
        showLoading();
        user = WangYuApplication.getUser(context);
        Map<String, String> params = new HashMap<>();
        if (user != null) {
            params.put("userId", user.getId());
        }
        params.put("activityId", matchId + "");
        params.put("netbarId", netbarId);
        params.put("page", page + "");
        params.put("pageSize", rows + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.APPLYED_CORPS, params, HttpConstant.APPLYED_CORPS);
    }

    private void toJoinCorps(Corps corps) {
        intent = new Intent();
        intent.setClass(context, JoinCorpsActivity.class);
        intent.putExtra("teamId", corps.getTeam_Id());
        intent.putExtra("teamName", corps.getTeam_name());
        context.startActivity(intent);
    }

    @Override
    protected void initData() {
        super.initData();
        corpsAdapter.notifyDataSetChanged();
    }

    private void initCondition() {
        cityList.clear();
        netbarList.clear();
        City allCity = new City();
        allCity.setName("全部城市");
        allCity.setAreaCode("-1");
        cityList.add(allCity);
        netbarList.add(new InternetBarInfo("-1", "全部网吧"));

        if (cityNetbarList.size() > 0) {
            for (int i = 0; i < cityNetbarList.size(); i++) {
                CityNetbar cityNetbar = cityNetbarList.get(i);
                City city = new City();
                city.setName(cityNetbar.getCity());
                city.setAreaCode(cityNetbar.getArea_code() + "");
                cityList.add(city);
                for (int j = 0; j < cityNetbar.getNetbars().size(); j++) {
                    netbar_city.put(cityNetbar.getNetbars().get(j).getId(),
                            i + 1);
                    netbarList.add(cityNetbar.getNetbars().get(j));
                }
            }
        }
        cityAdapter.notifyDataSetChanged();
        netbarAdapter.notifyDataSetChanged();

        spinner_city.setSelection(0);
        spinner_netbar.setSelection(0);
        areaCode = "";
        netbarId = "";
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        LogUtil.e("city", "cata == " + object.toString());
        refresh_view.setRefreshing(false);
        JSONObject obj;
        try {
            if (object.has("object")) {
                obj = object.getJSONObject("object");
                if (obj.has("teams")) {
                    JSONArray teamsJson = obj.getJSONArray("teams");
                    List<Corps> corpNews = GsonUtil.getList(teamsJson.toString(), Corps.class);
                    if (page == 1) {
                        corpsList.clear();
                    }
                    corpsList.addAll(corpNews);
                    initData();
                    if (loadMore) {
                        loadMore = false;
                        refresh_view.setLoading(false);
                        if (corpNews.isEmpty()) {
                            page--;
                            showToast(R.string.nomore);
                        }
                    }
                } else {
                    if (loadMore) {
                        loadMore = false;
                        refresh_view.setLoading(false);
                        page--;
                        showToast(R.string.nomore);

                    }
                }
                if (obj.has("condition") && page == 1) {
                    JSONArray conditionJson = obj.getJSONArray("condition");
                    List<CityNetbar> conditionNews = GsonUtil.getList(conditionJson.toString(), CityNetbar.class);
                    if (!conditionNews.isEmpty()) {
                        cityNetbarList.clear();
                        cityNetbarList.addAll(conditionNews);
                    }
                }
                initCondition();
                if (obj.has("isLast")) {
                    isLast = obj.getInt("isLast");
                }
                if (corpsList.isEmpty()) {
                    lvMatchCorps.setErrorShow(true);
                } else {
                    lvMatchCorps.setErrorShow(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        areaCode = "";
        netbarId = "";
    }

    @Override
    public void onError(String method, String errorInfo) {
        super.onError(method, errorInfo);
        hideLoading();
        showToast(errorInfo);
        refresh_view.setRefreshing(false);
        if (corpsList.isEmpty()) {
            lvMatchCorps.setErrorShow(true);
        } else {
            lvMatchCorps.setErrorShow(false);
        }
        if (loadMore) {
            loadMore = false;
            page--;
            refresh_view.setLoading(false);
        }
    }

    /**
     * List转换String
     *
     * @param list :需要转换的List
     * @return String转换后的字符串
     */
    public String[] netbarListToString(List<String> list) {
        String[] sb = new String[list.size() + 1];
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || list.get(i) == "") {
                    continue;
                }
                sb[i] = list.get(i);
            }
        }
        sb[list.size()] = "请选择网吧";
        return sb;
    }

    public String[] cityListToString(List<String> list) {
        String[] sb = new String[list.size() + 1];
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || list.get(i) == "") {
                    continue;
                }
                sb[i] = list.get(i);
            }
        }
        sb[list.size()] = "所有战队";
        return sb;
    }

    class CityNetbar {
        private int area_code;
        private String city;
        private List<InternetBarInfo> netbars = new ArrayList<InternetBarInfo>();

        public List<InternetBarInfo> getNetbars() {
            return netbars;
        }

        public void setNetbars(List<InternetBarInfo> netbars) {
            this.netbars = netbars;
        }

        @Override
        public String toString() {
            return "CityNetbar{" +
                    "area_code=" + area_code +
                    ", city='" + city + '\'' +
                    ", netbarList=" + netbars +
                    '}';
        }

        public int getArea_code() {
            return area_code;
        }

        public void setArea_code(int area_code) {
            this.area_code = area_code;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public CityNetbar(int area_code, String city) {
            super();
            this.area_code = area_code;
            this.city = city;
        }

        public CityNetbar() {
            super();
        }

    }

    public interface CorpsAction {
        void onJoinCorps(Corps corps);
    }

    public interface SpinnerInterface {
        void onItemSelected(boolean isParent, int position);
    }

    protected void cityItemOnclick(int position) {
        if (position != 0) {
            selectCity(position);
        } else {
            selectAllCity();
        }
    }

    private void selectAllCity() {
        areaCode = "";
        netbarId = "";
        spinner_city.closed();
        page = 1;
        requestCorps();
    }

    private void selectCity(int position) {
        CityNetbar cn = cityNetbarList.get(position - 1);
        netbarList.clear();
        netbarList.add(new InternetBarInfo("-1", "全部网吧"));
        netbarList.addAll(netbarList.size(), cn.getNetbars());
        netbarAdapter.notifyDataSetChanged();
        spinner_netbar.setSelection(0);
        spinner_city.setSelection(position);
        areaCode = cn.getArea_code() + "";
        netbarId = "";
        page = 1;
        requestCity(cn.getArea_code() + "");
        spinner_city.closed();
    }

    protected void netBarItemOnclick(int position) {
        if (position != 0) {
            selectNetbar(position);
        } else {
            selectAllNetbar();
        }
    }

    private void selectAllNetbar() {
        if (!areaCode.equals("")) {
            spinner_netbar.setSelection(0);
            spinner_netbar.closed();
            netbarId = "";
            page = 1;
            requestCity(areaCode);
        } else {
            spinner_netbar.closed();
            netbarId = "";
            page = 1;
        }
    }

    private void selectNetbar(int position) {
        int index = netbar_city.get(netbarList.get(position)
                .getId());
        netbarId = netbarList.get(position).getId() + "";
        areaCode = cityNetbarList.get(index - 1).getArea_code() + "";
        page = 1;
        requestNetbar(netbarList.get(position).getId() + "");
        if (areaCode.equals("")) {
            CityNetbar cn = cityNetbarList.get(index - 1);
            netbarList.clear();
            netbarList.add(new InternetBarInfo("-1", "全部网吧"));
            netbarList
                    .addAll(netbarList.size(), cn.getNetbars());
            netbarAdapter.notifyDataSetChanged();
            for (int i = 0; i < cn.getNetbars().size(); i++) {
                if (cn.getNetbars().get(i).getId()
                        .equals(netbarId)) {
                    position = i;
                }
            }
        }
        spinner_city.setSelection(index);
        spinner_netbar.setSelection(position);
        spinner_netbar.closed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        user = WangYuApplication.getUser(context);
        if (corpsAdapter.getCount() <= 0) {
            return;
        }
        if (user != null) {
            Corps corps = corpsList.get(position);
            Intent intent = new Intent(context, CorpsDetailsV2Activity.class);
            intent.putExtra("teamId", corps.getTeam_Id());
            intent.putExtra("matchId", matchId);
            intent.putExtra("isJoin", corps.getIs_join());
            context.startActivity(intent);
        } else {
            intent = new Intent();
            intent.setClass(context, LoginActivity.class);
            context.startActivity(intent);
        }

    }
}
