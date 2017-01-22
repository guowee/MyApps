package com.hipad.smarthome.kettle.database.table;


import com.hipad.smarthome.kettle.database.table.QualityTable.QualityColumns;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class TempCTable extends AppTable {

	public static final String TABLE = "TempCTable";

	public static final class TempCColumns implements BaseColumns {
		public static final String USERID = "_userId";
		public static final String DEVICEID = "_deviceId";

		public static final String CURRDATE = "_currDate";

		public static final String TEMPC_1 = "tempc_1";
		public static final String TEMPC_2 = "tempc_2";
		public static final String TEMPC_3 = "tempc_3";
		public static final String TEMPC_4 = "tempc_4";
		public static final String TEMPC_5 = "tempc_5";
		public static final String TEMPC_6 = "tempc_6";
		public static final String TEMPC_7 = "tempc_7";
		public static final String TEMPC_8 = "tempc_8";

	}

	@Override
	public void createTable(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder()
				.append("CREATE TABLE IF NOT EXISTS ").append(TABLE)
				.append("(").append(TempCColumns._ID)
				.append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
				.append(TempCColumns.USERID).append(" VARCHAR(16),")
				.append(TempCColumns.DEVICEID).append(" VARCHAR(16),")
				.append(TempCColumns.CURRDATE).append(" VARCHAR(20),")
				.append(TempCColumns.TEMPC_1).append(" double,")
				.append(TempCColumns.TEMPC_2).append(" double,")
				.append(TempCColumns.TEMPC_3).append(" double,")
				.append(TempCColumns.TEMPC_4).append(" double,")
				.append(TempCColumns.TEMPC_5).append(" double,")
				.append(TempCColumns.TEMPC_6).append(" double,")
				.append(TempCColumns.TEMPC_7).append(" double,")
				.append(TempCColumns.TEMPC_8).append(" double")
				.append(");");
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
