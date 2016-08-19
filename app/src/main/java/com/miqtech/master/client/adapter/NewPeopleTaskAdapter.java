package com.miqtech.master.client.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.TaskInfo;
import com.miqtech.master.client.ui.EditDataActivity;
import com.miqtech.master.client.ui.ExpiryListActivity;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.MyActivityCardActivity;
import com.miqtech.master.client.ui.ReleaseWarActivity;

public class NewPeopleTaskAdapter extends BaseAdapter {
    private Context context;
    private List<TaskInfo> list;

    public NewPeopleTaskAdapter(Context context, List<TaskInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
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
            arg1 = LayoutInflater.from(context).inflate(R.layout.layout_new_three_item, arg2, false);
            holder.title = (TextView) arg1.findViewById(R.id.tvGoldTitle);
            holder.coinsNums = (TextView) arg1.findViewById(R.id.tvGoldNum);
            holder.finishs = (TextView) arg1.findViewById(R.id.tvfinish);
            arg1.setTag(holder);
        } else {
            holder = (ViewHolder) arg1.getTag();
        }

        TaskInfo bean = list.get(arg0);

        holder.title.setText(bean.getName());
        holder.coinsNums.setText("+" + bean.getCoin());

        if (1 == bean.getAll_accomplish()) {
            holder.finishs.setEnabled(false);
            holder.finishs.setBackgroundDrawable(null);
            holder.finishs.setText("已完成");
            holder.finishs.setTextColor(context.getResources().getColor(R.color.lv_item_content_text));
        } else {
            holder.finishs.setEnabled(true);
            holder.finishs.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_white_bg_corne_gray));
            holder.finishs.setTextColor(context.getResources().getColor(R.color.black_extend_intro));
            showText(holder.finishs, bean);
        }
        return arg1;
    }

    private void showText(TextView textView, TaskInfo bean) {
        String operation;
        if (bean.getName().equals("完善个人资料")) {
            operation = "去完善";
            toNext(EditDataActivity.class, textView);
        } else if (bean.getName().equals("完善参赛卡")) {
            operation = "去完善";
            toNext(MyActivityCardActivity.class, textView);
        } else if (bean.getName().equals("首次用金币兑换商品")) {
            operation = "去兑换";
            toNext(ExpiryListActivity.class, textView);
        } else if (bean.getName().equals("首次发布约战")) {
            operation = "去约战";
            toNext(ReleaseWarActivity.class, textView);
        } else if (bean.getName().equals("首次支付网费")) {
            operation = "去首页";
            toNext(MainActivity.class, textView);
        } else {
            operation = "";
        }
        textView.setText(operation);
    }

    private void toNext(final Class c, TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, c);
                context.startActivity(intent);
            }
        });
    }

    class ViewHolder {
        TextView title;
        TextView coinsNums;
        TextView finishs;
    }

}
