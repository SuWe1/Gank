package com.gank.mainpager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gank.app.App;
import com.gank.bean.BeanTeype;
import com.gank.bean.FrontNews;
import com.gank.model.StringModeImpl;
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

import static com.gank.app.App.DbLiteOrm;

/**
 * Created by Swy on 2017/3/11.
 */

public class FrontPresenter implements FrontContract.Presenter {
    private static final String TAG = "FrontPresenter";
    private Context context;
    private FrontContract.View view;

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
    }

    @Override
    public void loadPosts(int PagerNum, final boolean cleaing) {
        currentPagerNum=PagerNum;
        if (cleaing){
            view.showLoading();
        }
        if (Network.networkConnected(context)){
            model.load(Api.GANK_FRONT + PagerNum, new OnStringListener() {
                @Override
                public void onSuccess(String result) {
                    try {
                        FrontNews news=gson.fromJson(result,FrontNews.class);
//                        ContentValues values=new ContentValues();
                        if (cleaing){
                            list.clear();
                        }
                        for (FrontNews.Question item : news.getResults()){
//                            item.setId(list.size()+1);
                            if (!queryIfIdExists(item.get_id())){
                                DbLiteOrm.insert(item, ConflictAlgorithm.Replace);
                            }else {
                                ArrayList<FrontNews.Question> frontlist= App.DbLiteOrm.query(new QueryBuilder<FrontNews.Question>(FrontNews.Question.class)
                                .where(FrontNews.Question.COL_ID+"=?",new String[]{item.get_id()}));
                                FrontNews.Question frontitem=frontlist.get(0);
                                item.setId(frontitem.getId());
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
        }else{
            //从数据库加载的逻辑
            //更新列表缓存 因为详情页都是用webView呈现 所以缓存content为空
            if (cleaing){
                QueryBuilder query=new QueryBuilder(FrontNews.Question.class);
                query.appendOrderDescBy("_id");
                query.limit(0,10*currentPagerNum);
                list.addAll(DbLiteOrm.<FrontNews.Question>query(query));
                view.showResult(list);
            }else {
                view.showNotNetError();
            }
        }
    }

    public boolean queryIfIdExists(String _id){
        ArrayList<FrontNews.Question> questionArrayList= DbLiteOrm.query(new QueryBuilder(FrontNews.Question.class)
                .where(FrontNews.Question.COL_ID+"=?",new String[]{_id}));
        Log.i(TAG, "queryIfIdExists: questionArrayList.size():"+questionArrayList.size());
        if (questionArrayList.size()==0){
            return false;
        }
        return true;
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

    //ID为自增长
    @Override
    public void StartReading(int positon) {
        //当前列表信息传递给Detail页面处理
        FrontNews.Question item=list.get(positon);
        Intent intent=new Intent(context, DetailActivity.class);
        intent.putExtra("id",item.getId());
        intent.putExtra("_id",item.get_id());
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
