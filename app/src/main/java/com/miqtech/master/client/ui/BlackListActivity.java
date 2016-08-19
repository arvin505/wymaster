package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.BlackListAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.BlackUser;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/8.
 */
public class BlackListActivity extends BaseActivity implements View.OnClickListener, BlackListAdapter.BlackListOnClickListener {

    private GridView gvBlack;
    private BlackListAdapter adapter;
    private List<BlackUser> users = new ArrayList<BlackUser>();
    private int page = 1;
    private int isLast;
    private int position;
    private TextView tv_finish;
    private User user;
    private Context context;
    private String blackList = "";// 用来提交的黑名单id
    private BlackUser bb = new BlackUser();
    private ImageView noDataImage;
    private int current = 0;// 0没有显示删除按钮，1表示显示了删除按钮
    private Boolean isDelect = true;

    protected void init() {
        super.init();
        setContentView(R.layout.activity_blacklist);
        initView();
        initData();
    }

    protected void initData() {
        adapter = new BlackListAdapter(context, users, this);
        gvBlack.setAdapter(adapter);
        loadBlackList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBlackList();
    }

    @Override
    protected void initView() {
        super.initView();
        context = BlackListActivity.this;
        gvBlack = (GridView) findViewById(R.id.gvBlack);
        tv_finish = (TextView) findViewById(R.id.tvfinish);
        noDataImage = (ImageView) findViewById(R.id.noDataImage);
        // refresh_view = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        setLeftIncludeTitle("黑名单");
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        // refresh_view.setOnRefreshListener(this);
        tv_finish.setOnClickListener(this);

        gvBlack.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    private void loadBlackList() {
        showLoading();
        User user = WangYuApplication.getUser(this);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            //map.put("page", page + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.BLACK_LIST, map, HttpConstant.BLACK_LIST);
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        Object obj;
        try {
            if (object.has("object")) {
                obj = object.getString("object");
            } else {
                obj = object.toString();
            }
            if (HttpConstant.BLACK_LIST.equals(method)) {
                JSONObject jsonObj = new JSONObject(obj.toString());
                String listStr = jsonObj.getString("list");
                isLast = jsonObj.getInt("isLast");
                List<BlackUser> newUsers = new Gson().fromJson(listStr, new TypeToken<List<BlackUser>>() {
                }.getType());
                // if (page == 1) {
                // users.clear();
                // refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
                // } else {
                // users.remove(users.size() - 1);
                // refresh_view.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                // }
                users.clear();
                users.addAll(newUsers);
                if (!users.isEmpty()) {
                    users.add(bb);
                }

                adapter.notifyDataSetChanged();
                show();
            } else if (HttpConstant.BLACK_OR_NOT.equals(method)) {
                showToast("删除成功");
                blackList = "";
                if (users.size() > 1) {
                    current = 0;
                } else {
                    users.clear();
                    adapter.notifyDataSetChanged();
                }
                if (isDelect == false) {
                    isDelect = true;
                }
                hideStatu();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(errMsg);
    }

    private void show() {
        gvBlack.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (0 == current) {
                    showStatu();
                    current = 1;
                } else if (1 == current) {
                    hideStatu();
                    current = 0;
                }
                return true;
            }
        });

        gvBlack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == (users.size() - 1)) {
                    if (current == 0 && users.size() > 1) {
                        showStatu();
                        current = 1;
                    }
                } else {
                    Intent intent = new Intent(context, PersonalHomePageActivity.class);
                    intent.putExtra("id", users.get(arg2).getId() + "");
                    startActivity(intent);
                }
            }
        });

    }

    // @Override
    // public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
    // page = 1;
    // loadBlackList();
    // }
    //
    // @Override
    // public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
    // if (isLast == 0) {
    // page++;
    // loadBlackList();
    // } else if (isLast == 1) {
    // refresh_view.loadmoreFinish(PullToRefreshLayout.NOMORE);
    // }
    // }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                showdialog();
                break;
            case R.id.tvfinish:
                postBlacklist();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showdialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 提交删除黑名单的id
     */
    private void postBlacklist() {
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        if ("".equals(blackList) || TextUtils.isEmpty(blackList)) {
            if (users.size() > 1) {
                current = 0;
            }
            if (isDelect == false) {
                isDelect = true;
            }
            hideStatu();
            return;
        }
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("blackUserId", blackList);
            map.put("type", 0 + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.BLACK_OR_NOT, map, HttpConstant.BLACK_OR_NOT);
    }

    /**
     * 当有删除图片显示时 ：点击返回时弹出是否放弃编辑。没有删除图片显示时直接返回
     */
    private void showdialog() {
        if (users == null || users.size() == 0 || "[]".equals(users)) {
            finish();
        } else {
            if (1 == users.get(0).getIsshow() || isDelect == false) {

                final Dialog mDialog = new Dialog(context, R.style.register_style);
                mDialog.setContentView(R.layout.dialog_register_marked_words);
                TextView title_tv = (TextView) mDialog.findViewById(R.id.dialog_title_register);
                TextView context_tv = (TextView) mDialog.findViewById(R.id.dialog_content_register);
                TextView ok_bt = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
                TextView no_bt = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
                View vv = mDialog.findViewById(R.id.dialog_line_no_pact);
                vv.setVisibility(View.VISIBLE);
                no_bt.setVisibility(View.VISIBLE);

                title_tv.setText("提示");
                title_tv.setVisibility(View.VISIBLE);

                context_tv.setText("确认放弃修改?");
                context_tv.setVisibility(View.VISIBLE);

                ok_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        mDialog.dismiss();
                    }
                });

                no_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                mDialog.show();
            } else {
                finish();
            }
        }
    }

    @Override
    public void blackHandle(int position) {
        if ("".equals(blackList)) {
            blackList = users.get(position).getId() + "";
        } else {
            blackList = blackList + "," + users.get(position).getId();
        }
        if (isDelect == true) {
            isDelect = false;
        }
        users.remove(position);
        adapter.notifyDataSetChanged();
    }

    /**
     * 显示每个item红色删除按钮
     */
    private void showStatu() {
        for (int i = 0; i < users.size() - 1; i++) {
            users.get(i).setIsshow(1);
        }
        adapter.notifyDataSetChanged();
        tv_finish.setEnabled(true);
        tv_finish.setClickable(true);
        tv_finish.setTextColor(getResources().getColor(R.color.white));
    }

    /**
     * 隐藏每个item红色删除按钮
     */
    private void hideStatu() {
        for (int i = 0; i < users.size() - 1; i++) {
            users.get(i).setIsshow(0);
        }
        adapter.notifyDataSetChanged();
        tv_finish.setEnabled(false);
        tv_finish.setTextColor(getResources().getColor(R.color.font_black));
    }
}
