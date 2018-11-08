package com.missile.sqlite.demo.database.table;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.missile.sqlite.demo.database.AppTable;


public class NotepadTable extends AppTable {
    //表名
    public static final String TABLE = "notepadTable";

    /**
     * 表字段
     */
    public static final class NotepadColumns implements BaseColumns {
        public static final String NOTEID = "_noteId";
        public static final String NOTEAUTHOR = "_noteAuthor";
        public static final String NOTEDATE = "_noteDate";
        public static final String NOTETITLE = "_noteTitle";
        public static final String NOTECONTENT = "_noteContent";
    }


    public NotepadTable() {
        mTableName = TABLE;
    }

    @Override
    public void createTable(SQLiteDatabase db) {

        StringBuffer sb = new StringBuffer()
                .append("CREATE TABLE IF NOT EXISTS ").append(TABLE)
                .append("(").append(NotepadColumns._ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(NotepadColumns.NOTEID).append(" VARCHAR(50),")
                .append(NotepadColumns.NOTEAUTHOR).append(" VARCHAR(50),")
                .append(NotepadColumns.NOTEDATE).append(" VARCHAR(50),")
                .append(NotepadColumns.NOTETITLE).append(" VARCHAR(50),")
                .append(NotepadColumns.NOTECONTENT).append(" VARCHAR(50)")
                .append(");");
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
