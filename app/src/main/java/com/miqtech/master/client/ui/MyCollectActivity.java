package com.miqtech.master.client.ui;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.ui.fragment.FragmentActivityCollect;
import com.miqtech.master.client.ui.fragment.FragmentGameCollect;
import com.miqtech.master.client.ui.fragment.FragmentMyInfo;
import com.miqtech.master.client.ui.fragment.FragmentNetBarCollect;

/**
 * 我的收藏
 * Created by Administrator on 2015/12/4.
 */
public class MyCollectActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager viewPager;

    private LinearLayout llMyBar;
    private LinearLayout llMyGame;
    private LinearLayout llMyInfo;
    private LinearLayout llMyActivity;

    private TextView tvMyBar;
    private TextView tvMyGame;
    private TextView tvMyInfo;
    private TextView tvActivity;

    private ImageView img_bar_select_bot;
    private ImageView img_game_select_bot;
    private ImageView img_info_select_bot;
    private ImageView img_activity_select_bot;

    private FragmentPagerAdpter adpter;

    private MyBaseFragment fragment;

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_mycollect);
        initView();
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        int index = viewPager.getCurrentItem();
        fragment = (MyBaseFragment) adpter.getItem(index);
        fragment.refreView();
    }

    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        llMyBar = (LinearLayout) findViewById(R.id.llNetBar);
        llMyGame = (LinearLayout) findViewById(R.id.llMyGame);
        llMyInfo = (LinearLayout) findViewById(R.id.llMyInfo);
        llMyActivity = (LinearLayout) findViewById(R.id.llActivity);
        tvMyBar = (TextView) findViewById(R.id.tvNetBar);
        tvMyGame = (TextView) findViewById(R.id.tvMyGame);
        tvMyInfo = (TextView) findViewById(R.id.tvMyInfo);
        tvActivity = (TextView) findViewById(R.id.tvActivity);
        img_bar_select_bot = (ImageView) findViewById(R.id.img_bar_select_bot);
        img_game_select_bot = (ImageView) findViewById(R.id.img_game_select_bot);
        img_info_select_bot = (ImageView) findViewById(R.id.img_info_select_bot);
        img_activity_select_bot = (ImageView) findViewById(R.id.img_activity_select_bot);
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle(getResources().getString(R.string.my_collect));
        getButtomLineView().setVisibility(View.GONE);
        adpter = new FragmentPagerAdpter(this);
        viewPager.setOffscreenPageLimit(4);
        adpter.addTab(FragmentNetBarCollect.class, null);
        adpter.addTab(FragmentActivityCollect.class, null);
        adpter.addTab(FragmentMyInfo.class, null);
        adpter.addTab(FragmentGameCollect.class, null);
        viewPager.setAdapter(adpter);
        viewPager.setOnPageChangeListener(this);
        getLeftBtn().setOnClickListener(this);
        llMyBar.setOnClickListener(this);
        llMyGame.setOnClickListener(this);
        llMyInfo.setOnClickListener(this);
        llMyActivity.setOnClickListener(this);
        tvMyBar.setTextColor(getResources().getColor(R.color.orange));
        tvMyInfo.setTextColor(getResources().getColor(R.color.gray));
        tvMyGame.setTextColor(getResources().getColor(R.color.gray));
        tvActivity.setTextColor(getResources().getColor(R.color.gray));
        img_bar_select_bot.setVisibility(View.VISIBLE);
        img_info_select_bot.setVisibility(View.GONE);
        img_game_select_bot.setVisibility(View.GONE);
        img_activity_select_bot.setVisibility(View.GONE);

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        setTitleStatus();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        try{
            switch (v.getId()) {
                case R.id.ibLeft:
                    onBackPressed();
                    break;
                case R.id.llNetBar:
                    setTitleStatus();
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.llMyInfo:
                    setTitleStatus();
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.llMyGame:
                    setTitleStatus();
                    viewPager.setCurrentItem(3);
                    break;
                case R.id.llActivity:
                    setTitleStatus();
                    viewPager.setCurrentItem(1);
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setTitleStatus() {
        if (viewPager.getCurrentItem() == 0) {
            tvMyBar.setTextColor(getResources().getColor(R.color.orange));
            tvMyInfo.setTextColor(getResources().getColor(R.color.gray));
            tvMyGame.setTextColor(getResources().getColor(R.color.gray));
            tvActivity.setTextColor(getResources().getColor(R.color.gray));
            img_bar_select_bot.setVisibility(View.VISIBLE);
            img_info_select_bot.setVisibility(View.GONE);
            img_game_select_bot.setVisibility(View.GONE);
            img_activity_select_bot.setVisibility(View.GONE);

        } else if (viewPager.getCurrentItem() == 1) {
            tvMyBar.setTextColor(getResources().getColor(R.color.gray));
            tvMyInfo.setTextColor(getResources().getColor(R.color.gray));
            tvActivity.setTextColor(getResources().getColor(R.color.orange));
            tvMyGame.setTextColor(getResources().getColor(R.color.gray));
            img_bar_select_bot.setVisibility(View.GONE);
            img_info_select_bot.setVisibility(View.GONE);
            img_activity_select_bot.setVisibility(View.VISIBLE);
            img_game_select_bot.setVisibility(View.GONE);
        } else if (viewPager.getCurrentItem() == 2) {
            tvMyBar.setTextColor(getResources().getColor(R.color.gray));
            tvActivity.setTextColor(getResources().getColor(R.color.gray));
            tvMyInfo.setTextColor(getResources().getColor(R.color.orange));
            tvMyGame.setTextColor(getResources().getColor(R.color.gray));
            img_bar_select_bot.setVisibility(View.GONE);
            img_info_select_bot.setVisibility(View.VISIBLE);
            img_game_select_bot.setVisibility(View.GONE);
            img_activity_select_bot.setVisibility(View.GONE);
        } else {
            tvMyBar.setTextColor(getResources().getColor(R.color.gray));
            tvMyInfo.setTextColor(getResources().getColor(R.color.gray));
            tvActivity.setTextColor(getResources().getColor(R.color.gray));
            tvMyGame.setTextColor(getResources().getColor(R.color.orange));
            img_bar_select_bot.setVisibility(View.GONE);
            img_info_select_bot.setVisibility(View.GONE);
            img_game_select_bot.setVisibility(View.VISIBLE);
            img_activity_select_bot.setVisibility(View.GONE);
        }
    }

}
