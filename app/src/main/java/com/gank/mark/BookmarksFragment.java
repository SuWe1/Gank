package com.gank.mark;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gank.R;
import com.gank.adapter.BookMarksAdapter;
import com.gank.bean.BeanTeype;
import com.gank.bean.GankNews;
import com.gank.interfaze.OnRecyclerViewOnClickListener;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/4.
 */

public class BookmarksFragment extends Fragment implements BookmarksContract.View {
    private BookMarksAdapter adapter;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private BookmarksContract.Presenter presenter;

    public BookmarksFragment() {

    }

    public static BookmarksFragment newInstance() {

        return new BookmarksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list,container,false);
        initView(view);
        presenter.loadResults(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadResults(true);
            }
        });
        return view;
    }

    @Override
    public void showResults(ArrayList<GankNews.Question> ganklist,ArrayList<Integer> types) {
        if (adapter==null){
            adapter=new BookMarksAdapter(getActivity(),ganklist,types);
            adapter.setItemOnClickListener(new OnRecyclerViewOnClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    int type=recyclerView.findViewHolderForLayoutPosition(position).getItemViewType();
                    if (type==BookMarksAdapter.TYPE_Gank_NORMAL){
                        presenter.startReading(BeanTeype.TYPE_Gank,position);
                    }
                }
            });
            recyclerView.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataChanged() {
        presenter.loadResults(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void stopLoading() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void setPresenter(BookmarksContract.Presenter presenter) {
        if (presenter!=null){
            this.presenter=presenter;
        }
    }

    @Override
    public void initView(View view) {
        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }
}
