package com.gank.mainpager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.gank.R;
import com.gank.about.AboutPreferenceActivity;
import com.gank.mark.BookmarksFragment;
import com.gank.mark.BookmarksPresenter;
import com.gank.meizi.MeiziFragment;
import com.gank.meizi.MeiziPresenter;
import com.gank.settings.SettingsPreferenceActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private  MainFragment mainFragment;
    private BookmarksFragment bookmarksfragment;
    private MeiziFragment meiziFragment;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    public static final String ACTION_BOOKMARKS = "com.gank.mark";
    public static final String ACTION_MEIZI = "com.gank.meizi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //回复fragment状态
        /**
         * 添加Fragment前检查是否有保存的。如果没有状态保存，说明Acitvity是第1次被创建，我们添加Fragment
         */
        if (savedInstanceState!=null){
            mainFragment= (MainFragment) getSupportFragmentManager().getFragment(savedInstanceState,"MainFragment");
            bookmarksfragment=(BookmarksFragment) getSupportFragmentManager().getFragment(savedInstanceState,"BookmarksFragment");
            meiziFragment= (MeiziFragment) getSupportFragmentManager().getFragment(savedInstanceState,"meiziFragment");
        }else {
            mainFragment=MainFragment.newInstance();
            bookmarksfragment=BookmarksFragment.newInstance();
            meiziFragment=MeiziFragment.newInstance();
        }
        //Fragment事务
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment,mainFragment,"MainFragment").commit();
            getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment,bookmarksfragment,"BookmarksFragment").commit();
            getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment,meiziFragment,"meiziFragment").commit();
        }
        /*if (!bookmarksfragment.isAdded()){
            getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment,bookmarksfragment,"BookmarksFragment").commit();
        }*/
        new BookmarksPresenter(MainActivity.this,bookmarksfragment);
        new MeiziPresenter(MainActivity.this,meiziFragment);
        String action=getIntent().getAction();
        if (action.equals(ACTION_BOOKMARKS)){
            showBookMarksFragment();
            navigationView.setCheckedItem(R.id.nav_bookmarks);
        }else if (action.equals(ACTION_MEIZI)){
            showMeiziFragment();
            navigationView.setCheckedItem(R.id.nav_meizi);
        }else {
            showMainFragment();
            navigationView.setCheckedItem(R.id.nav_home);
        }

    }

    private void initView(){
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle是一个开关，用于打开/关闭DrawerLayout抽屉
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        ///该方法会自动和actionBar关联, 将开关的图片显示在了action上，如果不设置，也可以有抽屉的效果，不过是默认的图标
        toggle.syncState();
        navigationView= (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    // 显示MainFragment并设置Title
    private void showMainFragment(){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.show(mainFragment);
        fragmentTransaction.hide(bookmarksfragment);
        fragmentTransaction.hide(meiziFragment);
        fragmentTransaction.commit();
        toolbar.setTitle(getResources().getString(R.string.app_name));
    }

    // 显示BookmarksFragment并设置Title
    private void showBookMarksFragment(){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.show(bookmarksfragment);
        fragmentTransaction.hide(mainFragment);
        fragmentTransaction.hide(meiziFragment);
        fragmentTransaction.commit();
        toolbar.setTitle(getResources().getString(R.string.nav_mark));
        if (bookmarksfragment.isAdded()){
            bookmarksfragment.notifyDataChanged();
        }
    }

    public void showMeiziFragment(){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.show(meiziFragment);
        fragmentTransaction.hide(mainFragment);
        fragmentTransaction.hide(bookmarksfragment);
        fragmentTransaction.commit();
        toolbar.setTitle(getResources().getString(R.string.nav_meizi));
        if (meiziFragment.isAdded()){
            meiziFragment.notifyDataChanged();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id=item.getItemId();
        if (id==R.id.nav_home){
            showMainFragment();
        }else if (id==R.id.nav_bookmarks){
            showBookMarksFragment();
        }else if (id==R.id.nav_meizi){
            showMeiziFragment();
        }else if (id==R.id.nav_change_theme){
            //当DrawerLayout关闭后更改主题
            drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {

                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    SharedPreferences sp=getSharedPreferences("user_settings", Context.MODE_PRIVATE);
                    //检测当前主题模式
                    int mode=getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                    if (mode==Configuration.UI_MODE_NIGHT_YES){
                        sp.edit().putInt("theme",0).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }else if (mode==Configuration.UI_MODE_NIGHT_NO){
                        sp.edit().putInt("theme",1).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    getWindow().setWindowAnimations(R.style.WindowsAnimationonChange);
//                    MainActivity.this.recreate();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        }else if (id==R.id.nav_settings){
            startActivity(new Intent(this, SettingsPreferenceActivity.class));
        }else if (id==R.id.nav_about){
            startActivity(new Intent(this, AboutPreferenceActivity.class));
        }
        return true;
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    //存储fragment的状态
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        /*FragmentManager manager = getSupportFragmentManager();
        manager.putFragment(outState, "MainFragment", mainFragment);
        manager.putFragment(outState,"BookmarksFragment",bookmarksfragment);*/
        if (mainFragment.isAdded()){
            getSupportFragmentManager().putFragment(outState,"MainFragment",mainFragment);
        }
        if (bookmarksfragment.isAdded()){
            getSupportFragmentManager().putFragment(outState,"BookmarksFragment",bookmarksfragment);
        }
        if (meiziFragment.isAdded()){
            getSupportFragmentManager().putFragment(outState,"meiziFragment",meiziFragment);
        }
    }
}
