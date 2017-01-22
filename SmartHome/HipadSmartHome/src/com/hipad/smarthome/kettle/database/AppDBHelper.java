package com.hipad.smarthome.kettle.database;

import java.util.*;

import com.hipad.smarthome.kettle.database.table.AppTable;
import com.hipad.smarthome.kettle.database.table.QualityTable;
import com.hipad.smarthome.kettle.database.table.TempCTable;
import com.hipad.smarthome.kettle.database.table.TimeQuantumTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDBHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "app.db";
	public static final int DB_VERSION = 1;

	private static List<AppTable> mTables;

	static {
		mTables = new ArrayList<AppTable>();
		mTables.add(new QualityTable());
		mTables.add(new TempCTable());
		mTables.add(new TimeQuantumTable());
	}

	public AppDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (mTables != null) {
			for (AppTable table : mTables) {
				// 创建表
				table.createTable(db);
				// 创建索引
				table.createIndex(db);
				// 创建触发器
				table.createTrigger(db);
				// 初始化表数据
				table.initTable(db);
			}
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (mTables != null) {
			for (AppTable table : mTables) {
				// 修改表
				table.alertTable(db);
			}
		}
	}

}
