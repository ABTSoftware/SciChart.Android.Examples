//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// SideMenuHelper.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.utils;

import com.scichart.charting.model.ChartModifierCollection;
import com.scichart.charting.modifiers.ChartModifierBase;
import com.scichart.charting.modifiers.IChartModifier;
import com.scichart.charting.visuals.SciChartSurface;

public class SideMenuHelper {

    public static void attachModifierToSurface(Class modifierType, SciChartSurface surface){
        ChartModifierCollection chartModifiers = surface.getChartModifiers();

        IChartModifier toolbarModifier = getModifier(modifierType, chartModifiers);

        if (toolbarModifier == null) {
            try {
                toolbarModifier = (IChartModifier) modifierType.newInstance();
                chartModifiers.add(toolbarModifier);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (toolbarModifier instanceof ChartModifierBase) {
            ((ChartModifierBase) toolbarModifier).setReceiveHandledEvents(true);
        }
    }

    public static IChartModifier getModifier(Class modifierType, ChartModifierCollection chartModifiers) {
        IChartModifier result = null;
        for (IChartModifier modifier : chartModifiers) {
            if (modifier.getClass() == modifierType) {
                result = modifier;
                break;
            }
        }
        return result;
    }
}
