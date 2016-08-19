package com.miqtech.master.client.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.fragment.FragmentMyReward;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaoyi on 2016/8/3.
 * 我的悬赏令
 */

public class MyRewardActivity extends BaseActivity {
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.llMyRecreation)
    LinearLayout llMyRecreation;
    @Bind(R.id.llMyJoin)
    LinearLayout llMyJoin;
    @Bind(R.id.tvRecreation)
    TextView tvRecreation;
    @Bind(R.id.tvJoin)
    TextView tvJoin;
    @Bind(R.id.img_recreation_select_bot)
    ImageView img_recreation_select_bot;
    @Bind(R.id.img_join_select_bot)
    ImageView img_join_select_bot;

    private FragmentPagerAdpter adpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myreward);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        setLeftIncludeTitle("我的悬赏令");
        getLeftBtn().setImageResource(R.drawable.back);
        adpter = new FragmentPagerAdpter(this);
        Bundle bundle = new Bundle();
        bundle.putInt("status", 0);
        adpter.addTab(FragmentMyReward.class, bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("status", 1);
        adpter.addTab(FragmentMyReward.class, bundle2);
        viewPager.setAdapter(adpter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setTitleStatus();
            }
        });
    }

    private void setTitleStatus() {
        if (viewPager.getCurrentItem() == 0) {
            tvRecreation.setTextColor(getResources().getColor(R.color.orange));
            tvJoin.setTextColor(getResources().getColor(R.color.gray));
            img_recreation_select_bot.setVisibility(View.VISIBLE);
            img_join_select_bot.setVisibility(View.GONE);
        } else if (viewPager.getCurrentItem() == 1) {
            tvRecreation.setTextColor(getResources().getColor(R.color.gray));
            tvJoin.setTextColor(getResources().getColor(R.color.orange));

            img_recreation_select_bot.setVisibility(View.GONE);
            img_join_select_bot.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.ibLeft, R.id.tvRecreation, R.id.tvJoin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.tvRecreation:
                setTitleStatus();
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.tvJoin:
                setTitleStatus();
                viewPager.setCurrentItem(1, true);
                break;
        }
    }

}
