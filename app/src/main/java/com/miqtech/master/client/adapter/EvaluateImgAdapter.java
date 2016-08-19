package com.miqtech.master.client.adapter;

import java.io.File;
import java.util.List;
import java.util.UUID;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.NetbarEvaluateActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class EvaluateImgAdapter extends BaseAdapter {

    private List<String> heads;
    private Context mContext;
    private LayoutInflater inflater;

    public EvaluateImgAdapter(Context mContext, List<String> heads) {
        super();
        this.heads = heads;
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return heads.size();
    }

    @Override
    public String getItem(int position) {
        // TODO Auto-generated method stub
        return heads.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.head_image_item, null);
        }
        ImageView iv_head = (ImageView) convertView;
        android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
                Utils.dip2px(mContext, 87), Utils.dip2px(mContext, 87));
        iv_head.setLayoutParams(params);
        if (parent.getId() == R.id.head_Gride) {
            if ((position == 0 || position == 1)) {
                int imageRes = Integer.parseInt(heads.get(position));
                AsyncImage.loadPhoto(mContext, imageRes, iv_head);
            } else {
                AsyncImage.loadPhoto(mContext, new File(heads.get(position)), iv_head);
            }
        }
        if (parent.getId() == R.id.photo_Gride) {
            File imag = new File(heads.get(position));
            AsyncImage.loadPhoto(mContext, imag, iv_head);
        }
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parent.getId() == R.id.head_Gride) {
                    if (position == 0) {
                        // 拍照
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 获得名字
                        ((NetbarEvaluateActivity) mContext).photoFile = new File(Environment
                                .getExternalStorageDirectory() + "/" + UUID.randomUUID() + ".jpg");
                        ((NetbarEvaluateActivity) mContext).serverPhoto = ((NetbarEvaluateActivity) mContext).photoFile
                                .getPath();
                        // 下面这句指定调用相机拍照后的照片存储的路径

                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(((NetbarEvaluateActivity) mContext).photoFile));
                        ((NetbarEvaluateActivity) mContext).startActivityForResult(intent, 1);
                        ((NetbarEvaluateActivity) mContext).drawer.animateClose();
                    } else if (position == 1) {
                        // 相册
                        // Intent album = new Intent(Intent.ACTION_GET_CONTENT);
                        // album.setType("image");
                        // startActivityForResult(album, 2);
                        ((NetbarEvaluateActivity) mContext).getImages();
                    } else {
                        ((NetbarEvaluateActivity) mContext).photoFile = new File(
                                ((NetbarEvaluateActivity) mContext).pics.get(position));
                        String name = ((NetbarEvaluateActivity) mContext).photoFile.getPath().substring(
                                ((NetbarEvaluateActivity) mContext).photoFile.getPath().lastIndexOf("/"));
                        ((NetbarEvaluateActivity) mContext).imagenames = System.currentTimeMillis()
                                + name.substring(name.indexOf("."));
                        ((NetbarEvaluateActivity) mContext).serverPhoto = ((NetbarEvaluateActivity) mContext).pics
                                .get(position);
                        ((NetbarEvaluateActivity) mContext).setPicWithoutCut(null, Uri.fromFile(new File(
                                ((NetbarEvaluateActivity) mContext).serverPhoto)));
                        ((NetbarEvaluateActivity) mContext).drawer.animateClose();
                    }
                } else {
                    ((NetbarEvaluateActivity) mContext).photoFile = new File(
                            ((NetbarEvaluateActivity) mContext).photoList.get(position));
                    String name = ((NetbarEvaluateActivity) mContext).photoFile.getPath().substring(
                            ((NetbarEvaluateActivity) mContext).photoFile.getPath().lastIndexOf("/"));
                    ((NetbarEvaluateActivity) mContext).imagenames = System.currentTimeMillis()
                            + name.substring(name.indexOf("."));
                    ((NetbarEvaluateActivity) mContext).serverPhoto = ((NetbarEvaluateActivity) mContext).photoFile
                            .getPath();
                    ((NetbarEvaluateActivity) mContext).setPicWithoutCut(null,Uri
                            .fromFile(((NetbarEvaluateActivity) mContext).photoFile));
                    ((NetbarEvaluateActivity) mContext).drawer.animateClose();
                }
            }
        });
        return convertView;
    }

}
