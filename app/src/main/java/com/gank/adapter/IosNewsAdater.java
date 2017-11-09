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
import com.gank.bean.IosNews;
import com.gank.interfaze.OnRecyclerViewOnClickListener;

import java.util.List;

/**
 * Created by Swy on 2017/6/9.
 */

public class IosNewsAdater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<IosNews.Question> list;
    private Context context;
    private final LayoutInflater inflater;

    private OnRecyclerViewOnClickListener listener;

    private static final int TYPE_NORMTAL=0;
    private static final  int TYPE_FOOTER=1;
    private static final int TYPE_NO_IMG=3;

    public IosNewsAdater(List<IosNews.Question> list, Context context) {
        this.list = list;
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_NORMTAL:
                return  new NormalViewHolder(inflater.inflate(R.layout.home_list_item_layout,parent,false),listener);
            case TYPE_NO_IMG:
                return new NoImageViewHolder(inflater.inflate(R.layout.home_list_item_without_image,parent,false),listener);
            case TYPE_FOOTER:
                return new FooterViewHolder(inflater.inflate(R.layout.list_footer,parent,false));
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (! (holder instanceof FooterViewHolder)){
            IosNews.Question item =list.get(position);
            if (holder instanceof NormalViewHolder){
                Glide.with(context)
                        .load(item.getImages().get(0))
                        .asBitmap()
                        .placeholder(R.mipmap.loading)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .error(R.mipmap.loading)
                        .centerCrop()
                        .into(((NormalViewHolder) holder).imageView);
                ((NormalViewHolder) holder).textView.setText(item.getDesc());
            }else if (holder instanceof  NoImageViewHolder){
                ((NoImageViewHolder) holder).textViewNoImg.setText(item.getDesc());
            }
        }
    }


    public void setItemOnClickListener(OnRecyclerViewOnClickListener listener){
        this.listener=listener;
    }
    @Override
    public int getItemCount() {
        return list.size()+1;
    }


    @Override
    public int getItemViewType(int position) {
        if (position==getItemCount()-1){
            return TYPE_FOOTER;
        }if (list.get(position).getImages()==null){
            return TYPE_NO_IMG;
        }
        return TYPE_NORMTAL;
    }

    class NormalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnRecyclerViewOnClickListener listener;
        ImageView imageView;
        TextView textView;

        public NormalViewHolder(View itemView,OnRecyclerViewOnClickListener listener) {
            super(itemView);
            this.listener=listener;
            imageView= (ImageView) itemView.findViewById(R.id.imageViewCover);
            textView= (TextView) itemView.findViewById(R.id.textViewTitle);
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

        OnRecyclerViewOnClickListener listener;
        TextView textViewNoImg;
        public NoImageViewHolder(View itemView,OnRecyclerViewOnClickListener listener) {
            super(itemView);
            this.listener=listener;
            textViewNoImg = (TextView) itemView.findViewById(R.id.textViewTitle);
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
