package com.example.aditi.testing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasksDb.db";
    private static final int VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME,
                null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " +
                Contract.Entry.TABLE_NAME + " ("+
                Contract.Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.Entry.COLUMN_NAME +" TEXT);";

        db.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Entry.TABLE_NAME);
        onCreate(db);
    }
}
