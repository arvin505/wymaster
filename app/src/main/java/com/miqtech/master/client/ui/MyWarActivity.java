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
import com.miqtech.master.client.ui.fragment.FragmentMyWarApply;
import com.miqtech.master.client.ui.fragment.FragmentMyWarRelease;
import com.miqtech.master.client.utils.PreferencesUtil;

/**
 * Created by Administrator on 2015/12/7.
 */
public class MyWarActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager viewPager;
    private LinearLayout llMyRelease;
    private LinearLayout llMyApply;
    private TextView tvMyRelease;
    private TextView tvMyApply;
    private ImageView imgWar;
    private ImageView imgGame;
    private int TageFragment = 0;
    private MyBaseFragment fragment;
    private FragmentPagerAdpter adpter;

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_mywar);
        initView();
    }

    @Override
    protected void initData() {
        PreferencesUtil.saveMatchStatue(this, false);
        super.initData();
        int index = viewPager.getCurrentItem();
        fragment = (MyBaseFragment)adpter.getItem(index);
        fragment.refreView();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        TageFragment = getIntent().getIntExtra("war",0);
        setLeftBtnImage(R.drawable.back);
        ImageView rlBack = getLeftBtn();
        setLeftIncludeTitle("我的约战");
        getButtomLineView().setVisibility(View.GONE);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        llMyRelease = (LinearLayout) findViewById(R.id.llMyRelease);
        llMyApply = (LinearLayout) findViewById(R.id.llMyApply);
        tvMyApply = (TextView) findViewById(R.id.tvMyApply);
        tvMyRelease = (TextView) findViewById(R.id.tvMyRelease);
        imgWar = (ImageView) findViewById(R.id.img_war_select_bot);
        imgGame = (ImageView) findViewById(R.id.img_game_select_bot);

        adpter = new FragmentPagerAdpter(this);
        adpter.addTab(FragmentMyWarRelease.class, null);
        adpter.addTab(FragmentMyWarApply.class, null);
        viewPager.setAdapter(adpter);
        viewPager.setOnPageChangeListener(this);
        rlBack.setOnClickListener(this);
        llMyRelease.setOnClickListener(this);
        llMyApply.setOnClickListener(this);

        tvMyRelease.setTextColor(getResources().getColor(R.color.orange));
        tvMyApply.setTextColor(getResources().getColor(R.color.gray));
        imgWar.setVisibility(View.VISIBLE);
        imgGame.setVisibility(View.GONE);
        viewPager.setCurrentItem(TageFragment );
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
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.llMyRelease:
                viewPager.setCurrentItem(0);
                setTitleStatus();
                break;
            case R.id.llMyApply:
                viewPager.setCurrentItem(1);
                setTitleStatus();
                break;
            default:
                break;
        }
    }

    private void setTitleStatus() {
        if (viewPager.getCurrentItem() == 0) {
            tvMyRelease.setTextColor(getResources().getColor(R.color.orange));
            tvMyApply.setTextColor(getResources().getColor(R.color.gray));
            imgWar.setVisibility(View.VISIBLE);
            imgGame.setVisibility(View.GONE);
        } else {
            tvMyRelease.setTextColor(getResources().getColor(R.color.gray));
            tvMyApply.setTextColor(getResources().getColor(R.color.orange));
            imgWar.setVisibility(View.GONE);
            imgGame.setVisibility(View.VISIBLE);
        }
    }

}
