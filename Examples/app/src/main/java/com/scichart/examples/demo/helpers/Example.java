//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Example.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.helpers;

import android.content.Context;
import android.util.Log;

import com.scichart.examples.demo.ExampleDefinition;
import com.scichart.examples.demo.Features;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.scichart.examples.demo.helpers.CodeFile.createAndInitCodeFile;
import static com.scichart.examples.demo.utils.FileUtils.readFile;

public class Example {

    public final UUID id;
    public final String title;
    public final String iconPath;
    public final String topLevelCategory;
    public final String group;
    public final String fragment;
    public final String fragmentName;
    public final String description;
    public final List<CodeFile> sourceFilePaths;
    public final Map<String, String> sourceFiles;
    public final List<Features> features;
    public final boolean isHeader;

    public Example(UUID id, String title,
                   String iconPath,
                   String topLevelCategory,
                   String group,
                   String fragment,
                   String fragmentName,
                   String description,
                   List<CodeFile> sourceFilePaths,
                   Map<String, String> sourceFiles,
                   List<Features> features,
                   boolean isHeader) {
        this.id = id;
        this.title = title;
        this.iconPath = iconPath;
        this.topLevelCategory = topLevelCategory;
        this.group = group;
        this.fragment = fragment;
        this.fragmentName = fragmentName;
        this.description= description;
        this.sourceFilePaths = sourceFilePaths;
        this.sourceFiles = sourceFiles;
        this.features = features;
        this.isHeader = isHeader;
    }

    public static Example createExample(ExampleDefinition exampleDefinition, Map<String, String> sourceFiles) {
        final List<CodeFile> sourceFilePaths = convertToCodeFiles(exampleDefinition.codeFiles);
        final String fragment = getFragment(sourceFilePaths);
        final String fragmentName = getFragmentName(fragment);
        final UUID id = UUID.randomUUID();

        return new Example(id, exampleDefinition.exampleTitle, exampleDefinition.iconPath,  exampleDefinition.exampleCategory, exampleDefinition.chartGroup,
                fragment, fragmentName, exampleDefinition.description, sourceFilePaths, sourceFiles, exampleDefinition.features, false);
    }

    public static Example prepareExample(ExampleDefinition exampleDefinition, Context context) {
        final Map<String, String> sourceFiles = new HashMap<>();
        for (String codeFile : exampleDefinition.codeFiles) {
            try {
                final InputStream inputStream = context.getAssets().open("app/src/main/" + codeFile);
                final String fileContent = readFile(inputStream);
                final String[] splitedPath = codeFile.split("/");
                if (splitedPath.length > 0) {
                    final String inputString = splitedPath[splitedPath.length - 1];
                    final String withoutTxt = inputString.substring(0, inputString.length() - 4);
                    sourceFiles.put(withoutTxt, fileContent);
                }
            } catch (IOException e) {
                Log.e("prepareExample: ", codeFile, e);
            }
        }
        return createExample(exampleDefinition, sourceFiles);
    }

    private static List<CodeFile> convertToCodeFiles(List<String> codeFiles) {
        final List<CodeFile> result = new ArrayList<>();
        for (String codeFile : codeFiles) {
            result.add(createAndInitCodeFile(codeFile));
        }
        return result;
    }

    private static String getFragment(List<CodeFile> filePaths) {
        for (CodeFile codeFile : filePaths) {
            if (codeFile.isJava) {
                return codeFile.javaClassNamePath;
            }
        }
        return "";
    }

    private static String getFragmentName(String fragment) {
        if (fragment.length() > 0) {
            final String[] splittedParts = fragment.split("\\.");
            if (splittedParts.length > 0) {
                return splittedParts[splittedParts.length - 1];
            }
        }
        return "";
    }

}
