package com.example.searchview.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.searchview.R;
import com.example.searchview.db.HistoryContract;

/**
 * Created by Swy on 2018/1/28.
 */

public class HistorySearchCursorAdapter extends CursorAdapter {

    public HistorySearchCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public HistorySearchCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public HistorySearchCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return  LayoutInflater.from(context).inflate(R.layout.search_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
         ListViewHolder lvh=new ListViewHolder(view);
         view.setTag(lvh);
         String text=cursor.getString(cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_QUERY));

         boolean isHistory =cursor.getInt(cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY))!=0;

         lvh.sv_text.setText(text);

         if (isHistory){
             lvh.sv_icon.setImageResource(R.drawable.ic_history_white);
         }else {
             lvh.sv_icon.setImageResource(R.drawable.ic_action_search_white);
         }
    }

    @Override
    public Object getItem(int position) {
        String retString="";
        Cursor cursor=getCursor();
        // 判断数据是否存在
        if (cursor.moveToPosition(position)){
            retString=cursor.getString(cursor.getColumnIndexOrThrow(HistoryContract.HistoryEntry.COLUMN_QUERY));
        }
        return retString;
    }

    private class ListViewHolder{
        ImageView sv_icon;
        TextView sv_text;

        public ListViewHolder(View contentView){
            sv_icon= (ImageView) contentView.findViewById(R.id.sv_icon);
            sv_text= (TextView) contentView.findViewById(R.id.sv_text);
        }
    }
}
