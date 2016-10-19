package com.apress.gerber.simplelayouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
* Created by Clifton
* Copyright 8/22/2014.
*/
class PersonAdapter extends BaseAdapter {
    private final Context context;
    private final int layout;
    private Person[] listItems;

    public PersonAdapter(Context context, int layout, Person[] listItems) {
        this.context = context;
        this.layout = layout;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.length;
    }

    @Override
    public Object getItem(int i) {
        return listItems[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view==null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, parent, false);
        }
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
        text1.setText(listItems[position].name);
        text2.setText(listItems[position].website);
        return view;
    }
}
