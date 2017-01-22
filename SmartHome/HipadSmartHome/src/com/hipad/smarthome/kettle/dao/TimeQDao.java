package com.hipad.smarthome.kettle.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hipad.smarthome.kettle.database.AppDBHelper;
import com.hipad.smarthome.kettle.database.table.TimeQuantumTable;
import com.hipad.smarthome.kettle.database.table.TimeQuantumTable.TimeQColumns;
import com.hipad.smarthome.kettle.statistics.entity.TimeQuantum;

public class TimeQDao {

	private AppDBHelper mOpenHelper;

	public TimeQDao(Context context) {
		mOpenHelper = new AppDBHelper(context);

	}

	/**
	 * 添加不同时段的煮水次数入库
	 * 
	 * @param times
	 */
	public void insertTimes(TimeQuantum times) {
		if (times != null) {

			SQLiteDatabase db = mOpenHelper.getWritableDatabase();

			Cursor cursor = db.query(TimeQuantumTable.TABLE,
					null, 
					TimeQColumns.USERID + " =? AND " + TimeQColumns.DEVICEID + " =? AND " + TimeQColumns.CURRDATE + " =?",
					new String[] { times.getUserId(), times.getDeviceId(), times.getCurrDate() }, 
					null, null, null);

			if (cursor != null && cursor.getCount() == 0) {
				ContentValues values = new ContentValues();
				values.put(TimeQColumns.USERID, times.getUserId());
				values.put(TimeQColumns.DEVICEID, times.getDeviceId());
				values.put(TimeQColumns.CURRDATE, times.getCurrDate());
				values.put(TimeQColumns.PERIOD_1, times.getPeriod_1());
				values.put(TimeQColumns.PERIOD_2, times.getPeriod_2());
				values.put(TimeQColumns.PERIOD_3, times.getPeriod_3());
				values.put(TimeQColumns.PERIOD_4, times.getPeriod_4());
				values.put(TimeQColumns.PERIOD_5, times.getPeriod_5());
				values.put(TimeQColumns.PERIOD_6, times.getPeriod_6());
				values.put(TimeQColumns.PERIOD_7, times.getPeriod_7());
				values.put(TimeQColumns.PERIOD_8, times.getPeriod_8());
				values.put(TimeQColumns.PERIOD_9, times.getPeriod_9());
				values.put(TimeQColumns.PERIOD_10, times.getPeriod_10());
				values.put(TimeQColumns.PERIOD_11, times.getPeriod_11());
				values.put(TimeQColumns.PERIOD_12, times.getPeriod_12());

				db.insert(TimeQuantumTable.TABLE, null, values);
			} else {

				while (cursor != null && cursor.moveToNext()) {

					TimeQuantum _time = new TimeQuantum(
							cursor.getString(cursor.getColumnIndex(TimeQColumns.USERID)),
							cursor.getString(cursor.getColumnIndex(TimeQColumns.DEVICEID)),
							cursor.getString(cursor.getColumnIndex(TimeQColumns.CURRDATE)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_1)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_2)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_3)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_4)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_5)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_6)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_7)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_8)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_9)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_10)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_11)),
							cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_12)));

					ContentValues values = new ContentValues();
					values.put(TimeQColumns.PERIOD_1, _time.getPeriod_1() + times.getPeriod_1());
					values.put(TimeQColumns.PERIOD_2, _time.getPeriod_2() + times.getPeriod_2());
					values.put(TimeQColumns.PERIOD_3, _time.getPeriod_3() + times.getPeriod_3());
					values.put(TimeQColumns.PERIOD_4, _time.getPeriod_4() + times.getPeriod_4());
					values.put(TimeQColumns.PERIOD_5, _time.getPeriod_5() + times.getPeriod_5());
					values.put(TimeQColumns.PERIOD_6, _time.getPeriod_6() + times.getPeriod_6());
					values.put(TimeQColumns.PERIOD_7, _time.getPeriod_7() + times.getPeriod_7());
					values.put(TimeQColumns.PERIOD_8, _time.getPeriod_8() + times.getPeriod_8());
					values.put(TimeQColumns.PERIOD_9, _time.getPeriod_9() + times.getPeriod_9());
					values.put(TimeQColumns.PERIOD_10, _time.getPeriod_10() + times.getPeriod_10());
					values.put(TimeQColumns.PERIOD_11, _time.getPeriod_11() + times.getPeriod_11());
					values.put(TimeQColumns.PERIOD_12, _time.getPeriod_12() + times.getPeriod_12());

					db.update(TimeQuantumTable.TABLE,
							values,
							TimeQColumns.USERID + " =? AND " + TimeQColumns.DEVICEID + " =? AND "+TimeQColumns.CURRDATE+" =?",
							new String[] { times.getUserId(), times.getDeviceId(), times.getCurrDate() });
				}
			}

			cursor.close();
			db.close();
		}
	}

	public List<TimeQuantum> obtainTimeQuantumList(String userId,
			String deviceId) {

		List<TimeQuantum> list = new ArrayList<TimeQuantum>();
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor cursor = null;
		try {

			if (userId != null && deviceId != null) {

				String sql = "select * from " + TimeQuantumTable.TABLE
						+ " WHERE " + TimeQColumns.USERID + " =? AND "
						+ TimeQColumns.DEVICEID + " =? ORDER BY "
						+ TimeQColumns._ID + " DESC LIMIT 7";

				cursor=db.rawQuery(sql, new String[] { userId, deviceId });

				while (cursor != null && cursor.moveToNext()) {
					TimeQuantum time = new TimeQuantum();

					time.setUserId(cursor.getString(cursor.getColumnIndex(TimeQColumns.USERID)));
					time.setDeviceId(cursor.getString(cursor.getColumnIndex(TimeQColumns.DEVICEID)));
					time.setCurrDate(cursor.getString(cursor.getColumnIndex(TimeQColumns.CURRDATE)));
					time.setPeriod_1(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_1)));
					time.setPeriod_2(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_2)));
					time.setPeriod_3(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_3)));
					time.setPeriod_4(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_4)));
					time.setPeriod_5(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_5)));
					time.setPeriod_6(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_6)));
					time.setPeriod_7(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_7)));
					time.setPeriod_8(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_8)));
					time.setPeriod_9(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_9)));
					time.setPeriod_10(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_10)));
					time.setPeriod_11(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_11)));
					time.setPeriod_12(cursor.getDouble(cursor.getColumnIndex(TimeQColumns.PERIOD_12)));

					list.add(time);

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (cursor != null)
				cursor.close();
		}
		db.close();

		return list;

	}

}
