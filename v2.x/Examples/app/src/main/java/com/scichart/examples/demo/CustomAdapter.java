//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomAdapter.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scichart.examples.R;
import com.scichart.examples.demo.viewobjects.ExampleView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private final int HEADER_ITEM = 0;
    private final int GENERAL_ITEM = 1;

    private List<ExampleView> data;

    private Context context;
    private LayoutInflater mInflater;

    public CustomAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).isHeader ? HEADER_ITEM : GENERAL_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case GENERAL_ITEM:
                    convertView = mInflater.inflate(R.layout.general_list_item, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.text);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
                    break;
                case HEADER_ITEM:
                    convertView = mInflater.inflate(R.layout.header_list_item, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
                    convertView.setEnabled(false);
                    convertView.setOnClickListener(null);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ExampleView dataItem = this.data.get(position);
        holder.textView.setText(dataItem.title);
        if (holder.imageView != null) {
            final Drawable resource = findResourceByName(dataItem.iconPath);
            if (resource != null) {
                holder.imageView.setImageDrawable(resource);
            } else {
                holder.imageView.setImageResource(R.drawable.not_available);
            }
        }

        return convertView;
    }

    public void setSortStrategy(ISortBy sortStrategy) {
        final List<ExampleView> dataSet = sortStrategy.sort();
        setData(dataSet);
    }

    public static class ViewHolder {
        public TextView textView;
        public ImageView imageView;
    }

    public void setData(List<ExampleView> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    private Drawable findResourceByName(String name) {
        // without file extension
        final String uri = "@drawable/" + name.toLowerCase();
        final int resource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        try {
            return context.getResources().getDrawable(resource);
        } catch (Resources.NotFoundException ex) {
            return null;
        }
    }

}
