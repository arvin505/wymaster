package com.miqtech.master.client.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.utils.LogUtil;

/**
 * Created by Administrator on 2016/3/28.
 */
public class MyHasErrorListView extends ListView {

    private String TAG_ERROR = "error_view";

    private String error_title;
    private int errorResId = R.drawable.blank_mine;
    private View errorView;
    private TextView tvErrorTitle;
    private boolean showError = false;

    public void setErrorView(String title, int resId) {
        this.error_title = title;
        this.errorResId = resId;
    }

    public void setErrorView(String title) {
        this.error_title = title;
    }

    public void setErrorView(int title, int resId) {
        this.error_title = getResources().getString(title);
        this.errorResId = resId;
    }

    public void setErrorView(int title) {
        this.error_title = getResources().getString(title);
    }

    public MyHasErrorListView(Context context) {
        super(context);
    }

    public MyHasErrorListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHasErrorListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // 该自定义控件只是重写了GridView的onMeasure方法，使其不会出现滚动条，ScrollView嵌套ListView也是同样的道理，不再赘述。
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }


    private boolean hasError = false;

    public void setErrorShow(boolean showError) {
        this.showError = showError;
        if (showError) {
            if (!hasError) {
                addErrorPage();
            }
        } else {
            if (hasError) {
                removeErrorPage();
            }
        }
        ListAdapter adapter = getAdapter();
        if (adapter instanceof BaseAdapter) {
            BaseAdapter baseAdapter = (BaseAdapter) adapter;
            baseAdapter.notifyDataSetChanged();
        }

    }

    private void addErrorPage() {
        hasError = true;
        if (errorView == null) {
            errorView = LayoutInflater.from(getContext()).inflate(R.layout.exception_page_only_text, null);
            tvErrorTitle = (TextView) errorView.findViewById(R.id.tv_err_title);
        }

        if (!TextUtils.isEmpty(error_title)) {
            tvErrorTitle.setText(error_title);
        }
        if (errorResId != -1) {
        }
        errorView.setTag(TAG_ERROR);
        LogUtil.e("setTag(TAG_ERROR)", errorView.getTag() + "");
        try {
            addHeaderView(errorView);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void removeErrorPage() {
//        for (int i = 0; i < getHeaderViewsCount(); i++) {
//            LogUtil.e("getHeaderViewsCoun--------------------------------------", getHeaderViewsCount() + "");
//            View view = getChildAt(i);
//            if (view != null && view.getTag() != null && TAG_ERROR.equals(view.getTag())) {
//                removeHeaderView(view);
//                hasError = false;
//                LogUtil.e("removeHeaderView(view)", "removeHeaderView(view)");
//            }
//        }

        if (errorView != null && errorView.getTag() != null && TAG_ERROR.equals(errorView.getTag())) {
            removeHeaderView(errorView);
            errorView = null;
            hasError = false;
        }
    }
}
