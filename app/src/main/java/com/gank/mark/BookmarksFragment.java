package com.gank.mark;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gank.bean.GankNews;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/4.
 */

public class BookmarksFragment extends Fragment implements BookmarksContract.View {
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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void showResults(ArrayList<GankNews> ganklist) {

    }

    @Override
    public void notifyDataChanged() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void setPresenter(BookmarksContract.Presenter presenter) {

    }

    @Override
    public void initView(View view) {

    }
}
