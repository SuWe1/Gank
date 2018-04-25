package com.gank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gank.R;
import com.gank.bean.BaseBean;
import com.gank.bean.FrontNews;
import com.gank.bean.GankNews;
import com.gank.bean.IosNews;
import com.gank.interfaze.OnRecyclerViewOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swy on 2017/3/6.
 */

public class BookMarksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<BaseBean> newsList;


    private OnRecyclerViewOnClickListener listener;

    public static final int TYPE_Gank_NORMAL = 0;
    public static final int TYPE_Gank_WITH_HEADER = 1;
    public static final int TYPE_Front_NORMAL = 2;
    public static final int TYPE_Front_WITH_HEADER = 3;
    public static final int TYPE_IOS_NORMAL = 4;
    public static final int TYPE_IOS_WITH_HEADER = 5;

    private List<Integer> types;

    public BookMarksAdapter(Context context, ArrayList<BaseBean> newsList, ArrayList<Integer> types) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.types = types;
        this.newsList = newsList;
    }


    public void setItemOnClickListener(OnRecyclerViewOnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_Front_NORMAL:
            case TYPE_Gank_NORMAL:
            case TYPE_IOS_NORMAL:
                return new GankViewHolder(inflater.inflate(R.layout.home_list_item_layout, parent, false), this.listener);
            default:
                return new GankTitleViewHolder(inflater.inflate(R.layout.bookmark_header, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (types.get(position)) {
            case TYPE_Gank_WITH_HEADER:
                ((GankTitleViewHolder) holder).textView.setText(context.getResources().getString(R.string.bookmarks_Android_title));
                break;
            case TYPE_Gank_NORMAL:
                //第一个为title
                GankNews.Question ad = (GankNews.Question) newsList.get(position);
                if (ad.getImages() == null) {
                    ((GankViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                } else {
                    Glide.with(context)
                            .load(ad.getImages().get(0))
                            .asBitmap()
                            .centerCrop()
                            .placeholder(R.mipmap.loading)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .error(R.mipmap.loading)
                            .into(((GankViewHolder) holder).imageView);
                }
                ((GankViewHolder) holder).textView.setText(ad.getDesc());
                break;
            case TYPE_Front_WITH_HEADER:
                ((GankTitleViewHolder) holder).textView.setText(context.getResources().getString(R.string.bookmarks_Front_title));
                break;
            case TYPE_Front_NORMAL:
                FrontNews.Question front = (FrontNews.Question) newsList.get(position);
                if (front.getImages() == null) {
                    ((GankViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                } else {
                    Glide.with(context)
                            .load(front.getImages().get(0))
                            .asBitmap()
                            .centerCrop()
                            .placeholder(R.mipmap.loading)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .error(R.mipmap.loading)
                            .into(((GankViewHolder) holder).imageView);
                }
                ((GankViewHolder) holder).textView.setText(front.getDesc());
                break;
            case TYPE_IOS_WITH_HEADER:
                ((GankTitleViewHolder) holder).textView.setText(context.getResources().getString(R.string.bookmarks_Ios_title));
                break;
            case TYPE_IOS_NORMAL:
                IosNews.Question ios = (IosNews.Question) newsList.get(position);
                if (ios.getImages() == null) {
                    ((GankViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                } else {
                    Glide.with(context)
                            .load(ios.getImages().get(0))
                            .asBitmap()
                            .centerCrop()
                            .placeholder(R.mipmap.loading)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .error(R.mipmap.loading)
                            .into(((GankViewHolder) holder).imageView);
                }
                ((GankViewHolder) holder).textView.setText(ios.getDesc());
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    @Override
    public int getItemViewType(int position) {
        return types.get(position);
    }

    class GankViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;

        OnRecyclerViewOnClickListener listener;

        public GankViewHolder(View itemView, OnRecyclerViewOnClickListener listener) {
            super(itemView);
            this.listener = listener;
            imageView = (ImageView) itemView.findViewById(R.id.imageViewCover);
            textView = (TextView) itemView.findViewById(R.id.textViewTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(v, getLayoutPosition());
            }
        }
    }

    //分类 没有点击事件  考虑能不能用收缩列表
    class GankTitleViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public GankTitleViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textViewType);
        }
    }
}
