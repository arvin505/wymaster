package com.miqtech.master.client.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.EventAgainst;
import com.miqtech.master.client.entity.EventAgainstDetail;
import com.miqtech.master.client.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 赛事对阵图
 * Created by zhaosentao on 2016/7/13.
 */
public class EventAgainstLinearLayout extends LinearLayout {

    private Context context;
    private ArrayList<EventAgainst> eventAgainstList = new ArrayList<EventAgainst>();

    int num;
    double number;
    RelativeLayout.LayoutParams params;
    private boolean isFirst = true;//是否是第一次计算1dp代表的值，（只计算一次即可）
    private float average;//在各个手机上每1dp代表多少像素
    private int column;//计算对阵图一共有几列

    private float distance41Dp;
    private float distance21Dp;

    public EventAgainstLinearLayout(Context context) {
        this(context, null);
    }

    public EventAgainstLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventAgainstLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        addAgainstView();
    }

    public void setData(ArrayList<EventAgainst> eventAgainstList) {
        this.eventAgainstList = eventAgainstList;
        distance41Dp = context.getResources().getDimension(R.dimen.distance_41dp);
        distance21Dp = context.getResources().getDimension(R.dimen.distance_21dp);
        addAgainstView();
    }

    private void addAgainstView() {
        if (eventAgainstList.isEmpty() || eventAgainstList.get(0).getDetailList().isEmpty()) {
            return;
        }

        column = eventAgainstList.size();
        //判断是否是2的N次方，如果不是,进行如下计算，找出空位，把空的实体类填进去
        if (!Utils.nCF(eventAgainstList.get(0).getDetailList().size(),eventAgainstList.size())) {
            addNULLBean();
        }

        for (int i = 0; i < column; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_event_against_turn, null);
            LinearLayout llTurn = (LinearLayout) view.findViewById(R.id.llTurn);
            TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);

            //显示轮次名称
            if (!TextUtils.isEmpty(eventAgainstList.get(i).getName())) {
                tvTitle.setText(eventAgainstList.get(i).getName());
            } else {
                tvTitle.setText("");
            }

            num = eventAgainstList.get(i).getDetailList().size();
            List<EventAgainstDetail> eventAgainstDetailList = eventAgainstList.get(i).getDetailList();

            if (eventAgainstDetailList.isEmpty()) {
                return;
            }

            for (int j = 0; j < num; j++) {
                View childView = LayoutInflater.from(context).inflate(R.layout.layout_event_against_player, null);
                EventAgainstDetail bean = eventAgainstDetailList.get(j);
                if (bean == null) {//当bean为空时，表示这局不需要显示，隐藏就好F
                    LinearLayout playerLlItem = (LinearLayout) childView.findViewById(R.id.playerLlItem);
                    playerLlItem.setVisibility(View.INVISIBLE);
                } else {
                    ViewHolder holder = new ViewHolder(childView);

                    setUpSpacingAndIsShowLine(holder, i, j, bean.isShowLine());

                    showData(holder, bean);
                }

                llTurn.addView(childView);
            }
            this.addView(view);
        }
    }

    /**
     * 添加空的实体类
     */
    private void addNULLBean() {
        if (column > 1) {//只有在大于2列时才进行计算
            List<EventAgainstDetail> firstDetail = eventAgainstList.get(0).getDetailList();//第一列的数据集
            List<EventAgainstDetail> secondDetail = eventAgainstList.get(1).getDetailList();//第二列的数据集
            int firstSize = firstDetail.size();
            int secondSize = secondDetail.size();
            //建立一个新的第一列数据，用来代替原先第一列的数据
            List<EventAgainstDetail> newEventAgainstDetail = new ArrayList<EventAgainstDetail>();
            for (int i = 0; i < secondSize * 2; i++) {
                newEventAgainstDetail.add(null);
            }

            int location = 0;//用来标记第二个for循环从什么位置开始循环
            for (int i = 0; i < secondSize; i++) {
                EventAgainstDetail secondBean = secondDetail.get(i);
                boolean isRecycler = false;//用来判断下面循环是否进行了，如果没有进行，则标记相应的第二列实体类的showLine为false
                int isTwoNumber = 0;//用来记录secondBean中id与firstBean的next_id相同的有几个
                int isBreak = 0;//用来判断是否已经进行了2次循环，是就跳出循环
                for (int j = location; j < firstSize; j++) {
                    isBreak++;
                    EventAgainstDetail firstBean = firstDetail.get(j);
                    isRecycler = true;
                    if (secondBean.getId() == firstBean.getNext_id()) {
                        location++;
                        isTwoNumber++;
                    }
                    //isTwoNumber不为0时原先第一列相应的实体类取代新的第一列相应的空
                    if (isTwoNumber == 1) {
                        if (isBreak == 2) {
                            secondBean.setShowLine(true);
                            newEventAgainstDetail.set(i * 2 + 1, firstDetail.get(j - 1));
                        } else if (isBreak == 1) {
                            secondBean.setShowLine(true);
                            newEventAgainstDetail.set(i * 2 + 1, firstDetail.get(j));
                        }
                    } else if (isTwoNumber == 2) {
                        secondBean.setShowLine(true);
                        newEventAgainstDetail.set(i * 2, firstDetail.get(j - 1));
                        newEventAgainstDetail.set(i * 2 + 1, firstDetail.get(j));
                        break;
                    } else if (isTwoNumber == 0) {
                        secondBean.setShowLine(false);
                    }

                    if (isBreak == 2) {
                        break;
                    }
                }

                if (!isRecycler) {
                    secondBean.setShowLine(false);
                }
            }
            eventAgainstList.get(0).setDetailList(newEventAgainstDetail);
        }
    }

    /**
     * 1.设置间距
     * 2.判断隐藏哪条线
     *
     * @param holder
     * @param i          第一个for循环的循环位置
     * @param j          第二个for循环的循环位置
     * @param isShowLine 当I为1是判断是否显示左边的线条（只争对i为1时有效）
     */
    private void setUpSpacingAndIsShowLine(ViewHolder holder, int i, int j, boolean isShowLine) {
//        if (isFirst) {
//            params = (RelativeLayout.LayoutParams) holder.lineLeft.getLayoutParams();
//            average = (float) params.width / (float) 20;
//            isFirst = false;
//        }

        if (i == 0) {//第一列时需要隐藏左边的线条
            holder.lineLeft.setVisibility(View.INVISIBLE);
        } else if (i == 1) {
            if (!isShowLine) {//这个是要当总数不是2的N次方时才会走，隐藏前面设置好的需要隐藏的线条
                holder.lineLeft.setVisibility(View.INVISIBLE);
            }
        }

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.playerRl.getLayoutParams();

        if (j == 0) {//设置距离顶部的高度
            number = (Math.pow(2, i) - 1);//0,1,3,7,15,31...
//            double rlMargin = number * 21;
//            layoutParams.setMargins(0, (int) average * ((int) rlMargin), 0, 0);
//            holder.playerRl.setLayoutParams(layoutParams);
            layoutParams.setMargins(0, (int) ((float) number * distance21Dp), 0, 0);
            holder.playerRl.setLayoutParams(layoutParams);
        } else {//设置距离上一局的高度(同一列的上一局)
            double rlMargin;
//            rlMargin = number * 41;//41是一个item的高度
//            layoutParams.setMargins(0, (int) average * ((int) rlMargin), 0, 0);
//            holder.playerRl.setLayoutParams(layoutParams);
            layoutParams.setMargins(0, (int) ((float) number * distance41Dp), 0, 0);
            holder.playerRl.setLayoutParams(layoutParams);
        }

        //设置上下2个线条那个隐藏，为0时上面隐藏，为1时下面隐藏
        switch (j % 2) {
            case 0:
                holder.lineUp.setVisibility(View.INVISIBLE);
//                RelativeLayout.LayoutParams playerDownParams = (RelativeLayout.LayoutParams) holder.lineDown.getLayoutParams();
//                playerDownParams.height = (int) average * 21;
//                holder.lineDown.setLayoutParams(playerDownParams);
                RelativeLayout.LayoutParams playerDownParams = (RelativeLayout.LayoutParams) holder.lineDown.getLayoutParams();
                playerDownParams.height = (int) distance21Dp;
                holder.lineDown.setLayoutParams(playerDownParams);
                break;
            case 1:
                holder.lineDown.setVisibility(View.INVISIBLE);
                break;
        }

        //当为最后一个时，隐藏上下右的线条
        if (i == (column - 1)) {
            holder.lineRight.setVisibility(View.INVISIBLE);
            holder.lineUp.setVisibility(View.INVISIBLE);
            holder.lineDown.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示对局信息
     *
     * @param holder
     * @param bean
     */
    private void showData(ViewHolder holder, EventAgainstDetail bean) {
        //显示选手A的名称
        if (!TextUtils.isEmpty(bean.getA_nickname())) {
            holder.tvPlayerAName.setText(bean.getA_nickname());
            //显示A的序列号
//            holder.tvPlayerANum.setText(bean.getA_seat_number() + "");
        } else {
            holder.tvPlayerAName.setText("");
//            holder.tvPlayerANum.setText("");
        }

        //显示A的序列号
        if (!TextUtils.isEmpty(bean.getA_seat_number())) {
            holder.tvPlayerANum.setText(bean.getA_seat_number());
        } else {
            holder.tvPlayerANum.setText("");
        }

        //显示选手B的名称
        if (!TextUtils.isEmpty(bean.getB_nickname())) {
            holder.tvPlayerBName.setText(bean.getB_nickname());
            //显示B的序列号
//            holder.tvPlayerBNum.setText(bean.getB_seat_number() + "");
        } else {
            holder.tvPlayerBName.setText("");
//            holder.tvPlayerBNum.setText("");
        }

        //显示B的序列号
        if (!TextUtils.isEmpty(bean.getB_seat_number())) {
            holder.tvPlayerBNum.setText(bean.getB_seat_number());
        } else {
            holder.tvPlayerBNum.setText("");
        }

        //    state	0未开始1进行中2已完结
        if (bean.getState() == 0) {
            holder.tvPlayerAScore.setBackgroundColor(getResources().getColor(R.color.white));
            holder.tvPlayerBScore.setBackgroundColor(getResources().getColor(R.color.white));
        } else if (bean.getState() == 1) {
            holder.tvPlayerAScore.setBackgroundColor(getResources().getColor(R.color.light_gray1));
            holder.tvPlayerBScore.setBackgroundColor(getResources().getColor(R.color.light_gray1));
            holder.tvPlayerAScore.setTextColor(getResources().getColor(R.color.shop_buy_record_gray));
            holder.tvPlayerBScore.setTextColor(getResources().getColor(R.color.shop_buy_record_gray));

            holder.tvPlayerAScore.setText(bean.getA_score() + "");
            holder.tvPlayerBScore.setText(bean.getB_score() + "");
        } else if (bean.getState() == 2) {
            if (bean.getA_is_win() == 1) {//a是否获胜
                holder.tvPlayerAScore.setBackgroundColor(getResources().getColor(R.color.orange));
                holder.tvPlayerBScore.setBackgroundColor(getResources().getColor(R.color.little_gray));
                holder.tvPlayerAScore.setTextColor(getResources().getColor(R.color.white));
                holder.tvPlayerBScore.setTextColor(getResources().getColor(R.color.shop_buy_record_gray));
            } else {
                holder.tvPlayerAScore.setBackgroundColor(getResources().getColor(R.color.little_gray));
                holder.tvPlayerBScore.setBackgroundColor(getResources().getColor(R.color.orange));
                holder.tvPlayerAScore.setTextColor(getResources().getColor(R.color.shop_buy_record_gray));
                holder.tvPlayerBScore.setTextColor(getResources().getColor(R.color.white));
            }
            holder.tvPlayerAScore.setText(bean.getA_score() + "");
            holder.tvPlayerBScore.setText(bean.getB_score() + "");
        }
    }

    class ViewHolder {
        @Bind(R.id.playerRl)
        RelativeLayout playerRl;
        @Bind(R.id.playerLineLeft)
        View lineLeft;
        @Bind(R.id.playerLineRight)
        View lineRight;
        @Bind(R.id.playerLineUp)
        View lineUp;
        @Bind(R.id.playerLineDown)
        View lineDown;

        @Bind(R.id.playerANum)
        TextView tvPlayerANum;
        @Bind(R.id.playerBNum)
        TextView tvPlayerBNum;
        @Bind(R.id.playerAName)
        TextView tvPlayerAName;
        @Bind(R.id.playerBName)
        TextView tvPlayerBName;
        @Bind(R.id.playerAScore)
        TextView tvPlayerAScore;
        @Bind(R.id.playerBScore)
        TextView tvPlayerBScore;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
