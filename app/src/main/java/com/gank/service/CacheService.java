package com.gank.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.gank.model.VolleySingleton;
import com.gank.db.DatabaseHelper;

import java.util.Calendar;

/**
 * Created by 11033 on 2017/3/4.
 * 数据表中还有一个字段--gank_content没有存储
 * 这是因为我们在请求知乎消息列表的时候，并没有返回消息的详细内容呀。
 * 不过详细内容我们还是需要缓存的，网络请求在UI线程上进行可能会引起ANR，那更好的解决办法就是在Service里面完成了。
 */

public class CacheService extends Service{
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private static final String TAG = CacheService.class.getSimpleName();

    private static final int TYPE_GANK=0x00;

    @Override
    public void onCreate() {
        super.onCreate();
        helper=new  DatabaseHelper(this,"Histroy.db",null,9);
        db=helper.getWritableDatabase();

        IntentFilter filter=new IntentFilter();
        filter.addAction("com.gank.LOCAL_BROADCAST");
        LocalBroadcastManager manager=LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(new LocalReceive(),filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    private void startGankCache(int id){
        Cursor cursor=db.query("Gank",null,null,null,null,null,null);
        if (cursor.moveToNext()){
            do {
                if (cursor.getInt(cursor.getColumnIndex("gank_id"))==id
                        && (cursor.getString(cursor.getColumnIndex("gank_content"))).equals("")){

                }
            }while (cursor.moveToNext());
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        VolleySingleton.getVolleySingletonl(this).getRequestQueue().cancelAll(TAG);
    }
    class LocalReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int id=intent.getIntExtra("id",0);
            switch (intent.getIntExtra("type",-1)){
                case TYPE_GANK:
                    startGankCache(id);
                    break;
            }
        }
    }

    //清楚超过时间期限的缓存
    private void deleteTimeOutPosts(){
        SharedPreferences sp=getSharedPreferences("user_setting",MODE_PRIVATE);
        Calendar c = Calendar.getInstance();
        long timeStamp = (c.getTimeInMillis() / 1000) - Long.parseLong(sp.getString("time_of_saving_articles", "7"))*24*60*60;

        String[] whereArgs = new String[] {String.valueOf(timeStamp)};
//        db.delete("Gank",)
    }
}
