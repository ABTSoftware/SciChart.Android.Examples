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

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.scichart.core.common.Predicate;
import com.scichart.core.utility.ListUtil;
import com.scichart.examples.demo.ExampleDefinition;
import com.scichart.examples.demo.ExampleKey;
import com.scichart.examples.demo.ExampleLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.scichart.examples.demo.helpers.Example.prepareExample;

public class Module {

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
        final List<ExampleDefinition> exampleDefinitions = loadExampleDefinitions();
        initializeExamples(exampleDefinitions);
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
                exampleDefinition.codeFiles,
                exampleDefinition.features,
                exampleDefinition.isVisible);
    }

    public final List<Example> getExamples() {
        return examples;
    }

    public final Example getExampleByTitle(final String title) {
        return ListUtil.firstOrDefault(examples, new Predicate<Example>() {
            @Override
            public boolean select(Example item) {
                return item.title.equals(title);
            }
        });
    }

    public final List<String> getCategories() {
        return categories;
    }

    public String getCategoryName(String category) {
        final String name = category.substring(1);//.replaceAll(" ", "\n");

        return name;
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
