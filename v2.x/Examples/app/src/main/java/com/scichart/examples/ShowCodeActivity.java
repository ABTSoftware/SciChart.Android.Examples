//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ShowCodeActivity.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.scichart.examples.demo.helpers.Example;

import java.util.ArrayList;

import static com.scichart.examples.demo.DemoKeys.EXAMPLE_ID;
import static com.scichart.examples.demo.utils.FileUtils.loadFromFile;

public class ShowCodeActivity extends Activity {

    private String exampleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_code_activity);

        customizeWindowSize();

        initActivityAttributes(savedInstanceState);

        final Example example = HomeActivity.getModule().getExampleByTitle(exampleId);

        if (example != null) {
            final Spinner sourceFilesSpinner = (Spinner) findViewById(R.id.sourceFiles);
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, new ArrayList<>(example.sourceFiles.keySet())); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sourceFilesSpinner.setAdapter(spinnerArrayAdapter);
            sourceFilesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    final ListView sourceCodeView = (ListView) findViewById(R.id.sourceCode);
                    final String data = loadFromFile(spinnerArrayAdapter.getItem(position));
                    if (data != null) {
                        sourceCodeView.setAdapter(
                                new DataItemAdapter(
                                        ShowCodeActivity.this, R.layout.source_code_listview, data.split("<br>")));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    private void initActivityAttributes(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            if (extras != null) {
                exampleId = extras.getString(EXAMPLE_ID);
            }
        } else {
            exampleId = savedInstanceState.getString(EXAMPLE_ID);
        }
    }

    private void customizeWindowSize() {
        final DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = height - 100;
        params.width = width;

        this.getWindow().setAttributes(params);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXAMPLE_ID, exampleId);
    }

    private class DataItemAdapter extends ArrayAdapter<String> {

        private final String[] data;

        public DataItemAdapter(Context context, int resource, String[] data) {
            super(context, resource);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public String getItem(int i) {
            return data[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);

            final TextView textView = (TextView) view.findViewById(R.id.text);
            // Set here, it doesn't  work when set in xml due to a bug
            textView.setHorizontallyScrolling(true);

            final String dataItem = data[position];
            textView.setText(Html.fromHtml(dataItem));

            return view;
        }

    };

}
