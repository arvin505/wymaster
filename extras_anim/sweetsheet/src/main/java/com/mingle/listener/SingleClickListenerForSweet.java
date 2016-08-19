package com.mingle.listener;

import android.view.View;


/**
 * @author zzz40500
 * @version 1.0
 * @date 2015/8/5.
 * @github: https://github.com/zzz40500
 */
public class SingleClickListenerForSweet implements View.OnClickListener {

    private View.OnClickListener mListener;

    private SingleClickHelperForSweet singleClickhelperForSweet =new SingleClickHelperForSweet();

    public SingleClickListenerForSweet(View.OnClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onClick(View v) {

        if (singleClickhelperForSweet.clickEnable()) {
            if(mListener != null) {
                mListener.onClick(v);
            }
        }

    }


}
