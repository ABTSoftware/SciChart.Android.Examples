//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// ScatterChartFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.support.annotation.ColorInt;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.AxisDragModifierBase.AxisDragMode;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.pointmarkers.TrianglePointMarker;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.XyScatterRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.Collections;
import java.util.Random;

import butterknife.Bind;

public class ScatterChartFragment extends ExampleBaseFragment {
    @Bind(R.id.chart)
    SciChartSurface surface;

    private final Random random = new Random();

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IAxis xAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();

        final IRenderableSeries rSeries1 = getScatterRenderableSeries(new TrianglePointMarker(), 0xFFFFEB01, false);
        final IRenderableSeries rSeries2 = getScatterRenderableSeries(new EllipsePointMarker(), 0xFFffA300, false);
        final IRenderableSeries rSeries3 = getScatterRenderableSeries(new TrianglePointMarker(), 0xFFff6501, true);
        final IRenderableSeries rSeries4 = getScatterRenderableSeries(new EllipsePointMarker(), 0xFFffa300, true);

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yAxis);
                Collections.addAll(surface.getRenderableSeries(), rSeries1, rSeries2, rSeries3, rSeries4);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroup()
                        .withZoomExtentsModifier().build()
                        .withPinchZoomModifier().build()
                        .withCursorModifier().withReceiveHandledEvents(true).build()
                        .withXAxisDragModifier().withReceiveHandledEvents(true).build()
                        .withYAxisDragModifier().withDragMode(AxisDragMode.Pan).build()
                        .build());
            }
        });
    }

    private XyScatterRenderableSeries getScatterRenderableSeries(IPointMarker pointMarker, @ColorInt int color, boolean negative) {
        final String seriesName = pointMarker instanceof EllipsePointMarker ?
                negative ? "Negative Ellipse" : "Positive Ellipse" :
                negative ? "Negative" : "Positive";

        final IXyDataSeries<Integer, Double> dataSeries = sciChartBuilder.newXyDataSeries(Integer.class, Double.class).withSeriesName(seriesName).build();

        for (int i = 0; i < 200; i++) {
            final double time = (i < 100) ? getRandom(random, 0, i + 10) / 100 : getRandom(random, 0, 200 - i + 10) / 100;
            final double y = negative ? -time * time * time : time * time * time;

            dataSeries.append(i, y);
        }

        return sciChartBuilder.newScatterSeries()
                .withDataSeries(dataSeries)
                .withStrokeStyle(color)
                .withPointMarker(sciChartBuilder.newPointMarker(pointMarker).withSize(6, 6).withStroke(0xFFFFFFFF, 0.1f).withFill(color).build())
                .build();
    }

    private double getRandom(Random random, double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}