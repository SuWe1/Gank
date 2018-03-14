package com.example.searchview.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.sql.SQLData;
import java.util.UnknownFormatConversionException;

/**
 * Created by Swy on 2018/1/28.
 */

public class HistoryProvider extends ContentProvider {

    private static final int SEARCH_HISTORY = 100;
    private static final int SEARCH_HISTORY_DATE = 101;
    private static final int SEARCH_HISTORY_ID = 102;
    private static final int SEARCH_HISTORY_IS_HISTORY = 103;

    private static final UriMatcher mUriMatcher=buildUriMatcher();

    private HistoryDbHelper dbHelper;

    /**
     * uri匹配 返回对应code码  uri格式：content:.// + authority+path+id
     *
     --常量 UriMatcher.NO_MATCH 表示不匹配任何路径的返回码

     --# 号为通配符

     --* 号为任意字符


     ContentUris 类通过ID: parseId(uri)获取Uri路径后面的ID部分或者通过withAppendedId方法，为该Uri加上ID
     * @return
     */
    public static UriMatcher buildUriMatcher(){
        String content= HistoryContract.CONTENT_AUTHORITY;
        UriMatcher  matcher=new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content,HistoryContract.PATH_HISTORY,SEARCH_HISTORY);
        matcher.addURI(content, HistoryContract.PATH_HISTORY + "/#", SEARCH_HISTORY_DATE);
        matcher.addURI(content, HistoryContract.PATH_HISTORY + "/#", SEARCH_HISTORY_ID);
        matcher.addURI(content, HistoryContract.PATH_HISTORY + "/#", SEARCH_HISTORY_IS_HISTORY);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper=new HistoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor mCursor;
        switch (mUriMatcher.match(uri)){
            case SEARCH_HISTORY:
                mCursor=db.query(HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case  SEARCH_HISTORY_DATE:
                long date= ContentUris.parseId(uri);
                mCursor=db.query(HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        HistoryContract.HistoryEntry.COLUMN_INSERT_DATE+" =?",
                        new String[]{String.valueOf(date)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SEARCH_HISTORY_ID:
                long id= ContentUris.parseId(uri);
                mCursor=db.query(HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        HistoryContract.HistoryEntry.COLUMN_INSERT_DATE+" =?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case SEARCH_HISTORY_IS_HISTORY:
                long flag = ContentUris.parseId(uri);
                mCursor = db.query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                        new String[]{String.valueOf(flag)},
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        Context context=getContext();
        if (context!=null){
            //cursor自动更新
            mCursor.setNotificationUri(context.getContentResolver(),uri);
        }
        return mCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case SEARCH_HISTORY:
                return HistoryContract.HistoryEntry.CONTENT_TYPE;
            case SEARCH_HISTORY_DATE:
            case SEARCH_HISTORY_ID:
            case SEARCH_HISTORY_IS_HISTORY:
                return HistoryContract.HistoryEntry.CONTENT_ITEM;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long _id;
        Uri retUri;
        switch (mUriMatcher.match(uri)){
            case SEARCH_HISTORY:
                _id=db.insert(HistoryContract.HistoryEntry.TABLE_NAME,null,values);
                if (_id>0){
                   retUri= HistoryContract.HistoryEntry.buildHistoryUri(_id);
                }else {
                    throw  new UnsupportedOperationException("Unable to insert rows into "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        return retUri;       -9
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        // Number of rows effected.
        int rows;
        switch (mUriMatcher.match(uri)){
            case SEARCH_HISTORY:
                rows=db.delete(HistoryContract.HistoryEntry.TABLE_NAME,
                       selection,selectionArgs
                );
                break;
            default:
                throw new UnknownFormatConversionException("Unknown Uri"+uri);
        }
        if (selection == null || rows !=0){
            Context context=getContext();
            if (context!=null){
                context.getContentResolver().notifyChange(uri,null);
            }
        }
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        // Number of rows effected.
        int rows;
        switch (mUriMatcher.match(uri)){
            case SEARCH_HISTORY:
                rows=db.update(HistoryContract.HistoryEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnknownFormatConversionException("Unknown Uri "+uri);
        }
        if (rows!=0){
            Context context=getContext();
            if (context!=null){
                context.getContentResolver().notifyChange(uri,null);
            }
        }
        return rows;
    }
}
