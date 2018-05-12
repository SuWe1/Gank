package com.gank.mark;

import com.gank.BasePresenter;
import com.gank.BaseView;
import com.gank.bean.BaseBean;
import com.gank.bean.BeanTeype;
import com.gank.bean.FrontNews;
import com.gank.bean.GankNews;
import com.gank.bean.IosNews;

import java.util.ArrayList;

/**
 * Created by SWy on 2017/3/4.
 */

public interface BookmarksContract {
    interface Presenter extends BasePresenter {
        //请求结果
        void loadResults(boolean reflesh);

        //跳转到详情页
        void startReading(int position);

        //请求新数据 数据加载到列表中
        void checkForFreshData();

        void lookAround();
    }

    interface View extends BaseView<Presenter> {
        //显示结果
        void showResults(ArrayList<BaseBean> newsList);

        //刷新数据变化
        void notifyDataChanged();

        void showLoading();

        void stopLoading();
    }
}
