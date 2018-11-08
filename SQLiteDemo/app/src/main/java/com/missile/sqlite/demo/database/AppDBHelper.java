package com.missile.sqlite.demo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.missile.sqlite.demo.constant.AppCst;
import com.missile.sqlite.demo.database.table.NotepadTable;

import java.util.ArrayList;
import java.util.List;


public class AppDBHelper extends SQLiteOpenHelper {

    private static List<AppTable> mTables;

    static {
        mTables = new ArrayList<>();
        mTables.add(new NotepadTable());

    }

    public AppDBHelper(Context context) {
        super(context, AppCst.DB_NAME, null, AppCst.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        if (mTables != null) {
            for (AppTable table : mTables) {
                table.createTable(sqLiteDatabase);
                table.createIndex(sqLiteDatabase);
                table.createTrigger(sqLiteDatabase);
                table.initTable(sqLiteDatabase);
            }
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (mTables != null) {
            for (AppTable table : mTables) {
                table.alertTable(sqLiteDatabase);
            }
        }
    }
}
