package com.miqtech.master.client.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

public class MySpinner extends Spinner {

	public MySpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public void closed(){
		onDetachedFromWindow();
	}
	
}
