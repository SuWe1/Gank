package com.gank.settings;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gank.R;

/**
 * Created by Swy on 2017/3/7.
 */

public class SettingPreferenceFragment extends PreferenceFragmentCompat implements SettingsContract.View {
    private SettingsContract.Presenter presenter;

    private Toolbar toolbar;

    private Preference timePeference;

    public SettingPreferenceFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting_preference_fragment);
        initView(getView());
        findPreference("no_picture_mode").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                presenter.setNoPic(preference);
                return false;
            }
        });

        findPreference("clear_glide_cache").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                presenter.cleanGlideCache();
                return false;
            }
        });

        timePeference=findPreference("time_of_saving_article");
        timePeference.setSummary(presenter.getTimeSummary());
        timePeference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                presenter.setSaveTime(preference,newValue);
                timePeference.setSummary(presenter.getTimeSummary());
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    public static SettingPreferenceFragment newInstance() {
        return  new SettingPreferenceFragment();
    }

    @Override
    public void showCleanGlideCacheSuccess() {
        Snackbar.make(toolbar,R.string.clear_image_cache_successfully,Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        if (presenter!=null){
            this.presenter=presenter;
        }
    }

    @Override
    public void initView(View view) {
        toolbar= (Toolbar) getActivity().findViewById(R.id.toolbar);
    }
}
