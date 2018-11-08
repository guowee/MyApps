package com.missile.demo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Context mContext;
    AppCompatButton mInsertButton;
    AppCompatButton mQueryButton;
    AppCompatButton mDeleteButton;
    AppCompatButton mUpdateButton;

    private long lastTime = 0;

    private ContentObserverSubclass mContentObserverSubclass;

    private static final String PROVIDER_NAME = "com.missile.notepad.provider";
    private static final Uri CONTENT_INSERT_URI = Uri.parse("content://" + PROVIDER_NAME + "/notepad/insert");
    private static final Uri CONTENT_QUERY_URI = Uri.parse("content://" + PROVIDER_NAME + "/notepad/query");
    private static final Uri CONTENT_DELETE_URI = Uri.parse("content://" + PROVIDER_NAME + "/notepad/delete");
    private static final Uri CONTENT_UPDATE_URI = Uri.parse("content://" + PROVIDER_NAME + "/notepad/update");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        mInsertButton = findViewById(R.id.btn_insert);
        mInsertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long ret = insert();
                Toast.makeText(mContext, "ret: " + ret, Toast.LENGTH_SHORT).show();
            }
        });


        mQueryButton = findViewById(R.id.btn_query);
        mQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query();
            }
        });

        mDeleteButton = findViewById(R.id.btn_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });

        mUpdateButton = findViewById(R.id.btn_update);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });


        mContentObserverSubclass = new ContentObserverSubclass(new Handler());
        mContext.getContentResolver().registerContentObserver(CONTENT_INSERT_URI, true, mContentObserverSubclass);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mContentObserverSubclass != null) {
            mContext.getContentResolver().unregisterContentObserver(mContentObserverSubclass);
        }
    }

    private long insert() {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put("_noteId", String.valueOf(System.currentTimeMillis()).substring(0, 8));
        cv.put("_noteAuthor", "Alpha Go");

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNowStr = sdf.format(d);
        cv.put("_noteDate", dateNowStr);
        cv.put("_noteTitle", "ContentProvider Usage.");
        cv.put("_noteContent", "android.content.ContentProvider");

        Uri uri = resolver.insert(CONTENT_INSERT_URI, cv);
        long id = ContentUris.parseId(uri);
        return id;
    }


    private void query() {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(CONTENT_QUERY_URI, new String[]{"_noteId", "_noteAuthor", "_noteContent"}, null, null, "_noteId");
        if (cursor != null && cursor.getCount() > 0) {
            String noteId;
            String noteAuthor;
            String noteContent;
            while (cursor.moveToNext()) {
                noteId = cursor.getString(cursor.getColumnIndex("_noteId"));
                noteAuthor = cursor.getString(cursor.getColumnIndex("_noteAuthor"));
                noteContent = cursor.getString(cursor.getColumnIndex("_noteContent"));
                Log.e("TAG", "_noteId: " + noteId + ", _noteAuthor: " + noteAuthor + ", _noteContent: " + noteContent);
            }
            cursor.close();
        }

    }

    private int delete() {
        ContentResolver resolver = mContext.getContentResolver();
        return resolver.delete(CONTENT_DELETE_URI, "_noteId=?", new String[]{"1000001"});
    }

    private int update() {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put("_noteAuthor", "Jimmy Carter");
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNowStr = sdf.format(d);
        cv.put("_noteDate", dateNowStr);
        cv.put("_noteTitle", "ContentProvider Usage QQ.");
        cv.put("_noteContent", "android.content.ContentProvider WW");
        return resolver.update(CONTENT_UPDATE_URI, cv, "_noteId=?", new String[]{"15416793"});
    }


    private class ContentObserverSubclass extends ContentObserver {

        public ContentObserverSubclass(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            if (System.currentTimeMillis() - lastTime > 2000) {
                query();
                lastTime = System.currentTimeMillis();
            }
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
    }


}
