package com.example.searchview.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.Key;

/**
 * Created by Swy on 2018/1/28.
 */

public class HistoryDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION=1;

    private static final String DB_NAME = "SearchHistory.db";

    public HistoryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ HistoryContract.HistoryEntry.TABLE_NAME+" ("+
                HistoryContract.HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                HistoryContract.HistoryEntry.COLUMN_QUERY + " TEXT NOT NULL," +
                HistoryContract.HistoryEntry.COLUMN_INSERT_DATE + " INTEGER DEFAULT 0," +
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " INTEGER NOT NULL DEFAULT 0," +
                "UNIQUE (" + HistoryContract.HistoryEntry.COLUMN_QUERY + ") ON CONFLICT REPLACE);"
        );
    }

    /**
     * 数据库版本回退
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteTables(db);
        onCreate(db);
    }

    /**
     * 数据库版本升级
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteTables(db);
        onCreate(db);
    }

    private void deleteTables(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS "+ HistoryContract.HistoryEntry.TABLE_NAME);
    }
}
