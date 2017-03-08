package com.gank.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by 11033 on 2017/3/8.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // the 'theme' has two values, 0 and 1
        // 0 --> day theme, 1 --> night theme
        if (getSharedPreferences("user_settings",MODE_PRIVATE).getInt("theme", 0) == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}
