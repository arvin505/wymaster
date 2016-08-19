package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MatchLobbyAdapter;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.entity.MatchCondition;
import com.miqtech.master.client.entity.MatchItem;
import com.miqtech.master.client.entity.MatchV2;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;
import com.miqtech.master.client.view.pullToRefresh.internal.FrameAnimationHeaderLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 往期赛事
 * Created by wuxn on 2016/8/15.
 */
public class MatchOverdueActivity extends BaseActivity implements View.OnClickListener, MatchLobbyAdapter.MatchLobbyItemOnClickListener {
    private Context context;
    @Bind(R.id.llGame)
    LinearLayout llGame;
    @Bind(R.id.tvGame)
    TextView tvGame;
    @Bind(R.id.llGameType)
    LinearLayout llGameType;
    @Bind(R.id.rvMatch)
    PullToRefreshRecyclerView prrvMatch;
    @Bind(R.id.ivGame)
    ImageView ivGame;
    @Bind(R.id.ivGameType)
    ImageView ivGameType;
    @Bind(R.id.llFilter)
    LinearLayout llFilter;
    @Bind(R.id.tvGameType)
    TextView tvGameType;

    private RecyclerView rvMatch;

    private List<MatchV2> matches = new ArrayList<MatchV2>();

    private LinearLayoutManager layoutManager;

    private HashMap<String, String> params = new HashMap<>();

    private int page = 1;

    private int pageSize = 10;

    //当前赛事类型
    private int currentType = 0;

    private boolean shouldShowMore = true;

    private int isLast;

    private PopupWindow gameTypePopWindow, gameStatusPopWindow;

    private ListView lvAllGame;

    private List<MatchItem> matchItems;

    //当前游戏的position
    private int currentGamePosition = 0;

    private ListView lvGameStatus;

    private GameStatusListAdapter gameStatusAdapter;

    private MatchLobbyAdapter adapter;

    //记录点击的view的id
    private int onClickViewId;

    private String itemId;

    private int currentGameStatusPosition = 0;

    private int officialMatchPosition = 0;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_matchoverdue);
        context = this;
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftIncludeTitle("往期赛事");
        getLeftBtn().setOnClickListener(this);
        rvMatch = prrvMatch.getRefreshableView();
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvMatch.setLayoutManager(layoutManager);
        adapter = new MatchLobbyAdapter(context, matches, this);
        rvMatch.setAdapter(adapter);
        llGame.setOnClickListener(this);
        llGameType.setOnClickListener(this);
        tvGame.setText(getIntent().getStringExtra("matchName"));
        setRefreshListener();
    }

    private void loadGameMatch() {
        params.clear();
        params.put("itemId", itemId);
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("state", 3 + "");
        params.put("type", currentType + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MATCH_LIST, params, HttpConstant.MATCH_LIST);
    }

    private void loadAllMatchScreenItem() {
        params.clear();
        params.put("state", 1 + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ALL_MATCH_ITEM, params, HttpConstant.ALL_MATCH_ITEM);
    }

    @Override
    protected void initData() {
        super.initData();
        itemId = getIntent().getStringExtra("itemId");
        loadGameMatch();
    }

    private void setRefreshListener() {
        prrvMatch.setMode(PullToRefreshBase.Mode.BOTH);
        prrvMatch.setHeaderLayout(new FrameAnimationHeaderLayout(context));
        prrvMatch.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                shouldShowMore = true;
                loadGameMatch();
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
                            loadGameMatch();
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

            }
        });
    }

    private void showGameStatusFilterPopWindow() {
        ivGameType.setBackgroundResource(R.drawable.match_filter_up);
        if (gameStatusPopWindow == null) {
            gameStatusPopWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            gameStatusPopWindow.setAnimationStyle(R.style.pop_anim);
            View popView = LayoutInflater.from(context).inflate(R.layout.layout_pop_matchoverdue_filter, null);
            gameStatusPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivGameType.setBackgroundResource(R.drawable.match_filter_down);
                }
            });
            View viewPopEmpty = popView.findViewById(R.id.viewPopEmpty);
            viewPopEmpty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameStatusPopWindow.dismiss();
                }
            });
            lvGameStatus = (ListView) popView.findViewById(R.id.lvGameStatus);
            gameStatusAdapter = new GameStatusListAdapter();
            lvGameStatus.setAdapter(gameStatusAdapter);
            gameStatusPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivGameType.setBackgroundResource(R.drawable.match_filter_down);
                }
            });
            lvGameStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    currentGameStatusPosition = position;
                    gameStatusAdapter.notifyDataSetChanged();
                    MatchCondition condition = matchItems.get(currentGamePosition).getCondition().get(position);
                    currentType = condition.getType();
                    if (currentType == 0) {
                        //全类型
                        tvGameType.setText("全类型赛事");
                    } else if (currentType == 1) {
                        //官方赛
                        tvGameType.setText("官方赛赛事");
                    } else if (currentType == 2) {
                        //自发赛
                        tvGameType.setText("自发赛赛事");
                    } else if (currentType == 3) {
                        //悬赏令
                        tvGameType.setText("悬赏令赛事");
                    }
                    page = 1;
                    loadGameMatch();
                    gameStatusPopWindow.dismiss();
                }
            });
            gameStatusPopWindow.setContentView(popView);
        }
        gameStatusPopWindow.showAsDropDown(llFilter, 0, 0);
    }

    private class GameStatusListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return matchItems.get(currentGamePosition).getCondition().size();
        }

        @Override
        public Object getItem(int position) {
            return matchItems.get(position).getCondition().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.layout_matchoverdue_status_item, null);
                holder = new ViewHolder();
                holder.tvMatchNum = (TextView) convertView.findViewById(R.id.tvMatchNum);
                holder.tvSelected = (TextView) convertView.findViewById(R.id.tvSelected);
                holder.tvTypeName = (TextView) convertView.findViewById(R.id.tvTypeName);
                holder.rlGameType = (RelativeLayout) convertView.findViewById(R.id.rlGameType);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MatchCondition condition = matchItems.get(currentGamePosition).getCondition().get(position);
            int type = condition.getType();
            if (type == 0) {
                //全类型
                holder.tvTypeName.setText("全类型");
            } else if (type == 1) {
                //官方赛
                holder.tvTypeName.setText("官方赛");
            } else if (type == 2) {
                //自发赛
                holder.tvTypeName.setText("自发赛");
            } else if (type == 3) {
                //悬赏令
                holder.tvTypeName.setText("悬赏令");
            }
            holder.tvMatchNum.setText(condition.getNum() + "");
            if (currentGameStatusPosition == position) {
                holder.tvTypeName.setTextColor(context.getResources().getColor(R.color.orange));
                holder.tvMatchNum.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.corner_match_num_selceted_bg));
            } else {
                holder.tvTypeName.setTextColor(context.getResources().getColor(R.color.shop_font_black));
                holder.tvMatchNum.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.corner_match_num_bg));
            }
            return convertView;
        }

        private class ViewHolder {
            TextView tvSelected;
            TextView tvTypeName;
            TextView tvMatchNum;
            RelativeLayout rlGameType;
        }
    }

    private void showGameTypeFilterPopWindow() {
        ivGame.setBackgroundResource(R.drawable.match_filter_up);
        if (gameTypePopWindow == null) {
            gameTypePopWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            gameTypePopWindow.setAnimationStyle(R.style.pop_anim);
            View popView = LayoutInflater.from(context).inflate(R.layout.layout_pop_matchgamefilter, null);
            gameTypePopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivGame.setBackgroundResource(R.drawable.match_filter_down);
                }
            });
            lvAllGame = (ListView) popView.findViewById(R.id.lvAllGame);
            final GameListAdapter gameListAdapter = new GameListAdapter();
            lvAllGame.setAdapter(gameListAdapter);

            lvAllGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MatchItem matchItem = matchItems.get(position);
                    tvGame.setText(matchItem.getItem_name());
                    ivGame.setBackgroundResource(R.drawable.match_filter_down);
                    gameTypePopWindow.dismiss();
                    itemId = matchItem.getItem_id() + "";
                    currentGamePosition = position;
                    currentGameStatusPosition = 0;
                    tvGameType.setText("全类型赛事");
                    page = 1;
                    loadGameMatch();
                    gameListAdapter.notifyDataSetChanged();
                }
            });
            View emptyView = popView.findViewById(R.id.viewPopEmpty);
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameTypePopWindow.dismiss();
                }
            });
            gameTypePopWindow.setContentView(popView);
            gameTypePopWindow.setOutsideTouchable(true);
        }
        gameTypePopWindow.showAsDropDown(llFilter, 0, 0);
    }

    private class GameListAdapter extends BaseAdapter {

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
                convertView = View.inflate(context, R.layout.layout_match_game_item, null);
                holder = new ViewHolder();
                holder.ivImg = (ImageView) convertView.findViewById(R.id.ivImg);
                holder.tvGameName = (TextView) convertView.findViewById(R.id.tvGameName);
                holder.tvMatchNum = (TextView) convertView.findViewById(R.id.tvMatchNum);
                holder.ivSelected = (ImageView) convertView.findViewById(R.id.ivSelected);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            MatchItem matchItem = matchItems.get(position);
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + matchItem.getItem_icon(), holder.ivImg);
            holder.tvGameName.setText(matchItem.getItem_name());
            holder.tvMatchNum.setText("(" + matchItem.getNum() + ")");
            if (currentGamePosition == position) {
                holder.tvMatchNum.setTextColor(context.getResources().getColor(R.color.colorActionBarSelected));
                holder.tvGameName.setTextColor(context.getResources().getColor(R.color.colorActionBarSelected));
                holder.ivSelected.setVisibility(View.VISIBLE);
            } else {
                holder.tvMatchNum.setTextColor(context.getResources().getColor(R.color.shop_font_black));
                holder.tvGameName.setTextColor(context.getResources().getColor(R.color.shop_font_black));
                holder.ivSelected.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class ViewHolder {
            ImageView ivImg;
            TextView tvGameName;
            TextView tvMatchNum;
            ImageView ivSelected;
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        prrvMatch.onRefreshComplete();
        if (object == null) {
            return;
        }
        try {
            if (HttpConstant.MATCH_LIST.equals(method)) {
                String objStr = object.getString("object");
                JSONObject jsonObj = new JSONObject(objStr);
                String listStr = jsonObj.getString("list");
                isLast = jsonObj.getInt("isLast");
                List<MatchV2> newMatches = new Gson().fromJson(listStr, new TypeToken<List<MatchV2>>() {
                }.getType());
                if (page == 1) {
                    matches.clear();
                    matches.addAll(newMatches);
                } else {
                    matches.addAll(newMatches);
                }
//                if (matches.size() == 0) {
//                    ivEmpty.setVisibility(View.VISIBLE);
//                } else {
//                    ivEmpty.setVisibility(View.GONE);
//                }
                adapter.notifyDataSetChanged();
                page++;
            } else if (HttpConstant.ALL_MATCH_ITEM.equals(method)) {
                String objStr = object.getString("object");
                matchItems = new Gson().fromJson(objStr, new TypeToken<List<MatchItem>>() {
                }.getType());

                currentGamePosition = findItemByGameId();
                //根据ID来区别点击的类型
                switch (onClickViewId) {
                    case R.id.llGame:
                        showGameTypeFilterPopWindow();
                        break;
                    case R.id.llGameType:
                        showGameStatusFilterPopWindow();
                        break;
                }
            }else if (HttpConstant.ROUND_INFO.equals(method)) {
                String objStr = object.getString("object");
                ArrayList<MatchV2.RoundInfo> rounds = new Gson().fromJson(objStr, new TypeToken<List<MatchV2.RoundInfo>>() {
                }.getType());
                MatchV2 match = matches.get(officialMatchPosition);
                match.setRounds(rounds);
                View matchView = layoutManager.findViewByPosition(officialMatchPosition );
                MatchLobbyAdapter.MatchOfficialViewHolder matchViewHolder = (MatchLobbyAdapter.MatchOfficialViewHolder) rvMatch.getChildViewHolder(matchView);
                for (int i = 0; i < rounds.size(); i++) {
                    MatchV2.RoundInfo round = rounds.get(i);
                    if (round.getState().equals("报名")) {
                        matchViewHolder.rlApply.setVisibility(View.VISIBLE);
                        matchViewHolder.tvApplyTime.setText(round.getDate());
                    } else if (round.getState().equals("进行")) {
                        matchViewHolder.rlDoing.setVisibility(View.VISIBLE);
                        matchViewHolder.tvDoingTime.setText(round.getDate());
                    } else if (round.getState().equals("预热")) {
                        matchViewHolder.rlWarmUp.setVisibility(View.VISIBLE);
                        matchViewHolder.tvWarmUpTime.setText(round.getDate());
                    }
                }
                matchViewHolder.ivArrows.setImageDrawable(context.getResources().getDrawable(R.drawable.match_filter_up));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    //根据ID找到对应的position
    private int findItemByGameId() {
        if (matchItems != null) {
            int count = matchItems.size();
            for (int i = 0; i < count; i++) {
                if ((matchItems.get(i).getItem_id() + "").equals(itemId)) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llGame:
                if (gameTypePopWindow != null && gameTypePopWindow.isShowing()) {
                    gameTypePopWindow.dismiss();
                } else {
                    if (gameStatusPopWindow != null && gameStatusPopWindow.isShowing()) {
                        gameStatusPopWindow.dismiss();
                    }
                    if (matchItems == null) {
                        onClickViewId = R.id.llGame;
                        loadAllMatchScreenItem();
                    } else {
                        showGameTypeFilterPopWindow();
                    }
                }
                break;
            case R.id.llGameType:
                if (gameStatusPopWindow != null && gameStatusPopWindow.isShowing()) {
                    gameStatusPopWindow.dismiss();
                } else {
                    if (gameTypePopWindow != null && gameTypePopWindow.isShowing()) {
                        gameTypePopWindow.dismiss();
                    }
                    if (matchItems == null) {
                        onClickViewId = R.id.llGameType;
                        loadAllMatchScreenItem();
                    } else {
                        showGameStatusFilterPopWindow();
                    }
                }
                break;
            case R.id.ibLeft:
                AppManager.getAppManager().finishActivity(this);
                break;
        }

    }

    @Override
    public void officialMatchOnClick(int matchId) {
        Intent intent = new Intent();
        intent.setClass(context, OfficalEventActivity.class);
        intent.putExtra("matchId", matchId + "");
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
    public void rewardMatchOnClick(int rewardId, int state) {
        Intent intent = new Intent();
        intent.setClass(context, RewardActivity.class);
        intent.putExtra("rewardId", rewardId);
        intent.putExtra("isEnd", state + "");
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
    @Override
    public void onBackPressed() {

        if (gameStatusPopWindow != null && gameStatusPopWindow.isShowing()) {
            gameStatusPopWindow.dismiss();
        } else if (gameTypePopWindow != null && gameTypePopWindow.isShowing()) {
            gameTypePopWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }

}
