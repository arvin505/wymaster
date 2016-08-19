package com.miqtech.master.client.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.EventAgainst;
import com.miqtech.master.client.entity.EventAgainstDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * X
 * Created by admin on 2016/7/5.
 */
public class RaceCardView extends ViewGroup {
    //数据
    private List<EventAgainst> datas;
    //    //背景图
//
    private Context context;
//
//    private Paint paint;
//
//
//    public RaceCardView(Context context) {
//        this(context, null);
//    }
//
//    public RaceCardView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public RaceCardView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.context = context;
//        paint = new Paint();
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width;
//        int height;
//        height = getViewHeight();
//        width = getViewWidth();
//        super.onMeasure(width, height);
//    }
//

    /**
     * 获取viewgroup宽度
     *
     * @return
     */
    private int getViewWidth() {
        if (datas != null) {
            int roundCount = datas.size();
            int marginCount = roundCount - 1;
            float lineWidth = this.getResources().getDimension(R.dimen.racecard_round_to_round_margin);
            float groupWidth = this.getResources().getDimension(R.dimen.racecard_group_width);
            float leftRightMargin = this.getResources().getDimension(R.dimen.racecard_left_right_margin);
            float viewWidth = roundCount * groupWidth + lineWidth * marginCount + leftRightMargin * 2;
            return (int) viewWidth;
        }
        return 0;
    }

    /**
     * 获取viewgroup高度
     *
     * @return 高度
     */
    private int getViewHeight() {
        if (datas != null && datas.size() > 0) {
            EventAgainst roundOne = datas.get(0);
            List<EventAgainstDetail> detailList = roundOne.getDetailList();
            if (detailList != null) {
                float groupHeight = this.getResources().getDimension(R.dimen.racecard_group_height);
                float marginHeight = this.getResources().getDimension(R.dimen.racecard_group_margin);
                float racecardMargin = this.getResources().getDimension(R.dimen.racecard_top_bottom_margin);
                //第一轮最大组数
                double maxRoundOneGroupCount = Math.pow(2, datas.size() - 1);
                //组的数量
                int groupCount = (int) maxRoundOneGroupCount;
                //边距的数量
                int marginCount = groupCount - 1;
                float viewHeight = groupHeight * groupCount + marginCount * marginHeight + racecardMargin * 2;
                return (int) viewHeight;
            }
        }
        return 0;
    }
//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View childView = getChildAt(i);
//            childView.layout(0, 0, (int) context.getResources().getDimension(R.dimen.racecard_group_width), (int) context.getResources().getDimension(R.dimen.racecard_group_height));
//        }
//    }
//
//    public void setView(List<EventAgainst> datas) {
//        this.datas = datas;
////        EventAgainst roundData = datas.get(0);
////        List<EventAgainstDetail> allGroup = roundData.getDetailList();
////        int count = allGroup.size();
////        for (int i = 0; i < count; i++) {
//        View groupView = View.inflate(context, R.layout.layout_racecard_group, null);
//        addView(groupView);
//        postInvalidate();
//        //       }
//
//    }

    public RaceCardView(Context context) {
        this(context, null);
    }

    public RaceCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RaceCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void shuaXin(Context context, List<EventAgainst> datas) {
        this.datas = datas;
        this.context = context;
        int roundCount = datas.size();
        //roundCount总轮数
        for (int j = 0; j < roundCount; j++) {
            List<EventAgainstDetail> roundDatas = datas.get(j).getDetailList();
            int count = roundDatas.size();
            for (int i = 0; i < count; i++) {
                addChildView(roundDatas.get(i), j);
            }
        }
        postInvalidate();
        this.setBackgroundResource(R.color.transparent);
    }

    private void addChildView(EventAgainstDetail detail, int round) {
        View childView = View.inflate(context, R.layout.layout_racecard_group, null);
        TextView tvTeamCodeA = (TextView) childView.findViewById(R.id.tvTeamCodeA);
        TextView tvTeamNameA = (TextView) childView.findViewById(R.id.tvTeamNameA);
        TextView tvScoreA = (TextView) childView.findViewById(R.id.tvScoreA);
        TextView tvTeamCodeB = (TextView) childView.findViewById(R.id.tvTeamCodeB);
        TextView tvTeamNameB = (TextView) childView.findViewById(R.id.tvTeamNameB);
        TextView tvScoreB = (TextView) childView.findViewById(R.id.tvScoreB);
        tvTeamCodeA.setText(detail.getA_seat_number() + "");
        tvTeamNameA.setText(detail.getA_nickname());
        tvScoreA.setText(detail.getA_score() + "");
        tvTeamCodeB.setText(detail.getB_seat_number() + "");
        tvTeamNameB.setText(detail.getB_nickname());
        tvScoreB.setText(detail.getB_score() + "");
        childView.setTag(round);
        this.addView(childView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        System.out.println("获取系统自动测量的该ViewGroup的大小: widthSize=" + widthSize + ",heightSize=" + heightSize);
        setMeasuredDimension(getViewWidth(), getViewHeight());
        // 修改了系统自动测量的子View的大小
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 系统自动测量子View:
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
        // 获取系统自动测量的该ViewGroup的大小
    }

    @Override
    protected void onLayout(boolean arg0, int l, int t, int r, int b) {
        System.out.println("该ViewGroup的布局:" + "l=" + l + ",t=" + t + ",r=" + r + ",b=" + b);
        int left = (int) this.getResources().getDimension(R.dimen.racecard_left_right_margin);
        int top = (int) this.getResources().getDimension(R.dimen.racecard_top_bottom_margin);
        int right = 0;
        int bottom = 0;

        //判断是否是2的N次方
        EventAgainst roundOneData = datas.get(0);
        //第一轮所有的组
        List<EventAgainstDetail> roundOneAllGroup = roundOneData.getDetailList();
        if (roundOneAllGroup != null && roundOneAllGroup.size() > 0) {
            boolean result = nCF(roundOneAllGroup.size());
            if (result) {
                normalLayout(left, top, right, bottom);
            } else {

            }
        }

    }

    /**
     * 正常情况下的布局
     */
    private void normalLayout(int left, int top, int right, int bottom) {
        int roundToRoundMargin = (int) this.getResources().getDimension(R.dimen.racecard_round_to_round_margin);
        int roundNum = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int newRoundNum = (int) childView.getTag();
            if (roundNum != newRoundNum) {
                left = left + roundToRoundMargin + childView.getMeasuredWidth();
                top = (int) this.getResources().getDimension(R.dimen.racecard_top_bottom_margin);
            }
            right = left + childView.getMeasuredWidth();
            bottom = top + childView.getMeasuredHeight();
            childView.layout(left, top, right, bottom);
            System.out.println("i=" + i + ",该子View的布局:" + "" +
                    "left=" + left + ",top=" + top + ",right=" + right + ",bottom=" + bottom);
            top += childView.getMeasuredHeight();
            System.out.println("childView  TAG:" + childView.getTag());
        }

    }

    /**
     * 不是N的2次方时候的布局
     */
    private void abnormalLayout() {
        int left = (int) this.getResources().getDimension(R.dimen.racecard_left_right_margin);
        int top = (int) this.getResources().getDimension(R.dimen.racecard_top_bottom_margin);
        int right = 0;
        int bottom = 0;
        int roundToRoundMargin = (int) this.getResources().getDimension(R.dimen.racecard_round_to_round_margin);
        HashMap<Integer, Boolean> byeMap = getByeList();
        //先排第二排
        //第一轮数量
        EventAgainst roundOneDatas = datas.get(0);
        int roundOneDatasCount = roundOneDatas.getDetailList().size();
        //第二轮数量
        EventAgainst roundTwoDatas = datas.get(1);
        int roundTwoDatasCount = roundTwoDatas.getDetailList().size();
        int oneTwoCount = roundOneDatasCount + roundTwoDatasCount;
        //先布局第二组
        for (int i = roundOneDatasCount - 1; i < oneTwoCount; i++) {
            View childView = getChildAt(i);
            left = left + roundToRoundMargin + childView.getMeasuredWidth();
            right = left + childView.getMeasuredWidth();
            bottom = top + childView.getMeasuredHeight();
            childView.layout(left, top, right, bottom);
            top += childView.getMeasuredHeight();
            if (i == oneTwoCount - 1) {
                left = (int) this.getResources().getDimension(R.dimen.racecard_left_right_margin);
                top = (int) this.getResources().getDimension(R.dimen.racecard_top_bottom_margin);
                right = 0;
                bottom = 0;
            }
        }
        for (int i = 0; i < roundOneDatasCount; i++) {
            Iterator iter = byeMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                int position = (int) entry.getKey();
                boolean val = (boolean) entry.getValue();
            }
            View childView = getChildAt(i);

        }
    }

    /**
     * 获取轮空的index list
     *
     * @return 轮空index
     */
    private HashMap<Integer, Boolean> getByeList() {
        ArrayList<EventAgainst> list = new ArrayList<EventAgainst>();
        if (datas != null && datas.size() > 0) {
            //获取第二轮是否轮空
            EventAgainst secondRound = datas.get(1);
            List<EventAgainstDetail> detailList = secondRound.getDetailList();
            if (detailList != null) {
                HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
                for (int i = 0; i < detailList.size(); i++) {
                    EventAgainstDetail group = detailList.get(i);
                    if (TextUtils.isEmpty(group.getA_nickname()) && TextUtils.isEmpty(group.getB_nickname())) {
                        //全部轮空
                        map.put(i, true);
                    } else if (TextUtils.isEmpty(group.getB_nickname())) {
                        //下面轮空
                        map.put(i, false);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断是否是2的N次方
     *
     * @param n
     * @return
     */
    private boolean nCF(int n) {
        boolean b = false;
        while (true) {
            int j = n % 2;
            n = n / 2;
            if (j == 1) {
                b = false;
                break;
            }
            if (n == 2) {
                b = true;
                break;
            }

        }
        return b;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
