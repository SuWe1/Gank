package com.gank.mainpager;

import android.support.v4.app.Fragment;
import android.view.View;

import com.gank.bean.GankNews;

import java.util.ArrayList;

/**
 * Created by 11033 on 2017/3/15.
 */

public class MeiziFragment extends Fragment implements MeiziContract.View {
    public MeiziFragment() {     
    }

    public static MeiziFragment newInstance() {
        return new MeiziFragment();
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

    }

    @Override
    public void showNotNetError() {

    }

    @Override
    public void setPresenter(GankContract.Presenter presenter) {

    }

    @Override
    public void initView(View view) {

    }
}
