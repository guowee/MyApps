package com.hipad.smarthome.receiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.hipad.smarthome.MyApplication;
import com.hipad.smarthome.R;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

public class DrinkAlarmReceiver extends BroadcastReceiver {
	private final static String TAG = "DrinkAlarmReceiver";	

	public final static String ACTION_DRINK_ALARM = "com.hipad.smarthome.action.DRINK_ALARM";
	public final static String EXTRA_CHECK_DRINK_ALARM = "check_drink_alarm";

	private Context mContext;
	private AlarmManager mAlarManager;
	
	private Calendar c = Calendar.getInstance();
	private int year = c.get(Calendar.YEAR);
	private int month = c.get(Calendar.MONTH) + 1;
	private int day = c.get(Calendar.DAY_OF_MONTH);		
	private int hour = c.get(Calendar.HOUR_OF_DAY);
	private int minute = c.get(Calendar.MINUTE);
	private int notfiy_id = year + month + day + hour + minute;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext=context;
		mAlarManager=(AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		
		if(intent.getBooleanExtra(EXTRA_CHECK_DRINK_ALARM, false)){
			Log.d(TAG, "check_drink_alarm");
			checkDrinkAlarm();
			return;
		}	
		
		SharedPreferences preferences=mContext.getSharedPreferences("waterAlarm", Context.MODE_PRIVATE);
		long boilTime=preferences.getLong("boil_time", 0);//水壶煮水的时刻（计时起点）
		
		float time=preferences.getFloat("time", 0);//设置闹铃的时间间隔
		long timeMillis=(long) (time*60*60*1000);
		
		long past_time=System.currentTimeMillis()-boilTime;//当前已经过去的时间
		
		Intent it=new Intent(ACTION_DRINK_ALARM);
		PendingIntent operation=PendingIntent.getBroadcast(mContext, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
		
		if(past_time>=timeMillis){
			// alarm the user to drink
			if(isInValidPeroid()) showSuccessNotify(notfiy_id,mContext.getString(R.string.drink_alarm));
			
			mAlarManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+timeMillis , operation);
		}else{
			long left_time=(long) (timeMillis-past_time);
			mAlarManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+left_time , operation);
		}
	}
	
	// 判断当前时间是否在指定的时间段内
	private boolean isInValidPeroid() {
		try {
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");// 设置日期格式
			// 定义区间值06:00:00--23:59:59
			Date startDate = df.parse("05:59:59");
			Date endDate = df.parse("23:59:59");

			Date curTime = df.parse(df.format(new Date()));
			if (curTime.after(startDate) && curTime.before(endDate)) {
				// 在指定时间段内
				return true;
			} else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void showSuccessNotify(int notfiy_id,String msg) {
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification noti = new Notification(R.drawable.logo_dl, "用水提醒", System.currentTimeMillis());
		noti.defaults=Notification.DEFAULT_SOUND;
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),R.layout.notify_layout);  
        remoteView.setImageViewResource(R.id.image, R.drawable.logo_dl);  
        remoteView.setTextViewText(R.id.title , "智能家居");  
        remoteView.setTextViewText(R.id.content , msg);  
        noti.contentView = remoteView;  
        Intent operation = new Intent(mContext, mContext.getClass());
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, R.string.app_name, operation, PendingIntent.FLAG_UPDATE_CURRENT);  
        noti.contentIntent = contentIntent;  
		notificationManager.notify(notfiy_id, noti);
	}
	
	public static void stopDrinkAlarm(Context context){
		AlarmManager alarManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent=new Intent(ACTION_DRINK_ALARM);
		PendingIntent pendIntent=PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarManager.cancel(pendIntent);
	}
	
	private void checkDrinkAlarm(){
		SharedPreferences preferences = mContext.getSharedPreferences("waterAlarm",Context.MODE_PRIVATE);
		
		String userId = preferences.getString("user", "unknown");
		boolean hasDrinkAlarm = preferences.getBoolean("flag", false);
		if(!hasDrinkAlarm || !MyApplication.user.getUserId().equals(userId)) return ;
		
		Intent intent=new Intent(ACTION_DRINK_ALARM);
		PendingIntent pendIntent=PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mAlarManager.cancel(pendIntent);
		
		float time = preferences.getFloat("time", -1);
		if(time < 0) return ;
		
		long triggerAtMillis = (long) (time*60*60*1000);
		mAlarManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + triggerAtMillis, pendIntent);
	}
}
