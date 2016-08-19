package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.RecreationMatchInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.ImageUtils;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class RecreationMatchAdapter extends BaseAdapter {

    private List<RecreationMatchInfo> mDatas;
    private Context mContext;
    private GridView mGridView;

    public RecreationMatchAdapter(GridView gridView, Context context, List<RecreationMatchInfo> data) {
        this.mContext = context;
        this.mDatas = data;
        this.mGridView = gridView;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    int count = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        count ++;
        LogUtil.e("log", "getview == " + count);
        RecreationMatchInfo matchInfo = mDatas.get(position);
        int height = ImageUtils.calculateImgHeightWithBitmap(mGridView, R.drawable.img_test);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.recommend_match_item, null);
            holder.imgPoster = (ImageView) convertView.findViewById(R.id.img_match_poster);


            holder.imgPoster.setLayoutParams(layoutParams);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_match_title);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_match_date);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tv_match_count);
            holder.tvType = (TextView) convertView.findViewById(R.id.tv_match_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.imgPoster.setLayoutParams(layoutParams);
        }


        AsyncImage.loadNetPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + matchInfo.getMainIcon(), holder.imgPoster);
        holder.tvTitle.setText(matchInfo.getTitle());
        holder.tvDate.setText(TimeFormatUtil.formatNoTime(matchInfo.getStartDate()));
        holder.tvCount.setText(matchInfo.getApplyNum() + "/ " + matchInfo.getMaxNum());
        String way = "";
        if (matchInfo.getWay() == 1) {
            way = mContext.getResources().getString(R.string.type_offline);
        } else {
            way = mContext.getResources().getString(R.string.type_online);
        }
        holder.tvType.setText(way);
        return convertView;
    }

    class ViewHolder {
        private ImageView imgPoster;   //海报
        private TextView tvType;  //类型
        private TextView tvTitle;// 标题
        private TextView tvCount;  //人数
        private TextView tvDate;  //日期
    }
}
