package com.gank.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gank.R;

/**
 * Created by 11033 on 2017/3/7.
 */

public class SettingsPreferenceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initView();
        Fragment fragment=SettingPreferenceFragment.newInstance();

        // 这里就是向占位的 Layout 内替换一个 Fragment (其实不是替换Layout而是替换其内部的Fragment)
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_container,fragment).commit();
        new SettingPresenter((SettingsContract.View) fragment,SettingsPreferenceActivity.this);
    }
    private void initView(){
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
