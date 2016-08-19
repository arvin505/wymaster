package com.miqtech.master.client.view;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.utils.LogUtil;

/**
 * Created by Administrator on 2015/12/14.
 */
public class HasErrorListView extends ListView {
    private String TAG_ERROR = "error_view";

    private String error_title;
    private int errorResId = R.drawable.blank_mine;
    private View errorView;
    private TextView tvErrorTitle;
    private ImageView imError;
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

    public HasErrorListView(Context context) {
        super(context);
    }

    public HasErrorListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HasErrorListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollListener != null) {
            mScrollListener.onScroll(l, t, oldl, oldt);
        }
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
                LogUtil.e("addErrorPage()", "addErrorPage()");
                addErrorPage();
            }
        } else {
            if (hasError) {
                LogUtil.e("removeErrorPage()", "removeErrorPage()");
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
            errorView = LayoutInflater.from(getContext()).inflate(R.layout.exception_page, null);
            tvErrorTitle = (TextView) errorView.findViewById(R.id.tv_err_title);
            imError = (ImageView) errorView.findViewById(R.id.noDataImage);
        }

        if (!TextUtils.isEmpty(error_title)) {
            tvErrorTitle.setText(error_title);
        }
        if (errorResId != -1) {
            imError.setImageResource(errorResId);
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
        LogUtil.e("void removeErrorPage", "void removeErrorPage");
        for (int i = 0; i < getHeaderViewsCount(); i++) {
            LogUtil.e("getHeaderViewsCount", getHeaderViewsCount() + "");
            View view = getChildAt(i);
            if (view != null && view.getTag() != null && TAG_ERROR.equals(view.getTag())) {
                removeHeaderView(view);
                hasError = false;
                LogUtil.e("removeHeaderView(view)", "removeHeaderView(view)");
            }
        }
    }

    public interface Scrolllistener {
        void onScroll(int l, int t, int oldl, int oldt);
    }

    private Scrolllistener mScrollListener;

    public Scrolllistener getScrollListener() {
        return mScrollListener;
    }

    public void setScrollListener(Scrolllistener mScrollListener) {
        this.mScrollListener = mScrollListener;
    }
}
