package com.miqtech.master.client.adapter;

import java.util.List;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Address;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.MatchAddressActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.ToastUtil;

public class MatchListAdapter extends BaseAdapter implements OnClickListener {
    private List<Address> address;
    private MatchAddressActivity context;
    private String matchTitle;

    public MatchListAdapter(MatchAddressActivity context, List<Address> address, String matchTitle) {
        this.context = context;
        this.address = address;
        this.matchTitle = matchTitle;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return address.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub

        return address.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.layout_matchaddress_item, parent, false);
            holder = new ViewHolder();
            holder.tvMatchName = (TextView) v.findViewById(R.id.tvMatchName);
            holder.tvTime = (TextView) v.findViewById(R.id.tvTime);
            holder.tvAddress = (TextView) v.findViewById(R.id.tvAddress);
            holder.llContent = (LinearLayout) v.findViewById(R.id.llContent);
            holder.btnApply = (Button) v.findViewById(R.id.btnApply);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.tvMatchName.setText(matchTitle + "第" + address.get(position).getRound() + "局");
        holder.tvAddress.setText(address.get(position).getAreas());
        holder.tvTime.setText(DateUtil.dateToStrLong(address.get(position).getOver_time()));
        holder.btnApply.setOnClickListener(new OnClickListener() {
            private Intent intent;

            @Override
            public void onClick(View v) {
                User user = WangYuApplication.getUser(context);
                if (user != null) {
                    context.joinMatch(position, address.get(position).getHasApply());
                } else {
                    ToastUtil.showToast("请登录", context);
                    intent = new Intent();
                    intent.setClass(context, LoginActivity.class);
                    context.startActivity(intent);
                }
            }
        });
        initImgView(address.get(position), holder);
        return v;
    }

    private class ViewHolder {
        TextView tvMatchName;
        TextView tvTime;
        TextView tvAddress;
        LinearLayout llContent;
        Button btnApply;
    }

    private void initImgView(Address add, ViewHolder holder) {
        List<InternetBarInfo> netBars = add.getNetbars();
        if (holder.llContent.getChildCount() == 0) {
            if (netBars != null && netBars.size() > 0) {
                for (int i = 0; i < netBars.size(); i++) {
                    View v = LayoutInflater.from(context).inflate(R.layout.layout_matchaddress_img_item,
                            holder.llContent, false);
                    ImageView ivNetBarImg = (ImageView) v.findViewById(R.id.ivNetBarImg);
                    TextView tvNetBarName = (TextView) v.findViewById(R.id.tvNetBarName);
                    AsyncImage.loadPhoto(context,
                            HttpConstant.SERVICE_UPLOAD_AREA + add.getNetbars().get(i).getIcon(), ivNetBarImg);
                    tvNetBarName.setText(add.getNetbars().get(i).getNetbar_name());
                    holder.llContent.addView(v);
                    v.setTag(netBars.get(i).getId());
                    v.setOnClickListener(this);
                }
            }
        }
        switch (add.getInApplyNew()) {
            case 0:
                holder.btnApply.setClickable(false);
                holder.btnApply.setText("未开始");
                holder.btnApply.setBackgroundResource(R.color.gray_bg);
                break;
            case 1:
                if (add.getHasApply() == 2) {
                    holder.btnApply.setClickable(false);
                    holder.btnApply.setText("您已加入战队");
                    holder.btnApply.setBackgroundResource(R.color.gray_bg);
                } else {
                    holder.btnApply.setClickable(true);
                    holder.btnApply.setText("报名");
                    holder.btnApply.setBackgroundResource(R.color.blue_gray);
                }
                break;
            case 2:
                holder.btnApply.setClickable(false);
                holder.btnApply.setText("报名已结束");
                holder.btnApply.setBackgroundResource(R.color.gray_bg);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        String id = (String) v.getTag();
        Intent intent = new Intent();
        intent.setClass(context, InternetBarActivityV2.class);
        intent.putExtra("netbarId", id);
        context.startActivity(intent);
    }

}
