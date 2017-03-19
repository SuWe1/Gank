package com.gank.mark;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gank.adapter.BookMarksAdapter;
import com.gank.app.App;
import com.gank.bean.BeanTeype;
import com.gank.bean.FrontNews;
import com.gank.bean.GankNews;
import com.gank.detail.DetailActivity;
import com.google.gson.Gson;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/4.
 */

public class BookmarksPresenter implements BookmarksContract.Presenter {
    private static final String TAG = "BookmarksPresenter";
    private BookmarksContract.View view;
    private Context context;
    private Gson gson;

    private ArrayList<GankNews.Question> gankList;
    private ArrayList<FrontNews.Question> frontList;

    private ArrayList<Integer> types;

    /*private DatabaseHelper dbHelper;
    private SQLiteDatabase db;*/
    public BookmarksPresenter(Context context ,BookmarksContract.View view) {
        this.context=context;
        this.view=view;
        this.view.setPresenter(this);
        gson=new Gson();
        /*dbHelper=new DatabaseHelper(context,"Histroy.db",null,9);
        db=dbHelper.getWritableDatabase();*/

        gankList=new ArrayList< >();
        frontList=new ArrayList<>();
        types=new ArrayList<>();
    }

    @Override
    public void loadResults(boolean reflesh) {
        if (!reflesh){
            view.showLoading();
        }else {
            gankList.clear();
            frontList.clear();
            types.clear();
        }
        checkForFreshData();
        view.showResults(gankList,frontList,types);
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
                intent.putExtra("id",gq.getId());
                intent.putExtra("_id", gq.get_id());
                intent.putExtra("url",gq.getUrl());
                intent.putExtra("title", gq.getDesc());
                if (gq.getImages()==null){
                    intent.putExtra("imgUrl", "");
                }else {
                    intent.putExtra("imgUrl", gq.getImages().get(0));
                }
                break;
            case TYPE_Front:
                FrontNews.Question question=frontList.get(position-2-gankList.size());
                intent.putExtra("type", BeanTeype.TYPE_Front);
                intent.putExtra("id",question.getId());
                intent  .putExtra("id", question.get_id());
                intent  .putExtra("url",question.getUrl());
                intent   .putExtra("title", question.getDesc());
                if (question.getImages()==null){
                    intent.putExtra("imgUrl", "");
                }else {
                    intent.putExtra("imgUrl", question.getImages().get(0));
                }
                break;
        }
        /**
         * Content的startActivity方法，需要开启一个新的task。如果使用 Activity的startActivity方法，
         * 不会有任何限制，因为Activity继承自Context，重载了startActivity方法。
         */
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void checkForFreshData() {
        types.add(BookMarksAdapter.TYPE_Gank_WITH_HEADER);
        /*Cursor cursor=db.rawQuery("select * from Gank where bookmark = ?",new String[]{"1"});
        if (cursor.moveToNext()){
            do {
                //将收藏的每组数据存放在一个列表中
                Log.i(TAG, "checkForFreshData: "+cursor.getString(cursor.getColumnIndex("gank_news")));
                GankNews.Question gq=gson.fromJson(cursor.getString(cursor.getColumnIndex("gank_news")),GankNews.Question.class);
                gankList.add(gq);
                types.add(BookMarksAdapter.TYPE_Gank_NORMAL);
            }while (cursor.moveToNext());
        }*/
        //模糊查找所有mark为true的
        String markSign="true";
        QueryBuilder gankqb =new QueryBuilder(GankNews.Question.class)
                .where(GankNews.Question.COL_MARK+"=?",new String[]{markSign});
        ArrayList<GankNews.Question> gklist= App.DbLiteOrm.query(gankqb);
        for (int i=0;i<gklist.size();i++){
            gankList.add(gklist.get(i));
            types.add(BookMarksAdapter.TYPE_Gank_NORMAL);
        }
        Log.i(TAG, "checkForFreshData: gankList.size(): "+gankList.size());
        types.add(BookMarksAdapter.TYPE_Front_WITH_HEADER);
        /*cursor=db.rawQuery("select * from Front where bookmark = ?",new String[]{"1"});
        if (cursor.moveToNext()){
            do {
                //将收藏的每组数据存放在一个列表中
                Log.i(TAG, "checkForFreshData: "+cursor.getString(cursor.getColumnIndex("front_news")));
                FrontNews.Question gq=gson.fromJson(cursor.getString(cursor.getColumnIndex("front_news")),FrontNews.Question.class);
                frontList.add(gq);
                types.add(BookMarksAdapter.TYPE_Gank_NORMAL);
            }while (cursor.moveToNext());
        }
        cursor.close();*/
        QueryBuilder frontqb=new QueryBuilder(FrontNews.Question.class)
                .where(FrontNews.Question.COL_MARK+"= ?",new String[]{markSign});
        ArrayList<FrontNews.Question> ftlist=App.DbLiteOrm.query(frontqb);
        for (int i=0;i<ftlist.size();i++){
            frontList.add(ftlist.get(i));
            types.add(BookMarksAdapter.TYPE_Front_NORMAL);
        }
        Log.i(TAG, "checkForFreshData: frontList.size(): "+frontList.size());
    }

    @Override
    public void lookAround() {

    }

    @Override
    public void start() {

    }
}
