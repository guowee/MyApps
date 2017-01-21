package com.example.wifireader;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WifiAdapter extends BaseAdapter {

    private Context context;
    private List<WifiConfig> list;

    public WifiAdapter(Context context, List<WifiConfig> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText("WIFI:" + list.get(position).getSsid() + "\n密码:" + list.get(position).getPassword());
        return convertView;
    }

}
