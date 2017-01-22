package com.hipad.smarthome;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smarthome.R;
import com.hipad.smarthome.moudle.AppointItem;
import com.hipad.smarthome.receiver.AlarmReceiver;
import com.hipad.smarthome.utils.AppointSlideSwitch;
import com.hipad.smarthome.utils.AppointSlideSwitch.OnAppointChangedListener;
import com.hipad.smarthome.utils.AppointMentDataBase;
import com.hipad.smarthome.utils.AppointMentPopWindow;
import com.hipad.smarthome.utils.DatabaseHelper;
import com.hipad.smarthome.utils.SlideSwitch;
import com.hipad.smarthome.utils.SlideSwitch.OnChangedListener;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppointMentAdapter extends BaseAdapter {

	private AppointMentActivity context;
	private LayoutInflater listContainer;
	public ArrayList<AppointMentItem> listItems = new ArrayList<AppointMentItem>();
	public String userId = null;
	float dx, ux;
	private AlarmManager mAlarmManager;
	public boolean isMoreClicked = false;
	private static final String TAG = "AppointMentAdapter";
	
	public AppointMentAdapter(Activity context) {
		this.context = (AppointMentActivity) context;
	}

	public void setListData(ArrayList<AppointMentItem> listItems,String user_id) {
		this.listItems = listItems;
		this.userId = user_id;
	}

	public void setMoreClick(boolean isClicked) {
		this.isMoreClicked = isClicked;
	}
	
	
	public int getCount() {
		// TODO Auto-generated method stub
		return this.listItems.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.listItems.get(arg0);
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View view, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		final AppointMentItem itemInfo = listItems.get(position);
		final AppointMentHolder listItem;
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (view == null) {
			view = listContainer.from(context).inflate(R.layout.appoint_list_item, null);
			listItem = new AppointMentHolder();
			listItem.event_title = (TextView) view.findViewById(R.id.event_name);
			listItem.event_time = (TextView) view.findViewById(R.id.event_time);
			listItem.event_btn = (ImageButton) view.findViewById(R.id.event_switchBtn);
			listItem.event_time_diff = (TextView) view.findViewById(R.id.event_time_diff);
			listItem.event_status = (TextView) view.findViewById(R.id.event_status);
			listItem.event_delBtn = (ImageButton) view.findViewById(R.id.event_delBtn);
			view.setTag(listItem);
		} else {
			listItem = (AppointMentHolder) view.getTag();
		}

		if (itemInfo != null) {
			if (isMoreClicked) {
				listItem.event_delBtn.setVisibility(View.VISIBLE);
				listItem.event_btn.setVisibility(View.GONE);
			} else {
				listItem.event_delBtn.setVisibility(View.GONE);
				listItem.event_btn.setVisibility(View.VISIBLE);
			}
			final AppointMentDataBase db = AppointMentDataBase.getInstance(context);
			AppointMentItem item = new AppointMentItem();
			ContentValues values = new ContentValues();
			listItem.event_title.setText(itemInfo.getAppointName());
			String time = itemInfo.getAppointTime();
			String date = itemInfo.getAppointDate();
			int appoint_year = Integer.parseInt(date.substring(0, 4));
			int appoint_month = Integer.parseInt(date.substring(4, 6));
			int appoint_day = Integer.parseInt(date.substring(6, 8));
			
			int hour = Integer.parseInt(time) / 60;
			int minute = Integer.parseInt(time) % 60;
			String hour_str = String.valueOf(hour);
			String minute_str = String.valueOf(minute);
			String year_str = String.valueOf(appoint_year);
			String month_str = String.valueOf(appoint_month);
			String day_str = String.valueOf(appoint_day);
			if (appoint_month < 10) {
				month_str = "0" + month_str;
			}
			
			if (appoint_day < 10) {
				day_str = "0" + day_str;
			}
			
			if (hour < 10) {
				hour_str = "0" + hour_str;
			}
			
			if (minute < 10) {
				minute_str = "0" + minute_str;
			}
			
//			if (hour < 12) {
//				if (hour >= 10) {
//					hour_str = String.valueOf(hour);
//				} else {
//					hour_str = "0" + String.valueOf(hour);
//				}
//				listItem.event_time_diff.setText(context.getString(R.string.appointment_time_morn));
//			} else if (hour == 12){
//				hour_str = String.valueOf(hour);
//				listItem.event_time_diff.setText(context.getString(R.string.appointment_time_noon));
//			} else if (hour > 12) {
//				if ((hour - 12) >= 10) {
//					hour_str = String.valueOf(hour - 12);
//				} else {
//					hour_str = "0" + String.valueOf(hour - 12);
//				}
//				listItem.event_time_diff.setText(context.getString(R.string.appointment_time_aft));
//			}
			
			listItem.event_time.setText(hour_str + ":" + minute_str);
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, appoint_year);
			c.set(Calendar.MONTH, appoint_month-1);
			c.set(Calendar.DAY_OF_MONTH, appoint_day);
			
			item = db.getAppointMentItem(itemInfo.getAppointTime(), itemInfo.getAppointDate(),itemInfo.getDeviceId(),itemInfo.getGateWayId(),userId);
			Log.d(TAG,"AppointTime:" + itemInfo.getAppointTime() + "AppointDate:" + itemInfo.getAppointDate() + "DeviceId:" + itemInfo.getDeviceId() + "GateWayId:" + itemInfo.getGateWayId() + "UserId:" + userId);
			if (item != null) {
				if (item.getAppointStart() == 1) {
					if (item.getIsEveryDay() == 1){
						listItem.event_status.setText(context.getString(R.string.appointment_status_start_everyday));
						listItem.event_btn.setBackgroundResource(R.drawable.switch_on_blue_bg);
					} else {
						if (isTimePassed(item)) {
							values.put("appoint_start", 0);
							db.updateAppointMentItem(values, item.getAppointTime(), item.getAppointDate(),item.getDeviceId(),item.getGateWayId(),userId);
							listItem.event_status.setText(context.getString(R.string.appointment_status_shut));
							listItem.event_btn.setBackgroundResource(R.drawable.switch_off_gray_bg);
						} else {
							listItem.event_status.setText(context.getString(R.string.appointment_status_start_single));
							listItem.event_btn.setBackgroundResource(R.drawable.switch_on_blue_bg);
						}
					}
				} else {
					listItem.event_status.setText(context.getString(R.string.appointment_status_shut));
					listItem.event_btn.setBackgroundResource(R.drawable.switch_off_gray_bg);
				}
				
				
				listItem.event_delBtn.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View view) {
						// TODO Auto-generated method stub
						final AlertDialog mDialog = new AlertDialog.Builder(context).create();
						mDialog.setCanceledOnTouchOutside(false);
						mDialog.show();
						mDialog.getWindow().setContentView(R.layout.appoint_delete_layout);
						mDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View view) {
								// TODO Auto-generated method stub
								mDialog.dismiss();
							}
						});
						mDialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View view) {
								// TODO Auto-generated method stub
								AppointMentDataBase db = AppointMentDataBase.getInstance(context);
								db.deleteAppointMentItem(itemInfo.getAppointTime(),itemInfo.getAppointDate(),itemInfo.getDeviceId(),itemInfo.getGateWayId(),userId);
								listItems.remove(itemInfo);
								notifyDataSetChanged();
								Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
								PendingIntent sender = PendingIntent.getBroadcast(context,Integer.parseInt(itemInfo.getAppointDate()) + (Integer.parseInt(itemInfo.getAppointTime())*10000),intent, PendingIntent.FLAG_UPDATE_CURRENT);
								AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
								am.cancel(sender);
								mDialog.dismiss();
							}
						});
	
					}
				});
				
				listItem.event_title.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View view) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putParcelable("itemInfo", itemInfo);
						intent.putExtras(bundle);
						intent.putExtra("is_edit", true);
						intent.putExtra("user_id", userId);
						intent.setClass(context, AppointMentSetActivity.class);
						context.startActivityForResult(intent,AppointMentActivity.MSG_APPOINT_SET);
					}
				});
				
				listItem.event_btn.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View view) {
						// TODO Auto-generated method stub
						ContentValues values = new ContentValues();
						if (itemInfo.getIsEveryDay() == 1) {
							if (itemInfo.getAppointStart() == 1) {
								itemInfo.setAppointStart(0);
								stopAppointMent(itemInfo);
								listItem.event_status.setText(context.getString(R.string.appointment_status_shut));
								listItem.event_btn.setBackgroundResource(R.drawable.switch_off_gray_bg);
								values.put("appoint_start", 0);
								db.updateAppointMentItem(values, itemInfo.appoint_time, itemInfo.appoint_date,itemInfo.getDeviceId(),itemInfo.getGateWayId(),userId);
							} else {
								Calendar c = Calendar.getInstance();
								if (isTimePassed(itemInfo)) {
									c.setTimeInMillis(System.currentTimeMillis());
									c.add(Calendar.HOUR_OF_DAY,24);
									int newAppoint_year = c.get(Calendar.YEAR);
									int newAppoint_month = c.get(Calendar.MONTH) + 1;
									int newAppoint_day = c.get(Calendar.DAY_OF_MONTH);
									String new_appointD,new_appointM,new_appointDate,old_appointDate;
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
									old_appointDate = itemInfo.getAppointDate();
									new_appointDate = String.valueOf(newAppoint_year) + new_appointM + new_appointD;
									itemInfo.setAppointDate(new_appointDate);
									if (startAppointMent(itemInfo)) {
										itemInfo.setAppointStart(1);
										values.put("appoint_start", 1);
										values.put("appoint_date", new_appointDate);
										db.updateAppointMentItem(values, itemInfo.appoint_time, old_appointDate,itemInfo.getDeviceId(),itemInfo.getGateWayId(),userId);
										listItem.event_btn.setBackgroundResource(R.drawable.switch_on_blue_bg);
										listItem.event_status.setText(context.getString(R.string.appointment_status_start_everyday));
									} else {
										listItem.event_status.setText(context.getString(R.string.appointment_status_shut));
										listItem.event_btn.setBackgroundResource(R.drawable.switch_off_gray_bg);
									}
									
								} else {
									if (startAppointMent(itemInfo)) {
										values.put("appoint_start", 1);
										db.updateAppointMentItem(values, itemInfo.appoint_time, itemInfo.appoint_date,itemInfo.getDeviceId(),itemInfo.getGateWayId(),userId);
										listItem.event_btn.setBackgroundResource(R.drawable.switch_on_blue_bg);
										listItem.event_status.setText(context.getString(R.string.appointment_status_start_everyday));
									} else {
										listItem.event_status.setText(context.getString(R.string.appointment_status_shut));
										listItem.event_btn.setBackgroundResource(R.drawable.switch_off_gray_bg);
									}
								}
							}
						} else {
							if (itemInfo.appoint_start == 1) {
								itemInfo.setAppointStart(0);
								stopAppointMent(itemInfo);
								listItem.event_status.setText(context.getString(R.string.appointment_status_shut));
								listItem.event_btn.setBackgroundResource(R.drawable.switch_off_gray_bg);
								values.put("appoint_start", 0);
								db.updateAppointMentItem(values, itemInfo.appoint_time, itemInfo.appoint_date,itemInfo.getDeviceId(),itemInfo.getGateWayId(),userId);
							} else {
								Calendar c = Calendar.getInstance();
								if (isTimePassed(itemInfo)) {
									c.setTimeInMillis(System.currentTimeMillis());
									c.add(Calendar.HOUR_OF_DAY,24);
									int newAppoint_year = c.get(Calendar.YEAR);
									int newAppoint_month = c.get(Calendar.MONTH) + 1;
									int newAppoint_day = c.get(Calendar.DAY_OF_MONTH);
									String new_appointD,new_appointM,new_appointDate,old_appointDate;
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
									old_appointDate = itemInfo.getAppointDate();
									new_appointDate = String.valueOf(newAppoint_year) + new_appointM + new_appointD;
									itemInfo.setAppointDate(new_appointDate);
									if (startAppointMent(itemInfo)) {
										itemInfo.setAppointStart(1);
										values.put("appoint_start", 1);
										values.put("appoint_date", new_appointDate);
										db.updateAppointMentItem(values, itemInfo.appoint_time, old_appointDate,itemInfo.getDeviceId(),itemInfo.getGateWayId(),userId);
										listItem.event_btn.setBackgroundResource(R.drawable.switch_on_blue_bg);
										listItem.event_status.setText(context.getString(R.string.appointment_status_start_single));
									} else {
										listItem.event_status.setText(context.getString(R.string.appointment_status_shut));
										listItem.event_btn.setBackgroundResource(R.drawable.switch_off_gray_bg);
									}
								} else {
									if (startAppointMent(itemInfo)) {
										values.put("appoint_start", 1);
										itemInfo.setAppointStart(1);
										db.updateAppointMentItem(values, itemInfo.appoint_time, itemInfo.appoint_date,itemInfo.getDeviceId(),itemInfo.getGateWayId(),userId);
										listItem.event_btn.setBackgroundResource(R.drawable.switch_on_blue_bg);
										listItem.event_status.setText(context.getString(R.string.appointment_status_start_single));
									} else {
										listItem.event_status.setText(context.getString(R.string.appointment_status_shut));
										listItem.event_btn.setBackgroundResource(R.drawable.switch_off_gray_bg);
									}
								}
							}
						}
						context.onAppointMentChange(false);
					}
				});
				
			}
			return view;
		} else {
			Log.d(TAG,"resfresh the view");
			context.onAppointMentChange(false);
			return null;
		}
	}

	private boolean isTimePassed(AppointMentItem itemInfo) {
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
		if (delayTime <= 0) {
			return true;
		}else {
			return false;
		}
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
			Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
			intent.putExtra("gateway_id", itemInfo.gatewayID);
			intent.putExtra("device_id", itemInfo.deviceID);
			intent.putExtra("warm_tmp", itemInfo.warm_tmp);
			intent.putExtra("warm_time",itemInfo.warm_time);
			intent.putExtra("appoint_date",itemInfo.appoint_date);
			intent.putExtra("appoint_time",itemInfo.appoint_time);
			intent.putExtra("appoint_name", itemInfo.appoint_name);
			PendingIntent operation = PendingIntent.getBroadcast(context,Integer.parseInt(itemInfo.appoint_date) + (Integer.parseInt(itemInfo.appoint_time)*10000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
	
	private class AppointMentHolder {
		private TextView event_title, event_time,event_time_diff,event_status;
		private ImageButton event_btn,event_delBtn;
	}
}
