package com.miqtech.master.client.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2015/11/23.
 * 首页腰图1 viewpager adapter
 */
public class Mid1VpAdapter extends PagerAdapter {

    private List<View> mViewList;

    public Mid1VpAdapter(List<View> views) {
        this.mViewList = views;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override

    public Object instantiateItem(ViewGroup view, int position) {
        view.addView(mViewList.get(position));
        return mViewList.get(position);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        ((ViewPager) container).removeView(view);
        view = null;
    }
}
