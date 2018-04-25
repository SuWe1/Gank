package com.gank.bean;

/**
 * Created by Swy on 2018/4/25.
 */

public class BaseBean {
    public int newsDetailType;
    //新闻类型
    public BeanTeype beanTeype;

    public BaseBean(int newsDetailType) {
        this.newsDetailType = newsDetailType;
    }

    public BaseBean() {
    }
}
