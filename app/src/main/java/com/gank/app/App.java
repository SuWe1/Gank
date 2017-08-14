package com.gank.app;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.appcompat.BuildConfig;

import com.litesuits.orm.LiteOrm;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by 11033 on 2017/3/8.
 */

public class App extends Application {
    private static final String DB_NAME = "liteorm.db";
    public static Context mContext;
    public static LiteOrm DbLiteOrm;

    @Override
    public void onCreate() {
        super.onCreate();
        //Bugly初始化
        CrashReport.initCrashReport(getApplicationContext(), "88ad7546e1", true);
        // the 'theme' has two values, 0 and 1
        // 0 --> day theme, 1 --> night theme
        if (getSharedPreferences("user_settings",MODE_PRIVATE).getInt("theme", 0) == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        mContext=this;
        DbLiteOrm=LiteOrm.newSingleInstance(this,DB_NAME);
        if (BuildConfig.DEBUG) {
            DbLiteOrm.setDebugged(true);
        }
    }
}
