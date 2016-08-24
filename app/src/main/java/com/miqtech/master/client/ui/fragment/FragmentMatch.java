package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.HomePageAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.HomePageRecommendAndBanner;
import com.miqtech.master.client.entity.MatchItem;
import com.miqtech.master.client.entity.MatchV2;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.EventDetailActivity;
import com.miqtech.master.client.ui.MatchLobbyActivity;
import com.miqtech.master.client.ui.MyMatchActivity;
import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RewardActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.ToastUtil;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;
import com.miqtech.master.client.view.pullToRefresh.internal.FrameAnimationHeaderLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2016/7/22.
 */
public class FragmentMatch extends BaseFragment implements HomePageAdapter.HomePageOnClickListener {

    @Bind(R.id.rvMatch)
    PullToRefreshRecyclerView prrvMatch;
    @Bind(R.id.rlAllMatch)
    RelativeLayout rlAllMatch;
    @Bind(R.id.ivType)
    ImageView ivType;

    private RecyclerView rvMatch;

    private Context context;


    private HashMap<String, String> params = new HashMap<>();

    private HomePageRecommendAndBanner homePageRecommendAndBanner = null;

    public HomePageAdapter adapter;

    private List<MatchV2> matches = new ArrayList<MatchV2>();

    private int page = 1;
    private int pageSize = 10;
    private int isLast;
    private LinearLayoutManager layoutManager;

    private int allMatchViewMarginTop = 0;

    private PopupWindow screenPopupWindow;

    private List<MatchItem> matchItems;


    private boolean shouldShowMore = true;

    private int officialMatchPosition;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache = ACache.get(getActivity());
        context = getActivity();
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoading();
        ButterKnife.bind(this, view);
        setRefreshListener();
        initView();
        loadData();
    }

    public void loadData() {
        loadBannerAndMyMatch();
    }

    private void initView() {
        int statusBarHeight = WangYuApplication.getStatusHeight(getActivity());
        float includeHeight = context.getResources().getDimension(R.dimen.main_include_header_height);
        allMatchViewMarginTop = (int) (statusBarHeight + includeHeight);
        //获取全部赛事控件距离屏幕顶端的距离
        rvMatch = prrvMatch.getRefreshableView();
        ivType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screenPopupWindow != null && screenPopupWindow.isShowing()) {
                    screenPopupWindow.dismiss();
                } else {
                    rlAllMatch.setVisibility(View.VISIBLE);
                    layoutManager.scrollToPositionWithOffset(1, 0);
                    if (screenPopupWindow == null) {
                        loadAllMatchScreenItem();
                    } else {
                        if (rlAllMatch.getVisibility() == View.VISIBLE) {
                            screenPopupWindow.showAsDropDown(rlAllMatch);
                        } else {
                            screenPopupWindow.showAsDropDown(rlAllMatch, 0, (int) context.getResources().getDimension(R.dimen.main_all_match_screen_height));
                        }
                    }
                }
            }
        });
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvMatch.setLayoutManager(layoutManager);
        adapter = new HomePageAdapter(context, matches, homePageRecommendAndBanner, this);
        rvMatch.setAdapter(adapter);
        rvMatch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //吸附栏状态
                View allMatchView = layoutManager.findViewByPosition(1);
                int[] allMatchViewlocation = new int[2];
                if (allMatchView != null) {
                    allMatchView.getLocationOnScreen(allMatchViewlocation);
                    if (allMatchViewlocation[1] <= allMatchViewMarginTop) {
                        rlAllMatch.setVisibility(View.VISIBLE);
                    } else {
                        rlAllMatch.setVisibility(View.GONE);
                    }
                }

                //进入动画
                int position = layoutManager.findLastVisibleItemPosition();
                RecyclerView.ViewHolder holder = rvMatch.findViewHolderForAdapterPosition(position);
                if (position - 2 < matches.size()) {
                    MatchV2 match = matches.get(position - 2);
                    if (holder instanceof HomePageAdapter.RewardMatchViewHolder) {
                        final HomePageAdapter.RewardMatchViewHolder mHolder = ((HomePageAdapter.RewardMatchViewHolder) holder);
                        int[] location = new int[2];
                        mHolder.ivRewardBg.getLocationOnScreen(location);
                        int screenHeight = WangYuApplication.HEIGHT;
                        if (location[1] < screenHeight - mHolder.ivRewardBg.getHeight() && !match.isHasVisibled()) {
                            final Animation animation = AnimationUtils.loadAnimation(context, R.anim.view_left_in);
                            match.setHasVisibled(true);
                            mHolder.ivRewardBg.startAnimation(animation);
                        }
                    } else if (holder instanceof HomePageAdapter.ReleaseByShelfMatchViewHolder) {
                        final HomePageAdapter.ReleaseByShelfMatchViewHolder mHolder = ((HomePageAdapter.ReleaseByShelfMatchViewHolder) holder);
                        int[] location = new int[2];
                        mHolder.ivSpontaneousBg.getLocationOnScreen(location);
                        int screenHeight = WangYuApplication.HEIGHT;
                        if (location[1] < screenHeight - mHolder.ivSpontaneousBg.getHeight() && !match.isHasVisibled()) {
                            final Animation animation = AnimationUtils.loadAnimation(context, R.anim.view_left_in);
                            match.setHasVisibled(true);
                            mHolder.ivSpontaneousBg.startAnimation(animation);
                        }
                    } else if (holder instanceof HomePageAdapter.MatchOfficialViewHolder) {
                        final HomePageAdapter.MatchOfficialViewHolder mHolder = ((HomePageAdapter.MatchOfficialViewHolder) holder);
                        int[] location = new int[2];
                        mHolder.ivSpontaneousBg.getLocationOnScreen(location);
                        int screenHeight = WangYuApplication.HEIGHT;
                        if (location[1] < screenHeight - mHolder.ivSpontaneousBg.getHeight() && !match.isHasVisibled()) {
                            final Animation animation = AnimationUtils.loadAnimation(context, R.anim.view_left_in);
                            match.setHasVisibled(true);
                            mHolder.ivSpontaneousBg.startAnimation(animation);
                        }
                    }
                }
            }
        });
    }


    private void loadBannerAndMyMatch() {
        User user = WangYuApplication.getUser(context);
        params.clear();
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        if (Constant.isLocation) {
            params.put("areaCode", Constant.currentCity.getAreaCode());
        } else {
            String aeraCode = PreferencesUtil.getAeraCode(getContext());
            if (!TextUtils.isEmpty(aeraCode)) {
                params.put("areaCode", aeraCode);
            }
        }
        params.put("more", 1 + "");
        params.put("belong", 0 + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AD, params, HttpConstant.AD);
    }

    private void loadMatchList() {
        params.clear();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
//        params.put("type", 3 + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MATCH_LIST, params, HttpConstant.MATCH_LIST);
    }


    private void setRefreshListener() {
        prrvMatch.setMode(PullToRefreshBase.Mode.BOTH);
        prrvMatch.setHeaderLayout(new FrameAnimationHeaderLayout(getActivity()));
        prrvMatch.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                shouldShowMore = true;
                adapter.bannerRefresh = true;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 0) {
                    //     adapter.showFooter();
                    rvMatch.scrollToPosition(adapter.getItemCount() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            loadMatchList();
                        }
                    }, 1000);
                } else {
                    if (shouldShowMore) {
                        showToast(getResources().getString(R.string.nomore));
                        shouldShowMore = false;
                    }
                    prrvMatch.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                //   showToast(getActivity().getResources().getString(R.string.noNeteork));
            }
        });
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        prrvMatch.onRefreshComplete();
        if (object == null) {
            return;
        }
        try {
            if (HttpConstant.AD.equals(method)) {
                setAD(object);
                mCache.put(HttpConstant.AD, object);
                loadMatchList();
            } else if (HttpConstant.MATCH_LIST.equals(method)) {
                setMatchList(object);
            } else if (HttpConstant.ALL_MATCH_ITEM.equals(method)) {
                String objStr = object.getString("object");
                matchItems = new Gson().fromJson(objStr, new TypeToken<List<MatchItem>>() {
                }.getType());
                showScreenPopWindow();
            } else if (HttpConstant.ROUND_INFO.equals(method)) {
                String objStr = object.getString("object");
                ArrayList<MatchV2.RoundInfo> rounds = new Gson().fromJson(objStr, new TypeToken<List<MatchV2.RoundInfo>>() {
                }.getType());
                MatchV2 match = matches.get(officialMatchPosition);
                match.setRounds(rounds);
                View matchView = layoutManager.findViewByPosition(officialMatchPosition + 2);
                HomePageAdapter.MatchOfficialViewHolder matchViewHolder = (HomePageAdapter.MatchOfficialViewHolder) rvMatch.getChildViewHolder(matchView);
                for (int i = 0; i < rounds.size(); i++) {
                    MatchV2.RoundInfo round = rounds.get(i);
                    if (round.getState().equals("报名")) {
                        matchViewHolder.rlApply.setVisibility(View.VISIBLE);
                        matchViewHolder.tvApplyTime.setText(round.getDate()+"正在报名中");
                    } else if (round.getState().equals("进行")) {
                        matchViewHolder.rlDoing.setVisibility(View.VISIBLE);
                        matchViewHolder.tvDoingTime.setText(round.getDate()+"正在进行中");
                    } else if (round.getState().equals("预热")) {
                        matchViewHolder.rlWarmUp.setVisibility(View.VISIBLE);
                        matchViewHolder.tvWarmUpTime.setText(round.getDate()+"正在预热中");
                    }
                }
                matchViewHolder.ivArrows.setImageDrawable(context.getResources().getDrawable(R.drawable.match_filter_up));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAD(JSONObject object) {
        try {
            String objStr = object.getString("object");
            homePageRecommendAndBanner = new Gson().fromJson(objStr, HomePageRecommendAndBanner.class);
            adapter.setHomePageRecommendAndBanner(homePageRecommendAndBanner);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setMatchList(JSONObject object) {
        try {
            String objStr = object.getString("object");
            JSONObject jsonObj = new JSONObject(objStr);
            String listStr = jsonObj.getString("list");
            isLast = jsonObj.getInt("isLast");
            List<MatchV2> newMatches = new Gson().fromJson(listStr, new TypeToken<List<MatchV2>>() {
            }.getType());
            if (page == 1) {
                matches.clear();
                matches.addAll(newMatches);
                mCache.put(HttpConstant.MATCH_LIST, object);
            } else {
                matches.addAll(newMatches);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        prrvMatch.onRefreshComplete();
        if (method.equals(HttpConstant.AD) || method.equals(HttpConstant.MATCH_LIST)) {
            loadCache();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prrvMatch.onRefreshComplete();
        if (method.equals(HttpConstant.AD) || method.equals(HttpConstant.MATCH_LIST)) {
            loadCache();
        }
    }

    private void loadCache() {
        JSONObject adJson = mCache.getAsJSONObject(HttpConstant.AD);
        if (adJson != null) {
            setAD(adJson);
        }
        JSONObject matchListJson = mCache.getAsJSONObject(HttpConstant.MATCH_LIST);
        if (matchListJson != null) {
            setMatchList(matchListJson);
        }
    }

    private void loadAllMatchScreenItem() {

        showLoading();
        params.clear();
        params.put("state", -1 + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ALL_MATCH_ITEM, params, HttpConstant.ALL_MATCH_ITEM);
    }


    private void showScreenPopWindow() {
        if (screenPopupWindow == null) {
            screenPopupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            screenPopupWindow.setAnimationStyle(R.style.pop_anim);
            View popView = LayoutInflater.from(getContext()).inflate(R.layout.layout_pop_matchscreen, null);
            ListView lvAllMatch = (ListView) popView.findViewById(R.id.lvAllMatch);
            View emptyView = popView.findViewById(R.id.viewPopEmpty);
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    screenPopupWindow.dismiss();
                }
            });
            lvAllMatch.setAdapter(new MatchScreenListAdapter());
            lvAllMatch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.setClass(context, MatchLobbyActivity.class);
                    intent.putExtra("itemName", matchItems.get(position).getItem_name());
                    intent.putExtra("itemId", matchItems.get(position).getItem_id() + "");
                    startActivity(intent);
                    screenPopupWindow.dismiss();
                }
            });
            screenPopupWindow.setContentView(popView);
            screenPopupWindow.setOutsideTouchable(true);
        }
        if (rlAllMatch.getVisibility() == View.VISIBLE) {
            screenPopupWindow.showAsDropDown(rlAllMatch);
        } else {
            screenPopupWindow.showAsDropDown(rlAllMatch, 0, (int) context.getResources().getDimension(R.dimen.main_all_match_screen_height));
        }
    }

    private class MatchScreenListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return matchItems.size();
        }

        @Override
        public Object getItem(int position) {
            return matchItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.layout_matchscreen_item, null);
                holder = new ViewHolder();
                holder.ivImg = (ImageView) convertView.findViewById(R.id.ivImg);
                holder.tvGameName = (TextView) convertView.findViewById(R.id.tvGameName);
                holder.tvMatchNum = (TextView) convertView.findViewById(R.id.tvMatchNum);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            MatchItem matchItem = matchItems.get(position);
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + matchItem.getItem_icon(), holder.ivImg);
            holder.tvGameName.setText(matchItem.getItem_name());
            holder.tvMatchNum.setText(matchItem.getNum() + "");
            return convertView;
        }

        private class ViewHolder {
            ImageView ivImg;
            TextView tvGameName;
            TextView tvMatchNum;
        }
    }

    @Override
    public void myMatchOnClick() {
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            Intent intent = new Intent();
            intent.setClass(context, MyMatchActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void allMatchOnClick() {
        if (screenPopupWindow != null && screenPopupWindow.isShowing()) {
            screenPopupWindow.dismiss();
        } else {
            rlAllMatch.setVisibility(View.VISIBLE);
            layoutManager.scrollToPositionWithOffset(1, 0);
            if (screenPopupWindow == null) {
                loadAllMatchScreenItem();
            } else {
                if (rlAllMatch.getVisibility() == View.VISIBLE) {
                    screenPopupWindow.showAsDropDown(rlAllMatch);
                } else {
                    screenPopupWindow.showAsDropDown(rlAllMatch, 0, (int) context.getResources().getDimension(R.dimen.main_all_match_screen_height));
                }
            }
        }
    }


    @Override
    public void headLineOnClick() {

    }

    @Override
    public void officialMatchOnClick(int officialId) {
        Intent intent = new Intent();
        intent.setClass(context, OfficalEventActivity.class);
        intent.putExtra("matchId", officialId + "");
        startActivity(intent);
    }

    @Override
    public void releaseBySelfMatchOnClick(int matchId) {
        Intent intent = new Intent();
        intent.setClass(context, EventDetailActivity.class);
        intent.putExtra("matchId", matchId + "");
        startActivity(intent);
    }

    @Override
    public void rewardMatchOnClick(int rewardId, String state) {
        Intent intent = new Intent();
        intent.setClass(context, RewardActivity.class);
        intent.putExtra("rewardId", rewardId);
        intent.putExtra("isEnd", (Integer.valueOf(state) - 2) + "");
        startActivity(intent);
    }

    @Override
    public void officialMatchRoundInfoOnClick(int position) {
        int officialMatchId = matches.get(position).getId();
        officialMatchPosition = position;
        loadMatchRoundInfo(officialMatchId);
    }

    private void loadMatchRoundInfo(int officialMatchId) {
        params.clear();
        params.put("id", officialMatchId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ROUND_INFO, params, HttpConstant.ROUND_INFO);
    }
}
