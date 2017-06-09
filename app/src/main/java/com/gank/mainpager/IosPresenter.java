package com.gank.mainpager;

import android.content.Context;
import android.content.Intent;

import com.android.volley.VolleyError;
import com.gank.app.App;
import com.gank.bean.BeanTeype;
import com.gank.bean.IosNews;
import com.gank.bean.StringModeImpl;
import com.gank.detail.DetailActivity;
import com.gank.interfaze.OnStringListener;
import com.gank.util.Api;
import com.gank.util.Network;
import com.google.gson.Gson;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Swy on 2017/6/9.
 */

public class IosPresenter implements IosContract.Presenter {
    private IosContract.View view;
    private Context context;

    private StringModeImpl model;

    private Gson gson=new Gson();
    private ArrayList<IosNews.Question> list=new ArrayList<>();


    private int CurrentPagerNum;

    public IosPresenter(IosContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.view.setPresenter(this);
        model=new StringModeImpl(context);
    }

    @Override
    public void start() {
        loadPosts(1,true);
    }

    @Override
    public void loadPosts(int PagerNum, final boolean cleaing) {
        CurrentPagerNum=PagerNum;
        if (cleaing){
            view.showLoading();
        }
        if (Network.networkConnected(context)){
            model.load(Api.Gank_IOS + PagerNum, new OnStringListener() {
                @Override
                public void onSuccess(String result) {
                    IosNews news=gson.fromJson(result,IosNews.class);
                    if (cleaing){
                        list.clear();
                    }
                    for (IosNews.Question item : news.getResults()){
                        /**
                         * issue1.数据库查重:首先检测数据库中是否已经储存过该条数据
                         * issue2:因为每次重启后都是在网络上重新下载数据 如果是数据库已经存在的数据则不会重新加载，也导致了这些数据当前id值为空
                         * ，所有要绑定对应的id值.
                         */
                        if (!queryIfIdExists(item.get_id())){
                            App.DbLiteOrm.insert(item, ConflictAlgorithm.Replace);
                        }else {
                            ArrayList<IosNews.Question> iosList=App.DbLiteOrm.query(new QueryBuilder<IosNews.Question>(IosNews.Question.class)
                                    .where(IosNews.Question.COL_ID+"=?",new String[]{item.get_id()}));
                            IosNews.Question iosItem=iosList.get(0);
                            item.set_id(iosItem.get_id());
                        }
                        list.add(item);
                    }
                    view.showResult(list);
                    view.Stoploading();
                }

                @Override
                public void onError(VolleyError error) {
                    view.showError();
                    view.Stoploading();
                }
            });
        }else {
            if (cleaing){
                QueryBuilder queryBuilder=new QueryBuilder(IosNews.Question.class);
                queryBuilder.appendOrderDescBy("id");
                queryBuilder.limit(0,0*CurrentPagerNum);
                list.addAll(App.DbLiteOrm.query(queryBuilder));
                view.showResult(list);

            }else {
                view.showNotNetError();
            }
        }
    }

    public boolean queryIfIdExists(String _id){
        ArrayList<IosNews.Question> queryList= App.DbLiteOrm.query(
                new QueryBuilder(IosNews.Question.class).where(IosNews.Question.COL_ID+"=?",new String[]{_id}));
        if (queryList.size()==0){
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
        loadPosts(CurrentPagerNum+=PagerNum,false);
    }

    @Override
    public void StartReading(int positon) {
        IosNews.Question item=list.get(positon);
        Intent intent=new Intent(context, DetailActivity.class);
        intent.putExtra("type", BeanTeype.TYPE_IOS);
        intent.putExtra("id",item.getId());
        intent.putExtra("_id",item.get_id());
        intent.putExtra("url",item.getUrl());
        intent.putExtra("title",item.getDesc());
        if (item.getImages()!=null){
            intent.putExtra("imgUrl",item.getImages().get(0));
        }else {
            intent.putExtra("imgUrl","");
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
}
