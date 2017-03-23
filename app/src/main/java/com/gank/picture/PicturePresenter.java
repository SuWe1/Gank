package com.gank.picture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gank.util.DataForString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by 11033 on 2017/3/22.
 */

public class PicturePresenter implements PictureContract.Presenter {
    private Context context;
    private PictureContract.View view;
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
     * @param bitmap
     */
    private void savBitmap(Bitmap bitmap){
        String filename= DataForString.GetTimeToName()+".jpg";
        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.i(TAG, "appDis.path: "+filepath.getAbsolutePath()+"   filename: "+filename);
        File file=null;
        FileOutputStream fos=null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            try {
                if (!filepath.exists()){
                    filepath.mkdirs();
                    file.createNewFile();
                }
                file=new File(filepath+".jpg",filename);
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
                Log.i(TAG, "FileNotFoundException: "+e.getLocalizedMessage());
            } catch (IOException e) {
                e.printStackTrace();
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
        }else {
            view.showSavaFail();
        }
        view.showSaveSuccessful(filepath.getAbsolutePath());
    }
}
