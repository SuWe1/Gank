package com.gank.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gank.R;
import com.gank.interfaze.OnRecyclerViewOnClickListener;

/**
 * Created by Swy on 2017/12/28.
 */

class NormalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView imageView;
    TextView textViewTitle;
    TextView textViewDate;
    TextView textViewName;
    OnRecyclerViewOnClickListener listener;

    public NormalViewHolder(View itemView, OnRecyclerViewOnClickListener listener) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.imageViewCover);
        textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
        textViewDate = (TextView) itemView.findViewById(R.id.textviewDate);
        textViewName = (TextView) itemView.findViewById(R.id.textviewName);
        this.listener = listener;
        //设置监听
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onItemClick(v, getLayoutPosition());
        }
    }
}
