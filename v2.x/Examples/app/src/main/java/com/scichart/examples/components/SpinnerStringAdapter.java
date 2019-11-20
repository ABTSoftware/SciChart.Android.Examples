//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SpinnerStringAdapter.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.components;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.scichart.examples.R;

import java.util.List;

public class SpinnerStringAdapter extends ArrayAdapter<String> {
    private final LayoutInflater inflater;

    public SpinnerStringAdapter(Context context, @ArrayRes int array) {
        super(context,  R.layout.example_sortby_spinner_top_item, context.getResources().getStringArray(array));

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SpinnerStringAdapter(Context context, List<String> strings) {
        super(context, R.layout.example_sortby_spinner_top_item, strings);

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.example_sortby_spinner_top_item, parent, false);

        final TextView title = (TextView) view.findViewById(R.id.text);
        title.setText(getItem(position));
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.example_sortby_spinner_item, parent, false);
        final TextView title = (TextView) view.findViewById(R.id.text);

        title.setText(getItem(position));
        return view;
    }
}