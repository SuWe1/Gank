package com.gank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gank.R;
import com.gank.bean.MeiziNews;
import com.gank.interfaze.OnMeiziRecyclerViewOnClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swy on 2017/3/17.
 */

public class MeiziAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MeiziAdapter";
    private Context context;
    private List<MeiziNews.Question> list = new ArrayList<>();
    private final LayoutInflater inflater;

    //点击事件回调
    private OnMeiziRecyclerViewOnClickListener listener;

    private static final int TYPE_NORMTAL = 0;
    private static final int TYPE_FOOTER = 1;

    public MeiziAdapter(List<MeiziNews.Question> list, Context context) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    //点击事件回调
    public void setItemOnClickListener(OnMeiziRecyclerViewOnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_NORMTAL:
                return new MeiziViewHolder(inflater.inflate(R.layout.home_list_item_meizi, parent, false), listener);
            case TYPE_FOOTER:
                return new FooterViewHolder(inflater.inflate(R.layout.view_list_footer, parent, false));
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof FooterViewHolder)) {
            MeiziNews.Question item = list.get(position);
            Glide.with(context)
                    .load(item.getUrl())
                    .asBitmap()
                    .placeholder(R.mipmap.loading)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .fitCenter()
                    .error(R.mipmap.loading)
                    .into(((MeiziViewHolder) holder).imageView);
            String time = item.getCreatedAt().substring(0, 10);
            ((MeiziViewHolder) holder).textView.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMTAL;
    }


    class MeiziViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        Button button;
        TextView textView;
        OnMeiziRecyclerViewOnClickListener onRecyclerViewOnClickListener;

        //设置监听
        public MeiziViewHolder(View itemView, OnMeiziRecyclerViewOnClickListener listener) {
            super(itemView);
            this.onRecyclerViewOnClickListener = listener;
            imageView = (ImageView) itemView.findViewById(R.id.meiziImg);
            button = (Button) itemView.findViewById(R.id.meiziBtn);
            textView = (TextView) itemView.findViewById(R.id.meiziTextTime);
//            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);
            button.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onRecyclerViewOnClickListener != null) {
                onRecyclerViewOnClickListener.onItemClick(v, imageView, getLayoutPosition());
            }
        }

        //return的值决定是否在长按后再加一个短按动作
        //true为不加短按,false为加入短按
        /*@Override
        public boolean onLongClick(View v) {
            if (onRecyclerViewOnClickListener!=null){
                onRecyclerViewOnClickListener.onItemLongClick(v,getLayoutPosition());
            }
            return true;
        }*/
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
