package com.miqtech.master.client.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.NetBarAmuse;
import com.miqtech.master.client.entity.NetbarActivityInfo;
import com.miqtech.master.client.entity.NetbarService;
import com.miqtech.master.client.entity.YueZhan;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.TimeFormatUtil;

import java.util.List;

//*
// * 网吧活动适配器
// * Created by admin on 2016/3/10.
//*


public class NetbarActivityAdapter extends RecyclerView.Adapter {

    //点击事件回调
    private OnItemClickListener mOnItemClickListener;

    private NetbarActivityInfo info;

    private Context context;

    private LayoutInflater inflater;

    private final static int NETBAR_SERVICE = 0;
    private final static int NETBAR_AMUSE = 1;
    private final static int NETBAR_MATCH_TITLE = 2;
    private final static int NETBAR_MATCH = 3;
    private final static int NETBAR_RELEASE_WAR = 4;


    public NetbarActivityAdapter(Context context, NetbarActivityInfo info, OnItemClickListener mOnItemClickListener) {
        this.context = context;
        this.info = info;
        this.mOnItemClickListener = mOnItemClickListener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view;
        switch (viewType) {
            case NETBAR_SERVICE:
                view = inflater.inflate(R.layout.layout_netbar_services, parent, false);
                holder = new NetbarServiceViewHolder(view);
                break;
            case NETBAR_AMUSE:
                view = inflater.inflate(R.layout.layout_netbar_amuse_item, parent, false);
                holder = new NetbarAmuseViewHolder(view);
                break;
            case NETBAR_MATCH_TITLE:
                view = inflater.inflate(R.layout.layout_netbar_yuezhan_item2, parent, false);
                holder = new NetbarMatchTitleHolder(view);
                break;
            case NETBAR_MATCH:
                view = inflater.inflate(R.layout.recommend_battle_item, parent, false);
                holder = new NetbarMatchesHolder(view);
                break;
            case NETBAR_RELEASE_WAR:
                view = inflater.inflate(R.layout.layout_releasewar_item, parent, false);
                holder = new NetbarReleaseWarHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NetbarServiceViewHolder) {
            NetbarServiceViewHolder netbarServiceViewHolder = (NetbarServiceViewHolder) holder;
            List<NetbarService> services = info.getServices();
            NetbarService service = services.get(position);
            if (service != null) {
                setServiceView(netbarServiceViewHolder, service);
            }
        } else if (holder instanceof NetbarAmuseViewHolder) {
            NetbarAmuseViewHolder netbarAmuseViewHolder = (NetbarAmuseViewHolder) holder;
            NetBarAmuse amuse = info.getAmuse();
            setAmuseView(netbarAmuseViewHolder, amuse);
        } else if (holder instanceof NetbarMatchTitleHolder) {

        } else if (holder instanceof NetbarMatchesHolder) {
            NetbarMatchesHolder netbarMatchesHolder = (NetbarMatchesHolder) holder;
            int currentPosition;
            int servicesCount = 0;
            int amuseCount = 0;
            int matchesCount = 0;
            int matchTitleCount = 1;
            if (info.getServices() != null) {
                servicesCount = info.getServices().size();
            }
            if (info.getAmuse() != null) {
                amuseCount = 1;
            }
            if (info.getMatches() != null) {
                matchesCount = info.getMatches().size();
            }
            currentPosition = position - (servicesCount + amuseCount + matchTitleCount);
            if (currentPosition >= 0) {
                YueZhan match = info.getMatches().get(currentPosition);
                setMatchView(netbarMatchesHolder, match);
            }
        } else if (holder instanceof NetbarReleaseWarHolder) {
            NetbarReleaseWarHolder netbarReleaseWarHolder = (NetbarReleaseWarHolder) holder;
            netbarReleaseWarHolder.btnReleaseWar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(null);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        int servicesCount = 0;
        int amuseCount = 0;
        int matchTitleCount = 1;
        int matchCount = 0;
        position += 1;

        if (info != null) {
            if (info.getServices() != null) {
                servicesCount = info.getServices().size();
            }
            if (info.getAmuse() != null) {
                amuseCount = 1;
            }
            if (info.getMatches() != null) {
                matchCount = info.getMatches().size();
            }
            if (position > servicesCount) {
                if (position > servicesCount + amuseCount) {
                    if (position > servicesCount + amuseCount + matchTitleCount) {
                        return NETBAR_MATCH;
                    }
                    if(matchCount == 0){
                        return NETBAR_RELEASE_WAR;
                    }else{
                        return NETBAR_MATCH_TITLE;
                    }
                }
                return NETBAR_AMUSE;
            }
            return NETBAR_SERVICE;
        }
        return super.getItemViewType(position);
    }

    //设置SERVICEVIEW
    private void setServiceView(NetbarServiceViewHolder holder, final NetbarService service) {
        String imgUrl = service.getUrl();
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + imgUrl, holder.imgServer);
        if (TextUtils.isEmpty(service.getName())) {
            holder.tvTitle.setText("暂无标题");
        } else {
            holder.tvTitle.setText(service.getName());
        }
        if (TextUtils.isEmpty(service.getInterest_num())) {
            holder.tvCount.setText("0人感兴趣");
        } else {
            holder.tvCount.setText(service.getInterest_num() + "人感兴趣");
        }
        holder.llServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(service);
            }
        });
    }

    //设置娱乐赛VIEW
    private void setAmuseView(NetbarAmuseViewHolder holder, final NetBarAmuse amuse) {
        String banner = amuse.getBanner();
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + banner, holder.ivImg);
        if (TextUtils.isEmpty(amuse.getTitle())) {
            holder.tvTitle.setText("暂无标题");
        } else {
            holder.tvTitle.setText(amuse.getTitle());
        }
        if (TextUtils.isEmpty(amuse.getStartDate())) {
            holder.tvTime.setText("暂无比赛开始时间");
        } else {
            holder.tvTime.setText(amuse.getStartDate());
        }
        int timeStatus = amuse.getTimeStatus();
        if (timeStatus == 1) {
            holder.tvAmuseStatus.setText("报名中");
        } else if (timeStatus == 2) {
            holder.tvAmuseStatus.setText("报名预热中");
        } else if (timeStatus == 3) {
            holder.tvAmuseStatus.setText("报名已截止");
        } else if (timeStatus == 4) {
            holder.tvAmuseStatus.setText("赛事已结束");
        } else if (timeStatus == 5) {
            holder.tvAmuseStatus.setText("比赛进行中");
        } else if (timeStatus == 6) {
            holder.tvAmuseStatus.setText("认证已截止");
        }
        holder.llNetbarAmuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(amuse);
            }
        });
    }

    //设置网吧约战VIEW
    private void setMatchView(NetbarMatchesHolder holder, final YueZhan match) {
        AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + match.getReleaser_icon(), holder.ivHeader);
        if (TextUtils.isEmpty(match.getTitle())) {
            holder.tvNickName.setText("暂无名称");
        } else {
            holder.tvNickName.setText(match.getNickname());
        }
        String itemName = match.getItem_name();
        String gameServer = match.getServer();
        if (TextUtils.isEmpty(itemName)) {
            itemName = "未知游戏";
        }
        if (TextUtils.isEmpty(gameServer)) {
            gameServer = "未知服务器";
        }
        holder.tvServer.setText(itemName + "-" + gameServer);
        if (match.getWay() == 1) {
            holder.tvStatus.setText("线上");
        } else if (match.getWay() == 2) {
            holder.tvStatus.setText("线下");
        }
        String beginTime = match.getBegin_time();
        beginTime = TimeFormatUtil.formatNoYear(match.getBegin_time());
        holder.tvTime.setText(beginTime);

        int applyNum = match.getApply_num();
        int peopleNum = match.getPeople_num();
        holder.tvCount.setText(applyNum + "/" + peopleNum);
        holder.llRecommendBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(match);
            }
        });
    }

    @Override
    public int getItemCount() {

        if (info != null) {
            int amuseCount = 0;
            int matchCount = 0;
            int serviceCount = 0;
            int matchTitleCount = 1;
            if (info.getAmuse() != null) {
                amuseCount = 1;
            }
            if (info.getMatches() != null) {
                matchCount = info.getMatches().size();
            }
            if (info.getServices() != null) {
                serviceCount = info.getServices().size();
            }
            return amuseCount + matchCount + serviceCount + matchTitleCount;
        }
        return 0;
    }

    private class NetbarServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView imgServer;
        TextView tvTitle;
        TextView tvCount;
        LinearLayout llServer;

        public NetbarServiceViewHolder(View itemView) {
            super(itemView);
            imgServer = (ImageView) itemView.findViewById(R.id.im_server);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_server_title);
            tvCount = (TextView) itemView.findViewById(R.id.tv_interested);
            llServer = (LinearLayout) itemView.findViewById(R.id.ll_server);
        }
    }

    private class NetbarAmuseViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llNetbarAmuse;
        ImageView ivImg;
        TextView tvAmuseStatus;
        TextView tvTitle;
        TextView tvTime;

        public NetbarAmuseViewHolder(View itemView) {
            super(itemView);
            llNetbarAmuse = (LinearLayout) itemView.findViewById(R.id.llNetbarAmuse);
            ivImg = (ImageView) itemView.findViewById(R.id.ivImg);
            tvAmuseStatus = (TextView) itemView.findViewById(R.id.tvAmuseStatus);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }

    private class NetbarMatchesHolder extends RecyclerView.ViewHolder {
        ImageView ivHeader;
        TextView tvNickName;
        TextView tvServer;
        TextView tvStatus;
        TextView tvTime;
        TextView tvCount;
        LinearLayout llRecommendBattle;

        public NetbarMatchesHolder(View itemView) {
            super(itemView);
            ivHeader = (ImageView) itemView.findViewById(R.id.battle_item_head);
            tvNickName = (TextView) itemView.findViewById(R.id.battle_item_nickname);
            tvServer = (TextView) itemView.findViewById(R.id.battle_item_server);
            tvStatus = (TextView) itemView.findViewById(R.id.battle_item_type);
            tvTime = (TextView) itemView.findViewById(R.id.battle_item_starttime);
            tvCount = (TextView) itemView.findViewById(R.id.battle_item_count);
            llRecommendBattle = (LinearLayout) itemView.findViewById(R.id.llRecommendBattle);
        }
    }

    private class NetbarMatchTitleHolder extends RecyclerView.ViewHolder {
        TextView tvNetbarMatch;
        TextView tvIcon;

        public NetbarMatchTitleHolder(View itemView) {
            super(itemView);
            tvNetbarMatch = (TextView) itemView.findViewById(R.id.tvNetbarMatch);
            tvIcon = (TextView) itemView.findViewById(R.id.tvIcon);
        }
    }

    private class NetbarReleaseWarHolder extends RecyclerView.ViewHolder {
        Button btnReleaseWar;

        public NetbarReleaseWarHolder(View itemView) {
            super(itemView);
            btnReleaseWar = (Button) itemView.findViewById(R.id.btnReleaseWar);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Object object);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
