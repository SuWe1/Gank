package com.gank.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gank.R;
import com.gank.bean.BeanTeype;

/**
 * Created by Swy on 2017/3/5.
 */

public class DetailActivity extends AppCompatActivity{
    private DetailFragment detailFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);
        if (savedInstanceState!=null){
            detailFragment= (DetailFragment) getSupportFragmentManager().getFragment(savedInstanceState,"detailFragment");
        }else {
            detailFragment=DetailFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.container,detailFragment).commit();
        }
        //获取列表传过来的具体item数据
        Intent intent=getIntent();
        DetailPresenter presenter=new DetailPresenter(detailFragment,DetailActivity.this);
        presenter.setType((BeanTeype) intent.getSerializableExtra("type"));
        presenter.setId(intent.getIntExtra("id",1));
        presenter.set_id(intent.getStringExtra("_id"));
        presenter.setTitle(intent.getStringExtra("title"));
        presenter.setUrl(intent.getStringExtra("url"));
        presenter.setImgUrl(intent.getStringExtra("imgUrl"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (detailFragment.isAdded()){
            getSupportFragmentManager().putFragment(outState,"detailFragment",detailFragment);
        }
    }
}
