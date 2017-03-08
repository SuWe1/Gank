package com.gank.mark;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gank.adapter.BookMarksAdapter;
import com.gank.bean.BeanTeype;
import com.gank.bean.GankNews;
import com.gank.db.DatabaseHelper;
import com.gank.detail.DetailActivity;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/4.
 */

public class BookmarksPresenter implements BookmarksContract.Presenter {

    private BookmarksContract.View view;
    private Context context;
    private Gson gson;

    private ArrayList<GankNews.Question> gankList;

    private ArrayList<Integer> types;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    public BookmarksPresenter(Context context ,BookmarksContract.View view) {
        this.context=context;
        this.view=view;
        this.view.setPresenter(this);
        gson=new Gson();
        dbHelper=new DatabaseHelper(context,"Histroy.db",null,8);
        db=dbHelper.getWritableDatabase();

        gankList=new ArrayList<GankNews.Question>();
        types=new ArrayList<>();
    }

    @Override
    public void loadResults(boolean reflesh) {
        if (!reflesh){
            view.showLoading();
        }else {
            gankList.clear();
            types.clear();
        }
        checkForFreshData();
        view.showResults(gankList,types);
        view.stopLoading();
    }

    @Override
    public void startReading(BeanTeype type, int position) {
        Intent intent=new Intent(context, DetailActivity.class);
        switch (type){
            case TYPE_Gank:
                //gq就是一组数据
                GankNews.Question gq=gankList.get(position-1);
                intent.putExtra("type", BeanTeype.TYPE_Gank);
                intent  .putExtra("id", gq.get_id());
                intent  .putExtra("url",gq.getUrl());
                intent   .putExtra("title", gq.getDesc());
                if (gq.getImages()==null){
                    intent.putExtra("imgUrl", "");
                }else {
                    intent.putExtra("imgUrl", gq.getImages().get(0));
                }
        }
        context.startActivity(intent);
    }

    @Override
    public void checkForFreshData() {
        types.add(BookMarksAdapter.TYPE_Gank_WITH_HEADER);
        Cursor cursor=db.rawQuery("select * from Gank where bookmark = ?",new String[]{"1"});
        if (cursor.moveToNext()){
            do {
                //将收藏的每组数据存放在一个列表中
                GankNews.Question gq=gson.fromJson(cursor.getString(cursor.getColumnIndex("gank_news")),GankNews.Question.class);
                gankList.add(gq);
                types.add(BookMarksAdapter.TYPE_Gank_NORMAL);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void lookAround() {

    }

    @Override
    public void start() {

    }
}
