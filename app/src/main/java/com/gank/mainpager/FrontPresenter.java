package com.gank.mainpager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

/**
 * Created by 11033 on 2017/3/11.
 */

public class FrontPresenter implements GankContract.Presenter {
    private Context context;
    private GankContract.View view;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private ArrayList<GankNews.Question> list=new ArrayList<>();
    private StringModeImpl model;
    private Gson gson=new Gson();
    //当前加载到的页数
    private int currentPagerNum;

    public FrontPresenter(Context context, GankContract.View view) {
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);
        model=new StringModeImpl(context);
        dbHelper=new DatabaseHelper(context,"Histroy.db",null,9);
        db=dbHelper.getWritableDatabase();
    }

    @Override
    public void loadPosts(int PagerNum, final boolean cleaing) {
        currentPagerNum=PagerNum;
        if (cleaing){
            view.showLoading();
        }
        if (Network.networkConnected(context)){
            model.load(Api.Gank_Front + PagerNum, new OnStringListener() {
                @Override
                public void onSuccess(String result) {
                    try {
                        GankNews news=gson.fromJson(result,GankNews.class);
                        ContentValues values=new ContentValues();
                        if (cleaing){
                            list.clear();
                        }
                        for (GankNews.Question item : news.getResults()){
                            list.add(item);
                            if (!queryIfIdExists(item.get_id())){
                                db.beginTransaction();
                                try {
                                    values.put("front_id",item.get_id());
                                    values.put("front_news",gson.toJson(item));
                                    values.put("front_content","");
                                    values.put("front_url",item.getUrl());
                                    long addResult=db.insert("Front",null,values);
                                }finally {
                                    db.endTransaction();
                                }
                            }
                        }
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
        }else{
            //从数据库加载的逻辑
            //更新列表缓存 因为详情页都是用webView呈现 所以缓存content为空
            if (cleaing){
                list.clear();
                Cursor cursor=db.query("Front",null,null,null,null,null,null);
                if (cursor.moveToNext()){
                    do {
                        GankNews.Question news=gson.fromJson(cursor.getString(cursor.getColumnIndex("front_news")),GankNews.Question.class);
                        list.add(news);
                    }while (cursor.moveToNext());
                }
                cursor.close();
                view.Stoploading();
                view.showResult(list);
            }else {
                view.showNotNetError();
            }
        }
    }

    private boolean queryIfIdExists(String id){
        Cursor cursor=db.query("Front",null,null,null,null,null,null);
        if (cursor.moveToNext()){
            do {
                if (id==cursor.getString(cursor.getColumnIndex("front_id")));
                return true;
            }while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }
    @Override
    public void reflush() {
        //清空当前列表 然后重新加载
        loadPosts(currentPagerNum,true);
    }

    @Override
    public void loadMore(int PagerNum) {
        loadPosts(currentPagerNum+1,false);
    }

    @Override
    public void StartReading(int positon) {
        //当前列表信息传递给Detail页面处理
        GankNews.Question item=list.get(positon);
        Intent intent=new Intent(context, DetailActivity.class);
        intent.putExtra("id",item.get_id());
        intent.putExtra("type", BeanTeype.TYPE_Front);
        intent.putExtra("url",item.getUrl());
        intent.putExtra("title",item.getDesc());
        if (item.getImages()==null){
            intent.putExtra("imgUrl","");
        }else {
            intent.putExtra("imgUrl",item.getImages().get(0));
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
        loadPosts(currentPagerNum,true);
    }
}
