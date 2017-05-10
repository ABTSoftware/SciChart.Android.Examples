//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleLoader.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.scichart.examples.demo.parser.ExampleDefinitionParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExampleLoader {

    private Context context;
    private final List<String> xmlPaths = new ArrayList<>();

    public ExampleLoader(Context context) {
        this.context = context;
    }

    public List<String> discoverAllXmlFiles() {
        final List<String> xmlFolders = listAssetsXmlFolders("ExampleDefinition");
        for (String xmlFolder : xmlFolders) {
            try {
                final String[] paths = context.getAssets().list(xmlFolder);
                for (String path : paths) {
                    xmlPaths.add(xmlFolder + "/" + path);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return xmlPaths;
    }

    private @NonNull List<String> listAssetsXmlFolders(String path) {
        final List<String> result = new ArrayList<>();

        try {
            final String[] categories = context.getAssets().list(path);
            for (String category : categories) {
                final String[] groups = context.getAssets().list(path + "/" + category);
                for (String group : groups) {
                    result.add(path + "/" + category + "/" + group);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ExampleDefinition parseDefinition(String path) {
        try {
            final InputStream in = context.getAssets().open(path);
            return new ExampleDefinitionParser().parseDefinition(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ExampleKey parseKey(String path) {
        final String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].replaceAll("_", " ");
        }
        return new ExampleKey(parts[1], parts[2], parts[3].split("\\.")[0]);
    }

}