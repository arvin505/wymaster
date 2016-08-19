package com.miqtech.master.client.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.EntryInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.Utils;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2015/11/20.
 */
public class EntrysView extends LinearLayout {
    LayoutParams params;
    ImageView imTypeTwo;

    public EntrysView(Context context) {
        super(context);
        init();
    }

    private OnItemClickListner mListner;

    public EntrysView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.weight = 1;
        setGravity(Gravity.CENTER);
    }

    List<EntryInfo> mData;

    public void setData(List<EntryInfo> data) {
        this.mData = data;
        //getView();
        getViewOnline();
        invalidate();
    }

    private void sortData() {
        Collections.sort(mData);
    }

    private void getView() {
        //sortData();
        removeAllViews();
        for (EntryInfo info : mData) {
            TextView textView = new TextView(getContext());
            int paddingPx = Utils.dp2px(20);
            textView.setGravity(Gravity.CENTER);
            //textView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            int resId = 0;
            if (info.getType() == 1) { //1附近网吧
                resId = R.drawable.icon_netbar;
            } else if (info.getType() == 2) { //2热点资讯
                resId = R.drawable.icon_rechotnew;
            } else if (info.getType() == 3) { //3金币商城
                resId = R.drawable.icon_coin;
            } else if (info.getType() == 4) {   //4我的红包
                resId = R.drawable.icon_red_envelope;
            }
            Drawable icon = getContext().getResources().getDrawable(resId);
            textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
            textView.setText(info.getTitle());
            textView.setTextColor(getResources().getColor(R.color.textColorBattle));
            textView.setLayoutParams(params);
            addView(textView);
        }

    }

    private void getViewOnline() {
        removeAllViews();
        for (EntryInfo info : mData) {
            View view = inflate(getContext(), R.layout.layout_entryview_item, null);
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            TextView textView = (TextView) view.findViewById(R.id.tv_entry);
            ImageView imageView = (ImageView) view.findViewById(R.id.im_entry);

            int resId = 0;
            if (info.getType() == 1) { //1附近网吧
                resId = R.drawable.icon_netbar;
            } else if (info.getType() == 2) { //2自发赛
                resId = R.drawable.icon_hotinformation;
            } else if (info.getType() == 3) { //3金币商城
                resId = R.drawable.icon_coin;
            } else if (info.getType() == 4) {   //4我的红包
                resId = R.drawable.icon_red_envelope;
            } else if (info.getType() == 5) {   //5自发赛
                ImageView im_hot = (ImageView) view.findViewById(R.id.im_hot);
                imTypeTwo = im_hot;
                resId = R.drawable.icon_rechotnew;
                if (PreferencesUtil.getIsFirstShowHot(getContext())) {
                    im_hot.setVisibility(View.VISIBLE);
                } else {
                    im_hot.setVisibility(View.GONE);
                }
            }
            textView.setText(info.getTitle());
            if (!TextUtils.isEmpty(info.getIcon())) {
                AsyncImage.loadImageWithResId(HttpConstant.SERVICE_UPLOAD_AREA + info.getIcon(), resId, imageView);
            } else {
                imageView.setImageResource(resId);
            }
            view.setLayoutParams(params);
            addView(view);
        }
    }

    public void setOnItemClickListner(final OnItemClickListner listner) {
        this.mListner = listner;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            final int position = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onItemClick(v, position, imTypeTwo);
                }
            });
        }
    }


    public interface OnItemClickListner {
        void onItemClick(View view, int position, ImageView imageView);
    }
}
