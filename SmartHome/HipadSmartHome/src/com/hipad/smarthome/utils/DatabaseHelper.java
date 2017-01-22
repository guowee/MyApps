package com.hipad.smarthome.utils;

import java.util.ArrayList;

import com.hipad.smarthome.BaseActivity;
import com.hipad.smarthome.moudle.AppointItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static DatabaseHelper mDB = null;

	private static Context context;
	
	// 数据库名
	private final static String DB_NAME = "hz.db";
	// 表名
	private final static String TABLE_NAME = "appoint";
	// 版本号
	private final static int VERSION = 1;
	//	数据库SQL语句 创建一个预约表
	private static final String APPOINT_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + "gatewat_id varchar(20)," + "device_id varchar(20),"
			+ "date_str varchar(20)," + "request_code INTEGER DEFAULT 100," + "keep_warm INTEGER DEFAULT 100," + "keep_time INTEGER DEFAULT 100);";
	 
	private SQLiteDatabase db;
	
	private DatabaseHelper(Context cxt){
		super(cxt, DB_NAME, null, VERSION);
	}
	
	public static synchronized DatabaseHelper getInstance(Context cxt){
		context = cxt;
		if(mDB == null){
			mDB = new DatabaseHelper(cxt);
		}
		return mDB;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		//初始化创建数据表
		db.execSQL(APPOINT_TABLE_SQL); 
	}

	//插入数据
	public long insertAppointItem(ContentValues values){
		db = mDB.getWritableDatabase();
		long id = 0;
		if (db != null) {
			id = db.insert(TABLE_NAME, "appoint", values);
		}
		return id;
	}
	
	//删除项 根据时间秒数
	public long deleteAppointItem(String dateStr){
		db = mDB.getWritableDatabase();
		long id = 0;
		if(db != null){
			String[] whereArgs = new String[] { dateStr };
			id = db.delete(TABLE_NAME, "date_str=?", whereArgs);
		}
		return id;
	}
	
	//获取所有预约项
	public ArrayList<AppointItem> getAppointList() {
		ArrayList<AppointItem> appointList = new ArrayList<AppointItem>();
		db = mDB.getReadableDatabase();
		Cursor cursor = null;
		try {
			db.beginTransaction();
			cursor = db.rawQuery("select * from appoint order by date_str", null);
		}finally{
			db.endTransaction();
		}
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			AppointItem item = null;
			do {
				item = new AppointItem();
				item.setGatewayID(cursor.getString(cursor.getColumnIndex("gatewat_id")));
				item.setDeviceID(cursor.getString(cursor.getColumnIndex("device_id")));
				item.setDataStr(cursor.getString(cursor.getColumnIndex("date_str")));
				item.setRequestCode(cursor.getInt(cursor.getColumnIndex("request_code")));
				item.setKeepWarm(cursor.getInt(cursor.getColumnIndex("keep_warm")));
				item.setKeepTime(cursor.getInt(cursor.getColumnIndex("keep_time")));

				appointList.add(item);
				Log.i("getAppointList", item.toString());
			} while (cursor.moveToNext());
		}
		return appointList;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS appoint");  
		onCreate(db);
	}
	
	public void closeDB(){
		if(db != null){
			db.close();
			db = null;
		}
	}
}
