package com.miqtech.master.client.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Corps;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.utils.LogUtil;

import java.util.List;


/**
 * Created by Administrator on 2016/1/20.
 */
public class MatchCorpsAdapter extends RecyclerView.Adapter {

    private List<Corps> mData;

    private final int ERROR_VIEW = 0;
    private final int HEADER_VIEW = 1;
    private final int ITEM_VIEW = 2;
    private final int FOOTER_VIEW = 3;

    private LayoutInflater inflater;

    private OnItemClickListener mOnItemClickListener;

    private Context mContext;


    private boolean showMore = false;


    public MatchCorpsAdapter(Context context, List<Corps> data, String title) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = data;
        this.title = title;
    }

    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShowMore() {
        return showMore;
    }

    public void setShowMore(boolean showMore) {
        this.showMore = showMore;
    }

    public void setOnitemclickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view;
        switch (viewType) {
            case ERROR_VIEW:
                view = inflater.inflate(R.layout.exception_page, parent, false);
                holder = new ErrorViewHolder(view);
                break;
            case HEADER_VIEW:
                view = inflater.inflate(R.layout.layout_gamelist_header, parent, false);
                holder = new HeadViewHolder(view);
                break;
            case ITEM_VIEW:
                view = inflater.inflate(R.layout.match_corps_item_v2, parent, false);
                holder = new ItemViewHolder(view);
                break;
            case FOOTER_VIEW:
                view = inflater.inflate(R.layout.layout_footer_view, parent, false);
                holder = new FooterViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case ERROR_VIEW:
                ErrorViewHolder errorViewHolder = (ErrorViewHolder) holder;
                errorViewHolder.tvTitle.setText("还没有战队报名呢");
                break;
            case HEADER_VIEW:
                HeadViewHolder headViewHolder = (HeadViewHolder) holder;
                headViewHolder.netbarName.setText(title);
                headViewHolder.filter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(v, position);
                        }
                    }
                });
                break;
            case ITEM_VIEW:
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                final Corps corps = mData.get(position - 1);
                User user = WangYuApplication.getUser(mContext);
                if (user != null) {
                    if (Long.parseLong(user.getId()) != corps.getHeader_user_id()) {
                        if (corps.getIs_join() == 0) {
                            itemViewHolder.btJoin.setText("加入战队");
                            itemViewHolder.btJoin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mOnItemClickListener != null) {
                                        mOnItemClickListener.onItemClick(v, position - 1);
                                    }
                                }
                            });
                        } else {
                            itemViewHolder.btJoin.setText("已加入");
                            itemViewHolder.btJoin.setOnClickListener(null);
                        }
                    } else {
                        itemViewHolder.btJoin.setText("已加入");
                        itemViewHolder.btJoin.setOnClickListener(null);
                    }
                } else {
                    itemViewHolder.btJoin.setText("加入战队");
                    itemViewHolder.btJoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnItemClickListener != null) {
                                mOnItemClickListener.onItemClick(v, position - 1);
                            }
                        }
                    });
                }


                itemViewHolder.corpName.setText(corps.getTeam_name());
                itemViewHolder.joinCount.setText(corps.getNum() + "/" + corps.getTotal_num());
                itemViewHolder.teamLeader.setText(corps.getHeader());
                itemViewHolder.llItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(v, position - 1);
                            LogUtil.e("params", "index : " + (position - 1));
                        }
                    }
                });
                break;
            case FOOTER_VIEW:
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                if (isShowMore()) {
                    footerViewHolder.footerView.setVisibility(View.VISIBLE);
                } else {
                    footerViewHolder.footerView.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mData.isEmpty()) {
            return 2;
        } else {
            return mData.size() + 2;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.isEmpty()) {
            if (position == 1) {
                return ERROR_VIEW;
            } else {
                return HEADER_VIEW;
            }
        }
        if (!mData.isEmpty() && position == 0) {
            return HEADER_VIEW;
        }
        if (position == mData.size() + 1) {
            return FOOTER_VIEW;
        }
        return ITEM_VIEW;
    }

    class ErrorViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        public ErrorViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_err_title);
        }
    }

    class HeadViewHolder extends RecyclerView.ViewHolder {
        private TextView netbarName;
        private TextView filter;

        public HeadViewHolder(View itemView) {
            super(itemView);
            netbarName = (TextView) itemView.findViewById(R.id.tv_filter_type);
            filter = (TextView) itemView.findViewById(R.id.tv_filter);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView corpName;
        private TextView joinCount;
        private TextView teamLeader;
        private Button btJoin;
        private LinearLayout llItem;

        public ItemViewHolder(View itemView) {
            super(itemView);
            corpName = (TextView) itemView.findViewById(R.id.corpName);
            joinCount = (TextView) itemView.findViewById(R.id.joinCount);
            teamLeader = (TextView) itemView.findViewById(R.id.team_leader);
            btJoin = (Button) itemView.findViewById(R.id.bt_join);
            llItem = (LinearLayout) itemView.findViewById(R.id.ll_item);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ProgressBar progressBar;
        RelativeLayout footerView;

        public FooterViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.footer_tv);
            progressBar = (ProgressBar) view.findViewById(R.id.footer_progress);
            footerView = (RelativeLayout) view.findViewById(R.id.footerView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int postion);
    }

}
