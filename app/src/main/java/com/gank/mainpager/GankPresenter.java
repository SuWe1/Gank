package com.gank.mainpager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.VolleyError;
import com.gank.bean.GankNews;
import com.gank.bean.StringModeImpl;
import com.gank.db.DatabaseHelper;
import com.gank.interfaze.OnStringListener;
import com.gank.util.Api;
import com.gank.util.Network;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/4.
 */

public class GankPresenter implements GankContract.Presenter {

    private Context context;
    private GankContract.View view;
    private StringModeImpl model;

    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private Gson gson = new Gson();
    private ArrayList<GankNews.Question> list = new ArrayList<>();

    //获取View
    public GankPresenter(Context context, GankContract.View view) {
    }

    @Override
    public void loatPosts(int PagerNum, final boolean cleaing) {
        if (cleaing) {
            view.showLoading();
        }
        if (Network.networkConnected(context)) {
            model.load(Api.Gank_Android + PagerNum, new OnStringListener() {
                @Override
                public void onSuccess(String result) {
                    GankNews news = gson.fromJson(result, GankNews.class);
                    //contenvalues只能存储基本类型的数据，像string，int之类的，不能存储对象这种东西，而HashTable却可以存储对象。
                    ContentValues values = new ContentValues();
                    if (cleaing) {
                        list.clear();
                    }
                    for (GankNews.Question item : news.getResults()) {
                        list.add(item);
                        if (!queryIfIdExists(item.get_id())) {
                            db.beginTransaction();
                            try {
                                values.put("gank_id", item.get_id());
                                values.put("gank_news", gson.toJson(item));
                                values.put("gank_content", "");
                                values.put("gank_url", item.getUrl());
                                db.insert("Gank", null, values);
                            } finally {
                                db.endTransaction();
                            }
                        }

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
            //暂时没做
        }
    }

    //查询是否存在数据库缓存中
    public boolean queryIfIdExists(int id) {
        Cursor cursor = db.query("Gank", null, null, null, null, null, null);
        if (cursor.moveToNext()) {
            do {
                if (id == cursor.getInt(cursor.getColumnIndex("gank_id"))) ;
                return true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }

    @Override
    public void reflush() {

    }

    @Override
    public void loadMore(int PagerNum) {

    }

    @Override
    public void StartReading(int positon) {

    }

    @Override
    public void LookAround() {

    }

    @Override
    public void start() {

    }
}
