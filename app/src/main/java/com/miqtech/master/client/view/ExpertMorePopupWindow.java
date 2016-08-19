package com.miqtech.master.client.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;

/**
 *
 * Created by zhaosentao on 2016/5/25.
 */
public class ExpertMorePopupWindow extends Dialog {
    private Context context;

    private View mMenuView;
    private View llSina;
    private View llWeChat;
    private View llFriend;
    private View llQQ;
    private View tvCancel;


    public ExpertMorePopupWindow(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ExpertMorePopupWindow(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        init();
    }

    protected ExpertMorePopupWindow(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        init();
    }

    public void init() {
        mMenuView = LayoutInflater.from(context).inflate(R.layout.expert_more_view, null);
//		mMenuView = inflater.inflate(R.layout.expert_more_view, null);
        llSina = mMenuView.findViewById(R.id.llSina);
        llWeChat = mMenuView.findViewById(R.id.llWeChat);
        llFriend = mMenuView.findViewById(R.id.llFriend);
        llQQ = mMenuView.findViewById(R.id.llQQ);
        tvCancel = mMenuView.findViewById(R.id.tvCancel);
        this.setContentView(mMenuView);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //设置宽度,设置在底部
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        lp.gravity = Gravity.BOTTOM;
        this.getWindow().setAttributes(lp);
        this.getWindow().setWindowAnimations(R.style.shareDialog);
        this.setCanceledOnTouchOutside(true);
    }

    public void setOnItemClick(View.OnClickListener itemOnClick) {
        llSina.setOnClickListener(itemOnClick);
        llWeChat.setOnClickListener(itemOnClick);
        llFriend.setOnClickListener(itemOnClick);
        llQQ.setOnClickListener(itemOnClick);
    }

    public void hideWeiBoAndQQ() {
        llSina.setVisibility(View.GONE);
        llQQ.setVisibility(View.INVISIBLE);
    }

}
