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

import java.util.Random;

/**
 * Created by Swy on 2017/3/4.
 */

public class MainFragment extends Fragment {

    private Context context;

    private MainPagerAdapter adapter;
    private GankFragment gankFragment;
    private FrontFragment frontFragment;
//    private MeiziFragment meiziFragment;
    private IosFragment iosFragment;
    private GankPresenter gankPresenter;
    private FrontPresenter frontPresenter;
//    private MeiziPresenter meiziPresenter;
    private IosPresenter iosPresenter;

    private TabLayout tabLayout;

    public MainFragment() {
    }

    public static MainFragment newInstance() {

        return new MainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        this.context=context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        /**
         * 之前写成this.context=getActivity().getApplicationContext();是为了防止内存泄漏
         * 这样在meizifragment的startReading中传入的context是不符合要求的所有该成如下了
         */
        this.context=getActivity();
        super.onCreate(savedInstanceState);
        // Fragment状态恢复
        if (savedInstanceState!=null){
            FragmentManager manager=getChildFragmentManager();
            gankFragment= (GankFragment) manager.getFragment(savedInstanceState,"gank");
            frontFragment= (FrontFragment) manager.getFragment(savedInstanceState,"front");
//            meiziFragment= (MeiziFragment) manager.getFragment(savedInstanceState,"meizi");
            iosFragment= (IosFragment) manager.getFragment(savedInstanceState,"ios");
        }else {
            frontFragment=FrontFragment.newInstance();
            gankFragment=GankFragment.newInstance();
//            meiziFragment=meiziFragment.newInstance();
            iosFragment=IosFragment.newInstance();
        }
        gankPresenter=new GankPresenter(context,gankFragment);
        frontPresenter=new FrontPresenter(context,frontFragment);
//        meiziPresenter=new MeiziPresenter(context,meiziFragment);
        iosPresenter=new IosPresenter(iosFragment,context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main,container,false);
        //初始化控件
        initView(view);
        //显示菜单懒
        setHasOptionsMenu(true);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
//                if (tab.getPosition() == 0) {
//                    fab.hide();
//                } else {
//                    fab.show();
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

    /**
     * TabLayout
     * app:tabGravity="center"//居中，如果是fill，则是充满
     * @param v
     */
    private void initView(View v){
        tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);
        adapter=new MainPagerAdapter(getChildFragmentManager(),context,gankFragment,frontFragment,iosFragment);
        viewPager.setAdapter(adapter);
        //当我们的tab选择时，让viewpager选中对应的item。
        //setupWithViewPager必须在ViewPager.setAdapter()之后调用
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
            manager.putFragment(outState,"front",frontFragment);
//            manager.putFragment(outState,"meizi",meiziFragment);
            manager.putFragment(outState,"ios",iosFragment);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_look_around){
            lookAround();
        }
        return true;
    }
    //随机读取android  前端
    public void lookAround(){
        Random random=new Random();
        //0-3的随机数 不包括3
        int who=random.nextInt(3);
        switch (who){
            case 0:
                gankPresenter.LookAround();
                break;
            case 1:
                frontPresenter.LookAround();
                break;
            case 2:
                iosPresenter.LookAround();
                break;
            default:
                break;
        }

    }
     public MainPagerAdapter getAdapter(){
        return adapter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        FragmentTransaction fragmentTransaction=getChildFragmentManager().beginTransaction();
//        for (Fragment fragment:adapter.getFragments()){
//            fragmentTransaction.remove(fragment)
//        }
    }
}
