package com.missile.sqlite.demo.database;

import android.database.sqlite.SQLiteDatabase;

public abstract class AppTable {

    protected String mTableName;

    public abstract void createTable(SQLiteDatabase db);

    public abstract void initTable(SQLiteDatabase db);

    public abstract void createIndex(SQLiteDatabase db);

    public abstract void createTrigger(SQLiteDatabase db);

    public void dropTable(SQLiteDatabase db) {
        if (mTableName != null) {
            db.execSQL("DROP TABLE IF EXISTS " + mTableName);
        }
    }

    public void alertTable(SQLiteDatabase db) {
        dropTable(db);
        createTable(db);
        createIndex(db);
        createTrigger(db);
        initTable(db);
    }

}
