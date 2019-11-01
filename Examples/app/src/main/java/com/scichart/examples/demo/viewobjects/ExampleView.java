//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleView.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.viewobjects;

public class ExampleView {

    public final String title;
    public final String iconPath;
    public final boolean isHeader;

    public ExampleView(String title, String iconPath, boolean isHeader) {
        this.title = title;
        this.iconPath = iconPath;
        this.isHeader = isHeader;
    }

}
