package com.hipad.smarthome;

import java.util.ArrayList;
import java.util.Calendar;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.user.User;
import com.hipad.smarthome.moudle.AppointItem;
import com.hipad.smarthome.receiver.AlarmReceiver;
import com.hipad.smarthome.utils.AppointMentDataBase;
import com.hipad.smarthome.utils.AppointMentPopWindow;
import com.hipad.smarthome.utils.AppointMentPopWindow.AppointMentListener;
import com.hipad.smarthome.utils.DatabaseHelper;
import com.hipad.smarthome.utils.SlideSwitch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class AppointMentActivity extends BaseActivity implements AppointMentListener{

	private Context context;
	private ListView appointList;
	private AppointMentAdapter appointAdapter;
	private CommonDevice device;
	private boolean isMoreClicked = false;
	
	private ImageView mLeftBtn;
	private ImageView mAddBtn;
	private ImageView mMoreBtn;
	public static final int MSG_APPOINT_SET = 0x0001;
	public static final int MSG_APPOINT_SET_RES = 0x0002;
	private String deviceId,gatewayId;
	private AlarmManager mAlarmManager;
	String userId = null;
	private static final String TAG = "AppointMentActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appoint_ment);
		context = this;
		Bundle bundle = getIntent().getExtras();
		device = (CommonDevice) bundle.get(CommonDevice.EXTRA_DEVICE);
		deviceId = device.getDeviceId();
		gatewayId = device.getGateway();
		if (gatewayId.equals("") || gatewayId == null) {
			gatewayId="0000000000000000";
		}
		User mUser = MyApplication.user;
		if (mUser != null) {
			userId = mUser.getUserId();
		}
		if (userId == null) {
			return;
		}
		Log.d(TAG,"AppointMentActivity===============>userId:" + userId);
		initView();
	}

	private void initView() {
		appointList = (ListView) findViewById(R.id.list);
		appointAdapter = new AppointMentAdapter(this);
		onAppointMentChange(false);
		mLeftBtn = (ImageView) findViewById(R.id.title_left_icon);
		mAddBtn = (ImageView) findViewById(R.id.appoint_add_icon);
		mMoreBtn = (ImageView) findViewById(R.id.appoint_more_icon);
		
		mLeftBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if (isMoreClicked) {
					isMoreClicked = !isMoreClicked;
					appointAdapter.setMoreClick(isMoreClicked);
					onAppointMentChange(false);
				}
				finish();
			}
		});
		
		mAddBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				if (isMoreClicked) {
					isMoreClicked = !isMoreClicked;
					appointAdapter.setMoreClick(isMoreClicked);
					onAppointMentChange(false);
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putParcelable(CommonDevice.EXTRA_DEVICE,device);
				intent.putExtras(bundle);
				intent.putExtra("is_edit", false);
				intent.putExtra("user_id", userId);
				intent.setClass(AppointMentActivity.this, AppointMentSetActivity.class);
				startActivityForResult(intent, MSG_APPOINT_SET);
			}
		});
		
		mMoreBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				isMoreClicked = !isMoreClicked;
				appointAdapter.setMoreClick(isMoreClicked);
				onAppointMentChange(false);
			}
		});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MSG_APPOINT_SET:
			if (MSG_APPOINT_SET_RES == resultCode) {
				onAppointMentChange(true);
			}
			break;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		IntentFilter intentFilter = new IntentFilter("com.android.updatelistview");
		registerReceiver(MyBroadCastReceiver, intentFilter);
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		//restartAppointMent();
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(MyBroadCastReceiver);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
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
			Intent intent = new Intent(getApplicationContext(),AlarmReceiver.class);
			intent.putExtra("gateway_id", itemInfo.gatewayID);
			intent.putExtra("device_id", itemInfo.deviceID);
			intent.putExtra("warm_tmp", itemInfo.warm_tmp);
			intent.putExtra("warm_time",itemInfo.warm_time);
			intent.putExtra("appoint_date",itemInfo.appoint_date);
			intent.putExtra("appoint_time",itemInfo.appoint_time);
			intent.putExtra("appoint_name", itemInfo.appoint_name);
			PendingIntent operation = PendingIntent.getBroadcast(this,Integer.parseInt(itemInfo.appoint_date) + (Integer.parseInt(itemInfo.appoint_time)*10000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), operation);
			return true;
		} else {
			return false;
		}
	}
	
	private void stopAppointMent(AppointMentItem itemInfo) {
		Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context,Integer.parseInt(itemInfo.appoint_date) + (Integer.parseInt(itemInfo.appoint_time)*10000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
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
	
	private void restartAppointMent() {
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		final AppointMentDataBase db = AppointMentDataBase.getInstance(context);
		ArrayList<AppointMentItem> itemList = db.getAppointList(MyApplication.user.getUserId());
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
							Log.d(TAG,"start appoint " + item.getAppointName()+ " success");
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
							Log.d(TAG,"start appoint " + item.getAppointName() + " success");
						}
					}
				}
			}	
		}
		onAppointMentChange(false);
	}
	
	public int getGapCount(int s_year,int s_month,int s_day, int e_year,int e_month,int e_day) {
        Calendar fromCalendar = Calendar.getInstance();  
        fromCalendar.set(s_year, s_month, s_day);  
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        fromCalendar.set(Calendar.MINUTE, 0);  
        fromCalendar.set(Calendar.SECOND, 0);  
        fromCalendar.set(Calendar.MILLISECOND, 0);  
  
        Calendar toCalendar = Calendar.getInstance();  
        toCalendar.set(e_year, e_month, e_day);  
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        toCalendar.set(Calendar.MINUTE, 0);  
        toCalendar.set(Calendar.SECOND, 0);  
        toCalendar.set(Calendar.MILLISECOND, 0);  
  
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
	}

	private BroadcastReceiver MyBroadCastReceiver =new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			onAppointMentChange(false);
		}
		
	};
	
	public void onAppointMentChange(boolean isSuccess){
		if(isSuccess){
			showToastShort("Ô¤Ô¼³É¹¦£¡");
		}
		AppointMentDataBase db = AppointMentDataBase.getInstance(context);
		if (userId == null) {
			if (MyApplication.user != null) {
				userId = MyApplication.user.getUserId();
			} else {
				return;
			}
		}
		ArrayList<AppointMentItem> itemList = db.getAppointList(deviceId,gatewayId,userId);
		appointAdapter.setListData(itemList,userId);
		appointList.setAdapter(appointAdapter);
		appointAdapter.notifyDataSetChanged();
	}
}
