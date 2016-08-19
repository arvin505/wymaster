package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.entity.CardCompat;
import com.miqtech.master.client.ui.baseactivity.RedbagBaseActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.ui.fragment.FragmentMyCard;
import com.miqtech.master.client.ui.fragment.FragmentMyCurrentRedBag;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的红包
 * Created by Administrator on 2015/12/4.
 */
public class MyRedBagActivity extends RedbagBaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener,
        FragmentMyCurrentRedBag.OnHeadlineSelectedListener {

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.llCurrentRedBag)
    LinearLayout llCurrentRedBag;
    @Bind(R.id.llHistoryRedBag)
    LinearLayout llHistoryRedBag;

    @Bind(R.id.tvCurrentRedBag)
    TextView tvCurrentRedBag;

    @Bind(R.id.tv_my_card)
    TextView tvMyCard;

    @Bind(R.id.img_current_select_bot)
    ImageView img_current_select_bot;
    @Bind(R.id.img_history_select_bot)
    ImageView img_history_select_bot;

    @Bind(R.id.tv_show_history_card)
    TextView tvHistoryCard;

    private Context context;
    private Intent intent;
    private FragmentPagerAdpter adpter;
    private MyBaseFragment fragment;

    private boolean once = false;

    private int mBottomHeight;

    private Bundle bundle;

    private CardCompat mCard;
    private float amount;
    private String netbarId;

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_myredbag);
        ButterKnife.bind(this);
        context = this;
        getData();
        initView();
    }

    private void getData() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            mCard = bundle.getParcelable("redBag");
            netbarId = bundle.getString("netbarid");
            amount = bundle.getFloat("amount");
        }
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
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("红包卡券");
        getRightTextview().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        int padding = Utils.dip2px(this, 5);
        getRightTextview().setPadding(padding, padding, padding, padding);
        getButtomLineView().setVisibility(View.GONE);

/*        red_btn = (ImageView) findViewById(R.id.ibRight);
        red_btn.setImageResource(R.drawable.red_bag_icon);
        red_btn.setVisibility(View.VISIBLE);
        red_btn.setOnClickListener(this);*/

        setRightTextView("规则");
        getRightTextview().setOnClickListener(this);
        getRightTextview().setVisibility(View.VISIBLE);


        adpter = new FragmentPagerAdpter(this);
        Bundle bundle = new Bundle();
        bundle.putInt("type", TYPE_AVAILBE);
        if (mCard != null) {
            bundle.putParcelable("card", mCard);
            bundle.putInt("VIEW_TYPE", 1);
            setLeftIncludeTitle("选择红包卡券");
        }
        if (amount != 0) {
            bundle.putFloat("amount", amount);
        }
        if (netbarId != null) {
            bundle.putString("netbarid", netbarId);
        }
        adpter.addTab(FragmentMyCurrentRedBag.class, bundle);
        adpter.addTab(FragmentMyCard.class, bundle);

        viewPager.setAdapter(adpter);
        viewPager.addOnPageChangeListener(this);
        getLeftBtn().setOnClickListener(this);
        llCurrentRedBag.setOnClickListener(this);
        llHistoryRedBag.setOnClickListener(this);

        tvCurrentRedBag.setTextColor(getResources().getColor(R.color.orange));
        tvMyCard.setTextColor(getResources().getColor(R.color.gray));
        img_current_select_bot.setVisibility(View.VISIBLE);
        img_history_select_bot.setVisibility(View.GONE);

        tvHistoryCard.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesUtil.saveRedbagStatue(context, false);
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
        setTitleStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.llCurrentRedBag:
                setTitleStatus();
                viewPager.setCurrentItem(0);
                break;
            case R.id.llHistoryRedBag:
                setTitleStatus();
                viewPager.setCurrentItem(1);
                break;
            case R.id.tvRightHandle:
                intent = new Intent(context, SubjectActivity.class);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.HOW2USEREDBAG);
                context.startActivity(intent);
                break;
            case R.id.tv_show_history_card:
                intent = new Intent(context, MyHistoryRedBagActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setTitleStatus() {
        if (viewPager.getCurrentItem() == 0) {
            tvCurrentRedBag.setTextColor(getResources().getColor(R.color.orange));
            tvMyCard.setTextColor(getResources().getColor(R.color.gray));
            img_current_select_bot.setVisibility(View.VISIBLE);
            img_history_select_bot.setVisibility(View.GONE);
        } else {
            tvCurrentRedBag.setTextColor(getResources().getColor(R.color.gray));
            tvMyCard.setTextColor(getResources().getColor(R.color.orange));
            img_current_select_bot.setVisibility(View.GONE);
            img_history_select_bot.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onArticleSelected(int position) {
        System.out.println(position);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!once) {
            once = true;
            mBottomHeight = tvHistoryCard.getMeasuredHeight();
            tvHistoryCard.setVisibility(View.VISIBLE);
            if (mCard != null) {
                viewPager.setCurrentItem(mCard.cardType);
                tvHistoryCard.setVisibility(View.GONE);
            }
        }
    }

    public void hiddenBottom(boolean show) {
        if (show) {
            tvHistoryCard.setVisibility(View.GONE);
        } else {
            tvHistoryCard.setVisibility(View.VISIBLE);
        }
    }
}
