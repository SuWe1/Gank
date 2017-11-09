package com.gank.picture;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

/**
 * Created by Swy on 2017/3/22.
 */

public class PicturePresenter implements PictureContract.Presenter {
    private Context context;
    private PictureContract.View view;

    //权限
    private String [] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};

    //要分享的图片 保存在本地的资源
    private Bitmap shareBitmap;
    //要分享的图片 保存在本地的路径
    private volatile String sharePath;

    private boolean isShareWxOrCommunity=false;//false wx  true wxCommunity
    private boolean isShareQQ=false;//false   true

    private MyQQListener qqShareListener;

    private static final int SHARE_IMG_IS_READY = 200;
    private static final int SHARE_IMG_PATH_IS_READY = 300;
    private static final int SHARE_PIC_TO_WX_COMMUNITY =10;
    private static final int SHARE_PIC_TO_WX = 11;
    private static final int SHARE_PIC_TO_QQ = 12;

    public static final int IMAGE_SIZE=32768;//微信分享图片大小限制


    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATUS = 2;
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
        sharePath=file.getPath();//要分享到QQ的图片地址
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
            ActivityCompat.requestPermissions((Activity) context,permissions,
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    //isShareFriend true 分享到朋友，false分享到朋友圈
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHARE_PIC_TO_WX_COMMUNITY:
                    if(shareBitmap!=null){
                        ShareSingleton.getInstance().shareImgToWx(shareBitmap,false);
                    }else {
                        throw new  RuntimeException("The picture that  want to share cannot be empty!");
                    }
                    break;
                case SHARE_PIC_TO_WX:
                    if(shareBitmap!=null){
                        ShareSingleton.getInstance().shareImgToWx(shareBitmap,true);
                    }else {
                        throw new  RuntimeException("The picture that  want to share cannot be empty!");
                    }
                    break;
                case SHARE_PIC_TO_QQ:
                    if (sharePath!=null){
                        ShareSingleton.getInstance().shareLocalImgToQQ((Activity) context,sharePath, R.string.app_name, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE,qqShareListener);
                    }else {
                        throw new  RuntimeException("The picture path is not exist!");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void sharePicToQQ(final String imgUrl, final MyQQListener listener) {
        isShareQQ=true;
        qqShareListener=listener;
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.save_picture_to_local_title)
                .setMessage(R.string.save_picture_to_local_message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        view.shareCancel();
                    }
                })
                .setPositiveButton(R.string.agreement, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SavePicToLocal(imgUrl);
                        //要在主线程中
                        new ImgAsyncTask(context).execute(imgUrl);
                    }
                })
                .show();
    }

    //isShareFriend true 分享到朋友，false分享到朋友圈
    //注意AsyncTask要在主线程中执行
    /**
     * Issue
     * String result = new AsyncTask().execute(...).get()
     这种方法会不是异步方式，会出现ANR
     * @param imgUrl
     */
    @Override
    public void sharePicToWx(final String imgUrl){
        //从Glide缓存中获取Bitmap
        isShareWxOrCommunity=false;
        isShareQQ=false;
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED){
            new ImgAsyncTask(context).execute(imgUrl);
        }else {
            ActivityCompat.requestPermissions((Activity) context,permissions,MY_PERMISSIONS_REQUEST_READ_PHONE_STATUS);
        }
    }

    //isShareFriend true 分享到朋友，false分享到朋友圈
    //注意AsyncTask要在主线程中执行
    @Override
    public void sharePicToWxCommunity(final String imgUrl) {
        //从Glide缓存中获取Bitmap
        isShareWxOrCommunity=true;
        isShareQQ=false;
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED){
            new ImgAsyncTask(context).execute(imgUrl);
        }else {
            ActivityCompat.requestPermissions((Activity) context,permissions,MY_PERMISSIONS_REQUEST_READ_PHONE_STATUS);
        }
    }


    //glide downloadOnly方法需要在线程中执行
    /**
     * string 图片url
     * void
     * string 本地图片路径
     */
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
                return BitmapFactory.decodeFile(file.getPath());
            } catch (InterruptedException | ExecutionException e) {
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
            shareBitmap=bitmap;
            //false分享微信好友  true分享朋友圈
            if (isShareWxOrCommunity){
                handler.sendMessage(handler.obtainMessage(SHARE_PIC_TO_WX_COMMUNITY));
            }else {
                handler.sendMessage(handler.obtainMessage(SHARE_PIC_TO_WX));
            }
            //true 分享到扣扣
            if (isShareQQ){
                handler.sendMessage(handler.obtainMessage(SHARE_PIC_TO_QQ));
            }

        }
    }

    /**
     * 微信分享，分享图片大小不能大于32kb
     * @param bmp
     * @return
     */
    private Bitmap compressBitmap(Bitmap bmp){
        // 首先进行一次大范围的压缩

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        float zoom = (float)Math.sqrt(IMAGE_SIZE / (float)output.toByteArray().length); //获取缩放比例

        // 设置矩阵数据
        Matrix matrix = new Matrix();
        matrix.setScale(zoom, zoom);

        // 根据矩阵数据进行新bitmap的创建
        Bitmap resultBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        output.reset();

        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);

        // 如果进行了上面的压缩后，依旧大于32K，就进行小范围的微调压缩
        while(output.toByteArray().length > IMAGE_SIZE){
            matrix.setScale(0.9f, 0.9f);//每次缩小 1/10

            resultBitmap = Bitmap.createBitmap(resultBitmap, 0, 0,resultBitmap.getWidth(), resultBitmap.getHeight(), matrix,true);
            Log.i(TAG, "onPostExecute:resultBitmap.size= "+resultBitmap.getByteCount());
            output.reset();
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        }
        return resultBitmap;
    }
}
