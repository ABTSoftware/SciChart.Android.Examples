//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AxisNode.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing.accessibility;

import android.view.accessibility.AccessibilityNodeInfo;

import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.data.model.IRange;

import java.text.DecimalFormat;

public class AxisNode extends NodeBase {
    private final NumericAxis axis;

    public AxisNode(NumericAxis axis, int id) {
        super(id);
        this.axis = axis;
    }

    @Override
    public boolean contains(float x, float y) {
        return axis.getLayoutRect().contains((int)x, (int)y);
    }

    @Override
    public void initAccessibilityNodeInfo(AccessibilityNodeInfo nodeInfo) {
        final SciChartSurface parentSurface = (SciChartSurface) axis.getParentSurface();

        nodeInfo.addAction(AccessibilityNodeInfo.ACTION_SELECT);
        nodeInfo.addAction(AccessibilityNodeInfo.ACTION_CLEAR_SELECTION);
        nodeInfo.setPackageName(axis.getContext().getPackageName());
        nodeInfo.setClassName(axis.getClass().getName());
        nodeInfo.setBoundsInParent(axis.getLayoutRect());
        nodeInfo.setParent(parentSurface);

        final String axisName = this.axis.isXAxis() ? "X Axis" : "Y Axis";

        final DecimalFormat format = new DecimalFormat("0.#");
        final IRange<Double> visibleRange = axis.getVisibleRange();
        final String min = format.format(visibleRange.getMin());
        final String max = format.format(visibleRange.getMax());
        final String text = String.format("%s with visible range from %s to %s", axisName, min, max);

        nodeInfo.setText(text);
        nodeInfo.setContentDescription(text);
    }
}
