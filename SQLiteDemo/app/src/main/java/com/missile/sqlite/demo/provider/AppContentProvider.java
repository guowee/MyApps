package com.missile.sqlite.demo.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.missile.sqlite.demo.database.AppDBHelper;
import com.missile.sqlite.demo.database.table.NotepadTable;


public class AppContentProvider extends ContentProvider {

    private final static String DB_NOTEPAD_TABLE_NAME = NotepadTable.TABLE;


    private static SQLiteOpenHelper sqLiteOpenHelper;
    SQLiteDatabase db;


    private static final UriMatcher sUriMatcher;

    final static String AUTHORITY = "com.missile.notepad.provider";
    final static Uri BASE_URI = Uri.parse("content://com.missile.notepad.provider");


    private static final int NOTEPAD_QUERY_CODE = 0x01;
    private static final int NOTEPAD_INSERT_CODE = 0x02;
    private static final int NOTEPAD_DELETE_CODE = 0x03;
    private static final int NOTEPAD_UPDATE_CODE = 0x04;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(AUTHORITY, "notepad/insert", NOTEPAD_INSERT_CODE);
        sUriMatcher.addURI(AUTHORITY, "notepad/delete", NOTEPAD_DELETE_CODE);
        sUriMatcher.addURI(AUTHORITY, "notepad/update", NOTEPAD_UPDATE_CODE);
        sUriMatcher.addURI(AUTHORITY, "notepad/query", NOTEPAD_QUERY_CODE);

    }

    @Override
    public boolean onCreate() {
        sqLiteOpenHelper = new AppDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String tableName = getTableName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Illegal Uri: " + uri);
        }

        db = sqLiteOpenHelper.getWritableDatabase();

        Cursor cursor = null;
        if (db.isOpen()) {
            cursor = db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), BASE_URI);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        String tableName = getTableName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Illegal Uri: " + uri);
        }
        db = sqLiteOpenHelper.getWritableDatabase();

        if (db.isOpen()) {
            long rowId = db.insert(tableName, null, contentValues);
            db.close();
            if (rowId == -1) {
                return null;
            } else {
                getContext().getContentResolver().notifyChange(BASE_URI, null);
                return ContentUris.withAppendedId(uri, rowId);
            }
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tableName = getTableName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Illegal Uri: " + uri);
        }
        db = sqLiteOpenHelper.getWritableDatabase();
        int number = 0;
        if (db.isOpen()) {
            number = db.delete(tableName, selection, selectionArgs);
            db.close();
            getContext().getContentResolver().notifyChange(BASE_URI, null);
        }
        return number;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tableName = getTableName(uri);
        if (tableName == null) {
            throw new IllegalArgumentException("Illegal Uri: " + uri);
        }
        db = sqLiteOpenHelper.getWritableDatabase();
        int number = 0;
        if (db.isOpen()) {
            number = db.update(tableName, contentValues, selection, selectionArgs);
            db.close();
            getContext().getContentResolver().notifyChange(BASE_URI, null);
        }
        return number;
    }


    private String getTableName(Uri uri) {
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case NOTEPAD_INSERT_CODE:
            case NOTEPAD_DELETE_CODE:
            case NOTEPAD_QUERY_CODE:
            case NOTEPAD_UPDATE_CODE:
                tableName = DB_NOTEPAD_TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;

    }
}


