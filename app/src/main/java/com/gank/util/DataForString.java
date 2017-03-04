package com.gank.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 11033 on 2017/3/4.
 * Date类型转化为String类型
 */

public class DataForString {
    public String GankDataFormat(long date){
        String mDate;
        Date d=new Date(date);
        SimpleDateFormat format=new SimpleDateFormat("yy/MM/dd");
        mDate=format.format(d);
        return mDate;
    }
}
