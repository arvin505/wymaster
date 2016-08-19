package com.miqtech.master.client.adapter;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.miqtech.master.client.entity.InforCatalog;
import com.miqtech.master.client.ui.basefragment.BaseFragment;


import java.util.List;

/**
 * Created by Administrator on 2016/4/8.
 */
public class FragmentInfomationPagerAdapter extends FragmentStatePagerAdapter {

    private FragmentManager mFragmentManager;
    private List<BaseFragment> mFragments;
    private List<InforCatalog> mTitles;

    public FragmentInfomationPagerAdapter(FragmentManager fm, List<BaseFragment> fragments, List<InforCatalog> titles) {
        super(fm);
        mFragmentManager = fm;
        mFragments = fragments;
        mTitles = titles;
    }

    public void updateFragments(List<BaseFragment> fragments, List<InforCatalog> titles) {
        for (int i = 0; i < mFragments.size(); i++) {
            final BaseFragment fragment = mFragments.get(i);
            final FragmentTransaction ft = mFragmentManager.beginTransaction();
            if (i > 2) {
                ft.remove(fragment);
                mFragments.remove(i);
                i--;
            }
            ft.commit();
        }
        for (int i = 0; i < fragments.size(); i++) {
            if (i > 2) {
                mFragments.add(fragments.get(i));
            }
        }
        this.mTitles = titles;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (position >= mTitles.size()) {
            return "";
        }
        return mTitles.get(position).getParent().getName();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }
}
