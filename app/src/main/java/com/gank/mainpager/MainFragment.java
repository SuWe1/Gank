package com.gank.mainpager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gank.R;
import com.gank.adapter.MainPagerAdapter;

/**
 * Created by 11033 on 2017/3/4.
 */

public class MainFragment extends Fragment {

    private Context context;

    private MainPagerAdapter adapter;
    private GankFragment gankFragment;
    private GankPresenter gankPresenter;

    private TabLayout tabLayout;

    public MainFragment() {
    }

    public static MainFragment newInstance() {

        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.context=getActivity();
        super.onCreate(savedInstanceState);
        // Fragment状态恢复
        if (savedInstanceState!=null){
            FragmentManager manager=getChildFragmentManager();
            gankFragment= (GankFragment) manager.getFragment(savedInstanceState,"gank");
        }else {
            gankFragment=GankFragment.newInstance();
        }
        gankPresenter=new GankPresenter(context,gankFragment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main,container,false);
        //初始化控件
        initView(view);
        //显示菜单懒
        setHasOptionsMenu(true);

        return view;
    }

    private void initView(View v){
        tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        adapter=new MainPagerAdapter(getChildFragmentManager(),context,gankFragment);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main,menu);
    }

    //保存状态
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //填坑 要先判断是否已经保存
        if (outState==null){
            FragmentManager manager=getChildFragmentManager();
            manager.putFragment(outState,"gank",gankFragment);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_look_around){
            lookAround();
        }
        return true;
    }
    //考虑要不要随机读取android ios 前端 目前默认只是android
    public void lookAround(){
        gankPresenter.LookAround();
    }
  /*  public MainPagerdapter getAdapter(){
        return adapter;
    }*/
}
