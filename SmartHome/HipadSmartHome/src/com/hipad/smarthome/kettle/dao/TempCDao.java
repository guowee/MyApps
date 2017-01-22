package com.hipad.smarthome.kettle.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hipad.smarthome.kettle.database.AppDBHelper;
import com.hipad.smarthome.kettle.database.table.TempCTable;
import com.hipad.smarthome.kettle.database.table.TempCTable.TempCColumns;
import com.hipad.smarthome.kettle.statistics.entity.Temperature;

public class TempCDao {
	private AppDBHelper mOpenHelper;

	public TempCDao(Context context) {

		mOpenHelper = new AppDBHelper(context);
	}

	/**
	 * 添加温度数据入库
	 * 
	 * @param temp
	 */
	public void insertTempC(Temperature temp) {
		if (temp != null) {
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();
			Cursor cursor = db.query(
					TempCTable.TABLE,
					null,
					TempCColumns.USERID + " =? AND " + TempCColumns.DEVICEID
							+ " =? AND " + TempCColumns.CURRDATE + " =?",
					new String[] { temp.getUserId(), temp.getDeviceId(),
							temp.getCurrDate() }, null, null, null);

			if (cursor != null && cursor.getCount() == 0) {
				ContentValues values = new ContentValues();
				values.put(TempCColumns.USERID, temp.getUserId());
				values.put(TempCColumns.DEVICEID, temp.getDeviceId());
				values.put(TempCColumns.CURRDATE, temp.getCurrDate());
				values.put(TempCColumns.TEMPC_1, temp.getTempc_1());
				values.put(TempCColumns.TEMPC_2, temp.getTempc_2());
				values.put(TempCColumns.TEMPC_3, temp.getTempc_3());
				values.put(TempCColumns.TEMPC_4, temp.getTempc_4());
				values.put(TempCColumns.TEMPC_5, temp.getTempc_5());
				values.put(TempCColumns.TEMPC_6, temp.getTempc_6());
				values.put(TempCColumns.TEMPC_7, temp.getTempc_7());
				values.put(TempCColumns.TEMPC_8, temp.getTempc_8());

				db.insert(TempCTable.TABLE, null, values);
			} else {
				while (cursor != null && cursor.moveToNext()) {

					Temperature _temp = new Temperature(cursor.getString(cursor
							.getColumnIndex(TempCColumns.USERID)),
							cursor.getString(cursor
									.getColumnIndex(TempCColumns.DEVICEID)),
							cursor.getString(cursor
									.getColumnIndex(TempCColumns.CURRDATE)),
							cursor.getDouble(cursor
									.getColumnIndex(TempCColumns.TEMPC_1)),
							cursor.getDouble(cursor
									.getColumnIndex(TempCColumns.TEMPC_2)),
							cursor.getDouble(cursor
									.getColumnIndex(TempCColumns.TEMPC_3)),
							cursor.getDouble(cursor
									.getColumnIndex(TempCColumns.TEMPC_4)),
							cursor.getDouble(cursor
									.getColumnIndex(TempCColumns.TEMPC_5)),
							cursor.getDouble(cursor
									.getColumnIndex(TempCColumns.TEMPC_6)),
							cursor.getDouble(cursor
									.getColumnIndex(TempCColumns.TEMPC_7)),
							cursor.getDouble(cursor
									.getColumnIndex(TempCColumns.TEMPC_8)));

					ContentValues values = new ContentValues();
					values.put(TempCColumns.TEMPC_1,
							temp.getTempc_1() + _temp.getTempc_1());
					values.put(TempCColumns.TEMPC_2,
							temp.getTempc_2() + _temp.getTempc_2());
					values.put(TempCColumns.TEMPC_3,
							temp.getTempc_3() + _temp.getTempc_3());
					values.put(TempCColumns.TEMPC_4,
							temp.getTempc_4() + _temp.getTempc_4());
					values.put(TempCColumns.TEMPC_5,
							temp.getTempc_5() + _temp.getTempc_5());
					values.put(TempCColumns.TEMPC_6,
							temp.getTempc_6() + _temp.getTempc_6());
					values.put(TempCColumns.TEMPC_7,
							temp.getTempc_7() + _temp.getTempc_7());
					values.put(TempCColumns.TEMPC_8,
							temp.getTempc_8() + _temp.getTempc_8());

					db.update(TempCTable.TABLE, values, TempCColumns.USERID
							+ " =? AND " + TempCColumns.DEVICEID + " =? AND "+TempCColumns.CURRDATE+" =?",
							new String[] { temp.getUserId(),
									temp.getDeviceId(),temp.getCurrDate() });
				}
			}
			cursor.close();
			db.close();
		}

	}

	/**
	 * 获取数据库中最近插入的7条数据
	 * 
	 * @param userId
	 * @param deviceId
	 * @return
	 */

	public List<Temperature> obtainTemperatureList(String userId,
			String deviceId) {

		List<Temperature> list = new ArrayList<Temperature>();
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor cursor = null;
		if (userId != null && deviceId != null) {
			try {
				String sql = "select * from " + TempCTable.TABLE
						+ " WHERE " + TempCColumns.USERID + " =? AND "
						+ TempCColumns.DEVICEID + " =? ORDER BY "
						+ TempCColumns._ID + " DESC limit 7";
				cursor = db.rawQuery(sql, new String[] { userId, deviceId });

				while (cursor != null && cursor.moveToNext()) {

					Temperature temp = new Temperature();
					temp.setUserId(cursor.getString(cursor.getColumnIndex(TempCColumns.USERID)));
					temp.setDeviceId(cursor.getString(cursor.getColumnIndex(TempCColumns.DEVICEID)));
					temp.setCurrDate(cursor.getString(cursor.getColumnIndex(TempCColumns.CURRDATE)));
					temp.setTempc_1(cursor.getDouble(cursor.getColumnIndex(TempCColumns.TEMPC_1)));
					temp.setTempc_2(cursor.getDouble(cursor.getColumnIndex(TempCColumns.TEMPC_2)));
					temp.setTempc_3(cursor.getDouble(cursor.getColumnIndex(TempCColumns.TEMPC_3)));
					temp.setTempc_4(cursor.getDouble(cursor.getColumnIndex(TempCColumns.TEMPC_4)));
					temp.setTempc_5(cursor.getDouble(cursor.getColumnIndex(TempCColumns.TEMPC_5)));
					temp.setTempc_6(cursor.getDouble(cursor.getColumnIndex(TempCColumns.TEMPC_6)));
					temp.setTempc_7(cursor.getDouble(cursor.getColumnIndex(TempCColumns.TEMPC_7)));
					temp.setTempc_8(cursor.getDouble(cursor.getColumnIndex(TempCColumns.TEMPC_8)));

					list.add(temp);

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				if (cursor != null)
					cursor.close();
			}

		}

		db.close();

		return list;

	}

}
