package com.gank.model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Swy on 2017/3/4.
 */

/**
 * 关于Volley的用法 推荐郭神的博客 http://blog.csdn.net/guolin_blog/article/details/17482095
 */
public class VolleySingleton {
    public static VolleySingleton volleySingletonl;
    private RequestQueue requestQueue;

    public VolleySingleton(Context context) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    //懒汉
    public static synchronized VolleySingleton getVolleySingletonl(Context context) {
        if (volleySingletonl==null){
            volleySingletonl=new VolleySingleton(context);
        }
        return volleySingletonl;
    }

    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
