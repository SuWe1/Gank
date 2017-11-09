package com.gank.meizi;

import com.gank.BasePresenter;
import com.gank.BaseView;
import com.gank.bean.MeiziNews;

import java.util.ArrayList;

/**
 * Created by Swy on 2017/3/15.
 */

public interface MeiziContract {
    interface View extends BaseView<MeiziContract.Presenter> {
        void showError();
        void showLoading();
        void Stoploading();
        void showResult(ArrayList<MeiziNews.Question> list);
        void showNotNetError();
        void notifyDataChanged();
    }
    interface Presenter extends BasePresenter {
        // 请求数据
        void loadPosts(int PagerNum, boolean cleaing);
        //刷新数据
        void  reflush();
        //加载更多
        void loadMore(int PagerNum);
        //显示详情
        void StartReading(int positon, android.view.View transitView);
        //随便看
        void LookAround();
    }
}
