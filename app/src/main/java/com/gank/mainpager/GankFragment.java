package com.gank.mainpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gank.adapter.GankNewsAdapter;
import com.gank.bean.GankNews;
import com.gank.interfaze.OnRecyclerViewOnClickListener;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/4.
 */

public class GankFragment extends Fragment implements GankContract.View {

    private GankNewsAdapter adapter;
    private RecyclerView recyclerView;

    private GankContract.Presenter presenter;
    public GankFragment() {
    }

    //
    public static GankFragment newInstance() {

        return new GankFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void showError() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void Stoploading() {

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
    public void showPickDialog() {

    }

    @Override
    public void setPresenter(GankContract.Presenter presenter) {

    }

    @Override
    public void initView(View view) {

    }
}
