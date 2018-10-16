//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ZipAndShareAllProjectsTask.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.export;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.scichart.examples.R;
import com.scichart.examples.demo.helpers.Example;
import com.scichart.examples.demo.helpers.Module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ZipAndShareAllProjectsTask extends AsyncTask<Void, Void, Void> {
    private static final String SCI_CHART_PROJECTS = "SciChartProjects";

    private static final String BATCH_SCRIPT_CONTENT =
            "@echo off \n" +
            "for /d %%i in (.\\*) do (\n" +
                // need to copy local.properties to sub project to get location of android sdk
                "\tcopy local.properties %%i\\local.properties\n" +
                // copy aars to subproject
                "\tcopy charting-release.aar %%i\\app\\libs\\charting-release.aar\n" +
                "\tcopy data-release.aar %%i\\app\\libs\\data-release.aar\n" +
                "\tcopy core-release.aar %%i\\app\\libs\\core-release.aar\n" +
                "\tcopy drawing-release.aar %%i\\app\\libs\\drawing-release.aar\n" +
                "\tcopy extensions-release.aar %%i\\app\\libs\\extensions-release.aar\n" +
                // compile subproject
                "\tcall %%i\\gradlew.bat -p %%i\\ assemble\n " +
                "\tif ERRORLEVEL 1 (\n" +
                    "\t\t@echo - Example %%i : Failed to compile >> log.txt\n" +
                "\t) else (\n" +
                    "\t\t@echo - Example %%i : OK >> log.txt\n" +
                "\t)\n" +
            ")";

    private final Context context;
    private final Module module;

    private final File saveDir;

    private ProgressDialog progressDialog;

    public ZipAndShareAllProjectsTask(Context context, Module module, File saveDir) {
        this.context = context;
        this.module = module;
        this.saveDir = saveDir;
    }

    public ZipAndShareAllProjectsTask(Context context, Module module) {
        this(context, module, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context, R.style.SciChart_ExportProgressDialogStyle);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(context.getString(R.string.export_all_examples));
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(module.getExamples().size());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    protected Void doInBackground(Void... params) {
        final File cacheDir = context.getCacheDir();
        final File exportFolder = new File(cacheDir, SCI_CHART_PROJECTS);
        try {
            // remove temp dir if it exists
            ExportManager.deleteDir(exportFolder);

            // recreate temp dir
            if (exportFolder.mkdirs()) {
                // iterate examples and create zips
                final List<Example> examples = module.getExamples();
                for (int i = 0, size = examples.size(); i < size; i++) {
                    final Example example = examples.get(i);

                    try {
                        ZipAndShareTask.exportProject(context, example, exportFolder, false);
                    } finally {
                        Log.d("Export all projects", String.format("Exported %d of %d: %s", i + 1, size, example.title));
                    }

                    progressDialog.incrementProgressBy(1);
                }

                final File batchScript = new File(exportFolder, "compileExportedProjects.bat");

                try {
                    writeBatchScript(batchScript);
                } catch (IOException e) {
                    Log.e("Export all projects", "Error during export batch script", e);
                }

                // zip examples to one big zip file
                final File zipFile = new File(saveDir, SCI_CHART_PROJECTS + ".zip");
                ZipUtil.zipFolder(exportFolder, zipFile);

                // send zip file
                final String title = String.format(context.getString(R.string.export_option), SCI_CHART_PROJECTS);
                ExportManager.sendZip(zipFile, SCI_CHART_PROJECTS, "", context, title);
            }
        } finally {
            // remove temporary files
            ExportManager.deleteDir(exportFolder);
        }

        return null;
    }

    private static void writeBatchScript(File batchScript) throws IOException {
        if(batchScript.createNewFile()) {
            try (FileOutputStream stream = new FileOutputStream(batchScript)) {
                stream.write(BATCH_SCRIPT_CONTENT.getBytes());
            }
        }
    }
}
