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
        db.execSQL("create table if not exists Gank("
                + "id integer primary key autoincrement,"
                + "gank_id text not null,"
                + "gank_news text,"
                + "gank_content text,"
                + "gank_url varchar)");
        //增加是否收藏选项
        db.execSQL("alter table Gank add column bookmark integer default 0");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 7:
                db.execSQL("drop table if exists Gank");
                onCreate(db);
        }
    }
}
