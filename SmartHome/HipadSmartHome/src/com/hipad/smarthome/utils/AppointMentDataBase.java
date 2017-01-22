package com.hipad.smarthome.utils;

import java.util.ArrayList;
import java.util.Calendar;

import com.hipad.smarthome.AppointMentItem;
import com.hipad.smarthome.moudle.AppointItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppointMentDataBase extends SQLiteOpenHelper {

	private static AppointMentDataBase mDB = null;

	private static Context context;

	// 数据库名
	private final static String DB_NAME = "appoint.db";
	// 表名
	private final static String TABLE_NAME = "appointment";
	// 版本号
	private final static int VERSION = 1;
	// 数据库SQL语句 创建一个预约表
	private static final String APPOINT_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "user_id varchar(20),"
			+ "gateway_id varchar(20),"
			+ "device_id varchar(20),"
			+ "appoint_name varchar(20),"
			+ "warm_time integer,"
			+ "warm_tmp integer,"
			+ "appoint_start integer,"
			+ "appoint_type integer,"
			+ "is_everyday integer,"
			+ "menu1 integer,"
			+ "menu2 integer,"
			+ "scene_name varchar(20),"
			+ "delay_time integer,"
			+ "appoint_date varchar(20),"
			+ "appoint_time varchar(20));";

	private SQLiteDatabase db;

	public AppointMentDataBase(Context context) {
		super(context, DB_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	public static synchronized AppointMentDataBase getInstance(Context cxt) {
		context = cxt;
		if (mDB == null) {
			mDB = new AppointMentDataBase(cxt);
		}
		return mDB;
	}

	// 插入数据
	public long insertAppointmentItem(ContentValues values) {
		db = mDB.getWritableDatabase();
		long id = 0;
		if (db != null) {
			id = db.insert(TABLE_NAME, "appointment", values);
		}
		return id;
	}

	// 删除项 根据时间秒数
	public long deleteAppointMentItem(String appointTime,String appointDate,String deviceId,String gatewayId,String userId) {
		db = mDB.getWritableDatabase();
		long id = 0;
		if (db != null) {
			String[] whereArgs = new String[] { appointTime,appointDate,deviceId,gatewayId,userId};
			id = db.delete(TABLE_NAME, "appoint_time=? AND appoint_date=? AND device_id=? AND gateway_id=? AND user_id=?", whereArgs);
		}
		return id;
	}

	public long updateAppointMentItem(ContentValues values, String appointTime,String appointDate,String deviceId,String gatewayId,String userId) {
		db = mDB.getWritableDatabase();
		long id = 0;
		if (db != null) {
			String[] whereArgs = new String[] { appointTime,appointDate,deviceId,gatewayId,userId};
			id = db.update(TABLE_NAME, values, "appoint_time=? AND appoint_date=? AND device_id=? AND gateway_id=? AND user_id=?", whereArgs);
		}
		return id;
	}

	public AppointMentItem getAppointMentItem(String appoint_time,String appoint_date,String deviceId,String gatewayId,String userId) {
		AppointMentItem item = new AppointMentItem();
		db = mDB.getReadableDatabase();
		String[] args = { appoint_time,appoint_date,deviceId,gatewayId,userId};
		Cursor cursor = null;
		try {
			db.beginTransaction();
			cursor = db.rawQuery("SELECT * FROM appointment WHERE appoint_time=? AND appoint_date=? AND device_id=? AND gateway_id=? AND user_id=?", args);
		} finally {
			db.endTransaction();
		}
		
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			item.setAppointName(cursor.getString(cursor.getColumnIndex("appoint_name")));
			item.setGateWayId(cursor.getString(cursor.getColumnIndex("gateway_id")));
			item.setDeviceId(cursor.getString(cursor.getColumnIndex("device_id")));
			item.setAppointDate(cursor.getString(cursor.getColumnIndex("appoint_date")));
			item.setAppointTime(cursor.getString(cursor.getColumnIndex("appoint_time")));
			item.setAppointStart(cursor.getInt(cursor.getColumnIndex("appoint_start")));
			item.setAppointType(cursor.getInt(cursor.getColumnIndex("appoint_type")));
			item.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
			item.setIsEveryDay(cursor.getInt(cursor.getColumnIndex("is_everyday")));
			item.setDelayTime(cursor.getInt(cursor.getColumnIndex("delay_time")));
			item.setMenu1(cursor.getInt(cursor.getColumnIndex("menu1")));
			item.setMenu2(cursor.getInt(cursor.getColumnIndex("menu2")));
			item.setSceneName(cursor.getString(cursor.getColumnIndex("scene_name")));
			item.setWarmTime(cursor.getInt(cursor.getColumnIndex("warm_time")));
			item.setWarmTmp(cursor.getInt(cursor.getColumnIndex("warm_tmp")));
			return item;
		} else {
			return null;
		}
	}

	@SuppressWarnings("null")
	public boolean isAppointMentExist(String appoint_time,String appoint_date,String deviceId,String gatewayId,String userId) {
		db = mDB.getReadableDatabase();
		String[] args = { appoint_time,appoint_date,deviceId,gatewayId,userId};
		Cursor cursor = null;
		try {
			db.beginTransaction();
			cursor = db.rawQuery(
					"SELECT * FROM appointment WHERE appoint_time=? AND appoint_date=? AND device_id=? AND gateway_id=? AND user_id=?", args);
		} finally {
			db.endTransaction();
		}
		if (cursor.getCount() > 0) {
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
	
	public boolean hasEffectiveAppoint(String userId,String device_id) {
		ArrayList<AppointMentItem> appointList = new ArrayList<AppointMentItem>();
		db = mDB.getReadableDatabase();
		String[] args = {userId, device_id};
		Cursor cursor = null;
		try {
			db.beginTransaction();
			cursor = db.rawQuery("SELECT * FROM appointment WHERE user_id=? AND device_id=? ORDER BY delay_time DESC",args);
		} finally {
			db.endTransaction();
		}
		
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			AppointMentItem item = null;
			do {
				item = new AppointMentItem();
				item.setAppointName(cursor.getString(cursor.getColumnIndex("appoint_name")));
				item.setGateWayId(cursor.getString(cursor.getColumnIndex("gateway_id")));
				item.setDeviceId(cursor.getString(cursor.getColumnIndex("device_id")));
				item.setAppointDate(cursor.getString(cursor.getColumnIndex("appoint_date")));
				item.setAppointTime(cursor.getString(cursor.getColumnIndex("appoint_time")));
				item.setAppointStart(cursor.getInt(cursor.getColumnIndex("appoint_start")));
				item.setAppointType(cursor.getInt(cursor.getColumnIndex("appoint_type")));
				item.setIsEveryDay(cursor.getInt(cursor.getColumnIndex("is_everyday")));
				item.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
				item.setDelayTime(cursor.getInt(cursor.getColumnIndex("delay_time")));
				item.setMenu1(cursor.getInt(cursor.getColumnIndex("menu1")));
				item.setMenu2(cursor.getInt(cursor.getColumnIndex("menu2")));
				item.setSceneName(cursor.getString(cursor.getColumnIndex("scene_name")));
				item.setWarmTime(cursor.getInt(cursor.getColumnIndex("warm_time")));
				item.setWarmTmp(cursor.getInt(cursor.getColumnIndex("warm_tmp")));
				appointList.add(item);
			} while (cursor.moveToNext());
		} else {
			return false;
		}
		
		for (AppointMentItem item : appointList) {
			if (item.getIsEveryDay() == 1) {
				if (item.getAppointStart() == 1) {
					return true;
				}
			} else {
				if (item.getAppointStart() == 1) {
					if (!isTimePassed(item)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// 根据水壶的device_id查看当前水壶是否有预约事件
	public boolean isHasAppointMent(String device_id) {

		db = mDB.getReadableDatabase();
		String[] args = { device_id };
		Cursor cursor = null;
		try {
			db.beginTransaction();
			cursor = db.rawQuery("SELECT * FROM appointment WHERE device_id=? AND appoint_time > time('now') ORDER BY appoint_time DESC",args);
		} finally {
			db.endTransaction();
		}

		if (cursor != null &&cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}

	}

	// 获取所有预约项
	public ArrayList<AppointMentItem> getAppointList(String userId) {
		ArrayList<AppointMentItem> appointList = new ArrayList<AppointMentItem>();
		db = mDB.getReadableDatabase();
		String[] args = {userId};
		Cursor cursor = null;
		try {
			db.beginTransaction();
			cursor = db.rawQuery("SELECT * FROM appointment WHERE user_id=? ORDER BY delay_time DESC", args);
		} finally {
			db.endTransaction();
		}
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			AppointMentItem item = null;
			do {
				item = new AppointMentItem();
				item.setAppointName(cursor.getString(cursor.getColumnIndex("appoint_name")));
				item.setGateWayId(cursor.getString(cursor.getColumnIndex("gateway_id")));
				item.setDeviceId(cursor.getString(cursor.getColumnIndex("device_id")));
				item.setAppointDate(cursor.getString(cursor.getColumnIndex("appoint_date")));
				item.setAppointTime(cursor.getString(cursor.getColumnIndex("appoint_time")));
				item.setAppointStart(cursor.getInt(cursor.getColumnIndex("appoint_start")));
				item.setAppointType(cursor.getInt(cursor.getColumnIndex("appoint_type")));
				item.setIsEveryDay(cursor.getInt(cursor.getColumnIndex("is_everyday")));
				item.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
				item.setDelayTime(cursor.getInt(cursor.getColumnIndex("delay_time")));
				item.setMenu1(cursor.getInt(cursor.getColumnIndex("menu1")));
				item.setMenu2(cursor.getInt(cursor.getColumnIndex("menu2")));
				item.setSceneName(cursor.getString(cursor.getColumnIndex("scene_name")));
				item.setWarmTime(cursor.getInt(cursor.getColumnIndex("warm_time")));
				item.setWarmTmp(cursor.getInt(cursor.getColumnIndex("warm_tmp")));
				appointList.add(item);
			} while (cursor.moveToNext());
		} 
		return appointList;
	}
	
	public ArrayList<AppointMentItem> getAppointList(String deviceId,String gatewayId,String userId) {
		ArrayList<AppointMentItem> appointList = new ArrayList<AppointMentItem>();
		String[] args = {deviceId,gatewayId,userId};
		db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			db.beginTransaction();
			cursor = db.rawQuery("SELECT * FROM appointment WHERE device_id=? AND gateway_id=? AND user_id=? ORDER BY delay_time DESC", args);
		} finally {
			db.endTransaction();
		}
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			AppointMentItem item = null;
			do {
				item = new AppointMentItem();
				item.setAppointName(cursor.getString(cursor.getColumnIndex("appoint_name")));
				item.setGateWayId(cursor.getString(cursor.getColumnIndex("gateway_id")));
				item.setDeviceId(cursor.getString(cursor.getColumnIndex("device_id")));
				item.setAppointDate(cursor.getString(cursor.getColumnIndex("appoint_date")));
				item.setAppointTime(cursor.getString(cursor.getColumnIndex("appoint_time")));
				item.setAppointStart(cursor.getInt(cursor.getColumnIndex("appoint_start")));
				item.setAppointType(cursor.getInt(cursor.getColumnIndex("appoint_type")));
				item.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
				item.setIsEveryDay(cursor.getInt(cursor.getColumnIndex("is_everyday")));
				item.setDelayTime(cursor.getInt(cursor.getColumnIndex("delay_time")));
				item.setMenu1(cursor.getInt(cursor.getColumnIndex("menu1")));
				item.setMenu2(cursor.getInt(cursor.getColumnIndex("menu2")));
				item.setSceneName(cursor.getString(cursor.getColumnIndex("scene_name")));
				item.setWarmTime(cursor.getInt(cursor.getColumnIndex("warm_time")));
				item.setWarmTmp(cursor.getInt(cursor.getColumnIndex("warm_tmp")));
				appointList.add(item);
			} while (cursor.moveToNext());
		} 
		return appointList;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		this.db = db;
		// 初始化创建数据表
		db.execSQL(APPOINT_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS appointment");
		onCreate(db);
	}

	public void closeDB() {
		if (db != null) {
			db.close();
			db = null;
		}
	}
}
