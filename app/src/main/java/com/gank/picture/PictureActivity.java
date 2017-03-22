package com.gank.picture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.gank.R;

import uk.co.senab.photoview.PhotoViewAttacher;

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
        presenter.LoadPic(ImgUrl,mImageView);
    }

    private void initView(){
        /**
         * 主题使用的是NoActionbar不设定setSupportActionBar是不会显示菜单栏的
         * 用 setSupportActionBar 设定，Toolbar即能取代原本的 actionbar
         * 然后就可以使用getSupportActionBar().setDisplayHomeAsUpEnabled(true);的办法来显示还回箭头
         */
        mToolbar= (Toolbar) findViewById(R.id.picture_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("");
        coordinatorLayout= (CoordinatorLayout) findViewById(R.id.pic_coordinatorlayout);
        mImageView= (ImageView) findViewById(R.id.Img_Meizi);
        mAppBarLayout= (AppBarLayout) findViewById(R.id.pic_appbar);
    }

    public static Intent newIntent(Context context,String url){
        Intent intent=new Intent(context,PictureActivity.class);
        intent.putExtra(Img_Url,url);
        return intent;
    }

    private void parseIntent(){
        ImgUrl=getIntent().getStringExtra(Img_Url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
    public void showSaveSuccessful() {
        Snackbar.make(coordinatorLayout,R.string.loaded_success,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showSavaFail() {
//        Snackbar.make(coordinatorLayout,R.string.loaded_failed,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void initView(View view) {

    }
}
