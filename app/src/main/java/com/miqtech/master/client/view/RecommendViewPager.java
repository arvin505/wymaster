package com.miqtech.master.client.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.antonyt.infiniteviewpager.InfinitePagerAdapter;
import com.antonyt.infiniteviewpager.InfiniteViewPager;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.RecommendPagerAdapter;
import com.miqtech.master.client.entity.RecommendInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.ScrollController;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/24.
 */
public class RecommendViewPager extends FrameLayout {

    private List<RecommendInfo> list = new ArrayList<>();
    private InfiniteViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    public RecommendViewPager(Context context) {
        super(context);
        init(context);
    }

    public RecommendViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecommendViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_recommend_pager, this);
        findViews();
    }

    private void findViews() {
        mViewPager = (InfiniteViewPager) findViewById(R.id.vp_recommend);
        mViewPager.setPageMargin(-Utils.getScreenWidth(getContext()) / 3);

        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {

            @Override
            public void transformPage(View page, float position) {
                // TODO Auto-generated method stub
                Log.e("tag", position + "   pos");
                if (Math.abs(position) == 1) {
                    position = 1;
                }
                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);
            }
        });
    }

    /**
     * 初始化数据
     *
     * @param feedList
     */
    public void refreshData(List<RecommendInfo> feedList) {
        if (feedList == null || feedList.isEmpty()) {
            return;
        }
        list.clear();
        list.addAll(feedList);
        mPagerAdapter = new InfinitePagerAdapter(new RecommendPagerAdapter(getContext(), list));
        mViewPager.setOffscreenPageLimit(list.size());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setFocusable(true);
        mViewPager.setFocusableInTouchMode(true);
        mViewPager.requestFocus();
        ScrollController.addViewPager(getClass().getSimpleName(),mViewPager);
        this.setVisibility(VISIBLE);
    }

    private List<View> createViews(List<RecommendInfo> list){
        List<View> views = new ArrayList<>();
        for (RecommendInfo match : list){
            View view = null;
            view = View.inflate(getContext(), R.layout.layout_matchshlv_item, null);
            TextView tvType = (TextView) view.findViewById(R.id.tvType);
            TextView tvGameStatus = (TextView) view.findViewById(R.id.tvGameStatus);
            TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            TextView tvBeginTime = (TextView) view.findViewById(R.id.tvBeginTime);
            TextView tvHasApplyNum = (TextView) view.findViewById(R.id.tvHasApplyNum);
            ImageView ivImg = (ImageView) view.findViewById(R.id.ivImg);
            if(match != null){
                AsyncImage.loadPhoto(getContext(), HttpConstant.SERVICE_UPLOAD_AREA + match.getIcon(), ivImg);
                if (match.getWay() == 1) {
                    tvType.setText("官方活动/线下");
                } else if (match.getWay() == 2) {
                    tvType.setText("官方活动/线上");
                }
                tvGameStatus.setVisibility(View.GONE);
                tvTitle.setText(match.getTitle());
                tvBeginTime.setText(TimeFormatUtil.formatNoSecond(match.getStart_time()));
                tvHasApplyNum.setText(match.getApplyNum());
                //holder.tvSummary.setText(match.getSummary());
                views.add(view);
            }
        }
        return views;
    }
}
