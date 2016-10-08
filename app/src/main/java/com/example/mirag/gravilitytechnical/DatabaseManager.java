package com.example.mirag.gravilitytechnical;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseManager extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "applefeed.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMBER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_APP_TABLE =
            "CREATE TABLE " + AppEntryManager.AppEntry.TABLE_NAME + " (" +
                    AppEntryManager.AppEntry._ID + " INTEGER PRIMARY KEY," +
                    AppEntryManager.AppEntry.COLUMN_NAME_APP_ID + NUMBER_TYPE + COMMA_SEP +
                    AppEntryManager.AppEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    AppEntryManager.AppEntry.COLUMN_NAME_SUMMARY + TEXT_TYPE + COMMA_SEP +
                    AppEntryManager.AppEntry.COLUMN_NAME_PRICE_VALUE + NUMBER_TYPE + COMMA_SEP +
                    AppEntryManager.AppEntry.COLUMN_NAME_PRICE_CURRENCY + TEXT_TYPE + COMMA_SEP +
                    AppEntryManager.AppEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    AppEntryManager.AppEntry.COLUMN_NAME_CATEGORY + TEXT_TYPE + COMMA_SEP +
                    AppEntryManager.AppEntry.COLUMN_NAME_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    AppEntryManager.AppEntry.COLUMN_NAME_IMAGE_1 + TEXT_TYPE + COMMA_SEP +
                    AppEntryManager.AppEntry.COLUMN_NAME_IMAGE_2 + TEXT_TYPE + COMMA_SEP +
                    AppEntryManager.AppEntry.COLUMN_NAME_IMAGE_3 + TEXT_TYPE + " )";

    private static final String SQL_DELETE_APP_TABLE =
            "DROP TABLE IF EXISTS " + AppEntryManager.AppEntry.TABLE_NAME;

    DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_APP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_APP_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_APP_TABLE);
        onCreate(db);
    }

}
