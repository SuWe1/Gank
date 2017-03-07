package com.gank.settings;

import android.support.v7.preference.Preference;

import com.gank.BasePresenter;
import com.gank.BaseView;

/**
 * Created by 11033 on 2017/3/7.
 */

public interface SettingsContract {
    interface Presenter extends BasePresenter{
        void setNoPic(Preference preference);
        void cleanGlideCache();
        void setSaveTime(Preference preference,Object newValue);
        String getTimeSummary();
    }
    interface View extends BaseView<Presenter>{
        void showCleanGlideCacheSuccess();
    }
}
