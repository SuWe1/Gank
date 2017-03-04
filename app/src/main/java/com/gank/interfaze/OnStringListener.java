package com.gank.interfaze;

import com.android.volley.VolleyError;

/**
 * Created by 11033 on 2017/3/4.
 */

/**
 * Volley请求成功和失败时回调的方法
 */
public interface OnStringListener {
    void  onSuccess(String result);
    void onError(VolleyError error);
}
