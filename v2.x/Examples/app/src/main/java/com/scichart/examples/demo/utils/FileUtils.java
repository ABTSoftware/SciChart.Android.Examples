//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// FileUtils.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.utils;

import android.content.Context;

import com.scichart.examples.SciChartApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    public static void saveToFile(String fileName, String content) {
        FileOutputStream stream; // it should be closed
        try {
            // open (or create) private file
            stream = SciChartApp.getInstance().openFileOutput(fileName, Context.MODE_PRIVATE);
            stream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadFromFile(String fileName) {
        if (fileExists(fileName)) {
            FileInputStream stream = null;
            try {
                // read file and parse contents
                stream = SciChartApp.getInstance().openFileInput(fileName);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                final StringBuilder result = new StringBuilder();
                while (true) {
                    final String line = reader.readLine();
                    if (line == null) break;
                    result.append(line);
                }
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stream != null)
                        stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    public static boolean fileExists(String fileName) {
        final File file = SciChartApp.getInstance().getFileStreamPath(fileName);
        return file.exists();
    }

    // ---------------------------------------------------------------------------------------------

    public static String readAssetsFile(Context context, String pathToFile) {
        try {
            final InputStream inputStream = context.getAssets().open(pathToFile);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuilder builder = new StringBuilder();
            String s;
            while ((s = reader.readLine()) != null) {
                builder.append(s).append("\n");
            }
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readFile(String pathToFile) {
        final File exampleActivityFile = new File(pathToFile);
        if (exampleActivityFile.exists()) {
            try {
                final BufferedReader inputStream = new BufferedReader(new FileReader(exampleActivityFile));
                final StringBuilder builder = new StringBuilder();
                String s;
                while ((s = inputStream.readLine()) != null) {
                    builder.append(s).append("\n");
                }
                inputStream.close();
                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String readFile(InputStream inputStream) {
        final StringBuilder returnString = new StringBuilder();
        InputStreamReader streamReader = null;
        BufferedReader input = null;
        try {
            streamReader = new InputStreamReader(inputStream);
            input = new BufferedReader(streamReader);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (streamReader != null) streamReader.close();
                if (inputStream != null) inputStream.close();
                if (input != null) input.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return returnString.toString();
    }

}

