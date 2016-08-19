package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.CoinsTaskAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.CoinsTaskListBean;
import com.miqtech.master.client.entity.TaskInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 金币任务
 * Created by Administrator on 2015/12/3.
 */
public class CoinsTaskActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private User user;// 用户信息
    private Context context;
    private ListView lvTask;// 任务列表
    private View reHeader1, reHeader2, reHeader3;// listview的3个头
    private CoinsTaskAdapter adapter;
    private CoinsTaskListBean coinsTaskListBean;
    private int count = 0;// 用来计数完成了新手任务中的几个
    private List<TaskInfo> dailyTasks;// 每日任务
    private List<TaskInfo> teachingTasks;// 新手任务
    private List<TaskInfo> teachingTasks2 = new ArrayList<TaskInfo>();// 新手任务
    private List<TaskInfo> teachingTasks3 = new ArrayList<TaskInfo>();// 新手任务
    private TaskInfo taskInfoBean;
    private TextView tvTatalCoins;// 新手任务的总金币数
    private TextView tvTitleCoins;//
    private TextView tvFinish;// 新手任务全部完成时的状态
    private ImageView ivicon;//
    private LinearLayout llGoldType;
    private int id;//任务id


    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_coins_task);
        lengthCoding = UMengStatisticsUtil.CODE_3005;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        lvTask = (ListView) findViewById(R.id.lvxinshou);
        setLeftIncludeTitle("金币任务");
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        context = CoinsTaskActivity.this;
        user = WangYuApplication.getUser(context);
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        getTaskData();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 得到每日任务列表
     */
    private void getTaskData() {
        showLoading();
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("clientType", "2");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.COINS_TASK, map, HttpConstant.COINS_TASK);
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            Gson gs = new Gson();
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            }
            String objStr = obj.toString();
            if (method.equals(HttpConstant.COINS_TASK)) {
                coinsTaskListBean = gs.fromJson(objStr, CoinsTaskListBean.class);
                updateListview();
            }
        } catch (JsonSyntaxException e) {
            showToast("数据异常");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        try {
            if (object.has("result")) {
                showToast(object.getString("result"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 填充listView的数据
     */
    private void updateListview() {
        dailyTasks = coinsTaskListBean.getDailyTasks();
        teachingTasks = coinsTaskListBean.getTeachingTasks();
        count = getnum(teachingTasks);
        adapter = new CoinsTaskAdapter(context, dailyTasks);

        reHeader1 = View.inflate(context, R.layout.layout_coin_task_item, null);//新手任务栏
        tvFinish = ((TextView) (reHeader1.findViewById(R.id.tvfinish)));
        tvTitleCoins = ((TextView) (reHeader1.findViewById(R.id.tvGoldTitle)));
        tvTatalCoins = ((TextView) (reHeader1.findViewById(R.id.tvGoldNum)));
        ivicon = ((ImageView) (reHeader1.findViewById(R.id.ivGoldType)));
        llGoldType = ((LinearLayout) (reHeader1.findViewById(R.id.llGoldType)));
        llGoldType.setVisibility(View.GONE);
        tvTatalCoins.setText("+" + getTatalCoins(teachingTasks));
        tvTitleCoins.setText(addconnent("新手任务(", count + "", "/" + teachingTasks.size() + ")"));
        if (count == teachingTasks.size()) {
            tvTatalCoins.setTextColor(getResources().getColor(R.color.orgran_finish_coin_task));
            tvTatalCoins.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.finish_icon), null);
        }

        lvTask.addHeaderView(reHeader1);

        reHeader2 = View.inflate(context, R.layout.layout_budget_two_item, null);//邀请好友栏
        ((TextView) (reHeader2.findViewById(R.id.tvGoldTitle))).setText("邀请好友");
        ((TextView) (reHeader2.findViewById(R.id.tvGoldNum))).setText("+100金币/次");
        lvTask.addHeaderView(reHeader2);
        reHeader3 = View.inflate(context, R.layout.layout_coins_task_header3, null);//每日任务栏
        lvTask.addHeaderView(reHeader3);
        lvTask.setAdapter(adapter);
        lvTask.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long arg3) {
        // TODO Auto-generated method stub
        Intent intent = null;
        teachingTasks2.clear();
        teachingTasks2.addAll(coinsTaskListBean.getTeachingTasks());
        if (parent.getAdapter().getItemId(position) == -1) {
            switch (v.getId()) {
                case R.id.rlBudgetItem://新手任务
                    intent = new Intent(context, NewPeopleTaskActivity.class);
                    intent.putParcelableArrayListExtra("mTasklist", (ArrayList<? extends Parcelable>) teachingTasks2);
                    //intent.putParcelableArrayListExtra("mTasklist",(ArrayList<? extends Parcelable>) teachingTasks3 );
                    startActivity(intent);
                    break;
                case R.id.coins_header2://邀请好友
                    intent = new Intent(context, SubjectActivity.class);
                    intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.INVITEFRIEND);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        } else {
//            id = dailyTasks.get((int) parent.getAdapter().getItemId(position)).getId();
//            intent = new Intent(context, SubjectActivity.class);
//            intent.putExtra("id", id + "");
//            intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.RENWU);
//            startActivity(intent);
        }

    }

    /**
     * 用来计算完成了新手任务中的几个
     *
     * @param list 传入新手任务的数据集
     * @return 完成任务的个数
     */
    private int getnum(List<TaskInfo> list) {
        int qqq = 0;
        for (int i = 0; i < list.size(); i++) {
            taskInfoBean = list.get(i);
            if (1 == taskInfoBean.getAll_accomplish()) {
                qqq++;
            }
        }
        return qqq;
    }

    /**
     * 用来计算新手任务中总的金币数
     *
     * @param list 传入新手任务的数据集
     * @return 返回金币总数
     */
    private int getTatalCoins(List<TaskInfo> list) {
        int aaa = 0;
        for (int i = 0; i < list.size(); i++) {
            taskInfoBean = list.get(i);
            aaa = aaa + taskInfoBean.getCoin();
        }
        return aaa;
    }

    private SpannableStringBuilder addconnent(String cotent1, String content2, String content3) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(cotent1);
        int start = cotent1.length();
        int middle = start + content2.length();
        ssb.append(content2);
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.orange));// 设置文本颜色
                // 去掉下划线
                ds.setUnderlineText(false);
            }
        }, start, middle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ssb.append(content3);
    }


}
