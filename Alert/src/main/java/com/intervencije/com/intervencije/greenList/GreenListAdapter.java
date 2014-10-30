package com.intervencije.com.intervencije.greenList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intervencije.R;

import java.util.ArrayList;

public class GreenListAdapter extends BaseAdapter {

    private ArrayList listData;

    private LayoutInflater layoutInflater;

    public GreenListAdapter(Context context, ArrayList listData) {
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

    public void updateGreenList(ArrayList newList) {
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
            convertView = layoutInflater.inflate(R.layout.green_list_row_item, null);
            holder = new ViewHolder();
            holder.greenWrapper = (RelativeLayout) convertView.findViewById(R.id.greenwrapper);
            holder.nameView = (TextView) convertView.findViewById(R.id.name);
            holder.specialsView = (TextView) convertView.findViewById(R.id.specials);
            holder.driverLicenceView = (TextView) convertView.findViewById(R.id.driverLicence);
            holder.timeView = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GreenListItems item = (GreenListItems) listData.get(position);
        if(item.getPersonYellow())
            holder.greenWrapper.setBackgroundColor(Color.YELLOW);

        holder.nameView.setText(item.getName() + " " + item.getSurname());
        holder.specialsView.setText(item.getSpecialThings());
        holder.driverLicenceView.setText(item.getDriverLicence());
        holder.timeView.setText(item.getTimeOfcall());


        return convertView;
    }

    static class ViewHolder {
        RelativeLayout greenWrapper;
        TextView nameView;
        TextView specialsView;
        TextView driverLicenceView;
        TextView timeView;
    }
}
