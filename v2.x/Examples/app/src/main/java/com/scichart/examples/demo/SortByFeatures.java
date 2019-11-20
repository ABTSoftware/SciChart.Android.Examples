//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SortByFeatures.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo;

import com.scichart.examples.demo.helpers.Example;
import com.scichart.examples.demo.viewobjects.ExampleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SortByFeatures implements ISortBy {

    private List<Example> examples;

    public SortByFeatures(List<Example> examples) {
        this.examples = examples;
    }

    @Override
    public List<ExampleView> sort() {
        final Map<String, List<Example>> examplesByFeatures = new TreeMap<>();
        for (Example example : examples) {
            for (Features features : example.features) {
                List<Example> examples = examplesByFeatures.get(features.name());
                if (examples == null) {
                    examples = new ArrayList<>();
                    examplesByFeatures.put(features.name(), examples);
                }
                examples.add(example);
            }
        }
        final List<ExampleView> result = new ArrayList<>();
        for (Map.Entry<String, List<Example>> entry : examplesByFeatures.entrySet()) {
            final ExampleView example = new ExampleView(entry.getKey(), "", true);
            result.add(example);
            for (Example ex : entry.getValue()) {
                result.add(new ExampleView(ex.title, ex.iconPath, false));
            }
        }
        return result;
    }

}
