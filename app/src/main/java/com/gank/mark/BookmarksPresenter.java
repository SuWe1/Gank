package com.gank.mark;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gank.adapter.BookMarksAdapter;
import com.gank.app.App;
import com.gank.bean.BaseBean;
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

    private ArrayList<BaseBean> newsList = new ArrayList<>();


    public BookmarksPresenter(Context context, BookmarksContract.View view) {
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);
        gson = new Gson();
    }

    @Override
    public void loadResults(boolean reflesh) {
        if (!reflesh) {
            view.showLoading();
        } else {
            newsList.clear();
        }
        checkForFreshData();
        if (newsList != null) {
            view.showResults(newsList);
        }
        view.stopLoading();
    }

    /**
     * @param position item处于当前总列表的位置
     */
    @Override
    public void startReading(int position) {
        Intent intent = new Intent(context, DetailActivity.class);
        BaseBean newsItem = newsList.get(position);
        switch (newsItem.beanTeype) {
            case TYPE_Gank:
                //gq就是一组数据
                GankNews.Question gq = (GankNews.Question) newsList.get(position);
                intent.putExtra("type", BeanTeype.TYPE_Gank);
                intent.putExtra("id", gq.getId());
                intent.putExtra("_id", gq.get_id());
                intent.putExtra("url", gq.getUrl());
                intent.putExtra("title", gq.getDesc());
                if (gq.getImages() == null) {
                    intent.putExtra("imgUrl", "");
                } else {
                    intent.putExtra("imgUrl", gq.getImages().get(0));
                }
                break;
            case TYPE_Front:
                FrontNews.Question question = (FrontNews.Question) newsList.get(position);
                intent.putExtra("type", BeanTeype.TYPE_Front);
                intent.putExtra("id", question.getId());
                intent.putExtra("_id", question.get_id());
                intent.putExtra("url", question.getUrl());
                intent.putExtra("title", question.getDesc());
                if (question.getImages() == null) {
                    intent.putExtra("imgUrl", "");
                } else {
                    intent.putExtra("imgUrl", question.getImages().get(0));
                }
                break;
            case TYPE_IOS:
                IosNews.Question iosQuestion = (IosNews.Question) newsList.get(position);
                intent.putExtra("type", BeanTeype.TYPE_IOS);
                intent.putExtra("id", iosQuestion.getId());
                intent.putExtra("_id", iosQuestion.get_id());
                intent.putExtra("url", iosQuestion.getUrl());
                intent.putExtra("title", iosQuestion.getDesc());
                if (iosQuestion.getImages() == null) {
                    intent.putExtra("imgUrl", "");
                } else {
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
        newsList.clear();
        //模糊查找所有mark为true的
        String markSign = "true";
        QueryBuilder gankqb = new QueryBuilder(GankNews.Question.class)
                .where(GankNews.Question.COL_MARK + "=?", new String[]{markSign});
        ArrayList<GankNews.Question> gklist = App.DbLiteOrm.query(gankqb);
        if (gklist.size() > 0) {
            newsList.add(new BaseBean(BookMarksAdapter.TYPE_Gank_WITH_HEADER));
            for (int i = 0; i < gklist.size(); i++) {
                gklist.get(i).newsDetailType = BookMarksAdapter.TYPE_Gank_NORMAL;
                newsList.add(gklist.get(i));
            }
        }
        QueryBuilder frontqb = new QueryBuilder(FrontNews.Question.class)
                .where(FrontNews.Question.COL_MARK + "= ?", new String[]{markSign});
        ArrayList<FrontNews.Question> ftlist = App.DbLiteOrm.query(frontqb);
        if (ftlist.size() > 0) {
            newsList.add(new BaseBean(BookMarksAdapter.TYPE_Front_WITH_HEADER));
            for (int i = 0; i < ftlist.size(); i++) {
                ftlist.get(i).newsDetailType = BookMarksAdapter.TYPE_Front_NORMAL;
                newsList.add(ftlist.get(i));
            }
        }
        QueryBuilder iosqb = new QueryBuilder<>(IosNews.Question.class).where(IosNews.Question.COL_MARK + "=?", new String[]{markSign});
        ArrayList<IosNews.Question> ioslist = App.DbLiteOrm.query(iosqb);
        if (ioslist.size() > 0) {
            newsList.add(new BaseBean(BookMarksAdapter.TYPE_IOS_WITH_HEADER));
            for (int i = 0; i < ioslist.size(); i++) {
                ioslist.get(i).newsDetailType = BookMarksAdapter.TYPE_IOS_NORMAL;
                newsList.add(ioslist.get(i));
            }
        }
    }

    @Override
    public void lookAround() {

    }

    @Override
    public void start() {

    }
}
