package com.intervencije.com.intervencije.redList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.intervencije.R;

import java.util.ArrayList;

public class RedListAdapter extends BaseAdapter {

    private ArrayList listData;

    private LayoutInflater layoutInflater;

    public RedListAdapter(Context context, ArrayList listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateRedList(ArrayList newList) {
        listData.clear();
        listData.addAll(newList);
        this.notifyDataSetChanged();
    }

    public void clear() {
        listData.clear();
        this.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.red_list_row_item, null);
            holder = new ViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.nameRed);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RedListItems item = (RedListItems) listData.get(position);
        holder.nameView.setText(item.getName() + " " + item.getSurname());


        return convertView;
    }

    static class ViewHolder {
        TextView nameView;
    }
}
