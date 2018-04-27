package com.smarter.com.smarter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by kasal on 1/04/2018.
 */

public final class Database {

    private UsageDbHelperr dbHelper;
    private SQLiteDatabase db;

    public Database(Context ctx) {
        dbHelper = new UsageDbHelperr(ctx);
    }


    public static class UsageEntry implements BaseColumns{
        public static final String TABLE_NAME = "Usage";
        public static final String COLUMN_1 = "resId ";
        public static final String COLUMN_2 = "usagedate";
        public static final String COLUMN_3 = "hours";
        public static final String COLUMN_4 = "fridge";
        public static final String COLUMN_5 = "aircond";
        public static final String COLUMN_6 = "washmach";
        public static final String COLUMN_7 = "temperature";

    }

    public Database open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public void close() {
        dbHelper.close();
    }

    public void insertUsage(Integer resid, String usagedate, Integer hours, Double fridge, Double aircond, Double washmach, Double temperature) {
        ContentValues values = new ContentValues();
        values.put(UsageEntry.COLUMN_1, resid);
        values.put(UsageEntry.COLUMN_2, usagedate);
        values.put(UsageEntry.COLUMN_3, hours);
        values.put(UsageEntry.COLUMN_4, fridge);
        values.put(UsageEntry.COLUMN_5, aircond);
        values.put(UsageEntry.COLUMN_6, washmach);
        values.put(UsageEntry.COLUMN_7, temperature);
        db.insert(UsageEntry.TABLE_NAME, null, values);
    }

    public Cursor getAllUsages() {
        return db.query(UsageEntry.TABLE_NAME, columns, null, null, null, null, null);
    }

    public Cursor getAllUsagesByresid(Integer res_id) {
        String queryString = "SELECT * FROM " + UsageEntry.TABLE_NAME + " WHERE " + UsageEntry.COLUMN_1 + " = ?" ;
        String[] whereArgs = {res_id.toString()};
        return db.rawQuery(queryString, whereArgs);
    }




    private String[] columns = {
            UsageEntry.COLUMN_1,
            UsageEntry.COLUMN_2,
            UsageEntry.COLUMN_3,
            UsageEntry.COLUMN_4,
            UsageEntry.COLUMN_5,
            UsageEntry.COLUMN_6,
            UsageEntry.COLUMN_7};

    public int deleteUsage(String rowId) {
        String[] selectionArgs = { String.valueOf(rowId) };
        String selection = UsageEntry.COLUMN_1 + " LIKE ?";
        return db.delete(UsageEntry.TABLE_NAME, selection,selectionArgs );
    }


}
