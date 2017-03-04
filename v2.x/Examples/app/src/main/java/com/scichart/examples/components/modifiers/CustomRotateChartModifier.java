//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// CustomRotateChartModifier.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.components.modifiers;

import com.scichart.charting.model.AxisCollection;
import com.scichart.charting.modifiers.ChartModifierBase;
import com.scichart.charting.visuals.ISciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.core.framework.IUpdateSuspender;

public final class CustomRotateChartModifier extends ChartModifierBase {

    public void rotateChart() {
        if (isAttached()) {
            final ISciChartSurface parentSurface = getParentSurface();

            IUpdateSuspender updateSuspender = parentSurface.suspendUpdates();
            try {
                rotateAxes(parentSurface.getXAxes());
                rotateAxes(parentSurface.getYAxes());
            } finally {
                updateSuspender.dispose();
            }
        }
    }

    private void rotateAxes(AxisCollection axes) {
        for (int i = 0, size = axes.size(); i < size; i++) {
            final IAxis axis = axes.get(i);
            final AxisAlignment axisAlignment = axis.getAxisAlignment();

            switch (axisAlignment) {
                case Right:
                    axis.setAxisAlignment(AxisAlignment.Bottom);
                    break;
                case Left:
                    axis.setAxisAlignment(AxisAlignment.Top);
                    break;
                case Top:
                    axis.setAxisAlignment(AxisAlignment.Right);
                    break;
                case Bottom:
                    axis.setAxisAlignment(AxisAlignment.Left);
                    break;
                case Auto:
                    if(axis.isXAxis()) {
                        // Bottom
                        axis.setAxisAlignment(AxisAlignment.Left);
                    } else {
                        // Right
                        axis.setAxisAlignment(AxisAlignment.Bottom);
                    }
            }
        }
    }

}