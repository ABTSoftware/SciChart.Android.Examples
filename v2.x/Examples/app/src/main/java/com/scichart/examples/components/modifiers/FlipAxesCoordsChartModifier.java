//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// FlipAxesCoordsChartModifier.java is part of the SCICHART® Examples. Permission is hereby granted
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
import com.scichart.charting.visuals.axes.IAxis;

public class FlipAxesCoordsChartModifier extends ChartModifierBase {
    public void flipXAxes(){
        if(isAttached()) {
            flipAxesCoords(getParentSurface().getXAxes());
        }
    }

    public void flipYAxes(){
        if(isAttached()) {
            flipAxesCoords(getParentSurface().getYAxes());
        }
    }

    private void flipAxesCoords(AxisCollection axes){
        for(IAxis axis : axes){
            axis.setFlipCoordinates(!axis.getFlipCoordinates());
        }
    }
}
