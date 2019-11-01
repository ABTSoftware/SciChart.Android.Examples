//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2019. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// AccessibilityChartSandboxFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments.testing.accessibility;

import android.view.accessibility.AccessibilityEvent;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.visuals.axes.IAxisCore;
import com.scichart.charting.visuals.axes.NumericAxis;
import com.scichart.charting.visuals.axes.VisibleRangeChangeListener;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.IRange;
import com.scichart.drawing.utility.ColorUtil;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.ArrayList;

import butterknife.BindView;

public class AccessibilityChartSandboxFragment extends ExampleBaseFragment {
    @BindView(R.id.chart)
    AccessibleSciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_accessible_chart_fragment;
    }

    @Override
    protected void initExample() {
        final NumericAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1, 0.1).build();
        final NumericAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0, 0.1).build();

        final ArrayList<INode> nodes = surface.helper.nodes;

        IXyDataSeries<Integer, Integer> dataSeries = sciChartBuilder.newXyDataSeries(Integer.class, Integer.class).withSeriesName("Column chart").build();
        final int[] yValues = {50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60};

        for (int i = 0; i < yValues.length; i++) {
            final int yValue = yValues[i];
            dataSeries.append(i, yValue);

            nodes.add(new ColumnPointNode(i, i, yValue, surface));
        }

        final FastColumnRenderableSeries rSeries = sciChartBuilder.newColumnSeries()
                .withStrokeStyle(0xFF232323, 0.4f)
                .withDataPointWidth(0.7)
                .withLinearGradientColors(ColorUtil.LightSteelBlue, ColorUtil.SteelBlue)
                .withDataSeries(dataSeries)
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                surface.getXAxes().add(xAxis);
                surface.getYAxes().add(yAxis);
                surface.getRenderableSeries().add(rSeries);

                final AccessiblePinchZoomModifier pinchZoomModifier = new AccessiblePinchZoomModifier();
                final AccessibleZoomPanModifier zoomPanModifier = new AccessibleZoomPanModifier();
                final AccessibleZoomExtentsModifier zoomExtentsModifier = new AccessibleZoomExtentsModifier();

                surface.getChartModifiers().add(sciChartBuilder.newModifierGroup()
                        .withModifier(zoomPanModifier)
                        .withModifier(pinchZoomModifier)
                        .withModifier(zoomExtentsModifier)
                        .build());
            }
        });

        final AxisNode xAxisNode = new AxisNode(xAxis, nodes.size());
        nodes.add(xAxisNode);

        final AxisNode yAxisNode = new AxisNode(yAxis, nodes.size());
        nodes.add(yAxisNode);

        xAxis.setVisibleRangeChangeListener(new VisibleRangeChangeListener() {
            @Override
            public void onVisibleRangeChanged(IAxisCore axis, IRange oldRange, IRange newRange, boolean isAnimating) {
                // need to send this even to update position of rects on screen during scrolling
                surface.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
            }
        });

        yAxis.setVisibleRangeChangeListener(new VisibleRangeChangeListener() {
            @Override
            public void onVisibleRangeChanged(IAxisCore axis, IRange oldRange, IRange newRange, boolean isAnimating) {
                // need to send this even to update position of rects on screen during scrolling
                surface.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
            }
        });
    }
}
