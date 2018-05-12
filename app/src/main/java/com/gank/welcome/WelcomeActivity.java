package com.gank.welcome;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gank.R;
import com.gank.mainpager.MainActivity;

public class WelcomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private WelcomeAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ImageButton btnPre;
    private ImageButton btnNext;
    private AppCompatButton btnFinish;
    //中间三个小白点
    private ImageView[] indicators;

    private int bgColors[];
    private int currentPage;
    private SharedPreferences sp;

    private static final int INIT_DATA_IN_FRIST_COME_APP_FINISH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("frsitLaunch", true)) {
            setContentView(R.layout.welcome_main);
            initView();
            initData();
            new InitDataInFristComeApp().execute();
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                    ArgbEvaluator().evaluate(float fraction, Object startValue, Object endValue)
                    int colorUpdate = (int) new ArgbEvaluator().evaluate(positionOffset,
                            bgColors[position], bgColors[position == 2 ? position : position + 1]);
                    mViewPager.setBackgroundColor(colorUpdate);
                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                    updateColor(position);
                    mViewPager.setBackgroundColor(bgColors[position]);
                    switch (position) {
                        case 0:
                            btnPre.setVisibility(View.GONE);
                            btnNext.setVisibility(View.VISIBLE);
                            btnFinish.setVisibility(View.GONE);
                            break;
                        case 1:
                            btnPre.setVisibility(View.VISIBLE);
                            btnNext.setVisibility(View.VISIBLE);
                            btnFinish.setVisibility(View.GONE);
                            break;
                        case 2:
                            btnPre.setVisibility(View.VISIBLE);
                            btnNext.setVisibility(View.GONE);
                            btnFinish.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            btnPre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPage -= 1;
                    mViewPager.setCurrentItem(currentPage);
                }
            });
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPage += 1;
                    mViewPager.setCurrentItem(currentPage);
                }
            });
            btnFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("frsitLaunch", false);
                    editor.apply();
                    notFristLaunchApp();
                }
            });
        } else {
            notFristLaunchApp();
            finish();
        }

    }

    private void initView() {
        mSectionsPagerAdapter = new WelcomeAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        btnPre = (ImageButton) findViewById(R.id.btnPre);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnFinish = (AppCompatButton) findViewById(R.id.btnFinish);
        indicators = new ImageView[]{(ImageView) findViewById(R.id.indicator1), (ImageView) findViewById(R.id.indicator2), (ImageView) findViewById(R.id.indicator3)};
    }

    public void initData() {
        bgColors = new int[]{ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.cyan_500),
                ContextCompat.getColor(this, R.color.light_blue_500)};
    }

    private void updateColor(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(i == position ? R.drawable.wel_onboarding_indicator_selected : R.drawable.wel_onboarding_indicator_unselected);
        }
    }

    private void notFristLaunchApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INIT_DATA_IN_FRIST_COME_APP_FINISH:
                    btnFinish.setText(R.string.welcome_finish);
                    btnFinish.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 第一次进入App所需要的预加载
     */
    class InitDataInFristComeApp extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            handler.sendEmptyMessage(INIT_DATA_IN_FRIST_COME_APP_FINISH);
        }
    }


}
