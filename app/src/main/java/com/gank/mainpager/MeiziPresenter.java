package com.gank.mainpager;

import android.content.Context;

import com.android.volley.VolleyError;
import com.gank.app.App;
import com.gank.bean.MeiziNews;
import com.gank.bean.StringModeImpl;
import com.gank.interfaze.OnStringListener;
import com.gank.util.Api;
import com.gank.util.Network;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/17.
 */

public class MeiziPresenter implements MeiziContract.Presenter {
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
                            list.add(item);
                            App.DbLiteOrm.insert(item, ConflictAlgorithm.Replace);
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

    @Override
    public void reflush() {
        loadPosts(CurrentPagerNum,true);
    }

    @Override
    public void loadMore(int PagerNum) {
        loadPosts(CurrentPagerNum+PagerNum,false);
    }

    @Override
    public void StartReading(int positon) {
        MeiziNews.Question item=list.get(positon);

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
