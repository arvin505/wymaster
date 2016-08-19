package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Gallery;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.adapter.RegisterGalleryAdapter;
import com.miqtech.master.client.entity.MyRegister;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.fragment.FragmentRegisterFihish;
import com.miqtech.master.client.ui.fragment.FragmentRegisterImportCode;
import com.miqtech.master.client.ui.fragment.FragmentRegisterImportPhone;
import com.miqtech.master.client.ui.fragment.FragmentRegisterSetPassword;
import com.miqtech.master.client.view.MyPagerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/9.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.register_grallery)
    Gallery myGallery;
    @Bind(R.id.mypagerview)
    MyPagerView myPagerview;

    @Bind(R.id.tvRightHandle)
    TextView tvRightHandle;

    private String[] titles;
    private List<MyRegister> myRegisterList = new ArrayList<MyRegister>();
    private Context context;
    private RegisterGalleryAdapter adapter;
    private FragmentPagerAdpter pagerAdpter;

    public int retrievePassword = 0;//用来标记是否是找回密码  2表示是找回密码，其它符号表示是注册

    private String noteCode;

    private String password;


    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        context = RegisterActivity.this;
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle(getResources().getString(R.string.register));
        getButtomLineView().setVisibility(View.GONE);
        getLeftBtn().setOnClickListener(this);

        getData();
        adapter = new RegisterGalleryAdapter(context, myRegisterList);
        myGallery.setAdapter(adapter);

        pagerAdpter = new FragmentPagerAdpter(this);
        pagerAdpter.addTab(FragmentRegisterImportPhone.class, null);
        pagerAdpter.addTab(FragmentRegisterImportCode.class, null);
        pagerAdpter.addTab(FragmentRegisterSetPassword.class, null);
        pagerAdpter.addTab(FragmentRegisterFihish.class, null);
        myPagerview.setAdapter(pagerAdpter);
        myPagerview.setCurrentItem(0);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }

    private void getData() {
        retrievePassword = getIntent().getIntExtra("retrievePassword", -1);
//        retrievePassword = 2;
        if (2 == retrievePassword) {
            setLeftIncludeTitle(getResources().getString(R.string.findPassword));
            titles = getResources().getStringArray(R.array.retrieve_password);
        } else {
            setLeftIncludeTitle(getResources().getString(R.string.register));
            titles = getResources().getStringArray(R.array.register_titles);
        }
        for (int i = 0; i < titles.length; i++) {
            MyRegister bean = new MyRegister();
            bean.setTitle(titles[i]);
            if (0 == i) {
                bean.setType(1);
            } else {
                bean.setType(0);
            }
            myRegisterList.add(bean);
        }
    }

    /**
     * 进入下个操作页面
     *
     * @param i
     */
    public void SkipNextFragment(int i) {
        myRegisterList.get(i).setType(1);
        adapter.notifyDataSetChanged();
        myPagerview.setCurrentItem(i);
        myGallery.setSelection(i);
    }

    /**
     * 获取右上角的空间
     *
     * @return
     */
    public TextView getRightTv() {
        tvRightHandle.setVisibility(View.VISIBLE);
        return tvRightHandle;
    }

    /**
     * 判断是否是找回密码。1表示注册，2表示找回密码
     *
     * @return
     */
    public int getRetrievePassword() {
        return retrievePassword;
    }

    /**
     * 创建弹框提示
     *
     * @param mContext
     * @param title    标题
     * @param content  内容
     */
    public void creatDialogForHint(Context mContext, String title, String content) {
        final Dialog mDialog = new Dialog(mContext, R.style.register_style);
        mDialog.setContentView(R.layout.dialog_register_marked_words);
        TextView title_tv = (TextView) mDialog.findViewById(R.id.dialog_title_register);
        TextView context_tv = (TextView) mDialog.findViewById(R.id.dialog_content_register);
        TextView ok_bt = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);

        if (!TextUtils.isEmpty(title)) {
            title_tv.setText(title);
            title_tv.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(content)) {
            context_tv.setText(content);
            context_tv.setVisibility(View.VISIBLE);
        }

        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public void creatDialogImg(Context mContext) {

        final Dialog mDialog = new Dialog(mContext, R.style.register_style);
        mDialog.setContentView(R.layout.layout_hintalert_dialog);
        TextView result_tv = (TextView) mDialog.findViewById(R.id.tvResult);
        TextView tvContent = (TextView) mDialog.findViewById(R.id.tvContent);
        result_tv.setText("成功");
        try {
            mDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000);


    }

    public void setNoteCode(String str) {
        noteCode = str;
    }

    public String getNoteCode() {
        return noteCode;
    }

    public void setPassword(String str) {
        password = str;
    }

    public String getPassword() {
        return password;
    }

}
