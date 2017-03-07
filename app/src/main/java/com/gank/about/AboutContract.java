package com.gank.about;

import com.gank.BasePresenter;
import com.gank.BaseView;

/**
 * Created by 11033 on 2017/3/7.
 */

public interface AboutContract {
    interface  Presenter extends BasePresenter{
        //提交bug
        void commitBug();
        //小彩蛋
        void showEasterEgg();
    }
    interface  View extends BaseView<Presenter>{
        void showFeedbackError();
    }
}
