package com.gank.picture;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.gank.R;
import com.gank.interfaze.MyQQListener;
import com.gank.model.ShareSingleton;
import com.gank.util.DataForString;
import com.tencent.connect.share.QQShare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

/**
 * Created by 11033 on 2017/3/22.
 */

public class PicturePresenter implements PictureContract.Presenter {
    private Context context;
    private PictureContract.View view;

    //要分享的图片 保存在本地的资源
    private Bitmap shareBitmap;
    //要分享的图片 保存在本地的路径
    private volatile String sharePath;

    private static final int SHARE_IMG_IS_READY = 200;
    private static final int SHARE_IMG_PATH_IS_READY = 300;
    private static final int SHARE_PIC_TO_WX_COMMUNITY =10;
    private static final int SHARE_PIC_TO_WX = 11;


    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    public PicturePresenter(Context context,PictureContract.View view) {
        this.context = context;
        this.view=view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void LoadPic(String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    @Override
    public void SavePicToLocal(final String url) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (resource!=null){
                            savBitmap(resource);
                        }
                    }
                });
    }




    /**
     * 不要忘记声明读写权限
     * 在6.0之前，写入sd卡权限只需在清单文件中添加  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>即可，
     * 而在6.0中，则需要在activity中用代码来请求一些敏感的权限，其中就包括对sd卡的操作权限
     * 如果你申请某个危险的权限，假设你的app早已被用户授权了同一组的某个危险权限，那么系统会立即授权，而不需要用户去点击授权(比如申请了写就可以读)
     * @param bitmap
     */
    private void savBitmap(Bitmap bitmap){
        File sd=Environment.getExternalStorageDirectory();
        Log.i(TAG, "sd: "+sd.canWrite());
        String filename= DataForString.GetTimeToName()+".jpg";
        File appDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.i(TAG, "appDis.path: "+appDir.getAbsolutePath()+"   filename: "+filename);
        File file=new File(appDir,filename);
        Log.i(TAG, "file: "+file.canWrite());
        FileOutputStream fos=null;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            // Permission Granted
            try {
                appDir.mkdirs();
                fos=new FileOutputStream(file);
                            /*
                             * @param format   The format of the compressed image
                             * @param quality  Hint to the compressor, 0-100. 0 meaning compress for
                             *                 small size, 100 meaning compress for max quality. Some
                             *                 formats, like PNG which is lossless, will ignore the
                             *                 quality setting
                             * @param stream   The outputstream to write the compressed data.
                             */
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.flush();
                // 最后通知图库更新
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(new File(file.getPath()))));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                view.showSaveFail();
                Log.i(TAG, "FileNotFoundException: "+e.getLocalizedMessage());
            } catch (IOException e) {
                e.printStackTrace();
                view.showSaveFail();
                Log.i(TAG, "IOException: "+e.getLocalizedMessage());
            } finally {
                if (fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            view.showSaveSuccessful(appDir.getAbsolutePath());
        }else {
            // Permission Denied
            //申请权限
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
    }

    //isShareFriend true 分享到朋友，false分享到朋友圈
    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHARE_PIC_TO_WX_COMMUNITY:
                    if(shareBitmap!=null){
                        ShareSingleton.getInstance().shareImgToWx(context,shareBitmap,false);
                    }else {
                        throw new  RuntimeException("The picture that  want to share cannot be empty!");
                    }
                    break;
                case SHARE_PIC_TO_WX:

                    if(shareBitmap!=null){
                        ShareSingleton.getInstance().shareImgToWx(context,shareBitmap,true);
                    }else {
                        throw new  RuntimeException("The picture that  want to share cannot be empty!");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void sharePicToQQ(final String imgUrl, final MyQQListener listener) {
        Log.i(TAG, "sharePicToQQ:imgUrl= "+imgUrl);
        //要在主线程中
        new ImgAsyncTask(context).execute(imgUrl);
        String path=sharePath.concat(".jpg");
        Log.i(TAG, "run: "+path);
        ShareSingleton.getInstance().shareLocalImgToQQ((Activity) context,path, R.string.app_name, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN,listener);
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
    }

    //isShareFriend true 分享到朋友，false分享到朋友圈
    //注意AsyncTask要在主线程中执行
    @Override
    public void sharePicToWx(final String imgUrl) {
        //从Glide缓存中获取Bitmap
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
        try {
            sharePath=new ImgAsyncTask(context).execute(imgUrl).get();
            if (!TextUtils.isEmpty(sharePath)){
                shareBitmap= BitmapFactory.decodeFile(sharePath);
                handler.sendMessage(handler.obtainMessage(SHARE_PIC_TO_WX));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    //isShareFriend true 分享到朋友，false分享到朋友圈
    //注意AsyncTask要在主线程中执行
    @Override
    public void sharePicToWxCommunity(final String imgUrl) {
        //从Glide缓存中获取Bitmap
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    sharePath=new ImgAsyncTask(context).execute(imgUrl).get();
                    if (!TextUtils.isEmpty(sharePath)){
                        shareBitmap= BitmapFactory.decodeFile(sharePath);
                        handler.sendMessage(handler.obtainMessage(SHARE_PIC_TO_WX_COMMUNITY));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //glide downloadOnly方法需要在线程中执行
    /**
     * string 图片url
     * void
     * string 本地图片路径
     */
    private class ImgAsyncTask extends AsyncTask<String,Void,String>{

        private Context context;

        public ImgAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                File file= Glide.with(context).load(params[0]).downloadOnly(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL).get();
                return file.getAbsolutePath();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            if (path==null){
                shareBitmap=null;
            }
//            sharePath=path;
//            shareBitmap = BitmapFactory.decodeFile(path);
//            Log.i(TAG, "sharePicToQQ:sharePath= "+sharePath);
//            handler.sendMessage(handler.obtainMessage(SHARE_IMG_PTH_IS_READY));
//            handler.sendMessage(handler.obtainMessage(SHARE_IMG_IS_READY));

        }
    }

}
