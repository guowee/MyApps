package com.hipad.smarthome.kettle.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hipad.smarthome.kettle.database.AppDBHelper;
import com.hipad.smarthome.kettle.database.table.QualityTable;
import com.hipad.smarthome.kettle.database.table.QualityTable.QualityColumns;
import com.hipad.smarthome.kettle.statistics.entity.WaterLevel;

public class WaterLevelDao {
	private AppDBHelper mOpenHelper;

	public WaterLevelDao(Context context) {
		mOpenHelper = new AppDBHelper(context);
	}

	/**
	 * 添加水质数据入库
	 * 
	 * @param level
	 */
	public void insertWaterLevel(WaterLevel level) {
		if (level != null) {
			SQLiteDatabase db = mOpenHelper.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(QualityColumns.DEVICEID, level.getDeviceId());
			values.put(QualityColumns.USERID, level.getUserId());
			values.put(QualityColumns.LEVEL, level.getLevel());
			db.insert(QualityTable.TABLE, null, values);
			db.close();
		}

	}

	/**
	 * 获取水质数据
	 * 
	 * @param userId
	 * @param deviceId
	 * @return
	 */
	public List<WaterLevel> obtainWaterLevelList(String userId, String deviceId) {
		List<WaterLevel> list = new ArrayList<WaterLevel>();
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor cursor = null;
		if (userId != null && deviceId != null) {

			try {

				String sql = "select * from " + QualityTable.TABLE + " where "
						+ QualityColumns.USERID + " =? AND "
						+ QualityColumns.DEVICEID + " =? ORDER BY "
						+ QualityColumns._ID + " DESC limit 20";

				cursor = db.rawQuery(sql, new String[] { userId, deviceId });

				while (cursor != null && cursor.moveToNext()) {

					WaterLevel level = new WaterLevel();
					level.setUserId(cursor.getString(cursor
							.getColumnIndex(QualityColumns.USERID)));
					level.setDeviceId(cursor.getString(cursor
							.getColumnIndex(QualityColumns.DEVICEID)));
					level.setLevel(cursor.getDouble(cursor
							.getColumnIndex(QualityColumns.LEVEL)));

					list.add(level);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}

		}
		db.close();
		return list;
	}

}
