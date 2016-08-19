package com.miqtech.master.client.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.SearchEditTextAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Game;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.http.ResponseListener;
import com.miqtech.master.client.ui.SearchGameListActivity;
import com.miqtech.master.client.utils.ToastUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.utils.PreferencesUtil;

import org.json.JSONObject;

public class SearchGame extends Dialog implements ResponseListener,
        TextWatcher, View.OnClickListener, OnEditorActionListener {
    // //自定义的Loading界面
    LoadingDialog progressDialog = null;
    private Activity mContext;
    private LinearLayout historyGameList;
    private List<Game> games = new ArrayList<Game>();
    private SearchEditText searchEditText;
    private SearchEditTextAdapter searchAdapter;
    private LinearLayout searchBottom;
    private LayoutInflater inflater;
    private View parentView;
    private List<String> items;
    private View clearHistory;
    private View iv_Search;
    private Intent intent;

    public Game game;


    public SearchGame(Activity context, boolean cancelable,
                      OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
        init();
    }

    public SearchGame(Activity context, int theme) {
        super(context, theme);
        mContext = context;
        init();
    }

    public SearchGame(Activity context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        inflater = LayoutInflater.from(mContext);
        parentView = inflater.inflate(R.layout.search_game, null);
        setContentView(parentView);
        Window dialogWindow = this.getWindow();
        dialogWindow.setWindowAnimations(R.style.windowStyle);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP);
        lp.x = 0; // 新位置X坐标
        lp.y = Utils.dip2px(mContext, 80); // 新位置Y坐标
        lp.width = (int) WangYuApplication.WIDTH
                - Utils.dip2px(mContext, 40); // 宽度
        lp.height = (int) WangYuApplication.HEIGHT
                - Utils.dip2px(mContext, 80); // 高度
        dialogWindow.setAttributes(lp);
        initView();
    }

    private void initView() {
        searchEditText = (SearchEditText) parentView
                .findViewById(R.id.searchEditText);
        searchBottom = (LinearLayout) parentView
                .findViewById(R.id.searchBottom);
        searchAdapter = new SearchEditTextAdapter(mContext);
        searchEditText.setAdapter(searchAdapter);
        searchEditText.addTextChangedListener(this);
        clearHistory = parentView.findViewById(R.id.history_title);
        iv_Search = parentView.findViewById(R.id.iv_search);
        historyGameList = (LinearLayout) parentView
                .findViewById(R.id.historyData);
        clearHistory.setOnClickListener(this);
        iv_Search.setOnClickListener(this);
        searchEditText.setOnEditorActionListener(this);
    }

    private void initData() {

    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        super.dismiss();
    }

    @Override
    public void cancel() {
        searchEditText.setText("");
        super.cancel();
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        Object obj = null;
        try {
            obj = object.getString("object");
        }catch (Exception e){
            e.printStackTrace();
        }

        Gson gs = new Gson();
        String objStr = obj.toString();
        if (method.equals(HttpConstant.NEARBY_SEARCH_LIST)) {
            if (!TextUtils.isEmpty(objStr) && !"success".equals(objStr)) {
                games = gs.fromJson(objStr, new TypeToken<List<Game>>() {
                }.getType());
            }
            if (games.size() > 0) {
                initData();
            }
        }
    }

    @Override
    public void onError(String errMsg, String method) {

    }

    @Override
    public void onFaild(JSONObject object, String method) {

    }

    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int res) {
        String msg = mContext.getResources().getString(res);
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 要显示的文本
     *
     * @param text
     */
    public void showLoading(String text) {
        if (progressDialog == null) {
            progressDialog = LoadingDialog.createDialog(mContext, text);
            progressDialog.show();
        } else if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 显示Loading框
     * <p/>
     * 提示文本默认为“正在加载”
     */
    public void showLoading() {
        this.showLoading(null);
    }

    /**
     * 将显示的Loading框关闭
     */
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void show() {
        super.show();
        parentView.invalidate();
        updateHistory();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // ToastUtil.showToast("CharSequence"+s+"   start"+start+"   before"+before+"  count"+count,
        // mContext);
        if (s.length() > 0 || !s.equals("")) {
            searchBottom.setVisibility(View.GONE);
        }
        if (s.length() <= 0) {
            searchBottom.setVisibility(View.VISIBLE);
            updateHistory();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void updateHistory() {
        historyGameList.removeAllViews();
        items = PreferencesUtil.readGameHistory(mContext);
        for (String string : items) {
            if (!string.equals("")) {
                View content = (View) inflater.inflate(R.layout.search_item,
                        null);
                TextView gameText = (TextView) content
                        .findViewById(R.id.content_text);
                gameText.setText(string);
                gameText.setPadding(Utils.dip2px(mContext, 16), 0, 0, 0);
                content.setTag(string);
                content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchEditText.setText(v.getTag().toString());
                        searchBottom.setVisibility(View.VISIBLE);
                        searchEditText.dismissDropDown();
                    }
                });
                historyGameList.addView(content);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_title:
                PreferencesUtil.removeGameHistory(mContext);
                updateHistory();
                ((SearchEditTextAdapter) searchEditText.getAdapter()).initData(0);
                break;
            case R.id.iv_search:
                toResult();
                break;
        }
    }

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;


    private void toResult() {
        String text = "";
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            text = searchEditText.getText().toString();
            if (text.trim().length() == 0 || text.contains("|")) {
                ToastUtil.showToast("输入有误，或者有特殊字符", mContext);
                return;
            }
            PreferencesUtil.saveGameHistory(mContext, text);
            ((SearchEditTextAdapter) searchEditText.getAdapter()).initData(0);
            cancel();
        }
        intent = new Intent(mContext, SearchGameListActivity.class);
        intent.putExtra("searchText", text);
        mContext.startActivity(intent);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            // 修改回车键功能
            // 先隐藏键盘
            hideSolfInput();
            toResult();
        }
        return false;
    }

    private void hideSolfInput() {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

}