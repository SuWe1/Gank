package com.gank.mark;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gank.adapter.BookMarksAdapter;
import com.gank.app.App;
import com.gank.bean.BeanTeype;
import com.gank.bean.FrontNews;
import com.gank.bean.GankNews;
import com.gank.bean.IosNews;
import com.gank.detail.DetailActivity;
import com.google.gson.Gson;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;

/**
 * Created by Swy on 2017/3/4.
 */

public class BookmarksPresenter implements BookmarksContract.Presenter {
    private static final String TAG = "BookmarksPresenter";
    private BookmarksContract.View view;
    private Context context;
    private Gson gson;

    private ArrayList<GankNews.Question> gankList;
    private ArrayList<FrontNews.Question> frontList;
    private ArrayList<IosNews.Question> iosList;

    private ArrayList<Integer> types;

    public BookmarksPresenter(Context context ,BookmarksContract.View view) {
        this.context=context;
        this.view=view;
        this.view.setPresenter(this);
        gson=new Gson();

        gankList=new ArrayList< >();
        frontList=new ArrayList<>();
        iosList=new ArrayList<>();
        types=new ArrayList<>();
    }

    @Override
    public void loadResults(boolean reflesh) {
        if (!reflesh){
            view.showLoading();
        }else {
            gankList.clear();
            frontList.clear();
            iosList.clear();
            types.clear();
        }
        checkForFreshData();
        view.showResults(gankList,frontList,iosList,types);
        view.stopLoading();
    }

    /**
     *
     * @param type
     * @param position item处于当前总列表的位置
     */
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
                intent.putExtra("_id", question.get_id());
                intent.putExtra("url",question.getUrl());
                intent.putExtra("title", question.getDesc());
                if (question.getImages()==null){
                    intent.putExtra("imgUrl", "");
                }else {
                    intent.putExtra("imgUrl", question.getImages().get(0));
                }
                break;
            case TYPE_IOS:
                IosNews.Question iosQuestion=iosList.get(position-gankList.size()-frontList.size()-3);
                intent.putExtra("type", BeanTeype.TYPE_IOS);
                intent.putExtra("id",iosQuestion.getId());
                intent.putExtra("_id", iosQuestion.get_id());
                intent.putExtra("url",iosQuestion.getUrl());
                intent.putExtra("title", iosQuestion.getDesc());
                if (iosQuestion.getImages()==null){
                    intent.putExtra("imgUrl", "");
                }else {
                    intent.putExtra("imgUrl", iosQuestion.getImages().get(0));
                }
                break;
            default:
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
        QueryBuilder frontqb=new QueryBuilder(FrontNews.Question.class)
                .where(FrontNews.Question.COL_MARK+"= ?",new String[]{markSign});
        ArrayList<FrontNews.Question> ftlist=App.DbLiteOrm.query(frontqb);
        for (int i=0;i<ftlist.size();i++){
            frontList.add(ftlist.get(i));
            types.add(BookMarksAdapter.TYPE_Front_NORMAL);
        }
        Log.i(TAG, "checkForFreshData: frontList.size(): "+frontList.size());


        types.add(BookMarksAdapter.TYPE_IOS_WITH_HEADER);
        QueryBuilder iosqb=new QueryBuilder<>(IosNews.Question.class).where(IosNews.Question.COL_MARK+"=?",new String[]{markSign});
        ArrayList<IosNews.Question> ioslist=App.DbLiteOrm.query(iosqb);
        for (int i=0;i<ioslist.size();i++){
            iosList.add(ioslist.get(i));
            types.add(BookMarksAdapter.TYPE_IOS_NORMAL);
        }
        Log.i(TAG, "checkForFreshData: iosList.size(): "+iosList.size());
    }

    @Override
    public void lookAround() {

    }

    @Override
    public void start() {

    }
}
