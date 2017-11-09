package com.gank.picture;

import android.widget.ImageView;

import com.gank.BasePresenter;
import com.gank.BaseView;
import com.gank.interfaze.MyQQListener;

/**
 * Created by Swy on 2017/3/22.
 */

public interface PictureContract {
    interface Presenter extends BasePresenter{
        void LoadPic(String url, ImageView imageView);
        void SavePicToLocal(String url);
        void sharePicToQQ(final String imgUrl,final MyQQListener listener);
        void sharePicToWx(final String imgUrl);
        void sharePicToWxCommunity(final String imgUrl);
    }
    interface  View extends BaseView<Presenter>{
        void showResult();
        void showSaveSuccessful(String path);
        void showSaveFail();
        void showNoPermission();
        void shareSuccess();
        void shareError();
        void shareCancel();
    }
}
