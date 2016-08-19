package com.miqtech.master.client.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.ReleaseWarActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 *           dateTimePicKDialog=new
 *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 *           dateTimePicKDialog.dateTimePicKDialog(inputDate);
 * 
 *           } });
 * 
 * @author
 */
public class DateTimePickDialog implements OnDateChangedListener, OnTimeChangedListener {
	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;

	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialog(Activity activity, String initDateTime) {
		this.activity = activity;
		if(TextUtils.isEmpty(initDateTime)){
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1),
					calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
					(calendar.get(Calendar.MINUTE) + 30));
			initDateTime = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-"
					+ calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
					+ calendar.get(Calendar.MINUTE);
		}
		this.initDateTime = initDateTime;

	}

	public void init(DatePicker datePicker, TimePicker timePicker) {
		// Calendar calendar = Calendar.getInstance();
		// if (!(null == initDateTime || "".equals(initDateTime))) {
		Calendar calendar = this.getCalendarByInintData(initDateTime);
		// } else {
		// initDateTime = calendar.get(Calendar.YEAR) + "-" +
		// calendar.get(Calendar.MONTH) + "-"
		// + calendar.get(Calendar.DAY_OF_MONTH) + " " +
		// calendar.get(Calendar.HOUR_OF_DAY) + ":"
		// + calendar.get(Calendar.MINUTE);
		// }

		datePicker.init(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) - 1),
				calendar.get(Calendar.DAY_OF_MONTH), this);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
	}

	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	@SuppressLint("NewApi")
	public AlertDialog dateTimePicKDialog(final TextView inputDate) {
		LinearLayout dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.common_datetime,
				null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);

		ad = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK).setTitle(initDateTime)
				.setView(dateTimeLayout).setPositiveButton("设置", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (matchDate(dateTime)) {
							inputDate.setText(dateTime);
							ReleaseWarActivity.initDateTime = dateTime;
						} else {
							Calendar currentCalendar = Calendar.getInstance();
							Date currentDate = currentCalendar.getTime();
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
							String currentDateString = formatter.format(currentDate);
							
							Calendar afterCalendar = Calendar.getInstance();
							afterCalendar.add(Calendar.DATE, 7);
							Date afterDate = afterCalendar.getTime();
							SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
							String afterDateString = formatter1.format(afterDate);
							
							Toast toast = Toast.makeText(activity,
								     "请输入"+currentDateString+"至"+afterDateString, Toast.LENGTH_SHORT);
								   toast.setGravity(Gravity.CENTER, 0, 0);
								   toast.show();
						}

					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).show();
		onDateChanged(null, 0, 0, 0);
		return ad;
	}

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		// 获得日历实例
		Calendar calendar = Calendar.getInstance();

		calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
				timePicker.getCurrentHour(), timePicker.getCurrentMinute());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		dateTime = sdf.format(calendar.getTime());
		ad.setTitle(dateTime);
	}

	private boolean matchDate(String strDate) {
		Calendar calendar = getCalendarByInintData(dateTime);
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.set(currentCalendar.get(Calendar.YEAR), (currentCalendar.get(Calendar.MONTH) + 1),
				currentCalendar.get(Calendar.DAY_OF_MONTH), currentCalendar.get(Calendar.HOUR_OF_DAY),
				currentCalendar.get(Calendar.MINUTE));
		if (calendar.after(currentCalendar)) {
			currentCalendar.add(Calendar.DATE, 7);

			if (currentCalendar.after(calendar)) {
				return true;

			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	/**
	 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();

		// 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
		String date = spliteString(initDateTime, " ", "index", "front"); // 日期
		String time = spliteString(initDateTime, " ", "index", "back"); // 时间

		String[] dateList = date.split("-");

		// String yearStr = spliteString(date, "年", "index", "front"); // 年份
		// String monthAndDay = spliteString(date, "年", "index", "back"); // 月日
		//
		// String monthStr = spliteString(monthAndDay, "月", "index", "front");
		// // 月
		// String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日

		String hourStr = spliteString(time, ":", "index", "front"); // 时
		String minuteStr = spliteString(time, ":", "index", "back"); // 分

		int currentHour = Integer.valueOf(hourStr.trim()).intValue();
		int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

		calendar.set(Integer.valueOf(dateList[0]).intValue(), Integer.valueOf(dateList[1]).intValue(),
				Integer.valueOf(dateList[2]).intValue(), currentHour, currentMinute);
		return calendar;
	}

	/**
	 * 截取子串
	 * 
	 * @param srcStr
	 *            源串
	 * @param pattern
	 *            匹配模式
	 * @param indexOrLast
	 * @param frontOrBack
	 * @return
	 */
	public static String spliteString(String srcStr, String pattern, String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
		} else {
			loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc); // 截取子串
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
		}
		return result;
	}

}
