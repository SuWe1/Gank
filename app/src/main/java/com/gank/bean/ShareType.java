package com.gank.bean;

/**
 * Created by Swy on 2017/8/10.
 */

public enum ShareType {
    TYPE_WX(1,"微信"),
    TYPE_QQ(2,"QQ"),
    TYPE_WX_FRIEND_COMMUNITY(3,"朋友圈");

    private int key;
    private String value;

    ShareType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
