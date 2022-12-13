//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// Module.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.helpers;

import static com.scichart.examples.demo.helpers.Example.prepareExample;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.scichart.core.utility.ListUtil;
import com.scichart.examples.R;
import com.scichart.examples.demo.ExampleDefinition;
import com.scichart.examples.demo.ExampleKey;
import com.scichart.examples.demo.ExampleLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Module {
    private final static String BASE_GITHUB_PATH = "https://github.com/ABTSoftware/SciChart.Android.Examples/tree/SciChart_v4_Release/app/src/main/java/";
    private final static String BASE_FRAGMENT_NAMESPACE = "com.scichart.examples.fragments";
    private boolean isInitialized = false;
    private boolean isKotlin = false;

    private final Context context;
    private final ExampleLoader loader;

    private final List<Example> examples = new ArrayList<>();
    private final List<String> categories = new ArrayList<>();
    private final Map<String, List<String>> groupsByCategory = new HashMap<>();

    public Module(Context context) {
        this.context = context;
        this.loader = new ExampleLoader(context);
    }

    public void initialize() {
        try {
            final List<ExampleDefinition> exampleDefinitions = loadExampleDefinitions();
            initializeExamples(exampleDefinitions);
        } finally {
            isInitialized = true;
        }
    }

    public final boolean isKotlin() {
        return isKotlin;
    }

    public final void toggleKotlin() {
        isKotlin = !isKotlin;
        // re-initialize
    }
    public final boolean isInitialized() {
        return isInitialized;
    }

    private void initializeExamples(List<ExampleDefinition> exampleDefinitions) {
        final Set<String> categories = new TreeSet<>(); // need to sort categories alphabetically

        for (ExampleDefinition exampleDefinition : exampleDefinitions) {
            final Example example = prepareExample(exampleDefinition, context);
            examples.add(example);
            categories.add(example.topLevelCategory);
        }
        this.categories.addAll(categories);
        for (ExampleDefinition exampleDefinition : exampleDefinitions) {
            final String exampleCategory = exampleDefinition.exampleCategory;
            List<String> groups = groupsByCategory.get(exampleCategory);
            if (groups == null) {
                groups = new ArrayList<>();
                groupsByCategory.put(exampleCategory, groups);
            }
            groups.add(exampleDefinition.chartGroup);
        }
    }

    private List<ExampleDefinition> loadExampleDefinitions() {
        final List<ExampleDefinition> exampleDefinitions = new ArrayList<>();
        final List<String> xmlPathsList = loader.discoverAllXmlFiles();
        for (String path : xmlPathsList) {
            final ExampleKey exampleKey = loader.parseKey(path);
            final ExampleDefinition exampleDefinition = updateWithKeyValues(loader.parseDefinition(path), exampleKey);
            if (exampleDefinition.isVisible) {
                exampleDefinitions.add(exampleDefinition);
            }
        }
        return exampleDefinitions;
    }

    private ExampleDefinition updateWithKeyValues(ExampleDefinition exampleDefinition, ExampleKey exampleKey) {
        return new ExampleDefinition(
                exampleKey.exampleTitle,
                exampleKey.exampleCategory,
                exampleKey.chartGroup,
                exampleDefinition.iconPath,
                exampleDefinition.description,
                exampleDefinition.fileName,
                exampleDefinition.codeFiles,
                exampleDefinition.features,
                exampleDefinition.permissions,
                exampleDefinition.isVisible
        );
    }

    public final List<Example> getExamples() {
        return examples;
    }

    public final Example getExampleByTitle(final String title) {
        return ListUtil.firstOrDefault(examples, item -> item.title.equals(title));
    }

    public final String getExampleFragmentPath(final Example example) {
        final StringBuilder builder = new StringBuilder();
        appendPathPart(builder, BASE_FRAGMENT_NAMESPACE);
        appendPathPart(builder, example.topLevelCategory);
        appendPathPart(builder, getCamelCasedGroupName(example.group));
        if (isKotlin) {
            appendPathPart(builder, "kt");
        }
        builder.append(example.fileName.replace('/', '.'));

        return builder.toString();
    }

    public final String getGitHubLink(final Example example) {
        String exampleFragmentPath = getExampleFragmentPath(example).replace('.', '/');
        if (isKotlin) {
            exampleFragmentPath = exampleFragmentPath.replace("/kt", "");
            exampleFragmentPath = exampleFragmentPath + ".kt";
        } else {
            exampleFragmentPath = exampleFragmentPath + ".java";
        }

        return BASE_GITHUB_PATH + exampleFragmentPath;
    }

    private void appendPathPart(StringBuilder builder, String pathPart) {
        builder.append(pathPart);
        builder.append(".");
    }

    private String getCamelCasedGroupName(String groupName) {
        groupName = groupName.substring(0, 1).toLowerCase() + groupName.substring(1);

        StringBuilder builder = new StringBuilder(groupName);
        for (int i = 0; i < builder.length(); i++) {
            if (builder.charAt(i) == ' ') {
                builder.deleteCharAt(i);
                String replaceWith = String.valueOf(Character.toUpperCase(builder.charAt(i)));
                builder.replace(i, i + 1, replaceWith);
            }
        }

        return builder.toString();
    }

    public final List<String> getCategories() {
        return categories;
    }

    public String getCategoryName(String category) {
        final int categoryResourceString;
        if (category.toLowerCase(Locale.ROOT).contains("3d")) {
            categoryResourceString = R.string.charts3D;
        } else if (category.toLowerCase(Locale.ROOT).contains("featured")) {
            categoryResourceString = R.string.featured;
        } else {
            categoryResourceString = R.string.charts2D;
        }

        return context.getResources().getString(categoryResourceString);
    }

    public Drawable getIconForCategory(String category) {
        final String imageName = category.replaceAll(" ", "_");
        final Drawable resource = getIconForName(imageName);

        return resource;
    }

    private Drawable getIconForName(String name) {
        final String uri = "@drawable/" + name.toLowerCase(); // without .png
        final int resource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        return context.getResources().getDrawable(resource);
    }
}
