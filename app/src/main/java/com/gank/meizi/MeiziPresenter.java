package com.gank.meizi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.gank.app.App;
import com.gank.bean.MeiziNews;
import com.gank.model.StringModeImpl;
import com.gank.interfaze.OnStringListener;
import com.gank.picture.PictureActivity;
import com.gank.util.Api;
import com.gank.util.Network;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.ArrayList;

/**
 * Created by Swy on 2017/3/17.
 */

public class MeiziPresenter implements MeiziContract.Presenter {
    private static final String TAG = "MeiziPresenter";
    private Context context;
    private MeiziContract.View view;
    private StringModeImpl mode;
    private Gson gson=new Gson();
    private ArrayList<MeiziNews.Question> list=new ArrayList<>();
    //当前加载页数
    private int CurrentPagerNum;



    public MeiziPresenter(Context context,MeiziContract.View view){
        this.context=context;
        this.view=view;
        this.view.setPresenter(this);
        mode=new StringModeImpl(context);
    }

    @Override
    public void loadPosts(int PagerNum, final boolean cleaing) {
        CurrentPagerNum=PagerNum;
        if (cleaing){
            view.showLoading();
        }
        if (Network.networkConnected(context)){
            mode.load(Api.Gank_Meizi + PagerNum, new OnStringListener() {
                @Override
                public void onSuccess(String result) {
                    try {
                        MeiziNews news =gson.fromJson(result,MeiziNews.class);
                        if (cleaing){
                            list.clear();
                        }
                        for (MeiziNews.Question item :news.getResults()){
                            if (!queryIfIdExists(item.get_id())){
                                App.DbLiteOrm.insert(item, ConflictAlgorithm.Replace);
                            }else {
                                ArrayList<MeiziNews.Question> meizilist=App.DbLiteOrm.query(new QueryBuilder<MeiziNews.Question>(MeiziNews.Question.class)
                                .where(MeiziNews.Question.COL_ID+"=?",new String[]{item.get_id()}));
                                MeiziNews.Question meiziItem=meizilist.get(0);
                                item.setId(meiziItem.getId());
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

                }
            });
        }else {
            if (cleaing){
                QueryBuilder query=new QueryBuilder(MeiziNews.Question.class);
                query.appendOrderDescBy("_id");
                query.limit(0,10*CurrentPagerNum);
                list.addAll(App.DbLiteOrm.<MeiziNews.Question>query(query));
                view.showResult(list);
            }else {
                view.showNotNetError();
            }
        }
    }

    public boolean queryIfIdExists(String _id){
        ArrayList<MeiziNews.Question> questionArrayList=App.DbLiteOrm.query(new QueryBuilder(MeiziNews.Question.class)
                .where(MeiziNews.Question.COL_ID+"=?",new String[]{_id}));
        Log.i(TAG, "queryIfIdExists: questionArrayList.size():"+questionArrayList.size());
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

    @Override
    public void StartReading(int positon, View transitView) {
        MeiziNews.Question item=list.get(positon);
        Intent intent= PictureActivity.newIntent(context,item.getUrl());
        //Material Designer  ActivityOptionsCompat，我们可以通过这个类来启动activity和添加动画
        ActivityOptionsCompat optionsCompat= ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,transitView,PictureActivity.TRANSIT_PIC);
        try {
            ActivityCompat.startActivity(context,intent,optionsCompat.toBundle());
        }catch (IllegalArgumentException e){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    public void LookAround() {

    }

    //开始只加载一页内容
    @Override
    public void start() {
        loadPosts(1,true);
    }
}
