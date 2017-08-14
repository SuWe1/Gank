package com.gank.detail;

import com.gank.BasePresenter;
import com.gank.BaseView;
import com.gank.interfaze.MyQQListener;

/**
 * Created by 11033 on 2017/3/5.
 */

public class DetailContract {
    interface Presenter extends BasePresenter{
        /**
         * 流浪器中打开
         * 复制文本
         * 复制连接
         * 添加收藏或取消收藏
         * 查询是否收藏
         * 请求数据
         */
        void openInBrower();
        void copyText();
        void copyLink();
        void addToOrDeleteFromBookMarks();
        boolean queryIsBooksMarks();
        void requestData();
        void shareArticleToQQ(final MyQQListener listener);
        void shareArticleToWx();
        void shareArticleToWxCommunity();
    }
    interface View extends BaseView<Presenter> {
        // 显示正在加载
        void showLoading();
        // 停止加载
        void stopLoading();
        // 显示加载错误
        void showLoadingError();
        // 显示分享时错误
        void showSharingError();
        // 正确获取数据后显示内容
//        void showResult(String result);
//        // 对于body字段的消息，直接接在url的内容
        void showResultWithoutBody(String url);
        // 设置顶部大图
        void showCover(String url);
        // 设置标题
        void setTitle(String title);
        // 设置是否显示图片
        void setImageMode(boolean showImage);
        // 用户选择在浏览器中打开时，如果没有安装浏览器，显示没有找到浏览器错误
        void showBrowserNotFoundError();
        // 显示已复制文字内容
        void showTextCopied();
        // 显示文字复制失败
        void showCopyTextError();
        // 显示已添加至收藏夹
        void showAddedToBookmarks();
        // 显示已从收藏夹中移除
        void showDeletedFromBookmarks();
        void  showNotNetError();

        void shareSuccess();
        void shareError();
        void shareCancel();
    }
}
