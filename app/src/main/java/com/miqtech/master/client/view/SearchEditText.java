package com.miqtech.master.client.view;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.SearchEditTextAdapter;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class SearchEditText extends AutoCompleteTextView{

	private Button confirm;
	private SearchEditTextAdapter searchAdapter;
	private LayoutInflater inflater;
	private Context mContext;
	public SearchEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public SearchEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public SearchEditText(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	private void init(){
		//通讯录
		inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);		
		setThreshold(1);
		setDropDownBackgroundResource(R.color.little_gray);
//		setDropDownBackgroundResource(R.color.);
		//设置选择框透明度
		getDropDownBackground().setAlpha(0);
//		setDropDownHeight();
		setThreshold(1);
		setDropDownVerticalOffset(6);
		initListener();
	}
	
	/**
	 * 设置监听器
	 */
	private void initListener(){
		this.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				AutoCompleteTextView view = (AutoCompleteTextView) v;
				if (hasFocus) {
						view.showDropDown();
				}
			}
		});
	}
}
