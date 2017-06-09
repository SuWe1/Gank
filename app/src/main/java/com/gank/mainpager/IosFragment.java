package com.gank.mainpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gank.R;
import com.gank.adapter.IosNewsAdater;
import com.gank.bean.IosNews;
import com.gank.interfaze.OnRecyclerViewOnClickListener;

import java.util.ArrayList;

/**
 * Created by Swy on 2017/6/9.
 */

public class IosFragment extends android.support.v4.app.Fragment implements IosContract.View {

    private IosNewsAdater adater;
    private IosContract.Presenter presenter;
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    @Override
    public void setPresenter(IosContract.Presenter presenter) {
        if (presenter!=null){
            this.presenter=presenter;
        }
    }

    public static IosFragment newInstance() {

        return new IosFragment();
    }
    public IosFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list,container,false);
        initView(view);
        presenter.start();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.reflush();
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isScrollState=false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager= (LinearLayoutManager) recyclerView.getLayoutManager();
                //没有滚动时候
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                    int lastVisibilityItem=manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount=manager.getItemCount();
                    if (lastVisibilityItem==totalItemCount-1  && isScrollState){
                        presenter.loadMore(1);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isScrollState=dy>0;
                if (dy>0){
                    fab.hide();
                }else {
                    fab.show();
                }
            }
        });

        // 按通常的做法，在每个fragment中去设置监听时间会导致先前设置的listener失效
        // 尝试将监听放置到main pager adapter中，这样做会引起fragment中recycler view和fab的监听冲突
        //fab并不能获取到点击事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        return view;
    }

    @Override
    public void initView(View view) {
        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        //设置下拉刷新的按钮的颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setRippleColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void showError() {
        Snackbar.make(fab,R.string.loaded_failed,Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.reflush();
                    }
                }).show();
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void Stoploading() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showResult(ArrayList<IosNews.Question> list) {
        if (adater==null){
            adater=new IosNewsAdater(list,getContext());
            adater.setItemOnClickListener(new OnRecyclerViewOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    presenter.StartReading(position);
                }

                @Override
                public void onItemLongClick(View v, int position) {

                }
            });
            recyclerView.setAdapter(adater);
        }else {
            adater.notifyDataSetChanged();
        }
    }

    @Override
    public void showNotNetError() {
        Snackbar.make(fab,R.string.not_net_work,Snackbar.LENGTH_INDEFINITE).show();
    }
}
