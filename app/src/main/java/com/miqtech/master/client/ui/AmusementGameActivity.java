package com.miqtech.master.client.ui;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.R;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.FilterInfo;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.basefragment.AmuseGameBaseFragment;
import com.miqtech.master.client.ui.fragment.FragmentGameNetbar;
import com.miqtech.master.client.ui.fragment.FragmentGameOfficialOffline;
import com.miqtech.master.client.ui.fragment.FragmentGameOfficialOnline;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 娱乐比赛
 * Created by Administrator on 2015/11/26.
 */
public class AmusementGameActivity extends BaseActivity implements View.OnClickListener {

    /*@Bind(R.id.tv_city)
    TextView tvCity;*/
    @Bind(R.id.tv_game_netbar)
    TextView tvGameNetbar;   //网吧发起
    @Bind(R.id.tv_game_official_online)
    TextView tvGameOfficialOnline;  //官方线上
    @Bind(R.id.tv_game_official_offline)
    TextView tvGameOfficialOffline; // 官方线下
    @Bind(R.id.vp_game_fragment)
    ViewPager vpGameFragment;   //fragment viewpager

    @Bind(R.id.tvOfflineBottom)
    TextView tvOfflineBottom;
    @Bind(R.id.tvOnlineBottom)
    TextView tvOnlineBottom;
    @Bind(R.id.tv_netbarBottom)
    TextView tv_netbarBottom;


    private FragmentPagerAdpter adapter;
    private List<TextView> tvList = new ArrayList<>();
    private List<View> lineList = new ArrayList<>();
    private String cityName;
    private FilterInfo filterInfo;



    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_amusegame);
        ButterKnife.bind(this);
        initData();
        initView();
        setListner();
    }


    private void setListner() {
        tvGameNetbar.setOnClickListener(this);
        tvGameOfficialOnline.setOnClickListener(this);
        tvGameOfficialOffline.setOnClickListener(this);
        getLeftBtn().setOnClickListener(this);

        //headerIcon.setOnClickListener(this);
    }


    protected void initView() {
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle(getResources().getString(R.string.game_entertain));

        lineList.add(tvOnlineBottom);
        lineList.add(tvOfflineBottom);
        lineList.add(tv_netbarBottom);

        tvList.add(tvGameOfficialOnline);
        tvList.add(tvGameOfficialOffline);
        tvList.add(tvGameNetbar);

        if (Constant.isLocation){
            setCityName(Constant.cityName);
        }

        adapter = new FragmentPagerAdpter(this);
        adapter.addTab(FragmentGameOfficialOnline.class, null);
        adapter.addTab(FragmentGameOfficialOffline.class, null);
        adapter.addTab(FragmentGameNetbar.class, null);
        vpGameFragment.setAdapter(adapter);

        vpGameFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setViewPagerSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setViewPagerSelect(int position) {
        for (int i = 0; i < tvList.size(); i++) {
            if (i != position) {
                tvList.get(i).setTextColor(getResources().getColor(R.color.colorActionBarUnSelected));
                lineList.get(i).setVisibility(View.GONE);
            }
        }
        if (position == 0){
            //tvCity.setVisibility(View.GONE);
        }else {
           // tvCity.setVisibility(View.VISIBLE);
        }
        tvList.get(position).setTextColor(getResources().getColor(R.color.colorActionBarSelected));
        lineList.get(position).setVisibility(View.VISIBLE);


    }

    public void setCityName(String cityName){
        this.cityName = cityName;
       // tvCity.setText(cityName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_game_official_online:
                setViewPagerSelect(0);
                vpGameFragment.setCurrentItem(0, true);
                break;
            case R.id.tv_game_official_offline:
                setViewPagerSelect(1);
                vpGameFragment.setCurrentItem(1, true);
                break;
            case R.id.tv_game_netbar:
                setViewPagerSelect(2);
                vpGameFragment.setCurrentItem(2, true);
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }

    public FilterInfo getFilterInfo() {
        return filterInfo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1){
            filterInfo = data.getBundleExtra("result").getParcelable("filter");
            int index = vpGameFragment.getCurrentItem();
            AmuseGameBaseFragment fragment = (AmuseGameBaseFragment) adapter.getItem(index);
            fragment.loadDataWithFilter(filterInfo);
        }

    }

}
