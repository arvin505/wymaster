package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.ui.fragment.FragmentMyCorps;
import com.miqtech.master.client.ui.fragment.FragmentMyJoinMatch;
import com.miqtech.master.client.ui.fragment.FragmentMyAmuse;
import com.miqtech.master.client.ui.fragment.FragmentMyReleaseMatch;
import com.miqtech.master.client.utils.PreferencesUtil;

/**
 * 我的赛事
 * Created by Administrator on 2015/12/5.
 */
public class MyMatchActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private Context context;
    private int page = 1;
    private int isLast;
    private ViewPager viewPager;
    private LinearLayout llJoin, llRecreation;
    private TextView tvJoin, tvCorps, tvRecreation;
    private ImageView imgJoin, imgCorps, imgRecreation;
    private FragmentPagerAdpter adpter;
    public final static int Type_Join = 1;
    public final static int Type_Corps = 2;
    private FragmentMyJoinMatch myJoinMatchFragment;
    private FragmentMyCorps myCorpsFragment;
    private MyBaseFragment fragment;

    public FragmentMyJoinMatch getMyJoinMatchFragment() {
        return myJoinMatchFragment;
    }

    public void setMyJoinMatchFragment(FragmentMyJoinMatch myJoinMatchFragment) {
        this.myJoinMatchFragment = myJoinMatchFragment;
    }

    public FragmentMyCorps getMyCorpsFragment() {
        return myCorpsFragment;
    }

    public void setMyCorpsFragment(FragmentMyCorps myCorpsFragment) {
        this.myCorpsFragment = myCorpsFragment;
    }


    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_mymatch);
        context = this;
        initView();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        setLeftIncludeTitle("我的赛事");
        getLeftBtn().setOnClickListener(this);
        setLeftBtnImage(R.drawable.back);
        getButtomLineView().setVisibility(View.GONE);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        llRecreation = (LinearLayout) findViewById(R.id.llMyRecreation);
        llJoin = (LinearLayout) findViewById(R.id.llMyJoin);


        tvRecreation = (TextView) findViewById(R.id.tvRecreation);
        tvJoin = (TextView) findViewById(R.id.tvJoin);
        tvCorps = (TextView) findViewById(R.id.tvCorps);

        imgRecreation = (ImageView) findViewById(R.id.img_recreation_select_bot);
        imgJoin = (ImageView) findViewById(R.id.img_join_select_bot);
        imgCorps = (ImageView) findViewById(R.id.img_corps_select_bot);

        adpter = new FragmentPagerAdpter(this);
        adpter.addTab(FragmentMyReleaseMatch.class, null);
        adpter.addTab(FragmentMyAmuse.class, null);
        //adpter.addTab(FragmentMyCorps.class, null);
        viewPager.setAdapter(adpter);
        viewPager.addOnPageChangeListener(this);
        getLeftBtn().setOnClickListener(this);

        llJoin.setOnClickListener(this);

        llRecreation.setOnClickListener(this);

        tvRecreation.setTextColor(getResources().getColor(R.color.orange));
        tvJoin.setTextColor(getResources().getColor(R.color.gray));
        tvCorps.setTextColor(getResources().getColor(R.color.gray));

        imgRecreation.setVisibility(View.VISIBLE);
        imgJoin.setVisibility(View.GONE);
        imgCorps.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesUtil.clearPushStatue(context);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.llMyRecreation:
                viewPager.setCurrentItem(0);
                setTitleStatus();
                break;
            case R.id.llMyJoin:
                viewPager.setCurrentItem(1);
                setTitleStatus();
                break;
            case R.id.llMyCorps:
                viewPager.setCurrentItem(2);
                setTitleStatus();
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageSelected(int arg0) {
        setTitleStatus();
    }

    private void setTitleStatus() {
        if (viewPager.getCurrentItem() == 0) {
            tvRecreation.setTextColor(getResources().getColor(R.color.orange));
            tvJoin.setTextColor(getResources().getColor(R.color.gray));
            tvCorps.setTextColor(getResources().getColor(R.color.gray));
            imgRecreation.setVisibility(View.VISIBLE);
            imgJoin.setVisibility(View.GONE);
            imgCorps.setVisibility(View.GONE);
        } else if (viewPager.getCurrentItem() == 1) {
            tvRecreation.setTextColor(getResources().getColor(R.color.gray));
            tvJoin.setTextColor(getResources().getColor(R.color.orange));
            tvCorps.setTextColor(getResources().getColor(R.color.gray));

            imgRecreation.setVisibility(View.GONE);
            imgJoin.setVisibility(View.VISIBLE);
            imgCorps.setVisibility(View.GONE);
        } else if (viewPager.getCurrentItem() == 2) {
            tvRecreation.setTextColor(getResources().getColor(R.color.gray));
            tvJoin.setTextColor(getResources().getColor(R.color.gray));
            tvCorps.setTextColor(getResources().getColor(R.color.orange));

            imgRecreation.setVisibility(View.GONE);
            imgJoin.setVisibility(View.GONE);
            imgCorps.setVisibility(View.VISIBLE);
        }
    }

    public void refreshPage() {
        getMyJoinMatchFragment().loadMyReleaseWar();
        getMyCorpsFragment().loadMyCorps();
    }

    @Override
    protected void initData() {
        super.initData();
        int index = viewPager.getCurrentItem();
        fragment = (MyBaseFragment) adpter.getItem(index);
        fragment.refreView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int index = viewPager.getCurrentItem();
        if (index == 3 && requestCode == 1 && resultCode == RESULT_OK) {
            fragment = (MyBaseFragment) adpter.getItem(index);
            fragment.refreView();
        }
    }
}
