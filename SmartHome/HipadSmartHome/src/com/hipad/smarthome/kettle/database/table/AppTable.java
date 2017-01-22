package com.hipad.smarthome.kettle.database.table;

import android.database.sqlite.SQLiteDatabase;

public abstract class AppTable {

	protected String mTableName;

	/**
	 * 创建表
	 * 
	 * @param db
	 * @return
	 */
	public abstract void createTable(SQLiteDatabase db);

	/**
	 * 初始化表数据
	 * 
	 * @param db
	 * @return
	 */
	public abstract void initTable(SQLiteDatabase db);

	/**
	 * 创建表索引
	 * 
	 * @param db
	 * @return
	 */
	public abstract void createIndex(SQLiteDatabase db);

	/**
	 * 创建触发器
	 * 
	 * @param db
	 * @return
	 */
	public abstract void createTrigger(SQLiteDatabase db);

	/**
	 * 删除表
	 * 
	 * @param db
	 * @return
	 */
	public void dropTable(SQLiteDatabase db) {
		if (mTableName != null) {
			db.execSQL("DROP TABLE IF EXISTS " + mTableName);
		}
	}

	/**
	 * 修改表
	 * 
	 * @param db
	 * @return
	 */
	public void alertTable(SQLiteDatabase db) {
		dropTable(db);
		createTable(db);
		createIndex(db);
		createTrigger(db);
		initTable(db);
	}

}
