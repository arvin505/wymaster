package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.miqtech.master.client.adapter.HomePageAdapter;
import com.miqtech.master.client.adapter.MatchLobbyAdapter;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.entity.MatchCondition;
import com.miqtech.master.client.entity.MatchItem;
import com.miqtech.master.client.entity.MatchState;
import com.miqtech.master.client.entity.MatchV2;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;
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
 * Created by admin on 2016/8/3.
 */
public class MatchLobbyActivity extends BaseActivity implements MatchLobbyAdapter.MatchLobbyItemOnClickListener, View.OnClickListener {
    @Bind(R.id.tvGame)
    TextView tvGame;
    @Bind(R.id.tvGameType)
    TextView tvGameType;
    @Bind(R.id.rvMatch)
    PullToRefreshRecyclerView prrvMatch;
    @Bind(R.id.llGame)
    LinearLayout llGame;
    @Bind(R.id.llGameStatus)
    LinearLayout llGameStatus;
    @Bind(R.id.llFilter)
    LinearLayout llFilter;
    @Bind(R.id.ivGame)
    ImageView ivGame;
    @Bind(R.id.ivGameTimeType)
    ImageView ivGameTimeType;
    @Bind(R.id.ivEmpty)
    ImageView ivEmpty;

    private RecyclerView rvMatch;

    private HashMap<String, String> params = new HashMap<>();


    private int page = 1;

    private int pageSize = 10;

    private int isLast;

    private Context context;

    private LinearLayoutManager layoutManager;

    private MatchLobbyAdapter adapter;

    private List<MatchV2> matches = new ArrayList<MatchV2>();

    private List<MatchItem> matchItems;


    private boolean shouldShowMore = true;

    private String itemId;


    private PopupWindow matchTimeStatusFilterPopupWindow, matchStatusFilterPopupWindow;

    private ListView lvGameStatus, lvGameTimeStatus, lvAllGame;

    //记录点击的view的id
    private int onClickViewId;


    private String itemName;
    //当前游戏的position
    private int currentGamePosition = 0;

    private int currentGameTypePosition = 0;

    private int currentGameTimeTypePosition = 0;

    //被选中的时间类型记录
    private String recordSelectedTimeType = 0 + ";" + 0;


    private GameTimeStatusListAdapter gameTimeStatusAdapter;

    private GameStatusListAdapter gameStatusAdapter;

    private boolean first = true;
    //当前赛事类型
    private int currentType = 0;
    //当前赛事时间类型
    private int currentState = -1;

    private int officialMatchPosition = 0;


    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_matchlobby);
        ButterKnife.bind(this);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        setRefreshListener();
        llGame.setOnClickListener(this);
        llGameStatus.setOnClickListener(this);
        rvMatch = prrvMatch.getRefreshableView();
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvMatch.setLayoutManager(layoutManager);
        adapter = new MatchLobbyAdapter(context, matches, this);
        rvMatch.setAdapter(adapter);
        getLeftBtn().setOnClickListener(this);
        setLeftIncludeTitle("赛事大厅");
        setRightTextView("往期赛事");
        getRightTextview().setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        itemId = getIntent().getStringExtra("itemId");
        itemName = getIntent().getStringExtra("itemName");
        tvGame.setText(itemName);
        loadGameMatch();
    }

    private void loadGameMatch() {
        params.clear();
        params.put("itemId", itemId + "");
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("state", currentState + "");
        params.put("type", currentType + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MATCH_LIST, params, HttpConstant.MATCH_LIST);
    }

    private void loadAllMatchScreenItem() {
        params.clear();
        params.put("state", 0 + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ALL_MATCH_ITEM, params, HttpConstant.ALL_MATCH_ITEM);
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

    private void showMatchTimeStatusFilterPopWindow() {
        ivGameTimeType.setBackgroundResource(R.drawable.match_filter_up);
        if (matchTimeStatusFilterPopupWindow == null) {
            matchTimeStatusFilterPopupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            matchTimeStatusFilterPopupWindow.setAnimationStyle(R.style.pop_anim);
            View popView = LayoutInflater.from(context).inflate(R.layout.layout_pop_match_filter, null);
            lvGameStatus = (ListView) popView.findViewById(R.id.lvGameStatus);
            lvGameTimeStatus = (ListView) popView.findViewById(R.id.lvGameTimeStatus);
            gameTimeStatusAdapter = new GameTimeStatusListAdapter();
            gameStatusAdapter = new GameStatusListAdapter();
            lvGameStatus.setAdapter(gameStatusAdapter);
            lvGameTimeStatus.setAdapter(gameTimeStatusAdapter);
            matchTimeStatusFilterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivGameTimeType.setBackgroundResource(R.drawable.match_filter_down);
                }
            });
            lvGameStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    currentGameTypePosition = position;
                    gameTimeStatusAdapter.notifyDataSetChanged();
                    gameStatusAdapter.notifyDataSetChanged();
                }
            });
            View emptyView = popView.findViewById(R.id.viewPopEmpty);
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    matchTimeStatusFilterPopupWindow.dismiss();
                }
            });
            lvGameTimeStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    currentGameTimeTypePosition = position;
                    currentType = matchItems.get(currentGamePosition).getCondition().get(currentGameTypePosition).getType();
                    currentState = matchItems.get(currentGamePosition).getCondition().get(currentGameTypePosition).getState().get(currentGameTimeTypePosition).getState();
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
                    recordSelectedTimeType = currentGameTypePosition + ";" + currentGameTimeTypePosition;
                    gameTimeStatusAdapter.notifyDataSetChanged();
                    first = false;
                    page = 1;
                    loadGameMatch();
                    matchTimeStatusFilterPopupWindow.dismiss();
                }
            });
            matchTimeStatusFilterPopupWindow.setContentView(popView);
        }
        matchTimeStatusFilterPopupWindow.showAsDropDown(llFilter, 0, 0);
    }

    private void showMatchStatusFilterPopWindow() {
        ivGame.setBackgroundResource(R.drawable.match_filter_up);
        if (matchStatusFilterPopupWindow == null) {
            matchStatusFilterPopupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            matchStatusFilterPopupWindow.setAnimationStyle(R.style.pop_anim);
            View popView = LayoutInflater.from(context).inflate(R.layout.layout_pop_matchgamefilter, null);
            matchStatusFilterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
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
               //     setGameItemSelectedType(parent, view);
                    MatchItem matchItem = matchItems.get(position);
                    tvGame.setText(matchItem.getItem_name());
                    ivGame.setBackgroundResource(R.drawable.match_filter_down);
                    matchStatusFilterPopupWindow.dismiss();
                    itemId = matchItem.getItem_id() + "";
                    currentGamePosition = position;
                    page = 1;
                    tvGameType.setText("全类型赛事");
                    loadGameMatch();
                    gameListAdapter
                            .notifyDataSetChanged();
                }
            });
            View emptyView = popView.findViewById(R.id.viewPopEmpty);
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    matchStatusFilterPopupWindow.dismiss();
                }
            });
            matchStatusFilterPopupWindow.setContentView(popView);
            matchStatusFilterPopupWindow.setOutsideTouchable(true);
        }
        matchStatusFilterPopupWindow.showAsDropDown(llFilter, 0, 0);
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
                if (matchStatusFilterPopupWindow != null && matchStatusFilterPopupWindow.isShowing()) {
                    matchStatusFilterPopupWindow.dismiss();
                } else {
                    if (matchTimeStatusFilterPopupWindow != null && matchTimeStatusFilterPopupWindow.isShowing()) {
                        matchTimeStatusFilterPopupWindow.dismiss();
                    }
                    if (matchItems == null) {
                        onClickViewId = R.id.llGame;
                        loadAllMatchScreenItem();
                    } else {
                        showMatchStatusFilterPopWindow();
                    }
                }
                break;
            case R.id.llGameStatus:
                if (matchTimeStatusFilterPopupWindow != null && matchTimeStatusFilterPopupWindow.isShowing()) {
                    matchTimeStatusFilterPopupWindow.dismiss();
                } else {
                    if (matchStatusFilterPopupWindow != null && matchStatusFilterPopupWindow.isShowing()) {
                        matchStatusFilterPopupWindow.dismiss();
                    }
                    if (matchItems == null) {
                        onClickViewId = R.id.llGameStatus;
                        loadAllMatchScreenItem();
                    } else {
                        showMatchTimeStatusFilterPopWindow();
                    }
                }
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.tvRightHandle:
                Intent intent = new Intent();
                intent.setClass(context,MatchOverdueActivity.class);
                intent.putExtra("itemId",itemId);
                intent.putExtra("matchName",tvGame.getText().toString());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (matchStatusFilterPopupWindow != null && matchStatusFilterPopupWindow.isShowing()) {
            matchStatusFilterPopupWindow.dismiss();
        } else if (matchTimeStatusFilterPopupWindow != null && matchTimeStatusFilterPopupWindow.isShowing()) {
            matchTimeStatusFilterPopupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
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

    private class GameTimeStatusListAdapter extends BaseAdapter {
        List<MatchState> states;

        @Override
        public int getCount() {
            states = matchItems.get(currentGamePosition).getCondition().get(currentGameTypePosition).getState();
            return states.size();
        }

        @Override
        public Object getItem(int position) {
            return states.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.layout_matchtimestatus_item, null);
                holder = new ViewHolder();
                holder.tvTimeType = (TextView) convertView.findViewById(R.id.tvTimeType);
                holder.tvGameNum = (TextView) convertView.findViewById(R.id.tvGameNum);
                holder.ivSelected = (ImageView) convertView.findViewById(R.id.ivSelected);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            MatchState matchState = states.get(position);
            int state = matchState.getState();
            // -1全类型0预热中1报名中2进行中
            if (state == -1) {
                holder.tvTimeType.setText("全类型");
            } else if (state == 0) {
                holder.tvTimeType.setText("预热中");
            } else if (state == 1) {
                holder.tvTimeType.setText("报名中");
            } else if (state == 2) {
                holder.tvTimeType.setText("进行中");
            }
            String[] records = recordSelectedTimeType.split(";");
            if (records[0].equals(currentGameTypePosition + "") && records[1].equals(currentGameTimeTypePosition + "") && position == currentGameTimeTypePosition) {
                holder.tvGameNum.setTextColor(context.getResources().getColor(R.color.colorActionBarSelected));
                holder.tvTimeType.setTextColor(context.getResources().getColor(R.color.colorActionBarSelected));
                holder.ivSelected.setVisibility(View.VISIBLE);
            } else {
                holder.tvGameNum.setTextColor(context.getResources().getColor(R.color.shop_font_black));
                holder.tvTimeType.setTextColor(context.getResources().getColor(R.color.shop_font_black));
                holder.ivSelected.setVisibility(View.GONE);
            }
            holder.tvGameNum.setText("(" + matchState.getNum() + ")");
            return convertView;
        }

        private class ViewHolder {
            TextView tvTimeType;
            TextView tvGameNum;
            ImageView ivSelected;
        }
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
                convertView = View.inflate(context, R.layout.layout_gametype_item, null);
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
            if (currentGameTypePosition == position) {
                holder.tvSelected.setVisibility(View.VISIBLE);
                holder.rlGameType.setBackgroundResource(R.color.white);
            } else {
                holder.tvSelected.setVisibility(View.INVISIBLE);
                holder.rlGameType.setBackgroundResource(0);
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
                if (matches.size() == 0) {
                    ivEmpty.setVisibility(View.VISIBLE);
                } else {
                    ivEmpty.setVisibility(View.GONE);
                }
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
                        showMatchStatusFilterPopWindow();
                        break;
                    case R.id.llGameStatus:
                        showMatchTimeStatusFilterPopWindow();
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
        prrvMatch.onRefreshComplete();
        if (method.equals(HttpConstant.MATCH_LIST)) {
            if (matches.size() == 0) {
                ivEmpty.setVisibility(View.VISIBLE);
            } else {
                ivEmpty.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prrvMatch.onRefreshComplete();
        if (method.equals(HttpConstant.MATCH_LIST)) {
            if (matches.size() == 0) {
                ivEmpty.setVisibility(View.VISIBLE);
            } else {
                ivEmpty.setVisibility(View.GONE);
            }
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
}
