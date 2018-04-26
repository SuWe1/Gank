package com.gank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gank.R;
import com.gank.bean.GankNews;
import com.gank.interfaze.OnRecyclerViewOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swy on 2017/3/4.
 */

public class GankNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "GankNewsAdapter";
    private Context context;
    private List<GankNews.Question> list=new ArrayList<>();
    private final LayoutInflater inflater;
    //设置回调
    private OnRecyclerViewOnClickListener listener;

    private static final int TYPE_NORMTAL=0;
    private static final  int TYPE_FOOTER=1;
    private static final int TYPE_NO_IMG=3;

    public GankNewsAdapter( List<GankNews.Question> list, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_NORMTAL:
                return new NormalViewHolder(inflater.inflate(R.layout.home_list_item_layout,parent,false),listener);
            case TYPE_FOOTER:
                return new FooterViewHolder(inflater.inflate(R.layout.view_list_footer,parent,false));
            case TYPE_NO_IMG:
                return new NoImageViewHolder(inflater.inflate(R.layout.home_list_item_without_image,parent,false),listener);
                default:
                    break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof FooterViewHolder)){
            GankNews.Question item=list.get(position);
            if (item!=null){
                if (holder instanceof NormalViewHolder){
                    NormalViewHolder normalViewHolder= (NormalViewHolder) holder;
                    Glide.with(context)
                            .load(item.getImages().get(0))
                            .asBitmap()
                            .placeholder(R.mipmap.loading)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .error(R.mipmap.loading)
                            .centerCrop()
                            .into(((NormalViewHolder) holder).imageView);
                    normalViewHolder.textViewTitle.setText(item.getDesc());
                    String time =item.getPublishedAt().substring(0,10);
                    normalViewHolder.textViewDate.setText(time);
                    normalViewHolder.textViewName.setText(item.getWho());
                }else if (holder instanceof NoImageViewHolder){
                    NoImageViewHolder noImageViewHolder= (NoImageViewHolder) holder;
                    noImageViewHolder.textViewNoImg.setText(item.getDesc());
                    noImageViewHolder.textViewDate.setText(item.getPublishedAt().substring(0,10));
                    noImageViewHolder.textViewName.setText(item.getWho());
                }
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

    class FooterViewHolder extends RecyclerView.ViewHolder{
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
