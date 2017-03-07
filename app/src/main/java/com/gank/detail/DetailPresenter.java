package com.gank.detail;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import com.gank.bean.BeanTeype;
import com.gank.bean.StringModeImpl;
import com.gank.db.DatabaseHelper;
import com.gank.util.Network;
import com.google.gson.Gson;

/**
 * Created by 11033 on 2017/3/5.
 */

public class DetailPresenter implements  DetailContract.Presenter {
    private DetailContract.View view;
    private StringModeImpl model;
    private Context context;


    private SharedPreferences sp;
    private DatabaseHelper dbHelper;

    private Gson gson;

    //从acticity提供来的数据
    private BeanTeype type;
    private String id;
    //标题
    private String title;
    //文章链接
    private String url;
    //图片链接
    private String imgUrl;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(BeanTeype type) {
        this.type = type;
    }

    public DetailPresenter(DetailContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.view.setPresenter(this);
        model=new StringModeImpl(context);
        sp=context.getSharedPreferences("user_settings",Context.MODE_PRIVATE);
        dbHelper=new DatabaseHelper(context,"Histroy.db",null,5);
        gson=new Gson();
    }

    @Override
    public void openInBrower() {
        try {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            switch (type){
                case TYPE_Gank:
                    intent.setData(Uri.parse(url));
            }
            context.startActivity(intent);
        }catch (Exception ex){
            view.showBrowserNotFoundError();
        }

    }

    @Override
    public void copyText() {
        ClipboardManager manager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data=null;
        switch (type){
            case TYPE_Gank:
//                data=ClipData.newPlainText()
        }
        manager.setPrimaryClip(data);
        view.showTextCopied();
    }

    @Override
    public void copyLink() {
        ClipboardManager manager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data=null;
        switch (type){
            case TYPE_Gank:
                data=ClipData.newPlainText("text",url);
        }
        manager.setPrimaryClip(data);
        view.showTextCopied();
    }

    @Override
    public void addToOrDeleteFromBookMarks() {
        //表名和id
        String tmpTable="";
        String tmpID="";
        switch (type){
            case TYPE_Gank:
                tmpTable="Gank";
                tmpID="gank_id";
        }
        if (queryIsBooksMarks()){
            //从收藏列表删除
            ContentValues values=new ContentValues();
            values.put("bookmark",0);
            dbHelper.getWritableDatabase().update(tmpTable,values,tmpID+"=?",new String[]{String.valueOf(id)});
            values.clear();
            view.showDeletedFromBookmarks();
        }else {
            //添加到收藏
            ContentValues values=new ContentValues();
            values.put("bookmark",1);
            dbHelper.getWritableDatabase().update(tmpTable,values,tmpID+"=?",new String[]{String.valueOf(id)});
            values.clear();
            view.showAddedToBookmarks();
        }
    }

    @Override
    public boolean queryIsBooksMarks() {
        if (id==null || type==null){
            view.showLoadingError();
            return false;
        }
        //表和id'
        String tempTable = "";
        String tempId = "";

        switch (type){
            case TYPE_Gank:
                tempTable="Gank";
                tempId="gank_id";
                break;
        }
        //这里SQL语句没写好 卡了我三天的bug啊！！！ 一定要注意空格
        String sql="select * from "+tempTable+" where "+tempId+" =? ";
        Cursor cursor=dbHelper.getReadableDatabase()
                .rawQuery(sql,new String[]{String.valueOf(id)});
        if (cursor.moveToNext()){
            do {
                int isBookMarked=cursor.getInt(cursor.getColumnIndex("bookmark"));
                if (isBookMarked==1){
                    return true;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }

    @Override
    public void requestData() {
        if (id==null || type==null){
            view.showLoadingError();
            return;
        }
        view.showLoading();
        view.setTitle(title);
        view.showCover(imgUrl);

        view.setImageMode(sp.getBoolean("no_picture_mode",false));
        switch (type){
            case TYPE_Gank:
                if (Network.networkConnected(context)){
                    view.showResultWithoutBody(url);
                }else {
                    view.showNotNetError();
                    /*Cursor cursor=dbHelper.getReadableDatabase()
                    .query("Gank",null,null,null,null,null,null);
                if (cursor.moveToNext()){
                    do {
                        if (cursor.getInt(cursor.getColumnIndex("gank_id"))==id){
                            String content=cursor.getString(cursor.getColumnIndex("gank_content"));
//                                //没有写完
                        }
                    }while (cursor.moveToNext());
                }*/
                    view.stopLoading();
        }
                break;
        }
        view.stopLoading();
    }

    @Override
    public void start() {

    }

//    private boolean checkNull() {
//        return (type == BeanTeype.TYPE_Gank && zhihuDailyStory == null);
//    }
}
