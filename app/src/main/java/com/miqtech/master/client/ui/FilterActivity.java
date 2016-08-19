package com.miqtech.master.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FilterAwardAdapter;
import com.miqtech.master.client.adapter.FilterGameAdapter;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.AwardInfo;
import com.miqtech.master.client.entity.FilterInfo;
import com.miqtech.master.client.entity.GameItem;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FilterActivity extends BaseActivity implements View.OnClickListener {

    public static String[] enjoyTypes = {"全部参与方式", "个人参与", "团队参与"};
    public static String[] cityTypes = {"全部城市", "当前城市约战", "全部线上约战"};
    public static String[] locations = {"当前城市", "全国"};
    public static String[] stateTypes = {"全部状态", "报名中", "预热中","报名已截止", "已结束", "进行中"};
    //public static String[] awardType = {"全部奖品", "游戏点券", "其他奖励"};

    // enjoy type
    @Bind(R.id.ll_enjoytype)
    LinearLayout llEnjoyType;
    @Bind(R.id.tv_enjoytype_all)
    TextView tvEnjoyTypeAll;
    @Bind(R.id.tv_enjoytype_personal)
    TextView tvEnjoyTypePersonal;
    @Bind(R.id.tv_enjoytype_team)
    TextView tvEnjoyTypeTeam;

    // --enjoy type end

    //location
    @Bind(R.id.ll_location)
    LinearLayout llLocation;
    @Bind(R.id.tv_loc_all)
    TextView tvLocAll;  //全部
    @Bind(R.id.tv_loc_local)
    TextView tvLocLocal;  //本地
    // --location end

    //battle
    @Bind(R.id.ll_battle)
    LinearLayout llBattle;
    @Bind(R.id.tv_battle_area_local)
    TextView tvBattleLocal;
    @Bind(R.id.tv_battle_all_online)
    TextView tvBattleOnline;
    @Bind(R.id.tv_ballte_all)
    TextView tvBattleAll;

    //Game type

    @Bind(R.id.ll_game)
    LinearLayout llGame;
    @Bind(R.id.tv_game_name_all)
    TextView tvAllGame;
    @Bind(R.id.gv_game)
    GridView gvGame;

    //- game type end

    //game state
    @Bind(R.id.ll_state)
    LinearLayout llGameState;
    @Bind(R.id.tv_game_state_all)
    TextView tvGameStateAll;
    @Bind(R.id.tv_state_signing)
    TextView tvGameStateSigning;
    @Bind(R.id.tv_state_end)
    TextView tvGameStateEnd;
    @Bind(R.id.tv_state_preheating)
    TextView tvGameStatePreheating;
    @Bind(R.id.tv_state_processing)
    TextView tvGameStateProcessing;

    //-- game state end

    //reward
    @Bind(R.id.ll_reward)
    LinearLayout llReward;
    @Bind(R.id.tv_reward_all)
    TextView tvRewardAll;
    @Bind(R.id.gv_award)
    GridView gvAward;

    @Bind(R.id.img_header_icon)
    ImageView backIcon;
    @Bind(R.id.tv_header_title)
    TextView tvTitle;


    private Bundle mBundle;


    private FilterInfo filterInfo;
    private FilterGameAdapter adapter;
    private FilterAwardAdapter awardAdapter;
    private List<String> games = new ArrayList<>();
    private List<GameItem> gameItems;
    private int selectedId = 0;
    private int awardSelected = 0;

    @Override
    protected void init() {
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        mBundle = getIntent().getBundleExtra("data");
        LogUtil.e("bun", "dun == " + mBundle.toString());
        initView();
        initFilter(filterInfo);
        if (Constant.gameItems == null) {
            loadGameItem();
        } else {
            initGameList(Constant.gameItems);
        }
        if (Constant.awardInfos == null) {
            loadAward();
        } else {
            initAwardList(Constant.awardInfos);
        }
        setOnClickListner();
    }

    protected void initView() {
        if (mBundle != null) {
            filterInfo = mBundle.getParcelable("filter");
            if (filterInfo.getGameItem() != null) {
                selectedId = filterInfo.getGameItem().getItem_id();
            } else {
                selectedId = 0;
            }
            if (filterInfo.getAwardInfo() != null) {
                awardSelected = filterInfo.getAwardInfo().getAwardtype();
            } else {
                awardSelected = 0;
            }
        }

        if (filterInfo.getFilterType() == Constant.FILTER_GAME) {
            llLocation.setVisibility(View.GONE);
            llGameState.setVisibility(View.GONE);
            llBattle.setVisibility(View.GONE);
            tvTitle.setText(R.string.filter_match);


        } else if (filterInfo.getFilterType() == Constant.FILTER_BATTLE) {
            llLocation.setVisibility(View.GONE);
            llReward.setVisibility(View.GONE);
            llEnjoyType.setVisibility(View.GONE);
            llGameState.setVisibility(View.GONE);

            tvTitle.setText(R.string.filter_battle);
        } else if (filterInfo.getFilterType() == Constant.FILTER_GAME_OFFLINE) {
            llGameState.setVisibility(View.GONE);
            llBattle.setVisibility(View.GONE);
            llLocation.setVisibility(View.GONE);
            tvTitle.setText(R.string.filter_match);
        } else {
            llLocation.setVisibility(View.GONE);
            llEnjoyType.setVisibility(View.GONE);
            llBattle.setVisibility(View.GONE);
            llReward.setVisibility(View.GONE);
            tvTitle.setText(R.string.filter_composite_match);
        }

        String[] gamesStr = getResources().getStringArray(R.array.games);
        addStrArrayToList(gamesStr, games);
        addStrArrayToList(gamesStr, games);


        tvLocLocal.setText(getResources().getString(R.string.native_city_game, Constant.cityName));
        //tvBattleLocal.setText(getResources().getString(R.string.native_city, Constant.cityName));


    }


    private void addStrArrayToList(String[] strings, List<String> target) {
        if (target != null) {
            for (String str : strings) {
                target.add(str);
            }
        }
    }

    private void setOnClickListner() {
        tvEnjoyTypeAll.setOnClickListener(this);
        tvEnjoyTypePersonal.setOnClickListener(this);
        tvEnjoyTypeTeam.setOnClickListener(this);

        tvLocAll.setOnClickListener(this);
        tvLocLocal.setOnClickListener(this);

        tvBattleLocal.setOnClickListener(this);
        tvBattleOnline.setOnClickListener(this);
        tvBattleAll.setOnClickListener(this);

        tvAllGame.setOnClickListener(this);

        tvGameStateAll.setOnClickListener(this);
        tvGameStateSigning.setOnClickListener(this);
        tvGameStateEnd.setOnClickListener(this);
        tvGameStatePreheating.setOnClickListener(this);
        tvGameStateProcessing.setOnClickListener(this);

        tvRewardAll.setOnClickListener(this);
        backIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_enjoytype_all:
                changeSelected(v);
                filterInfo.setEnjoyType(0);
                break;
            case R.id.tv_enjoytype_personal:
                changeSelected(v);
                filterInfo.setEnjoyType(1);
                break;
            case R.id.tv_enjoytype_team:
                changeSelected(v);
                filterInfo.setEnjoyType(2);
                break;


            case R.id.tv_loc_all:
                filterInfo.setLocation(1);
                changeSelected(v);
                break;
            case R.id.tv_loc_local:
                filterInfo.setLocation(0);
                changeSelected(v);
                break;

            case R.id.tv_ballte_all:
                filterInfo.setBattle(0);
                changeSelected(v);
                break;
            case R.id.tv_battle_area_local:
                filterInfo.setBattle(1);
                changeSelected(v);
                break;
            case R.id.tv_battle_all_online:
                filterInfo.setBattle(2);
                changeSelected(v);
                break;

            case R.id.tv_game_name_all:
                filterInfo.setGameItem(new GameItem());
                changeGameSelected(-1, v);
                break;
            case R.id.tv_game_state_all:
                filterInfo.setState(0);
                changeSelected(v);
                break;
            case R.id.tv_state_signing:
                filterInfo.setState(1);
                changeSelected(v);
                break;
            case R.id.tv_state_end:
                filterInfo.setState(4);
                changeSelected(v);
                break;
            case R.id.tv_state_preheating:
                filterInfo.setState(2);
                changeSelected(v);
                break;
            case R.id.tv_state_processing:
                filterInfo.setState(5);
                changeSelected(v);
            case R.id.tv_reward_all:
                filterInfo.setAwardInfo(new AwardInfo());
                changeAwardSelected(-1, v);
                changeSelected(v);
                break;
            case R.id.img_header_icon:

                break;
        }

        finish();
    }

    private void changeSelected(View view) {
        ViewGroup parent;
        if (!(view instanceof ViewGroup)) {
            parent = (ViewGroup) view.getParent().getParent();
        } else {
            parent = (ViewGroup) view;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                changeSelected(child);
            } else {
                if (child instanceof TextView) {
                    ((TextView) child).setTextColor(getResources().getColor(R.color.font_gray));
                    ((TextView) child).setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(getResources().getColor(R.color.colorActionBarSelected));
            view.setBackgroundResource(R.drawable.cell_selected);
            view.setPadding(0, 0, 0, 0);
        }
    }

    private void initFilter(FilterInfo filterInfo) {
        if (filterInfo.getGameItem() == null || filterInfo.getGameItem().getItem_id() == 0) {
            changeGameSelected(-1, tvAllGame);
        }

        if (filterInfo.getAwardInfo() == null || filterInfo.getAwardInfo().getAwardtype() == 0) {
            changeAwardSelected(-1, tvRewardAll);
        }

        if (filterInfo.getBattle() == 0) {
            changeSelected(tvBattleAll);
        } else if (filterInfo.getBattle() == 1) {
            changeSelected(tvBattleLocal);
        } else if (filterInfo.getBattle() == 2) {
            changeSelected(tvBattleOnline);
        }

        if (filterInfo.getEnjoyType() == 0) {
            changeSelected(tvEnjoyTypeAll);
        } else if (filterInfo.getEnjoyType() == 1) {
            changeSelected(tvEnjoyTypePersonal);
        } else if (filterInfo.getEnjoyType() == 2) {
            changeSelected(tvEnjoyTypeTeam);
        }

        if (filterInfo.getLocation() == 1) {
            changeSelected(tvLocAll);
        } else if (filterInfo.getLocation() == 0) {
            changeSelected(tvLocLocal);
        }

        if (filterInfo.getState() == 0) {
            changeSelected(tvGameStateAll);
        } else if (filterInfo.getState() == 1) {
            changeSelected(tvGameStateSigning);
        } else if (filterInfo.getState() == 4) {
            changeSelected(tvGameStateEnd);
        } else if (filterInfo.getState() == 2) {
            changeSelected(tvGameStatePreheating);
        } else if (filterInfo.getState() == 5) {
            changeSelected(tvGameStateProcessing);
        }
    }

    private void changeGameSelected(int position, View view) {
        if (position > -1) {
            for (int i = 0; i < gvGame.getChildCount(); i++) {
                gvGame.getChildAt(i).setBackgroundColor(Color.WHITE);
                ((TextView) ((ViewGroup) gvGame.getChildAt(i)).getChildAt(0)).
                        setTextColor(getResources().getColor(R.color.colorActionBarUnSelected));
            }
            LogUtil.e("pos", gvGame.getChildCount() + " count");
            gvGame.getChildAt(position).setBackgroundResource(R.drawable.cell_selected);
            gvGame.getChildAt(position).setPadding(0, 0, 0, 0);
            tvAllGame.setBackgroundColor(Color.WHITE);
            tvAllGame.setTextColor(getResources().getColor(R.color.colorActionBarUnSelected));
            ((TextView) ((ViewGroup) gvGame.getChildAt(position)).getChildAt(0)).setTextColor(getResources().getColor(R.color.colorActionBarSelected));
        } else {
            for (int i = 0; i < gvGame.getChildCount(); i++) {
                gvGame.getChildAt(i).setBackgroundColor(Color.WHITE);
                ((TextView) ((ViewGroup) gvGame.getChildAt(i)).getChildAt(0)).
                        setTextColor(getResources().getColor(R.color.colorActionBarUnSelected));
            }
            view.setBackgroundResource(R.drawable.cell_selected);
            view.setPadding(0, 0, 0, 0);
            ((TextView) view).setTextColor(getResources().getColor(R.color.colorActionBarSelected));
        }
    }

    private void changeAwardSelected(int position, View view) {
        if (position > -1) {
            for (int i = 0; i < gvAward.getChildCount(); i++) {
                gvAward.getChildAt(i).setBackgroundColor(Color.WHITE);
                ((TextView) ((ViewGroup) gvAward.getChildAt(i)).getChildAt(0)).
                        setTextColor(getResources().getColor(R.color.colorActionBarUnSelected));
            }
            LogUtil.e("pos", gvAward.getChildCount() + " count");
            gvAward.getChildAt(position).setBackgroundResource(R.drawable.cell_selected);
            gvAward.getChildAt(position).setPadding(0, 0, 0, 0);
            tvRewardAll.setBackgroundColor(Color.WHITE);
            tvRewardAll.setTextColor(getResources().getColor(R.color.colorActionBarUnSelected));
            ((TextView) ((ViewGroup) gvAward.getChildAt(position)).getChildAt(0)).setTextColor(getResources().getColor(R.color.colorActionBarSelected));
        } else {
            for (int i = 0; i < gvAward.getChildCount(); i++) {
                gvAward.getChildAt(i).setBackgroundColor(Color.WHITE);
                ((TextView) ((ViewGroup) gvAward.getChildAt(i)).getChildAt(0)).
                        setTextColor(getResources().getColor(R.color.colorActionBarUnSelected));
            }
            view.setBackgroundResource(R.drawable.cell_selected);
            view.setPadding(0, 0, 0, 0);
            ((TextView) view).setTextColor(getResources().getColor(R.color.colorActionBarSelected));
        }
    }


    public void finish() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("filter", filterInfo);
        if (filterInfo.getGameItem() == null) {
            filterInfo.setGameItem(new GameItem());
        }
        Intent intent = new Intent();
        intent.putExtra("result", bundle);
        setResult(1, intent);
        gvGame.setSelection(2);
        super.finish();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("new", "new == " + intent.toString());
    }

    private void loadGameItem() {
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MATCH_ITEM_LIST, null, HttpConstant.MATCH_ITEM_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (HttpConstant.MATCH_ITEM_LIST.equals(method)) {
            try {
                List<GameItem> list = initGameItem(object);

                if (list != null && !list.isEmpty()) {
                    initGameList(list);
                    Constant.gameItems = list;
                } else {
                    showToast("error");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.e("xiaoyi", "eeeeee");
            }
        } else {
            try {
                List<AwardInfo> list = initAwardItem(object);
                if (list != null && !list.isEmpty()) {
                    initAwardList(list);
                    Constant.awardInfos = list;
                } else {
                    showToast("error");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initGameList(final List<GameItem> list) {
        adapter = new FilterGameAdapter(this, list, selectedId);
        gvGame.setAdapter(adapter);
        LogUtil.e("list", list.toString());
        gvGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeGameSelected(position, view);
                filterInfo.setGameItem(list.get(position));
                finish();
            }
        });
    }

    private List<GameItem> initGameItem(JSONObject object) throws JSONException {
        List<GameItem> items = new ArrayList<>();
        LogUtil.e("json", object.toString());
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONArray objectJson = object.getJSONArray("object");
            items = GsonUtil.getList(objectJson.toString(), GameItem.class);
            return items;
        }
        return items;
    }

    private List<AwardInfo> initAwardItem(JSONObject object) throws JSONException {
        List<AwardInfo> items = new ArrayList<>();
        LogUtil.e("json", object.toString());
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONArray objectJson = object.getJSONArray("object");
            items = GsonUtil.getList(objectJson.toString(), AwardInfo.class);
            return items;
        }
        return items;
    }

    private void initAwardList(final List<AwardInfo> list) {
        awardAdapter = new FilterAwardAdapter(this, list, awardSelected);
        gvAward.setAdapter(awardAdapter);
        LogUtil.e("list", list.toString());
        gvAward.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeAwardSelected(position, view);
                filterInfo.setAwardInfo(list.get(position));
                finish();
            }
        });
    }

    private void loadAward() {
        Map<String, String> params = new HashMap<>(); //????
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AWARD_TYPE, params, HttpConstant.AWARD_TYPE);
    }
}
