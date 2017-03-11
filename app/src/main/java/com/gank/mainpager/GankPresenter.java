package com.gank.mainpager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gank.bean.BeanTeype;
import com.gank.bean.GankNews;
import com.gank.bean.StringModeImpl;
import com.gank.db.DatabaseHelper;
import com.gank.detail.DetailActivity;
import com.gank.interfaze.OnStringListener;
import com.gank.util.Api;
import com.gank.util.Network;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * Created by 11033 on 2017/3/4.
 */

public class GankPresenter implements GankContract.Presenter {

    private Context context;
    private GankContract.View view;
    private StringModeImpl model;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Gson gson = new Gson();
    private ArrayList<GankNews.Question> list = new ArrayList<>();


    //当前加载页数
    private int CurrentPagerNum;


    //获取View
    public GankPresenter(Context context, GankContract.View view) {
        this.context=context;
        this.view=view;
        this.view.setPresenter(this);
        model=new StringModeImpl(context);
        dbHelper=new DatabaseHelper(context,"Histroy.db",null,9);
        db=dbHelper.getWritableDatabase();
    }


    @Override
    public void loadPosts(int PagerNum, final boolean cleaing) {
        CurrentPagerNum=PagerNum;
        if (cleaing) {
            view.showLoading();
        }
        if (Network.networkConnected(context)) {
            model.load(Api.Gank_Android + PagerNum, new OnStringListener() {
                @Override
                public void onSuccess(String result) {
                    try {
//                        Log.i(TAG, "gankpresenter.model.load.result"+result);
                        GankNews news = gson.fromJson(result, GankNews.class);
                        //contenvalues只能存储基本类型的数据，像string，int之类的，不能存储对象这种东西，而HashTable却可以存储对象。
                        ContentValues values = new ContentValues();
                        if (cleaing) {
                            list.clear();
                        }
                        for (GankNews.Question item : news.getResults()) {
//                            Log.i(TAG, "onSuccess: item.getImages()"+item.getImages().size());
                            list.add(item);
                            Log.i(TAG, "onSuccess: list.size"+list.size());
                            if (!queryIfIdExists(item.get_id())) {
                                db.beginTransaction();
                                try {
                                    values.put("gank_id", item.get_id());
                                    values.put("gank_news", gson.toJson(item));
                                    values.put("gank_content", "");
                                    values.put("gank_url", item.getUrl());
                                    long addResult=db.insert("Gank", null, values);
                                    Log.i(TAG, "onSuccess: addResult "+addResult);
                                } finally {
                                    db.endTransaction();
                                }
                            }

                        }
                        Log.i(TAG, "gankpresenter.model.load list.size="+list.size());
                        view.showResult(list);
                    }catch (JsonSyntaxException e){
                        view.showError();
                    }
                   view.Stoploading();
                }

                @Override
                public void onError(VolleyError error) {
                    view.Stoploading();
                    view.showError();
                }
            });
        } else {
            //暂时没做缓存加载
            view.showNotNetError();
        }
    }

    //查询是否存在数据库缓存中
    public boolean queryIfIdExists(String id) {
        Cursor cursor = db.query("Gank", null, null, null, null, null, null);
        if (cursor.moveToNext()) {
            do {
                if (id == cursor.getString(cursor.getColumnIndex("gank_id"))) ;
                return true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }

    @Override
    public void reflush() {
        loadPosts(CurrentPagerNum,true);
    }

    @Override
    public void loadMore(int PagerNum) {
        loadPosts(CurrentPagerNum+PagerNum,false);
    }

    @Override
    public void StartReading(int positon) {
        //每个item就是一组数据
        GankNews.Question item=list.get(positon);
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("type", BeanTeype.TYPE_Gank);
        intent  .putExtra("id", list.get(positon).get_id());
        intent  .putExtra("url",list.get(positon).getUrl());
        intent   .putExtra("title", list.get(positon).getDesc());
        if (item.getImages()==null){
            intent.putExtra("imgUrl", "");
        }else {
            intent.putExtra("imgUrl", list.get(positon).getImages().get(0));
        }
        context.startActivity(intent);
    }

    @Override
    public void LookAround() {
        if (list.isEmpty()){
            view.showError();
            return;
        }
        StartReading(new Random().nextInt(list.size()));
    }

    @Override
    public void start() {
        loadPosts(CurrentPagerNum,true);
    }
}
