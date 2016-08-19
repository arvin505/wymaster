package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Administrator on 2015/11/23.
 */
public class RecommendNetbarAdapter extends BaseAdapter {
    private List<InternetBarInfo> mData;
    private Context mContext;

    public RecommendNetbarAdapter(Context context, List data) {
        this.mData = data;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<InternetBarInfo> data) {
        this.mData = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.nearbybaritem, null);
            holder = new ViewHolder();
            holder.tvBarName = (TextView) convertView.findViewById(R.id.tv_netbar_name);
            holder.tvBarLoc = (TextView) convertView.findViewById(R.id.tv_netbar_loction);
            holder.tvBarDistance = (TextView) convertView.findViewById(R.id.tv_netbar_distance);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_netbar_hourprice);
            holder.imgIcon = (ImageView) convertView.findViewById(R.id.img_netbar_icon);
            holder.imgHot = (ImageView) convertView.findViewById(R.id.img_bar_hot);
            holder.imgJ = (ImageView) convertView.findViewById(R.id.img_bar_j);
            holder.imgZ = (ImageView) convertView.findViewById(R.id.img_bar_z);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        InternetBarInfo internetBarInfo = mData.get(position);

        holder.tvBarName.setText(internetBarInfo.getNetbar_name());
        holder.tvBarLoc.setText(internetBarInfo.getAddress());
        holder.tvPrice.setText(mContext.getResources().getString(R.string.price_per_hour, internetBarInfo.getPrice_per_hour()));

        if (internetBarInfo.getIs_hot() == 1) {
            holder.imgHot.setVisibility(View.VISIBLE);
        } else {
            holder.imgHot.setVisibility(View.INVISIBLE);
        }
        if (internetBarInfo.getIs_recommend() == 1) {
            holder.imgJ.setVisibility(View.VISIBLE);
        } else {
            holder.imgJ.setVisibility(View.INVISIBLE);
        }
        if (internetBarInfo.getIs_order() == 1) {
            holder.imgZ.setVisibility(View.VISIBLE);
        } else {
            holder.imgZ.setVisibility(View.INVISIBLE);
        }

        double distanceloc = getDistance(internetBarInfo.getLatitude(), internetBarInfo
                .getLongitude());
        holder.tvBarDistance.setText(disConversion(distanceloc / 1000));

        AsyncImage.loadNetPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + internetBarInfo.getIcon(), holder.imgIcon);

        return convertView;
    }

    class ViewHolder {
        TextView tvBarName;
        TextView tvBarLoc;
        TextView tvBarDistance;
        TextView tvPrice;
        ImageView imgIcon;
        ImageView imgJ;
        ImageView imgZ;
        ImageView imgHot;
    }

    // 返回单位是米
    public double getDistance(double latitude1, double longitude1) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(Constant.latitude);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(Constant.longitude);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        LogUtil.e("用来计算距离的当前的经度，纬度----------------", Constant.longitude + "=================" + Constant.latitude);
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    private final double EARTH_RADIUS = 6378137.0;

    public String disConversion(double dis) {
        DecimalFormat df = new DecimalFormat("#.#");
        if ((dis * 1000) <= 999)
            return df.format((dis * 1000)) + "m";
        if (dis > 999)
            return 999 + "+km";
        return df.format(dis) + "km";
    }
}
