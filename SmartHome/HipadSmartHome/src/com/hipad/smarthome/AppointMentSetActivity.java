package com.hipad.smarthome;

import java.util.Calendar;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smarthome.receiver.AlarmReceiver;
import com.hipad.smarthome.utils.AppointMentDataBase;
import com.hipad.smarthome.utils.AppointMentTimeSetPopWindow;
import com.hipad.smarthome.utils.AppointMentTimeSetPopWindow.TimeChooseListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class AppointMentSetActivity extends BaseActivity{
	
	private TextView nameText,timeText,contentTitle,contentName,appointModel;
//	private ImageView nameRightBtn,timeRightBtn,contentRightBtn,modelRightBtn;
	private ImageView leftBtn;
	private RelativeLayout title_view,content_view,model_view,time_view;
	private int myYear,myMonth,myDay,myHour,myMinute;
	public static final int MSG_CONTENT_ACTIVITY = 0x000001;
	public static final int MSG_MODE_ACTIVITY = 0x000002;
	public static final int MSG_CONTENT_ACTIVITY_RES = 0x000003;
	public static final int MSG_MODE_ACTIVITY_RES = 0x000004;
	private CommonDevice device;
	private byte[] menu = new byte[2];
	private boolean isEveryDay,isEdit = false;
	private AppointMentTimeSetPopWindow mTimeSetPop;
	private String userId = null;
	private int appoint_year,appoint_month,appoint_day,appoint_hour,appoint_minute;
	private String appoint_name,appoint_date,appoint_time,scene_name,getway_id,device_id;
	private int warm_time,warm_tmp,is_everyday,menu1,menu2,appoint_start,appoint_type,delay_time;
	private ImageView appoint_confirm;
	private AlarmManager mAlarmManager;
	AppointMentItem item = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appointment_set_layout);
		isEdit = getIntent().getBooleanExtra("is_edit", false);
		Bundle bundle = getIntent().getExtras();
		userId = getIntent().getStringExtra("user_id");
		if (isEdit) {
			item = (AppointMentItem) bundle.get("itemInfo");
			getway_id = item.getGateWayId();
			device_id = item.getDeviceId();
		} else {
			device = (CommonDevice) bundle.get(CommonDevice.EXTRA_DEVICE);
			getway_id = device.getGateway();
			device_id = device.getDeviceId();
		}
		if (getway_id.equals("") || getway_id == null) {
			getway_id = "0000000000000000";
		}
		mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		initView();
	}

	private void initView() {
		nameText =(TextView) findViewById(R.id.appointment_name);
		timeText= (TextView) findViewById(R.id.appointment_time);
		contentTitle = (TextView) findViewById(R.id.appointment_content_title);
		contentName = (TextView) findViewById(R.id.appointment_content);
		appointModel = (TextView) findViewById(R.id.appointment_model);
		appoint_confirm = (ImageView) findViewById(R.id.appoint_icon_confirm);
		
		leftBtn = (ImageView) findViewById(R.id.title_left_icon);
		appoint_confirm.setOnClickListener(new ConfirmListener());
		mTimeSetPop = new AppointMentTimeSetPopWindow(this);
		mTimeSetPop.setTimeChooseListener(new TimeSetListener());
		String hour = null,minute = null;
		if (isEdit) {
			nameText.setText(item.getAppointName());
			if (item.getAppointType() == 2) {
				contentTitle.setText(item.getSceneName());
				contentName.setVisibility(View.VISIBLE);
				contentName.setText(getString(R.string.appointment_content_boil_start) + item.getWarmTime() + getString(R.string.appointment_content_boil_end));
			} else if (item.getAppointType() == 1) {
				contentTitle.setText(getString(R.string.appointment_content_default));
				contentName.setVisibility(View.VISIBLE);
				contentName.setText(getString(R.string.appointment_content_boil_start) + item.getWarmTime() + getString(R.string.appointment_content_boil_end));
			} else {
				contentTitle.setText(getString(R.string.appointment_content_default));
				contentName.setVisibility(View.GONE);
			}
			if (item.getIsEveryDay() == 1) {
				appointModel.setText(getString(R.string.appointment_mode_everyday));
				isEveryDay = true;
				is_everyday = 1;
			} else {
				appointModel.setText(getString(R.string.appointment_mode_single));
				isEveryDay = false;
				is_everyday = 0;
			}
			appoint_hour = Integer.parseInt(item.getAppointTime())/60;
			appoint_minute = Integer.parseInt(item.getAppointTime())%60;
			if (appoint_hour < 10) {
				hour = "0" + String.valueOf(appoint_hour);
			} else {
				hour = String.valueOf(appoint_hour);
			}
			
			if (appoint_minute < 10) {
				minute = "0" + String.valueOf(appoint_minute);
			} else {
				minute = String.valueOf(appoint_minute);
			}
			timeText.setText(hour + ":" + minute);
			menu[0] = (byte)item.getMenu1();
			menu[1] = (byte)item.getMenu2();
			appoint_type = item.getAppointType();
			scene_name = item.getSceneName();
			warm_time = item.getWarmTime();
			warm_tmp = item.getWarmTmp();
		} else if (!isEdit) {
			if (nameText.getText().toString() == null || nameText.getText().toString().equals("")) {
				nameText.setText(getString(R.string.appointment_name_default));
			}
			
			if (contentTitle.getText().toString() == null || contentTitle.getText().toString().equals("")) {
				contentTitle.setText(getString(R.string.appointment_content_default));
				contentName.setVisibility(View.GONE);
			}
			
			if (timeText.getText().toString() == null || timeText.getText().toString().equals("")) {
				Calendar c=Calendar.getInstance();
				if (c.get(Calendar.HOUR_OF_DAY) < 10) {
					hour = "0" + c.get(Calendar.HOUR_OF_DAY);
				} else {
					hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
				}
				
				if (c.get(Calendar.MINUTE) < 10) {
					minute = "0" + c.get(Calendar.MINUTE);
				} else {
					minute = String.valueOf(c.get(Calendar.MINUTE));
				}
				appoint_hour = c.get(Calendar.HOUR_OF_DAY);
				appoint_minute = c.get(Calendar.MINUTE);
				timeText.setText(hour + ":" + minute);
			}
			
			if (appointModel.getText().toString() == null || appointModel.getText().toString().equals("")) {
				appointModel.setText(getString(R.string.appointment_mode_defalut));
				isEveryDay = true;
				is_everyday = 1;
			}
		}

		title_view = (RelativeLayout) findViewById(R.id.title_view);
		content_view = (RelativeLayout) findViewById(R.id.content_view);
		model_view = (RelativeLayout) findViewById(R.id.model_view);
		time_view = (RelativeLayout) findViewById(R.id.time_view);
		
//		nameRightBtn = (ImageView) findViewById(R.id.appoint_name_rightbtn);
//		timeRightBtn = (ImageView) findViewById(R.id.appoint_time_rightbtn);
//		contentRightBtn = (ImageView) findViewById(R.id.appoint_content_rightbtn);
//		modelRightBtn = (ImageView) findViewById(R.id.appoint_model_rightbtn);
		leftBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		title_view.setOnClickListener(new NameListener());
		content_view.setOnClickListener(new ContentListener());
		model_view.setOnClickListener(new ModelListener());
		time_view.setOnClickListener(new TimeListener());
	}
	
	private class ConfirmListener implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			AppointMentDataBase db = AppointMentDataBase.getInstance(AppointMentSetActivity.this);
			String month_str,day_str;
			Calendar c = Calendar.getInstance();
			appoint_year = c.get(Calendar.YEAR);
			appoint_month = c.get(Calendar.MONTH) + 1;
			appoint_day = c.get(Calendar.DAY_OF_MONTH);
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			delay_time = (appoint_hour - hour) * 60 * 60 + (appoint_minute - minute) * 60;
			appoint_name = nameText.getText().toString();
			
			if (appoint_month < 10) {
				month_str = "0" + String.valueOf(appoint_month);
			} else {
				month_str = String.valueOf(appoint_month);
			}
			
			if (appoint_day < 10) {
				day_str = "0" + String.valueOf(appoint_day);
			} else {
				day_str = String.valueOf(appoint_day);
			}
			
			appoint_date = String.valueOf(appoint_year) + month_str + day_str;
			appoint_time = String.valueOf(appoint_hour * 60 + appoint_minute);
			boolean isAppointExist = db.isAppointMentExist(appoint_time, appoint_date,device_id,getway_id,userId);
			if (!isEdit) {
				if (isAppointExist) {
					showToastShort(getString(R.string.appointment_time_repeat));
					return;
				}
			}
			ContentValues values = new ContentValues();
			if (delay_time < 0) {
				c.add(Calendar.HOUR_OF_DAY,24);
				int newAppoint_year = c.get(Calendar.YEAR);
				int newAppoint_month = c.get(Calendar.MONTH) + 1;
				int newAppoint_day = c.get(Calendar.DAY_OF_MONTH);
				String new_appointD,new_appointM;
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
				appoint_date = String.valueOf(newAppoint_year) + new_appointM + new_appointD;
				delay_time = 24*60*60 + delay_time;
			}

			values.put("gateway_id", getway_id);
			values.put("device_id", device_id);
			values.put("appoint_name", appoint_name);
			values.put("warm_time", warm_time);
			values.put("warm_tmp", warm_tmp);
			values.put("appoint_start", 1);
			values.put("appoint_type", appoint_type);
			values.put("is_everyday", is_everyday);
			values.put("menu1", (int)menu[0]);
			values.put("menu2", (int)menu[1]);
			values.put("user_id", userId);
			values.put("scene_name", scene_name);
			values.put("delay_time", delay_time);
			values.put("appoint_date", appoint_date);
			values.put("appoint_time", appoint_time);
			
			if (isEdit) {
				db.updateAppointMentItem(values, item.getAppointTime(),item.getAppointDate(),device_id,getway_id,userId);
				Intent stopIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
				PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(item.getAppointDate())+ (Integer.parseInt(item.getAppointTime())*10000),stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				mAlarmManager.cancel(sender);
				isEdit = false;
			} else {
				db.insertAppointmentItem(values);
			}

			Intent intent = new Intent(getApplicationContext(),AlarmReceiver.class);
			intent.putExtra("appoint_name", appoint_name);
			intent.putExtra("gateway_id", getway_id);
			intent.putExtra("device_id", device_id);
			intent.putExtra("warm_tmp", warm_tmp);
			intent.putExtra("warm_time",warm_time);
			intent.putExtra("appoint_date", appoint_date);
			intent.putExtra("appoint_time", appoint_time);
			c.setTimeInMillis(System.currentTimeMillis());
			c.add(Calendar.SECOND, delay_time);
			PendingIntent operation = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(appoint_date)+ (Integer.parseInt(appoint_time)*10000),intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), operation);
			setResult(AppointMentActivity.MSG_APPOINT_SET_RES,intent);		
			finish();
		}
		
	}
	
	private class ModelListener implements View.OnClickListener {

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("iseveryday", isEveryDay);
			intent.setClass(AppointMentSetActivity.this, AppointMentModelActivity.class);
			startActivityForResult(intent, MSG_MODE_ACTIVITY);
		}
		
	}
	
	private class ContentListener implements View.OnClickListener {

		public void onClick(View view) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(AppointMentSetActivity.this, AppointMentContentActivity.class);
			startActivityForResult(intent, MSG_CONTENT_ACTIVITY);
		}
		
	}
	

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode) {
		case MSG_CONTENT_ACTIVITY:
			if (MSG_CONTENT_ACTIVITY_RES == resultCode) {
				appoint_type = data.getIntExtra("appointtype", 0);
				menu = data.getByteArrayExtra("menu");
				scene_name = data.getStringExtra("scenename");
				warm_time = data.getIntExtra("warmTime", 0);
				warm_tmp = data.getIntExtra("warmTmp", 0);
				if (appoint_type == 2) {
					contentTitle.setText(scene_name);
					contentName.setVisibility(View.VISIBLE);
					contentName.setText(getString(R.string.appointment_content_boil_start) + warm_time + getString(R.string.appointment_content_boil_end));
				}
				
				if (appoint_type == 1) {
					contentTitle.setText(getString(R.string.appointment_content_default));
					contentName.setVisibility(View.VISIBLE);
					contentName.setText(getString(R.string.appointment_content_boil_start) + warm_time + getString(R.string.appointment_content_boil_end));
				}
				
				if (appoint_type == 0) {
					contentTitle.setText(getString(R.string.appointment_content_default));
					contentName.setVisibility(View.GONE);
				}
			}
			break;
		case MSG_MODE_ACTIVITY:
			if (MSG_MODE_ACTIVITY_RES == resultCode) {
				isEveryDay = data.getBooleanExtra("iseveryday", false);
				if (isEveryDay) {
					isEveryDay = true;
					is_everyday =1;
					appointModel.setText(getString(R.string.appointment_mode_everyday));
				} else {
					isEveryDay = false;
					is_everyday = 0;
					appointModel.setText(getString(R.string.appointment_mode_single));
				}
			}
			break;
		default:
			break;
		}
	}

	private class TimeSetListener implements TimeChooseListener {

		@Override
		public void onTimeChoose(int hour, int minute) {
			// TODO Auto-generated method stub
			appoint_hour = hour;
			appoint_minute = minute;
			String str_h = null,str_m = null;
			myHour  = hour;
			myMinute = minute;
	    	if (myHour < 10) {
	    		str_h = "0" + myHour;
	    	} else {
	    		str_h = String.valueOf(myHour);
	    	}
	    	
	    	if (myMinute < 10) {
	    		str_m = "0" + myMinute;
	    	} else {
	    		str_m = String.valueOf(myMinute);
	    	}
	    	timeText.setText(str_h + ":" + str_m);
		}
		
	}
	
	private class NameListener implements View.OnClickListener {

		@SuppressLint({ "NewApi", "ResourceAsColor" })
		public void onClick(View view) {
			// TODO Auto-generated method stub
			final AlertDialog mDialog = new AlertDialog.Builder(AppointMentSetActivity.this).create();
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
			mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); 
			mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			mDialog.getWindow().setContentView(R.layout.appoint_name_set);
			final EditText name = (EditText) mDialog.findViewById(R.id.edit_name_set);
			if (isEdit) {
				name.setText(item.getAppointName());
				name.setSelection(item.getAppointName().length());
			} 
			mDialog.getWindow().findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					mDialog.dismiss();
				}
			});
			mDialog.getWindow().findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					String str_name = name.getEditableText().toString();
					if (str_name == null || str_name.equals("")) {
						str_name = getString(R.string.appointment_name_default);
					}
					nameText.setText(str_name);
					appoint_name = name.getEditableText().toString();
					mDialog.dismiss();
				}
			});
		}
		
	}
	
	private DatePickerDialog.OnDateSetListener DatePickerListener = new DatePickerDialog.OnDateSetListener(){
		 
	    public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
	    	myYear = year;
	    	myMonth= monthOfYear + 1;
	    	myDay = dayOfMonth;
	    }
	};
	
	private class TimeListener implements View.OnClickListener{

		@SuppressLint("ResourceAsColor")
		public void onClick(View view) {
			// TODO Auto-generated method stub
//			final Calendar c = Calendar.getInstance();
//            TimePickerDialog picker = new TimePickerDialog(AppointMentSetActivity.this,R.style.MyDatePickerTheme,TimePickerListener,c.get(Calendar.HOUR),c.get(Calendar.MINUTE),true);
//            picker.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.appointment_confirm), new DialogInterface.OnClickListener() {
//				
//				public void onClick(DialogInterface view, int which) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//            picker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.appointment_cancel), new DialogInterface.OnClickListener() {
//				
//				public void onClick(DialogInterface view, int which) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//            picker.show();
//			Context context = picker.getContext();  
//		    int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);  
//			View divider = picker.findViewById(divierId);  
//			divider.setBackgroundColor(R.color.white_gray);
			mTimeSetPop.showPopupWindow();

		}
		
	}
	
	private TimePickerDialog.OnTimeSetListener TimePickerListener = new TimePickerDialog.OnTimeSetListener(){
		 
		public void onTimeSet(TimePicker view, int hour, int minute) {
			// TODO Auto-generated method stub
			String str_h = null,str_m = null;
			myHour  = hour;
			myMinute = minute;
	    	if (myHour < 10) {
	    		str_h = "0" + myHour;
	    	} else {
	    		str_h = String.valueOf(myHour);
	    	}
	    	
	    	if (myMinute < 10) {
	    		str_m = "0" + myMinute;
	    	} else {
	    		str_m = String.valueOf(myMinute);
	    	}
	    	timeText.setText(str_h + ":" + str_m);
		}
	};
}
