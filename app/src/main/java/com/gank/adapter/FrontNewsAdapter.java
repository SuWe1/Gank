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
import com.gank.bean.FrontNews;
import com.gank.interfaze.OnRecyclerViewOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11033 on 2017/3/18.
 */

public class FrontNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<FrontNews.Question> list=new ArrayList<>();
    private final LayoutInflater inflater;
    //设置回调
    private OnRecyclerViewOnClickListener listener;

    private static final int TYPE_NORMTAL=0;
    private static final  int TYPE_FOOTER=1;
    private static final int TYPE_NO_IMG=3;

    public FrontNewsAdapter(List<FrontNews.Question> list, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_NORMTAL:
                return new FrontNewsAdapter.NormalViewHolder(inflater.inflate(R.layout.home_list_item_layout,parent,false),listener);
            case TYPE_FOOTER:
                return new FrontNewsAdapter.FooterViewHolder(inflater.inflate(R.layout.list_footer,parent,false));
            case TYPE_NO_IMG:
                return new FrontNewsAdapter.NoImageViewHolder(inflater.inflate(R.layout.home_list_item_without_image,parent,false),listener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof FrontNewsAdapter.FooterViewHolder)){
            FrontNews.Question item=list.get(position);
            if (holder instanceof FrontNewsAdapter.NormalViewHolder){
                Glide.with(context)
                        .load(item.getImages().get(0))
                        .asBitmap()
                        .placeholder(R.mipmap.loading)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .error(R.mipmap.loading)
                        .centerCrop()
                        .into(((FrontNewsAdapter.NormalViewHolder) holder).imageView);
                ((FrontNewsAdapter.NormalViewHolder) holder).textView.setText(item.getDesc());
            }else if (holder instanceof FrontNewsAdapter.NoImageViewHolder){
                ((FrontNewsAdapter.NoImageViewHolder) holder).textViewNoImg.setText(item.getDesc());
            }
        }


    }

    //大小加上footer
    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
//        Log.i(TAG, "getItemViewType: "+list.size());
        if (position==getItemCount()-1){
            return TYPE_FOOTER;
        }if (list.get(position).getImages()==null){
            return TYPE_NO_IMG;
        }
        return TYPE_NORMTAL;
    }

    public void setItemOnClickListener(OnRecyclerViewOnClickListener listener){
        this.listener=listener;
    }

    class NormalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView;
        OnRecyclerViewOnClickListener listener;

        public NormalViewHolder(View itemView, OnRecyclerViewOnClickListener listener) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.imageViewCover);
            textView= (TextView) itemView.findViewById(R.id.textViewTitle);
            this.listener=listener;
            //设置监听
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener!=null){
                listener.onItemClick(v,getLayoutPosition());
            }
        }
    }

    class NoImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewNoImg;
        OnRecyclerViewOnClickListener listener;
        public NoImageViewHolder(View itemView ,OnRecyclerViewOnClickListener listener) {
            super(itemView);
            textViewNoImg = (TextView) itemView.findViewById(R.id.textViewTitle);
            this.listener=listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener!=null){
                listener.onItemClick(v,getLayoutPosition());
            }
        }
    }
    class FooterViewHolder extends RecyclerView.ViewHolder{
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
