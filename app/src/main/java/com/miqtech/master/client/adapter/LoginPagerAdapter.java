package com.miqtech.master.client.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by wuxuenan on 2015/11/20 0020.
 */
public class LoginPagerAdapter extends PagerAdapter {
    private Context context;
    private List<View> views;



    public LoginPagerAdapter(Context context,List<View> views) {
        this.context = context ;
        this.views = views ;
    }

    @Override
    public int getCount() {

        if (views != null && views.size() > 0) {
            return views.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void addItemView(View view) {
        views.add(view);
        notifyDataSetChanged();
    }
}
