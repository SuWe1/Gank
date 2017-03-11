package com.gank.mainpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gank.R;
import com.gank.adapter.GankNewsAdapter;
import com.gank.bean.GankNews;
import com.gank.interfaze.OnRecyclerViewOnClickListener;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/11.
 */

public class FrontFragment extends Fragment implements GankContract.View {
    private static final String TAG = "FrontFragment";

    private GankNewsAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refrehLayout;
    private GankContract.Presenter presenter;

    public FrontFragment() {
    }

    public static FrontFragment newInstance() {
        return new FrontFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list,container,false);
        initView(view);
        presenter.start();
        refrehLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.reflush();
            }
        });
        //判断是否上拉加载跟多和fab显示或者隐藏
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isScrollState =false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager= (LinearLayoutManager) recyclerView.getLayoutManager();
                //没有滚动
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                    int lastVisibilityItem=manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount=manager.getItemCount();
                    //总数减去底部footer  判断是否滚动到底部并且是向下滑动 默认加载一页
                    if ((totalItemCount-1)==lastVisibilityItem && isScrollState){
                        presenter.loadMore(1);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isScrollState=dy>0;
                //隐藏或者显示fab
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
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        return view;
    }

    @Override
    public void showError() {
        Snackbar.make(fab, R.string.loaded_failed,Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.reflush();
                    }
                })
                .show();
    }

    @Override
    public void showLoading() {
        refrehLayout.post(new Runnable() {
            @Override
            public void run() {
                refrehLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void Stoploading() {
        refrehLayout.post(new Runnable() {
            @Override
            public void run() {
                refrehLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showResult(ArrayList<GankNews.Question> list) {
        if (adapter==null){
            adapter=new GankNewsAdapter(list,getContext());
            adapter.setItemOnClickListener(new OnRecyclerViewOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    presenter.StartReading(position);
                }
            });
            recyclerView.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showNotNetError() {
        Snackbar.make(fab, R.string.not_net_work, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(GankContract.Presenter presenter) {

        if (presenter!=null){
            this.presenter=presenter;
        }
    }

    @Override
    public void initView(View view) {
        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        refrehLayout= (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        //设置刷线加载颜色跟随主题
        refrehLayout.setColorSchemeResources(R.color.colorPrimary);
        fab= (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setRippleColor(getResources().getColor(R.color.colorPrimaryDark));
    }
}
