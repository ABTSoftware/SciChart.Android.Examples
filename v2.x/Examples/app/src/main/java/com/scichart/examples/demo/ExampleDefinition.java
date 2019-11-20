//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleDefinition.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo;

import java.util.List;

public class ExampleDefinition {

    public final String exampleTitle;
    public final String exampleCategory;
    public final String chartGroup;
    public final String iconPath;
    public final String description;
    public final List<String> codeFiles;
    public final List<Features> features;
    public final boolean isVisible;

    public ExampleDefinition(String exampleTitle,
                             String exampleCategory,
                             String chartGroup,
                             String iconPath,
                             String description,
                             List<String> codeFiles,
                             List<Features> features,
                             boolean isVisible) {
        this.exampleTitle = exampleTitle;
        this.exampleCategory = exampleCategory;
        this.chartGroup = chartGroup;
        this.iconPath = iconPath;
        this.description = description;
        this.codeFiles = codeFiles;
        this.features = features;
        this.isVisible = isVisible;
    }

}
