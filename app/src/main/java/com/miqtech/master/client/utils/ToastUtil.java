package com.miqtech.master.client.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.miqtech.master.client.application.WangYuApplication;

public class ToastUtil {

	public static void showToast(String str, Context context) {
		Toast toast = Toast.makeText(context.getApplicationContext(), str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 30);
		toast.show();
	}

	public static void showToastResources(int id, Context context) {
		showToast(context.getApplicationContext().getResources().getString(id), context.getApplicationContext());
	}

}
