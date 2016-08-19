package com.miqtech.master.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;


import com.miqtech.master.client.adapter.AttentionAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.Fans;
import com.miqtech.master.client.entity.User;

import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.PullToRefreshLayout;
import com.miqtech.master.client.view.RefreshLayout;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;

public class AttentionListActivity extends BaseActivity implements  OnClickListener,
        TextWatcher, AttentionAdapter.AttentionListener {
    private PullToRefreshListView prlvFans;
    private HasErrorListView lvFans, lvSearchFans;
    private LinearLayout llSearch;
    private EditText edtSearch;
    private ImageView ivCancelSearch;
    private String id;
    private int page = 1;
    private List<Fans> fans = new ArrayList<Fans>();
    private List<Fans> searchFans = new ArrayList<Fans>();
    private AttentionAdapter adapter;
    private AttentionAdapter searchAdapter;
    private int isLast;
    private ImageView back;
    private ImageView search;
    private boolean isLoadMore = false;
    private int total = 0;
    private int selectPosition = -1;
    private Context context;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    protected void init() {
        super.init();
        setContentView(R.layout.activity_fanslist);
        initView();
        initData();
    }

    protected void initView() {
        super.initView();
        context = AttentionListActivity.this;
        prlvFans = (PullToRefreshListView)findViewById(R.id.prlvFans);
        prlvFans.setMode(PullToRefreshBase.Mode.BOTH);
        prlvFans.setScrollingWhileRefreshingEnabled(true);
        lvFans =prlvFans.getRefreshableView();
        lvSearchFans = (HasErrorListView) findViewById(R.id.lvSearchFans);

        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        ivCancelSearch = (ImageView) findViewById(R.id.ivCancelSearch);

        prlvFans.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadUserAttention();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (fans.size() < total) {
                    page++;
                    loadUserAttention();
                    isLoadMore = true;
                }else {
                    prlvFans.onRefreshComplete();
                    showToast(R.string.nomore);
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });
//        refresh_view.setOnLoadListener(new RefreshLayout.OnLoadListener() {
//            @Override
//            public void onLoad() {
//
//                if (fans.size() < total) {
//                    page++;
//                    loadUserAttention();
//                    isLoadMore = true;
//                }else {
//                    refresh_view.setLoading(false);
//                    showToast(R.string.nomore);
//                }
//            }
//        });
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        search = (ImageView) findViewById(R.id.ivHandle);
        search.setOnClickListener(this);
        setLeftIncludeTitle("关注列表");
        edtSearch.addTextChangedListener(this);
        ivCancelSearch.setOnClickListener(this);

    }

    protected void initData() {
        super.initData();
        id = getIntent().getStringExtra("id");
        User user = WangYuApplication.getUser(this);
        if (user != null && user.getId().equals(id)) {
            adapter = new AttentionAdapter(fans, this, this, 1);
            searchAdapter = new AttentionAdapter(searchFans, this, this, 1);
        } else {
            adapter = new AttentionAdapter(fans, this, this, 0);
            searchAdapter = new AttentionAdapter(searchFans, this, this, 0);
        }

        lvFans.setErrorView("还木有关注小伙伴呢，赶紧去找个牛X大腿抱抱");
        lvFans.setAdapter(adapter);
        lvSearchFans.setErrorView("桑心，木有找到呀");
        lvSearchFans.setAdapter(searchAdapter);
        loadUserAttention();
    }

    private void loadUserAttention() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("userId", id);
        params.put("page", page + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ATTENTION_LIST, params, HttpConstant.ATTENTION_LIST);
    }

    private void searchAttention(String str) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", id);
        params.put("nickName", str + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SEARCH_ATTENTIONS, params, HttpConstant.ATTENTION_LIST);
    }

    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        prlvFans.onRefreshComplete();
        super.onSuccess(object, method);
        try {
            if (HttpConstant.ATTENTION_LIST.equals(method)) {
                String obj = object.getJSONObject("object").toString();
                if (TextUtils.isEmpty(obj.toString())) {
                    return;
                }
                try {
                    JSONObject jsonObj = new JSONObject(obj.toString());
                    String listStr = jsonObj.getString("list");
                    isLast = jsonObj.getInt("isLast");
                    total = jsonObj.getInt("total");
                    List<Fans> newFans = new Gson().fromJson(listStr, new TypeToken<List<Fans>>() {
                    }.getType());

                    if (newFans != null && !newFans.isEmpty()) {
                        if (page == 1) {
                            fans.clear();
                        }
                        fans.addAll(newFans);
                        lvFans.setErrorShow(false);
                        adapter.notifyDataSetChanged();
                    } else {
                        if (isLoadMore) {
                            showToast(R.string.nomore);
                            page--;
                        }
                        if (page == 1){
                            lvFans.setErrorShow(true);
                        }
                    }
                    if (isLoadMore) {
                        isLoadMore = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (HttpConstant.SEARCH_ATTENTIONS.equals(method)) {
                String obj = object.getJSONObject("object").toString();
                if (TextUtils.isEmpty(obj.toString())) {
                    return;
                }
                List<Fans> newSearchFans = new Gson().fromJson(obj.toString(), new TypeToken<List<Fans>>() {
                }.getType());
                searchFans.clear();
                searchFans.addAll(newSearchFans);
                lvSearchFans.setErrorShow(true);
                searchAdapter.notifyDataSetChanged();
            } else if (HttpConstant.ATTENTION_USER.equals(method)) {
                Fans fan = fans.get(selectPosition);
                if (fan.getIs_valid() == 1) {
                    fan.setIs_valid(0);
                    showToast("取消关注成功");
                } else if (fan.getIs_valid() == 0) {
                    fan.setIs_valid(1);
                    showToast("关注成功");
                }
                BroadcastController.sendUserChangeBroadcase(context);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onError(String method, String errorInfo) {
        super.onError(method, errorInfo);
        hideLoading();
       prlvFans.onRefreshComplete();
        if (isLoadMore) {
            isLoadMore = false;
            page--;
        }
        if (fans.isEmpty()){
            lvFans.setErrorShow(true);
        }else {
            lvFans.setErrorShow(false);
        }
        if (searchFans.isEmpty()){
            lvSearchFans.setErrorShow(true);
        }else {
            lvSearchFans.setErrorShow(false);
        }
    }

    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        page = 1;
        loadUserAttention();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.rlHandle:
                if (llSearch.getVisibility() == View.VISIBLE) {

//				Animation animation = AnimationUtils.loadAnimation(this, R.anim.dialog_in);
//				llSearch.startAnimation(animation);
//				animation.setAnimationListener(new AnimationListener() {
//					
//					@Override
//					public void onAnimationStart(Animation animation) {
//						// TODO Auto-generated method stub
//						
//					}
//					
//					@Override
//					public void onAnimationRepeat(Animation animation) {
//						// TODO Auto-generated method stub
//						
//					}
//					
//					@Override
//					public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
                    llSearch.setVisibility(View.GONE);
                    lvSearchFans.setVisibility(View.GONE);
                    // 关闭输入法
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
//					}
//				});

                } else if (llSearch.getVisibility() == View.GONE) {
                    llSearch.setVisibility(View.VISIBLE);
                    lvSearchFans.setVisibility(View.VISIBLE);
                    // 打开输入法
                    edtSearch.setFocusable(true);
                    edtSearch.setFocusableInTouchMode(true);
                    edtSearch.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager) edtSearch.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(edtSearch, 0);
                }
                break;
            case R.id.ivCancelSearch:
                edtSearch.setText("");
                break;
            default:
                break;
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    public void afterTextChanged(Editable s) {
        showToast(edtSearch.getText().toString());
        if (!TextUtils.isEmpty(edtSearch.getText().toString())) {
            page = 1;
            ivCancelSearch.setVisibility(View.VISIBLE);
            searchAttention(edtSearch.getText().toString());
        } else {
            ivCancelSearch.setVisibility(View.GONE);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (llSearch.getVisibility() == View.GONE) {
                finish();
            } else if (llSearch.getVisibility() == View.VISIBLE) {
                llSearch.setVisibility(View.GONE);
                lvSearchFans.setVisibility(View.GONE);
            }
        }
        return false;
    }

    @Override
    public void attentionUser(int id, int position) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("concernId", id + "");
        params.put("type", 1 + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ATTENTION_USER, params, HttpConstant.ATTENTION_USER);
        selectPosition = position;
    }

    @Override
    public void cancelAttentionUser(int id, int position) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("concernId", id + "");
        params.put("type", 0 + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ATTENTION_USER, params, HttpConstant.ATTENTION_USER);
        selectPosition = position;
    }


//	private class FansOnClickListener implements OnItemClickListener{
//
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			Fans fan = fans.get(position);
//			Intent intent = new Intent();
//			intent.setClass(AttentionListActivity.this, PersonalHomePageActivity.class);
//			intent.putExtra("id", fan.getId()+"");
//			startActivity(intent);
//		}
//		
//	}
//	private class SearchFansOnClickListener implements OnItemClickListener{
//
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			Fans fan = searchFans.get(position);
//			Intent intent = new Intent();
//			intent.setClass(AttentionListActivity.this, PersonalHomePageActivity.class);
//			intent.putExtra("id", fan.getId()+"");
//			startActivity(intent);
//		}
//		
//	}
}
