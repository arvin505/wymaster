package com.miqtech.master.client.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.miqtech.master.client.entity.MatchSchedule;
import com.miqtech.master.client.utils.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/1/21.
 */
public class CorpsFilterPagerAdapter extends PagerAdapter {

    private List<View> mViews;

    private List<MatchSchedule> mMatchSchedules;

    public CorpsFilterPagerAdapter(List<View> views, List<MatchSchedule> data) {
        this.mViews = views;
        this.mMatchSchedules = data;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position), 0);
        return mViews.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return DateUtil.strToDatePinYin(mMatchSchedules.get(position).getDate());
    }
}
