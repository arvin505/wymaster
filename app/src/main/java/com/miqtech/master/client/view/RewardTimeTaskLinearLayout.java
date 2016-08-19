package com.miqtech.master.client.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.watcher.Observerable;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 悬赏令倒计时
 * Created by zhaosentao on 2016/8/2.
 */
public class RewardTimeTaskLinearLayout extends LinearLayout {

    private Timer timer;
    private int totalTime = 0;
    private MyHandler myHandler;
    private MyTimerTask task;

    private Context context;
    private ViewHolder holder;
    private View view;
    private boolean isFirstCreat = true;

    private String dayStr;
    private TextView textView;//底部橘色上面的状态文字
    private TextView tvBottomStatu;//底部橘色的状态文字
    private LinearLayout linearLayout;//底部橘色按钮
    private ImageView imageView;//底部橘色部分内的图片

    private Observerable observerable = Observerable.getInstance();


    public RewardTimeTaskLinearLayout(Context context) {
        this(context, null);
    }

    public RewardTimeTaskLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RewardTimeTaskLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * 初始化 倒计时控件
     *
     * @param totalTime     倒计时的时间
     * @param textView      橘色底部上方的状态的文字颜色
     * @param linearLayout  橘色底部整体按钮
     * @param imageView     橘色底部内图片
     * @param tvBottomStatu 橘色底部内的文字
     */
    public void initView(int totalTime, TextView textView, LinearLayout linearLayout, ImageView imageView, TextView tvBottomStatu) {
        if (isFirstCreat) {
            this.tvBottomStatu = tvBottomStatu;
            this.textView = textView;
            this.linearLayout = linearLayout;
            this.imageView = imageView;
            this.totalTime = totalTime;
            dayStr = context.getApplicationContext().getResources().getString(R.string.day);
            view = LayoutInflater.from(context).inflate(R.layout.layout_reward_time_task, null);
            holder = new ViewHolder(view);
            this.addView(view);
            myHandler = new MyHandler();
            secToTimeReward(totalTime);
            timeOut();
            isFirstCreat = false;
        }
    }

    private void timeOut() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer == null) {
            timer = new Timer();
        }
        if (task == null) {
            task = new MyTimerTask();
        }
        timer.schedule(task, 1000, 1000);
        observerable.notifyChange(Observerable.ObserverableType.REWARD_COMMENT, 7,timer,task);
    }

    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            totalTime--;
            myHandler.sendEmptyMessage(0);
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (totalTime > 0) {
                //倒计时时间
                //设置倒计时时间
                secToTimeReward(totalTime);
            } else {
                showDayOrTime(-1);
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }

                if (task != null) {
                    task.cancel();
                    task = null;
                }
                myHandler.removeMessages(0);
            }
        }
    }

    private void secToTimeReward(int time) {
        int day = 0; //天
        int hour = 0;
        int minute = 0;
        int second = 0;
        minute = time / 60;
        if (minute < 60) {
            second = time % 60;
            holder.tvTime.setText("00");
            holder.tvMinute.setText(TimeFormatUtil.unitFormat(minute));
            holder.tvSecond.setText(TimeFormatUtil.unitFormat(second));
            showDayOrTime(0);
        } else {
            hour = minute / 60;
            if (hour < 24) {
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                holder.tvTime.setText(TimeFormatUtil.unitFormat(hour));
                holder.tvMinute.setText(TimeFormatUtil.unitFormat(minute));
                holder.tvSecond.setText(TimeFormatUtil.unitFormat(second));
                showDayOrTime(0);
            } else {
                day = hour / 24;
                if (day != preDay) {//只要当天数产生变化时才会重新改变显示的数据
                    holder.tvDay.setText(day + dayStr);
                    showDayOrTime(1);
                }
                preDay = day;
            }
        }
    }

    int preDay;

    /**
     * 显示天数  还是时分秒
     *
     * @param i 1是天数，0是时分秒,-1时倒计时结束时，显示正在审核中
     */
    private void showDayOrTime(int i) {
        if (i == 1) {
            holder.tvDay.setVisibility(View.VISIBLE);

            holder.tvTime.setVisibility(View.GONE);
            holder.tvMinute.setVisibility(View.GONE);
            holder.tvSecond.setVisibility(View.GONE);
            holder.tvColonCenter.setVisibility(View.GONE);
            holder.tvColonRight.setVisibility(View.GONE);
        } else if (i == 0) {
            holder.tvDay.setVisibility(View.GONE);

            holder.tvTime.setVisibility(View.VISIBLE);
            holder.tvMinute.setVisibility(View.VISIBLE);
            holder.tvSecond.setVisibility(View.VISIBLE);
            holder.tvColonCenter.setVisibility(View.VISIBLE);
            holder.tvColonRight.setVisibility(View.VISIBLE);
        } else if (i == -1) {

            holder.tvDay.setVisibility(View.GONE);
            holder.tvTime.setVisibility(View.GONE);
            holder.tvMinute.setVisibility(View.GONE);
            holder.tvSecond.setVisibility(View.GONE);
            holder.tvColonCenter.setVisibility(View.GONE);
            holder.tvColonRight.setVisibility(View.GONE);

            textView.setTextColor(context.getApplicationContext().getResources().getColor(R.color.card_text_color));
            textView.setTextSize(13);
            textView.setText(context.getApplicationContext().getResources().getString(R.string.is_under_review));

            imageView.setImageResource(R.drawable.wanted_fight02);
            linearLayout.setBackgroundResource(R.drawable.shape_conners_4_solid_cdcdcd);
            tvBottomStatu.setTextColor(context.getApplicationContext().getResources().getColor(R.color.shop_splite_line));
            linearLayout.setEnabled(false);
        }
    }

    class ViewHolder {
        @Bind(R.id.tvDay)
        TextView tvDay;
        @Bind(R.id.tvTime)
        TextView tvTime;
        @Bind(R.id.tvColonCenter)
        TextView tvColonCenter;
        @Bind(R.id.tvMinute)
        TextView tvMinute;
        @Bind(R.id.tvColonRight)
        TextView tvColonRight;
        @Bind(R.id.tvSecond)
        TextView tvSecond;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
