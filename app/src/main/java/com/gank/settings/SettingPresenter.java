package com.gank.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.preference.Preference;

import com.bumptech.glide.Glide;
import com.gank.R;

/**
 * Created by 11033 on 2017/3/7.
 */

public class SettingPresenter implements SettingsContract.Presenter {

    private SettingsContract.View view;
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public static final int CLEAR_GLIDE_CACHE_SUCCESS=1;


    public SettingPresenter(SettingsContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.view.setPresenter(this);
        sp=context.getSharedPreferences("user_settings",Context.MODE_PRIVATE);
        editor=sp.edit();
    }

    @Override
    public void setNoPic(Preference preference) {
        editor.putBoolean("no_picture_mode",preference.getSharedPreferences().getBoolean("no_picture_mode",false));
        editor.apply();
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CLEAR_GLIDE_CACHE_SUCCESS:
                    view.showCleanGlideCacheSuccess();
            }
        }
    };
    @Override
    public void cleanGlideCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
                Message msg=handler.obtainMessage();
                msg.what=CLEAR_GLIDE_CACHE_SUCCESS;
                msg.sendToTarget();
//                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void setSaveTime(Preference preference, Object newValue) {
        editor.putString("time_of_saving_articles", (String) newValue);
        editor.apply();
    }

    @Override
    public String getTimeSummary() {
        String[] options=context.getResources().getStringArray(R.array.time_to_save_article);
        String str=sp.getString("time_of_saving_articles","7");
        switch (str){
            case "1":
                return options[0];
            case "3":
                return options[1];
            case "15":
                return options[3];
            default:
                return options[2];
        }
    }

    @Override
    public void start() {

    }
}
