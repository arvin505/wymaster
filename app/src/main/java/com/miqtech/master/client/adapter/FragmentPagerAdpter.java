package com.miqtech.master.client.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.miqtech.master.client.ui.basefragment.BaseFragment;


public class FragmentPagerAdpter extends FragmentStatePagerAdapter {

    private final Context mContext;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    private List<Fragment> fragments = new ArrayList<>();

    static final class TabInfo {

        private final Class<?> mClss;
        private final Bundle mArgs;

        TabInfo(Class<?> aClass, Bundle args) {
            mClss = aClass;
            mArgs = args;
        }
    }

    public FragmentPagerAdpter(FragmentActivity activity) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        Fragment fragment;
        if (!fragments.isEmpty() && position < fragments.size() && fragments.get(position) != null) {
            fragment = fragments.get(position);
        } else {
//            fragment = BaseFragment.instantiate(mContext.getApplicationContext(), info.mClss.getName(), info.mArgs);
            fragment = BaseFragment.instantiate(mContext, info.mClss.getName(), info.mArgs);
            fragments.add(fragment);
        }
        return fragment;
//        return BaseFragment.instantiate(mContext, info.mClss.getName(), info.mArgs);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }

    public void addTab(Class<?> clss, Bundle args) {
        TabInfo info = new TabInfo(clss, args);
        mTabs.add(info);
        notifyDataSetChanged();
    }

}
