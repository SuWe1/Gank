package com.example.searchview.db;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.lang.reflect.Field;
import java.security.Key;

/**
 * Created by Swy on 2018/1/28.
 */

public class HistoryContract {
    public static final String CONTENT_AUTHORITY =initAuthority();

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_HISTORY="history";

    private static String initAuthority() {
        String authority = "com.gank.app.materialsearchview.searchhistorydatabase";

       try {
           ClassLoader classLoader=HistoryContract.class.getClassLoader();
           Class<?> clazz =classLoader.loadClass("com.gank.app.MsvAuthority");
           Field field=clazz.getDeclaredField("CONTENT_AUTHORITY");
           authority=field.get(null).toString();
       }catch (ClassNotFoundException e){
           e.printStackTrace();
       } catch (IllegalAccessException e) {
           e.printStackTrace();
       } catch (NoSuchFieldException e) {
           e.printStackTrace();
       }
        return authority;
    }

    public static final class HistoryEntry implements BaseColumns {
        // Content provider stuff.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_HISTORY;

        public static final String CONTENT_ITEM =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_HISTORY;

        /**
         * 定义表名
         */
        public static final String TABLE_NAME = "SEARCH_HISTORY";
        /**
         * 定义列名
         */
        public static final String COLUMN_QUERY = "query";
        public static final String COLUMN_INSERT_DATE = "insert_date";
        /**
         * 0 不是 1 是
         */
        public static final String COLUMN_IS_HISTORY = "is_history";

        public static Uri buildHistoryUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
