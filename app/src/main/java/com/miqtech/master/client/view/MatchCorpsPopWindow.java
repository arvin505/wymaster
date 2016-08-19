package com.miqtech.master.client.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.CorpsFilterAreaAdapter;
import com.miqtech.master.client.adapter.CorpsFilterNetbarAdapter;
import com.miqtech.master.client.adapter.CorpsFilterPagerAdapter;
import com.miqtech.master.client.entity.MatchSchedule;
import com.miqtech.master.client.utils.LogUtil;

import java.util.ArrayList;

import java.util.List;


/**
 * Created by Administrator on 2016/1/21.
 */
public class MatchCorpsPopWindow extends PopupWindow implements View.OnClickListener {

    private PagerSlidingTabStrip tabStrip;

    private ViewPager viewPager;

    private Button btCancle;

    private Button btOk;

    private Context mContext;

    private List<MatchSchedule> mMatchSchedules;

    private List<View> mViews = new ArrayList<>();

    private CorpsFilterPagerAdapter adapter;

    private FilterCallback mCallback;

    private int matchId = -1;

    private long netbarId = -1;

    private int areaId = -1;

    private List<Selected> selectedList = new ArrayList<>();

    public MatchCorpsPopWindow(Context context, List<MatchSchedule> data, FilterCallback callback) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.mContext = context;
        this.mMatchSchedules = data;
        initView();

        this.mCallback = callback;
        generatePagerView(data);

        matchId = data.get(0).getRound();
        netbarId = data.get(0).getAreas().get(0).getNetbars().get(0).getId();
        netbarname = data.get(0).getAreas().get(0).getNetbars().get(0).getName();


    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_match_popwindow, null);
        findViews(view);
        setContentView(view);
        setListener();

    }

    private void findViews(View view) {
        tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.vp_tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        btCancle = (Button) view.findViewById(R.id.btn_cancle);
        btOk = (Button) view.findViewById(R.id.btn_ok);
    }


    private void setListener() {
        btCancle.setOnClickListener(this);
        btOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
                mCallback.callback(-1, -1, -1);
                dismiss();
                break;
            case R.id.btn_ok:
                dismiss();
                int index = viewPager.getCurrentItem();
                Selected selected = selectedList.get(index);
                if (!mMatchSchedules.get(index).getAreas().get(selected.areaSelected).getNetbars().isEmpty()) {
                    netbarId = mMatchSchedules.get(index).getAreas().get(selected.areaSelected).getNetbars().get(selected.netbarSelected).getId();
                    netbarname = mMatchSchedules.get(index).getAreas().get(selected.areaSelected).getNetbars().get(selected.netbarSelected).getName();
                    areaId = selected.areaSelected;
                    mCallback.callback(matchId, netbarId, areaId);
                    LogUtil.e("netbasr", "netbar : " + netbarname + "   id : " + netbarId + "  match id : " + matchId);
                }
                break;
        }
    }

    private String netbarname;

    private void setSelected(int area, int netbar) {
        Selected selected = selectedList.get(viewPager.getCurrentItem());
        selected.areaSelected = area;
        selected.netbarSelected = netbar;
    }

    private void generatePagerView(final List<MatchSchedule> data) {

        for (int i = 0; i < data.size(); i++) {

            Selected selected = new Selected();
            selectedList.add(selected);

            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_corpspopwindow_filter, null);
            final ListView lvArea = (ListView) view.findViewById(R.id.lv_area);
            ListView lvNetBar = (ListView) view.findViewById(R.id.lv_netbar);
            final CorpsFilterAreaAdapter areaAdapter = new CorpsFilterAreaAdapter(mContext, data.get(i).getAreas());
            lvArea.setAdapter(areaAdapter);
            lvArea.setSelection(0);
            final CorpsFilterNetbarAdapter netbarAdapter = new CorpsFilterNetbarAdapter(mContext, data.get(i).getAreas().get(0).getNetbars());

            lvArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    areaAdapter.setSelectedIndex(position);
                    areaAdapter.notifyDataSetChanged();
                    netbarAdapter.setNetbars(data.get(viewPager.getCurrentItem()).getAreas().get(areaAdapter.getSelectedIndex()).getNetbars());
                    netbarAdapter.setSelectIndex(0);
                    netbarAdapter.notifyDataSetChanged();
                    netbarId = data.get(viewPager.getCurrentItem()).getAreas().get(areaAdapter.getSelectedIndex()).getNetbars().get(0).getId();
                    setSelected(position, 0);
                }
            });

            lvNetBar.setAdapter(netbarAdapter);
            lvNetBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    netbarAdapter.setSelectIndex(position);
                    netbarAdapter.notifyDataSetChanged();
                    netbarId = mMatchSchedules.get(viewPager.getCurrentItem()).getAreas().get(areaAdapter.getSelectedIndex()).getNetbars().get(position).getId();
                    netbarname = mMatchSchedules.get(viewPager.getCurrentItem()).getAreas().get(areaAdapter.getSelectedIndex()).getNetbars().get(position).getName();
                    setSelected(areaAdapter.getSelectedIndex(), position);
                }
            });

            if (netbarAdapter.getCount() < areaAdapter.getCount()) {
                fixListViewHeight(lvArea);
            } else {
                fixListViewHeight(lvNetBar);
            }

            mViews.add(view);
        }

        adapter = new CorpsFilterPagerAdapter(mViews, mMatchSchedules);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        tabStrip.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                matchId = mMatchSchedules.get(position).getRound();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void fixListViewHeight(AbsListView listView) {      // 如果没有设置数据适配器，则ListView没有子项，返回。
        try {
            ListAdapter listAdapter = listView.getAdapter();
            int totalHeight = 0;
            if (listAdapter == null) {
                return;
            }

            for (int i = 0, len = listAdapter.getCount(); i < len && i < 4; i++) {
                View listViewItem = listAdapter.getView(i, null, listView);
                // 计算子项View 的宽高
                listViewItem.measure(0, 0);
                // 计算所有子项的高度和
                totalHeight += listViewItem.getMeasuredHeight();
            }
            View childView = listAdapter.getView(0, null, listView);
            childView.measure(0, 0);
           /* int childHeight = childView.getMeasuredHeight();
            totalHeight = childHeight * listAdapter.getCount();*/
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            // listView.getDividerHeight()获取子项间分隔符的高度
            // params.height设置ListView完全显示需要的高度
            params.height = totalHeight/* + (listView.getDividerHeight() * (listAdapter.getCount() - 1))*/;
            listView.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface FilterCallback {
        void callback(int matchId, long netbarId, int areaId);
    }

    private class Selected {
        int areaSelected;
        int netbarSelected;
    }
}
