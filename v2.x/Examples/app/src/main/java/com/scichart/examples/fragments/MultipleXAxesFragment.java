//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// MultipleXAxesFragment.java is part of the SCICHART® Examples. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® examples are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.examples.fragments;

import android.util.Pair;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.modifiers.SourceMode;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AxisAlignment;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.examples.R;
import com.scichart.examples.fragments.base.ExampleBaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.Bind;

public class MultipleXAxesFragment extends ExampleBaseFragment {

    private final static String X_TOP_AXIS = "xTopAxis";
    private final static String X_BOTTOM_AXIS = "xBottomAxis";
    private final static String Y_LEFT_AXIS = "yLeftAxis";
    private final static String Y_RIGHT_AXIS = "yRightAxis";

    private final static int COUNT = 150;

    // Used to generate Random Walk
    private final Random random = new Random(251916);

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        final IAxis xTopAxis = sciChartBuilder.newNumericAxis()
                .withAxisAlignment(AxisAlignment.Top)
                .withAxisId(X_TOP_AXIS)
                .withTextColor(0xFF279B27)
                .build();

        final IAxis xBottomAxis = sciChartBuilder.newNumericAxis()
                .withAxisAlignment(AxisAlignment.Bottom)
                .withAxisId(X_BOTTOM_AXIS)
                .withTextColor(0xFFFF1919)
                .build();

        final IAxis yLeftAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withAxisAlignment(AxisAlignment.Left)
                .withAxisId(Y_LEFT_AXIS)
                .withTextFormatting("#.0")
                .withTextColor(0xFFFC9C29)
                .build();

        final IAxis yRightAxis = sciChartBuilder.newNumericAxis()
                .withGrowBy(new DoubleRange(0.1d, 0.1d))
                .withAxisAlignment(AxisAlignment.Right)
                .withAxisId(Y_RIGHT_AXIS)
                .withTextFormatting("#.0")
                .withTextColor(0xFF4083B7)
                .build();

        final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Red line").build();
        final IXyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Green line").build();
        final IXyDataSeries<Double, Double> ds3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Orange line").build();
        final IXyDataSeries<Double, Double> ds4 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Blue line").build();

        final Pair<List<Double>, List<Double>> ds1Points = fillData();
        final Pair<List<Double>, List<Double>> ds2Points = fillData();
        final Pair<List<Double>, List<Double>> ds3Points = fillData();
        final Pair<List<Double>, List<Double>> ds4Points = fillData();

        ds1.append(ds1Points.first, ds1Points.second);
        ds2.append(ds2Points.first, ds2Points.second);
        ds3.append(ds3Points.first, ds3Points.second);
        ds4.append(ds4Points.first, ds4Points.second);

        final IRenderableSeries rs1 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds1)
                .withXAxisId(X_BOTTOM_AXIS)
                .withYAxisId(Y_LEFT_AXIS)
                .withStrokeStyle(0xFFFF1919, 1f, true)
                .build();

        final IRenderableSeries rs2 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds2)
                .withXAxisId(X_BOTTOM_AXIS)
                .withYAxisId(Y_LEFT_AXIS)
                .withStrokeStyle(0xFF279B27, 1f, true)
                .build();

        final IRenderableSeries rs3 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds3)
                .withXAxisId(X_TOP_AXIS)
                .withYAxisId(Y_RIGHT_AXIS)
                .withStrokeStyle(0xFFFC9C29, 1f, true)
                .build();

        final IRenderableSeries rs4 = sciChartBuilder.newLineSeries()
                .withDataSeries(ds4)
                .withXAxisId(X_TOP_AXIS)
                .withYAxisId(Y_RIGHT_AXIS)
                .withStrokeStyle(0xFF4083B7, 1f, true)
                .build();

        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(), xTopAxis, xBottomAxis);
                Collections.addAll(surface.getYAxes(), yLeftAxis, yRightAxis);
                Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3, rs4);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers()
                        .withLegendModifier().withSourceMode(SourceMode.AllSeries).build()
                        .withXAxisDragModifier().withReceiveHandledEvents(true).build()
                        .withYAxisDragModifier().withReceiveHandledEvents(true).build()
                        .build());
            }
        });
    }

    private Pair<List<Double>, List<Double>> fillData() {
        final List<Double> xList = new ArrayList<>();
        final List<Double> yList = new ArrayList<>();
        double randomWalk = 10;
        for (int i = 0; i < COUNT; i++) {
            randomWalk += (random.nextDouble() - 0.498);
            yList.add(randomWalk);
            xList.add((double) i);
        }
        return new Pair<>(xList, yList);
    }
}