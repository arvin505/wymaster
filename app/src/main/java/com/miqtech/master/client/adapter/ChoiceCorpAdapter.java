package com.miqtech.master.client.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Corps;

import java.util.List;

/**
 * Created by Administrator on 2016/1/25.
 */
public class ChoiceCorpAdapter extends RecyclerView.Adapter {

    private List<Corps> mCrops;
    private Context mContext;
    private LayoutInflater inflater;
    private int selected = -1;
    private boolean showMore = false;

    private final int FOOTER_VIEW = 0;
    private final int ITEM_VIEW = 1;
    private final int CREAT_CORPS_VIEW = 2;
    private final int WRITER_CORPS_NAME_VIEW = 3;
    private final int EMPTY_VIEW = 4;

    private int typeView = -1;//1显示创建战队按钮，2 显示填写战队名称界面


    public ChoiceCorpAdapter(List<Corps> mCrops, Context mContext, int typeView) {
        this.mCrops = mCrops;
        this.mContext = mContext;
        this.typeView = typeView;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        if (mCrops.isEmpty()) {
            return 1;
        } else {
            return mCrops.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mCrops.isEmpty()) {
            if (typeView == 1) {
                return CREAT_CORPS_VIEW;
            } else if (typeView == 2) {
                return WRITER_CORPS_NAME_VIEW;
            } else {
                return EMPTY_VIEW;
            }
        }

        if (!mCrops.isEmpty()) {
            if (position + 1 == getItemCount()) {
                return FOOTER_VIEW;
            }
            return ITEM_VIEW;
        }
        return EMPTY_VIEW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view;
        switch (viewType) {
            case ITEM_VIEW:
                view = inflater.inflate(R.layout.layout_choice_corp_item, parent, false);
                holder = new ItemViewHolder(view);
                break;
            case FOOTER_VIEW:
                view = inflater.inflate(R.layout.layout_footer_view, parent, false);
                holder = new FooterViewHolder(view);
                break;
            case CREAT_CORPS_VIEW:
                view = inflater.inflate(R.layout.layout_creat_corps, parent, false);
                holder = new CorpsViewHolder(view);
                break;
            case WRITER_CORPS_NAME_VIEW:
                view = inflater.inflate(R.layout.layout_creat_corps, parent, false);
                holder = new CorpsViewHolder(view);
                break;
            case EMPTY_VIEW:
                view = inflater.inflate(R.layout.layout_empty, parent, false);
                holder = new EmptyViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_VIEW:
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                getItemView(itemViewHolder, position);
                break;
            case FOOTER_VIEW:
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                if (showMore) {
                    footerViewHolder.footerView.setVisibility(View.VISIBLE);
                } else {
                    footerViewHolder.footerView.setVisibility(View.GONE);
                }
                break;
            case CREAT_CORPS_VIEW:
                CorpsViewHolder corpsViewHolder = (CorpsViewHolder) holder;
                corpsViewHolder.llWriterCorpsName.setVisibility(View.GONE);
                corpsViewHolder.llCreatCorps.setVisibility(View.VISIBLE);
                toWriterCorpsName(corpsViewHolder);
                mListener.isShowBtn();
                break;
            case WRITER_CORPS_NAME_VIEW:
                CorpsViewHolder corpsViewHolder2 = (CorpsViewHolder) holder;
                corpsViewHolder2.llCreatCorps.setVisibility(View.GONE);
                corpsViewHolder2.llWriterCorpsName.setVisibility(View.VISIBLE);
                mListener.isShowBtn();
                mListener.onListenerForEditView(corpsViewHolder2.edCropsName,corpsViewHolder2.tvWordsNumber);
                break;
        }
    }

    private void toWriterCorpsName(CorpsViewHolder myHolder) {
        myHolder.btCreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTypeView(2);
            }
        });
    }

    private void getItemView(ItemViewHolder holder, final int position) {
        if (mCrops.isEmpty()) {
            return;
        }
        Corps corp = mCrops.get(position);
        holder.tvNum.setText(corp.getNum() + "");
        holder.tvCaptain.setText("队长:" + corp.getHeader());
        holder.tvTeamName.setText(corp.getTeam_name() + "");
        holder.tvTotal.setText(corp.getTotal_num() + "");
        if (position == selected) {
            holder.imSelected.setImageResource(R.drawable.ic_pay_checked);
        } else {
            holder.imSelected.setImageResource(R.drawable.icon_netbar_unchecked);
        }

        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemclick(position);
                }
            }
        });
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNum;
        private TextView tvTotal;
        private TextView tvTeamName;
        private TextView tvCaptain;
        private ImageView imSelected;
        private LinearLayout llItem;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvNum = (TextView) itemView.findViewById(R.id.tv_num);
            tvTotal = (TextView) itemView.findViewById(R.id.tv_total);
            tvTeamName = (TextView) itemView.findViewById(R.id.tv_teamname);
            tvCaptain = (TextView) itemView.findViewById(R.id.tv_captain);
            imSelected = (ImageView) itemView.findViewById(R.id.im_selected);
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

    class CorpsViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llCreatCorps;
        TextView tvTitle;
        Button btCreat;

        LinearLayout llWriterCorpsName;
        TextView tvExplain;
        EditText edCropsName;
        TextView tvWordsNumber;
        public CorpsViewHolder(View view) {
            super(view);
            llCreatCorps = (LinearLayout) view.findViewById(R.id.ll_creat_corps);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            btCreat = (Button) view.findViewById(R.id.btn_create_crops);

            llWriterCorpsName = (LinearLayout) view.findViewById(R.id.ll_writer_corps_name);
            tvExplain = (TextView) view.findViewById(R.id.tv_title_explain);
            edCropsName = (EditText) view.findViewById(R.id.ed_corps_name);
            tvWordsNumber = (TextView)view.findViewById(R.id.tv_words_number);
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View view) {
            super(view);
        }
    }


    public boolean isShowMore() {
        return showMore;
    }

    public void setShowMore(boolean showMore) {
        this.showMore = showMore;
    }

    private OnItemClickListener mListener;

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public interface OnItemClickListener {
        /**
         * listview里item的点击事件
         *
         * @param position
         */
        void onItemclick(int position);

        /**
         * 监听是否显示确认按钮
         */
        void isShowBtn();

        /**
         * 监听战队名称输入框 并做出相应的变化
         *
         * @param editText 输入框
         * @param textView 显示字的剩余
         */
        void onListenerForEditView(EditText editText,TextView textView);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    /**
     *
     * @param typeView  1显示创建战队按钮，2 显示填写战队名称界面
     */
    public void setTypeView(int typeView) {
        this.typeView = typeView;
        mCrops.clear();
        this.notifyDataSetChanged();
    }

    public int getTypeView() {
        return typeView;
    }
}
