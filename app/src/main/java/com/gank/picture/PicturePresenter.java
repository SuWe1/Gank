package com.gank.picture;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;

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
    private static final int THUMB_SIZE = 0;
    private Context context;
    private PictureContract.View view;

    private Bitmap shareBitmap;

    private ShareSingleton shareSingleton;

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
    public void SavePicTolocal(final String url) {
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

    Handler handler=new Handler(Looper.getMainLooper());

    @Override
    public void sharePicToQQ(final String imgUrl, final MyQQListener listener) {
        shareSingleton=ShareSingleton.getInstance();
        Log.i(TAG, "sharePicToQQ:imgUrl= "+imgUrl);
        handler.post(new Runnable() {
            @Override
            public void run() {
                //要在主线程中
                shareSingleton.shareImgToQQ((Activity) context,imgUrl, R.string.app_name, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN,listener);
            }
        });

    }

    @Override
    public void sharePicToWx(final String imgUrl) {
        //从Glide缓存中获取Bitmap
        new ImgAsyncTask(context).execute(imgUrl);
    }


    private void shareWx(Bitmap bmp, boolean isShareFriend){
        //初始化WXImageObject和WXMediaMessage对象
        WXImageObject imgObj=new WXImageObject(bmp);
        WXMediaMessage bitmapMsg=new WXMediaMessage();
        bitmapMsg.mediaObject=imgObj;

        //设置缩略图
        Bitmap thumbBmp=Bitmap.createScaledBitmap(bmp,THUMB_SIZE,THUMB_SIZE,true);
        bmp.recycle();
//        bitmapMsg.thumbData=

    }

    //glide downloadOnly方法需要在线程中执行
    private class ImgAsyncTask extends AsyncTask<String,Void,Bitmap>{

        private Context context;

        public ImgAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                File file= Glide.with(context).load(params[0]).downloadOnly(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap==null){
                shareBitmap=null;
            }
            shareBitmap =bitmap;
        }
    }

}
