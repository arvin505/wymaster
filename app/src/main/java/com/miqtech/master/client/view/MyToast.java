package com.miqtech.master.client.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.jpush.service.PushType;
import com.miqtech.master.client.ui.CoinsTaskActivity;
import com.miqtech.master.client.ui.EventDetailActivity;
import com.miqtech.master.client.ui.ExchangeDetailActivity;
import com.miqtech.master.client.ui.GoldCoinsStoreActivity;
import com.miqtech.master.client.ui.InformationAtlasActivity;
import com.miqtech.master.client.ui.InformationDetailActivity;
import com.miqtech.master.client.ui.InformationTopicActivity;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.MyMessageActivity;
import com.miqtech.master.client.ui.MyRedBagActivity;
import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.ui.RewardActivity;
import com.miqtech.master.client.ui.ShopDetailActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.YueZhanDetailsActivity;

public class MyToast {

    private WindowManager mWdm;
    private View mToastView;
    private WindowManager.LayoutParams mParams;
//    private Timer mTimer;
    private boolean mShowTime;//记录Toast的显示长短类型
    private boolean mIsShow;//记录当前Toast的内容是否已经在显示

    public static final boolean LENGTH_LONG = true;
    public static final boolean LENGTH_SHORT = false;
    private Context context;

    private int pushType;
    private Intent intent;


    private MyToast(Context context, String text, int pushType, Intent intent, boolean showTime) {
        this.context = context;
        this.pushType = pushType;
        this.intent = intent;
        this.mShowTime = showTime;//记录Toast的显示长短类型
        mIsShow = false;//记录当前Toast的内容是否已经在显示
        mWdm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mToastView = LayoutInflater.from(context).inflate(R.layout.layout_notification_dialog, null);
        TextView textView = (TextView) mToastView.findViewById(R.id.tvNotification);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
        params.width = WangYuApplication.WIDTH;
        textView.setLayoutParams(params);
        textView.setText(text);
        //通过Toast实例获取当前android系统的默认Toast的View布局
        Toast.makeText(context, text, Toast.LENGTH_SHORT).setView(mToastView);

//        mTimer = new Timer();
        //设置布局参数
        setParams();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipAndShow();
            }
        });
    }


    private void setParams() {
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.windowAnimations = R.style.toastAnimine;//设置进入退出动画效果
        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.gravity = Gravity.BOTTOM;
        if (AppManager.getAppManager().currentActivity().getClass() == MainActivity.class) {
            mParams.y = (int) context.getResources().getDimension(R.dimen.distance_49dp);
        }
    }

    public static MyToast MakeText(Context context, String text, int pushType, Intent intent, boolean showTime) {
        MyToast result = new MyToast(context, text, pushType, intent, showTime);
        return result;
    }

    public void show() {
        try {
            if (!mIsShow) {//如果Toast没有显示，则开始加载显示
                mIsShow = true;
                mWdm.addView(mToastView, mParams);//将其加载到windowManager上
//                mTimer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        mWdm.removeView(mToastView);
//                        mIsShow = false;
//                    }
//                }, (long) (mShowTime ? 5000 : 2000));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWdm.removeView(mToastView);
                        mIsShow = false;
                    }
                },5000);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException i) {
            i.printStackTrace();
        }
    }

    public void cancel() {
//        if (mTimer == null) {
//            mWdm.removeView(mToastView);
//            mTimer.cancel();
//        }
        mIsShow = false;
    }

    private void skipAndShow() {
        Intent newIntent = new Intent();
        switch (pushType) {
            case PushType.SYS_ID:
                newIntent.setClass(context, MyMessageActivity.class);
                newIntent.putExtra("typeFragment", 3);
                context.startActivity(newIntent);
                break;
            case PushType.COMMENT_ID:
                newIntent.setClass(context, MyMessageActivity.class);
                newIntent.putExtra("typeFragment", 2);
                context.startActivity(newIntent);
                break;
            case PushType.REDBAG_ID:
                newIntent.setClass(context, MyRedBagActivity.class);
                context.startActivity(newIntent);
                break;
            case PushType.RESERVATION_ID:
//                newIntent.setClass(this, ReserveOrderActivity.class);
//                newIntent.putExtra("orderType", 0);
//                newIntent.putExtra("reserveId", intent.getStringExtra("reserveId"));
//                this.startActivity(newIntent);
                break;
            case PushType.YUEZHAN_ID:
                newIntent.setClass(context, YueZhanDetailsActivity.class);
                newIntent.putExtra("id", intent.getStringExtra("id"));
                context.startActivity(newIntent);
                break;
            case PushType.NETBAR_ID:
                newIntent.setClass(context, InternetBarActivityV2.class);
                newIntent.putExtra("netbarId", intent.getStringExtra("netbarId"));
                context.startActivity(newIntent);
                break;
            case PushType.PAY_ID:
//                newIntent.setClass(this, ReserveOrderActivity.class);
//                newIntent.putExtra("orderType", 1);
//                newIntent.putExtra("reserveId", intent.getStringExtra("reserveId"));
//                this.startActivity(newIntent);
                break;
            case PushType.MATCH_ID:
                newIntent.setClass(context, OfficalEventActivity.class);
                newIntent.putExtra("matchId", intent.getStringExtra("matchId"));
                context.startActivity(newIntent);
                break;
            case PushType.WEEK_REDBAG_ID:
                newIntent.setClass(context, SubjectActivity.class);
                newIntent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.REDBAG);
                context.startActivity(newIntent);
                break;
            case PushType.AMUSE_ID:
                newIntent.setClass(context, RecreationMatchDetailsActivity.class);
                newIntent.putExtra("id", intent.getStringExtra("id"));
                context.startActivity(newIntent);
                break;
            case PushType.INFORMATION_ID:
                int id = Integer.parseInt(intent.getStringExtra("infoId"));
                int type = intent.getIntExtra("infoType", 0);
                toInfoDetail(type, id);
                break;
            case PushType.COMMODITY_ID:
                newIntent.setClass(context, ExchangeDetailActivity.class);
                newIntent.putExtra("exchangeID", intent.getStringExtra("exchangeID"));
                context.startActivity(newIntent);
                break;
            case PushType.ROBTREASURE_ID:
                newIntent.setClass(context, ShopDetailActivity.class);
                newIntent.putExtra("id", intent.getStringExtra("id"));
                context.startActivity(newIntent);
                break;
            case PushType.MALL_ID:
                newIntent.setClass(context, GoldCoinsStoreActivity.class);
                context.startActivity(newIntent);
                break;
            case PushType.COIN_TASK_ID:
                newIntent.setClass(context, CoinsTaskActivity.class);
                context.startActivity(newIntent);
                break;
            case PushType.AWARD_COMMDITY_ID:
                newIntent.setClass(context, SubjectActivity.class);
                newIntent.putExtra("id", intent.getStringExtra("id"));
                newIntent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.GOODS_DETAIL);
                context.startActivity(newIntent);
                break;
            case PushType.OET_ID://自发赛
                newIntent.setClass(context, EventDetailActivity.class);
                newIntent.putExtra("matchId", intent.getStringExtra("id"));
                context.startActivity(newIntent);
                break;
            case PushType.BOUNTY_ID://悬赏令
                newIntent.setClass(context, RewardActivity.class);
                newIntent.putExtra("rewardId", Integer.valueOf(intent.getStringExtra("id")));
                newIntent.putExtra("isEnd", "1");
                context.startActivity(newIntent);
                break;
        }
    }

    private void toInfoDetail(int type, int id) {
        Intent intent;
        if (type == 1) {//类型:1图文  跳转
            intent = new Intent(context, InformationDetailActivity.class);
            intent.putExtra("id", id + "");
            intent.putExtra("type", type);
            /*intent.putExtra("categoryId", mInfoCatalog.getParent().getId() + "");
            intent.putExtra("pid", mInfoCatalog.getParent().getPid() + "");*/
            context.startActivity(intent);
        } else if (type == 2) {//2专题  跳转
            intent = new Intent();
            intent.putExtra("activityId", id);
            /*intent.putExtra("zhuanTitle", detail.getTitle());
            intent.putExtra("mInfoCatalog", mInfoCatalog);*/
            //intent.putExtra("url", detail.getIcon());
            intent.setClass(context, InformationTopicActivity.class);
            context.startActivity(intent);
        } else if (type == 3) {//3图集  跳转
            intent = new Intent();
            intent.putExtra("activityId", id);
            intent.setClass(context, InformationAtlasActivity.class);
            context.startActivity(intent);
        } else if (type == 4) {   //视频
            intent = new Intent(context, InformationDetailActivity.class);
            intent.putExtra("id", id + "");
            intent.putExtra("type", InformationDetailActivity.INFORMATION_VIDEO);
            context.startActivity(intent);
        }
    }
}
