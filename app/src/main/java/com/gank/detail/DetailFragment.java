package com.gank.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gank.R;
import com.gank.interfaze.MyQQListener;
import com.tencent.tauth.UiError;

/**
 * Created by Swy on 2017/3/5.
 */

public class DetailFragment extends Fragment implements DetailContract.View {

    private static final String TAG = "DetailFragment";
    private ImageView imageView;
    private WebView webview;
    private NestedScrollView scrollView;
    private CollapsingToolbarLayout toolbarLayout;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout refreshLayout;

    private Context context;
    private DetailContract.Presenter presenter;


    public DetailFragment() {
    }

    public static DetailFragment newInstance() {

        return new DetailFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=getContext();
    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        if (presenter!=null){
            this.presenter=presenter;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_read_detail,container,false);
        initView(view);
        setHasOptionsMenu(true);
        presenter.requestData();
        view.findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0,0);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.requestData();
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_more,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            getActivity().onBackPressed();
        }else if (id==R.id.action_more){
            final BottomSheetDialog dialog=new BottomSheetDialog(getActivity());
            final View view=getActivity().getLayoutInflater().inflate(R.layout.view_detail_bar_detail,null);
            if (presenter.queryIsBooksMarks()){
                Log.i(TAG, "onOptionsItemSelected: "+presenter.queryIsBooksMarks());
                ((TextView)view.findViewById(R.id.textView)).setText(R.string.action_delete_from_bookmarks);
                ((ImageView)view.findViewById(R.id.imageView)).setColorFilter(getContext().getResources().getColor(R.color.colorPrimary));
            }
            //添加收藏
            view.findViewById(R.id.layout_bookmark).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    presenter.addToOrDeleteFromBookMarks();
                }
            });
            //复制连接地址
            view.findViewById(R.id.layout_copy_link).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    presenter.copyLink();
                }
            });
            //复制文本
            view.findViewById(R.id.layout_copy_text).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    showCopyTextError();
                }
            });
            //浏览器中打开
            view.findViewById(R.id.layout_open_in_browser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    presenter.openInBrower();
                }
            });
            //分享文章
            final LinearLayout shareLinearLayout= (LinearLayout) view.findViewById(R.id.layout_share);
            shareLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    View shareLayout=LayoutInflater.from(getActivity()).inflate(R.layout.view_popup_share_layout,null);
//                    PopupWindow popupWindow=new PopupWindow(getActivity());
//                    popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
//                    popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
//                    popupWindow.setContentView(shareLayout);
//                    popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
//                    popupWindow.setAnimationStyle(R.style.WindowsAnimationonChange);
//                    popupWindow.showAsDropDown(coordinatorLayout, Gravity.CENTER,0,0);
                    final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.choice_share_way)
                            .setView(shareLayout);
                    final AlertDialog shareDialog=builder.create();
                    Window window=shareDialog.getWindow();
                    window.setWindowAnimations(R.style.WindowsAnimationonChange);
                    shareDialog.show();
                    shareLayout.findViewById(R.id.QQRL).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareDialog.dismiss();
                            presenter.shareArticleToQQ(myQQListener);
                        }
                    });
                    shareLayout.findViewById(R.id.wxRL).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareDialog.dismiss();
                            presenter.shareArticleToWx();
                        }
                    });
                    shareLayout.findViewById(R.id.communityRL).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareDialog.dismiss();
                            presenter.shareArticleToWxCommunity();
                        }
                    });
                    shareLayout.findViewById(R.id.markRL).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareDialog.dismiss();
                            presenter.shareArticleToWxCollect();
                        }
                    });
                }
            });
            dialog.setContentView(view);
            dialog.show();
        }
        return true;
    }

    private MyQQListener myQQListener=new MyQQListener(){
        //分享成功
        @Override
        public void onComplete(Object o) {
            super.onComplete(o);
            shareSuccess();
        }

        @Override
        public void onCancel() {
            super.onCancel();
            shareCancel();
        }

        @Override
        public void onError(UiError uiError) {
            super.onError(uiError);
            shareError();
            Log.i("picture", "onError:errorCode: "+uiError.errorCode+" errorMessage: "+uiError.errorMessage+"  errorDetail "+uiError.errorDetail);
        }
    };

    @Override
    public void shareSuccess() {
        Snackbar.make(coordinatorLayout,R.string.share_success,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void shareError() {
        Snackbar.make(coordinatorLayout,R.string.share_onerror,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void shareCancel() {
        Snackbar.make(coordinatorLayout,R.string.share_cancel,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void stopLoading() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showLoadingError() {
        Snackbar.make(imageView,R.string.loaded_failed,Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.requestData();
                    }
                }).show();
    }

    @Override
    public void showSharingError() {
        Snackbar.make(imageView,R.string.share_error,Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    public void showResultWithoutBody(String url) {
        webview.loadUrl(url);
    }

    @Override
    public void showCover(String url) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(R.mipmap.loading)
                .centerCrop()
                .error(R.mipmap.zhanweitu)
                .into(imageView);
    }

    @Override
    public void setTitle(String title) {
        setCollapsingToolbarLayoutTitle(title);
    }

    @Override
    public void setImageMode(boolean showImage) {
        webview.getSettings().setBlockNetworkImage(showImage);
    }

    @Override
    public void showBrowserNotFoundError() {
        Snackbar.make(imageView, R.string.no_browser_found,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showTextCopied() {
        Snackbar.make(imageView, R.string.copied_to_clipboard, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showCopyTextError() {
        Snackbar.make(imageView, R.string.copied_to_clipboard_failed, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showAddedToBookmarks() {
        Snackbar.make(imageView, R.string.added_to_bookmarks, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showNotNetError() {
        Snackbar.make(imageView, R.string.not_net_work, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showDeletedFromBookmarks() {
        Snackbar.make(imageView, R.string.deleted_from_bookmarks, Snackbar.LENGTH_SHORT).show();
    }



    @Override
    public void initView(View view) {
        refreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        //设置下拉刷新的按钮的颜色
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        webview= (WebView) view.findViewById(R.id.web_view);
        webview.setScrollbarFadingEnabled(true);
        //fragment和acticity交互之组件传递
        DetailActivity activity = (DetailActivity) getActivity();
        activity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) view.findViewById(R.id.image_view);
        scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
        coordinatorLayout= (CoordinatorLayout) view.findViewById(R.id.coordinatorlayout);
        toolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);
        //扩张时候的title颜色
//        toolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimary));
//        toolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        //webview设置属性
        webview.getSettings().setJavaScriptEnabled(true);
        //缩放,设置为不能缩放可以防止页面上出现放大和缩小的图标
        webview.getSettings().setBuiltInZoomControls(false);
        //缓存
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //开启DOM storage API功能
        webview.getSettings().setDomStorageEnabled(true);
        //开启application Cache功能
        webview.getSettings().setAppCacheEnabled(false);

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //解决 net::ERR_UNKNOWN_URL_SCHEME
                //屏蔽掉错误的重定向url
                if (url.startsWith("http:") || url.startsWith("https:")){
                    view.loadUrl(url);
                }
                return true;
            }
        });

    }

    private void setCollapsingToolbarLayoutTitle(String title){
        toolbarLayout.setTitle(title);
        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webview.removeAllViews();
        webview.destroy();
        webview=null;
        Log.i(TAG, "onDestroyView: webview is destroy");
    }
}
