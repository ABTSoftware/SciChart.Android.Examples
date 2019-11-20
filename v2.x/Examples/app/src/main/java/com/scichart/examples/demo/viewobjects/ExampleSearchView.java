//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleSearchView.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.viewobjects;

import com.scichart.examples.demo.Features;
import com.scichart.examples.demo.helpers.Example;

import java.util.List;
import java.util.Map;

public class ExampleSearchView {

    public final String title;
    public final String description;
    public final String xml;
    public final List<Features> features;

    private ExampleSearchView(String title, String description, String xml, List<Features> features) {
        this.title = title;
        this.description = description;
        this.xml = xml;
        this.features = features;
    }

    public static ExampleSearchView createExampleSearchView(Example example) {
        final String xml = getXmlValue(example);
        return new ExampleSearchView(example.title, example.description, xml, example.features);
    }

    private static String getXmlValue(Example example) {
        String xml = "";
        for (Map.Entry<String, String> entry : example.sourceFiles.entrySet()) {
            if (entry.getKey().contains(".xml")) {
                xml = entry.getValue();
            }
        }
        return xml/*.replaceAll("\n", "")*/;
    }

}
