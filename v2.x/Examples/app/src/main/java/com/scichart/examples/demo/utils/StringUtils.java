//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// StringUtils.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.demo.utils;

public class StringUtils {

    public static String getExtension(String path) {
        final String[] split = path.split("\\.");
        return split.length > 0 ? split[split.length - 1] : null;
    }

    public static String getStringWithoutNLastSymbols(String str, int n) {
        return str.substring(0, str.length() - n);
    }

}
