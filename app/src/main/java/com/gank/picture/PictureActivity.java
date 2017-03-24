package com.gank.picture;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.gank.R;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.gank.picture.PicturePresenter.MY_PERMISSIONS_REQUEST_CALL_PHONE;

/**
 * Created by 11033 on 2017/3/22.
 */

public class PictureActivity extends AppCompatActivity implements PictureContract.View{

    private CoordinatorLayout coordinatorLayout;
    private ImageView mImageView;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private PhotoViewAttacher mPhotoViewAttacher;
    private String ImgUrl;

    public static final String Img_Url="imgUrl";
    public static final String TRANSIT_PIC="picture";

    //是否隐藏appbar
    private boolean mIsHide=false;

    private PictureContract.Presenter presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_layout);
        presenter=new PicturePresenter(this,this);
        initView();
        parseIntent();
        ViewCompat.setTransitionName(mImageView,TRANSIT_PIC);
        presenter.LoadPic(ImgUrl,mImageView);
    }

    private void initView(){
        /**
         * 主题使用的是NoActionbar不设定setSupportActionBar是不会显示菜单栏的
         * 用 setSupportActionBar 设定，Toolbar即能取代原本的 actionbar
         * 然后就可以使用getSupportActionBar().setDisplayHomeAsUpEnabled(true);的办法来显示还回箭头
         * issue:设置转场动画之前点击还回按钮默认是还回的 设置之后结果无效了 所有在onOptionsItemSelected重新判断了一下时间如果点击
         * 了还回按钮则finish PictureActivity
         * issue:toolbar.setTitle()需要在调用setSupportActionBar(toolbar)方法之前设置
         */
        mToolbar= (Toolbar) findViewById(R.id.picture_toolbar);
        mToolbar.setTitle("妹子");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        coordinatorLayout= (CoordinatorLayout) findViewById(R.id.pic_coordinatorlayout);
        mImageView= (ImageView) findViewById(R.id.Img_Meizi);
        mAppBarLayout= (AppBarLayout) findViewById(R.id.pic_appbar);
//        setupPhotoAttacher();
    }

    public static Intent newIntent(Context context,String url){
        Intent intent=new Intent(context,PictureActivity.class);
        intent.putExtra(Img_Url,url);
        return intent;
    }

    private void parseIntent(){
        ImgUrl=getIntent().getStringExtra(Img_Url);
    }

    private void setupPhotoAttacher(){
        mPhotoViewAttacher=new PhotoViewAttacher(mImageView);
        mPhotoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float v, float v1) {
                hideOrShowAppBar();
            }
        });
        mPhotoViewAttacher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(PictureActivity.this)
                        .setTitle(R.string.save_picture_to_local_title)
                        .setMessage(R.string.save_picture_to_local)
                        .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                             dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.SavePicTolocal(ImgUrl);
                                dialog.dismiss();
                            }
                        })
                        .show();

                return true;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.picture_menu:
                presenter.SavePicTolocal(ImgUrl);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideOrShowAppBar(){
        mAppBarLayout.animate()
                .translationY(mIsHide?0:-mAppBarLayout.getHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        mIsHide=!mIsHide;
    }

    @Override
    public void setPresenter(PictureContract.Presenter presenter) {
        if (presenter!=null){
            this.presenter=presenter;
        }
    }

    @Override
    public void showResult() {
//        presenter.LoadPic(ImgUrl,mImageView);
    }

    @Override
    public void showSaveSuccessful(String path) {
        Snackbar.make(coordinatorLayout,R.string.save_success,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showSavaFail() {
        Snackbar.make(coordinatorLayout,R.string.save_fail,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showNoPermission() {
        Snackbar.make(coordinatorLayout,R.string.save_fail_no_permission,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void initView(View view) {

    }

    //用户对请求作出响应后的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                presenter.SavePicTolocal(ImgUrl);
            } else {
                // Permission Denied
                showNoPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
