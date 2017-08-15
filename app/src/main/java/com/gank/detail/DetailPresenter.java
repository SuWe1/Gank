package com.gank.detail;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.gank.R;
import com.gank.app.App;
import com.gank.bean.BeanTeype;
import com.gank.bean.FrontNews;
import com.gank.bean.GankNews;
import com.gank.bean.IosNews;
import com.gank.interfaze.MyQQListener;
import com.gank.model.ShareSingleton;
import com.gank.model.StringModeImpl;
import com.gank.util.Network;
import com.google.gson.Gson;
import com.litesuits.orm.log.OrmLog;
import com.tencent.connect.share.QQShare;

/**
 * Created by 11033 on 2017/3/5.
 */

public class DetailPresenter implements  DetailContract.Presenter {
    private static final String TAG = "DetailPresenter";
    private DetailContract.View view;
    private StringModeImpl model;
    private Context context;


    private SharedPreferences sp;

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
        gson=new Gson();
    }

    @Override
    public void openInBrower() {
        try {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            switch (type){
                case TYPE_Gank:
                    intent.setData(Uri.parse(url));
                    break;
                case TYPE_Front:
                    intent.setData(Uri.parse(url));
                    break;
                case TYPE_IOS:
                    intent.setData(Uri.parse(url));
                    break;
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
            case TYPE_IOS:
                tmpTable="IOS";
                tmpID="ios_id";
                IosNews.Question ios=App.DbLiteOrm.queryById(id,IosNews.Question.class);
                if (queryIsBooksMarks()){
                    view.showDeletedFromBookmarks();
                    ios.mark=false;
                }else {
                    view.showAddedToBookmarks();
                    ios.mark=true;
                }
                App.DbLiteOrm.update(ios);
        }
//        Log.i(TAG, "addToOrDeleteFromBookMarks: tmpTable:"+tmpTable+" tmpID:"+tmpID+" _id:"+ _id +" queryIsBooksMarks():"+queryIsBooksMarks());
    }

    @Override
    public boolean queryIsBooksMarks() {
        if (_id ==null || type==null){
            view.showLoadingError();
            return false;
        }
        //true为已经收藏 false未收藏
        switch (type){
            case TYPE_Gank:
                GankNews.Question gank= App.DbLiteOrm.queryById(id,GankNews.Question.class);
                OrmLog.i(TAG,gank);
                boolean isMark=gank.mark;
                if (isMark){
                    return true;
                }else {
                    return false;
                }
//                return  true;
            case  TYPE_Front:
                FrontNews.Question front=App.DbLiteOrm.queryById(id,FrontNews.Question.class);
                if (front.mark){
                    return true;
                }else {
                    return false;
                }
            case TYPE_IOS:
                Log.i(TAG, "queryIsBooksMarks: "+id);
                IosNews.Question ios=App.DbLiteOrm.queryById(id,IosNews.Question.class);
                OrmLog.i(TAG,ios);
                /*if (ios==null){
                    Log.i(TAG, "queryIsBooksMarks:ios==null ");
                    break;
                }*/
                if (ios.mark){
                    return true;
                }else {
                    return false;
                }
        }
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
                break;
            case TYPE_IOS:
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
    public void shareArticleToQQ(MyQQListener listener) {
        //title == desc
        if (TextUtils.isEmpty(imgUrl)){
            ShareSingleton.getInstance().shareToQQ((Activity) context,url,"推荐给你一篇文章",title, R.string.app_name, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE,listener);
        }else {
            ShareSingleton.getInstance().shareToQQ((Activity) context,url,"推荐给你一篇文章",title,imgUrl,R.string.app_name, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE,listener);
        }
    }

    @Override
    public void shareArticleToWx() {
        //title == desc
        ShareSingleton.getInstance().shareWebToWx(url,"",title,true);
    }

    @Override
    public void shareArticleToWxCommunity() {
        //title == desc
        ShareSingleton.getInstance().shareWebToWx(url,"",title,false);
    }

    @Override
    public void shareArticleToWxCollect() {
        //title == desc
        ShareSingleton.getInstance().shareWebToWxCollect(url,"干货",title);
    }

    @Override
    public void start() {

    }

//    private boolean checkNull() {
//        return (type == BeanTeype.TYPE_Gank && zhihuDailyStory == null);
//    }
}
