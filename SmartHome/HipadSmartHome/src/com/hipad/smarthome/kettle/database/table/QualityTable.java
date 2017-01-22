package com.hipad.smarthome.kettle.database.table;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * 水壶的水质数据
 * 
 * @author Administrator
 *
 */
public class QualityTable extends AppTable {

	public static final String TABLE = "QualityTable";

	public static final class QualityColumns implements BaseColumns {
		public static final String USERID = "_userId";
		public static final String DEVICEID = "_deviceId";
		public static final String LEVEL = "_level";
	}

	@Override
	public void createTable(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder()
				.append("CREATE TABLE IF NOT EXISTS ").append(TABLE)
				.append("(").append(QualityColumns._ID)
				.append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
				.append(QualityColumns.USERID).append(" VARCHAR(16),")
				.append(QualityColumns.DEVICEID).append(" VARCHAR(16),")
				.append(QualityColumns.LEVEL).append(" double").append(");");
		db.execSQL(sb.toString());

	}

	@Override
	public void initTable(SQLiteDatabase db) {

	}

	@Override
	public void createIndex(SQLiteDatabase db) {

	}

	@Override
	public void createTrigger(SQLiteDatabase db) {

	}

}
