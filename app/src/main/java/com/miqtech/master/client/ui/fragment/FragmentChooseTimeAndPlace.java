package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.CorpsFilterPagerAdapter;
import com.miqtech.master.client.adapter.MatchFilterAreaAdapter;
import com.miqtech.master.client.adapter.MatchFilterNetbarAdapter;
import com.miqtech.master.client.entity.MatchNetbar;
import com.miqtech.master.client.entity.MatchSchedule;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.OfficePopupWindowActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.ScrollController;
import com.miqtech.master.client.view.PagerSlidingTabStrip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 选择时间和地点
 * Created by Administrator on 2016/1/25.
 */
public class FragmentChooseTimeAndPlace extends BaseFragment implements View.OnClickListener {
    private View mainView;
    private PagerSlidingTabStrip vpTabs;
    private ViewPager viewPager;
    private TextView tvClose;
    private TextView tvTitle;
    private TextView tvTotal;
    private TextView tvWhichOne;

    private Context context;
    private List<MatchSchedule> matchScheduleList = new ArrayList<MatchSchedule>();
    private List<View> mViews = new ArrayList<View>();
    private boolean isFirst = true;

    private int netbarId;//网吧ID
    public String matchId = "";//比赛ID
    private String matchTime;
    private String matchAddress;
    private String round;
    private int registrationTypes = -1;//0个人报名，1创建临时战队，2加入临时战队

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainView == null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            mainView = inflater.inflate(R.layout.fragment_choose_time_and_place, null);
            context = inflater.getContext();
            initView();
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            shoeTopBar();
            loadData();
            isFirst = false;
        }
    }

    private void initView() {
        vpTabs = (PagerSlidingTabStrip) mainView.findViewById(R.id.vp_tabs);
        viewPager = (ViewPager) mainView.findViewById(R.id.viewpager);
        tvClose = (TextView) mainView.findViewById(R.id.tv_close_popupwindow);
        tvTitle = (TextView) mainView.findViewById(R.id.tv_title_popupwindow);
        tvTotal = (TextView) mainView.findViewById(R.id.tv_total);
        tvWhichOne = (TextView) mainView.findViewById(R.id.tv_select_which_one);
        tvClose.setOnClickListener(this);
    }

    private void loadData() {
        showLoading();
        Map<String, String> map = new HashMap<>();
        map.put("activityId", matchId);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTVITY_APPLY_DATES_NETBARS_V2, map, HttpConstant.ACTVITY_APPLY_DATES_NETBARS_V2);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            if (method.equals(HttpConstant.ACTVITY_APPLY_DATES_NETBARS_V2)) {
                if (object.has("object")) {
                    List<MatchSchedule> newMatchSchedule = GsonUtil.getList(object.getString("object").toString(), MatchSchedule.class);
                    matchScheduleList.clear();
                    matchScheduleList.addAll(newMatchSchedule);
                    generatePagerView(matchScheduleList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        showToast(errMsg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_close_popupwindow:
                ((OfficePopupWindowActivity) context).onBackPressed();
                break;
        }
    }

    private void generatePagerView(final List<MatchSchedule> data) {
        for (int i = 0; i < data.size(); i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_match_popwindow_filter, null);
            final ListView lvArea = (ListView) view.findViewById(R.id.lv_area);
            ListView lvNetBar = (ListView) view.findViewById(R.id.lv_netbar);
            final MatchFilterAreaAdapter areaAdapter = new MatchFilterAreaAdapter(context, data.get(i).getAreas());
            lvArea.setAdapter(areaAdapter);
            lvArea.setSelection(0);

            if (!data.get(i).getAreas().isEmpty()){
                final MatchFilterNetbarAdapter netbarAdapter = new MatchFilterNetbarAdapter(context, data.get(i).getAreas().get(0).getNetbars());

                lvArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        areaAdapter.setSelectedIndex(position);
                        areaAdapter.notifyDataSetChanged();
                        netbarAdapter.setNetbars(data.get(viewPager.getCurrentItem()).getAreas().get(areaAdapter.getSelectedIndex()).getNetbars());
                        netbarAdapter.setSelectIndex(-1);
                        netbarAdapter.notifyDataSetChanged();
                    }
                });

                lvNetBar.setAdapter(netbarAdapter);
                lvNetBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        netbarAdapter.setSelectIndex(position);
                        netbarAdapter.notifyDataSetChanged();
                        MatchNetbar matchNetbar = matchScheduleList.get(viewPager.getCurrentItem()).getAreas().get(areaAdapter.getSelectedIndex()).getNetbars().get(position);

                        MatchSchedule matchSchedule = matchScheduleList
                                .get(viewPager.getCurrentItem());

                        matchTime = matchSchedule.getDate();
                        round = matchSchedule.getRound() + "";
                        netbarId = matchNetbar.getId();
                        matchAddress = matchNetbar.getAddress();


                        ((OfficePopupWindowActivity) context).setNetbarId(netbarId);
                        ((OfficePopupWindowActivity) context).setMatchAddress(matchAddress);
                        ((OfficePopupWindowActivity) context).setRound(round);
                        ((OfficePopupWindowActivity) context).setMatchTime(DateUtil.strToDatePinYin(matchTime));

                        int status = matchScheduleList.get(viewPager.getCurrentItem()).getStatus();
                        if (status == 1 || status == 2) {
                            ((OfficePopupWindowActivity) context).setSelectFragment(1);
                        }/*else if (status == 2){
                        showToast("所选日期未开始报名");
                    }*/ else if (status == 3) {
                            showToast("所选日期已停止报名");
                        } else if (status == 4) {
                            //赛事结束
                            showToast("当日比赛已结束，请选择其他日期");
                        } else if (status == 5) {
                            //赛事进行中
                            showToast("赛事已经开始，请选择其他日期");
                        }

                    }
                });
            }
            mViews.add(view);
        }

        ScrollController.addViewPager(getClass().getSimpleName() + "_content", viewPager);
        CorpsFilterPagerAdapter adapter = new CorpsFilterPagerAdapter(mViews, matchScheduleList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        vpTabs.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    private void shoeTopBar() {
        matchId = ((OfficePopupWindowActivity) context).getMatchId();
        registrationTypes = ((OfficePopupWindowActivity) context).getRegistrationTypes();
        tvWhichOne.setText("1");
        tvClose.setText("关闭");
        tvTitle.setText("选择日期与地点");
        if (registrationTypes == 0) {
            tvTotal.setText("/3");
        } else if (registrationTypes == 1 || registrationTypes == 2) {
            tvTotal.setText("/4");
        }
    }

}
