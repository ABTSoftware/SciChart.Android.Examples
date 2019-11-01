//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ExampleKey.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo;

public class ExampleKey {

    public final String exampleCategory;
    public final String chartGroup;
    public final String exampleTitle;

    public ExampleKey(String exampleCategory, String chartGroup, String exampleTitle) {
        this.exampleCategory = exampleCategory;
        this.chartGroup = chartGroup;
        this.exampleTitle = exampleTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleKey that = (ExampleKey) o;

        if (exampleCategory != null ? !exampleCategory.equals(that.exampleCategory) : that.exampleCategory != null)
            return false;
        if (chartGroup != null ? !chartGroup.equals(that.chartGroup) : that.chartGroup != null)
            return false;
        return !(exampleTitle != null ? !exampleTitle.equals(that.exampleTitle) : that.exampleTitle != null);
    }

    @Override
    public int hashCode() {
        int result = exampleCategory != null ? exampleCategory.hashCode() : 0;
        result = 31 * result + (chartGroup != null ? chartGroup.hashCode() : 0);
        result = 31 * result + (exampleTitle != null ? exampleTitle.hashCode() : 0);
        return result;
    }

}
