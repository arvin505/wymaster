package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 专题适配器
 * Created by zhaosentao on 2015/11/26.
 */
public class ExpertAdapter extends BaseAdapter {

    private Context context;
    private List<InforItemDetail> list;
    private String read;
    private String wread;
    private String picture;
    private List<ImageView> imageViews = new ArrayList<ImageView>();

    public ExpertAdapter(Context context, List<InforItemDetail> inforItemDetails) {
        this.context = context;
        this.list = inforItemDetails;
        read = context.getResources().getString(R.string.read);
        wread = context.getResources().getString(R.string.wread);
        picture = context.getResources().getString(R.string.picture);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.information_listview_item, null);

            /*专题或者图文界面*/
            holder.iv = (ImageView) convertView.findViewById(R.id.image_iv_item_infor_fragment);
            holder.title = (TextView) convertView.findViewById(R.id.title_tv_item_infor_fragment);
            holder.content = (TextView) convertView.findViewById(R.id.content_item_infor_fragment);
            holder.yue = (TextView) convertView.findViewById(R.id.yue_tv_item_infor_fragment);
            holder.zhuan = (TextView) convertView.findViewById(R.id.zhuan_tv_item_infor_fragment);
            holder.last_line2 = convertView.findViewById(R.id.last_line2);


            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_buttom_title_infor_fragment);
            holder.image1 = (ImageView) convertView.findViewById(R.id.iv1_infor_fragment);
            holder.image2 = (ImageView) convertView.findViewById(R.id.iv2_infor_fragment);
            holder.image3 = (ImageView) convertView.findViewById(R.id.iv3_infor_fragment);
            holder.tv_num = (TextView) convertView.findViewById(R.id.tv_several_image_infor_fragment);
            holder.tv_yuedu = (TextView) convertView.findViewById(R.id.tv_yue_several_image_infor_fragment);
            holder.fl_imgs_infor_fragment = (FrameLayout) convertView.findViewById(R.id.fl_imgs_infor_fragment);
            holder.last_line2 = convertView.findViewById(R.id.last_line2);
            /**/
            holder.item1 = (LinearLayout) convertView.findViewById(R.id.ll_item1_information);
            holder.item2 = (LinearLayout) convertView.findViewById(R.id.ll_item2_information);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        InforItemDetail bean = list.get(position);

        if (list.get(position).getType() == 3) {//有3张图的
            holder.item1.setVisibility(View.GONE);
            holder.item2.setVisibility(View.VISIBLE);
            showData_2(list.get(position), holder, position);
        } else {
            holder.item2.setVisibility(View.GONE);
            holder.item1.setVisibility(View.VISIBLE);
            showData(bean, holder, position);
        }
        return convertView;
    }

    /**
     * 显示图文
     *
     * @param bean
     * @param holder
     * @param position
     */
    private void showData(InforItemDetail bean, ViewHolder holder, int position) {//type	类型:1图文 2专题 3图集

        if (bean.getTitle() != null) {
            holder.title.setText(bean.getTitle());
        } else {
            holder.title.setText("");
        }
        if (bean.getBrief() != null) {
            holder.content.setText(bean.getBrief());
        } else {
            holder.content.setText("");
        }

        holder.yue.setText(getYueNum(bean.getRead_num()));

//        AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon(), holder.iv);
        AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + Utils.transformUTF8(bean.getIcon()), holder.iv);

//        if (position == list.size() - 1) {
//            holder.last_line.setVisibility(View.GONE);
//        }
    }

    /**
     * 显示图集数据
     *
     * @param bean
     * @param viewHolder2
     * @param position
     */
    private void showData_2(InforItemDetail bean, ViewHolder viewHolder2, int position) {//type	类型:1图文 2专题 3图集
        if (bean.getTitle() != null) {
            viewHolder2.tv_title.setText(bean.getTitle());
        } else {
            viewHolder2.tv_title.setText("");
        }
        viewHolder2.tv_yuedu.setText(getYueNum(bean.getRead_num()));
        String[] myImgList = null;
        if (bean.getImgs() != null) {
            myImgList = bean.getImgs().split(",");
            if (myImgList.length > 0) {
                viewHolder2.tv_num.setText(myImgList.length + picture);
            } else {
                viewHolder2.tv_num.setText(0 + picture);
            }
        }
        showImage(myImgList, viewHolder2);

//        if (position == list.size() - 1) {
//            viewHolder2.last_line2.setVisibility(View.GONE);
//        }
    }

    /**
     * 显示图片
     *
     * @param Str
     * @param hh
     */
    private void showImage(String[] Str, ViewHolder hh) {
        if (Str == null || "".equals(Str) || Str.length == 0) {
            hh.fl_imgs_infor_fragment.setVisibility(View.GONE);
            return;
        }
        imageViews.clear();
        imageViews.add(hh.image1);
        imageViews.add(hh.image2);
        imageViews.add(hh.image3);
        hh.fl_imgs_infor_fragment.setVisibility(View.VISIBLE);
        int length;
        if (Str.length > 3) {
            length = 3;
        } else {
            length = Str.length;
        }

        for (ImageView myImg : imageViews) {
            myImg.setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < length; i++) {
            imageViews.get(i).setVisibility(View.VISIBLE);
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + Str[i] + "!small", imageViews.get(i));
        }
    }

    /**
     * 得到阅读数的2种形式 ： 6555阅    9.6万阅
     *
     * @param i
     * @return
     */
    private String getYueNum(int i) {
        String str = "";
        if (i < 10000) {
            str = i + read;
        } else {
            str = i % 1000 / 10 + wread;
        }
        return str;
    }

    class ViewHolder {
        ImageView iv;
        TextView title;//标题
        TextView content;//内容
        TextView yue;//阅读量
        TextView zhuan;//是否是专题
        View last_line;


        LinearLayout item1;
        LinearLayout item2;
        TextView tv_title;//标题
        ImageView image1;
        ImageView image2;
        ImageView image3;
        TextView tv_num;//显示几张图的数字
        TextView tv_yuedu;//阅读量
        View last_line2;
        FrameLayout fl_imgs_infor_fragment;


    }
}
