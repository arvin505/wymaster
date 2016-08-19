package com.miqtech.master.client.ui;

import android.content.Intent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/18 0018.
 */
public class GuideActivity extends BaseActivity implements Animation.AnimationListener {
    @Bind(R.id.ivFont)
    ImageView ivFont;
    @Bind(R.id.ivImg1)
    ImageView ivImg1;
    @Bind(R.id.ivImg2)
    ImageView ivImg2;
    @Bind(R.id.ivImg3)
    ImageView ivImg3;
    @Bind(R.id.btnToJoin)
    Button btnToJoin;
    private AlphaAnimation alphaAnimation1;
    private AlphaAnimation alphaAnimation2;
    private AlphaAnimation alphaAnimation3;
    private AlphaAnimation alphaAnimation4;
    private AlphaAnimation alphaAnimation5;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        alphaAnimation1 = new AlphaAnimation(0, 1);
        alphaAnimation1.setDuration(800);
        ivFont.startAnimation(alphaAnimation1);
        alphaAnimation2 = new AlphaAnimation(0, 1);
        alphaAnimation2.setDuration(800);
        alphaAnimation3 = new AlphaAnimation(0, 1);
        alphaAnimation3.setDuration(800);
        alphaAnimation4 = new AlphaAnimation(0, 1);
        alphaAnimation4.setDuration(800);
        alphaAnimation5 = new AlphaAnimation(0, 1);
        alphaAnimation5.setDuration(800);

        alphaAnimation1.setAnimationListener(this);
        alphaAnimation2.setAnimationListener(this);
        alphaAnimation3.setAnimationListener(this);
        alphaAnimation4.setAnimationListener(this);
        alphaAnimation5.setAnimationListener(this);


        btnToJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(GuideActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation == alphaAnimation1){
            ivImg1.setVisibility(View.VISIBLE);
            ivImg1.startAnimation(alphaAnimation2);
        }else if(animation == alphaAnimation2){
            ivImg2.setVisibility(View.VISIBLE);
            ivImg2.startAnimation(alphaAnimation3);
        }else if(animation == alphaAnimation3){
            ivImg3.setVisibility(View.VISIBLE);
            ivImg3.startAnimation(alphaAnimation4);
        }else if(animation == alphaAnimation4) {
            btnToJoin.setVisibility(View.VISIBLE);
            btnToJoin.startAnimation(alphaAnimation5);
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
