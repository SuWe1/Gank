package com.gank.mainpager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gank.app.App;
import com.gank.bean.BeanTeype;
import com.gank.bean.GankNews;
import com.gank.model.StringModeImpl;
import com.gank.detail.DetailActivity;
import com.gank.model.OnStringListener;
import com.gank.util.Api;
import com.gank.util.Network;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.log.OrmLog;

import java.util.ArrayList;
import java.util.Random;

import static com.gank.app.App.DbLiteOrm;

/**
 * Created by Swy on 2017/3/4.
 */

public class GankPresenter implements GankContract.Presenter {

    private static final String TAG = "GankPresenter";
    private Context context;
    private GankContract.View view;
    private StringModeImpl model;

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
    }


    /**
     *
     * @param PagerNum 页数
     * @param cleaing 是否需要清空  区分刷新和加载更多
     */
    @Override
    public void loadPosts(int PagerNum, final boolean cleaing) {
        CurrentPagerNum=PagerNum;
        if (cleaing) {
            view.showLoading();
        }
        if (Network.networkConnected(context)) {
            model.load(Api.GANK_ANDROID + PagerNum, new OnStringListener() {
                @Override
                public void onSuccess(String result) {
                    try {
//                        Log.i(TAG, "gankpresenter.model.load.result"+result);
                        GankNews news = gson.fromJson(result, GankNews.class);
                        //contenvalues只能存储基本类型的数据，像string，int之类的，不能存储对象这种东西，而HashTable却可以存储对象。
//                        ContentValues values = new ContentValues();
                        if (cleaing) {
                            list.clear();
                        }
                        for (GankNews.Question item : news.getResults()) {
                            /**
                             * issue1.数据库查重:首先检测数据库中是否已经储存过该条数据
                             * issue2:因为每次重启后都是在网络上重新下载数据 如果是数据库已经存在的数据则不会重新加载，也导致了这些数据当前id值为空
                             * ，所有要绑定对应的id值.
                             */
                            if (!queryIfIdExists(item.get_id())){
                                DbLiteOrm.insert(item, ConflictAlgorithm.Replace);
                            }else {
                                ArrayList<GankNews.Question> ganklist=App.DbLiteOrm.query(new QueryBuilder<GankNews.Question>(GankNews.Question.class)
                                        .where(GankNews.Question.COL_ID+"=?",new String[]{item.get_id()}));
                                GankNews.Question gankitem=ganklist.get(0);
                                item.setId(gankitem.getId());
                            }
                            list.add(item);
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
        } else {
            //更新列表缓存 因为详情页都是用webView呈现 所以缓存content为空
            if (cleaing){
                QueryBuilder query=new QueryBuilder(GankNews.Question.class);
                query.appendOrderDescBy("id");
                query.limit(0,10*CurrentPagerNum);
                list.addAll(DbLiteOrm.<GankNews.Question>query(query));
                view.showResult(list);
            }else {
                view.showNotNetError();
            }
        }
    }

    public boolean queryIfIdExists(String _id){
        ArrayList<GankNews.Question> questionArrayList=App.DbLiteOrm.query(new QueryBuilder(GankNews.Question.class)
                .where(GankNews.Question.COL_ID+"=?",new String[]{_id}));
//        Log.i(TAG, "queryIfIdExists: questionArrayList.size():"+questionArrayList.size());
        if (questionArrayList.size()==0){
            return false;
        }
        return true;
    }
    @Override
    public void reflush() {
        loadPosts(CurrentPagerNum,true);
    }

    @Override
    public void loadMore(int PagerNum) {
        loadPosts(CurrentPagerNum+PagerNum,false);
    }

    //ID为自增长
    @Override
    public void StartReading(int positon) {
        //每个item就是一组数据
        GankNews.Question item=list.get(positon);
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("type", BeanTeype.TYPE_Gank);
        intent.putExtra("id",list.get(positon).getId());
        int id=list.get(positon).getId();
        Log.i(TAG, "StartReading: "+id);
        intent.putExtra("_id", list.get(positon).get_id());
        intent.putExtra("url",list.get(positon).getUrl());
        intent.putExtra("title", list.get(positon).getDesc());
        if (item.getImages()==null){
            intent.putExtra("imgUrl", "");
        }else {
            intent.putExtra("imgUrl", list.get(positon).getImages().get(0));
        }
        /**
         * Content的startActivity方法，需要开启一个新的task。如果使用 Activity的startActivity方法，
         * 不会有任何限制，因为Activity继承自Context，重载了startActivity方法。
         */
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void LookAround() {
        if (list.isEmpty()){
            view.showError();
            return;
        }
        StartReading(new Random().nextInt(list.size()));
        QueryBuilder gankqb =new QueryBuilder(GankNews.Question.class)
                .where(GankNews.Question.COL_MARK+"=?",new String[]{"true"});
        ArrayList<GankNews.Question> gklist= App.DbLiteOrm.query(gankqb);
        OrmLog.i(TAG,gklist);
    }

    //开始只加载一页内容
    @Override
    public void start() {
        loadPosts(1,true);
    }
}
