package com.miqtech.master.client.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.SearchEditTextAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.http.RequestUtil;
import com.miqtech.master.client.http.ResponseListener;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.NetbarListV2Activity;
import com.miqtech.master.client.ui.ReleaseWarActivity;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 网吧搜索dialog
 *
 * @author zhangp
 *         http://192.168.16.106:8080/searchLocalNetbar?userId=21&token=sZsSuV
 *         +5U9eJakz3JLmqNQ==&longitude=120&latitude=30
 */
@SuppressLint("ResourceAsColor")
public class SearchDialog extends Dialog implements TextWatcher,
        View.OnClickListener, OnEditorActionListener, ResponseListener {

    // //自定义的Loading界面
    LoadingDialog progressDialog = null;
    private Activity mContext;
    private LinearLayout nearbyNatbarList;
    private LinearLayout historyNatbarList;
    private RelativeLayout rlSearchHistory;
    private List<InternetBarInfo> internetBars = new ArrayList<InternetBarInfo>();
    private SearchEditText searchEditText;
    private SearchEditTextAdapter searchAdapter;
    private LinearLayout searchBottom;
    private LayoutInflater inflater;
    private View parentView;
    private List<String> items;
    private View clearHistory;
    private View iv_Search;
    private Intent intent;

    public InternetBarInfo netbar;

    private boolean isRelease = false;

    private SelectedNetbarListener listener;

    public SearchDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
        init();
    }

    public SearchDialog(Activity context, int theme) {
        super(context, theme);
        mContext = context;
        init();
    }

    public SearchDialog(Activity context, int theme, boolean isRelease, SelectedNetbarListener listener) {
        super(context, theme);
        mContext = context;
        this.isRelease = isRelease;
        this.listener = listener;
        init();
    }

    public SearchDialog(Activity context) {
        super(context);
        mContext = context;
        init();
    }


    private void init() {
        inflater = LayoutInflater.from(mContext);
        parentView = inflater.inflate(R.layout.search_netbar, null);
        setContentView(parentView);
        Window dialogWindow = this.getWindow();
        dialogWindow.setWindowAnimations(R.style.windowStyle);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP);
        lp.x = 0; // 新位置X坐标
        lp.y = Utils.dip2px(mContext, 80); // 新位置Y坐标
        lp.width = (int) WangYuApplication.WIDTH - Utils.dip2px(mContext, 40); // 宽度
        lp.height = (int) WangYuApplication.HEIGHT - Utils.dip2px(mContext, 80); // 高度
        //dialogWindow.setAttributes(lp);
        initView();
    }

    public void startRequst() {
        if (Constant.longitude == Constant.latitude && internetBars.size() > 0) {
            return;
        }
        Double longitude = Constant.longitude;
        Double latitude = Constant.latitude;
        Map<String, String> params = new HashMap<>();
        params.put("longitude", longitude + "");
        params.put("latitude", latitude + "");
        RequestUtil.getInstance().excutePostRequest(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NEARBY_SEARCH_LIST, params, this, HttpConstant.NEARBY_SEARCH_LIST);
    }

    private void initView() {
        searchEditText = (SearchEditText) parentView.findViewById(R.id.searchEditText);
        searchBottom = (LinearLayout) parentView.findViewById(R.id.searchBottom);
        searchAdapter = new SearchEditTextAdapter(mContext);
        searchEditText.setAdapter(searchAdapter);
        searchEditText.addTextChangedListener(this);
        clearHistory = parentView.findViewById(R.id.history_title);
        rlSearchHistory = (RelativeLayout) parentView.findViewById(R.id.rlSearchHistory);
        iv_Search = parentView.findViewById(R.id.iv_search);
        nearbyNatbarList = (LinearLayout) parentView.findViewById(R.id.nearbyData);
        historyNatbarList = (LinearLayout) parentView.findViewById(R.id.historyData);
        clearHistory.setOnClickListener(this);
        iv_Search.setOnClickListener(new NoDoubleClickListener());
        searchEditText.setOnEditorActionListener(this);
    }

    private void initData() {
        nearbyNatbarList.removeAllViews();
        for (InternetBarInfo info : internetBars) {
            if (info != null) {
                View content = (View) inflater.inflate(R.layout.search_item, null);
                TextView netbar = (TextView) content.findViewById(R.id.content_text);
                netbar.setText(info.getNetbar_name());
                netbar.setPadding(Utils.dip2px(mContext, 37), 0, 0, 0);
                content.setTag(info.getId());
                netbar.setTag(info);
                content.setOnClickListener(new View.OnClickListener() {
                    private Intent intent;

                    @Override
                    public void onClick(View v) {

                        String tag = v.getTag().toString();
                        InternetBarInfo bar = (InternetBarInfo) ((TextView) v.findViewById(R.id.content_text)).getTag();
                        intent = new Intent();
                        intent.putExtra("isRelease", true);
                        if (isRelease) {
                            listener.selectedNetbar(bar);
                            cancel();
                        } else {
                            intent.setClass(mContext, InternetBarActivityV2.class);
                            // 封装数据
                            Bundle bundle = new Bundle();
                            bundle.putString("netbarId", tag);
                            intent.putExtras(bundle);
                            mContext.startActivity(intent);
                            cancel();
                        }
                    }
                });
                nearbyNatbarList.addView(content);
            }
        }
        updateHistory();
    }


    public interface SelectedNetbarListener {
        void selectedNetbar(InternetBarInfo netbar);

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
        Gson gs = new Gson();
        JSONArray obj = null;
        try {
            obj = object.getJSONArray("object");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String objStr = obj.toString();
        if (method.equals(HttpConstant.NEARBY_SEARCH_LIST)) {
            if (!TextUtils.isEmpty(objStr) && !"success".equals(objStr)) {
                internetBars = gs.fromJson(objStr, new TypeToken<List<InternetBarInfo>>() {
                }.getType());
            }
            if (internetBars.size() > 0) {
                initData();
            }
        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        // TODO Auto-generated method stub
        hideLoading();
    }

    @Override
    public void onFaild(JSONObject object, String method) {

    }

    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int res) {
        String msg = mContext.getResources().getString(res);
        showToast(msg);
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
     * <p>
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
        if (internetBars.size() <= 0) {
            showLoading();
            startRequst();
        }
        updateHistory();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
        historyNatbarList.removeAllViews();
        items = PreferencesUtil.readHistory(0);
        if (items.isEmpty()){
            return;
        }
        if (items.get(0).equals("")) {
            rlSearchHistory.setVisibility(View.GONE);
        } else {
            rlSearchHistory.setVisibility(View.VISIBLE);
            for (String string : items) {
                if (!string.equals("")) {
                    View content = (View) inflater.inflate(R.layout.search_item, null);
                    TextView netbar = (TextView) content.findViewById(R.id.content_text);
                    netbar.setText(string);
                    netbar.setPadding(Utils.dip2px(mContext, 16), 0, 0, 0);
                    content.setTag(string);
                    content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchEditText.setText(v.getTag().toString());
                            searchBottom.setVisibility(View.VISIBLE);
                            searchEditText.dismissDropDown();
                        }
                    });
                    historyNatbarList.addView(content);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_title:
                PreferencesUtil.removeHistory(mContext,PreferencesUtil.HISTORY);
                updateHistory();
                ((SearchEditTextAdapter) searchEditText.getAdapter()).initData(0);
                break;
            case R.id.iv_search:

                break;
        }
    }

    public class NoDoubleClickListener implements View.OnClickListener {

        public static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime = 0;

        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                String text = searchEditText.getText().toString();
                if (text.trim().length() == 0 || text.contains("|")) {
                    showToast("输入有误，或者有特殊字符");
                    return;
                }
                toResult(text);
            }
        }
    }

    private void toResult(String text) {
        if (mContext instanceof NetbarListV2Activity) {
            ((NetbarListV2Activity) mContext).toSearchPage(text);
        } else {
            intent = new Intent(mContext, NetbarListV2Activity.class);
            intent.putExtra("searchType", 1);
            intent.putExtra("netbarName", text);
            if (isRelease) {
                intent.putExtra("isReleaseWar", isRelease);
                mContext.startActivityForResult(intent, ReleaseWarActivity.BAR_REQUEST);
            } else {
                intent.putExtra("searchType", 1);
                intent.putExtra("netbarName", text);
                intent.putExtra("isSearch", true);
                mContext.startActivity(intent);
            }
        }
        PreferencesUtil.saveHistory(mContext, text);
        ((SearchEditTextAdapter) searchEditText.getAdapter()).initData(0);
        cancel();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            // 修改回车键功能
            // 先隐藏键盘
            hideSolfInput();
            String text = v.getText().toString();
            if (text.trim().length() == 0 || text.contains("|")) {
                showToast("输入有误，或者有特殊字符");
                return true;
            }
            toResult(text);
        }
        return false;
    }

    private void hideSolfInput() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
