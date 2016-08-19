package com.miqtech.master.client.ui.basefragment;

import android.view.LayoutInflater;
import android.view.View;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.SearchActivity;

/**
 * Created by Administrator on 2015/12/30.
 */
public abstract class SearchBaseFragment extends BaseFragment {

    public abstract void search(String key);

    public abstract void changeText(String key);

    public boolean searchIng = false;

    public View getSearchHeadView() {
        View headView = null;
        headView = LayoutInflater.from(getContext()).inflate(R.layout.layout_search_header, null);
        headView.setClickable(false);
        return headView;
    }

    public void setSearchKey(String key) {
        try {
            if (key != null) {
                ((SearchActivity) getActivity()).setEtSearch(key);
            }else {
                ((SearchActivity) getActivity()).setEtSearch("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
