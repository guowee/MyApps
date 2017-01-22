package com.hipad.smarthome.kettle.database.table;


import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
/*
 * 时间段数据表
 */
public class TimeQuantumTable extends AppTable {
	public static final String TABLE = "TimeQuantumTable";

	public static final class TimeQColumns implements BaseColumns {
		public static final String USERID = "_userId";
		public static final String DEVICEID = "_deviceId";

		public static final String CURRDATE = "_currDate";

		public static final String PERIOD_1 = "period_1";
		public static final String PERIOD_2 = "period_2";
		public static final String PERIOD_3 = "period_3";
		public static final String PERIOD_4 = "period_4";
		public static final String PERIOD_5 = "period_5";
		public static final String PERIOD_6 = "period_6";
		public static final String PERIOD_7 = "period_7";
		public static final String PERIOD_8 = "period_8";
		public static final String PERIOD_9 = "period_9";
		public static final String PERIOD_10 = "period_10";
		public static final String PERIOD_11 = "period_11";
		public static final String PERIOD_12 = "period_12";

	}

	@Override
	public void createTable(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder()
				.append("CREATE TABLE IF NOT EXISTS ").append(TABLE)
				.append("(").append(TimeQColumns._ID)
				.append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
				.append(TimeQColumns.USERID).append(" VARCHAR(16),")
				.append(TimeQColumns.DEVICEID).append(" VARCHAR(16),")
				.append(TimeQColumns.CURRDATE).append(" VARCHAR(20),")
				.append(TimeQColumns.PERIOD_1).append(" double,")
				.append(TimeQColumns.PERIOD_2).append(" double,")
				.append(TimeQColumns.PERIOD_3).append(" double,")
				.append(TimeQColumns.PERIOD_4).append(" double,")
				.append(TimeQColumns.PERIOD_5).append(" double,")
				.append(TimeQColumns.PERIOD_6).append(" double,")
				.append(TimeQColumns.PERIOD_7).append(" double,")
				.append(TimeQColumns.PERIOD_8).append(" double,")
				.append(TimeQColumns.PERIOD_9).append(" double,")
				.append(TimeQColumns.PERIOD_10).append(" double,")
				.append(TimeQColumns.PERIOD_11).append(" double,")
				.append(TimeQColumns.PERIOD_12).append(" double").append(");");
		db.execSQL(sb.toString());

	}

	@Override
	public void initTable(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createIndex(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createTrigger(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

}
