//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExportManager.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.export;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.scichart.core.framework.DisposableBase;
import com.scichart.examples.BuildConfig;
import com.scichart.examples.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

final class ExportManager extends DisposableBase {
    private static final String APP_FOLDER = "app/";
    private static final String AUTHORITY = "com.scichart.examples.demo.SciChartFilesProvider";

    private final Context context;
    private final String exampleName;

    private final File exportFolder;
    private final File assetsFolder;
    private final File resFolder;
    private final File javaFolder;

    private final File saveDir;

    public ExportManager(Context context, String exampleName, File saveDir) {
        this.context = context;
        this.exampleName = exampleName;

        final File cacheDir = context.getCacheDir();
        this.exportFolder = new File(cacheDir, exampleName);

        this.assetsFolder = new File(exportFolder, APP_FOLDER + BuildConfig.ASSETS_FOLDER);
        this.resFolder = new File(exportFolder, APP_FOLDER + BuildConfig.RES_FOLDER);
        this.javaFolder = new File(exportFolder, APP_FOLDER + BuildConfig.JAVA_FOLDER);

        this.saveDir = saveDir;
    }

    public final void writeIntoAssetFolder(String fileName, byte[] content) {
        try {
            writeAssetToFolder(assetsFolder, fileName, content);
        } catch (IOException e) {
            Log.e("writeIntoAssetFolder", fileName, e);
        }
    }

    public final void writeIntoResFolder(String fileName, byte[] content) {
        try {
            writeAssetToFolder(resFolder, fileName, content);
        } catch (IOException e) {
            Log.e("writeIntoResFolder", fileName, e);
        }
    }

    public final void writeIntoJavaFolder(String fileName, byte[] content) {
        try {
            writeAssetToFolder(javaFolder, fileName, content);
        } catch (IOException e) {
            Log.e("writeIntoJavaFolder", fileName, e);
        }
    }

    public final void writeIntoExportFolder(String fileName, byte[] content) {
        try {
            writeAssetToFolder(exportFolder, fileName, content);
        } catch (IOException e) {
            Log.e("writeIntoAssetFolder", fileName, e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void writeAssetToFolder(File folderFile, String fileName, byte[] content) throws IOException {
        final File file = new File(folderFile, fileName);

        file.getParentFile().mkdirs();
        file.createNewFile();

        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(content);
        }

        Log.i("writeAssetToFolder", file.getAbsolutePath());
    }

    public void exportProject(boolean shouldSendZip) {
        // need to place zip file in public folder to have access to it from activity which sends exported example
        final File zipFile = new File(saveDir, exampleName + ".zip");

        ZipUtil.zipFolder(exportFolder, zipFile);

        if (shouldSendZip) {
            final String subject = exampleName + " example";
            final String text = String.format(context.getString(R.string.export_solution_text), exampleName);
            final String title = String.format(context.getString(R.string.export_option), exampleName);

            sendZip(zipFile, subject, text, context, title);
        }
    }

    public static void sendZip(File zipFile, String subject, String text, Context context, String title) {
        final Intent intent = new Intent(Intent.ACTION_SEND);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("application/zip");

        final Uri uriForFile = FileProvider.getUriForFile(context, AUTHORITY, zipFile);

        intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        context.startActivity(Intent.createChooser(intent, title));
    }

    @Override
    public void dispose() {
        deleteDir(exportFolder);
    }

    static void deleteDir(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteDir(child);

        fileOrDirectory.delete();
    }
}

