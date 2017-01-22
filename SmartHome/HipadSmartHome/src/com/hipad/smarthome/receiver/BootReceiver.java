package com.hipad.smarthome.receiver;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hipad.smart.service.ServiceImpl;
import com.hipad.smarthome.AppointMentItem;
import com.hipad.smarthome.LoginActivity;
import com.hipad.smarthome.MainActivity;
import com.hipad.smarthome.MyApplication;
import com.hipad.smarthome.R;
import com.hipad.smarthome.moudle.AppointItem;
import com.hipad.smarthome.utils.AppointMentDataBase;
import com.hipad.smarthome.utils.DatabaseHelper;

public class BootReceiver extends BroadcastReceiver {
	private final static String TAG = "BootReceiver";
	private Context mContext;
	private AlarmManager mAlarmManager;

	private boolean startAppointMent(AppointMentItem itemInfo) {

		String appointTime = itemInfo.getAppointTime();
		String appointDate = itemInfo.getAppointDate();
		int appoint_hour = Integer.parseInt(appointTime) / 60;
		int appoint_minute = Integer.parseInt(appointTime) % 60;
		String edit_year = appointDate.substring(0, 4);
		String edit_month = appointDate.substring(4,6);
		String edit_day = appointDate.substring(6, 8);
		int appointment_year = Integer.parseInt(edit_year);
		int appointment_month = Integer.parseInt(edit_month);
		int appointment_day = Integer.parseInt(edit_day);
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);		
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		if (appoint_hour == 0) {
			appoint_hour = 24;
		}
		int delaySecond = ((appoint_hour - hour) * 60 + (appoint_minute - minute)) * 60;
		c.setTimeInMillis(System.currentTimeMillis());
		c.add(Calendar.YEAR, appointment_year-year);
		c.add(Calendar.MONTH, appointment_month-month);
		c.add(Calendar.DAY_OF_MONTH, appointment_day-day);
		c.add(Calendar.SECOND,delaySecond);
		long delayTime = c.getTimeInMillis() - System.currentTimeMillis();
		if (delayTime > 0) {
			Intent intent = new Intent(mContext.getApplicationContext(),AlarmReceiver.class);
			intent.putExtra("gateway_id", itemInfo.gatewayID);
			intent.putExtra("device_id", itemInfo.deviceID);
			intent.putExtra("warm_tmp", itemInfo.warm_tmp);
			intent.putExtra("warm_time",itemInfo.warm_time);
			intent.putExtra("appoint_date",itemInfo.appoint_date);
			intent.putExtra("appoint_time",itemInfo.appoint_time);
			intent.putExtra("appoint_name", itemInfo.appoint_name);
			PendingIntent operation = PendingIntent.getBroadcast(mContext, Integer.parseInt(itemInfo.appoint_date) + (Integer.parseInt(itemInfo.appoint_time)*10000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), operation);
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isTimePassed(AppointMentItem itemInfo) {
		String appointTime = itemInfo.getAppointTime();
		String appointDate = itemInfo.getAppointDate();
		int appoint_hour = Integer.parseInt(appointTime) / 60;
		int appoint_minute = Integer.parseInt(appointTime) % 60;
		String edit_year = appointDate.substring(0, 4);
		String edit_month = appointDate.substring(4, 6);
		String edit_day = appointDate.substring(6, 8);
		int appointment_year = Integer.parseInt(edit_year);
		int appointment_month = Integer.parseInt(edit_month);
		int appointment_day = Integer.parseInt(edit_day);
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		if (appoint_hour == 0) {
			appoint_hour = 24;
		}
		int delaySecond = ((appoint_hour - hour) * 60 + (appoint_minute - minute)) * 60;

		c.setTimeInMillis(System.currentTimeMillis());
		c.add(Calendar.YEAR, appointment_year - year);
		c.add(Calendar.MONTH, appointment_month - month);
		c.add(Calendar.DAY_OF_MONTH, appointment_day - day);
		c.add(Calendar.SECOND, delaySecond);
		long delayTime = c.getTimeInMillis() - System.currentTimeMillis();
		if (delayTime <= 0) {
			return true;
		} else {
			return false;
		}
	}

	private void stopAppointMent(AppointMentItem itemInfo) {
		Intent intent = new Intent(mContext.getApplicationContext(),AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(mContext,Integer.parseInt(itemInfo.appoint_date) + (Integer.parseInt(itemInfo.appoint_time)*10000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
	}
	
	public static void stopAllAppoint(Context context,String userId) {
		AlarmManager mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		final AppointMentDataBase db = AppointMentDataBase.getInstance(context);
		Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
		PendingIntent sender; 
		ArrayList<AppointMentItem> itemList = db.getAppointList(userId);
		for (AppointMentItem item : itemList) {
			sender = PendingIntent.getBroadcast(context,Integer.parseInt(item.appoint_date) + (Integer.parseInt(item.appoint_time)*10000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mManager.cancel(sender);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "boot completed, action:" + intent.getAction());
		if(MyApplication.user == null) {
			return;
		}
		String userId = MyApplication.user.getUserId();
		Log.d(TAG,"userId:" + userId);
		if (userId == null) {
			return;
		}
		mContext = context;
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		final AppointMentDataBase db = AppointMentDataBase.getInstance(context);
		ArrayList<AppointMentItem> itemList = db.getAppointList(userId);
		for (AppointMentItem item : itemList) {
			ContentValues values = new ContentValues();
			Calendar c = Calendar.getInstance();
			if (item.getIsEveryDay() == 1) {
				if (item.getAppointStart() == 1) {
					if (isTimePassed(item)) {
						stopAppointMent(item);
						c.setTimeInMillis(System.currentTimeMillis());
						c.add(Calendar.HOUR_OF_DAY, 24);
						int newAppoint_year = c.get(Calendar.YEAR);
						int newAppoint_month = c.get(Calendar.MONTH) + 1;
						int newAppoint_day = c.get(Calendar.DAY_OF_MONTH);
						String new_appointD, new_appointM, new_appointDate, old_appointDate;
						if (newAppoint_month < 10) {
							new_appointM = "0" + String.valueOf(newAppoint_month);
						} else {
							new_appointM = String.valueOf(newAppoint_month);
						}

						if (newAppoint_day < 10) {
							new_appointD = "0" + String.valueOf(newAppoint_day);
						} else {
							new_appointD = String.valueOf(newAppoint_day);
						}
						old_appointDate = item.getAppointDate();
						new_appointDate = String.valueOf(newAppoint_year) + new_appointM + new_appointD;
						item.setAppointDate(new_appointDate);
						if (startAppointMent(item)) {
							values.put("appoint_start", 1);
							values.put("appoint_date", new_appointDate);
							db.updateAppointMentItem(values, item.appoint_time, old_appointDate,item.getDeviceId(),item.getGateWayId(),userId);
						}
					} else {
						if (startAppointMent(item)) {
							Log.d(TAG,"start appoint success");
						}
					}
				}
			} else {
				if (item.getAppointStart() ==1) {
					if (isTimePassed(item)) {
						stopAppointMent(item);
						values.put("appoint_start", 0);
						db.updateAppointMentItem(values, item.appoint_time, item.appoint_date,item.getDeviceId(),item.getGateWayId(),userId);
					} else {
						if (startAppointMent(item)) {
							Log.d(TAG,"start appoint success");
						}
					}
				}
			}
		}
	}
}
