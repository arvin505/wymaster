package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.BudgetCoin;
import com.miqtech.master.client.entity.Exchange;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.PullToRefreshLayout;
import com.miqtech.master.client.view.PullableScrollView;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 金币商城：兑换记录和收支记录
 * Created by Administrator on 2015/12/3.
 */
public class MyGoldCoinsActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener, View.OnClickListener, PullableScrollView.MyScrollListener, Observerable.ISubscribe {

    private List<BudgetCoin> budgets = new ArrayList<BudgetCoin>();
    private List<Exchange> exchanges = new ArrayList<Exchange>();
    private List<Object> budgetList = new ArrayList<Object>();
    private List<Object> exchangeList = new ArrayList<Object>();
    private PullToRefreshLayout refresh_view;
    private LinearLayout llIncomeHistory, llExchangeHistory, llEarnCoins;
    private TextView tvIncomeHistory, tvExchangeHistory, tvTodayEarnings, tvGoldCoinsNum;
    private ImageView img_income_select_bot, img_exchange_select_bot;
    private int budgetsIsLast;
    private int exchangeIsLast;
    private int budgetPage = 1;
    private int exchangePage = 1;

    private int BUDGET_TYPE = 1;
    private int EXCHANGE_TYPE = 2;
    private int currentType = BUDGET_TYPE;

    private boolean isFirstLoadExchange = true;
    private boolean isFirstLoadBudget = true;

    private String totalCoins;
    private Context context;

    private PullableScrollView scrollView;
    private LinearLayout llfirst, llsecond, llIncomeHistory_second, llExchangeHistory_second, myllheader, ll_Budget, ll_Exchange;
    private TextView tvIncomeHistory_second, tvExchangeHistory_second;
    private ImageView img_income_select_bot_second, img_exchange_select_bot_second;
    private int top;

    ImageView ivGoldType;
    TextView tvGoldTitle;
    TextView tvGoldNum;
    ImageView tvGoldType;
    TextView tvDate;
    private int BudgetOrExchange;

    private Observerable mWatcher;

    protected void init() {
        super.init();
        setContentView(R.layout.activity_mygoldcoins);
        initView();
        initData();
        mWatcher = Observerable.getInstance();
        mWatcher.subscribe(Observerable.ObserverableType.EXCHANGE, this);
    }

    protected void initData() {
        super.initData();
        if (1 == BudgetOrExchange) {
            loadBudget();
            selrct(1);
        } else if (2 == BudgetOrExchange) {
            loadExchangeHistory();
            selrct(2);
        }
        loadToadyIncome();
    }

    protected void initView() {
        super.initView();
        context = MyGoldCoinsActivity.this;
        BudgetOrExchange = getIntent().getIntExtra("BudgetOrExchange", 1);
        llEarnCoins = (LinearLayout) findViewById(R.id.llEarnCoins);
        ll_Budget = (LinearLayout) findViewById(R.id.ll_Budget);
        ll_Exchange = (LinearLayout) findViewById(R.id.ll_Exchange);
        llIncomeHistory = (LinearLayout) findViewById(R.id.llIncomeHistory);
        llExchangeHistory = (LinearLayout) findViewById(R.id.llExchangeHistory);
        tvIncomeHistory = (TextView) findViewById(R.id.tvIncomeHistory);
        tvExchangeHistory = (TextView) findViewById(R.id.tvExchangeHistory);
        tvTodayEarnings = (TextView) findViewById(R.id.tvTodayEarnings);
        tvGoldCoinsNum = (TextView) findViewById(R.id.tvGoldCoinsNum);
        img_income_select_bot = (ImageView) findViewById(R.id.img_income_select_bot);
        img_exchange_select_bot = (ImageView) findViewById(R.id.img_exchange_select_bot);

        scrollView = (PullableScrollView) findViewById(R.id.pullScrollView);
        scrollView.smoothScrollTo(0, 20);// 置顶

        setLeftIncludeTitle("我的金币");
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);

        myllheader = (LinearLayout) findViewById(R.id.my_ll_heander);
        llfirst = (LinearLayout) findViewById(R.id.my_coins_crossband_first);
        llsecond = (LinearLayout) findViewById(R.id.my_coins_crossband_second);
        llIncomeHistory_second = (LinearLayout) findViewById(R.id.llIncomeHistory_second);
        llExchangeHistory_second = (LinearLayout) findViewById(R.id.llExchangeHistory_second);
        tvIncomeHistory_second = (TextView) findViewById(R.id.tvIncomeHistory_second);
        tvExchangeHistory_second = (TextView) findViewById(R.id.tvExchangeHistory_second);
        img_income_select_bot_second = (ImageView) findViewById(R.id.img_income_select_bot_second);
        img_exchange_select_bot_second = (ImageView) findViewById(R.id.img_exchange_select_bot_second);

        refresh_view = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refresh_view.setOnRefreshListener(this);

        llIncomeHistory.setOnClickListener(this);
        llExchangeHistory.setOnClickListener(this);
        llIncomeHistory_second.setOnClickListener(this);
        llExchangeHistory_second.setOnClickListener(this);

        llEarnCoins.setOnClickListener(this);

//		tvIncomeHistory.setTextColor(getResources().getColor(R.color.blue_gray));
//		tvExchangeHistory.setTextColor(getResources().getColor(R.color.gray));
//		img_income_select_bot.setVisibility(View.VISIBLE);
//		img_exchange_select_bot.setVisibility(View.INVISIBLE);

        totalCoins = getIntent().getStringExtra("totalCoins");
        tvGoldCoinsNum.setText(totalCoins);

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        myllheader.measure(w, h);
        int menuHeight = myllheader.getMeasuredHeight();
        top = menuHeight + 38;
        scrollView.setOnMyScrollListener(this);

    }

    private void loadToadyIncome() {
        User user = WangYuApplication.getUser(this);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.TODAY_INCOME, map, HttpConstant.TODAY_INCOME);
        }
    }

    private void loadBudget() {
        showLoading();
        User user = WangYuApplication.getUser(this);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("type", "1");
            map.put("page", budgetPage + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.COIN_HISTORY, map, HttpConstant.COIN_HISTORY);
        }
    }

    private void loadExchangeHistory() {
        showLoading();
        User user = WangYuApplication.getUser(this);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("type", "1");
        map.put("page", exchangePage + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EXCHANGE_RECORD, map, HttpConstant.EXCHANGE_RECORD);
    }

    private void initBudgetList() {
        budgetList.clear();
        String temp = "";
        for (int i = 0; i < budgets.size(); i++) {
            if (!TextUtils.isEmpty(budgets.get(i).getDate())) {
                String date = DateUtil.dateToStrLong(budgets.get(i).getDate());
                if (!temp.equals(date)) {
                    temp = date;
                    budgetList.add(temp);
                    budgetList.add(budgets.get(i));
                } else {
                    budgetList.add(budgets.get(i));
                }
            }
        }
        creatlistview_budge();
    }

    private void initExchangeList() {
        exchangeList.clear();
        String temp = "";
        for (int i = 0; i < exchanges.size(); i++) {
            if (!TextUtils.isEmpty(exchanges.get(i).getCreateDate())) {
                String date = DateUtil.dateToStrLong(exchanges.get(i).getCreateDate());
                if (!temp.equals(date)) {
                    temp = date;
                    exchangeList.add(temp);
                    exchangeList.add(exchanges.get(i));
                } else {
                    exchangeList.add(exchanges.get(i));
                }
            }
        }
        creatlistview_exchange();
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            }
            if (TextUtils.isEmpty(obj.toString())) {
                return;
            }
            if (method.equals(HttpConstant.COIN_HISTORY)) {
                isFirstLoadBudget = false;

                if (budgetPage == 1) {
                    refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    refresh_view.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                JSONObject jsonObj = new JSONObject(obj.toString());
                budgetsIsLast = jsonObj.getInt("isLast");
                String listStr = jsonObj.getString("list");
                List<BudgetCoin> newBudgets = new Gson().fromJson(listStr, new TypeToken<List<BudgetCoin>>() {
                }.getType());
                if (budgetPage == 1) {
                    budgets.clear();
                }
                budgets.addAll(newBudgets);
                initBudgetList();

            } else if (method.equals(HttpConstant.EXCHANGE_RECORD)) {
                isFirstLoadExchange = false;
                if (exchangePage == 1) {
                    refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    refresh_view.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                JSONObject jsonObj = object.getJSONObject("object");
                exchangeIsLast = jsonObj.getInt("isLast");
                JSONArray listStr = jsonObj.getJSONArray("list");
                List<Exchange> newExchanges = new Gson().fromJson(listStr.toString(), new TypeToken<List<Exchange>>() {
                }.getType());
                if (exchangePage == 1) {
                    exchanges.clear();
                }
                exchanges.addAll(newExchanges);
                initExchangeList();

            } else if (method.equals(HttpConstant.TODAY_INCOME)) {

                JSONObject jsonObj = new JSONObject(obj.toString());
                int incomeNum = jsonObj.getInt("income");
                tvTodayEarnings.setText("+" + incomeNum);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        showToast(errMsg);
    }

    private void creatlistview_budge() {
        ll_Budget.removeAllViews();
        for (int i = 0; i < budgetList.size(); i++) {
            ll_Budget.addView(creatItem(budgetList.get(i)));
        }
    }

    private View creatItem(Object object) {
        View v = null;
        if (object instanceof BudgetCoin) {
            v = LayoutInflater.from(context).inflate(R.layout.layout_budget_item, null);
            ivGoldType = (ImageView) v.findViewById(R.id.ivGoldType);
            tvGoldType = (ImageView) v.findViewById(R.id.tvGoldType);
            tvGoldTitle = (TextView) v.findViewById(R.id.tvGoldTitle);
            tvGoldNum = (TextView) v.findViewById(R.id.tvGoldNum);
            BudgetCoin budget = (BudgetCoin) object;
            if (budget.getType() == 1) {
                AsyncImage.loadLocalPhoto(context, R.drawable.task_icon, ivGoldType);
            } else if (budget.getType() == 2) {
                AsyncImage.loadLocalPhoto(context, R.drawable.invate_icon, ivGoldType);
            } else if (budget.getType() == 3) {
                AsyncImage.loadLocalPhoto(context, R.drawable.exchange_icon, ivGoldType);
            } else {
                AsyncImage.loadLocalPhoto(context, R.drawable.exchange_icon, ivGoldType);
            }
            if (budget.getDirection() == -1) {
                tvGoldType.setImageResource(R.drawable.icon_coin_sub);
            } else if (budget.getDirection() == 1) {
                tvGoldType.setImageResource(R.drawable.icon_coin_add);
            }
            tvGoldTitle.setText(budget.getName());
            tvGoldNum.setText(budget.getCoin() + "");

        } else if (object instanceof String) {
            v = LayoutInflater.from(context).inflate(R.layout.layout_date_item, null);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvDate.setText(object.toString());
        }
        return v;
    }

    private void creatlistview_exchange() {
        ll_Exchange.removeAllViews();
        for (int i = 0; i < exchangeList.size(); i++) {
            ll_Exchange.addView(creatItem_exchange(exchangeList.get(i)));
        }
    }

    private View creatItem_exchange(Object object) {
        View v = null;
        if (object instanceof String) {
            v = View.inflate(context, R.layout.layout_date_item, null);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvDate.setText(object.toString());
        } else if (object instanceof Exchange) {
            // FIXME: 2016/5/17 
            v = View.inflate(context, R.layout.layout_exchange_item, null);
            ivGoldType = (ImageView) v.findViewById(R.id.ivGoldType);
            tvGoldTitle = (TextView) v.findViewById(R.id.tvGoldTitle);
            TextView tvState = (TextView) v.findViewById(R.id.tvState);
            Exchange exchange = (Exchange) object;
            String[] stateArr;
            if (exchange.getType() == 2) {  //众筹夺宝
                stateArr = getResources().getStringArray(R.array.crowdstate);
            } else {
                stateArr = getResources().getStringArray(R.array.exchangestate);
            }
            tvState.setText(stateArr[exchange.getState()]);
            AsyncImage.loadLocalPhoto(context, R.drawable.exchange_icon, ivGoldType);
            tvGoldTitle.setText(exchange.getName());
            v.setTag(exchange);
            v.setOnClickListener(this);
            v.setTag(exchange.getId());
        }
        return v;
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (currentType == BUDGET_TYPE) {
            budgetPage = 1;
            loadBudget();
            refresh_view.loadmoreFinish(PullToRefreshLayout.NOMORE);
        } else if (currentType == EXCHANGE_TYPE) {
            exchangePage = 1;
            loadExchangeHistory();
        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        if (currentType == BUDGET_TYPE) {
            if (budgetsIsLast == 0) {
                budgetPage++;
                loadBudget();
            } else {
                refresh_view.loadmoreFinish(PullToRefreshLayout.NOMORE);
            }
        } else if (currentType == EXCHANGE_TYPE) {
            if (exchangeIsLast == 0) {
                exchangePage++;
                loadExchangeHistory();
            } else {
                refresh_view.loadmoreFinish(PullToRefreshLayout.NOMORE);
            }
        }

    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.llIncomeHistory:
                ll_Budget.setVisibility(View.VISIBLE);
                ll_Exchange.setVisibility(View.GONE);
                tvIncomeHistory.setTextColor(getResources().getColor(R.color.blue_gray));
                tvExchangeHistory.setTextColor(getResources().getColor(R.color.gray));
                img_income_select_bot.setVisibility(View.VISIBLE);
                img_exchange_select_bot.setVisibility(View.INVISIBLE);
                currentType = BUDGET_TYPE;
                if (isFirstLoadBudget) {
                    loadBudget();
                }
                break;
            case R.id.llExchangeHistory:
                ll_Budget.setVisibility(View.GONE);
                ll_Exchange.setVisibility(View.VISIBLE);
                tvExchangeHistory.setTextColor(getResources().getColor(R.color.blue_gray));
                tvIncomeHistory.setTextColor(getResources().getColor(R.color.gray));
                img_exchange_select_bot.setVisibility(View.VISIBLE);
                img_income_select_bot.setVisibility(View.INVISIBLE);
                currentType = EXCHANGE_TYPE;
                if (isFirstLoadExchange) {
                    loadExchangeHistory();
                }
                break;
            case R.id.llIncomeHistory_second:
                tvIncomeHistory_second.setTextColor(getResources().getColor(R.color.blue_gray));
                tvExchangeHistory_second.setTextColor(getResources().getColor(R.color.gray));
                img_income_select_bot_second.setVisibility(View.VISIBLE);
                img_exchange_select_bot_second.setVisibility(View.INVISIBLE);
                currentType = BUDGET_TYPE;
                if (isFirstLoadBudget) {
                    loadBudget();
                }
                ll_Budget.setVisibility(View.VISIBLE);
                ll_Exchange.setVisibility(View.GONE);
                break;
            case R.id.llExchangeHistory_second:
                tvExchangeHistory_second.setTextColor(getResources().getColor(R.color.blue_gray));
                tvIncomeHistory_second.setTextColor(getResources().getColor(R.color.gray));
                img_exchange_select_bot_second.setVisibility(View.VISIBLE);
                img_income_select_bot_second.setVisibility(View.INVISIBLE);
                currentType = EXCHANGE_TYPE;
                if (isFirstLoadExchange) {
                    loadExchangeHistory();
                }
                ll_Budget.setVisibility(View.GONE);
                ll_Exchange.setVisibility(View.VISIBLE);
                break;
            case R.id.ibLeft:
                LogUtil.d("MyViewClient", "ibLeft 销毁此页面");
                onBackPressed();
                break;
            case R.id.rlExchangeItem:

                i = new Intent(context, ExchangeDetailActivity.class);
                i.putExtra("exchangeID", v.getTag().toString());
                startActivity(i);
                break;
            case R.id.llEarnCoins:
                i = new Intent(context, CoinsTaskActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    @Override
    public void move(int x, int y, int oldx, int oldy) {
        // TODO Auto-generated method stub
        if (y > top) {
            if (currentType == BUDGET_TYPE) {
                selrct(4);
            } else if (currentType == EXCHANGE_TYPE) {
                selrct(5);
            }
            llfirst.setVisibility(View.INVISIBLE);
            llsecond.setVisibility(View.VISIBLE);

        } else {
            if (currentType == BUDGET_TYPE) {
                selrct(1);
            } else if (currentType == EXCHANGE_TYPE) {
                selrct(2);
            }
            llfirst.setVisibility(View.VISIBLE);
            llsecond.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @param i
     */
    private void selrct(int i) {
        switch (i) {
            case 1:
                tvIncomeHistory.setTextColor(getResources().getColor(R.color.blue_gray));
                tvExchangeHistory.setTextColor(getResources().getColor(R.color.gray));
                img_income_select_bot.setVisibility(View.VISIBLE);
                img_exchange_select_bot.setVisibility(View.INVISIBLE);
                currentType = BUDGET_TYPE;
                ll_Budget.setVisibility(View.VISIBLE);
                ll_Exchange.setVisibility(View.GONE);
                break;
            case 2:
                tvExchangeHistory.setTextColor(getResources().getColor(R.color.blue_gray));
                tvIncomeHistory.setTextColor(getResources().getColor(R.color.gray));
                img_exchange_select_bot.setVisibility(View.VISIBLE);
                img_income_select_bot.setVisibility(View.INVISIBLE);
                currentType = EXCHANGE_TYPE;

                ll_Budget.setVisibility(View.GONE);
                ll_Exchange.setVisibility(View.VISIBLE);
                break;
            case 3:
                tvIncomeHistory.setTextColor(getResources().getColor(R.color.blue_gray));
                tvExchangeHistory.setTextColor(getResources().getColor(R.color.gray));
                img_income_select_bot.setVisibility(View.VISIBLE);
                img_exchange_select_bot.setVisibility(View.INVISIBLE);

                tvIncomeHistory_second.setTextColor(getResources().getColor(R.color.blue_gray));
                tvExchangeHistory_second.setTextColor(getResources().getColor(R.color.gray));
                img_income_select_bot_second.setVisibility(View.VISIBLE);
                img_exchange_select_bot_second.setVisibility(View.INVISIBLE);
                break;
            case 4:
                currentType = BUDGET_TYPE;
                tvIncomeHistory_second.setTextColor(getResources().getColor(R.color.blue_gray));
                tvExchangeHistory_second.setTextColor(getResources().getColor(R.color.gray));
                img_income_select_bot_second.setVisibility(View.VISIBLE);
                img_exchange_select_bot_second.setVisibility(View.INVISIBLE);
                ll_Budget.setVisibility(View.VISIBLE);
                ll_Exchange.setVisibility(View.GONE);
                break;
            case 5:
                currentType = EXCHANGE_TYPE;
                tvExchangeHistory_second.setTextColor(getResources().getColor(R.color.blue_gray));
                tvIncomeHistory_second.setTextColor(getResources().getColor(R.color.gray));
                img_exchange_select_bot_second.setVisibility(View.VISIBLE);
                img_income_select_bot_second.setVisibility(View.INVISIBLE);

                ll_Budget.setVisibility(View.GONE);
                ll_Exchange.setVisibility(View.VISIBLE);
                break;
            case 6:
                img_income_select_bot.setVisibility(View.INVISIBLE);
                img_exchange_select_bot.setVisibility(View.INVISIBLE);
                break;
            case 7:
                img_exchange_select_bot_second.setVisibility(View.INVISIBLE);
                img_income_select_bot_second.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public <T> void update(T... data) {
        String id = data[0].toString();
        for (Exchange exchange : exchanges) {
            if (id.equals(exchange.getId())) {
                View view = ll_Exchange.findViewWithTag(id);
                TextView tv = (TextView) view.findViewById(R.id.tvState);
                tv.setText("兑奖中");
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWatcher.unSubscribe(Observerable.ObserverableType.EXCHANGE, this);
    }
}
