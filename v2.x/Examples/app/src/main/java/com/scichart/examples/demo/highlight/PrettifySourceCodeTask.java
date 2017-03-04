//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// PrettifySourceCodeTask.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.highlight;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.scichart.examples.demo.helpers.Example;

import java.util.Map;

import static com.scichart.examples.demo.utils.FileUtils.fileExists;
import static com.scichart.examples.demo.utils.FileUtils.saveToFile;

public class PrettifySourceCodeTask extends AsyncTask<Void, Void, Void> {
    private static final String JAVA = "java";

    @NonNull
    private final Example example;

    public PrettifySourceCodeTask(@NonNull Example example) {
        this.example = example;
    }

    @Override
    protected Void doInBackground(Void... params) {
        final Map<String, String> sourceFiles = example.sourceFiles;

        for (Map.Entry<String, String> entry : sourceFiles.entrySet()) {
            final String fileName = entry.getKey();
            if (!fileExists(fileName)) {
                final String fileContent = entry.getValue();
                final String data = PrettifyHighlighter.highlight(JAVA, fileContent);
                saveToFile(fileName, data);
            }
        }

        return null;
    }
}
