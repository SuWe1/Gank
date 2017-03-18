package com.gank.mainpager;

import android.content.Context;
import android.content.Intent;

import com.android.volley.VolleyError;
import com.gank.app.App;
import com.gank.bean.BeanTeype;
import com.gank.bean.GankNews;
import com.gank.bean.StringModeImpl;
import com.gank.detail.DetailActivity;
import com.gank.interfaze.OnStringListener;
import com.gank.util.Api;
import com.gank.util.Network;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 11033 on 2017/3/4.
 */

public class GankPresenter implements GankContract.Presenter {

    private static final String TAG = "GankPresenter";
    private Context context;
    private GankContract.View view;
    private StringModeImpl model;

//    private DatabaseHelper dbHelper;
//    private SQLiteDatabase db;
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
//        dbHelper=new DatabaseHelper(context,"Histroy.db",null,9);
//        db=dbHelper.getWritableDatabase();
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
//                        ContentValues values = new ContentValues();
                        if (cleaing) {
                            list.clear();
                        }
                        for (GankNews.Question item : news.getResults()) {
//                            Log.i(TAG, "onSuccess: item.getImages()"+item.getImages().size());
                            list.add(item);
                            App.DbLiteOrm.insert(item, ConflictAlgorithm.Replace);
//                            Log.i(TAG, "onSuccess: list.size"+list.size());
//                            if (!queryIfIdExists(item.get_id())) {
//                                db.beginTransaction();
//                                //因为详情页都是用webView呈现 所以缓存content为空
//                                try {
//                                    values.put("gank_id", item.get_id());
//                                    values.put("gank_news", gson.toJson(item));
//                                    values.put("gank_content", "");
//                                    values.put("gank_url", item.getUrl());
//                                    long addResult=db.insert("Gank", null, values);
////                                    Log.i(TAG, "onSuccess: addResult "+addResult);
//                                } finally {
//                                    db.endTransaction();
//                                }
//                            }

                        }
//                        Log.i(TAG, "gankpresenter.model.load list.size="+list.size());
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
            //更新列表缓存 因为详情页都是用webView呈现 所以缓存content为空
            if (cleaing){
                /*list.clear();
                Cursor cursor=db.query("Gank",null,null,null,null,null,null);
                if (cursor.moveToNext()){
                    do {
                        GankNews.Question news=gson.fromJson(cursor.getString(cursor.getColumnIndex("gank_news")),GankNews.Question.class);
                        list.add(news);
                    }while (cursor.moveToNext());
                }
                cursor.close();
                view.Stoploading();
                view.showResult(list);*/
                QueryBuilder query=new QueryBuilder(GankNews.Question.class);
                query.appendOrderDescBy("_id");
                query.limit(0,10*CurrentPagerNum);
                list.addAll(App.DbLiteOrm.<GankNews.Question>query(query));
                view.showResult(list);
            }else {
                view.showNotNetError();
            }
        }
    }

    /*//查询是否存在数据库缓存中
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
    }*/

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
        intent  .putExtra("_id", list.get(positon).get_id());
        intent  .putExtra("url",list.get(positon).getUrl());
        intent   .putExtra("title", list.get(positon).getDesc());
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
    }

    //开始只加载一页内容
    @Override
    public void start() {
        loadPosts(1,true);
    }
}
