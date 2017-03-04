//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SearchResultsDialog.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.scichart.examples.R;
import com.scichart.examples.demo.search.SearchResultsDataAdapter;
import com.scichart.examples.demo.viewobjects.ExampleSearchView;

import java.util.List;

public class SearchResultsDialog extends LinearLayout implements AdapterView.OnItemClickListener {

    private ListView resultsList;
    private SearchResultsDataAdapter dataAdapter;

    private IOnCloseDialogListener closeListener;
    private IOnSearchItemClickListener searchItemClickListener;

    public SearchResultsDialog(Context context) {
        super(context);
        init(context);
    }

    public SearchResultsDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.search_results_dialog, this, true);

        dataAdapter = new SearchResultsDataAdapter(context);
        resultsList = (ListView) findViewById(R.id.results_list);
        resultsList.setAdapter(dataAdapter);
        resultsList.setOnItemClickListener(this);
        resultsList.setEmptyView(findViewById(android.R.id.empty));
    }

    public void setData(List<ExampleSearchView> data) {
        dataAdapter.setData(data);
        dataAdapter.notifyDataSetChanged();
    }

    public void setOnCloseListener(IOnCloseDialogListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setOnSearchItemClickListener(IOnSearchItemClickListener searchItemClickListener) {
        this.searchItemClickListener = searchItemClickListener;
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        final ExampleSearchView selectedExample = dataAdapter.getItem(position);
        if (searchItemClickListener != null) {
            searchItemClickListener.onSearchItemClick(selectedExample.title);
        }
    }

    public void setInputString(String inputString) {
        dataAdapter.setInputString(inputString);
    }

    public interface IOnCloseDialogListener {
        void onClose();
    }

    public interface IOnSearchItemClickListener {
        void onSearchItemClick(String id);
    }

}
