package com.intervencije.com.intervencije.sms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.intervencije.MainActivity;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Nejc on 17.11.2013.
 */
public class SmsDataSource {
    private SQLiteDatabase database;
    private SqlLiteHelper dbHelper;
    private String[] allColumns = {SqlLiteHelper.COLUMN_ID,SqlLiteHelper.COLUMN_NUMBER, SqlLiteHelper.COLUMN_BODY, SqlLiteHelper.COLUMN_DATEANDTIME};
    private String orderBy = SqlLiteHelper.COLUMN_DATEANDTIME + " DESC";
    private Context context;

    public SmsDataSource(Context context) {
        dbHelper = new SqlLiteHelper(context);
        this.context = context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public SmsListItems createSMS(String number, String body, String dateAndTime) {
        ContentValues values = new ContentValues();
        values.put(SqlLiteHelper.COLUMN_NUMBER, number);
        values.put(SqlLiteHelper.COLUMN_BODY, body);
        values.put(SqlLiteHelper.COLUMN_DATEANDTIME, dateAndTime);
        long insertId = database.insert(SqlLiteHelper.TABLE_SMS, null, values);

        Cursor cursor = database.query(SqlLiteHelper.TABLE_SMS,
                allColumns, SqlLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        SmsListItems newSms = cursorToComment(cursor);
        cursor.close();

        return newSms;

    }

    public void deleteSMS(SmsListItems sms) {
        long id = sms.getId();
        database.delete(SqlLiteHelper.TABLE_SMS, SqlLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public void deleteAllSms() {
        database.delete(SqlLiteHelper.TABLE_SMS, null, null);
    }

    public ArrayList getAllSms() {
        ArrayList Sms = new ArrayList();

        Cursor cursor = database.query(SqlLiteHelper.TABLE_SMS,
                allColumns, null, null, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SmsListItems sms = cursorToComment(cursor);
            Sms.add(sms);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return Sms;
    }

    String phoneNumberInContacts = "";

    private SmsListItems cursorToComment(Cursor cursor) {
        SmsListItems sms = new SmsListItems();
        sms.setId(cursor.getLong(0));

        String phoneNumber = cursor.getString(1);

        if(contactExists(context,phoneNumber))
            sms.setNumber(phoneNumberInContacts);
        else
            sms.setNumber(phoneNumber);

        sms.setBody(cursor.getString(2));

        String[] parts = cursor.getString(3).split(" ");
        sms.setDate(parts[0]);
        sms.setTime(parts[1]);


        return sms;
    }

    public boolean contactExists(Context context, String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                phoneNumberInContacts= cur.getString(2);
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }
}
