package com.gank.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gank.util.DataForString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
    public void SavePicTolocal(String url) {
        Bitmap bitmap = null;
        File photoDir=new File(Environment.getExternalStorageDirectory(),"Meizi");
        if (!photoDir.exists()){
            photoDir.mkdirs();
        }
        String filename= DataForString.GetTimeToName()+".jpg";
        File file=new File(photoDir,filename);
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            if (bitmap!=null){
                /**
                 * @param format   The format of the compressed image
                 * @param quality  Hint to the compressor, 0-100. 0 meaning compress for
                 *                 small size, 100 meaning compress for max quality. Some
                 *                 formats, like PNG which is lossless, will ignore the
                 *                 quality setting
                 * @param stream   The outputstream to write the compressed data.
                 */
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
