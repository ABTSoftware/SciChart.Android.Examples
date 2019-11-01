//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ColumnPointNode.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing.accessibility;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;

import java.text.DecimalFormat;

public class ColumnPointNode extends NodeBase {
    private final double x, y;
    private final SciChartSurface surface;

    private final Rect bounds = new Rect();

    public ColumnPointNode(int id, double x, double y, SciChartSurface surface) {
        super(id);

        this.x = x;
        this.y = y;
        this.surface = surface;
    }

    @Override
    public boolean contains(float x, float y) {
        return bounds.contains((int) x, (int)y);
    }

    @Override
    public void initAccessibilityNodeInfo(AccessibilityNodeInfo nodeInfo) {
        final IAxis xAxis = surface.getXAxes().get(0);
        final IAxis yAxis = surface.getYAxes().get(0);

        final int xStart = (int) xAxis.getCoordinate(x - 0.5);
        final int xEnd = (int) xAxis.getCoordinate(x + 0.5);
        final int yCoord = (int) yAxis.getCoordinate(y);
        final int zeroCoord = (int) yAxis.getCoordinate(0d);

        bounds.set(xStart, yCoord, xEnd, zeroCoord);

        nodeInfo.addAction(AccessibilityNodeInfo.ACTION_SELECT);
        nodeInfo.addAction(AccessibilityNodeInfo.ACTION_CLEAR_SELECTION);
        nodeInfo.setPackageName(getClass().getPackage().getName());
        nodeInfo.setClassName(getClass().getName());
        nodeInfo.setBoundsInParent(bounds);
        nodeInfo.setParent(surface);

        DecimalFormat format = new DecimalFormat("0.##");

        final String text = String.format("Column with %s value", format.format(y));

        nodeInfo.setText(text);
        nodeInfo.setContentDescription(text);
    }
}
