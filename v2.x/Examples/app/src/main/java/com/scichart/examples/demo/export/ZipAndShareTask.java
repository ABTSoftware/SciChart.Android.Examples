//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ZipAndShareTask.java is part of the SCICHART® Examples. Permission is hereby granted
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
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.AnyRes;
import android.util.Log;

import com.scichart.core.utility.StringUtil;
import com.scichart.examples.BuildConfig;
import com.scichart.examples.R;
import com.scichart.examples.demo.helpers.CodeFile;
import com.scichart.examples.demo.helpers.Example;

import java.io.File;
import java.util.List;

public class ZipAndShareTask extends AsyncTask<Void, Void, Void> {
    private static final String SOLUTION_ASSETS = "solution";
    private static final int SOLUTION_ASSETS_LENGTH = SOLUTION_ASSETS.length() + 1;

    // symbols which we need to remove from asset names which were renamed
    private static final String TXT_ENDING = ".txt";
    private static final int TXT_ENDING_LENGTH = TXT_ENDING.length();

    private static final String DATA_ASSETS = "data";

    private static final String APP_FOLDER = "app/";
    private static final String SCI_CHART_APP_ASSET = APP_FOLDER + BuildConfig.EXAMPLE_FOLDER + "/" + BuildConfig.SCI_CHART_APP + ".txt";
    private static final String MAIN_FOLDER = APP_FOLDER + BuildConfig.MAIN_FOLDER + "/";

    private static final String[] UTIL_CLASSES = new String[] { APP_FOLDER + BuildConfig.UTILS_FOLDER , APP_FOLDER + BuildConfig.DATA_FOLDER, APP_FOLDER + BuildConfig.COMPONENTS_FOLDER,};
    private static final String FRAGMENT_BASE_CLASSES = APP_FOLDER + BuildConfig.EXAMPLES_FRAGMENT_FOLDER + "/base";

    private static final String RES_VALUES_ASSETS = APP_FOLDER + BuildConfig.RES_FOLDER + "/values";
    private static final String RES_DRAWABLE_ASSETS = APP_FOLDER + BuildConfig.RES_FOLDER + "/drawable";
    private static final String RES_DRAWABLE_MDPI_ASSETS = APP_FOLDER + BuildConfig.RES_FOLDER + "/drawable-mdpi";
    private static final String RES_LAYOUT_ASSETS = APP_FOLDER + BuildConfig.RES_FOLDER + "/layout";
    private static final String LICENSE_FILE_ASSET = APP_FOLDER + BuildConfig.RES_FOLDER + "/raw/license.xml.txt";

    private static final String MAIN_ACTIVITY_LAYOUT = "activity_main.xml";
    private static final String ANDROID_MANIFEST = "AndroidManifest.xml";
    private static final String BUILD_GRADLE = "build.gradle";

    private static final String BEGIN_DEMO_APP = "BEGIN_DEMO_APPLICATION";
    private static final String END_DEMO_APP = "END_DEMO_APPLICATION";

    private final Context context;
    private final Example example;

    private final File saveDir;

    private ProgressDialog progressDialog;

    public ZipAndShareTask(Context context, Example example, File saveDir) {
        this.context = context;
        this.example = example;
        this.saveDir = saveDir;
    }

    public ZipAndShareTask(Context context, Example example) {
        this(context, example, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context, R.style.SciChart_ExportProgressDialogStyle);
        progressDialog.setTitle(context.getString(R.string.export_example));
        progressDialog.setMessage(example.title);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void param) {
        super.onPostExecute(param);

        progressDialog.dismiss();
        progressDialog = null;

    }

    @Override
    protected Void doInBackground(Void... params) {
        exportProject(context, example, saveDir, true);

        return null;
    }

    static void exportProject(Context context, Example example, File saveDir, boolean shouldShareZip) {
        try (final ExportManager exportManager = new ExportManager(context, example.title.replaceAll("\\s+", ""), saveDir)) {
            final AssetReader assetReader = new AssetReader(context.getAssets());

            addSciChartAppClass(exportManager, assetReader);

            addTemplateSolutionFiles(exportManager, assetReader, example);

            addDataAssetFiles(exportManager, assetReader);

            addResValuesFiles(exportManager, assetReader);
            addResDrawableFiles(exportManager, assetReader, RES_DRAWABLE_ASSETS);
            addResDrawableFiles(exportManager, assetReader, RES_DRAWABLE_MDPI_ASSETS);
            addResLayoutFiles(context, exportManager, assetReader);
            addLicenseFile(exportManager, assetReader);

            addUtilsCode(exportManager, assetReader);

            addExampleFiles(exportManager, assetReader, example);

            exportManager.exportProject(shouldShareZip);
        } catch (Exception e) {
            Log.e("ZipAndShareTask", "Exception was thrown during export of " + example.title, e);
        }
    }

    private static void addSciChartAppClass(ExportManager exportManager, AssetReader assetReader) {
        final AssetReader.AssetFile sciChartAppAsset = assetReader.readAsset(SCI_CHART_APP_ASSET);

        final String fileName = tryRemoveTxtEndingFromAssetName(sciChartAppAsset.assetName);
        final byte[] content = removeDemoAppCodeFrom(sciChartAppAsset.content);

        exportManager.writeIntoExportFolder(fileName, content);
    }

    private static void addTemplateSolutionFiles(ExportManager exportManager, AssetReader assetReader, Example example) {
        final AssetReader.AssetFile[] assetFiles = assetReader.readAssetsFromPath(SOLUTION_ASSETS);
        for (final AssetReader.AssetFile assetFile : assetFiles) {
            // need to remove 'solution/' part from all file names to properly export template
            final String fileName = assetFile.assetName.substring(SOLUTION_ASSETS_LENGTH);

            final byte[] content;
            if(fileName.endsWith(MAIN_ACTIVITY_LAYOUT)){
                content = processMainActivityLayout(assetFile.content, example);
            } else if(fileName.endsWith(ANDROID_MANIFEST)) {
                content = processAndroidManifest(assetFile.content, example);
            } else if(fileName.endsWith(BUILD_GRADLE)){
                content = processBuildGradle(assetFile.content, example);
            } else {
                content = assetFile.content;
            }

            exportManager.writeIntoExportFolder(fileName, content);
        }
    }

    private static byte[] processMainActivityLayout(byte[] content, Example example) {
        return new String(content).replace("[example_fragment]", example.fragment).getBytes();
    }

    private static byte[] processAndroidManifest(byte[] content, Example example) {
        return new String(content).replace("[example_title]", example.title).getBytes();
    }

    private static byte[] processBuildGradle(byte[] content, Example example) {
        return new String(content).replace("[example_fragment_name]", example.fragmentName).getBytes();
    }

    private static void addDataAssetFiles(ExportManager exportManager, AssetReader assetReader) {
        final AssetReader.AssetFile[] assetFiles = assetReader.readAssetsFromPath(DATA_ASSETS);
        for (final AssetReader.AssetFile assetFile : assetFiles) {
            exportManager.writeIntoAssetFolder(assetFile.assetName, assetFile.content);
        }
    }

    private static void addUtilsCode(ExportManager exportManager, AssetReader assetReader) {
        for (final String pathToUTilClasses : UTIL_CLASSES) {
            addJavaClassesFromPath(pathToUTilClasses, exportManager, assetReader);
        }
    }

    private static void addJavaClassesFromPath(String path, ExportManager exportManager, AssetReader assetReader) {
        final AssetReader.AssetFile[] assetFiles = assetReader.readAssetsFromPath(path);
        for (final AssetReader.AssetFile assetFile : assetFiles) {
            final String assetName = tryRemoveTxtEndingFromAssetName(assetFile.assetName);

            exportManager.writeIntoExportFolder(assetName, assetFile.content);
        }
    }

    private static void addResValuesFiles(ExportManager exportManager, AssetReader assetReader) {
        final AssetReader.AssetFile[] assetFiles = assetReader.readAssetsFromPath(RES_VALUES_ASSETS);
        for (final AssetReader.AssetFile assetFile : assetFiles) {
            final String assetName = tryRemoveTxtEndingFromAssetName(assetFile.assetName);

            final byte[] content;
            if(assetName.endsWith("styles.xml")) {
                content = removeDemoAppCodeFrom(assetFile.content);
            } else {
                content = assetFile.content;
            }

            exportManager.writeIntoExportFolder(assetName, content);
        }
    }

    private static void addResDrawableFiles(ExportManager exportManager, AssetReader assetReader, String path) {
        final AssetReader.AssetFile[] assetFiles = assetReader.readAssetsFromPath(path);
        for (final AssetReader.AssetFile assetFile : assetFiles) {
            final String assetName = tryRemoveTxtEndingFromAssetName(assetFile.assetName);

            exportManager.writeIntoExportFolder(assetName, assetFile.content);
        }
    }

    private static void addResLayoutFiles(Context context, ExportManager exportManager, AssetReader assetReader) {
        final String[] assetsNameFromResourceIds = getAssetsNamesFromLayoutIds(context, RES_LAYOUT_ASSETS, R.layout.example_sortby_spinner_item, R.layout.example_sortby_spinner_top_item, R.layout.example_toolbar_item);
        for (String assetsNameFromResourceId : assetsNameFromResourceIds) {
            final AssetReader.AssetFile assetFile = assetReader.readAsset(assetsNameFromResourceId);

            final String assetName = tryRemoveTxtEndingFromAssetName(assetFile.assetName);

            exportManager.writeIntoExportFolder(assetName, assetFile.content);
        }
    }

    private static String[] getAssetsNamesFromLayoutIds(Context context, String resLayoutAssets, @AnyRes int... ids){
        final Resources resources = context.getResources();
        final int size = ids.length;
        final String[] assetNames = new String[size];
        for (int i = 0; i < size; i++) {
            final int id = ids[i];

            final String resourceEntryName = resources.getResourceEntryName(id);

            assetNames[i] = resLayoutAssets + "/" + resourceEntryName + ".xml.txt";
        }

        return assetNames;
    }

    private static void addLicenseFile(ExportManager exportManager, AssetReader assetReader) {
        final AssetReader.AssetFile licenseFileAsset = assetReader.readAsset(LICENSE_FILE_ASSET);

        final String licenseFileName = tryRemoveTxtEndingFromAssetName(licenseFileAsset.assetName);

        exportManager.writeIntoExportFolder(licenseFileName, licenseFileAsset.content);
    }

    private static void addExampleFiles(ExportManager exportManager, AssetReader assetReader, Example example) {
        // add base classes for example fragments
        addJavaClassesFromPath(FRAGMENT_BASE_CLASSES, exportManager, assetReader);

        // add example source code
        final List<CodeFile> sourceFilePaths = example.sourceFilePaths;
        for (int i = 0, size = sourceFilePaths.size(); i < size; i++) {
            final String path = MAIN_FOLDER + sourceFilePaths.get(i).codeFile;
            final AssetReader.AssetFile assetFile = assetReader.readAsset(path);

            final String fileName = tryRemoveTxtEndingFromAssetName(assetFile.assetName);
            exportManager.writeIntoExportFolder(fileName, assetFile.content);
        }
    }

    private static byte[] removeDemoAppCodeFrom(byte[] content) {
        final String contentString = new String(content);
        final String[] lines = contentString.split(StringUtil.NEW_LINE);

        final StringBuilder newContent = new StringBuilder();
        final int size = lines.length;
        for (int i = 0; i < size; i++) {
            String line = lines[i];

            if(line.contains(BEGIN_DEMO_APP)) {
                while (!line.contains(END_DEMO_APP) && i < size) {
                    i++;
                    line = lines[i];
                }
            } else {
                newContent.append(line).append(StringUtil.NEW_LINE);
            }

        }

        return newContent.toString().getBytes();
    }

    private static String tryRemoveTxtEndingFromAssetName(String assetName) {
        if(assetName.endsWith(TXT_ENDING))
            return assetName.substring(0, assetName.length() - TXT_ENDING_LENGTH);
        else
            return assetName;
    }
}
