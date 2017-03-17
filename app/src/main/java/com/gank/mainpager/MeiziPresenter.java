package com.gank.mainpager;

import android.content.Context;

import com.gank.bean.MeiziNews;
import com.gank.bean.StringModeImpl;
import com.gank.db.DatabaseHelper;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/17.
 */

public class MeiziPresenter implements MeiziContract.Presenter {
    private Context context;
    private MeiziContract.View view;
    private StringModeImpl mode;
    private Gson gson;
    private ArrayList<MeiziNews.Question> list=new ArrayList<>();
    //当前加载页数
    private int CurrentPagerNum;

    private DatabaseHelper dbHelper;

    public MeiziPresenter(Context context,MeiziContract.View view){
        this.context=context;
        this.view=view;
        this.view.setPresenter(this);
        mode=new StringModeImpl(context);
    }

    @Override
    public void loadPosts(int PagerNum, boolean cleaing) {

    }

    @Override
    public void reflush() {

    }

    @Override
    public void loadMore(int PagerNum) {

    }

    @Override
    public void StartReading(int positon) {

    }

    @Override
    public void LookAround() {

    }

    @Override
    public void start() {

    }
}
