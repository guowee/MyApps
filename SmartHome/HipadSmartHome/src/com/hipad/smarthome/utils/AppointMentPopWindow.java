package com.hipad.smarthome.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hipad.smarthome.AppointMentActivity;
import com.hipad.smarthome.R;
import com.hipad.smarthome.receiver.AlarmReceiver;
import com.hipad.smarthome.utils.AppointMentTimeSetPopWindow.TimeChooseListener;
import com.hipad.smarthome.utils.TargetTemperatureChooser.TempChosenListener;
import com.hipad.smarthome.utils.TimeSetPopWindow.TimeChoseListener;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AppointMentPopWindow extends PopupWindow {
	private AppointMentActivity activity;
	private View popView;
	private EditText edit_name, edit_tmp, edit_warm_peroid, edit_time,
			edit_date;
	private TargetTemperatureChooser mTmpch;
	private TimeSetPopWindow mTimeSetPopWindow;
	private AppointMentTimeSetPopWindow mAppointMentTimeSetPopWindow;
	private Button cancel, confirm;

	private String tmp_str = null, warm_time_str, appointment_time_str,
			event_title_str, edit_date_str;

	private String appointment_time, appointment_date;
	private int appointment_hour = 0, appointment_minute = 0,
			appointment_year = 0, appointment_month = 0, appointment_day = 0;
	private String deviceId, gateWayId;
	private AlarmManager mAlarmManager;

	private String edit_appoint_time = null;
	private String edit_appoint_event_title = null;
	private String edit_appoint_temp_num = null;
	private String edit_appoint_keep_time = null;
	private String edit_appoint_date = null;

	private static final int DATA_PICKER_ID = 1;

	private boolean isEdit = false;
	private int screen_width = 0;
	private int screen_height = 0;

	private int textLength = 0;
	static String regEx = "[\u4e00-\u9fa5]";
	static Pattern pat = Pattern.compile(regEx);

	public static boolean isContainsChinese(String str) {
		Matcher matcher = pat.matcher(str);
		boolean flg = false;
		if (matcher.find()) {
			flg = true;
		}
		return flg;
	}

	private boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	public AppointMentPopWindow(final Activity context, final String devId,
			final String gatewayId) {
		this.deviceId = devId;
		this.gateWayId = gatewayId;
		this.activity = (AppointMentActivity) context;
		mAlarmManager = (AlarmManager) activity
				.getSystemService(Context.ALARM_SERVICE);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popView = inflater.inflate(R.layout.appointment_layout, null);
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		screen_width = wm.getDefaultDisplay().getWidth();
		screen_height = wm.getDefaultDisplay().getHeight();
		this.setContentView(popView);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.setWidth(screen_width);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		ColorDrawable cdw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(cdw);
		initView();
	}

	public AppointMentPopWindow(final Activity context, final String devId,
			final String gatewayId, final String edit_appoint_time,
			final String edit_appoint_event_title,
			final String edit_appoint_temp_num,
			final String edit_appoint_keep_time, final String edit_appoint_date) {
		this.deviceId = devId;
		this.gateWayId = gatewayId;
		this.activity = (AppointMentActivity) context;
		this.edit_appoint_time = edit_appoint_time;
		this.edit_appoint_event_title = edit_appoint_event_title;
		this.edit_appoint_temp_num = edit_appoint_temp_num;
		this.edit_appoint_keep_time = edit_appoint_keep_time;
		this.edit_appoint_date = edit_appoint_date;
		isEdit = true;
		mAlarmManager = (AlarmManager) activity
				.getSystemService(Context.ALARM_SERVICE);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popView = inflater.inflate(R.layout.appointment_layout, null);
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		screen_width = wm.getDefaultDisplay().getWidth();
		screen_height = wm.getDefaultDisplay().getHeight();
		this.setContentView(popView);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.setWidth(screen_width);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		ColorDrawable cdw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(cdw);
		initView();
	}

	private void initView() {
		mTmpch = new TargetTemperatureChooser(activity);
		mTimeSetPopWindow = new TimeSetPopWindow(activity);
		mAppointMentTimeSetPopWindow = new AppointMentTimeSetPopWindow(activity);

		cancel = (Button) popView.findViewById(R.id.btn_cancel);
		confirm = (Button) popView.findViewById(R.id.btn_confirm);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AppointMentDataBase db = AppointMentDataBase
						.getInstance(activity);
				if (isEdit) {
					tmp_str = edit_tmp.getText().toString();
					warm_time_str = edit_warm_peroid.getText().toString();
					appointment_time_str = edit_time.getText().toString();

					String edit_hour = appointment_time_str.substring(0,
							appointment_time_str.lastIndexOf(':'));
					String edit_minute = appointment_time_str.substring(
							appointment_time_str.lastIndexOf(':') + 1,
							appointment_time_str.length());
					appointment_hour = Integer.parseInt(edit_hour);
					appointment_minute = Integer.parseInt(edit_minute);
					appointment_time = String.valueOf(appointment_hour * 60
							+ appointment_minute);

					edit_date_str = edit_date.getText().toString();
					String edit_year = edit_date_str.substring(0, 4);
					String edit_month = edit_date_str.substring(5, 7);
					String edit_day = edit_date_str.substring(8, 10);
					appointment_year = Integer.parseInt(edit_year);
					appointment_month = Integer.parseInt(edit_month);
					appointment_day = Integer.parseInt(edit_day);
					String month_str = String.valueOf(appointment_month);
					String day_str = String.valueOf(appointment_day);
					if (appointment_month < 10) {
						month_str = "0" + month_str;
					}
					if (appointment_day < 10) {
						day_str = "0" + day_str;
					}
					appointment_date = String.valueOf(appointment_year)
							+ month_str + day_str;
				}

				event_title_str = edit_name.getText().toString();
				if (event_title_str == null || event_title_str.equals("")) {
					activity.showToastShort(activity.getResources().getString(
							R.string.appointment_name_null));
					return;
				}

				if (edit_date_str == null || edit_date_str.equals("")) {
					activity.showToastShort(activity.getResources().getString(
							R.string.appointment_appoint_date_null));
					return;
				}

				if (tmp_str == null) {
					activity.showToastShort(activity.getResources().getString(
							R.string.appointment_temp_null));
					return;
				}

				if (warm_time_str == null) {
					activity.showToastShort(activity.getResources().getString(
							R.string.appointment_warm_time_null));
					return;
				}

				if ((appointment_time == null)
						|| (appointment_time_str == null)) {
					activity.showToastShort(activity.getResources().getString(
							R.string.appointment_appoint_time_null));
					return;
				}

				boolean isAppointExist = db.isAppointMentExist(appointment_time, appointment_date,deviceId,gateWayId,"");
				if (!isEdit) {
					if (isAppointExist) {
						activity.showToastShort(activity.getResources()
								.getString(R.string.appointment_time_repeat));
						return;
					}
				}
				Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH) + 1;
				int day = c.get(Calendar.DAY_OF_MONTH);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);

				if (appointment_hour == 0) {
					appointment_hour = 24;
				}
				int delay_second = (appointment_hour - hour) * 60 * 60
						+ (appointment_minute - minute) * 60;
				c.setTimeInMillis(System.currentTimeMillis());
				c.add(Calendar.YEAR, appointment_year - year);
				c.add(Calendar.MONTH, appointment_month - month);
				c.add(Calendar.DAY_OF_MONTH, appointment_day - day);
				c.add(Calendar.SECOND, delay_second);
				long delayTime = c.getTimeInMillis()
						- System.currentTimeMillis();
				if (delayTime > 0) {
					ContentValues values = new ContentValues();
					if (gateWayId.equals("") || gateWayId == null) {
						gateWayId = "0000000000000000";
					}
					values.put("gateway_id", gateWayId);
					values.put("device_id", deviceId);
					values.put("event_title", event_title_str);
					values.put("temp_num", tmp_str);
					values.put("keep_time", warm_time_str);
					values.put("delay_time", delayTime);
					values.put("appoint_date", appointment_date);
					values.put("appoint_time", appointment_time);
					values.put("appoint_start", 1);

					if (isEdit) {
						db.updateAppointMentItem(values, edit_appoint_time,edit_appoint_date,deviceId,gateWayId,"");
						Intent stopIntent = new Intent(activity
								.getApplicationContext(), AlarmReceiver.class);
						PendingIntent sender = PendingIntent.getBroadcast(
								activity, Integer.parseInt(edit_appoint_date)
										+ Integer.parseInt(edit_appoint_time)*10000,
								stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						mAlarmManager.cancel(sender);
						isEdit = false;
					} else {
						db.insertAppointmentItem(values);
					}

					Intent intent = new Intent(
							activity.getApplicationContext(),
							AlarmReceiver.class);
					intent.putExtra("gateway_id", gateWayId);
					intent.putExtra("device_id", deviceId);
					intent.putExtra("keep_warm", Integer.parseInt(tmp_str));
					intent.putExtra("keep_time",
							Integer.parseInt(warm_time_str));
					PendingIntent operation = PendingIntent.getBroadcast(
							activity, Integer.parseInt(appointment_date)
									+ Integer.parseInt(appointment_time)*10000,
							intent, PendingIntent.FLAG_UPDATE_CURRENT);
					;
					mAlarmManager.set(AlarmManager.RTC_WAKEUP,
							c.getTimeInMillis(), operation);
					dismiss();
					appointListener.onAppointMentChange(true);
				} else {
					activity.showToastShort(activity.getResources().getString(
							R.string.appointment_time_error));
					return;
				}
			}
		});

		edit_tmp = (EditText) popView.findViewById(R.id.edit_tmp);
		edit_warm_peroid = (EditText) popView.findViewById(R.id.edit_warm_time);
		edit_time = (EditText) popView.findViewById(R.id.edit_appoint_time);
		edit_name = (EditText) popView.findViewById(R.id.edit_name);
		edit_date = (EditText) popView.findViewById(R.id.edit_appoint_date);

		edit_date.setInputType(InputType.TYPE_NULL);
		edit_tmp.setInputType(InputType.TYPE_NULL);
		edit_warm_peroid.setInputType(InputType.TYPE_NULL);
		edit_time.setInputType(InputType.TYPE_NULL);

		if (isEdit) {
			edit_name.setText(edit_appoint_event_title);
			edit_name.setSelection(edit_appoint_event_title.length());
			edit_tmp.setText(edit_appoint_temp_num);
			edit_warm_peroid.setText(edit_appoint_keep_time);
			int appoint_hour = Integer.parseInt(edit_appoint_time) / 60;
			int appoint_minute = Integer.parseInt(edit_appoint_time) % 60;
			edit_time.setText(appoint_hour + ":" + appoint_minute);

			int appoint_year = Integer.parseInt(edit_appoint_date.substring(0,
					4));
			int appoint_month = Integer.parseInt(edit_appoint_date.substring(4,
					6));
			int appoint_day = Integer.parseInt(edit_appoint_date
					.substring(6, 8));
			String month_str = String.valueOf(appoint_month);
			String day_str = String.valueOf(appoint_day);
			if (appoint_month < 10) {
				month_str = "0" + month_str;
			}
			if (appoint_day < 10) {
				day_str = "0" + day_str;
			}
			edit_date.setText(String.valueOf(appoint_year) + "-" + month_str
					+ "-" + day_str);
		}

		edit_date.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focus) {
				// TODO Auto-generated method stub
				if (focus) {
					showDatePickerDialog();
				}
			}
		});

		edit_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDatePickerDialog();
			}
		});

		edit_name.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focus) {
				// TODO Auto-generated method stub
				if (!focus) {
					InputMethodManager imm = (InputMethodManager) activity
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(edit_name.getWindowToken(), 0);
				}
			}
		});

		edit_tmp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mTmpch.show();
				mTmpch.setTempChoseListener(new TempChosenListener() {

					@Override
					public void onTempChosen(int temp) {
						tmp_str = String.valueOf(temp);
						edit_tmp.setText(tmp_str);
					}
				});
			}
		});

		edit_tmp.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focus) {
				if (focus) {
					mTmpch.show();
					mTmpch.setTempChoseListener(new TempChosenListener() {

						@Override
						public void onTempChosen(int temp) {
							tmp_str = String.valueOf(temp);
							edit_tmp.setText(tmp_str);
						}
					});
				}
			}
		});

		edit_warm_peroid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mTimeSetPopWindow.showPopupWindow();
				mTimeSetPopWindow.setTimeChoseListener(new TimeChoseListener() {

					@Override
					public void onTimeChoose(int minute) {
						warm_time_str = String.valueOf(minute);
						edit_warm_peroid.setText(warm_time_str);
					}
				});
			}
		});
		edit_warm_peroid.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focus) {
				if (focus) {
					mTimeSetPopWindow.showPopupWindow();
					mTimeSetPopWindow
							.setTimeChoseListener(new TimeChoseListener() {

								@Override
								public void onTimeChoose(int minute) {
									warm_time_str = String.valueOf(minute);
									edit_warm_peroid.setText(warm_time_str);
								}
							});
				}
			}
		});

		edit_time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mAppointMentTimeSetPopWindow.showPopupWindow();
				mAppointMentTimeSetPopWindow
						.setTimeChooseListener(new TimeChooseListener() {

							@Override
							public void onTimeChoose(int hour, int minute) {
								appointment_hour = hour;
								appointment_minute = minute;
								String hour_str = String.valueOf(hour);
								String minute_str = String.valueOf(minute);
								if (hour < 10) {
									hour_str = "0" + hour_str;
								}
								if (minute < 10) {
									minute_str = "0" + minute_str;
								}
								appointment_time_str = hour_str + ":"
										+ minute_str;
								edit_time.setText(appointment_time_str);
							}
						});
			}
		});
		edit_time.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean focus) {
				if (focus) {
					mAppointMentTimeSetPopWindow.showPopupWindow();
					mAppointMentTimeSetPopWindow
							.setTimeChooseListener(new TimeChooseListener() {

								@Override
								public void onTimeChoose(int hour, int minute) {
									appointment_hour = hour;
									appointment_minute = minute;
									String hour_str = String.valueOf(hour);
									String minute_str = String.valueOf(minute);
									if (hour < 10) {
										hour_str = "0" + hour_str;
									}
									if (minute < 10) {
										minute_str = "0" + minute_str;
									}
									appointment_time_str = hour_str + ":"
											+ minute_str;
									edit_time.setText(appointment_time_str);
									appointment_time = String.valueOf(hour * 60
											+ minute);
								}
							});
				}
			}
		});

	}

	private void showDatePickerDialog() {
		Calendar c = Calendar.getInstance();
		MyDatePickerDialog dialog = new MyDatePickerDialog(activity,
				R.style.MyDatePickerTheme,
				new MyDatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
						appointment_year = year;
						appointment_month = monthOfYear + 1;
						appointment_day = dayOfMonth;
						String month_str = String.valueOf(monthOfYear + 1);
						String day_str = String.valueOf(dayOfMonth);
						if (monthOfYear < 10) {
							month_str = "0" + month_str;
						}
						if (dayOfMonth < 10) {
							day_str = "0" + day_str;
						}
						appointment_date = String.valueOf(year) + month_str
								+ day_str;
						edit_date_str = String.valueOf(year) + "-" + month_str
								+ "-" + day_str;
						edit_date.setText(edit_date_str);
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH));
		dialog.myShow();
		// Dialog mDialog = new DatePickerDialog(activity,
		// android.R.style.Theme_Holo_Light_Dialog,onDateSetListener,
		// c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
		// mDialog.show();
	}

	// DatePickerDialog.OnDateSetListener onDateSetListener = new
	// DatePickerDialog.OnDateSetListener() {
	//
	// @Override
	// public void onDateSet(DatePicker view, int year, int monthOfYear,int
	// dayOfMonth) {
	// appointment_year = year;
	// appointment_month = monthOfYear + 1;
	// appointment_day = dayOfMonth;
	// String month_str = String.valueOf(monthOfYear + 1);
	// String day_str = String.valueOf(dayOfMonth);
	// if (monthOfYear < 10) {
	// month_str = "0" + month_str;
	// }
	// if (dayOfMonth < 10) {
	// day_str = "0" + day_str;
	// }
	// appointment_date = String.valueOf(year) + month_str + day_str;
	// edit_date_str = String.valueOf(year) + "-" + month_str + "-" + day_str;
	// edit_date.setText(edit_date_str);
	// }
	// };

	public boolean isLeapYear(int year) {
		if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
			return true;
		} else {
			return false;
		}
	}

	public int getGapCount(int s_year, int s_month, int s_day, int e_year,
			int e_month, int e_day) {
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

		return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime()
				.getTime()) / (1000 * 60 * 60 * 24));
	}

	public void showPopview() {
		this.showAtLocation(popView, Gravity.CENTER, 0, 0);
	}

	// 分页监听
	public interface AppointMentListener {
		void onAppointMentChange(boolean isSuccess);
	}

	private AppointMentListener appointListener;

	public void setAppointMentListener(AppointMentListener appointListener) {
		this.appointListener = appointListener;
	}
}
