package com.gank;

import android.view.View;

/**
 * Created by Swy on 2017/3/4.
 */

public interface BaseView<T> {
    // 为View设置Presenter
    void setPresenter(T presenter);
    // 初始化界面控件
    void initView(View view);
}
