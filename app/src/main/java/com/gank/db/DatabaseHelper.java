package com.gank.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 11033 on 2017/3/4.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if no exits Gank("
                + "id integer primary key autoincrement,"
                + "gank_id integer not null,"
                + "gank_news text,"
                + "gank_content text,"
                + "gank_url varchar");
        //增加是否收藏选项
        db.execSQL("alert table Gank add column bookemark interger default 0");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
