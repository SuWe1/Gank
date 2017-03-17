package com.gank.mainpager;

import com.gank.BasePresenter;
import com.gank.BaseView;
import com.gank.bean.GankNews;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/15.
 */

public interface MeiziContract {
    interface View extends BaseView<GankContract.Presenter> {
        void showError();
        void showLoading();
        void Stoploading();
        void showResult(ArrayList<GankNews.Question> list);
        void showNotNetError();

    }
    interface Presenter extends BasePresenter {
        // 请求数据
        void loadPosts(int PagerNum, boolean cleaing);
        //刷新数据
        void  reflush();
        //加载更多
        void loadMore(int PagerNum);
        //显示详情
        void StartReading(int positon);
        //随便看
        void LookAround();
    }
}
