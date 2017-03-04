package com.gank;

/**
 * Created by 11033 on 2017/3/4.
 */

public interface BasePresenter {
    // 获取数据并改变界面显示，在todo-mvp的项目中的调用时机为Fragment的OnResume()方法中
    void start();
}
