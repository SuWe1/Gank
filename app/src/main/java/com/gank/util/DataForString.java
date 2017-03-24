package com.gank.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 11033 on 2017/3/4.
 * Date类型转化为String类型
 */

public class DataForString {
    public static String GetTimeToName(){
        String mDate;
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        //获取当前时间
        Date d=new Date(System.currentTimeMillis());
        mDate=format.format(d);
        return mDate;
    }
}
