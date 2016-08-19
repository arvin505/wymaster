package com.miqtech.master.client.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.basefragment.BaseFragment;

/**
 * Created by admin on 2016/3/8.
 */
public class FragmentInternetbarInfo extends BaseFragment{
    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_netbar_baseinfo, container, false);
    }
}
