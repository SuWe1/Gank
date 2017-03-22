package com.gank.picture;

import android.widget.ImageView;

import com.gank.BasePresenter;
import com.gank.BaseView;

/**
 * Created by 11033 on 2017/3/22.
 */

public interface PictureContract {
    interface Presenter extends BasePresenter{
        void LoadPic(String url, ImageView imageView);
        void SavePicTolocal(String url);
    }
    interface  View extends BaseView<Presenter>{
        void showResult();
        void showSaveSuccessful();
        void showSavaFail();
    }
}
