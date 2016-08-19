package com.miqtech.master.client.ui.basefragment;

import com.miqtech.master.client.entity.FilterInfo;

/**
 * Created by Administrator on 2015/12/1.
 */
public abstract class AmuseGameBaseFragment extends BaseFragment {

    public abstract void loadDataWithFilter(FilterInfo filterInfo);

    public abstract void setFilterText(String filter);


}
