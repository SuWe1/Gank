package com.example.searchview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.searchview.adapter.HistorySearchCursorAdapter;
import com.example.searchview.db.HistoryContract;
import com.example.searchview.utils.AnimationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Swy on 2018/1/28.
 */

public class HistorySearchView extends FrameLayout {

    /**
     * Number of suggestions to show.
     */
    private static int MAX_HISTORY = 10;

    /**
     * 是否已经打开搜索框
     */
    private boolean isOpen;

    /**
     * 是否清除视图焦点
     */
    private boolean mClearingFocus;

    /**
     * 是否需要动画效果
     */
    private boolean mShouldAnimate;


    private boolean mShouldCloseOnTintClick;


    /**
     * 搜索框关闭或打开回调接口
     */
    private SearchViewListener mSearchViewListener;

    /**
     * 搜索框内查询字符提交或更改回调接口
     */
    private OnQueryTextListener mOnQueryTextListener;

    /**
     * 是否需要保持到历史搜索
     */
    private boolean mShouldKeepHistory;

    private HistorySearchCursorAdapter mAdapter;

    /**
     * 搜索框之前的内容.
     */
    private CharSequence mOldQuery;

    /**
     * 搜索框现在的内容.
     */
    private CharSequence mCurrentQuery;

    private Context mContext;

    private FrameLayout mRoot;
    private View mTintView;
    private LinearLayout mSearchBar;
    private ImageButton mBack;
    private EditText mSearchEditText;
    private ImageButton mClear;
    private ListView mSuggestionListView;

    public HistorySearchView(@NonNull Context context) {
        super(context,null);
    }

    public HistorySearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
    }

    public HistorySearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        this.mContext=context;
        this.mShouldAnimate=true;
        this.mShouldKeepHistory=true;

        initView();

        initStyle(attrs, defStyleAttr);
    }

    /**
     * 加载搜索框视图
     */
    private void initView(){
        //家在布局 并加载到父布局的层次结构中
        LayoutInflater.from(mContext).inflate(R.layout.search_view,this,true);


        mRoot= (FrameLayout) findViewById(R.id.search_layout);
        mSuggestionListView= (ListView) mRoot.findViewById(R.id.suggestion_list);
        mBack= (ImageButton) mRoot.findViewById(R.id.action_back);
        mClear= (ImageButton) findViewById(R.id.action_clear);
        mSearchEditText= (EditText) mRoot.findViewById(R.id.et_search);
        mSearchBar= (LinearLayout) mRoot.findViewById(R.id.search_bar);
        mTintView=mRoot.findViewById(R.id.transparent_view);

        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearch();
            }
        });
        mClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEditText.setText("");
            }
        });
        mTintView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearch();
            }
        });

        //初始化搜索框
        initSearchView();

        mAdapter=new HistorySearchCursorAdapter(mContext,getHistoryCursor(),0);
        //记录过滤
        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                String filter=constraint.toString();
                if (filter.isEmpty()){
                    return getHistoryCursor();
                }else {
                    return  mContext.getContentResolver().query(
                            HistoryContract.HistoryEntry.CONTENT_URI,
                            null,
                            HistoryContract.HistoryEntry.COLUMN_QUERY+ " LIKE ? ",
                             new String[]{"%"+filter+"&"},
                            HistoryContract.HistoryEntry.COLUMN_IS_HISTORY+" DESC," +
                             HistoryContract.HistoryEntry.COLUMN_QUERY
                    );
                }
            }
        });
        mSuggestionListView.setAdapter(mAdapter);
        mSuggestionListView.setTextFilterEnabled(true);
    }

    /**
     * 加载style
     * @param attributeSet
     * @param defStyleAttributes
     */
    private void initStyle(AttributeSet attributeSet,int defStyleAttributes){
        //低版本兼容矢量图
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        TypedArray typedArray=mContext.obtainStyledAttributes(attributeSet,R.styleable.MaterialSearchView,defStyleAttributes,0);
        if (typedArray!=null){
            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchBackground)){
                setBackground(typedArray.getDrawable(R.styleable.MaterialSearchView_searchBackground));
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_android_textColor)) {
                setTextColor(typedArray.getColor(R.styleable.MaterialSearchView_android_textColor,
                        ContextCompat.getColor(mContext,R.color.black)));
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_android_textColorHint)) {
                setHintTextColor(typedArray.getColor(R.styleable.MaterialSearchView_android_textColorHint,
                        ContextCompat.getColor(mContext,R.color.gray_50)));
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_android_hint)) {
                setHint(typedArray.getString(R.styleable.MaterialSearchView_android_hint));
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchCloseIcon)) {
                setClearIcon(typedArray.getResourceId(
                        R.styleable.MaterialSearchView_searchCloseIcon,
                        R.drawable.ic_clear_black_24dp)
                );
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchBackIcon)) {
                setBackIcon(typedArray.getResourceId(
                        R.styleable.MaterialSearchView_searchBackIcon,
                        R.drawable.ic_arrow_back_black_24dp)
                );
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchSuggestionBackground)) {
                setSuggestionBackground(typedArray.getResourceId(
                        R.styleable.MaterialSearchView_searchSuggestionBackground,
                        R.color.search_layover_bg)
                );
            }

            if(typedArray.hasValue(R.styleable.MaterialSearchView_android_inputType)) {
                setInputType(typedArray.getInteger(
                        R.styleable.MaterialSearchView_android_inputType,
                        InputType.TYPE_CLASS_TEXT)
                );
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchBarHeight)) {
                setSearchBarHeight(typedArray.getDimensionPixelSize(R.styleable.MaterialSearchView_searchBarHeight, getAppCompatActionBarHeight()));
            } else {
                setSearchBarHeight(getAppCompatActionBarHeight());
            }


            ViewCompat.setFitsSystemWindows(this, typedArray.getBoolean(R.styleable.MaterialSearchView_android_fitsSystemWindows, false));

            typedArray.recycle();
        }
    }

    private void initSearchView(){
        //提交
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onSubmitQuery();
                return true;
            }
        });
        //文字改变
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s.toString());
                mAdapter.notifyDataSetChanged();
                HistorySearchView.this.onTextChanged(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //焦点改变
        mSearchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showKeyboard(mSearchEditText);
                    showSuggetions();
                }
            }
        });
    }

    /**
     * 显示历史记录列表
     */
    private void showSuggetions(){
        mSuggestionListView.setVisibility(VISIBLE);
    }

    /**
     * 搜索框内内容更改时过滤提示列表和更新按钮
     * @param newText
     */
    private void onTextChanged(CharSequence newText){
        mCurrentQuery=mSearchEditText.getText();

        //如果现在的内容不为空  显示清除按钮
        if (!TextUtils.isEmpty(mCurrentQuery)){
            displayClearButton(true);
        }else {
            displayClearButton(false);
        }

        if(mOnQueryTextListener!=null){
            mOnQueryTextListener.onQueryTextChange(newText.toString());
        }

        //更新
        mOldQuery=mCurrentQuery;
    }

    /**
     * 是否显示清除按钮
     * @param display
     */
    private void displayClearButton(boolean display){
        mClear.setVisibility(display ? VISIBLE : GONE);
    }

    /**
     *提交搜索
     */
    private void onSubmitQuery(){
        //获取搜索框中的内容
        CharSequence query=mSearchEditText.getText();

        //如果搜索内容不为空
        if (query != null && TextUtils.getTrimmedLength(query) > 0){
            //
            if (mOnQueryTextListener !=null && !mOnQueryTextListener.onQueryTextSubmit(query.toString())){

                if (mShouldKeepHistory){
                    saveQueryToDb(query.toString(),System.currentTimeMillis());
                }

                // Refresh the cursor on the adapter,
                // so the new entry will be shown on the next time the user opens the search view.
                refreshAdapterCursor();

                closeSearch();
                mSearchEditText.setText("");
            }
        }

    }

    /**
     * 保持查询记录到数据库
     *
     * @param query - The query to be saved. Can't be empty or null.
     * @param date - The insert date, in millis. As a suggestion, use System.currentTimeMillis();
     **/
    public void saveQueryToDb(String query, long date){
        if (!TextUtils.isEmpty(query) && date > 0){
            ContentValues values=new ContentValues();
            values.put(HistoryContract.HistoryEntry.COLUMN_QUERY,query);
            values.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE,date);
            values.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY,1);
            mContext.getContentResolver().insert(HistoryContract.HistoryEntry.CONTENT_URI,values);
        }
    }

    /**
     * 更新提示列表
     */
    private void refreshAdapterCursor(){
        Cursor cursor=getHistoryCursor();
        mAdapter.changeCursor(cursor);
    }

    /**
     * 点击记录列表执行提交查询操作. If submit is set to true, it'll submit the query.
     *
     * @param query - The Query value.
     * @param submit - Whether to submit or not the query or not.
     */
    public void setQuery(CharSequence query,boolean submit){
        mSearchEditText.setText(query);

        if (query!=null){
            mSearchEditText.setSelection(mSearchEditText.length());
            mCurrentQuery=query;
        }
        if (submit && !TextUtils.isEmpty(query)){
            onSubmitQuery();
        }
    }

    /**
     * 获取数据库中的搜索记录
     * @return cursor
     */
    private Cursor getHistoryCursor(){
        return mContext.getContentResolver().query(HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY+" =?",
                new String[]{"1"},
                HistoryContract.HistoryEntry.COLUMN_INSERT_DATE+" DESC LIMIT "+MAX_HISTORY);
    }

    /**
     * 打开搜索栏
     */
    public void openSearch(){
        if (isOpen){
            return;
        }

        mSearchEditText.setText(" ");
        mSearchEditText.requestFocus();

        if (mShouldAnimate){
            //sdk>=21
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                mRoot.setVisibility(VISIBLE);
                AnimationUtils.circleRevealView(mSearchBar);
            }else {
                AnimationUtils.fadeInView(mRoot);
            }
        }else {
            mRoot.setVisibility(VISIBLE);
        }

        if (mSearchViewListener!=null){
            mSearchViewListener.onSearchViewOpened();
        }
        isOpen=true;
    }

    /**
     * 关闭搜索栏
     */
    public void closeSearch(){
        //判断是否已经处于关闭状态
        if (!isOpen){
            return;
        }

        //清空edit
        mSearchEditText.setText("");
        dismissSuggestion();
        clearFocus();

        if (mShouldAnimate){
            final View v=mRoot;
            AnimatorListenerAdapter listenerAdapter=new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    v.setVisibility(GONE);
                }
            };

            //判断sdk是否大于21
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                AnimationUtils.circleHideView(mSearchBar,listenerAdapter);
            }else {
                AnimationUtils.fadeOutView(mRoot);
            }
        }else {
            mRoot.setVisibility(GONE);
        }

        //如果回掉接口存在则调用方法
        if (mSearchViewListener!=null){
            mSearchViewListener.onSearchViewClosed();
        }

        isOpen=false;

    }

    /**
     * 隐藏提示列表
     */
    private void dismissSuggestion(){
        mSuggestionListView.setVisibility(GONE);
    }

    //region View Methods
    /**
     * Handles any cleanup when focus is cleared from the view.
     */
    @Override
    public void clearFocus() {
        this.mClearingFocus=true;
        hideKeyboard(this);
        super.clearFocus();
        mSearchEditText.clearFocus();
        this.mClearingFocus=false;
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        // Don't accept if we are clearing focus, or if the view isn't focusable.
        return !(mClearingFocus || !isFocusable()) && mSearchEditText.requestFocus(direction, previouslyFocusedRect);
    }

    /**
     * 隐藏软键盘
     * @param view
     */
    private void hideKeyboard(View view){
        InputMethodManager  inputMethodManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示软键盘
     * @param view The view to attach the keyboard to.
     */
    private void showKeyboard(View view){
        //sdk<=10
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view.hasFocus()){
            view.clearFocus();
        }
        view.requestFocus();

        if (!isHardKeyboardAvailable()){
            InputMethodManager inputMethodManager= (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view,0);
        }
    }




    /**
     *
     * 接口 及 接口回调方法
     */
    public interface SearchViewListener{
        /**
         * 搜索框打开时触发的回掉方法
         */
        void onSearchViewOpened();
        /**
         * 搜索关闭时触发的回掉方法
         */
        void onSearchViewClosed();
    }


    public interface OnQueryTextListener{
        /**
         * Called when a search query is submitted.
         *
         * @param query The text that will be searched.
         * @return True when the query is handled by the listener, false to let the SearchView handle the default case.
         */
        boolean onQueryTextSubmit(String query);
        /**
         * Called when a search query is changed.
         * @param newText The new text of the search query.
         * @return True when the query is handled by the listener, false to let the SearchView handle the default case.
         */
        boolean onQueryTextChange(String newText);
    }

    /**
     *
     注册回调接口
     */
    public void setOnQueryTextListener(OnQueryTextListener mOnQueryTextListener) {
        this.mOnQueryTextListener = mOnQueryTextListener;
    }

    public void setSearchViewListener(SearchViewListener mSearchViewListener) {
        this.mSearchViewListener = mSearchViewListener;
    }







    /**
     * Method that checks if there's a physical keyboard on the phone.
     *
     * @return true if there's a physical keyboard connected, false otherwise.
     */
    private boolean isHardKeyboardAvailable() {
        return mContext.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;
    }


    /**
     * 设置提示列表点击接口
     * @param listener
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        mSuggestionListView.setOnItemClickListener(listener);
    }

    /**
     * 设置提示列表长点击接口
     * @param listener
     */
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener){
        mSuggestionListView.setOnItemLongClickListener(listener);
    }

    /**
     * Toggles the Tint click action.
     *
     * @param shouldClose - Whether the tint click should close the search view or not.
     */
    public void setCloseOnTintClick(boolean shouldClose) {
        mShouldCloseOnTintClick = shouldClose;
    }

    /**
     * Sets whether the MSV should be animated on open/close or not.
     *
     * @param mShouldAnimate - true if you want animations, false otherwise.
     */
    public void setShouldAnimate(boolean mShouldAnimate) {
        this.mShouldAnimate = mShouldAnimate;
    }

    /**
     * Sets whether the MSV should be keeping track of the submited queries or not.
     *
     * @param keepHistory - true if you want to save the search history, false otherwise.
     */
    public void setShouldKeepHistory(boolean keepHistory) {
        this.mShouldKeepHistory = keepHistory;
    }

    /**
     * Sets how many items you want to show from the history database.
     *
     * @param maxHistory - The number of items you want to display.
     */
    public static void setMaxHistoryResults(int maxHistory) {
        MAX_HISTORY = maxHistory;
    }


    /**
     * Sets the background of the SearchView.
     * @param background The drawable to use as a background.
     */
    @Override
    public void setBackground(Drawable background) {
        // Method changed in jelly bean for setting background.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mTintView.setBackground(background);
        } else {
            //noinspection deprecation
            mTintView.setBackgroundDrawable(background);
        }
    }

    /**
     * Sets the background color of the SearchView.
     *
     * @param color The color to use for the background.
     */
    @Override
    public void setBackgroundColor(int color) {
        setTintColor(color);
    }

    public void setSearchBarColor(int color) {
        // Set background color of search bar.
        mSearchEditText.setBackgroundColor(color);
    }

    /**
     * Change the color of the background tint.
     *
     * @param color The new color.
     */
    private void setTintColor(int color) {
        mTintView.setBackgroundColor(color);
    }

    /**
     * Sets the alpha value of the background tint.
     * @param alpha The alpha value, from 0 to 255.
     */
    public void setTintAlpha(int alpha) {
        if (alpha < 0 || alpha > 255) {return;}

        Drawable d = mTintView.getBackground();

        if (d instanceof ColorDrawable) {
            ColorDrawable cd = (ColorDrawable) d;
            int color = cd.getColor();
            int newColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));

            setTintColor(newColor);
        }
    }

    /**
     * Adjust the background tint alpha, based on a percentage.
     *
     * @param factor The factor of the alpha, from 0% to 100%.
     */
    public void adjustTintAlpha(float factor) {
        if (factor < 0 || factor > 1.0) {return;}

        Drawable d = mTintView.getBackground();

        if (d instanceof ColorDrawable) {
            ColorDrawable cd = (ColorDrawable) d;
            int color = cd.getColor();

            color = adjustAlpha(color,factor);

            mTintView.setBackgroundColor(color);
        }
    }

    /**
     * Adjust the alpha of a color based on a percent factor.
     *
     * @param color - The color you want to change the alpha value.
     * @param factor - The factor of the alpha, from 0% to 100%.
     * @return The color with the adjusted alpha value.
     */
    private int adjustAlpha(int color, float factor) {
        if (factor < 0){ return color;}

        int alpha = Math.round(Color.alpha(color) * factor);

        return Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color));
    }

    /**
     * Sets the text color of the EditText.
     * @param color The color to use for the EditText.
     */
    public void setTextColor(int color) {
        mSearchEditText.setTextColor(color);
    }

    /**
     * Sets the text color of the search hint.
     * @param color The color to be used for the hint text.
     */
    public void setHintTextColor(int color) {
        mSearchEditText.setHintTextColor(color);
    }

    /**
     * Sets the hint to be used for the search EditText.
     * @param hint The hint to be displayed in the search EditText.
     */
    public void setHint(CharSequence hint) {
        mSearchEditText.setHint(hint);
    }

    /**
     * Sets the icon for the clear action.
     * @param resourceId The resource ID of drawable that will represent the clear action.
     */
    public void setClearIcon(int resourceId) {
        mClear.setImageResource(resourceId);
    }

    /**
     * Sets the icon for the back action.
     * @param resourceId The resource Id of the drawable that will represent the back action.
     */
    public void setBackIcon(int resourceId) {
        mBack.setImageResource(resourceId);
    }

    /**
     * Sets the background of the suggestions ListView.
     *
     * @param resource The resource to use as a background for the
     *                 suggestions listview.
     */
    public void setSuggestionBackground(int resource) {
        if (resource > 0) {
            mSuggestionListView.setBackgroundResource(resource);
        }
    }

    /**
     * Sets the input type of the SearchEditText.
     *
     * @param inputType The input type to set to the EditText.
     */
    public void setInputType(int inputType) {
        mSearchEditText.setInputType(inputType);
    }

    /**
     * Sets the bar height if prefered to not use the existing actionbar height value
     *
     * @param height The value of the height in pixels
     */
    public void setSearchBarHeight(final int height) {
        mSearchBar.setMinimumHeight(height);
        mSearchBar.getLayoutParams().height = height;
    }

    /**
     * Returns the actual AppCompat ActionBar height value. This will be used as the default
     *
     * @return The value of the actual actionbar height in pixels
     */
    private int getAppCompatActionBarHeight(){
        TypedValue tv = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true);
        return getResources().getDimensionPixelSize(tv.resourceId);
    }

    /**
     * Retrieves the adapter.
     */
    public CursorAdapter getAdapter() {
        return mAdapter ;
    }
    //endregion

    //region Accessors
    /**
     * Determines if the search view is opened or closed.
     * @return True if the search view is open, false if it is closed.
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Gets the current text on the SearchView, if any. Returns an empty String if no text is available.
     * @return The current query, or an empty String if there's no query.
     */
    public String getCurrentQuery() {
        if (!TextUtils.isEmpty(mCurrentQuery)) {
            return mCurrentQuery.toString();
        }
        return "";
    }

    /** Determines if the user's voice is available
     * @return True if we can collect the user's voice, false otherwise.
     */
    private boolean isVoiceAvailable() {
        // Get package manager
        PackageManager packageManager = mContext.getPackageManager();

        // Gets a list of activities that can handle this intent.
        List<ResolveInfo> activities = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        // Returns true if we have at least one activity.
        return activities.size() > 0;
    }

    /**
     * Retrieves a suggestion at a given index in the adapter.
     *
     * @return The search suggestion for that index.
     */
    public String getSuggestionAtPosition(int position) {
        // If position is out of range just return empty string.
        if(position < 0 || position >= mAdapter.getCount()) {
            return "";
        } else {
            return mAdapter.getItem(position).toString();
        }
    }

    public void activityResumed(){
        refreshAdapterCursor();
    }

    /**
     * Add a single suggestion item to the suggestion list.
     * @param suggestion - The suggestion to be inserted on the database.
     */
    public synchronized void addSuggestion(String suggestion) {
        if (!TextUtils.isEmpty(suggestion)) {
            ContentValues value = new ContentValues();
            value.put(HistoryContract.HistoryEntry.COLUMN_QUERY, suggestion);
            value.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, System.currentTimeMillis());
            value.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY,0); // Saving as suggestion.


            mContext.getContentResolver().insert(
                    HistoryContract.HistoryEntry.CONTENT_URI,
                    value
            );
        }
    }

    /**
     * Removes a single suggestion from the list. <br/>
     * Disclaimer, this doesn't remove a single search history item, only suggestions.
     * @param suggestion - The suggestion to be removed.
     */
    public synchronized void removeSuggestion(String suggestion) {
        if (!TextUtils.isEmpty(suggestion)) {
            mContext.getContentResolver().delete(
                    HistoryContract.HistoryEntry.CONTENT_URI,
                    HistoryContract.HistoryEntry.TABLE_NAME +
                            "." +
                            HistoryContract.HistoryEntry.COLUMN_QUERY +
                            " = ? AND " +
                            HistoryContract.HistoryEntry.TABLE_NAME +
                            "." +
                            HistoryContract.HistoryEntry.COLUMN_IS_HISTORY +
                            " = ?"
                    ,
                    new String[]{suggestion,String.valueOf(0)}
            );
        }
    }

    public synchronized void addSuggestions(List<String> suggestions) {
        ArrayList<ContentValues> toSave = new ArrayList<>();
        for (String str : suggestions) {
            ContentValues value = new ContentValues();
            value.put(HistoryContract.HistoryEntry.COLUMN_QUERY, str);
            value.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, System.currentTimeMillis());
            value.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY,0); // Saving as suggestion.

            toSave.add(value);
        }

        ContentValues[] values = toSave.toArray(new ContentValues[toSave.size()]);

        mContext.getContentResolver().bulkInsert(
                HistoryContract.HistoryEntry.CONTENT_URI,
                values
        );
    }

    public void addSuggestions(String[] suggestions) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(suggestions));
        addSuggestions(list);
    }

    public synchronized void clearSuggestions() {
        mContext.getContentResolver().delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                new String[]{"0"}
        );
    }

    public synchronized void clearHistory() {
        mContext.getContentResolver().delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                new String[]{"1"}
        );
    }

    public synchronized void clearAll() {
        mContext.getContentResolver().delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                null
        );
    }

    //endregion

}
