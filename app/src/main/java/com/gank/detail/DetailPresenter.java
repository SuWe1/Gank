package com.gank.detail;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.gank.app.App;
import com.gank.bean.BeanTeype;
import com.gank.bean.FrontNews;
import com.gank.bean.GankNews;
import com.gank.bean.StringModeImpl;
import com.gank.util.Network;
import com.google.gson.Gson;
import com.litesuits.orm.log.OrmLog;

/**
 * Created by 11033 on 2017/3/5.
 */

public class DetailPresenter implements  DetailContract.Presenter {
    private static final String TAG = "DetailPresenter";
    private DetailContract.View view;
    private StringModeImpl model;
    private Context context;


    private SharedPreferences sp;
//    private DatabaseHelper dbHelper;

    private Gson gson;

    //从acticity提供来的数据
    private BeanTeype type;
    private int id;
    private String _id;
    //标题
    private String title;
    //文章链接
    private String url;
    //图片链接
    private String imgUrl;

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void set_id(String _id) {
        this._id = _id;
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
//        dbHelper=new DatabaseHelper(context,"Histroy.db",null,9);
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
                GankNews.Question gank= App.DbLiteOrm.queryById(id,GankNews.Question.class);
                if (queryIsBooksMarks()){
                    view.showDeletedFromBookmarks();
                    gank.mark=false;
                }else {
                    view.showAddedToBookmarks();
                    gank.mark=true;
                }
                App.DbLiteOrm.update(gank);
                break;
            case TYPE_Front:
                tmpTable="Front";
                tmpID="front_id";
                FrontNews.Question front=App.DbLiteOrm.queryById(id,FrontNews.Question.class);
                if (queryIsBooksMarks()){
                    view.showDeletedFromBookmarks();
                    front.mark=false;
                }else {
                    view.showAddedToBookmarks();
                    front.mark=true;
                }
                App.DbLiteOrm.update(front);
                break;
        }
        Log.i(TAG, "addToOrDeleteFromBookMarks: tmpTable:"+tmpTable+" tmpID:"+tmpID+" _id:"+ _id +" queryIsBooksMarks():"+queryIsBooksMarks());
        /*if (queryIsBooksMarks()){
            //从收藏列表删除
            ContentValues values=new ContentValues();
            values.put("bookmark",0);
            dbHelper.getWritableDatabase().update(tmpTable,values,tmpID+" = ? ",new String[]{String.valueOf(_id)});
            values.clear();
            view.showDeletedFromBookmarks();
        }else {
            //添加到收藏
            ContentValues values=new ContentValues();
            values.put("bookmark",1);
            dbHelper.getWritableDatabase().update(tmpTable,values,tmpID+" = ? ",new String[]{String.valueOf(_id)});
            values.clear();
            view.showAddedToBookmarks();
        }*/
    }

    @Override
    public boolean queryIsBooksMarks() {
        if (_id ==null || type==null){
            view.showLoadingError();
            return false;
        }
        //表和id'
        String tempTable = "";
        String tempId = "";
        //true为已经收藏 false未收藏
        switch (type){
            case TYPE_Gank:
                tempTable="Gank";
                tempId="gank_id";
                GankNews.Question gank= App.DbLiteOrm.queryById(id,GankNews.Question.class);
                /*ArrayList<GankNews.Question> list= App.DbLiteOrm.query(new QueryBuilder(GankNews.Question.class).where(GankNews.Question.COL_ID
                ,new String[]{_id}));
                Log.i(TAG, "queryIsBooksMarks: "+list.size());
                GankNews.Question gank=list.get(0);*/
                OrmLog.i(TAG,gank);
                    boolean isMark=gank.mark;
                if (isMark){
                    return true;
                }else {
                    return false;
                }
//                return  true;
            case  TYPE_Front:
                tempTable="Front";
                tempId="front_id";
                FrontNews.Question front=App.DbLiteOrm.queryById(id,FrontNews.Question.class);
                if (front.mark){
                    return true;
                }else {
                    return false;
                }
        }
        //这里SQL语句没写好 卡了我三天的bug啊！！！ 一定要注意空格
//        String sql="select * from "+tempTable+" where "+tempId+" = ? ";
//        Cursor cursor=dbHelper.getReadableDatabase()
//                .rawQuery(sql,new String[]{String.valueOf(_id)});
//        if (cursor.moveToNext()){
//            do {
//                int isBookMarked=cursor.getInt(cursor.getColumnIndex("bookmark"));
//                if (isBookMarked==1){
//                    return true;
//                }
//            }while (cursor.moveToNext());
//        }
//        cursor.close();
        return false;
    }

    @Override
    public void requestData() {
        if (_id ==null || type==null){
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
                    //本是参考网上案列存储详情页数据的 但是详情页都是用webView呈现 所以缓存content为空
                    /*Cursor cursor=dbHelper.getReadableDatabase()
                    .query("Gank",null,null,null,null,null,null);
                if (cursor.moveToNext()){
                    do {
                        if (cursor.getInt(cursor.getColumnIndex("gank_id"))==_id){
                            String content=cursor.getString(cursor.getColumnIndex("gank_content"));
//                                //没有写完
                        }
                    }while (cursor.moveToNext());
                }*/
                    view.stopLoading();
        }
                break;
            case TYPE_Front:
                if (Network.networkConnected(context)){
                    view.showResultWithoutBody(url);
                }else {
                    view.showNotNetError();
                    view.stopLoading();
                }
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
