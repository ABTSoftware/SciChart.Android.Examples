//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ZipUtil.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.export;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

final class ZipUtil {

    private static final String TAG = "ZipUtil";

    public static void zipFolder(File inputFolderPath, File outputFile) {
        // need to cut part of input path to prevent export of internal android folders
        final int symbolsToRemoveCount = inputFolderPath.getParentFile().getPath().length() + 1;
        try (final FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            try (final ZipOutputStream zos = new ZipOutputStream(outputStream)) {
                addDirToArchive(zos, inputFolderPath, symbolsToRemoveCount);
            }
        } catch (IOException ioe) {
            Log.d(TAG, "zipFolder: exception was thrown", ioe);
        }
    }

    private static void addDirToArchive(ZipOutputStream zos, File srcFile, int symbolsToRemoveCount) throws IOException {
        final File[] files = srcFile.listFiles();

        for (final File selectedFile : files) {
            if (selectedFile.isDirectory()) {
                final String path = selectedFile.getPath().substring(symbolsToRemoveCount);
                final ZipEntry zipEntry = new ZipEntry(path + "/");

                //place the zip entry in the ZipOutputStream object
                zos.putNextEntry(zipEntry);
                Log.d(TAG, "Adding folder : " + path);

                addDirToArchive(zos, selectedFile, symbolsToRemoveCount);
            } else {
                // create byte buffer
                final byte[] buffer = new byte[1024];

                try (final FileInputStream fis = new FileInputStream(selectedFile)) {
                    final String path = selectedFile.getPath().substring(symbolsToRemoveCount);

                    zos.putNextEntry(new ZipEntry(path));
                    Log.d(TAG, "Adding file: " + path);

                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    zos.closeEntry();
                }
            }
        }
    }

}
