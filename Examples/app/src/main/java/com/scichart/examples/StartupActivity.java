//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StartupActivity.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

import com.scichart.examples.demo.DemoKeys;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.startup_activity);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        ButterKnife.bind(this, this);

        final SciChartApp instance = SciChartApp.getInstance();
        if(!instance.getModule().isInitialized()) {
            new ParseExampleTasks(this).execute();
        }
    }

    @OnClick(R.id.charts2dCard)
    void open2dChartsExampleList() {
        openExampleListActivity(DemoKeys.CHARTS_2D);
    }

    @OnClick(R.id.charts3dCard)
    void open3dChartsExampleList() {
        openExampleListActivity(DemoKeys.CHARTS_3D);
    }

    @OnClick(R.id.featuredChartsCard)
    void openFeaturedExampleList() {
        openExampleListActivity(DemoKeys.FEATURED);
    }

    private void openExampleListActivity(String category) {
        final Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(DemoKeys.CATEGORY_ID, category);
        startActivity(intent);
    }

    private static class ParseExampleTasks extends AsyncTask<Void, Void, Void> {
        private final Context context;

        private ProgressDialog progressDialog;

        private ParseExampleTasks(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context, R.style.SciChart_ExportProgressDialogStyle);
            progressDialog.setTitle("");
            progressDialog.setMessage("Loading data...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SciChartApp.getInstance().getModule().initialize();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
