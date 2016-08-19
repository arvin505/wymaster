package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.InforCatalog;
import com.miqtech.master.client.entity.LiveGameInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by admin on 2016/7/28.
 */
public class GameSelectAdapter extends BaseAdapter{
    private Context mContext;
    private List<LiveGameInfo> mDatas;
    private LayoutInflater inflater;

    public GameSelectAdapter(Context context, List<LiveGameInfo> datas) {
        this.mContext = context;
        this.mDatas = datas;
        inflater = LayoutInflater.from(mContext);
        //添加空数据 把灰色背景修改为白色
        if(mDatas.size()%3!=0){
           int addEmptyNum =(mDatas.size()/3+1)*3-mDatas.size();
            for(int i=0;i<addEmptyNum;i++){
                mDatas.add(new LiveGameInfo());
            }
        }
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_game_select_item, null);
            holder.tvGameName = (TextView) convertView.findViewById(R.id.tvGameName);
            holder.ivGamePic = (ImageView) convertView.findViewById(R.id.ivGamePic);
            holder.tvLivePlayNum=(TextView)convertView.findViewById(R.id.tvLivePlayNum);
            holder.tvVideoNum=(TextView)convertView.findViewById(R.id.tvVideoNum);
            holder.llParent=(LinearLayout)convertView.findViewById(R.id.llParent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LiveGameInfo data = mDatas.get(position);
        if (data != null && data.getId()!=0) {
            holder.tvGameName.setVisibility(View.VISIBLE);
            holder.ivGamePic.setVisibility(View.VISIBLE);
            holder.tvLivePlayNum.setVisibility(View.VISIBLE);
            holder.tvVideoNum.setVisibility(View.VISIBLE);
            holder.tvGameName.setText(data.getName());
            AsyncImage.loadPhoto(mContext,HttpConstant.SERVICE_UPLOAD_AREA + data.getIcon(),holder.ivGamePic);
            holder.tvLivePlayNum.setText(data.getLiveNum()+"");
            holder.tvVideoNum.setText(data.getVideoNum()+"");
        }else{
            holder.tvGameName.setVisibility(View.INVISIBLE);
            holder.ivGamePic.setVisibility(View.INVISIBLE);
            holder.tvLivePlayNum.setVisibility(View.INVISIBLE);
            holder.tvVideoNum.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvGameName; //游戏名字
        ImageView ivGamePic; //游戏图片
        TextView tvLivePlayNum; //直播次数
        TextView tvVideoNum;   //视屏包房次数
        LinearLayout llParent ; //父控件
    }
}
