package com.smarter.com.smarter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kasal on 2/04/2018.
 */

public class UsageDbHelperr extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Database.UsageEntry.TABLE_NAME + " (" +
                    Database.UsageEntry._ID + " INTEGER PRIMARY KEY," +
                    Database.UsageEntry.COLUMN_1 + " INTEGER NOT NULL," +
                    Database.UsageEntry.COLUMN_2 + " TEXT NOT NULL," +
                    Database.UsageEntry.COLUMN_3 + " INTEGER NOT NULL," +
                    Database.UsageEntry.COLUMN_4 + " REAL NOT NULL," +
                    Database.UsageEntry.COLUMN_5 + " REAL NOT NULL," +
                    Database.UsageEntry.COLUMN_6 + " REAL NOT NULL," +
                    Database.UsageEntry.COLUMN_7 + " REAL NOT NULL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Database.UsageEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Usage.db";


    public UsageDbHelperr(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }



}
