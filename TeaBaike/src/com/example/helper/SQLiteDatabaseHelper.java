package com.example.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabaseHelper {

	/** 用于管理和操作SQLite数据库 */
	private SQLiteDatabase database = null;
	/** 由SQLiteOpenHelper继承过来，用于实现数据库的建立与更新 */
	private MySQLiteOpenHelper dbHelper = null;
	/** 要创建的数据库名字 */
	private static final String DB_NAME = "mytea.db3";
	/** 数据库版本 */
	private static final int VERSION = 1;
	/** 创建表名 */
	private static final String SQL_CREATE_TABLE = "CREATE TABLE tb_teacontents(_id INTEGER PRIMARY KEY , title , source , create_time , type)";

	public SQLiteDatabaseHelper(Context context) {

		dbHelper = new MySQLiteOpenHelper(context, DB_NAME, null, VERSION);

		database = dbHelper.getReadableDatabase();

		database = dbHelper.getWritableDatabase();

	}

	class MySQLiteOpenHelper extends SQLiteOpenHelper {

		public MySQLiteOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(SQL_CREATE_TABLE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			if (newVersion > oldVersion) {

				db.execSQL("DROP TABLE IF EXISTS tb_teacontents");

				onCreate(db);
			}

		}

	}

	/**
	 * 查询数据返回Cursor
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public Cursor selectCursor(String sql, String[] selectionArgs) {

		return database.rawQuery(sql, selectionArgs);

	}

	/**
	 * 将查询得到的Cursor中的数据转换为List集合
	 * 
	 * @param cursor
	 * @return
	 */

	private List<Map<String, String>> cursorToList(Cursor cursor) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String[] arrColumnName = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < arrColumnName.length; i++) {
				String cols_value = cursor.getString(i);
				map.put(arrColumnName[i], cols_value);
			}
			list.add(map);
		}
		if (cursor != null) {

			cursor.close();
		}
		return list;
	}

	/**
	 * 执行带占位符的select语句，返回list集合
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */

	public List<Map<String, String>> SelectData(String sql,
			String[] selectionArgs) {

		Cursor cursor = selectCursor(sql, selectionArgs);

		return cursorToList(cursor);
	}

	/**
	 * 执行带占位符的update、insert、delete语句，更新数据库，返回true或false
	 * 
	 * @param sql
	 * @param bindArgs
	 *            占位符的参数值
	 * @return
	 */
	public boolean updataData(String sql, Object[] bindArgs) {
		try {
			if (bindArgs == null) {
				database.execSQL(sql);
			} else {
				database.execSQL(sql, bindArgs);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 关闭数据库操作类
	 */
	public void destroy() {
		if (dbHelper != null) {
			dbHelper.close();
			dbHelper = null;
		}
		if (database != null) {
			database.close();
			database = null;
		}
	}

}
