package com.intervencije.com.intervencije.sms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nejc on 17.11.2013.
 */
public class SqlLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_SMS = "events";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NUMBER = "event_number";
    public static final String COLUMN_BODY = "event_body";
    public static final String COLUMN_DATEANDTIME = "event_date_and_time";

    private static final String DATABASE_NAME = "alert.db";
    private static final int DATABASE_VERSION = 3;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SMS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NUMBER
            + " text, " + COLUMN_BODY
            + " text, " + COLUMN_DATEANDTIME + " text);";

    public SqlLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
        onCreate(db);
    }
}
