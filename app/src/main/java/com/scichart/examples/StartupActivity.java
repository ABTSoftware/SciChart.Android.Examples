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

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scichart.examples.databinding.StartupActivityBinding;
import com.scichart.examples.demo.DemoKeys;

public class StartupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final StartupActivityBinding binding = StartupActivityBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        final SciChartApp instance = SciChartApp.getInstance();
        if(!instance.getModule().isInitialized()) {
            new ParseExampleTasks(this).execute();
        }

        int statusBarHeightId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getResources().getDimensionPixelSize(statusBarHeightId);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,statusBarHeight,0,0);
        binding.startActivityLogo.setLayoutParams(lp);

        binding.charts2d.setOnClickListener(v -> openExampleListActivity(DemoKeys.CHARTS_2D));
        binding.charts3d.setOnClickListener(v -> openExampleListActivity(DemoKeys.CHARTS_3D));
        binding.featuredCharts.setOnClickListener(v -> openExampleListActivity(DemoKeys.FEATURED));
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
