//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SortByMostUsed.java is part of the SCICHART® Examples. Permission is hereby granted
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

public class SortByMostUsed implements ISortBy {

    private List<Example> examples;

    public SortByMostUsed(List<Example> examples) {
        this.examples = examples;
    }

    @Override
    public List<ExampleView> sort() {
        final List<ExampleView> result = new ArrayList<>();
        final ExampleView mostUsedGroup = new ExampleView("MostUsedGroup", "", true);
        result.add(mostUsedGroup);
        for (Example ex : examples) {
            result.add(new ExampleView(ex.title, ex.iconPath, false));
        }
        return result;
    }


}
