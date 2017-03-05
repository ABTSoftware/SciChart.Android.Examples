//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// QuadLeftRightYAxesFragment.java is part of the SCICHART® Examples. Permission is hereby granted
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

public class QuadLeftRightYAxesFragment extends ExampleBaseFragment {

    private final static String X_AXIS = "xAxis";
    private final static String Y_LEFT_AXIS_1 = "yLeftAxis1";
    private final static String Y_LEFT_AXIS_2 = "yLeftAxis2";
    private final static String Y_LEFT_AXIS_3 = "yLeftAxis3";
    private final static String Y_LEFT_AXIS_4 = "yLeftAxis4";
    private final static String Y_RIGHT_AXIS_1 = "yRightAxis1";
    private final static String Y_RIGHT_AXIS_2 = "yRightAxis2";
    private final static String Y_RIGHT_AXIS_3 = "yRightAxis3";
    private final static String Y_RIGHT_AXIS_4 = "yRightAxis4";

    private final IXyDataSeries<Double, Double> ds1 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Red line").build();
    private final IXyDataSeries<Double, Double> ds2 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Black line").build();
    private final IXyDataSeries<Double, Double> ds3 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Orange line").build();
    private final IXyDataSeries<Double, Double> ds4 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Blue line").build();
    private final IXyDataSeries<Double, Double> ds5 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Another Blue").build();
    private final IXyDataSeries<Double, Double> ds6 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Green line").build();
    private final IXyDataSeries<Double, Double> ds7 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Another Red").build();
    private final IXyDataSeries<Double, Double> ds8 = sciChartBuilder.newXyDataSeries(Double.class, Double.class).withSeriesName("Another Black").build();

    private final static int COUNT = 2000;

    // Used to generate Random Walk
    private Random random = new Random(251916);

    @Bind(R.id.chart)
    SciChartSurface surface;

    @Override
    public boolean showDefaultModifiersInToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.example_single_chart_fragment;
    }

    @Override
    protected void initExample() {
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {

                final Pair<List<Double>, List<Double>> ds1Points = fillData();
                final Pair<List<Double>, List<Double>> ds2Points = fillData();
                final Pair<List<Double>, List<Double>> ds3Points = fillData();
                final Pair<List<Double>, List<Double>> ds4Points = fillData();
                final Pair<List<Double>, List<Double>> ds5Points = fillData();
                final Pair<List<Double>, List<Double>> ds6Points = fillData();
                final Pair<List<Double>, List<Double>> ds7Points = fillData();
                final Pair<List<Double>, List<Double>> ds8Points = fillData();

                ds1.append(ds1Points.first, ds1Points.second);
                ds2.append(ds2Points.first, ds2Points.second);
                ds3.append(ds3Points.first, ds3Points.second);
                ds4.append(ds4Points.first, ds4Points.second);
                ds5.append(ds5Points.first, ds5Points.second);
                ds6.append(ds6Points.first, ds6Points.second);
                ds7.append(ds7Points.first, ds7Points.second);
                ds8.append(ds8Points.first, ds8Points.second);

                final IAxis xAxis = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .withAxisAlignment(AxisAlignment.Bottom)
                        .withAxisId(X_AXIS)
                        .build();

                final IAxis yLeftAxis1 = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .withAxisAlignment(AxisAlignment.Left)
                        .withAxisId(Y_LEFT_AXIS_1)
                        .withTextColor(0xFFFF1919)
                        .build();

                final IAxis yLeftAxis2 = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .withAxisAlignment(AxisAlignment.Left)
                        .withAxisId(Y_LEFT_AXIS_2)
                        .withTextColor(0xFFCCCCCC)
                        .build();

                final IAxis yLeftAxis3 = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .withAxisAlignment(AxisAlignment.Left)
                        .withAxisId(Y_LEFT_AXIS_3)
                        .withTextColor(0xFFFC9C29)
                        .withIsCenterAxis(true)
                        .build();

                final IAxis yLeftAxis4 = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .withAxisAlignment(AxisAlignment.Left)
                        .withAxisId(Y_LEFT_AXIS_4)
                        .withTextColor(0xFF4083B7)
                        .withIsCenterAxis(true)
                        .build();

                final IAxis yRightAxis1 = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .withAxisAlignment(AxisAlignment.Right)
                        .withAxisId(Y_RIGHT_AXIS_1)
                        .withTextColor(0xFF4083B7)
                        .build();

                final IAxis yRightAxis2 = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .withAxisAlignment(AxisAlignment.Right)
                        .withAxisId(Y_RIGHT_AXIS_2)
                        .withTextColor(0xFF279B27)
                        .build();

                final IAxis yRightAxis3 = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .withAxisAlignment(AxisAlignment.Right)
                        .withAxisId(Y_RIGHT_AXIS_3)
                        .withTextColor(0xFFFF1919)
                        .withIsCenterAxis(true)
                        .build();

                final IAxis yRightAxis4 = sciChartBuilder.newNumericAxis()
                        .withGrowBy(new DoubleRange(0.1d, 0.1d))
                        .withAxisAlignment(AxisAlignment.Right)
                        .withAxisId(Y_RIGHT_AXIS_4)
                        .withTextColor(0xFFCCCCCC)
                        .withIsCenterAxis(true)
                        .build();

                final IRenderableSeries rs1 = sciChartBuilder.newLineSeries()
                        .withDataSeries(ds1)
                        .withXAxisId(xAxis.getAxisId())
                        .withYAxisId(yLeftAxis1.getAxisId())
                        .withStrokeStyle(0xFFFF1919)
                        .build();

                final IRenderableSeries rs2 = sciChartBuilder.newLineSeries()
                        .withDataSeries(ds2)
                        .withXAxisId(xAxis.getAxisId())
                        .withYAxisId(yLeftAxis2.getAxisId())
                        .withStrokeStyle(0xFFCCCCCC)
                        .build();

                final IRenderableSeries rs3 = sciChartBuilder.newLineSeries()
                        .withDataSeries(ds3)
                        .withXAxisId(xAxis.getAxisId())
                        .withYAxisId(yLeftAxis3.getAxisId())
                        .withStrokeStyle(0xFFFC9C29)
                        .build();

                final IRenderableSeries rs4 = sciChartBuilder.newLineSeries()
                        .withDataSeries(ds4)
                        .withXAxisId(xAxis.getAxisId())
                        .withYAxisId(yLeftAxis4.getAxisId())
                        .withStrokeStyle(0xFF4083B7)
                        .build();

                final IRenderableSeries rs5 = sciChartBuilder.newLineSeries()
                        .withDataSeries(ds5)
                        .withXAxisId(xAxis.getAxisId())
                        .withYAxisId(yRightAxis1.getAxisId())
                        .withStrokeStyle(0xFF4083B7)
                        .build();

                final IRenderableSeries rs6 = sciChartBuilder.newLineSeries()
                        .withDataSeries(ds6)
                        .withXAxisId(xAxis.getAxisId())
                        .withYAxisId(yRightAxis2.getAxisId())
                        .withStrokeStyle(0xFF279B27)
                        .build();

                final IRenderableSeries rs7 = sciChartBuilder.newLineSeries()
                        .withDataSeries(ds7)
                        .withXAxisId(xAxis.getAxisId())
                        .withYAxisId(yRightAxis3.getAxisId())
                        .withStrokeStyle(0xFFFF1919)
                        .build();

                final IRenderableSeries rs8 = sciChartBuilder.newLineSeries()
                        .withDataSeries(ds8)
                        .withXAxisId(xAxis.getAxisId())
                        .withYAxisId(yRightAxis4.getAxisId())
                        .withStrokeStyle(0xFFCCCCCC)
                        .build();

                Collections.addAll(surface.getXAxes(), xAxis);
                Collections.addAll(surface.getYAxes(), yLeftAxis1, yLeftAxis2, yLeftAxis3, yLeftAxis4, yRightAxis1, yRightAxis2, yRightAxis3, yRightAxis4);
                Collections.addAll(surface.getRenderableSeries(), rs1, rs2, rs3, rs4, rs5, rs6, rs7, rs8);
                Collections.addAll(surface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
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
            xList.add((double)i);
        }
        return new Pair<>(xList, yList);
    }
}
