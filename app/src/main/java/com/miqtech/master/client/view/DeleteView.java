package com.miqtech.master.client.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.miqtech.master.client.R;

public class DeleteView extends Dialog implements View.OnClickListener {
	private TextView leftTextView, rightTextView;
	private IDialogOnclickInterface dialogOnclickInterface;

	public void setDialogOnclickInterface(IDialogOnclickInterface dialogOnclickInterface) {
		this.dialogOnclickInterface = dialogOnclickInterface;
	}

	private Context context;

	public DeleteView(Context context, int theme,int layout) {
		super(context, theme);
		this.context = context;
		setContentView(layout);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		leftTextView = (TextView) findViewById(R.id.textview_one);
		rightTextView = (TextView) findViewById(R.id.textview_two);
		leftTextView.setOnClickListener(this);
		rightTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.textview_one:
			 dialogOnclickInterface.leftOnclick();
			break;
		case R.id.textview_two:
			dialogOnclickInterface.rightOnclick();
			break;
		default:
			break;
		}
	}

	public interface IDialogOnclickInterface {
		 void leftOnclick();

		void rightOnclick();
	}
}
