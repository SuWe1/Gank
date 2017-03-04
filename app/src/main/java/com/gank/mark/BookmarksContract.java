package com.gank.mark;

import com.gank.BasePresenter;
import com.gank.BaseView;
import com.gank.bean.BeanTeype;
import com.gank.bean.GankNews;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/4.
 */

public interface BookmarksContract {
    interface Presenter extends BasePresenter{
        void  loadResults(boolean reflesh);
        void startReading(BeanTeype type,int position);
        void checkForFreshData();
        void lookAround();
    }

    interface View extends BaseView<Presenter>{
        void showResults(ArrayList<GankNews> ganklist);
        void notifyDataChanged();
        void showLoading();
        void stopLoading();
    }
}
