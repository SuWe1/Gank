package com.gank.mainpager;

import android.content.Context;
import android.content.Intent;

import com.android.volley.VolleyError;
import com.gank.app.App;
import com.gank.bean.BeanTeype;
import com.gank.bean.FrontNews;
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
 * Created by 11033 on 2017/3/11.
 */

public class FrontPresenter implements FrontContract.Presenter {
    private Context context;
    private FrontContract.View view;
//    private DatabaseHelper dbHelper;
//    private SQLiteDatabase db;

    private ArrayList<FrontNews.Question> list=new ArrayList<>();
    private StringModeImpl model;
    private Gson gson=new Gson();
    //当前加载到的页数
    private int currentPagerNum;

    public FrontPresenter(Context context, FrontContract.View view) {
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);
        model=new StringModeImpl(context);
//        dbHelper=new DatabaseHelper(context,"Histroy.db",null,9);
//        db=dbHelper.getWritableDatabase();
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
                        FrontNews news=gson.fromJson(result,FrontNews.class);
//                        ContentValues values=new ContentValues();
                        if (cleaing){
                            list.clear();
                        }
                        for (FrontNews.Question item : news.getResults()){
                            list.add(item);
                            App.DbLiteOrm.insert(item, ConflictAlgorithm.Replace);
//                            if (!queryIfIdExists(item.get_id())){
//                                db.beginTransaction();
//                                try {
//                                    values.put("front_id",item.get_id());
//                                    values.put("front_news",gson.toJson(item));
//                                    values.put("front_content","");
//                                    values.put("front_url",item.getUrl());
//                                    long addResult=db.insert("Front",null,values);
//                                }finally {
//                                    db.endTransaction();
//                                }
//                            }
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
                /*list.clear();
                Cursor cursor=db.query("Front",null,null,null,null,null,null);
                if (cursor.moveToNext()){
                    do {
                        FrontNews.Question news=gson.fromJson(cursor.getString(cursor.getColumnIndex("front_news")),FrontNews.Question.class);
                        list.add(news);
                    }while (cursor.moveToNext());
                }
                cursor.close();
                view.Stoploading();
                view.showResult(list);*/
                QueryBuilder query=new QueryBuilder(FrontNews.Question.class);
                query.appendOrderDescBy("_id");
                query.limit(0,10*currentPagerNum);
                list.addAll(App.DbLiteOrm.<FrontNews.Question>query(query));
                view.showResult(list);
            }else {
                view.showNotNetError();
            }
        }
    }

    /*private boolean queryIfIdExists(String id){
        Cursor cursor=db.query("Front",null,null,null,null,null,null);
        if (cursor.moveToNext()){
            do {
                if (id==cursor.getString(cursor.getColumnIndex("front_id")));
                return true;
            }while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }*/
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
        FrontNews.Question item=list.get(positon);
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
