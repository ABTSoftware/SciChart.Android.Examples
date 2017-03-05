//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// EnumUtils.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.utils;

import java.util.ArrayList;
import java.util.EnumSet;

public class EnumUtils {

    public static ArrayList<String> getEnumValuesArray(Class<? extends Enum> enumType) {
        final EnumSet<? extends Enum> enumSet = EnumSet.allOf(enumType);
        final ArrayList<String> enumNames = new ArrayList<>(enumSet.size());
        for (Enum enumValue : enumSet) {
            enumNames.add(enumValue.name());
        }

        return enumNames;
    }
}
