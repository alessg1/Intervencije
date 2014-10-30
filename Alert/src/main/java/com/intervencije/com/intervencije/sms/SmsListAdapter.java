package com.intervencije.com.intervencije.sms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.intervencije.R;

import java.sql.SQLException;
import java.util.ArrayList;

public class SmsListAdapter extends BaseAdapter {

    private ArrayList listData;

    private LayoutInflater layoutInflater;

    private Context context;


    public SmsListAdapter(Context context, ArrayList listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;

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

    public void updateSmsList(ArrayList newList) {
        listData.clear();
        listData.addAll(newList);
        this.notifyDataSetChanged();
    }


    public void deleteItem(int position) {

        SmsDataSource smsDataSource = new SmsDataSource(context);

        try {
            smsDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        smsDataSource.deleteSMS((SmsListItems) listData.get(position));

        smsDataSource.close();

        listData.remove(position);
        this.notifyDataSetChanged();
    }

    public void clear() {
        listData.clear();
        this.notifyDataSetChanged();
    }


    public void update(SmsDataSource smsDataSource) {
        listData.clear();
        listData = smsDataSource.getAllSms();
        this.notifyDataSetChanged();
    }

    public void deleteAllItems() {
        listData.clear();
        this.notifyDataSetChanged();

        SmsDataSource smsDataSource = new SmsDataSource(context);

        try {
            smsDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        smsDataSource.deleteAllSms();

        smsDataSource.close();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.sms_list_row, null);
            holder = new ViewHolder();
            holder.bodyView = (TextView) convertView.findViewById(R.id.body);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.number = (TextView) convertView.findViewById(R.id.number);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SmsListItems item = (SmsListItems) listData.get(position);
        holder.bodyView.setText(item.getBody());
        holder.date.setText(item.getDate());
        holder.time.setText(item.getTime());
        holder.number.setText(item.getNumber());

        return convertView;
    }


    static class ViewHolder {
        TextView bodyView;
        TextView date;
        TextView time;
        TextView number;
    }
}
