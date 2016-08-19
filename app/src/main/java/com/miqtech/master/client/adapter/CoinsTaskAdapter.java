package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.TaskInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CoinsTaskAdapter extends BaseAdapter {

    private Context context;
    private List<TaskInfo> taskInfoList;
    private Drawable drawableFinish;
    private Drawable drawableUnFinish;
    private float imgScale = (float) 18 / (float) 34;
    private float lineScale = (float) 18 / (float) 10;
    private int imgOriginalWith;
    private int imgOriginalHeigh;
    private int lineOriginalHeigh;
    private int imgChangeWith;
    private int imgChangeHeigh;
    private int lineChangeHeigh;
    private boolean isFirstCreate = true;

    private LinearLayout previousLlContent;
    private ImageView previousIvRight;

    private int previousId = 0;

    public CoinsTaskAdapter(Context context, List<TaskInfo> taskInfoList) {
        this.context = context;
        this.taskInfoList = taskInfoList;
        drawableFinish = context.getResources().getDrawable(R.drawable.finish_icon);
        drawableUnFinish = context.getResources().getDrawable(R.drawable.unfinish_icon);
        drawableFinish.setBounds(0, 0, drawableFinish.getMinimumWidth(), drawableFinish.getMinimumHeight());
        drawableUnFinish.setBounds(0, 0, drawableUnFinish.getMinimumWidth(), drawableUnFinish.getMinimumHeight());
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return taskInfoList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return taskInfoList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (arg1 == null) {
            holder = new ViewHolder();
            arg1 = LayoutInflater.from(context).inflate(R.layout.layout_coin_task_item, null);
            holder.name = (TextView) arg1.findViewById(R.id.tvGoldTitle);
            holder.coinsNum = (TextView) arg1.findViewById(R.id.tvGoldNum);
            holder.ivRight = (ImageView) arg1.findViewById(R.id.ivGoldRight);
            holder.ivGoldType = (ImageView) arg1.findViewById(R.id.ivGoldType);
            holder.llContent = (LinearLayout) arg1.findViewById(R.id.llGoldTypeRamark);
            holder.llGoldRight = (LinearLayout) arg1.findViewById(R.id.llGoldRight);
            holder.tvContent = (TextView) arg1.findViewById(R.id.tvGoldTypeRamark);
            holder.lineTop = arg1.findViewById(R.id.lineTop);
            holder.lineBotton = arg1.findViewById(R.id.lineBotton);
            holder.line = arg1.findViewById(R.id.view_line);
            arg1.setTag(holder);
        } else {
            holder = (ViewHolder) arg1.getTag();
        }

        TaskInfo bean = taskInfoList.get(arg0);

        holder.name.setTextColor(context.getResources().getColor(R.color.black_extend_intro));
        if (1 == bean.getLimit()) {
            holder.name.setText(bean.getName());
        } else {
            holder.name.setText(addconnent(bean.getName() + "(", bean.getAccomplish_time() + "", "/" + bean.getLimit() + ")", bean.getAll_accomplish()));
        }

        holder.line.setVisibility(View.GONE);
        holder.coinsNum.setText("+" + bean.getCoin());
        if (1 == bean.getAll_accomplish()) {
            holder.coinsNum.setTextColor(context.getResources().getColor(R.color.orgran_finish_coin_task));
            holder.coinsNum.setCompoundDrawables(null, null, drawableFinish, null);
            holder.name.setTextColor(context.getResources().getColor(R.color.font_gray));
            holder.ivGoldType.setImageDrawable(context.getResources().getDrawable(R.drawable.coins_task_finish));
        } else if (0 == bean.getAll_accomplish()) {
            holder.coinsNum.setTextColor(context.getResources().getColor(R.color.orange));
            holder.coinsNum.setCompoundDrawables(null, null, drawableUnFinish, null);

            if (holder.ivGoldType.getTag() == null || !holder.ivGoldType.getTag().equals(bean.getIcon())) {
                AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon() + "!small", holder.ivGoldType);
                holder.ivGoldType.setTag(bean.getIcon());
            }

        }

        holder.tvContent.setText(bean.getText());

        MyOnclick myOnclick = new MyOnclick(holder.llContent, holder.ivRight, arg0);

        holder.llGoldRight.setOnClickListener(myOnclick);

        isChangeShape(bean.getAll_accomplish(), holder);

        return arg1;
    }

    /**
     * 改变图片的大小
     *
     * @param allAccomplish
     * @param holder
     */
    private void isChangeShape(int allAccomplish, ViewHolder holder) {

        LinearLayout.LayoutParams paramsImg = (LinearLayout.LayoutParams) holder.ivGoldType.getLayoutParams();
        LinearLayout.LayoutParams paramsLineTop = (LinearLayout.LayoutParams) holder.lineTop.getLayoutParams();
        LinearLayout.LayoutParams paramsLineBotton = (LinearLayout.LayoutParams) holder.lineBotton.getLayoutParams();
        if (isFirstCreate) {
            imgOriginalWith = paramsImg.width;
            imgOriginalHeigh = paramsImg.height;
            lineOriginalHeigh = paramsLineTop.height;

            imgChangeWith = (int) (imgOriginalWith * imgScale);
            imgChangeHeigh = (int) (imgOriginalHeigh * imgScale);
            lineChangeHeigh = (int) (lineOriginalHeigh * lineScale);

            isFirstCreate = false;
        }

        if (allAccomplish == 1) {
            paramsImg.width = imgChangeWith;
            paramsImg.height = imgChangeHeigh;
            paramsLineTop.height = lineChangeHeigh;
            paramsLineBotton.height = lineChangeHeigh;
        } else {
            paramsImg.width = imgOriginalWith;
            paramsImg.height = imgOriginalHeigh;
            paramsLineTop.height = lineOriginalHeigh;
            paramsLineBotton.height = lineOriginalHeigh;
        }

        holder.ivGoldType.setLayoutParams(paramsImg);
        holder.lineTop.setLayoutParams(paramsLineTop);
        holder.lineBotton.setLayoutParams(paramsLineBotton);
    }

    class MyOnclick implements View.OnClickListener {
        LinearLayout llContent;
        ImageView ivRight;
        int id;

        public MyOnclick(LinearLayout llContent, ImageView ivRight, int id) {
            this.llContent = llContent;
            this.ivRight = ivRight;
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            if (taskInfoList.get(id).getIsShowText() == 1) {
                llContent.setVisibility(View.GONE);
                ivRight.setImageDrawable(context.getResources().getDrawable(R.drawable.coin_right));
                taskInfoList.get(id).setIsShowText(0);
            } else {
                taskInfoList.get(id).setIsShowText(1);
                llContent.setVisibility(View.VISIBLE);
                ivRight.setImageDrawable(context.getResources().getDrawable(R.drawable.coin_down));

                if (previousId != id && taskInfoList.get(id).getIsShowText() == 1 && previousIvRight != null && previousLlContent != null) {
                    previousLlContent.setVisibility(View.GONE);
                    previousIvRight.setImageDrawable(context.getResources().getDrawable(R.drawable.coin_right));

                    previousLlContent = null;
                    previousIvRight = null;
                    taskInfoList.get(previousId).setIsShowText(0);
                }
            }

            previousLlContent = llContent;
            previousIvRight = ivRight;
            previousId = id;
        }
    }

    /**
     * @param cotent1
     * @param content2
     * @param content3
     * @param allAccomplish 是否完成 1完成
     * @return
     */
    private SpannableStringBuilder addconnent(String cotent1, String content2, String content3, final int allAccomplish) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(cotent1);
        int start = cotent1.length();
        int middle = start + content2.length();
        ssb.append(content2);
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                if (allAccomplish == 1) {
                    ds.setColor(context.getResources().getColor(R.color.font_gray));// 设置文本颜色
                } else {
                    ds.setColor(context.getResources().getColor(R.color.orange));// 设置文本颜色
                }
                // 去掉下划线
                ds.setUnderlineText(false);
            }
        }, start, middle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb.append(content3);
    }

    class ViewHolder {
        TextView name, coinsNum;
        ImageView ivRight;
        ImageView ivGoldType;
        View line;
        View lineBotton;
        View lineTop;
        LinearLayout llContent;
        LinearLayout llGoldRight;
        TextView tvContent;
    }

}
