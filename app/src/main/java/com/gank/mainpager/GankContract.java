package com.gank.mainpager;

import com.gank.BasePresenter;
import com.gank.BaseView;
import com.gank.bean.GankNews;

import java.util.ArrayList;

/**
 * Created by Swy on 2017/3/4.
 */

public interface GankContract {
    interface View extends BaseView<Presenter>{
        //错误
        void showError();
        //正在加载
        void showLoading();
        //停止加载
        void Stoploading();
        //显示数据列表
        void showResult(ArrayList<GankNews.Question> list);
        //网络错误
        void showNotNetError();

    }
    interface Presenter extends BasePresenter{
        // 请求数据
        void loadPosts(int PagerNum, boolean cleaing);
        //刷新数据
        void  reflush();
        //加载更多
        void loadMore(int PagerNum);
        //显示详情
        void StartReading(int positon);
        //随便看看
        void LookAround();
    }
}
