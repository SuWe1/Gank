package com.gank.interfaze;

import android.view.View;

/**
 * Created by Swy on 2017/3/4.
 */

public interface OnRecyclerViewOnClickListener {
    void onItemClick(View v,int position);
    void onItemLongClick(View v,int position);
}
